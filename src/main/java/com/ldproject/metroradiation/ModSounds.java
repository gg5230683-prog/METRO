package com.ldproject.metroradiation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MetroRadiation.MODID);

    public static final RegistryObject<SoundEvent> GEIGER =
            SOUNDS.register("geiger",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MetroRadiation.MODID, "geiger")));
	public static final RegistryObject<SoundEvent> RADIATION_BREATH =
        SOUNDS.register("radiation_breath",
                () -> SoundEvent.createVariableRangeEvent(
                        new ResourceLocation(MetroRadiation.MODID, "radiation_breath")));
    public static final RegistryObject<SoundEvent> FILTER_WARNING =
            SOUNDS.register("filter_warning",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(MetroRadiation.MODID, "filter_warning")));

}
