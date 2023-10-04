package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
	private WashingIO io;
	private String order;
	private double temp;
	private double mu;
	private double dt;
	private double ml;
	private boolean heatedMessage;
	private WashingMessage tempM;
	

    public TemperatureController(WashingIO io) {
        this.io = io;
        order = "none";
        temp = io.getTemperature();
        dt = 10; //10 sekunder
        mu = dt * 0.0478;
        ml = dt * 0.00952;
        heatedMessage = false;
        
    }

    @Override
    public void run() {
        
        try {

            while (true) {
                // wait for 10s before checking the temp again
                WashingMessage m = receiveWithTimeout(10000 / Settings.SPEEDUP);
                
                
                if (m != null) {
                    System.out.println("got " + m);             
                	order = m.order().toString();
                	tempM = m;
                	heatedMessage = false;
                	
                } 
                
                switch(order) {
	                case("TEMP_IDLE"):
	                	io.heat(false);	 
	                	order = "none";
	                	tempM.sender().send(new WashingMessage(this,WashingMessage.Order.ACKNOWLEDGMENT));
	                break;
	                		                    
	                case("TEMP_SET_40"):
	                	temp = io.getTemperature();
	                
	                	if(temp > 38 && heatedMessage == false) {
	                		tempM.sender().send(new WashingMessage(this,WashingMessage.Order.ACKNOWLEDGMENT));
	                		heatedMessage = true;
	                	}
	                
	                	if(temp >= (40-mu)) {
	                		io.heat(false);
	                		
	                	} else if (temp <= (38+ml)){
	                		io.heat(true);
	                		
	                	}
	                
	                	
	                break;
	                
	                case("TEMP_SET_60"):
	                	temp = io.getTemperature();
	                
                	if(temp > 58 && heatedMessage == false) {
                		tempM.sender().send(new WashingMessage(this,WashingMessage.Order.ACKNOWLEDGMENT));
                		heatedMessage = true;
                	}
                
                	if(temp >= (60-mu)) {
                		io.heat(false);
                		
                	} else if (temp <= (58+ml)){
                		io.heat(true);
                		
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
