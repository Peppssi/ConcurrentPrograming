package MuliplePersonsRidesLift;

import lift.LiftView;
import lift.Passenger;

public class PassengerThread extends Thread {

    Monitor mon;
    Passenger pass;
    LiftView view;

    public PassengerThread(Monitor mon, Passenger pass, LiftView view) {
        this.mon = mon;
        this.pass = pass;
        this.view = view;

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

            // Passenger pass = view.createPassenger();
            // PassengerThread passThread = new PassengerThread(mon, pass, view);
            // passThread.start();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
