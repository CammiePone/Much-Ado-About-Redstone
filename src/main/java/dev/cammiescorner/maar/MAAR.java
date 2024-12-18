package dev.cammiescorner.maar;

import dev.cammiescorner.maar.common.registry.ModBlocks;
import dev.cammiescorner.maar.common.registry.ModParticles;
import dev.cammiescorner.maar.common.registry.ModSounds;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MAAR implements ModInitializer {
	public static final String MOD_ID = "maar";
	public static final Logger LOGGER = LoggerFactory.getLogger("Much Ado About Redstone");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Hello Quilt world from {}!", mod.metadata().name());

		ModBlocks.register();
		ModParticles.register();
		ModSounds.register();
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
