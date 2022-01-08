// 
// Decompiled by Procyon v0.5.36
// 

package com.tuana9a.game.entity.move.enemy;

import com.tuana9a.state.GameState;
import com.tuana9a.graphic.animation.StateAnimation;
import com.tuana9a.graphic.Assets;

public class NormalEnemy extends Enemy
{
    @Override
    protected void initStateAnimation() {
        super.initStateAnimation();
        this.allStateAnimations[NormalEnemy.NORMAL] = new StateAnimation(Assets.emoteSleep, new double[][] { { (this.width - 32) / 2.0f, -32.0 }, { (this.width - 32) / 2.0f, -32.0 } }, 32.0, 32.0);
        this.allStateAnimations[NormalEnemy.HIT] = new StateAnimation(Assets.emoteFaceSad, new double[][] { { (this.width - 32) / 2.0f, -32.0 }, { (this.width - 32) / 2.0f, -32.0 } }, 32.0, 32.0);
        this.allStateAnimations[NormalEnemy.DEAD] = new StateAnimation(Assets.deadState, new double[][] { { 0.0, 0.0 }, { 0.0, 0.0 } }, this.width, this.height);
    }
    
    public NormalEnemy(final GameState gameState, final int enemyId, final double x, final double y) {
        super(gameState, enemyId, x, y);
    }
}
