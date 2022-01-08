// 
// Decompiled by Procyon v0.5.36
// 

package com.tuana9a.state;

import com.tuana9a.ui.ActionListener;
import com.tuana9a.graphic.animation.UiAnimation;
import com.tuana9a.graphic.Assets;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Toolkit;
import com.tuana9a.utility.Utility;
import com.tuana9a.App;
import com.tuana9a.ui.transition.UiButton;
import com.tuana9a.ui.statics.UiImageStatic;
import com.tuana9a.ui.transition.UiProgressBar;
import com.tuana9a.ui.statics.UiNumber;
import com.tuana9a.game.GameCamera;
import com.tuana9a.game.Stage;
import com.tuana9a.utility.TimeSystem;

public class GameState extends AppState
{
    public static final int DEFAULT_FPS = 100;
    private int fps;
    private boolean pauseGame;
    private TimeSystem fpsTimer;
    private Stage stage;
    private GameCamera gameCamera;
    private UiNumber uiPayerHpNumber;
    private UiProgressBar uiBossHpBar;
    private UiImageStatic uiGameOver;
    private UiButton uiPause;
    private UiButton uiResume;
    
    public GameState(final App app) {
        super(app);
        this.fpsTimer = new TimeSystem();
        this.fps = 100;
        this.fpsTimer.deltaTime = 1000 / this.fps;
        this.gameCamera = new GameCamera(this);
        this.stage = new Stage(this);
    }
    
    @Override
    public void changeCursor() {
        final BufferedImage img = Utility.loadImg("resources/ui/cursor/ui_select.png");
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension d = toolkit.getBestCursorSize(32, 32);
        final Cursor cursor = toolkit.createCustomCursor(img, new Point(d.width / 2, d.height / 2), "img");
        this.display.getCanvas().setCursor(cursor);
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
        final int buttonSize = 50;
        final int iconSize = 64;
        final int barSize = 30;
        final int minSpace = 15;
        final UiImageStatic uiPlayerHpIcon = new UiImageStatic(this, minSpace, minSpace, iconSize, iconSize, Assets.playerHpIcon);
        this.uiPayerHpNumber = new UiNumber(this, iconSize + minSpace, minSpace, iconSize, Assets.imagesNumber);
        this.uiBossHpBar = new UiProgressBar(this, quarterW, minSpace + 10, halfW, barSize, Assets.bossHpBarLeft, Assets.bossHpBarMid, Assets.bossHpBarRight);
        this.uiPause = new UiButton(this, screenW - 3 * buttonSize - minSpace, minSpace, buttonSize, buttonSize, new UiAnimation(Assets.pauseButton), new ActionListener() {
            @Override
            public void performAction() {
                GameState.this.pauseGame = true;
                GameState.this.uiPause.hide();
                GameState.this.uiResume.show();
            }
        });
        this.uiResume = new UiButton(this, screenW - 3 * buttonSize - minSpace, minSpace, buttonSize, buttonSize, new UiAnimation(Assets.resumeButton), new ActionListener() {
            @Override
            public void performAction() {
                GameState.this.pauseGame = false;
                GameState.this.uiResume.hide();
                GameState.this.uiPause.show();
            }
        });
        final UiButton uiReplay = new UiButton(this, screenW - 2 * buttonSize - minSpace, minSpace, buttonSize, buttonSize, new UiAnimation(Assets.replayButton), new ActionListener() {
            @Override
            public void performAction() {
                synchronized (GameState.this.app) {
                    GameState.this.replayGame();
                }
            }
        });
        final UiButton uiReturnToMenu = new UiButton(this, screenW - buttonSize - minSpace, minSpace, buttonSize, buttonSize, new UiAnimation(Assets.returnMenuButton), new ActionListener() {
            @Override
            public void performAction() {
                synchronized (GameState.this.app) {
                    GameState.this.app.switchToState(GameState.this.app.menuState);
                }
            }
        });
        this.uiGameOver = new UiImageStatic(this, 0.0, 0.0, this.display.getWidth(), this.display.getHeight(), Assets.gameOverImage);
        this.uiResume.hide();
        this.uiGameOver.hide();
        this.uiManager.addAllUiComponent(uiPlayerHpIcon, this.uiPayerHpNumber, this.uiBossHpBar, this.uiPause, this.uiResume, this.uiGameOver, uiReturnToMenu, uiReplay);
    }
    
    @Override
    public void update() {
        if (!this.refreshTimer.isTime()) {
            return;
        }
        this.refreshTimer.reset();
        if (this.getKeyboardManager().freeCamMode) {
            this.getGameCamera().move();
        }
        this.stage.updateEveryRelCamAll();
        this.uiManager.updateAll();
        if (this.pauseGame) {
            return;
        }
        this.stage.update();
    }
    
    @Override
    public void render() {
        if (!this.fpsTimer.isTime()) {
            return;
        }
        this.fpsTimer.reset();
        this.clearOldFrameAndRenewIt();
        this.stage.render(this.g);
        this.uiManager.renderAll(this.g);
        this.showFrameToScreen();
    }
    
    public void startGame() {
        this.uiResume.onRelease();
        this.stage.newGame();
        this.uiGameOver.hide();
    }
    
    public void replayGame() {
        this.uiResume.onRelease();
        this.stage.replay();
        this.uiGameOver.hide();
    }
    
    public void updateUiPayerHp(final int playerHp) {
        this.uiPayerHpNumber.setNumber(playerHp);
        if (playerHp <= 0) {
            this.uiGameOver.show();
            this.uiResume.hide();
            this.uiPause.hide();
            this.pauseGame = true;
        }
    }
    
    public void updateUiBossHp(final int bossHp) {
        this.uiBossHpBar.updateCurrentProgress(bossHp);
    }
    
    public void isBossStage(final int bossHp) {
        this.uiBossHpBar.initProcess(bossHp, bossHp);
        this.uiBossHpBar.show();
    }
    
    public void notBossStage() {
        this.uiBossHpBar.hide();
    }
    
    public int getFps() {
        return this.fps;
    }
    
    public void setFps(final int fps) {
        this.fps = fps;
        this.fpsTimer.deltaTime = 1000 / fps;
    }
    
    public GameCamera getGameCamera() {
        return this.gameCamera;
    }
    
    public Stage getStage() {
        return this.stage;
    }
}
