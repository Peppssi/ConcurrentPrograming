package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

class Monitor {

    private Tool press, paint;
    private boolean pressing, painting;
    private Conveyor conveyor;

    public Monitor(Tool press, Tool paint, Conveyor conveyor) {
        this.press = press;
        this.paint = paint;
        this.conveyor = conveyor;
        pressing = false;
        painting = false;
    }

    public void runPress() throws InterruptedException {
        while (true) {
            press.waitFor(Widget.GREEN_BLOB);
            conveyor.off();
            press();
        }
    }

    public void runPaint() throws InterruptedException {
        while (true) {
            paint.waitFor(Widget.BLUE_MARBLE);
            conveyor.off();
            paint();
        }
    }

    private void paint() throws InterruptedException {
        painting = true;
        paint.performAction();
        painting = false;
        notifyAll();
        while (pressing) {
            wait();
        }
        conveyor.on();

    }

    private void press() throws InterruptedException {
        pressing = true;
        press.performAction();
        pressing = false;
        notifyAll();
        while (painting) {
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
