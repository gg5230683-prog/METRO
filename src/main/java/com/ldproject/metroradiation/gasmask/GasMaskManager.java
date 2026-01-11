package com.ldproject.metroradiation.gasmask;

import com.ldproject.metroradiation.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Логика противогаза: надеть, снять, сменить фильтр
 */
public class GasMaskManager {

    public static void removeGasMask(Player player) {
        if (!GasMaskData.hasGasMask(player)) {
            return;
        }

        // ВОЗВРАЩАЕМ предмет в инвентарь с сохраненной прочностью
        int durability = GasMaskData.getDurability(player);
        int damageValue = Math.min(1000, Math.max(0, 1000 - durability));

        ItemStack gasMask = new ItemStack(ModItems.GAS_MASK.get());
        gasMask.setDamageValue(damageValue);

        // Добавляем в инвентарь (если не влезает - выбрасываем)
        if (!player.getInventory().add(gasMask)) {
            player.drop(gasMask, false);
        }

        // ✅ ИСПРАВЛЕНИЕ: Сбрасываем только флаг "надет", НЕ трогаем время фильтра!
        // Время фильтра и прочность СОХРАНЯЮТСЯ для повторного надевания
        GasMaskData.setGasMask(player, false);
        // GasMaskData.setFilterTime(player, 0); <- УДАЛЕНО! Теперь время фильтра сохраняется
        GasMaskData.resetWarning(player);

        // Звук снятия
        player.level().playSound(null, player.blockPosition(),
                SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, 0.8F);
    }

    /**
     * Попытка сменить фильтр
     */
    public static boolean tryReplaceFilter(Player player) {
        if (!GasMaskData.hasGasMask(player)) {
            return false; // Противогаз не надет
        }

        // Ищем фильтр в инвентаре
        ItemStack filter = findItemInInventory(player, ModItems.GAS_FILTER.get().asItem());
        if (filter.isEmpty()) {
            return false; // Нет фильтра
        }

        // Удаляем один фильтр
        filter.shrink(1);

        // Устанавливаем новый фильтр
        GasMaskData.setFilterTime(player, GasMaskData.FILTER_DURATION);
        GasMaskData.resetWarning(player);

        // Звук замены фильтра
        player.level().playSound(null, player.blockPosition(),
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.2F);

        return true;
    }

    /**
     * Поиск предмета в инвентаре
     */
    private static ItemStack findItemInInventory(Player player, net.minecraft.world.item.Item item) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.is(item)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Выдать стартовые предметы (вызывается при первом входе в мир)
     */
    public static void giveStarterItems(ServerPlayer player) {
        if (GasMaskData.hasReceivedStarterItems(player)) {
            return; // Уже получал
        }

        // Выдаем противогаз и 1 фильтр
        player.getInventory().add(new ItemStack(ModItems.GAS_MASK.get()));
        player.getInventory().add(new ItemStack(ModItems.GAS_FILTER.get(), 1));

        GasMaskData.setReceivedStarterItems(player, true);
    }

    public static boolean tryEquipGasMask(ServerPlayer player) {
        return false;
    }
}