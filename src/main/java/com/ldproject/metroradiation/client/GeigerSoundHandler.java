package com.ldproject.metroradiation.client;

import com.ldproject.metroradiation.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GeigerSoundHandler {

    private static int tick = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Звук Гейгера только если:
        // 1. Есть радиация
        // 2. Противогаз не надет ИЛИ фильтр кончился
        boolean shouldPlayGeiger = ClientRadiationCache.radiation > 0 && 
                                   (!GasMaskClientCache.hasGasMask || GasMaskClientCache.filterTime <= 0);

        if (!shouldPlayGeiger) {
            tick = 0;
            return;
        }

        tick++;
        if (tick >= 20) {
            tick = 0;
            
            // Приглушаем звук если противогаз надет
            float volume = GasMaskClientCache.hasGasMask ? 0.3F : 1.0F;
            
            mc.level.playLocalSound(
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ(),
                    ModSounds.GEIGER.get(),
                    SoundSource.MASTER,
                    volume,
                    1.0F,
                    false
            );
        }
    }
}
