package restaurant.gui;


import restaurant.WaiterAgent;
import restaurant.HostAgent;
import restaurant.interfaces.Customer;

import java.awt.*;

public class WaiterGui implements Gui {

    private WaiterAgent agent = null;
    
    RestaurantGui gui;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    private int xTable = 200;
    private int yTable = 250;
    
    private  int hostWidth = 20;
    private  int hostHeight = 20;
    
    private boolean receivedAction;
    private boolean doingIdle;
    private static final int movementOffset = 20;
    
    private boolean wantBreak;
    	public boolean isOnBreak(){
    		return wantBreak;
    	}
    	public void wantBreak(boolean b){
    		wantBreak = b;
    		agent.msgWantABreak();
    	}
    
    private String displayText = "";

    public WaiterGui(WaiterAgent agent, RestaurantGui r) {
        this.agent = agent;
        receivedAction = false;
        doingIdle = false;
        wantBreak = false;
        gui = r;
    }

    public void updatePosition() {
    	if (receivedAction){
	        if (xPos < xDestination)
	            xPos++;
	        else if (xPos > xDestination)
	            xPos--;
	        if (yPos < yDestination)
	            yPos++;
	        else if (yPos > yDestination)
	            yPos--;
	        else
	        if (xPos == xDestination && yPos == yDestination){
        		if(!doingIdle)
        			agent.atLocation();
        		displayText = "";
        		receivedAction = false;
        		return;
	        }
    	}
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, hostWidth, hostHeight);
        if (displayText.trim().length() > 0){
        	if (xPos > 0 && xPos < 600 && yPos>0 && yPos<450){
        		g.drawString(displayText, xPos, yPos);
        	}
        }
    }

    public boolean isPresent() {
        return true;
    }
    
    public void setText(String text){
    	displayText = text;
    }
    
    public void DoTakeABreak(){
    	xDestination = -20; //Top Left of the screen
    	yDestination = -20; //Top Left of the screen
    	receivedAction = true;
    	doingIdle = false;
    }

    public void DoBringToTable(Customer customer, int tableNumber) {
    	for (restaurant.Table myTable : ((HostAgent) agent.getHost()).getTables()){
    		if (myTable.getTableNumber() == tableNumber){
    			xTable = myTable.getPosX();
    			yTable = myTable.getPosY();
    			
    			xDestination = xTable + movementOffset;
    			yDestination = yTable - movementOffset;
    		}
    	}
    	receivedAction = true;
    }
    
    public void DoIdle(){
    	xDestination = 200 + 25 * (agent.getWaiterNumber() % 10); //Idle destination
    	yDestination = 50 + 25 * (agent.getWaiterNumber() / 10); //Idle destination
    	receivedAction = true;
    	doingIdle = true;
    }
    
    public void DoGoToDeathPile(){
    	xDestination = 300;
    	yDestination = 400;
    	receivedAction = true;
    	doingIdle = false;
    }
    
    public void DoGetCustomer(){
    	xDestination = 20; //Host destination
    	yDestination = 20; // Host Destination
    	receivedAction = true;
    	doingIdle = false;
    }
    
    public void DoOffBreak(){
    	wantBreak = false;
    	gui.setWaiterEnabled(agent);
    }
    
    public void DoGiveOrderToCook(){
    	xDestination = 510; //Destination of cook
    	yDestination = 70; //Destination of cook
    	receivedAction = true;
    	doingIdle = false;
    }
    
    public void DoGoToCashier(){
    	xDestination = 100; //Host destination
    	yDestination = 30; // Host Destination
    	receivedAction = true;
    	doingIdle = false;
    }
    
    public void DoWalkToCustomer(restaurant.Table table, String text){
    	xDestination = table.getPosX() + movementOffset;
    	yDestination = table.getPosY() - movementOffset;
    	displayText = text;
    	receivedAction = true;
    	doingIdle = false;
    }

    public void DoLeaveCustomer() {
        xDestination = -movementOffset;
        yDestination = -movementOffset;
        receivedAction = true;
        doingIdle = false;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
   
}
