package com.ldproject.metroradiation.radiation;

import com.ldproject.metroradiation.config.RadiationConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class RadiationManager {

    public static int getPlayerRadiation(Player player) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();

        ResourceKey<Biome> biomeKey = level.getBiome(pos).unwrapKey().orElse(null);
        if (biomeKey == null) {
            return RadiationConfig.COMMON.defaultRadiation.get();
        }

        // ✅ ПРАВИЛЬНО получаем biome ID
        String biomeId = biomeKey.location().toString();

        // 🔧 Проверяем исключения (джунгли / пустыни)
        for (String entry : RadiationConfig.COMMON.biomeRadiation.get()) {
            String[] parts = entry.split("=");
            if (parts.length != 2) continue;

            String id = parts[0].trim();
            int value;

            try {
                value = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                continue;
            }

            if (id.equalsIgnoreCase(biomeId)) {
                return Math.max(0, value);
            }
        }

        // 🌍 Все остальные биомы
        return RadiationConfig.COMMON.defaultRadiation.get();
    }
}
