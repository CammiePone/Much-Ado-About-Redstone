package dev.cammiescorner.maar.common.registry;

import dev.cammiescorner.maar.MAAR;
import dev.cammiescorner.maar.common.blocks.PulserBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;

public class ModBlocks {
	//-----Block Map-----//
	private static final LinkedHashMap<Block, Identifier> BLOCKS = new LinkedHashMap<>();

	//-----Blocks-----//
	public static final Block PULSER = create("pulser", new PulserBlock());

	//-----Registry-----//
	public static void register() {
		BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
		Registry.register(Registry.ITEM, BLOCKS.get(PULSER), getItem(PULSER));
	}

	private static BlockItem getItem(Block block) {
		return new BlockItem(block, new QuiltItemSettings());
	}

	private static <T extends Block> T create(String name, T block) {
		BLOCKS.put(block, MAAR.id(name));
		return block;
	}
}
