package com.ldproject.metroradiation.gasmask;

import com.ldproject.metroradiation.MetroRadiation;
import com.ldproject.metroradiation.network.GasMaskActionPacket;
import com.ldproject.metroradiation.network.ModNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = MetroRadiation.MODID, value = Dist.CLIENT)
public class GasMaskKeyHandler {

    private static final String CATEGORY = "key.categories." + MetroRadiation.MODID;
    
    public static final KeyMapping GAS_MASK_KEY = new KeyMapping(
            "key." + MetroRadiation.MODID + ".gasmask",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            CATEGORY
    );

    private static int holdTicks = 0;
    private static boolean wasPressed = false;
    private static boolean actionSent = false;
    private static boolean equipSent = false; // ✅ НОВОЕ: Флаг для мгновенного надевания

    private static final int HOLD_DURATION_TICKS = 20; // 1 секунда = 20 тиков

    // ✅ ИСПРАВЛЕНИЕ: Используем TickEvent вместо InputEvent для точного подсчета
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        
        // Работает только в игре (не в меню/инвентаре)
        if (player == null || mc.screen != null) {
            // Сброс при открытии меню
            holdTicks = 0;
            wasPressed = false;
            actionSent = false;
            equipSent = false;
            return;
        }

        if (GAS_MASK_KEY.isDown()) {
            if (!wasPressed) {
                // ✅ МГНОВЕННОЕ НАДЕВАНИЕ: При первом нажатии сразу надеваем противогаз
                wasPressed = true;
                holdTicks = 0;
                actionSent = false;
                
                // Отправляем команду надеть/сменить фильтр СРАЗУ
                ModNetwork.CHANNEL.sendToServer(
                    new GasMaskActionPacket(GasMaskActionPacket.Action.EQUIP)
                );
                equipSent = true;
            } else {
                // Продолжаем держать - считаем время для снятия
                holdTicks++;
                
                // Если держим 1 секунду - снимаем противогаз
                if (holdTicks >= HOLD_DURATION_TICKS && !actionSent) {
                    // СНЯТЬ противогаз (зажато 1 секунду)
                    ModNetwork.CHANNEL.sendToServer(
                        new GasMaskActionPacket(GasMaskActionPacket.Action.REMOVE)
                    );
                    actionSent = true; // Больше не отправляем
                }
            }
        } else {
            // Сброс при отпускании
            wasPressed = false;
            holdTicks = 0;
            actionSent = false;
            equipSent = false;
        }
    }
}
