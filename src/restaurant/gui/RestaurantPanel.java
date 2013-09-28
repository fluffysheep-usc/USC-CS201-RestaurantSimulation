package restaurant.gui;

import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import agent.Agent;

import java.awt.*;
//import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Sarah");
    private CookAgent cook = new CookAgent("Brian");
    //private WaiterAgent waiter = new WaiterAgent("Matt", host, cook);
    //private WaiterAgent waiter2 = new WaiterAgent("David", host, cook);
    //private WaiterGui waitergui = new WaiterGui(waiter);
    //private WaiterGui waiter2gui = new WaiterGui(waiter2);
    
    //List of Agents for pausing.
    private Vector<Agent> agents = new Vector<Agent>();
    
    //List of Waiting customers.
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private JPanel group = new JPanel();
    private JPanel menu = new JPanel();
    private JPanel customerSide = new JPanel();

    public RestaurantGui gui; //reference to main gui
 

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        //waiter.setGUI(waitergui);
        //waiter2.setGUI(waiter2gui);

        //Hack only one waiter.
        //gui.animationPanel.addGui(waitergui);
        //gui.animationPanel.addGui(waiter2gui);
        
        host.startThread(); //Hack only one host.
        cook.startThread(); //Hack only one cook.
        
        //Temporary hack for having only one waiter.
        //waiter.startThread();
        //waiter2.startThread();
        
        agents.add(host); //Hack for only having one cook.
        agents.add(cook); //Hack for only having one cook.
        //Temporary hack for having only one waiter.
        //agents.add(waiter);
        //agents.add(waiter2);

        //setLayout(new GridLayout(gridXPos, gridYPos, gridXWidth, gridYWidth));
       //group.setLayout(new GridLayout(gridXPos, gridYPos, gridXWidth/2, gridYWidth/2));
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        

        //customerPanel.setBorder(BorderFactory.createTitledBorder(""));
        

        initRestLabel();
        customerSide.add(customerPanel);
        add(restLabel);
        customerSide.add(group);
        
        //Add the waiter agent to the Host list waiter agent
        //host.waiters.add(waiter);
        //host.waiters.add(waiter2);
    }
    
    public void addWaiter(String waiterName){
    	WaiterAgent w = new WaiterAgent(waiterName, host, cook);
    	WaiterGui waitergui = new WaiterGui(w);
    	w.setGUI(waitergui);
    	gui.animationPanel.addGui(waitergui);
    	host.waiters.add(w);
    	agents.add(w);
    	w.startThread();
    }
    
    public HostAgent getHost(){
    	return host;
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        Dimension restDim = new Dimension((int)(gui.WINDOWX/2), (int)(gui.WINDOWY/2));
        restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.X_AXIS));
        restLabel.setMinimumSize(restDim);
        restLabel.setMaximumSize(restDim);
        restLabel.setPreferredSize(restDim);
        
        //restLabel.setLayout(new BorderLayout());
        Dimension restCatDim = new Dimension(gui.WINDOWX/4, gui.WINDOWY/2);
        menu.setMinimumSize(restCatDim);
        menu.setMaximumSize(restCatDim);
        menu.setPreferredSize(restCatDim);
        customerSide.setMinimumSize(restCatDim);
        customerSide.setMaximumSize(restCatDim);
        customerSide.setPreferredSize(restCatDim);
        restLabel.add(menu);
        restLabel.add(customerSide);
        //restLabel.setBorder(BorderFactory.createTitledBorder("RestLabel"));
        
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");
        //label.setText("<html></td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");
        menu.add(label);
        //menu.setBorder(BorderFactory.createTitledBorder(""));
        //customerSide.setBorder(BorderFactory.createTitledBorder(""));
        
        //restLabel.setBorder(BorderFactory.createTitledBorder(""));
        menu.add(label, BorderLayout.CENTER);
        menu.add(new JLabel("               "), BorderLayout.EAST);
        menu.add(new JLabel("               "), BorderLayout.WEST);
        
        add(restLabel);
        
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
    }
    
    public void Pause(){
    	for(Agent a : agents){
    		a.Pause();
    	}
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    
    public void addPerson(String type, String name, boolean isHungry) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui, host);

    		gui.animationPanel.addGui(g);// dw
 
    		c.setHost(host);
    		c.setGui(g);
    		
    		if (isHungry){
    			c.getGui().setHungry();;
    		}
    		
    		//Add the newly created customer to the list of customers
    		customers.add(c);
    		//Add the newly created customer to the list of agents (for pausing)
    		agents.add(c);
    		
    		c.startThread();	
    		
    	}
    }
    

}
