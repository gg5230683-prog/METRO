package com.ldproject.metroradiation.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GasMaskItem extends Item {

    public GasMaskItem(Properties properties) {
        super(properties.durability(1000)); // 1000 прочности
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false; // Нельзя зачаровать
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return true; // Можно чинить на наковальне
    }
}
