package com.ldproject.metroradiation.client;

import com.ldproject.metroradiation.MetroRadiation;
import com.ldproject.metroradiation.gasmask.GasMaskKeyHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetroRadiation.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(GasMaskKeyHandler.GAS_MASK_KEY);
        event.register(GasMaskKeyHandler.VIEW_TIMER_KEY);
    }
}
