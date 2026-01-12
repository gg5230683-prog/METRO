package com.ldproject.metroradiation.network;

import com.ldproject.metroradiation.gasmask.GasMaskData;
import com.ldproject.metroradiation.gasmask.GasMaskManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * Пакет для управления противогазом с клиента на сервер
 */
public class GasMaskActionPacket {

    public enum Action {
        EQUIP,          // Надеть противогаз
        REMOVE,         // Снять противогаз
        REPLACE_FILTER  // Сменить фильтр
    }

    private final Action action;

    public GasMaskActionPacket(Action action) {
        this.action = action;
    }

    public static void encode(GasMaskActionPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
    }

    public static GasMaskActionPacket decode(FriendlyByteBuf buf) {
        return new GasMaskActionPacket(buf.readEnum(Action.class));
    }

    public static void handle(GasMaskActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            switch (msg.action) {
                case EQUIP:
                    // Пытаемся надеть противогаз или сменить фильтр
                    if (!GasMaskManager.tryEquipGasMask(player)) {
                        // Если уже надет, пытаемся сменить фильтр
                        GasMaskManager.tryReplaceFilter(player);
                    }
                    break;
                    
                case REMOVE:
                    GasMaskManager.removeGasMask(player);
                    break;
                    
                case REPLACE_FILTER:
                    GasMaskManager.tryReplaceFilter(player);
                    break;
            }

            boolean hasGasMask = GasMaskData.hasGasMask(player);
            GasMaskSyncPacket syncPacket = hasGasMask
                    ? new GasMaskSyncPacket(
                            true,
                            GasMaskData.getFilterTime(player),
                            GasMaskData.getDurability(player)
                    )
                    : new GasMaskSyncPacket(false, 0, 0);
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    syncPacket
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
