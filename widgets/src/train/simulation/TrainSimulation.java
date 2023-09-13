package train.simulation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

class Monitor {

    private Set<Segment> segBusy = new HashSet<Segment>();

    private synchronized void enterSeg(Segment s) throws InterruptedException {
        while (segBusy.contains(s)) {
            wait();
        }
        segBusy.add(s);
    }

    private synchronized void exitSeg(Segment s){
        segBusy.remove(s);
        notifyAll();
    }

    public void takeStep(Segment s) throws InterruptedException {
        enterSeg(s);
        s.enter();
    }

    public void removeStep(Segment s) throws InterruptedException {
        s.exit();
        exitSeg(s);
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
                mon.takeStep(temp);
                q.add(temp);
                mon.removeStep(q.remove(0));
            }
        } catch (InterruptedException e) {
            System.out.println("FEL");
            e.printStackTrace();
        }
    }
}

public class TrainSimulation {

    public static void main(String[] args) throws InterruptedException {

        int nbr_of_trains = 20;

        TrainView view = new TrainView();

        Monitor mon = new Monitor();

        for (int i = 0; i < nbr_of_trains; i++) {
            Train t = new Train(view.loadRoute(), mon);
            t.start();
        }
    }
}