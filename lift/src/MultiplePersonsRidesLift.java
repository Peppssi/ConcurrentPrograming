
import java.util.ArrayList;

import javax.print.event.PrintEvent;

import lift.LiftView;
import lift.Passenger;

class Monitor {
    private int[] toEnter; // number of passengers waiting to enter the lift at each floor
    private int[] toExit; // number of passengers (in lift) waiting to exit at each floor
    private int nbrOfFloors, maxPassengers, currentFloor, passengersInLift;
    private String direction;
    LiftView lift;

    public Monitor(int nbrOfFloors, int maxPassengers, LiftView view) {
        this.nbrOfFloors = nbrOfFloors;
        this.maxPassengers = maxPassengers;
        currentFloor = 0;
        passengersInLift = 0;
        lift = view;
        direction = "UP";
        toEnter = new int[nbrOfFloors];
        toExit = new int[nbrOfFloors];
    }

    public void setUpArrays(int[] tempEnter, int[] tempExit) {
        toEnter = tempEnter;
        toExit = tempExit;
    }

    public void moveLift() {
        if (currentFloor == nbrOfFloors - 1) {
            direction = "DOWN";
        } else if (currentFloor == 0) {
            direction = "UP";
        }
        if (direction == "UP") {
            lift.moveLift(currentFloor, currentFloor + 1);
            currentFloor++;
        } else {
            lift.moveLift(currentFloor, currentFloor - 1);
            currentFloor--;
        }
    }

    public synchronized void openDoors() {
        lift.openDoors(currentFloor);
        notifyAll();
    }

    public synchronized void waitForPassengers() throws InterruptedException {
        while (toEnter[currentFloor] > 0 && passengersInLift < maxPassengers) {
            wait();
        }
        lift.closeDoors();
    }

    public synchronized void PassEnterLift(Passenger pass) throws InterruptedException {
        while (pass.getStartFloor() != currentFloor || passengersInLift >= maxPassengers) {
            wait();
        }
        pass.enterLift();
        toEnter[currentFloor]--;
        passengersInLift++;
        notifyAll();
    }

    public synchronized void passExiLift(Passenger pass) throws InterruptedException {
        while(pass.getDestinationFloor() != currentFloor){
            wait();
        }
        pass.exitLift();
        toExit[currentFloor]--;
        passengersInLift--;
        notifyAll();
    }
}

public class MultiplePersonsRidesLift {

    public static void main(String[] args) throws InterruptedException {

        final int nbrOfFloors = 7, maxPassengers = 4, totalPassengers = 20;
        int[] toEnter = new int[nbrOfFloors], toExit = new int[nbrOfFloors];
        ArrayList<Passenger> passengerList = new ArrayList<Passenger>(totalPassengers);

        LiftView view = new LiftView(nbrOfFloors, maxPassengers);
        Monitor mon = new Monitor(nbrOfFloors, maxPassengers, view);

        for (int i = 0; i < totalPassengers; i++) {
            Passenger pass = view.createPassenger();
            passengerList.add(pass);
            Thread passThread = new Thread(() -> {
                try {
                    pass.begin();
                    mon.PassEnterLift(pass);
                    mon.passExiLift(pass);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            passThread.start();
        }

        mon.setUpArrays(toEnter, toExit);

        Thread lift = new Thread(() -> {
            while (true) {
                try {
                    mon.openDoors();
                    mon.waitForPassengers();
                    mon.moveLift();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        lift.start();

        for (int i = 0; i < nbrOfFloors; i++) {
            for (int n = 0; n < totalPassengers; n++) {
                if (passengerList.get(n).getStartFloor() == i) {
                    toEnter[i]++;
                }
                if (passengerList.get(n).getDestinationFloor() == i) {
                    toExit[i]++;
                }
            }
        }

    }
}
