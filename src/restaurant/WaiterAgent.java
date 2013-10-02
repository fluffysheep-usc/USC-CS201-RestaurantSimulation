package restaurant;

import agent.Agent;
import restaurant.Order.OrderState;
import restaurant.gui.WaiterGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.Semaphore;

import javax.swing.Timer;

public class WaiterAgent extends Agent {
	WaiterGui gui;
	List<MyCustomer> myCustomers = new ArrayList<MyCustomer>();
	CookAgent cook;
	HostAgent host;

	private enum WaiterState {none, wantABreak, askedBreak, goingOnBreak, onBreak};
	private WaiterState state = WaiterState.none;
	
		public HostAgent getHost(){
			return host;
		}
	String name;
		public String getName(){
			return name;
		}
		
	Timer breakTimer;
	int breakLength = 1; //seconds
	
	// This is to distribute the waiting customers evenly among waiters.
	public int numberOfCustomers;
	enum MyCustomerState {waiting, seated, readyToOrder, ordering, reordering, ordered, orderCooking, orderReady, eating, doneEating;}
	
	//Animation stuff - To implement in 2c
	private Semaphore atTargetLocation = new Semaphore(0, true);
	boolean idle; //Idle is not a state. It is simply an animation helper variable.

	
	public WaiterAgent(String name, HostAgent h, CookAgent c) {
		this.name = name;
		host = h;
		cook = c;
		
		breakTimer = new Timer(breakLength*1000, new ActionListener() {
			   public void actionPerformed(ActionEvent e){
			      OffBreak();
			      breakTimer.stop();
			   }
			});
	}
	
	//Wants a break
	public void msgWantABreak(){
		Do("Want a break after my customers leave.");
		state = WaiterState.wantABreak;
		stateChanged();
	}
	public void msgCanGoOnBreak(){
		state = WaiterState.goingOnBreak; 
		stateChanged();
	}

// ######## Messages ################
	public void msgSeatAtTable(CustomerAgent c, Table t) {
		c.waiter = this;
		MyCustomer mc = new MyCustomer(c,t);
		mc.state = MyCustomerState.waiting;
		t.occupiedBy = c;
		idle = true;
		myCustomers.add(mc);
		numberOfCustomers++;
		stateChanged();
	};	
	
	public void msgReadyToOrder(CustomerAgent c){  		
		for (MyCustomer mc : myCustomers){
			if (mc.customer == c){
				Do("Received customer call");
				mc.state = MyCustomerState.readyToOrder;
				stateChanged();
			}
		}
	}
	public void msgHeresMyChoice(CustomerAgent ca, String c){ 
		for (MyCustomer mc : myCustomers){
			if (mc.customer == ca){
				//mc.order = new Order(c, this, mc.table.tableNumber);
				mc.choice = c;
				mc.state = MyCustomerState.ordered;
				stateChanged();
			}
		}
	}
	public void msgOutOfFood(String choice, int table){
		for (MyCustomer mc : myCustomers){
			if (mc.table.tableNumber ==  table){
				//Do go back to customer and ask for an order.
				mc.state = MyCustomerState.reordering;
				stateChanged();
			}
		}
	}
	
	public void msgOrderIsReady(String o, int tableNumber){ 		
		for (MyCustomer mc : myCustomers){
			if (mc.choice == o && mc.table.tableNumber == tableNumber){
				mc.state = MyCustomerState.orderReady;
				stateChanged();
			}
		}
	}
	public void msgImDone(CustomerAgent c){ 
		for (MyCustomer mc: myCustomers){
			if (mc.customer == c){
				mc.state = MyCustomerState.doneEating;
				stateChanged();
			}
		}
	}

	
	
//##########  Scheduler  ##############
	protected boolean pickAndExecuteAnAction(){
		
		if (!myCustomers.isEmpty()){
			for (MyCustomer mc : myCustomers){
				if (mc.state == MyCustomerState.waiting){
						idle = false;
						SeatCustomer(mc.table, mc);
						return true;
				}
			}
			
			for (MyCustomer mc: myCustomers){
				if (mc.state == MyCustomerState.readyToOrder){
					idle = false;
					TakeOrder(mc);
					return true;
				}
			}
			
			for (MyCustomer mc: myCustomers){
				if (mc.state == MyCustomerState.ordered){ 
					idle = false;
					GiveOrderToCook(mc, true);
					return true;
				}
			}
			
			for (MyCustomer mc: myCustomers){
				if(mc.state == MyCustomerState.reordering){
					idle = false;
					TakeReorder(mc);
					return true;
				}
			}
			
			for (MyCustomer mc: myCustomers){
				if (mc.state == MyCustomerState.orderReady){
					idle = false;
					GiveFoodToCustomer(mc);
					return true;
				}
			}
			
			for (MyCustomer mc: myCustomers){
				if (mc.state == MyCustomerState.doneEating){
					idle = false;
					CustomerLeaving(mc);
					return true;
				}
			}
			
			DoIdle();
			return true;
			}
		
		if (state == WaiterState.wantABreak){
			IWantABreak();
			return true;
		}
		
		if (state == WaiterState.goingOnBreak && numberOfCustomers == 0){
			TakeABreak();
			return true;
		}
		
		DoIdle();
		return false;
		
		
	}
	
//############ Action ################
	//Want a break;
	public void IWantABreak(){
		Do("I'm telling the host I want a break.");
		state = WaiterState.askedBreak;
		host.msgWaiterWantsABreak(this);
	}
	
	//Take a break;
	public void TakeABreak(){
		Do("I'm taking a break!");
		DoTakeABreak();
		state = WaiterState.onBreak;
		breakTimer.restart();
		breakTimer.start();
	}
	//OffBreak
	public void OffBreak(){
		Do("I'm coming back to work!");
		state = WaiterState.none;
		host.msgWaiterOffBreak(this);
		gui.DoOffBreak();
	}
	
	public void SeatCustomer(Table t, MyCustomer mc) {
		DoGetCustomer();
		Do("is seating " + mc.customer.getName());
		mc.customer.msgFollowMe(new Menu());
		mc.state = MyCustomerState.seated;
		DoSeatCustomer(t.getTableNumber(), mc);
	}
	
	public void TakeOrder(MyCustomer mc){
		Do("is taking " + mc.customer.getName() + "'s order.");
		DoWalkToCustomer(mc, "");
		mc.customer.WhatWouldYouLike();
		mc.state = MyCustomerState.ordering;
	}
	 
	public void GiveOrderToCook(MyCustomer mc, boolean displayText){
		DoGiveOrderToCook();
		mc.state = MyCustomerState.orderCooking;
		cook.msgHeresAnOrder(mc.choice, this, mc.table.tableNumber);
	}
	
	public void TakeReorder(MyCustomer mc){
		Do("Going to customer " + mc.customer.getCustomerName() + " for a reorder.");
		DoWalkToCustomer(mc, "Reordering");
		Menu m = new Menu();
		m.remove(mc.choice);
		mc.customer.msgOutOfFood(new Menu());
		mc.state = MyCustomerState.ordering;
	}

	public void GiveFoodToCustomer(MyCustomer mc){
		DoGiveOrderToCook();
		DoWalkToCustomer(mc, mc.choice);
		Do("is giving food to " + mc.customer.getName());	
		mc.state = MyCustomerState.eating;
		mc.customer.HeresYourOrder(mc.choice);
	}
	public void CustomerLeaving(MyCustomer c){
		Do(c.customer.getName() + " is leaving the restaurant.");
		host.msgTableIsClear(c.table);
		myCustomers.remove(c);
		numberOfCustomers--;
	}

	//##GUI ACTIONS###
	private void DoTakeABreak(){
		gui.setText("OnBreak");
		gui.DoTakeABreak();
		atLocAcquire();
	}
	
	private void DoSeatCustomer(int tableNum, MyCustomer mc){
		gui.setText("Seating Customer");
		gui.DoBringToTable(mc.customer, tableNum);
		mc.customer.getGui().DoGoToSeat(tableNum);
		atLocAcquire();
	}
	
	public void DoGetCustomer(){
		Do("is getting customer.");
		gui.setText("Getting Customer");
		gui.DoGetCustomer();
		atLocAcquire();
	}
	
	public void DoWalkToCustomer(MyCustomer mc, String displayText){
		gui.DoWalkToCustomer(mc.table, displayText);
		atLocAcquire();
	}
	
	public void DoGiveOrderToCook(){
		Do("gives an order to the cook.");
		gui.setText("Going to Cook");
		gui.DoGiveOrderToCook();
		atLocAcquire();
	}
	
	private void atLocAcquire(){
		try {
			atTargetLocation.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void DoIdle(){
		gui.DoIdle();
		gui.setText("Idle");
		idle = false;
	}

//#####    GUI Utilities  ####
	public void atLocation() {//from animation
				atTargetLocation.release();// = true;
				idle = true;
	}
	
	public void setGUI(WaiterGui wg){
	    	gui = wg;
	}
	public WaiterGui getGUI(){
		return gui;
	}
	
//#### Inner Class ####	
	private class MyCustomer {
		CustomerAgent customer;
		   Table table;
		   //Order order;
		   String choice;
		   MyCustomerState state = MyCustomerState.waiting;
		   
		   public MyCustomer(CustomerAgent c, Table t) {
				customer = c;
				table = t;
			}
	}

}
