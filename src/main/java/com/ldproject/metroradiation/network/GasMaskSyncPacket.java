package com.ldproject.metroradiation.network;

import com.ldproject.metroradiation.client.GasMaskClientCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Пакет синхронизации данных противогаза с сервера на клиент
 */
public class GasMaskSyncPacket {

    private final boolean hasGasMask;
    private final int filterTime;
    private final int durability;

    public GasMaskSyncPacket(boolean hasGasMask, int filterTime, int durability) {
        this.hasGasMask = hasGasMask;
        this.filterTime = filterTime;
        this.durability = durability;
    }

    public static void encode(GasMaskSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.hasGasMask);
        buf.writeInt(msg.filterTime);
        buf.writeInt(msg.durability);
    }

    public static GasMaskSyncPacket decode(FriendlyByteBuf buf) {
        return new GasMaskSyncPacket(
                buf.readBoolean(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(GasMaskSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            boolean hadGasMask = GasMaskClientCache.hasGasMask;
            int previousFilterTime = GasMaskClientCache.filterTime;

            if (hadGasMask != msg.hasGasMask) {
                GasMaskClientCache.startTransition();
            }
            GasMaskClientCache.hasGasMask = msg.hasGasMask;
            GasMaskClientCache.filterTime = msg.filterTime;
            GasMaskClientCache.durability = msg.durability;

            if (msg.hasGasMask) {
                boolean filterInstalled = msg.filterTime > previousFilterTime;
                boolean justEquippedWithFilter = !hadGasMask && msg.filterTime > 0;
                boolean reachedOneMinute = previousFilterTime > 1200 && msg.filterTime <= 1200;
                boolean reachedZero = previousFilterTime > 0 && msg.filterTime <= 0;

                if (filterInstalled || justEquippedWithFilter || reachedOneMinute || reachedZero) {
                    GasMaskClientCache.startFilterDisplay();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
