package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private String order;
	private double waterLevel;
	private boolean filledFirst;
	private boolean emptiedFirst;
	private WashingMessage tempM;
    

    public WaterController(WashingIO io) {
        this.io = io;
        this.order = "none";
        this.waterLevel = io.getWaterLevel();
        this.filledFirst = false;
        this.emptiedFirst = false;
        
    }

    @Override
    public void run() {
        
        try {

            while (true) {
                // wait for 1s before checking the water level again
                WashingMessage m = receiveWithTimeout(2000 / Settings.SPEEDUP);
                
                
                if (m != null) {
                    System.out.println("got " + m);             
                	order = m.order().toString();
                	tempM = m;
                	filledFirst = false;
                    emptiedFirst = false;
                	
                } 
                
                switch(order) {
	                case("WATER_IDLE"):
	                	io.drain(false);
	                	io.fill(false);
	                	order = "none";
	                	tempM.sender().send(new WashingMessage(this,WashingMessage.Order.ACKNOWLEDGMENT));
	                break;
	                		                    
	                case("WATER_FILL"):
	               	     waterLevel = io.getWaterLevel();
	                
	                	if(waterLevel < 12) {
	                		io.fill(true);
	                	} else {
	                		io.fill(false);
	                		if(filledFirst == false) {
	                			tempM.sender().send(new WashingMessage(this,WashingMessage.Order.ACKNOWLEDGMENT));
	                			filledFirst = true;
	                		}
	                	}
	                	
	                break;
	                
	                case("WATER_DRAIN"):
	                	waterLevel = io.getWaterLevel();
	                
	                	if(waterLevel > 0) {
	                		io.drain(true);
	                	} 
	                	
	                	if (waterLevel == 0){
	                		if(emptiedFirst == false) {
	                			tempM.sender().send(new WashingMessage(this,WashingMessage.Order.ACKNOWLEDGMENT));
	                			emptiedFirst = true;
	                		}
	                	}
	                
	                break;
            	
                } 
                    
                 
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
