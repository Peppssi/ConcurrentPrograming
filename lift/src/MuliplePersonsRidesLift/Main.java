package MuliplePersonsRidesLift;

import lift.LiftView;
import lift.Passenger;

class Monitor {
    private int[] toEnter; // number of passengers waiting to enter the lift at each floor
    private int[] toExit; // number of passengers (in lift) waiting to exit at each floor
    private int nbrOfFloors, maxPassengers, currentFloor, passengersInLift;
    private boolean goingUp;
    private boolean doorsClosed;
    LiftView lift;

    public Monitor(int nbrOfFloors, int maxPassengers, LiftView view) {
        this.nbrOfFloors = nbrOfFloors;
        this.maxPassengers = maxPassengers;
        currentFloor = 0;
        passengersInLift = 0;
        goingUp = true;
        doorsClosed = true;
        lift = view;
        toEnter = new int[nbrOfFloors];
        toExit = new int[nbrOfFloors];
    }

    public void addToArrays(int floorNbrEnter, int floorNbrExit) {
        toEnter[floorNbrEnter]++;
    }

    public void move() {
        if (goingUp) {
            lift.moveLift(currentFloor, currentFloor + 1);
            currentFloor++;
        } else {
            lift.moveLift(currentFloor, currentFloor - 1);
            currentFloor--;
        }
        if (currentFloor % (nbrOfFloors - 1) == 0) {
            goingUp = !goingUp;
        }
    }

    public synchronized void waitForPassengers() throws InterruptedException {
        lift.showDebugInfo(toEnter, toExit);
        while (toEnter[currentFloor] > 0 && passengersInLift != maxPassengers || toExit[currentFloor] > 0) {
            wait();
        }
    }

    public synchronized void waitAndEnter(Passenger pass) throws InterruptedException {
        while (pass.getStartFloor() != currentFloor || doorsClosed || passengersInLift == maxPassengers) {
            wait();
        }
        toEnter[currentFloor]--;
        toExit[pass.getDestinationFloor()]++;
        passengersInLift++;
        pass.enterLift();
        notifyAll();
    }

    public synchronized void waitAndExit(Passenger pass) throws InterruptedException {
        while (pass.getDestinationFloor() != currentFloor || doorsClosed) {
            wait();
        }
        toExit[currentFloor]--;
        passengersInLift--;
        pass.exitLift();
        notifyAll();
    }

    public synchronized void openDoors() {
        if ((toEnter[currentFloor] > 0 && passengersInLift != maxPassengers) || (toExit[currentFloor] > 0)) {
            doorsClosed = false;
            lift.openDoors(currentFloor);
            notifyAll();
        }
    }

    public synchronized void closeDoors() {
        if (!doorsClosed) {
            lift.closeDoors();
            doorsClosed = true;
        }
    }
}

public class Main {

    public static void main(String[] args) throws InterruptedException {

        final int nbrOfFloors = 7, maxPassengers = 4, totalPassengers = 20;

        LiftView view = new LiftView(nbrOfFloors, maxPassengers);
        Monitor mon = new Monitor(nbrOfFloors, maxPassengers, view);

        for (int i = 0; i < totalPassengers; i++) {
            Passenger pass = view.createPassenger();
            PassengerThread passThread = new PassengerThread(mon, pass);
            passThread.start();
        }

        LiftThread lift = new LiftThread(view, mon, nbrOfFloors);
        lift.start();

    }
}
