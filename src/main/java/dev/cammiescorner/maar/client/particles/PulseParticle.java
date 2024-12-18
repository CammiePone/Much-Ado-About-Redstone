package dev.cammiescorner.maar.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.cammiescorner.maar.common.registry.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.*;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class PulseParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;
	private final Direction direction;
	private final Vec3d startPos;
	private int visualAge = 0;

	protected PulseParticle(ClientWorld clientWorld, double x, double y, double z, double distanceX, double distanceY, double distanceZ, SpriteProvider spriteProvider) {
		super(clientWorld, x, y, z, 0, 0, 0);
		this.spriteProvider = spriteProvider;
		this.startPos = new Vec3d(x, y, z);
		this.direction = Direction.fromVector(distanceX != 0 ? distanceX > 0 ? 1 : -1 : 0, distanceY != 0 ? distanceY > 0 ? 1 : -1 : 0, distanceZ != 0 ? distanceZ > 0 ? 1 : -1 : 0);
		this.velocityX = direction.getOffsetX();
		this.velocityY = direction.getOffsetY();
		this.velocityZ = direction.getOffsetZ();
		this.maxAge = 6;
	}

	@Override
	public Particle move(float speed) {
		velocityX *= speed;
		velocityY *= speed;
		velocityZ *= speed;

		return this;
	}

	@Override
	public void tick() {
		float speed = 0.25F;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;

		setSpriteForAge(spriteProvider);
		visualAge++;

		if((startPos.squaredDistanceTo(x, y, z) >= 900 || new Vec3d(velocityX, velocityY, velocityZ).lengthSquared() < 0.0001)) {
			if(age == 0)
				world.playSound(x, y, z, ModSounds.PULSER_DIE, SoundCategory.BLOCKS, 1F, world.random.nextFloat() * 0.2F + 1.8F, false);
			if(age++ >= maxAge)
				markDead();
		}
		else
			move(velocityX * speed, velocityY * speed, velocityZ * speed);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d camPos = camera.getPos();
		float lerpX = (float) (MathHelper.lerp(tickDelta, prevPosX, x) - camPos.getX());
		float lerpY = (float) (MathHelper.lerp(tickDelta, prevPosY, y) - camPos.getY());
		float lerpZ = (float) (MathHelper.lerp(tickDelta, prevPosZ, z) - camPos.getZ());
		Quaternion quaternion = switch(direction) {
			case UP -> new Quaternion(Vec3f.POSITIVE_X, 90, true);
			case DOWN -> new Quaternion(Vec3f.POSITIVE_X, -90, true);
			case NORTH -> new Quaternion(Vec3f.POSITIVE_Y, 0, true);
			case EAST -> new Quaternion(Vec3f.POSITIVE_Y, -90, true);
			case SOUTH -> new Quaternion(Vec3f.POSITIVE_Y, 180, true);
			case WEST -> new Quaternion(Vec3f.POSITIVE_Y, 90, true);
		};

		for(int j = 0; j < 3; j++) {
			float separation = (0.25F * j) - 0.25F;
			Vec3f[] vec3fs = new Vec3f[]{ new Vec3f(-1F, -1F, 0F), new Vec3f(-1F, 1F, 0F), new Vec3f(1F, 1F, 0F), new Vec3f(1F, -1F, 0F) };
			float size = 0.25F + visualAge * 0.005F;

			for(int i = 0; i < 4; ++i) {
				Vec3f vec3f2 = vec3fs[i];
				vec3f2.scale(size * (j == 0 || j == 2 ? 0.75F : 1));
				vec3f2.add(0, 0, separation);
				vec3f2.rotate(quaternion);
				vec3f2.add(lerpX, lerpY, lerpZ);
			}

			int light = 15728850;

			VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
			VertexConsumer vertex = immediate.getBuffer(RenderLayer.getEntityTranslucent(sprite.getAtlas().getId()));
			vertex.vertex(vec3fs[0].getX(), vec3fs[0].getY(), vec3fs[0].getZ()).color(1F, 1F, 1F, 1F).uv(getMaxU(), getMaxV()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0).next();
			vertex.vertex(vec3fs[1].getX(), vec3fs[1].getY(), vec3fs[1].getZ()).color(1F, 1F, 1F, 1F).uv(getMaxU(), getMinV()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0).next();
			vertex.vertex(vec3fs[2].getX(), vec3fs[2].getY(), vec3fs[2].getZ()).color(1F, 1F, 1F, 1F).uv(getMinU(), getMinV()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0).next();
			vertex.vertex(vec3fs[3].getX(), vec3fs[3].getY(), vec3fs[3].getZ()).color(1F, 1F, 1F, 1F).uv(getMinU(), getMaxV()).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0, 1, 0).next();
			immediate.draw();
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	@ClientOnly
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			PulseParticle pulseParticle = new PulseParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			pulseParticle.setSpriteForAge(spriteProvider);

			return pulseParticle;
		}
	}
}
