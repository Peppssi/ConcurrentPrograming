package wash.control;

import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

    public static void main(String[] args) throws InterruptedException {
        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);

        WashingIO io = sim.startSimulation();

        Thread currentProgram = null;

        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();

        while (true) {
            int n = io.awaitButton();
            System.out.println("user selected program " + n);

            switch (n) {
                case 0:
                    currentProgram.interrupt();
                    break;
                case 1:
                    WashingProgram1 p1 = new WashingProgram1(io, temp, water, spin);
                    p1.start();
                    currentProgram = p1;
                    break;
                case 2:
                WashingProgram2 p2 = new WashingProgram2(io, temp, water, spin);
                    p2.start();
                    currentProgram = p2;
                    break;
                case 3:
                    WashingProgram3 p3 = new WashingProgram3(io, temp, water, spin);
                    p3.start();
                    currentProgram = p3;
                    break;
                default:
                    // code block
            }

        }
    }
};
