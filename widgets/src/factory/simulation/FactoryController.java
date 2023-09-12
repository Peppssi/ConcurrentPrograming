package factory.simulation;

import java.util.concurrent.Semaphore;
import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

class Monitor {

    Tool press, paint;
    boolean pressing, painting, convOff;
    Conveyor conveyor;
    Semaphore mutex, mutex2;

    public Monitor(Tool press, Tool paint, Conveyor conveyor) {
        this.press = press;
        this.paint = paint;
        this.conveyor = conveyor;
        pressing = false;
        painting = false;
        convOff = false;
        mutex = new Semaphore(-1);
        mutex2 = new Semaphore(1);
    }

    public void runPress() throws InterruptedException {
        while (true) {
            press.waitFor(Widget.GREEN_BLOB);
            conveyor.off();
            press.performAction();
            mutex.release();
            trySetConvOn();
        }
    }

    public void runPaint() throws InterruptedException {
        while (true) {
            paint.waitFor(Widget.BLUE_MARBLE);
            conveyor.off();
            paint.performAction();
            mutex.release();
            trySetConvOn();
        }
    }

    private void trySetConvOn() throws InterruptedException {
        mutex2.acquire();
        if (convOff) {
            mutex.acquire();
            conveyor.on();
            convOff = false;
        }
        mutex2.release();
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
