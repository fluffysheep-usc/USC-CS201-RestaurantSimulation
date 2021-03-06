package restaurant.gui;

import javax.swing.*;

import restaurant.HostAgent;
import restaurant.interfaces.Host;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 600;
    private final int WINDOWY = 450;
    
    
    private final int TABLEWIDTH = 50;
    private final int TABLEHEIGHT = 50;
    
    private final int WaitingAreaX = 15;
    private final int WaitingAreaY = 15;
    private final int WaitingWidth = 70;
    private final int WaitingHeight = 400;
    
    
    private final int KitchenAreaX = 520;
    private final int KitchenAreaY = 50;
    private final int KitchenWidth = 10;
    private final int KitchenHeight = 50;
    
    private final int CashierX = 100;
    private final int CashierY = 15;
    private final int CashierW = 20;
    private final int CashierH = 20;
    
    private final int timerint = 5;
    public Timer timer;
    private List<Gui> guis = new ArrayList<Gui>();
    
    private Host host;
    public void setHost(Host host){
    	this.host = host;
    }

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	timer = new Timer(timerint, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}
	
	public void toggleTimer(){
		if (timer.isRunning()){
			timer.stop();
		}
		else timer.start();
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        //Cashier
        g2.setColor(Color.green);
        g2.fillRect(CashierX, CashierY, CashierW, CashierH);
        g2.setColor(Color.black);
        g2.drawString("Cashier", CashierX, CashierY);
        
        
        //Waiting Area
        g2.setColor(Color.CYAN);
        g2.fillRect(WaitingAreaX, WaitingAreaY, WaitingWidth, WaitingHeight);
        g2.setColor(Color.black);
        g2.drawString("Waiting Area", WaitingAreaX, WaitingAreaY);
        
        
        //Table to give to waiters.
        g2.setColor(Color.gray);
        g2.fillRect(KitchenAreaX, KitchenAreaY, KitchenWidth, KitchenHeight);
        g2.setColor(Color.black);
        g2.drawString("Plating", KitchenAreaX-10, KitchenAreaY +25);
        
      //Grills
        g2.setColor(Color.red);
        g2.fillRect(KitchenAreaX, KitchenAreaY + 50, KitchenWidth, KitchenHeight);
        g2.setColor(Color.black);
        g2.drawString("Grills", KitchenAreaX-10, KitchenAreaY + 75);
        
        if (host instanceof HostAgent)
        for (restaurant.Table t : ((HostAgent) host).getTables()){
        	//Here is the table
            g2.setColor(Color.ORANGE);
            g2.fillRect(t.getPosX(), t.getPosY(), TABLEWIDTH, TABLEHEIGHT);//200 and 250 need to be table params
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
    public void addGui(CookGui gui) {
        guis.add(gui);
    }
}
