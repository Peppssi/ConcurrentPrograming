package MuliplePersonsRidesLift;

import lift.Passenger;

public class PassengerThread extends Thread {

    Monitor mon;
    Passenger pass;

    public PassengerThread(Monitor mon, Passenger pass) {
        this.mon = mon;
        this.pass = pass;
        mon.addToArrays(pass.getStartFloor(), pass.getDestinationFloor());

    }

    @Override
    public void run() {
        try {

            pass.begin();
            mon.waitAndEnter(pass);
            mon.waitAndExit(pass);
            pass.end();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
