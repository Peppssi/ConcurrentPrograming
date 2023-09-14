package factory.simulation;
import java.util.concurrent.Semaphore;
import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

class Monitor {

    private Tool press, paint;
    private Conveyor conveyor;
    private Semaphore sem;

    public Monitor(Tool press, Tool paint, Conveyor conveyor) {
        this.press = press;
        this.paint = paint;
        this.conveyor = conveyor;
        sem = new Semaphore(0);
    }

    public void runPress() throws InterruptedException {
        while (true) {
            press.waitFor(Widget.GREEN_BLOB);
            stopConveyor();
            press.performAction();
            startConveyor();
        }
    }

    public void runPaint() throws InterruptedException {
        while (true) {
            paint.waitFor(Widget.BLUE_MARBLE);
            stopConveyor();
            paint.performAction();
            startConveyor();
        }
    }

    private synchronized void stopConveyor() {
        sem.release();
        conveyor.off();
    }

    private synchronized void startConveyor() throws InterruptedException {
        sem.acquire();
        notifyAll();
        while (sem.availablePermits() > 0) {
            wait();
        }
        conveyor.on();
    }
}

public class FactoryController {

    public static void main(String[] args) {

        Factory factory = new Factory();
        Conveyor conveyor = factory.getConveyor();

        Tool press = factory.getPressTool();
        Tool paint = factory.getPaintTool();

        Monitor mon = new Monitor(press, paint, conveyor);

        Thread pressThread = new Thread(() -> {
            try {
                mon.runPress();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        pressThread.start();

        Thread paintThread = new Thread(() -> {
            try {
                mon.runPaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        paintThread.start();
    }
}
