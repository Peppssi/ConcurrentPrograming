package MuliplePersonsRidesLift;

import lift.LiftView;
import lift.Passenger;

class Monitor {
    private int[] toEnter; // number of passengers waiting to enter the lift at each floor
    private int[] toExit; // number of passengers (in lift) waiting to exit at each floor
    private int nbrOfFloors, maxPassengers, currentFloor, passengersInLift, currentlyMoving;
    //private boolean goingUp;
    public boolean doorsClosed;
    LiftView lift;

    public Monitor(int nbrOfFloors, int maxPassengers, LiftView view) {
        this.nbrOfFloors = nbrOfFloors;
        this.maxPassengers = maxPassengers;
        currentFloor = 0;
        passengersInLift = 0;
        currentlyMoving = 0;
        //goingUp = true;
        doorsClosed = true;
        lift = view;
        toEnter = new int[nbrOfFloors];
        toExit = new int[nbrOfFloors];
    }

    public synchronized void addToEnter(int floorNbrEnter) {
        toEnter[floorNbrEnter]++;
        notifyAll();
    }

    private synchronized boolean isEmpty(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean checkArrays() {
        return (isEmpty(toEnter) && isEmpty(toExit));
    }

    // public synchronized boolean checkRemainingFloors() {
    //     if (goingUp) {
    //         for (int i = currentFloor; i < nbrOfFloors; i++) {
    //             if (toEnter[i] != 0 || toExit[i] != 0) {
    //                 notifyAll();
    //                 return false;
    //             }
    //         }
    //     } else {
    //         for (int i = currentFloor; i >= 0; i--) {
    //             if (toEnter[i] != 0 || toExit[i] != 0) {
    //                 notifyAll();
    //                 return false;
    //             }
    //         }
    //     }
    //     notifyAll();
    //     return true;
    // }

    public synchronized void updateVars(int currFloor) {
        currentFloor = currFloor;
    }

    public synchronized void waitForPassengers() throws InterruptedException {
        openDoors();
        System.out.println("Current floor:" + currentFloor + "DoorsClosed: " + doorsClosed + "passengersInLift: " + passengersInLift);
        lift.showDebugInfo(toEnter, toExit);
        while ((toEnter[currentFloor] > 0 && passengersInLift != maxPassengers) || (toExit[currentFloor] > 0)
                || currentlyMoving != 0) {
            wait();
        }
    }

    public synchronized void openDoors() throws InterruptedException {
        if (toEnter[currentFloor] > 0 && passengersInLift != maxPassengers || toExit[currentFloor] > 0) {
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

    public synchronized void waitAndEnter(Passenger pass) throws InterruptedException {
        while (pass.getStartFloor() != currentFloor || doorsClosed || passengersInLift == maxPassengers) {
            wait();
        }
        currentlyMoving++;
        toEnter[currentFloor]--;
        toExit[pass.getDestinationFloor()]++;
        passengersInLift++;
    }

    public synchronized void waitAndExit(Passenger pass) throws InterruptedException {
        while (pass.getDestinationFloor() != currentFloor || doorsClosed) {
            wait();
        }
        currentlyMoving++;
        toExit[currentFloor]--;
        passengersInLift--;
    }

    public synchronized void note() {
        currentlyMoving--;
        notifyAll();
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {

        final int nbrOfFloors = 10, maxPassengers = 4, totalPassengers = 20;

        LiftView view = new LiftView(nbrOfFloors, maxPassengers);
        Monitor mon = new Monitor(nbrOfFloors, maxPassengers, view);

        for (int i = 0; i < totalPassengers; i++) {
            Passenger pass = view.createPassenger();
            PassengerThread passThread = new PassengerThread(mon, pass, view);
            passThread.start();
        }

        LiftThread lift = new LiftThread(mon, view, nbrOfFloors);
        lift.start();

    }
}
