package com.ldproject.metroradiation.gasmask;

import com.ldproject.metroradiation.ModSounds;
import com.ldproject.metroradiation.item.ModItemTags;
import com.ldproject.metroradiation.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        int maxDurability = GasMaskData.getMaxDurability(player);
        String gasMaskItemId = GasMaskData.getGasMaskItemId(player);
        Item gasMaskItem = resolveGasMaskItem(gasMaskItemId);
        ItemStack gasMask = new ItemStack(gasMaskItem);
        if (maxDurability <= 0) {
            maxDurability = gasMask.getMaxDamage();
        }
        int damageValue = 0;
        if (maxDurability > 0) {
            damageValue = Math.min(maxDurability, Math.max(0, maxDurability - durability));
        }
        gasMask.setDamageValue(damageValue);

        // Добавляем в инвентарь (если не влезает - выбрасываем)
        if (!player.getInventory().add(gasMask)) {
            player.drop(gasMask, false);
        }

        // ✅ ИСПРАВЛЕНИЕ: Сбрасываем только флаг "надет", НЕ трогаем время фильтра!
        // Время фильтра и прочность СОХРАНЯЮТСЯ для повторного надевания
        GasMaskData.setGasMask(player, false);
        GasMaskData.setGasMaskItemId(player, "");
        GasMaskData.setMaxDurability(player, 0);
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

        if (GasMaskData.hasValidFilter(player)) {
            return false; // Фильтр уже установлен
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
                ModSounds.FILTER_SCREW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

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
        if (GasMaskData.hasGasMask(player)) {
            return false;
        }

        ItemStack gasMaskStack = findItemInInventory(player, ModItems.GAS_MASK.get().asItem());
        if (gasMaskStack.isEmpty()) {
            gasMaskStack = findGasMaskInInventory(player);
        }
        if (gasMaskStack.isEmpty()) {
            return false;
        }

        int maxDurability = gasMaskStack.getMaxDamage();
        int damage = gasMaskStack.getDamageValue();
        int durability = Math.max(0, maxDurability - damage);
        ResourceLocation gasMaskId = BuiltInRegistries.ITEM.getKey(gasMaskStack.getItem());

        gasMaskStack.shrink(1);

        GasMaskData.setGasMask(player, true);
        GasMaskData.setDurability(player, durability);
        GasMaskData.setMaxDurability(player, maxDurability);
        GasMaskData.setGasMaskItemId(player, gasMaskId.toString());
        GasMaskData.resetWarning(player);

        player.level().playSound(null, player.blockPosition(),
                SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, 1.0F);

        return true;
    }

    private static ItemStack findGasMaskInInventory(Player player) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && stack.is(ModItemTags.GAS_MASKS)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static Item resolveGasMaskItem(String gasMaskItemId) {
        if (gasMaskItemId != null && !gasMaskItemId.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(gasMaskItemId);
            if (id != null) {
                Item item = BuiltInRegistries.ITEM.get(id);
                if (item != Items.AIR) {
                    return item;
                }
            }
        }
        return ModItems.GAS_MASK.get();
    }
}
