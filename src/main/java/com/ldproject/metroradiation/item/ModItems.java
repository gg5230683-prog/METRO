package com.ldproject.metroradiation.item;

import com.ldproject.metroradiation.MetroRadiation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MetroRadiation.MODID);

    public static final RegistryObject<Item> GAS_MASK =
            ITEMS.register("gas_mask", () -> new GasMaskItem(new Item.Properties()));

    public static final RegistryObject<Item> GAS_FILTER =
            ITEMS.register("gas_filter", () -> new GasFilterItem(new Item.Properties()));
}
