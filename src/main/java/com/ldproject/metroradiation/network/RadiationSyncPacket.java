package com.ldproject.metroradiation.network;

import com.ldproject.metroradiation.client.ClientRadiationCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RadiationSyncPacket {

    private final int radiation;
    private final int time;

    public RadiationSyncPacket(int radiation, int time) {
        this.radiation = radiation;
        this.time = time;
    }

    public static void encode(RadiationSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.radiation);
        buf.writeInt(msg.time);
    }

    public static RadiationSyncPacket decode(FriendlyByteBuf buf) {
        return new RadiationSyncPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(RadiationSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientRadiationCache.radiation = msg.radiation;
            ClientRadiationCache.radiationTime = msg.time;
        });
        ctx.get().setPacketHandled(true);
    }
}
