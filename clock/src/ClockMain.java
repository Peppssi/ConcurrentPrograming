import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.Choice;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

class Monitor {

    private int timeSec = 0;
    private int timeMin = 0;
    private int timeHour = 0;

    private int alarmSec = 0;
    private int alarmMin = 0;
    private int alarmHour = 0;

    private boolean indState = false;

    private Semaphore mutex = new Semaphore(1);

    private long startTime = System.currentTimeMillis();

    private int i = 0;
    private boolean alarming = false;

    public void clockTicking(ClockOutput out) throws InterruptedException {

        while (true) {
            out.displayTime(timeHour, timeMin, timeSec);

            if (timeHour == alarmHour && timeMin == alarmMin && timeSec == alarmSec && indState) {
                alarming = true;
            }

            if (alarming) {
                out.alarm();
                i++;
                if (i == 20 || !indState) {
                    alarming = false;
                }
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            long sleepTime = 1000 - elapsedTime;

            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }

            startTime = System.currentTimeMillis();

            mutex.acquire();
            timeSec++;

            if (timeSec == 60) {
                timeSec = 0;
                timeMin++;
                if (timeMin == 60) {
                    timeMin = 0;
                    timeHour++;
                }
                if (timeHour == 24) {
                    timeHour = 0;
                }
            }
            mutex.release();
        }
    }

    public void setTime(int hour, int min, int sec) throws InterruptedException {

        mutex.acquire();
        timeHour = hour;
        timeMin = min;
        timeSec = sec;
        mutex.release();

    }

    public void setAlarm(int hour, int min, int sec) throws InterruptedException {

        mutex.acquire();
        alarmHour = hour;
        alarmMin = min;
        alarmSec = sec;
        mutex.release();
    }

    public void toggleAlarm(ClockOutput out) throws InterruptedException {
        mutex.acquire();
        out.setAlarmIndicator(!indState);
        indState = !indState;
        mutex.release();
    }

}

public class ClockMain {
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();
        Monitor mon = new Monitor();

        ClockInput in = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        Semaphore sem = in.getSemaphore();

        Thread clockThread = new Thread(() -> {
            try {
                mon.clockTicking(out);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        clockThread.start();

        while (true) {
            sem.acquire();
            UserInput userInput = in.getUserInput();
            Choice c = userInput.choice();
            int h = userInput.hours();
            int m = userInput.minutes();
            int s = userInput.seconds();
            System.out.println("choice=" + c + " h=" + h + " m=" + m + " s=" + s);
            switch (c.toString()) {
                case "SET_TIME":
                    mon.setTime(h, m, s);
                    break;

                case "SET_ALARM":
                    mon.setAlarm(h, m, s);
                    break;

                case "TOGGLE_ALARM":
                    mon.toggleAlarm(out);
                    break;
            }
        }
    }

}
