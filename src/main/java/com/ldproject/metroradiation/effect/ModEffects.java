package com.ldproject.metroradiation.effect;

import com.ldproject.metroradiation.MetroRadiation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MetroRadiation.MODID);

    public static final RegistryObject<MobEffect> RADIATION_CHOKING =
            EFFECTS.register("radiation_choking", RadiationChokingEffect::new);
}
