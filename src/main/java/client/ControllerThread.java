package client;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import javafx.application.Platform;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Thread to handle Controller Input and Keyboard Input.
 */
public class ControllerThread implements Runnable {

    private final ControllerManager controllers;
    public volatile boolean running;
    private static ControllerThread instance;

    /**
     * Returns instance of ControllerThread.
     *
     * @return Instance of ControllerThread.
     */
    public static ControllerThread getInstance() {
        return instance;
    }

    /**
     * Defines input actions.
     */
    public enum InputAction {
        A, B, STICK
    }

    /**
     * Initializes variables.
     */
    public ControllerThread() {
        instance = this;
        running = true;
        controllers = new ControllerManager();
        controllers.initSDLGamepad();
    }

    /**
     * Returns actions in Set of Input Actions.
     * Deciding whether an input action was performed or not.
     */
    Set<InputAction> actions() {
        ControllerState currState = controllers.getState(0);
        if (!currState.isConnected) {
            return Collections.emptySet();
        }
        Set<InputAction> actions = new HashSet<>();
        if (currState.leftStickMagnitude > 0.3) {
            actions.add(InputAction.STICK);
        }
        if (currState.a) {
            actions.add(InputAction.A);
        }
        if (currState.b) {
            actions.add(InputAction.B);
        }
        return actions;
    }

    /**
     * Controller thread main loop.
     */
    @Override
    public void run() {
        while (running) {
            ControllerState currState = controllers.getState(0);
            if (currState.isConnected) {
                System.out.println("Controller connected!");
                while (currState.isConnected && running) {
                    currState = controllers.getState(0);

                    if (actions().contains(InputAction.A)) {
                        Platform.runLater(() -> {
                            System.out.println("A pressed");
                        });
                        actions().remove(InputAction.A);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (actions().contains(InputAction.B)) {
                        Platform.runLater(() -> {
                            System.out.println("B pressed");
                        });
                        actions().remove(InputAction.B);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        controllers.quitSDLGamepad();
    }
    public static void main(String[] args) {
        Thread t = new Thread(new ControllerThread());
        t.start();
    }
}