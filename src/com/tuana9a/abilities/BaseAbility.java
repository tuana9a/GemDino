// 
// Decompiled by Procyon v0.5.36
// 

package com.tuana9a.abilities;

import com.tuana9a.entities.Animal;

public abstract class BaseAbility {
    public Animal fromAnimal;
    public static final long DEFAULT_SKILL_TIME = 5000L;

    public BaseAbility(final Animal fromAnimal) {
        this.fromAnimal = fromAnimal;
    }

    public abstract void perform();
}
