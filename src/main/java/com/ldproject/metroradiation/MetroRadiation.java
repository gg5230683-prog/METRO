package com.ldproject.metroradiation;

import com.ldproject.metroradiation.config.RadiationConfig;
import com.ldproject.metroradiation.effect.ModEffects;
import com.ldproject.metroradiation.events.PlayerTickHandler;
import com.ldproject.metroradiation.item.ModItems;
import com.ldproject.metroradiation.network.ModNetwork;
import com.ldproject.metroradiation.radiation.RadiationData;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MetroRadiation.MODID)
public class MetroRadiation {

    public static final String MODID = "metro_radiation";

    public MetroRadiation() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 🔊 Звуки
        ModSounds.SOUNDS.register(modEventBus);

        // 🎭 Эффекты
        ModEffects.EFFECTS.register(modEventBus);

        // 🎒 Предметы
        ModItems.ITEMS.register(modEventBus);

        // 🌐 Сеть
        ModNetwork.register();

        // ⚙️ Конфиг
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                RadiationConfig.COMMON_SPEC
        );

        // 🎮 События
        MinecraftForge.EVENT_BUS.register(new PlayerTickHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    // ✅ ИСПРАВЛЕНИЕ: Регистрация EntityDataAccessor при входе игрока в мир
    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Инициализируем data accessor для нового игрока
            if (!player.getEntityData().hasItem(RadiationData.RADIATION)) {
                player.getEntityData().define(RadiationData.RADIATION, 0);
            }
        }
    }
}
