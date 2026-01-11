package com.ldproject.metroradiation.item;

import net.minecraft.world.item.Item;

public class GasFilterItem extends Item {

    public GasFilterItem(Properties properties) {
        super(properties.stacksTo(64)); // Стакается до 64
    }
}
