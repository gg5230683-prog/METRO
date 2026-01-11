package com.ldproject.metroradiation.client;

import com.ldproject.metroradiation.MetroRadiation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetroRadiation.MODID, value = Dist.CLIENT)
public class GasMaskOverlay {

    // Текстуры (пока заглушки, потом добавишь свои)
    private static final ResourceLocation VIGNETTE = new ResourceLocation("textures/misc/vignette.png");

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!GasMaskClientCache.hasGasMask) {
            return; // Противогаз не надет
        }

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // ========== ЗАТЕМНЕНИЕ КРАЕВ (ВИНЬЕТКА) ==========
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.7F);
        RenderSystem.setShaderTexture(0, VIGNETTE);

        guiGraphics.blit(VIGNETTE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

        // ========== ТРЕЩИНЫ (в зависимости от прочности) ==========
        
        float durabilityPercent = GasMaskClientCache.durability / 1000.0F;
        
        if (durabilityPercent < 0.75F) {
            // Рисуем трещины (пока просто полупрозрачный красный overlay)
            float crackAlpha = 1.0F - durabilityPercent; // Чем меньше прочность, тем больше трещин
            
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            
            bufferbuilder.vertex(0, screenHeight, -90).color(1.0F, 0.0F, 0.0F, crackAlpha * 0.3F).endVertex();
            bufferbuilder.vertex(screenWidth, screenHeight, -90).color(1.0F, 0.0F, 0.0F, crackAlpha * 0.3F).endVertex();
            bufferbuilder.vertex(screenWidth, 0, -90).color(1.0F, 0.0F, 0.0F, crackAlpha * 0.3F).endVertex();
            bufferbuilder.vertex(0, 0, -90).color(1.0F, 0.0F, 0.0F, crackAlpha * 0.3F).endVertex();
            
            tesselator.end();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }

        // ========== HUD ИНФОРМАЦИЯ ==========
        
        // Таймер фильтра
        int filterTime = GasMaskClientCache.filterTime;
        int minutes = filterTime / 1200; // 1200 тиков = 1 минута
        int seconds = (filterTime % 1200) / 20;
        
        String filterText = String.format("Filter: %d:%02d", minutes, seconds);
        
        // Цвет в зависимости от времени
        int color;
        if (filterTime > 1200) {
            color = 0x00FF00; // Зеленый
        } else if (filterTime > 0) {
            color = 0xFFFF00; // Желтый (предупреждение)
        } else {
            color = 0xFF0000; // Красный (нет фильтра)
            filterText = "NO FILTER!";
        }

        guiGraphics.drawString(mc.font, filterText, 10, 10, color);

        // ✅ ИСПРАВЛЕНИЕ: Убрана надпись "Durability"

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
