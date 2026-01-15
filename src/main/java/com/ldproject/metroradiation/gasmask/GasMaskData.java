package com.ldproject.metroradiation.gasmask;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Управление данными противогаза в NBT игрока
 */
public class GasMaskData {

    private static final String HAS_GASMASK = "HasGasMask";
    private static final String FILTER_TIME = "FilterTime";
    private static final String GASMASK_DURABILITY = "GasMaskDurability";
    private static final String GASMASK_MAX_DURABILITY = "GasMaskMaxDurability";
    private static final String GASMASK_ITEM_ID = "GasMaskItemId";
    private static final String RECEIVED_STARTER_ITEMS = "ReceivedStarterItems";
    private static final String WARNING_PLAYED = "FilterWarningPlayed";

    public static final int FILTER_DURATION = 6000; // 5 минут
    public static final int WARNING_TIME = 1200; // 1 минута до конца

    // ========== ГЕТТЕРЫ ==========

    public static boolean hasGasMask(Player player) {
        return player.getPersistentData().getBoolean(HAS_GASMASK);
    }

    public static int getFilterTime(Player player) {
        return player.getPersistentData().getInt(FILTER_TIME);
    }

    public static int getDurability(Player player) {
        return player.getPersistentData().getInt(GASMASK_DURABILITY);
    }

    public static int getMaxDurability(Player player) {
        return player.getPersistentData().getInt(GASMASK_MAX_DURABILITY);
    }

    public static String getGasMaskItemId(Player player) {
        return player.getPersistentData().getString(GASMASK_ITEM_ID);
    }

    public static boolean hasReceivedStarterItems(Player player) {
        return player.getPersistentData().getBoolean(RECEIVED_STARTER_ITEMS);
    }

    public static boolean hasWarningPlayed(Player player) {
        return player.getPersistentData().getBoolean(WARNING_PLAYED);
    }

    // ========== СЕТТЕРЫ ==========

    public static void setGasMask(Player player, boolean value) {
        player.getPersistentData().putBoolean(HAS_GASMASK, value);
    }

    public static void setFilterTime(Player player, int time) {
        player.getPersistentData().putInt(FILTER_TIME, time);
    }

    public static void setDurability(Player player, int durability) {
        player.getPersistentData().putInt(GASMASK_DURABILITY, durability);
    }

    public static void setMaxDurability(Player player, int maxDurability) {
        player.getPersistentData().putInt(GASMASK_MAX_DURABILITY, maxDurability);
    }

    public static void setGasMaskItemId(Player player, String itemId) {
        player.getPersistentData().putString(GASMASK_ITEM_ID, itemId);
    }

    public static void setReceivedStarterItems(Player player, boolean value) {
        player.getPersistentData().putBoolean(RECEIVED_STARTER_ITEMS, value);
    }

    public static void setWarningPlayed(Player player, boolean value) {
        player.getPersistentData().putBoolean(WARNING_PLAYED, value);
    }

    // ========== УТИЛИТЫ ==========

    public static void decreaseFilterTime(Player player) {
        int time = getFilterTime(player);
        if (time > 0) {
            setFilterTime(player, time - 1);
        }
    }

    public static void decreaseDurability(Player player, int amount) {
        int durability = getDurability(player);
        setDurability(player, Math.max(0, durability - amount));
    }

    public static boolean hasValidFilter(Player player) {
        return getFilterTime(player) > 0;
    }

    public static void resetWarning(Player player) {
        setWarningPlayed(player, false);
    }
}
