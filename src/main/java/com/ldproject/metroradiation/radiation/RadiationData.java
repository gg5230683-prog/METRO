package com.ldproject.metroradiation.radiation;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;

public class RadiationData {

    public static final EntityDataAccessor<Integer> RADIATION =
            SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    /** Сервер пишет */
    public static void set(Player player, int value) {
        // ✅ ИСПРАВЛЕНИЕ: Добавлена проверка перед записью
        SynchedEntityData data = player.getEntityData();
        if (!data.hasItem(RADIATION)) {
            data.define(RADIATION, 0);
        }
        data.set(RADIATION, value);
    }

    /** Клиент читает */
    public static int get(Player player) {
        // ✅ ИСПРАВЛЕНИЕ: Безопасное чтение с проверкой
        SynchedEntityData data = player.getEntityData();
        if (!data.hasItem(RADIATION)) {
            return 0;
        }
        return data.get(RADIATION);
    }
}
