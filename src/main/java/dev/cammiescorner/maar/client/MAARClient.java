package dev.cammiescorner.maar.client;

import dev.cammiescorner.maar.MAAR;
import dev.cammiescorner.maar.client.particles.PulseParticle;
import dev.cammiescorner.maar.common.packets.s2c.SpawnPulseParticle;
import dev.cammiescorner.maar.common.registry.ModParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.screen.PlayerScreenHandler;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class MAARClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
			registry.register(MAAR.id("particle/pulse_0"));
			registry.register(MAAR.id("particle/pulse_1"));
			registry.register(MAAR.id("particle/pulse_2"));
		}));

		ParticleFactoryRegistry.getInstance().register(ModParticles.PULSE, PulseParticle.Factory::new);

		ClientPlayNetworking.registerGlobalReceiver(SpawnPulseParticle.ID, SpawnPulseParticle::handle);
	}
}
