package dev.cammiescorner.maar.common.blocks;

import dev.cammiescorner.maar.common.packets.s2c.SpawnPulseParticle;
import dev.cammiescorner.maar.common.registry.ModSounds;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class PulserBlock extends FacingBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;

	public PulserBlock() {
		super(QuiltBlockSettings.copyOf(Blocks.OBSERVER));
		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		Direction direction = state.get(FACING);
		int distance = world.getReceivedRedstonePower(pos) * 2;
		boolean bl = world.isReceivingRedstonePower(pos) && fromPos.offset(direction).equals(pos) && distance > 0;

		if(bl && !state.get(POWERED)) {
			Entity fakeEntity = EntityType.ITEM.create(world);
			Vec3d directionVec = new Vec3d(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
			Vec3d startPos = Vec3d.ofCenter(pos).add(directionVec.multiply(0.5));
			Vec3d endPos = Vec3d.ofCenter(pos).add(directionVec.multiply(distance));
			BlockHitResult hitResult = world.raycast(new RaycastContext(startPos, endPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, fakeEntity));

			if(hitResult != null) {
				BlockState blockState = world.getBlockState(hitResult.getBlockPos());
				Direction face = hitResult.getSide();

				// TODO make chunk component that handles if there's a queued block tick
				if(blockState.isOf(Blocks.OBSERVER) && blockState.get(ObserverBlock.FACING).equals(face)) {
					if(!world.getBlockTickScheduler().isQueued(hitResult.getBlockPos(), blockState.getBlock()))
						world.scheduleBlockTick(hitResult.getBlockPos(), blockState.getBlock(), (int) Math.sqrt(hitResult.getBlockPos().getSquaredDistance(pos)) * 4);
				}
			}

			world.setBlockState(pos, state.with(POWERED, true));
			world.playSound(null, pos, ModSounds.PULSER_FIRE, SoundCategory.BLOCKS, 1F, world.random.nextFloat() * 0.2F + 0.8F);
			world.scheduleBlockTick(pos, this, 20);
			SpawnPulseParticle.sendToAll(world.getServer(), startPos, Vec3d.ofCenter(hitResult.getBlockPos()).subtract(Vec3d.ofCenter(pos)));
			fakeEntity.remove(Entity.RemovalReason.DISCARDED);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		super.scheduledTick(state, world, pos, random);

		if(state.get(POWERED))
			world.setBlockState(pos, state.with(POWERED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite().getOpposite());
	}
}
