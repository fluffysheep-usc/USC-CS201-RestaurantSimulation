package restaurant;

//import javax.swing.*;

import agent.Agent;


import restaurant.WaiterAgent.MyCustomerState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
//import restaurant.HostAgent.HostState;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.util.List;
//import java.util.ArrayList;
import java.util.Map;

import javax.swing.Timer;

public class CookAgent extends Agent {
	
	String name;
	
	//A list of ALL orders that the cook is attending to.
	List<Order> orders;

	//A map containing all the foods and their cook times. Implement in Constructor pls!
	Map<String, Food> foodDictionary = new HashMap<String, Food>(); 
	
	public enum OrderState { pending, cooking, cooked, notified;}

	//Constructor
	public CookAgent(String name){
	  this.name = name;
	  orders = new ArrayList<Order>();
	  
	  //Tree map
	  foodDictionary.put("Steak", new Food("Steak", 5000, 1));
	  foodDictionary.put("Chicken", new Food("Chicken", 4500, 1));
	  foodDictionary.put("Salad", new Food("Salad", 6000, 1));
	  foodDictionary.put("Pizza", new Food("Pizza", 7000, 1));
	  
	}
		
//########## Messages  ###############
	public void msgHeresAnOrder(String o, WaiterAgent w, int tableNumber)
	{
		Order order = new Order(o, w, tableNumber);
		 orders.add(order);
		 stateChanged();
	}
	
	
//##########  Scheduler  ##############
@Override
	protected boolean pickAndExecuteAnAction() {
		// if there exists an Order o in pendingOrder such that o.OrderState == pending
		//then CookOrder(o);
	try{
		if (orders.size() > 0){
				//Look for all pending orders.
				for(Order o : orders){
					if (o.getState() == OrderState.pending){
						CookOrder(o);
						return true;
					}
					
				}
				
				for (int i=0; i<orders.size();i++){
					
					if (orders.get(i).getState() == OrderState.cooked){
						tellWaiterOrderIsReady(orders.get(i), i);
						i--;
						return true;
					}
				}
		}
	}
	catch(Exception e){
			e.printStackTrace();
	}
		
		return false;
	}
		
//########## Actions ###############
	private void CookOrder(Order o){
		Food temp = foodDictionary.get(o.choice);
		if (temp.amount == 0){
			o.waiter.msgOutOfFood(o.choice, o.tableNumber);
			orders.remove(o);
			return;
		}
		if (temp.amount == 1){
			//order more for the restaurant;
		}
		
		temp.amount --;
		
		  Do("is cooking " + o.choice + ".");
		  o.setTimer(foodDictionary.get(o.choice).cookTime);
	}
	
	private void tellWaiterOrderIsReady(Order o, int index){
		o.waiter.msgOrderIsReady(o.choice, o.tableNumber);
		o.setState(OrderState.notified);
		orders.remove(index);
	}
	
//################    Utility     ##################
	public String toString(){
		return "Cook " + name;
	}

//######################## End of Class Cook#############################
	
	//#### Inner Class ####	
	private class Food {
		   private String choice;
		   private int cookTime;
		   private int amount;
		   
		   private Food(String c, int ct, int amt){
			   choice = c;
			   cookTime = ct;
			   amount = amt;
		   }
	}
	
	private class Order {
		  String choice;
		  WaiterAgent waiter;
		  int tableNumber;
		  Timer timer;
		  int orderTime;
		  
		  private OrderState state = OrderState.pending;
		  
		  public Order(String c, WaiterAgent w, int tableNumber){
			 choice = c;
			 waiter = w;
			 this.tableNumber = tableNumber;
		  }
		  public void setTimer(int time){
			  orderTime = time;
			  state =  OrderState.cooking;
			  //Timer is a cooking timer.
			  timer = new Timer(time, new ActionListener() {
				   public void actionPerformed(ActionEvent e){
				      state = OrderState.cooked;
				      timer.stop();
				      stateChanged();
				   }
				});
			  timer.start();
		  }
		  public OrderState getState(){
			  return state;
		  }
		  public void setState(OrderState state){
			  this.state = state;
		  }
		  public String getChoice(){
			  return choice;
		  }
		  
	}

}



