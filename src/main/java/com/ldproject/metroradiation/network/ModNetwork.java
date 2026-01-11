package com.ldproject.metroradiation.network;

import com.ldproject.metroradiation.MetroRadiation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MetroRadiation.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int index = 0;

    public static void register() {
        // Радиация
        CHANNEL.registerMessage(
                index++,
                RadiationSyncPacket.class,
                RadiationSyncPacket::encode,
                RadiationSyncPacket::decode,
                RadiationSyncPacket::handle
        );

        // Действия с противогазом
        CHANNEL.registerMessage(
                index++,
                GasMaskActionPacket.class,
                GasMaskActionPacket::encode,
                GasMaskActionPacket::decode,
                GasMaskActionPacket::handle
        );

        // Синхронизация данных противогаза
        CHANNEL.registerMessage(
                index++,
                GasMaskSyncPacket.class,
                GasMaskSyncPacket::encode,
                GasMaskSyncPacket::decode,
                GasMaskSyncPacket::handle
        );
    }
}
