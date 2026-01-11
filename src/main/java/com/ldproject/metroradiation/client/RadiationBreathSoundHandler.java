package com.ldproject.metroradiation.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RadiationBreathSoundHandler {

    private static RadiationBreathLoopSound loopSound;
    private static boolean isPlaying = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isAlive()) {
            stop(mc);
            return;
        }

        // Звук дыхания только если:
        // 1. Есть радиация
        // 2. Противогаз не надет ИЛИ фильтр кончился
        boolean shouldPlay = ClientRadiationCache.radiation > 0 && 
                            (!GasMaskClientCache.hasGasMask || GasMaskClientCache.filterTime <= 0);

        if (shouldPlay && !isPlaying) {
            loopSound = new RadiationBreathLoopSound(mc.player);
            mc.getSoundManager().play(loopSound);
            isPlaying = true;
        }

        if (!shouldPlay && isPlaying) {
            stop(mc);
        }
    }

    private static void stop(Minecraft mc) {
        if (loopSound != null) {
            mc.getSoundManager().stop(loopSound);
            loopSound = null;
        }
        isPlaying = false;
    }
}
