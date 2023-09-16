package MuliplePersonsRidesLift;

import lift.LiftView;

public class LiftThread extends Thread {

    Monitor mon;
    LiftView lift;
    int currentFloor, nextFloor, nbrOfFloors;
    String direction;

    public LiftThread(LiftView lift, Monitor mon, int nbrOfFloors) {
        this.mon = mon;
        this.lift = lift;
        this.nbrOfFloors = nbrOfFloors;
        currentFloor = 0;
        direction = "UP";
    }

    @Override
    public void run() {
        try {
            while (true) {

                mon.openDoors();
                mon.waitForPassengers();
                mon.closeDoors();
                mon.move();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}