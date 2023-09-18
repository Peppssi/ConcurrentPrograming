package MuliplePersonsRidesLift;

import lift.Passenger;

public class PassengerThread extends Thread {

    Monitor mon;
    Passenger pass;
    boolean firstTime;

    public PassengerThread(Monitor mon, Passenger pass) {
        this.mon = mon;
        this.pass = pass;
        firstTime = true;

    }

    @Override
    public void run() {
        try {

            pass.begin();
            if (firstTime) {
                mon.addToEnter(pass.getStartFloor());
                firstTime = false;
            }
            mon.waitAndEnter(pass);
            pass.enterLift();
            mon.note();

            mon.waitAndExit(pass);
            pass.exitLift();
            mon.note();
            
            pass.end();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
