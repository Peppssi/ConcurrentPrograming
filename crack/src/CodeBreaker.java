import java.lang.reflect.Executable;
import java.math.BigInteger;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;

    private final JProgressBar mainProgressBar;

    private static ExecutorService pool;

    // -----------------------------------------------------------------------

    private CodeBreaker() {
        StatusWindow w = new StatusWindow();
        w.enableErrorChecks();
        workList = w.getWorkList();
        progressList = w.getProgressList();
        mainProgressBar = w.getProgressBar();
    }

    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;
        ProgressItem progressItem;
        JProgressBar mainProgressBar;

        Tracker(ProgressItem progressItem, JProgressBar mainProgressBar) {
            this.progressItem = progressItem;
            this.mainProgressBar = mainProgressBar;
        }

        @Override
        public void onProgress(int ppmDelta) {
            totalProgress += ppmDelta;
            SwingUtilities.invokeLater(() -> {
                progressItem.getProgressBar().setValue(totalProgress);
                mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta);
            });

        }
    }

    // -----------------------------------------------------------------------

    public static void main(String[] args) {

        /*
         * Most Swing operations (such as creating view elements) must be performed in
         * the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
            new Sniffer(codeBreaker).start();
        });

        pool = Executors.newFixedThreadPool(2);

    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
        SwingUtilities.invokeLater(() -> {
            WorklistItem w = new WorklistItem(n, message);
            JButton breakButton = new JButton("break");
            w.add(breakButton);

            breakButton.addActionListener(e -> {
                ProgressItem p = new ProgressItem(n, message);
                JButton cancelButton = new JButton("cancel");
                SwingUtilities.invokeLater(() -> {
                    progressList.add(p);
                    workList.remove(w);
                    mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
                    p.add(cancelButton);
                    cancelButton.addActionListener(e3 -> {
                        SwingUtilities.invokeLater(() -> {
                            p.getTextArea().setText("Task was canceled!");
                            //task.cancel();

                        });
                    });
                });

                Runnable task = () -> {
                    try {
                        ProgressTracker tracker = new Tracker(p, mainProgressBar);
                        String s = Factorizer.crack(message, n, tracker);
                        SwingUtilities.invokeLater(() -> {
                            p.getTextArea().setText(s);
                        });
                        SwingUtilities.invokeLater(() -> {
                            JButton removeButton = new JButton("remove");
                            p.add(removeButton);
                            p.remove(cancelButton);
                            removeButton.addActionListener(e2 -> {
                                progressList.remove(p);
                                mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
                                mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
                            });
                        });
                    } catch (InterruptedException e1) {
                        System.out.println("FEL, antagligen factorize som något hände i.");
                        e1.printStackTrace();
                    }
                
                };

                pool.execute(task);

            });
            workList.add(w);

        });
    }
}
