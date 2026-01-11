package com.ldproject.metroradiation.client;

import com.ldproject.metroradiation.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RadiationBreathSoundHandler {

    private static RadiationBreathLoopSound loopSound;
    private static SoundEvent currentSound;
    private static boolean isPlaying = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isAlive()) {
            stop(mc);
            return;
        }

        boolean hasRadiation = ClientRadiationCache.radiation > 0;
        boolean hasGasMask = GasMaskClientCache.hasGasMask;

        SoundEvent desiredSound = null;
        if (hasRadiation) {
            if (hasGasMask) {
                desiredSound = ModSounds.GASMASK_BREATH.get();
            } else {
                desiredSound = ModSounds.RADIATION_BREATH.get();
            }
        }

        if (desiredSound != null && (!isPlaying || desiredSound != currentSound)) {
            stop(mc);
            loopSound = new RadiationBreathLoopSound(mc.player, desiredSound);
            mc.getSoundManager().play(loopSound);
            currentSound = desiredSound;
            isPlaying = true;
        }

        if (desiredSound == null && isPlaying) {
            stop(mc);
        }
    }

    private static void stop(Minecraft mc) {
        if (loopSound != null) {
            mc.getSoundManager().stop(loopSound);
            loopSound = null;
        }
        currentSound = null;
        isPlaying = false;
    }
}
