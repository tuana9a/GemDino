// 
// Decompiled by Procyon v0.5.36
// 

package com.tuana9a.screen;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.tuana9a.Display;
import com.tuana9a.utils.Utility;
import com.tuana9a.ui.UiButton;
import com.tuana9a.ui.ActionListener;
import com.tuana9a.animation.UiAnimation;
import com.tuana9a.graphic.Assets;
import com.tuana9a.App;

public class MenuScreen extends BaseScreen {
    private static final MenuScreen instance = new MenuScreen();

    private MenuScreen() {
    }

    public static MenuScreen getInstance() {
        return instance;
    }

    @Override
    public void initUi() {
        final int screenW = this.getDisplayWidth();
        final int screenH = this.getDisplayHeight();
        final int halfW = screenW / 2;
        final int halfH = screenH / 2;
        final int quarterW = screenW / 4;
        final int quarterH = screenH / 4;
        final int threeQuarterW = halfW + quarterW;
        final int threeQuarterH = halfH + quarterH;
        final int buttonSize = 100;
        final int iconSize = 64;
        final int barSize = 30;
        final int minSpace = 15;
        final App app = App.getInstance();
        final UiButton startGame = new UiButton(this, halfW - buttonSize / 2.0, halfH - buttonSize / 2.0, buttonSize, buttonSize, new UiAnimation(Assets.newGameButton), new ActionListener() {
            @Override
            public void performAction() {
                synchronized (app) {
                    GameScreen gameScreen = GameScreen.getInstance();
                    app.switchToState(gameScreen);
                    gameScreen.startGame();
                }
            }
        });
        final UiButton exit = new UiButton(this, screenW - minSpace - buttonSize, minSpace, buttonSize, buttonSize, new UiAnimation(Assets.exitButton), new ActionListener() {
            @Override
            public void performAction() {
                synchronized (app) {
                    System.exit(0);
                }
            }
        });
        final UiButton music = new UiButton(this, minSpace, minSpace, buttonSize, buttonSize, new UiAnimation(Assets.musicButton), new ActionListener() {
            @Override
            public void performAction() {
            }
        });
        final UiButton sound = new UiButton(this, minSpace, minSpace + buttonSize, buttonSize, buttonSize, new UiAnimation(Assets.soundButton), new ActionListener() {
            @Override
            public void performAction() {
            }
        });
        final UiButton fullScreen = new UiButton(this, minSpace, minSpace + 2 * buttonSize, buttonSize, buttonSize, new UiAnimation(Assets.largerScreenButton), new ActionListener() {
            @Override
            public void performAction() {
                Display.getInstance().fullScreen();
            }
        });
        final UiButton smallScreen = new UiButton(this, minSpace, minSpace + 3 * buttonSize, buttonSize, buttonSize, new UiAnimation(Assets.smallerScreenButton), new ActionListener() {
            @Override
            public void performAction() {
                Display.getInstance().exitFullScreen();
            }
        });
        this.uiManager.addAllUiComponent(startGame, exit, music, sound, fullScreen, smallScreen);
    }

    @Override
    public void changeCursor() {
        final BufferedImage img = Utility.loadImg("resources/ui/cursor/ui_pointer.png");
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension d = toolkit.getBestCursorSize(32, 32);
        final Cursor cursor = toolkit.createCustomCursor(img, new Point(d.width / 2, d.height / 2), "img");
        Display.getInstance().getCanvas().setCursor(cursor);
    }

    @Override
    public void update() {
        if (!this.refreshTimer.isTime()) {
            return;
        }
        this.uiManager.updateAll();
    }

    @Override
    public void render() {
        if (!this.refreshTimer.isTime()) {
            return;
        }
        resetFrame();
        this.refreshTimer.reset();
        this.uiManager.renderAll(getGraphics());
        showFrame();
    }
}
