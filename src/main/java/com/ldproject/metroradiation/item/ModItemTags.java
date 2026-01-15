package com.ldproject.metroradiation.item;

import com.ldproject.metroradiation.MetroRadiation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {

    public static final TagKey<Item> GAS_MASKS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(MetroRadiation.MODID, "gas_masks")
    );

    private ModItemTags() {
    }
}
