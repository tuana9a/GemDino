// 
// Decompiled by Procyon v0.5.36
// 

package com.tuana9a.environment;

import java.awt.Graphics;

import com.tuana9a.entities.EntityManager;
import com.tuana9a.entities.TeleGate;
import com.tuana9a.configs.ConfigStaticObject;
import com.tuana9a.entities.StaticObject;
import com.tuana9a.configs.ConfigWeapon;
import com.tuana9a.entities.weapon.Spear;
import com.tuana9a.entities.weapon.Sword;
import com.tuana9a.entities.weapon.Shoot;
import com.tuana9a.entities.enemy.NormalEnemy;
import com.tuana9a.entities.enemy.HardEnemy;
import com.tuana9a.entities.enemy.Boss;
import com.tuana9a.configs.ConfigEnemy;
import com.tuana9a.entities.enemy.Enemy;
import com.tuana9a.App;
import com.tuana9a.entities.weapon.Weapon;
import com.tuana9a.screen.LoadScreen;
import com.tuana9a.utils.Loading;
import com.tuana9a.entities.Entity;
import com.tuana9a.entities.player.Player;
import com.tuana9a.screen.GameScreen;

public class Stage {
    private final GameScreen gameScreen;
    private final Map currentMap;
    private Player player;

    public Stage(final GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.currentMap = new Map(gameScreen);
    }

    public void resetEntityManagerAll() {
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.nullEveryThing();
    }

    public void resetEntityManagerExceptPlayer() {
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.nullEveryThingExceptPlayer();
    }

    public void resetPlayer() {
        this.player = null;
    }

    public void newGame() {
        this.newGame(null, null);
    }

    public void replay() {
        EntityManager entityManager = EntityManager.getInstance();
        this.player.dropAllWeapon();
        this.resetEntityManagerAll();
        this.player.updatePosition(this.currentMap.getPixelSpawnX(), this.currentMap.getPixelSpawnY());
        this.player.reborn();
        this.gameScreen.updateUiPayerHp(this.player.getHealth());
        entityManager.addEntity(this.player);
        entityManager.setPlayer(this.player);
        this.initEntitiesWithMap();
        entityManager.updateAll();
    }

    public void newGame(final Player newPlayer, final String mapId) {
        this.resetPlayer();
        this.resetEntityManagerAll();
        this.currentMap.loading = new Loading(10, 0, 100L);
        final App app = App.getInstance();
        LoadScreen loadScreen = LoadScreen.getInstance();
        app.switchToState(loadScreen);
        loadScreen.initLoadState(this.currentMap.loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                EntityManager entityManager = EntityManager.getInstance();
                if (mapId == null || mapId.equals("")) {
                    Stage.this.initMapById("1");
                } else {
                    Stage.this.initMapById(mapId);
                }
                if (newPlayer == null) {
                    final int playerId = Stage.this.currentMap.getPlayerId();
                    if (playerId == -1) {
                        Stage.this.player = new Player(Stage.this.gameScreen, 0, Stage.this.currentMap.getPixelSpawnX(), Stage.this.currentMap.getPixelSpawnY());
                    } else {
                        Stage.this.player = new Player(Stage.this.gameScreen, playerId, Stage.this.currentMap.getPixelSpawnX(), Stage.this.currentMap.getPixelSpawnY());
                    }
                } else {
                    Stage.this.player = newPlayer;
                    Stage.this.player.updatePosition(Stage.this.currentMap.getPixelSpawnX(), Stage.this.currentMap.getPixelSpawnY());
                    for (final Weapon w : Stage.this.player.getHand().getAllWeapons()) {
                        if (w != null) {
                            entityManager.addEntity(w);
                        }
                    }
                }
                entityManager.setPlayer(Stage.this.player);
                entityManager.addEntity(Stage.this.player);
                entityManager.updateAll();
            }
        }).start();
    }

    public void teleportToNewMap(final String mapId) {
        this.resetEntityManagerAll();
        this.player.clearIntersects();
        this.currentMap.loading = new Loading(10, 0, 100L);
        final App app = App.getInstance();
        LoadScreen loadScreen = LoadScreen.getInstance();
        app.switchToState(loadScreen);
        loadScreen.initLoadState(this.currentMap.loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                EntityManager entityManager = EntityManager.getInstance();
                if (mapId == null || mapId.equals("")) {
                    Stage.this.initMapById("1");
                } else {
                    Stage.this.initMapById(mapId);
                }
                Stage.this.player.updatePosition(Stage.this.currentMap.getPixelSpawnX(), Stage.this.currentMap.getPixelSpawnY());
                entityManager.setPlayer(Stage.this.player);
                entityManager.addEntity(Stage.this.player);
                for (final Weapon w : Stage.this.player.getHand().getAllWeapons()) {
                    if (w != null) {
                        entityManager.addEntity(w);
                    }
                }
                entityManager.updateAll();
            }
        }).start();
    }

    public void initMapById(final String mapId) {
        this.currentMap.loadMapById(mapId);
        this.initEntitiesWithMap();
    }

    public void initEntitiesWithMap() {
        this.gameScreen.notBossStage();
        EntityManager entityManager = EntityManager.getInstance();
        final int enemyNumber = this.currentMap.getEnemyNumber();
        final int[][] enemiesInfo = this.currentMap.getEnemiesInfo();
        final Enemy[] enemies = new Enemy[enemyNumber];
        final Weapon[] enemyWeapons = new Weapon[enemyNumber];
        for (int i = 0; i < enemyNumber; ++i) {
            final int[] info = enemiesInfo[i];
            if (Enemy.isBoss(info[0])) {
                this.gameScreen.isBossStage(ConfigEnemy.healths[info[0]]);
                enemies[i] = new Boss(this.gameScreen, info[0], (info[1] + 0.5) * 64.0 - ConfigEnemy.widths[info[0]] / 2, (info[2] + 1) * 64 - ConfigEnemy.heights[info[0]]);
            } else if (Enemy.isHard(info[0])) {
                enemies[i] = new HardEnemy(this.gameScreen, info[0], (info[1] + 0.5) * 64.0 - ConfigEnemy.widths[info[0]] / 2, (info[2] + 1) * 64 - ConfigEnemy.heights[info[0]]);
            } else {
                enemies[i] = new NormalEnemy(this.gameScreen, info[0], (info[1] + 0.5) * 64.0 - ConfigEnemy.widths[info[0]] / 2, (info[2] + 1) * 64 - ConfigEnemy.heights[info[0]]);
            }
        }
        for (int i = 0; i < enemyNumber; ++i) {
            final int randomWeaponId = i % 5;
            if (Weapon.isShootWeapon(randomWeaponId)) {
                enemyWeapons[i] = new Shoot(this.gameScreen, randomWeaponId, enemies[i]);
            } else if (Weapon.isSword(randomWeaponId)) {
                enemyWeapons[i] = new Sword(this.gameScreen, randomWeaponId, enemies[i]);
            } else if (Weapon.isSpear(randomWeaponId)) {
                enemyWeapons[i] = new Spear(this.gameScreen, randomWeaponId, enemies[i]);
            }
        }
        final int weaponNumber = this.currentMap.getWeaponNumber();
        final Weapon[] aloneWeapons = new Weapon[weaponNumber];
        final int[][] aloneWeaponsInfo = this.currentMap.getWeaponsInfo();
        for (int j = 0; j < weaponNumber; ++j) {
            final int[] info2 = aloneWeaponsInfo[j];
            if (Weapon.isShootWeapon(info2[0])) {
                aloneWeapons[j] = new Shoot(this.gameScreen, info2[0], (info2[1] + 0.5) * 64.0 - ConfigWeapon.widths[info2[0]] / 2, (info2[2] + 1) * 64 - ConfigWeapon.heights[info2[0]]);
            } else if (Weapon.isSword(info2[0])) {
                aloneWeapons[j] = new Sword(this.gameScreen, info2[0], (info2[1] + 0.5) * 64.0 - ConfigWeapon.widths[info2[0]] / 2, (info2[2] + 1) * 64 - ConfigWeapon.heights[info2[0]]);
            } else if (Weapon.isSpear(info2[0])) {
                aloneWeapons[j] = new Spear(this.gameScreen, info2[0], (info2[1] + 0.5) * 64.0 - ConfigWeapon.widths[info2[0]] / 2, (info2[2] + 1) * 64 - ConfigWeapon.heights[info2[0]]);
            }
        }
        final StaticObject[] statics = new StaticObject[this.currentMap.getStaticObjectNumber() + 1];
        final int[][] staticsInfo = this.currentMap.getStaticObjectsInfo();
        for (int k = 0; k < staticsInfo.length; ++k) {
            final int[] info3 = staticsInfo[k];
            if (Ground.isTeleAbove(info3[0])) {
                statics[k] = new TeleGate(this.gameScreen, info3[0], (info3[1] + 0.5) * 64.0 - ConfigStaticObject.widths[info3[0]] / 2, (info3[2] + 1) * 64 - ConfigStaticObject.heights[info3[0]], this.currentMap.nextMapId);
            } else {
                statics[k] = new StaticObject(this.gameScreen, info3[0], (info3[1] + 0.5) * 64.0 - ConfigStaticObject.widths[info3[0]] / 2, (info3[2] + 1) * 64 - ConfigStaticObject.heights[info3[0]]);
            }
        }
        entityManager.addAllEntities(new Entity[][]{enemies, enemyWeapons, aloneWeapons, statics});
    }

    public void update() {
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.updateAll();
    }

    public void updateEveryRelCamAll() {
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.updateAllEveryRelCam();
    }

    public void render(final Graphics g) {
        EntityManager entityManager = EntityManager.getInstance();
        this.currentMap.render(g);
        entityManager.renderAll(g);
    }

    public Map getCurrentMap() {
        return this.currentMap;
    }

    public Player getPlayer() {
        return this.player;
    }
}
