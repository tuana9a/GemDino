// 
// Decompiled by Procyon v0.5.36
// 

package com.tuana9a.app;

import com.tuana9a.graphic.Assets;
import com.tuana9a.input.MouseManager;
import com.tuana9a.input.KeyboardManager;
import com.tuana9a.interfaces.ActionEvent;
import com.tuana9a.screen.LoadingScreen;
import com.tuana9a.screen.MenuScreen;
import com.tuana9a.screen.BaseScreen;
import com.tuana9a.utils.Timer;

public class App implements Runnable {
    private static final App instance = new App();

    private boolean running;
    private BaseScreen currentState;
    private BaseScreen lastState;
    private final Timer refreshTimer;

    public static App getInstance() {
        return instance;
    }

    private App() {
        this.running = false;
        this.refreshTimer = new Timer(2);
        Assets.loadAll();
        Display display = Display.getInstance();
        KeyboardManager keyboardManager = KeyboardManager.getInstance();
        MouseManager mouseManager = MouseManager.getInstance();
        display.getFrame().addKeyListener(keyboardManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
    }

    @Override
    public void run() {
        this.running = true;
        MenuScreen menuScreen = MenuScreen.getInstance();
        this.switchToState(menuScreen);
        this.switchToState(menuScreen);
        while (this.running) {
            if (refreshTimer.isTime()) {
                refreshTimer.reset();
                final ActionQueue actionQueue = ActionQueue.getInstance();
                synchronized (this) {
                    this.currentState.update();
                    this.currentState.render();
                    ActionEvent event = actionQueue.pop();
                    while (event != null) {
                        event.perform();
                        event = actionQueue.pop();
                    }
                }
            }
            // TODO: need to optimize
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.running = false;
    }

    public BaseScreen getCurrentState() {
        return this.currentState;
    }

    public void switchToLastState() {
        this.switchToState(this.lastState);
    }

    public void switchToState(final BaseScreen whichState) {
        synchronized (this) {
            if (this.currentState != whichState) {
                this.lastState = this.currentState;
                (this.currentState = whichState).changeCursor();
                this.currentState.changeFont();
            }
        }
    }

}
