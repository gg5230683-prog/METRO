package com.ldproject.metroradiation.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class RadiationBreathLoopSound extends AbstractTickableSoundInstance {

    private final LocalPlayer player;

    public RadiationBreathLoopSound(LocalPlayer player, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        
        // ✅ ИСПРАВЛЕНИЕ: Проверка на null в конструкторе
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        this.player = player;
        this.looping = true;
        this.attenuation = Attenuation.NONE;
        this.volume = 0.9f;
        this.pitch = 1.0f;
    }

    @Override
    public void tick() {
        // 💀 смерть — стоп
        if (!player.isAlive()) {
            this.stop();
            return;
        }

        // ✅ ИСПРАВЛЕНИЕ: Обновление позиции звука
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }
}
