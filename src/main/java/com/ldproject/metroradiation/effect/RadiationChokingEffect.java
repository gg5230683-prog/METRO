package com.ldproject.metroradiation.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class RadiationChokingEffect extends MobEffect {

    public RadiationChokingEffect() {
        super(MobEffectCategory.HARMFUL, 0x5A7A7A); // серо-зелёный
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // сам эффект урон не наносит
    }
}
