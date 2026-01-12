package com.ldproject.metroradiation.client;

import com.ldproject.metroradiation.MetroRadiation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetroRadiation.MODID, value = Dist.CLIENT)
public class GasMaskOverlay {

    // Текстура виньетки противогаза
    private static final ResourceLocation VIGNETTE = new ResourceLocation(
            MetroRadiation.MODID,
            "textures/gui/gasmask_vignette.png"
    );
    
    // Easing функция для плавных переходов (ease-in-out cubic)
    private static float easeInOutCubic(float t) {
        return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float) Math.pow(-2.0F * t + 2.0F, 3.0F) / 2.0F;
    }
    
    // Более плавная easing функция (ease-in-out sine) - идеально для fade эффектов
    private static float easeInOutSine(float t) {
        return -(float)(Math.cos(Math.PI * t) - 1.0F) / 2.0F;
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        boolean hasGasMask = GasMaskClientCache.hasGasMask;
        boolean transitionActive = GasMaskClientCache.transitionActive;
        boolean transitionRendered = false;

        if (!hasGasMask && !transitionActive) {
            return; // Противогаз не надет
        }

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // ========== КОРОТКОЕ ПЛАВНОЕ ЗАТЕМНЕНИЕ ПРИ СНЯТИИ/НАДЕВАНИИ ==========

        if (transitionActive) {
            long elapsed = Util.getMillis() - GasMaskClientCache.transitionStartMs;
            if (elapsed >= GasMaskClientCache.TRANSITION_DURATION_MS) {
                GasMaskClientCache.transitionActive = false;
            } else {
                float progress = (float) elapsed / GasMaskClientCache.TRANSITION_DURATION_MS;
                float fade = progress <= 0.5F ? (progress * 2.0F) : (1.0F - progress) * 2.0F;

                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                bufferbuilder.vertex(0, screenHeight, -90).color(0.0F, 0.0F, 0.0F, fade).endVertex();
                bufferbuilder.vertex(screenWidth, screenHeight, -90).color(0.0F, 0.0F, 0.0F, fade).endVertex();
                bufferbuilder.vertex(screenWidth, 0, -90).color(0.0F, 0.0F, 0.0F, fade).endVertex();
                bufferbuilder.vertex(0, 0, -90).color(0.0F, 0.0F, 0.0F, fade).endVertex();
                tesselator.end();

                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                transitionRendered = true;
            }
        }

        if (!hasGasMask) {
            if (transitionRendered) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();
            }
            return;
        }

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

        float timerAlpha = 0.0F;
        
        // Приоритет 1: Плавное появление/исчезновение при нажатии Y
        if (GasMaskClientCache.forceShowTimer || GasMaskClientCache.yKeyReleaseStartMs > 0) {
            long currentTime = Util.getMillis();
            
            // Fade in (при нажатии Y) - используем sine для суперплавности
            if (GasMaskClientCache.forceShowTimer && GasMaskClientCache.yKeyPressStartMs > 0) {
                long elapsed = currentTime - GasMaskClientCache.yKeyPressStartMs;
                if (elapsed < GasMaskClientCache.Y_FADE_DURATION_MS) {
                    float progress = (float) elapsed / GasMaskClientCache.Y_FADE_DURATION_MS;
                    timerAlpha = easeInOutSine(progress);
                } else {
                    timerAlpha = 1.0F;
                }
            }
            // Резкое исчезновение при отпускании Y
            else if (!GasMaskClientCache.forceShowTimer && GasMaskClientCache.yKeyReleaseStartMs > 0) {
                timerAlpha = 0.0F;
                GasMaskClientCache.yKeyReleaseStartMs = 0L; // Сбрасываем
            }
        }
        // Приоритет 2: Автоматическое появление/исчезновение после смены фильтра
        else {
            long elapsed = Util.getMillis() - GasMaskClientCache.filterDisplayStartMs;
            if (elapsed >= 0 && elapsed <= GasMaskClientCache.FILTER_DISPLAY_DURATION_MS) {
                float progress = (float) elapsed / GasMaskClientCache.FILTER_DISPLAY_DURATION_MS;
                
                // Плавное появление в первые 15% времени (900мс из 6000мс)
                if (progress < 0.15F) {
                    float fadeProgress = progress / 0.15F;
                    timerAlpha = easeInOutSine(fadeProgress);
                }
                // Удержание на 100% до конца времени отображения
                else {
                    timerAlpha = 1.0F;
                }
            }
        }

        if (timerAlpha > 0.0F) {
            int alphaChannel = Math.min(255, Math.max(0, Math.round(timerAlpha * 255.0F)));
            int colorWithAlpha = (alphaChannel << 24) | (color & 0x00FFFFFF);
            guiGraphics.drawString(mc.font, filterText, 10, 10, colorWithAlpha);
        }

        // ✅ ИСПРАВЛЕНИЕ: Убрана надпись "Durability"

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
