package dev.cammiescorner.maar.common.registry;

import dev.cammiescorner.maar.MAAR;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public class ModParticles {
	public static final DefaultParticleType PULSE = FabricParticleTypes.simple();

	public static void register() {
		Registry.register(Registry.PARTICLE_TYPE, MAAR.id("pulse"), PULSE);
	}
}
