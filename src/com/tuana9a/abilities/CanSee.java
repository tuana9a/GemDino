// 
// Decompiled by Procyon v0.5.36
// 

package com.tuana9a.abilities;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.tuana9a.entities.Animal;
import com.tuana9a.entities.Entity;
import com.tuana9a.engine.EntityManager;
import com.tuana9a.entities.weapon.WeaponOut;
import com.tuana9a.entities.weapon.Weapon;
import com.tuana9a.utils.Algebra;

public class CanSee {
    private final Animal owner;
    private final int eyeSize;
    private double maxDistance;
    private double x;
    private double y;

    public CanSee(final Animal owner) {
        this.eyeSize = 20;
        this.maxDistance = 1000.0;
        this.owner = owner;
    }

    public boolean canSee(final Entity otherEntity) {
        this.x = this.owner.x + (this.owner.width - this.eyeSize) / 2.0;
        this.y = this.owner.y + this.owner.actualSize.y;
        final double desX = otherEntity.x + otherEntity.width / 2.0;
        final double desY = otherEntity.y + otherEntity.actualSize.y;
        if (Math.abs(desX - this.x) > this.maxDistance || Math.abs(desY - this.y) > this.maxDistance) {
            return false;
        }
        final double radianToEntity = Algebra.getRotate(this.x, this.y, desX, desY);
        double currentDis = 0.0;
        final int speed = 20;
        final double xMove = speed * Math.cos(radianToEntity);
        final double yMove = speed * Math.sin(radianToEntity);
        final EntityManager entityManager = EntityManager.getInstance();
        final ArrayList<Entity> allEntities = entityManager.getEntities();
        do {
            for (final Entity e : allEntities) {
                if (e != null && e != this.owner && !(e instanceof Weapon)) {
                    if (e instanceof WeaponOut) {
                        continue;
                    }
                    if (this.collideWith(e)) {
                        return e == otherEntity;
                    }
                }
            }
            this.x += xMove;
            this.y += yMove;
            currentDis += speed;
        } while (currentDis <= this.maxDistance);
        return false;
    }

    public void setMaxDistance(final double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double maxDis() {
        return this.maxDistance;
    }

    private boolean collideWith(final Entity e) {
        return e != null && new Rectangle((int) this.x, (int) this.y, this.eyeSize, this.eyeSize).intersects(e.actualSize());
    }
}
