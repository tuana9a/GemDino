// 
// Decompiled by Procyon v0.5.36
// 

package com.tuana9a.entities;

import com.tuana9a.entities.weapon.Weapon;
import com.tuana9a.entities.weapon.WeaponOut;
import com.tuana9a.game.Map;
import com.tuana9a.entities.enemy.Enemy;
import com.tuana9a.state.GameState;
import com.tuana9a.animation.StateAnimation;
import com.tuana9a.abilities.Skill;
import com.tuana9a.utils.Timer;

public abstract class Animal extends MovingEntity
{
    public static int STATE_NUMBER;
    public static int NORMAL;
    public static int DEAD;
    public static int HIT;
    protected int health;
    public int[][] holdingHand;
    public int[][] shoulder;
    protected AnimalVision vision;
    protected AnimalHand hand;
    public Timer deadTime;
    public Timer effectTime;
    public Timer[] skillTimers;
    public Skill[] skills;
    
    protected void initSkills() {
        this.skillTimers = new Timer[Animal.STATE_NUMBER];
        this.skills = new Skill[Animal.STATE_NUMBER];
    }
    
    @Override
    protected void initStateAnimation() {
        this.allStateAnimations = new StateAnimation[Animal.STATE_NUMBER];
    }
    
    public Animal(final GameState gameState, final int id, final double x, final double y) {
        super(gameState, id, x, y);
        this.deadTime = new Timer();
        this.effectTime = new Timer();
        this.vision = new AnimalVision(gameState, this);
        this.hand = new AnimalHand(this);
        this.initSkills();
    }
    
    @Override
    public void update() {
        this.updatePositionCam();
        if (this.isDead()) {
            return;
        }
        this.updateInteract();
        this.typicalUpdate();
        this.onNormal();
        if (this.state != Animal.NORMAL && this.effectTime.isTime()) {
            this.state = Animal.NORMAL;
        }
        if (this.xMove != 0.0 || this.yMove != 0.0) {
            this.move();
            this.moveAnimation.update();
        }
    }
    
    protected abstract void typicalUpdate();
    
    @Override
    public void move() {
        if (this.xMove > 0.0) {
            this.moveDirect = 1;
        }
        else if (this.xMove < 0.0) {
            this.moveDirect = 0;
        }
        final double left = this.x + this.actualSize.x;
        final double right = left + this.actualSize.width;
        final double top = this.y + this.actualSize.y;
        final double bottom = top + this.actualSize.height;
        final int columnLeftOrigin = (int)(left / 64.0);
        final int columnRightOrigin = (int)(right / 64.0);
        final int rowTopOrigin = (int)(top / 64.0);
        final int rowBottomOrigin = (int)(bottom / 64.0);
        final Map map = this.gameState.getStage().getCurrentMap();
        final int rowTopCheck = (int)Math.floor((top + this.yMove) / 64.0);
        final int rowBottomCheck = (int)Math.floor((bottom + this.yMove) / 64.0);
        final int columnLeftCheck = (int)Math.floor((left + this.xMove) / 64.0);
        final int columnRightCheck = (int)Math.floor((right + this.xMove) / 64.0);
        final double minSpace = 1.0;
        if (this.xMove != 0.0) {
            if (map.canWalkX(this, rowTopOrigin, rowBottomOrigin, columnLeftCheck, columnRightCheck)) {
                this.x += this.xMove;
            }
            else {
                if (this.xMove > 0.0) {
                    this.x = columnRightCheck * 64 - this.actualSize.x - this.actualSize.width - minSpace;
                }
                else {
                    this.x = (columnLeftCheck + 1) * 64 - this.actualSize.x + minSpace;
                }
                if (this instanceof Enemy) {
                    this.xMove = -this.xMove;
                }
                else {
                    this.xMove = 0.0;
                }
            }
        }
        if (this.yMove != 0.0) {
            if (map.canWalkY(this, rowTopCheck, rowBottomCheck, columnLeftOrigin, columnRightOrigin)) {
                this.y += this.yMove;
            }
            else {
                if (this.yMove > 0.0) {
                    this.y = rowBottomCheck * 64 - this.actualSize.y - this.actualSize.height - minSpace;
                }
                else {
                    this.y = (rowTopCheck + 1) * 64 - this.actualSize.y + minSpace;
                }
                if (this instanceof Enemy) {
                    this.yMove = -this.yMove;
                }
                else {
                    this.yMove = 0.0;
                }
            }
        }
    }
    
    public boolean canSee(final Entity e) {
        return this.vision.canSee(e);
    }
    
    public AnimalVision eye() {
        return this.vision;
    }
    
    public void setEyeRange(final double distance) {
        this.vision.setMaxDistance(distance);
    }
    
    @Override
    public void hitBy(final WeaponOut wo) {
        this.health -= wo.damage;
        this.state = Animal.HIT;
        this.effectTime.reset();
        this.effectTime.deltaTime = 1500L;
        if (this.health <= 0) {
            if (this.skillTimers[Animal.DEAD] != null && this.skillTimers[Animal.DEAD].isTime()) {
                this.onDead();
                this.skillTimers[Animal.DEAD].reset();
            }
            this.state = Animal.DEAD;
            this.health = 0;
            this.updateRotate(1.5707963267948966);
            this.dropAllWeapon();
        }
        if (this.skillTimers[Animal.HIT] != null && this.skillTimers[Animal.HIT].isTime()) {
            this.onHit();
            this.skillTimers[Animal.HIT].reset();
        }
    }
    
    public void clearIntersects() {
        this.intersectEntities.clear();
    }
    
    public void dropWeapon() {
        this.hand.dropWeapon();
    }
    
    public void dropWeapon(final Weapon w) {
        this.hand.dropWeapon(w);
    }
    
    public void stealWeapon(final Weapon w) {
        this.hand.stealWeapon(w);
    }
    
    public void dropAllWeapon() {
        this.hand.dropAllWeapon();
    }
    
    public void takeWeapon(final Weapon weapon) {
        this.hand.takeWeapon(weapon);
    }
    
    public void switchWeapon() {
        this.hand.switchWeapon();
    }
    
    public boolean isDead() {
        return this.state == Animal.DEAD;
    }
    
    public boolean isAlive() {
        return this.state == Animal.NORMAL;
    }
    
    public void onDead() {
    }
    
    public void onNormal() {
    }
    
    public void onHit() {
    }
    
    protected void reborn() {
        this.state = Animal.NORMAL;
        this.moveDirect = 1;
        final double n = 0.0;
        this.radianRotateLeft = n;
        this.radianRotateMain = n;
        this.updateBoundWhenRotate();
    }
    
    public void setHealth(final int health) {
        this.health = health;
    }
    
    public int getHealth() {
        return this.health;
    }
    
    public AnimalHand getHand() {
        return this.hand;
    }
    
    public double rightHandX() {
        return this.x + this.holdingHand[1][0];
    }
    
    public double rightHandY() {
        return this.y + this.holdingHand[1][1];
    }
    
    public double leftHandX() {
        return this.x + this.holdingHand[0][0];
    }
    
    public double leftHandY() {
        return this.y + this.holdingHand[0][1];
    }
    
    public double rightShoulderX() {
        return this.x + this.shoulder[1][0];
    }
    
    public double rightShoulderY() {
        return this.y + this.shoulder[1][1];
    }
    
    public double getLeftShoulderX() {
        return this.x + this.shoulder[0][0];
    }
    
    public double getLeftShoulderY() {
        return this.y + this.shoulder[0][1];
    }
    
    public double getSpeed() {
        return this.speed;
    }
    
    public void setSpeed(final double speed) {
        this.speed = speed;
    }
    
    static {
        Animal.STATE_NUMBER = 3;
        Animal.NORMAL = 0;
        Animal.DEAD = 1;
        Animal.HIT = 2;
    }
}
