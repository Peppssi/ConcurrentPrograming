package MuliplePersonsRidesLift;

import lift.LiftView;

public class LiftThread extends Thread {

    private Monitor mon;
    private LiftView lift;
    private boolean goingUp;
    private int currentFloor, nbrOfFloors;

    public LiftThread(Monitor mon, LiftView lift, int nbrOfFloors) {
        this.mon = mon;
        this.lift = lift;
        this.nbrOfFloors = nbrOfFloors;
        goingUp = true;
        currentFloor = 0;
    }

    public void move() throws InterruptedException {
        if (mon.checkArrays()) {
            return;
        }

        if (goingUp) {
            lift.moveLift(currentFloor, currentFloor + 1);
            currentFloor++;
            mon.updateVars(currentFloor);
        } else {
            lift.moveLift(currentFloor, currentFloor - 1);
            currentFloor--;
            mon.updateVars(currentFloor);
        }
        if (currentFloor % (nbrOfFloors - 1) == 0) {
            goingUp = !goingUp;
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                
                mon.waitForPassengers();
                mon.closeDoors();
                move();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}