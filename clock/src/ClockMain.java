import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.Choice;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

// class Monitor {

//     public synchronized void clockHandler() throws InterruptedException {

//         while (true) {
//             Thread.sleep(1000);
//             System.out.println("clockHandler Test");
//         }
//     }

//     public synchronized void alarmHandler() throws InterruptedException {

//         while (true) {
//             Thread.sleep(1000);
//             System.out.println("alarmHandler Test");
//         }
//     }

// }

public class ClockMain {
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();
        //Monitor mon = new Monitor();
        //Semaphore mutex = new Semaphore(1);

        ClockInput in = emulator.getInput();
        ClockOutput out = emulator.getOutput();

        // Thread clockThread = new Thread(() -> {
        //     try {
        //         mon.clockHandler();
        //     } catch (InterruptedException e) {
        //         System.out.println("InterruptedException hände i Clockhandler");
        //         e.printStackTrace();
        //     }
        // });
        // clockThread.start();

        // Thread alarmThread = new Thread(() -> {
        //     try {
        //         mon.alarmHandler();
        //     } catch (InterruptedException e) {
        //         System.out.println("InterruptedException hände i alarmhandler");
        //         e.printStackTrace();
        //     }
        // });
        // alarmThread.start();

        out.displayTime(15, 2, 37); // arbitrary time: just an example

        while (true) {
            UserInput userInput = in.getUserInput();
            Choice c = userInput.choice();
            int h = userInput.hours();
            int m = userInput.minutes();
            int s = userInput.seconds();
            System.out.println("choice=" + c + " h=" + h + " m=" + m + " s=" + s);
            //mutex.release();
        }
    }

}
