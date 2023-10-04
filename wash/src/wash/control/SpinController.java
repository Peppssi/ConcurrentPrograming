package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.io.WashingIO.Spin;

public class SpinController extends ActorThread<WashingMessage> {

    WashingIO io;
    boolean right;
    String order;

    public SpinController(WashingIO io) {
        this.io = io;
        right = false;
        order = "NONE";
    }

    @Override
    public void run() {

        try {

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    //System.out.println("spin got " + m);
                    order = m.order().toString();
                }
                switch (order) {
                    case "SPIN_SLOW": {
                        if (!right) {
                            io.setSpinMode(Spin.LEFT);
                        } else {
                            io.setSpinMode(Spin.RIGHT);
                        }
                        right = !right;
                        break;
                    }
                    case "SPIN_OFF":
                    io.setSpinMode(Spin.IDLE);
                        break;
                    case "SPIN_FAST":
                    io.setSpinMode(Spin.FAST);
                        break;
                    default:
                        // code block
                }

                if (m != null) {
                    //System.out.println("ACKNOWLEDGMENT " + order + " sent");
                    m.sender().send(new WashingMessage(this, WashingMessage.Order.ACKNOWLEDGMENT));
                }
            }

        } catch (

        InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
