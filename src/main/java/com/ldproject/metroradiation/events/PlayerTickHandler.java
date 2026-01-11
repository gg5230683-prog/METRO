package com.ldproject.metroradiation.events;

import com.ldproject.metroradiation.effect.ModEffects;
import com.ldproject.metroradiation.gasmask.GasMaskData;
import com.ldproject.metroradiation.gasmask.GasMaskManager;
import com.ldproject.metroradiation.network.GasMaskSyncPacket;
import com.ldproject.metroradiation.network.ModNetwork;
import com.ldproject.metroradiation.network.RadiationSyncPacket;
import com.ldproject.metroradiation.radiation.RadiationManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class PlayerTickHandler {

    private static final int SAFE_TIME = 5;

    // ========== ВЫДАЧА СТАРТОВЫХ ПРЕДМЕТОВ ==========
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getServer().execute(() -> {
                GasMaskManager.giveStarterItems(serverPlayer);
            });
        }
    }

    // ========== ГЛАВНЫЙ ТИК ==========

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        ServerPlayer serverPlayer = (ServerPlayer) player;

        // ========== РАДИАЦИЯ (раз в секунду) ==========
        
        if (player.tickCount % 20 == 0) {
            int radiation = RadiationManager.getPlayerRadiation(player);
            boolean hasGasMask = GasMaskData.hasGasMask(player);
            boolean hasValidFilter = GasMaskData.hasValidFilter(player);

            // Если противогаз надет и фильтр работает → защита от радиации
            boolean protectedFromRadiation = hasGasMask && hasValidFilter;

            if (radiation <= 0 || protectedFromRadiation) {
                // Безопасная зона или защищен
                player.getPersistentData().remove("RadiationTime");
                player.removeEffect(ModEffects.RADIATION_CHOKING.get());

                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new RadiationSyncPacket(0, 0)
                );
            } else {
                // Радиация действует
                int time = player.getPersistentData().getInt("RadiationTime") + 1;
                player.getPersistentData().putInt("RadiationTime", time);

                player.addEffect(new MobEffectInstance(
                        ModEffects.RADIATION_CHOKING.get(),
                        60,
                        0,
                        true,
                        false
                ));

                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new RadiationSyncPacket(radiation, time)
                );

                if (time > SAFE_TIME) {
                    player.hurt(player.damageSources().magic(), 2.0F);
                }
            }
        }

        // ========== ЛОГИКА ПРОТИВОГАЗА (КАЖДЫЙ ТИК!) ==========

        boolean hasGasMask = GasMaskData.hasGasMask(player);
        
        if (hasGasMask) {
            // ✅ ИСПРАВЛЕНИЕ: Уменьшаем время фильтра КАЖДЫЙ ТИК (20 раз в сек)
            boolean hasValidFilter = GasMaskData.hasValidFilter(player);
            
            if (hasValidFilter) {
                GasMaskData.decreaseFilterTime(player);
                
                int filterTime = GasMaskData.getFilterTime(player);
                
                // Предупреждение за 1 минуту (диапазон 1200-1180 тиков)
                if (filterTime <= GasMaskData.WARNING_TIME && 
                    filterTime > GasMaskData.WARNING_TIME - 20 && 
                    !GasMaskData.hasWarningPlayed(player)) {
                    
                    player.level().playSound(null, player.blockPosition(),
                        SoundEvents.NOTE_BLOCK_PLING.get(), SoundSource.PLAYERS, 1.0F, 2.0F);
                    GasMaskData.setWarningPlayed(player, true);
                }
            }

            // Синхронизация данных противогаза с клиентом (раз в секунду)
            if (player.tickCount % 20 == 0) {
                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new GasMaskSyncPacket(
                            true,
                            GasMaskData.getFilterTime(player),
                            GasMaskData.getDurability(player)
                        )
                );
            }
        } else {
            // Противогаз не надет - отправляем пустые данные (раз в секунду)
            if (player.tickCount % 20 == 0) {
                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new GasMaskSyncPacket(false, 0, 0)
                );
            }
        }
    }

    // ========== УРОН ПО ПРОТИВОГАЗУ ==========

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        DamageSource source = event.getSource();
        
        // Урон от радиации НЕ повреждает противогаз
        if (source == player.damageSources().magic()) {
            return;
        }

        // Если противогаз надет - уменьшаем прочность
        if (GasMaskData.hasGasMask(player)) {
            int damageAmount = (int) Math.ceil(event.getAmount());
            GasMaskData.decreaseDurability(player, damageAmount);
            
            // Синхронизация с клиентом
            if (player instanceof ServerPlayer serverPlayer) {
                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new GasMaskSyncPacket(
                            true,
                            GasMaskData.getFilterTime(player),
                            GasMaskData.getDurability(player)
                        )
                );
            }
        }
    }
}
