package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 */
public class AnotherConcurrentGUI extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final int WAIT = 10_000;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    private final Agent agent = new Agent();
    /**
     * Builds a new ConcurrentGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        new Thread(agent).start();
        new Thread(() -> {
            try {
                Thread.sleep(WAIT);
                endOfGUI();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }).start();

        up.addActionListener(new ActionListener() {
            /**
             * event handler associated to action event on button up.
             * 
             * @param e
             *            the action event that will be handled by this listener
             */
            @Override
            public void actionPerformed(final ActionEvent e) {
                // Agent should be final
                agent.upTime();
            }
        });

        down.addActionListener(new ActionListener() {
            /**
             * event handler associated to action event on button down.
             * 
             * @param e
             *            the action event that will be handled by this listener
             */
            @Override
            public void actionPerformed(final ActionEvent e) {
                // Agent should be final
                agent.downTime();
            }
        });

        stop.addActionListener(new ActionListener() {
            /**
             * event handler associated to action event on button stop.
             * 
             * @param e
             *            the action event that will be handled by this listener
             */
            @Override
            public void actionPerformed(final ActionEvent e) {
                endOfGUI();
            }
        });

    }

    /**
     * Stops the time and enables the Buttons.
     */
    private void endOfGUI() {
        agent.stopCounting();
        up.setEnabled(false);
        down.setEnabled(false);
        stop.setEnabled(false);
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean up = true;
        private volatile int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    counter += up ? 1 : -1;
                    /* Questo lo usa nell'esempio da seguire, ma nelle soluzione fa come quello non commentato
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            // This will happen in the EDT: since i'm reading counter it needs to be volatile.
                            ConcurrentGUI.this.display.setText(Integer.toString(Agent.this.counter));
                        }
                    });*/
                    final var todisplay = Integer.toString(counter);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            display.setText(todisplay);
                        }
                    });
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        public void downTime() {
            this.up = false;
        }

        public void upTime() {
            this.up = true;
        }

    }

}
