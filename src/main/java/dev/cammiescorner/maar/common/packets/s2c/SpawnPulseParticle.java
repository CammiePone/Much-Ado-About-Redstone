package dev.cammiescorner.maar.common.packets.s2c;

import dev.cammiescorner.maar.MAAR;
import dev.cammiescorner.maar.common.registry.ModParticles;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class SpawnPulseParticle {
	public static final Identifier ID = MAAR.id("sync_supporter_data");

	public static void sendToAll(MinecraftServer server, Vec3d startPos, Vec3d dir) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeDouble(startPos.getX());
		buf.writeDouble(startPos.getY());
		buf.writeDouble(startPos.getZ());
		buf.writeDouble(dir.getX());
		buf.writeDouble(dir.getY());
		buf.writeDouble(dir.getZ());

		ServerPlayNetworking.send(PlayerLookup.all(server), ID, buf);
	}

	@ClientOnly
	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		double x = buf.readDouble(), y = buf.readDouble(), z = buf.readDouble();
		double dirX = buf.readDouble(), dirY = buf.readDouble(), dirZ = buf.readDouble();

		client.execute(() -> {
			Vec3d startPos = new Vec3d(x, y, z);
			Vec3d dir = new Vec3d(dirX, dirY, dirZ);

			if(client.world != null)
				client.world.addParticle(ModParticles.PULSE, startPos.getX(), startPos.getY(), startPos.getZ(), dir.getX(), dir.getY(), dir.getZ());
		});
	}
}
