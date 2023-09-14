package factory.simulation;
import java.util.concurrent.Semaphore;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

class Monitor {

    private Tool press, paint;
    private boolean isPressing, isPainting;
    private Conveyor conveyor;

    public Monitor(Tool press, Tool paint, Conveyor conveyor) {
        this.press = press;
        this.paint = paint;
        this.conveyor = conveyor;
        isPressing = false;
        isPainting = false;
    }

    public void runPress() throws InterruptedException {
        while (true) {
            conveyor.on();
            press.waitFor(Widget.GREEN_BLOB);
            conveyor.off();
            isPressing = true;
            press.performAction();
            isPressing = false;
            waitFor();
        }
    }

    public void runPaint() throws InterruptedException {
        while (true) {
            conveyor.on();
            paint.waitFor(Widget.BLUE_MARBLE);
            conveyor.off();
            isPainting = true;
            paint.performAction();
            isPainting = false;
            waitFor();
        }
    }

    private synchronized void waitFor() throws InterruptedException{
        notifyAll();
        while(isPainting || isPressing){
                wait();
        }
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
