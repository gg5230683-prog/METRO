package com.ldproject.metroradiation.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class RadiationConfig {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {

        // 🌍 дефолтная радиация для ВСЕХ биомов
        public final ForgeConfigSpec.IntValue defaultRadiation;

        // 🔧 исключения по биомам
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeRadiation;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Radiation");

            defaultRadiation = builder
                    .comment("Default radiation level for all biomes")
                    .defineInRange("default_radiation", 4, 0, 100);

            biomeRadiation = builder
                    .comment(
                            "Radiation overrides per biome",
                            "Format: biome_id=level",
                            "Example:",
                            "minecraft:desert=0",
                            "minecraft:jungle=0"
                    )
                    .defineList(
                            "biome_radiation",
                            List.of(
                                    "minecraft:desert=0",
                                    "minecraft:desert_hills=0",
                                    "minecraft:desert_lakes=0",
                                    "minecraft:jungle=0",
                                    "minecraft:sparse_jungle=0",
                                    "minecraft:bamboo_jungle=0"
                            ),
                            o -> o instanceof String && ((String) o).contains("=")
                    );

            builder.pop();
        }
    }
}
