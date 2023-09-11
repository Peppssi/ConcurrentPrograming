package train.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

class Monitor {

    Set<Segment> hashSet = new HashSet<Segment>();

    public synchronized void addBusySeg(Segment s) throws InterruptedException {
        hashSet.add(s);
    }

    public synchronized void removeBusySeg(Segment s) throws InterruptedException {
        hashSet.remove(s);
        notifyAll();
    }

    public synchronized boolean isBusy(Segment s) throws InterruptedException {
        if (hashSet.contains(s)) {
            return true;
        } else
            return false;
    }

}

class Train extends Thread {

    private Route route;
    private Monitor mon;
    private int trainLength;

    public Train(Route route, Monitor mon) {
        this.route = route;
        this.mon = mon;
        trainLength = 3;
    }

    @Override
    public void run() {
        try {

            ArrayList<Segment> q = new ArrayList<>();

            for (int i = 0; i < trainLength; i++) {
                Segment s = route.next();
                q.add(s);
                q.get(i).enter();
            }

            while (true) {
                Segment temp = route.next();
                while(mon.isBusy(temp)){
                    wait();
                }
                mon.addBusySeg(temp);
                q.add(temp);
                q.get(q.size() - 1).enter();

                Segment temp2 = q.remove(0);
                temp2.exit();
                mon.removeBusySeg(temp2);
            }
        } catch (InterruptedException e) {
            System.out.println("FEL");
            e.printStackTrace();
        }
    }
}

public class TrainSimulation {

    public static void main(String[] args) throws InterruptedException {

        TrainView view = new TrainView();

        Monitor mon = new Monitor();

        for (int i = 0; i < 3; i++) {
            Train t = new Train(view.loadRoute(), mon);
            t.start();
        }

    }

}
