package MuliplePersonsRidesLift;

import lift.Passenger;

public class PassengerThread extends Thread {

    Monitor mon;
    Passenger pass;

    public PassengerThread(Monitor mon, Passenger pass) {
        this.mon = mon;
        this.pass = pass;

    }

    @Override
    public void run() {
        try {

            pass.begin();
            mon.addToEnter(pass.getStartFloor());

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
