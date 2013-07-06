package de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus;

import static de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.Thing.Tag.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * The type of a block is defined both by its material (e.g. stone brick) AND
 * the data value (e.g. differentiating between normal, mossy and cracked). This
 * class combines these two into one which simplifies things like using material
 * + data as a key in a Map.
 * 
 * Useful to express things like "mossy cobblestone wall"
 * 
 * This is meant to be part of a BukkitPlus library later on (something that
 * simplifies Bukkit), so don't add anything specific about SlimeIt here.
 * 
 * @author Michael Zinn (@RedNifre)
 * 
 */
public class Thing {

	private static HashMap<Material, HashMap<Byte, Thing>> definitions = new HashMap<Material, HashMap<Byte, Thing>>();

	public final Material material;
	public final byte data;

	public enum Tag {
		// Special
		UNDEFINED, // not yet handled correctly
		AIR, // both air blocks and void blocks/blocks above the world

		// shapes
		BLOCK, WALL, STAIRS, SLAB, // wall is also used for glass pane, fence
									// and iron bars
		// directions
		DOWN(BlockFace.DOWN),
		UP(BlockFace.UP),
		NORTH(BlockFace.NORTH),
		SOUTH(BlockFace.SOUTH),
		WEST(BlockFace.WEST),
		EAST(BlockFace.EAST),

		// pistons
		PISTON,
		BASE, EXTENSION,
		NONSTICKY, STICKY,
		RETRACTED, EXTENDED,

		// tools
		SWORD, PICKAXE, AXE, SHOVEL, HOE, SHEARS, FISHINGROD,

		// materials:
		DIRT, WOOD, STONE, IRON, GOLD, DIAMOND, OBSIDIAN,
		PUMPKIN,

		// modifiers
		COBBLE,
		BRICK,
		MOSSY,
		CRACKED,
		CHISELED,
		GLOWING,

		;

		boolean isOrientation = false;
		BlockFace correspondingBlockFace;

		Tag() {
		}

		Tag(BlockFace face) {
			isOrientation = true;
			correspondingBlockFace = face;
		}
	}

	private Set<BlockFace> frontFace = new HashSet<BlockFace>();
	private Set<BlockFace> backFace = new HashSet<BlockFace>();

	public BlockFace front;
	public BlockFace back;

	public final Set<Tag> TAGS = new HashSet<Tag>();

	public static Thing in(ItemStack stack) {
		return in(stack.getType(), (byte) 0); // ???
	}

	public static Thing in(Block block) {
		return in(block.getType(), block.getData());
	}

	public static Thing in(MaterialData materialData) {
		return in(materialData.getItemType(), materialData.getData());
	}

	public static Thing in(Material material, byte data) {
		HashMap<Byte, Thing> materialMap = definitions.get(material);
		if (materialMap == null) {
			materialMap = new HashMap<Byte, Thing>();
			definitions.put(material, materialMap);
		}

		Thing definedThing = materialMap.get(data);

		if (definedThing == null) {
			Thing undefinedThing = new Thing(material, data).tag(UNDEFINED);
			materialMap.put(data, undefinedThing);
			return undefinedThing;
		}

		return definedThing;
	}

	private Thing(Material material, int data) {
		this.material = material;
		this.data = (byte) data;

		HashMap<Byte, Thing> materialMap = definitions.get(material);
		if (materialMap == null) {
			materialMap = new HashMap<Byte, Thing>();
			definitions.put(material, materialMap);
		}

		if (materialMap.get(this.data) == null) {
			materialMap.put(this.data, this);
		}
		else {
			throw new RuntimeException("Duplicate MaterialData definition!");
		}
	}

	private Thing tag(Tag... tags) {
		for (Tag tag : tags) {
			TAGS.add(tag);
			if (tag.isOrientation) {
				front = tag.correspondingBlockFace;
				frontFace.add(front);
				back = tag.correspondingBlockFace.getOppositeFace();
				backFace.add(back);

			}
			if (tag.equals(Tag.BLOCK)) {
				tag(DOWN, UP, NORTH, SOUTH, EAST, WEST);
			}
		}
		return this;
	}

	public boolean isnt(Tag... tags) {
		return !is(tags);
	}

	/**
	 * 
	 * @param tags
	 * @return true if it matches all tags
	 */
	public boolean is(Tag... tags) {
		return TAGS.containsAll(Arrays.asList(tags));
	}

	public boolean isnt(Block block) {
		return !is(block);
	}

	public boolean is(Block block) {
		return material.equals(block.getType()) && data == block.getData();
	}

	public boolean isFrontFace(BlockFace face) {
		return frontFace.contains(face);
	}

	public boolean isBackFace(BlockFace face) {
		return backFace.contains(face);
	}

	public void applyTo(Block block) {
		block.setType(material);
		block.setData(data);
		block.getState().update();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Thing) {
			Thing m = (Thing) obj;
			return (m.material == this.material) && (m.data == this.data);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		// FIXME Questionable, but can be fixed later, since it doesn't get
		// persisted (at least I hope so)
		return (material.getId() << 8) | data;
	}

	// Definitions of concrete things that exist in Minecraft
	// Note that the name equals the tags the object will get

	//@formatter:off

	// Special
	public static final Thing AIR = new Thing(Material.AIR, 0);

	// Stone
	public static final Thing STONE_BLOCK          = new Thing(Material.STONE, 0);
	public static final Thing CHISELED_STONE_BLOCK = new Thing(Material.SMOOTH_BRICK, 3);

	// cobble
	public static final Thing COBBLE_STONE_BLOCK       = new Thing(Material.COBBLESTONE, 0);
	public static final Thing MOSSY_COBBLE_STONE_BLOCK = new Thing(Material.MOSSY_COBBLESTONE, 0);
	public static final Thing COBBLE_STONE_WALL        = new Thing(Material.COBBLE_WALL, 0);
	public static final Thing MOSSY_COBBLE_STONE_WALL  = new Thing(Material.COBBLE_WALL, 1);

	// stone bricks
	public static final Thing STONE_BRICK_BLOCK         = new Thing(Material.SMOOTH_BRICK, 0);
	public static final Thing MOSSY_STONE_BRICK_BLOCK   = new Thing(Material.SMOOTH_BRICK, 1);
	public static final Thing CRACKED_STONE_BRICK_BLOCK = new Thing(Material.SMOOTH_BRICK, 2);

	// pistons
	public static final Thing RETRACTED_NONSTICKY_PISTON_BASE_DOWN  = new Thing(Material.PISTON_BASE, 0);
	public static final Thing RETRACTED_NONSTICKY_PISTON_BASE_UP    = new Thing(Material.PISTON_BASE, 1);
	public static final Thing RETRACTED_NONSTICKY_PISTON_BASE_NORTH = new Thing(Material.PISTON_BASE, 2);
	public static final Thing RETRACTED_NONSTICKY_PISTON_BASE_SOUTH = new Thing(Material.PISTON_BASE, 3);
	public static final Thing RETRACTED_NONSTICKY_PISTON_BASE_WEST  = new Thing(Material.PISTON_BASE, 4);
	public static final Thing RETRACTED_NONSTICKY_PISTON_BASE_EAST  = new Thing(Material.PISTON_BASE, 5);

	public static final Thing EXTENDED_NONSTICKY_PISTON_BASE_DOWN  = new Thing(Material.PISTON_BASE,  8);
	public static final Thing EXTENDED_NONSTICKY_PISTON_BASE_UP    = new Thing(Material.PISTON_BASE,  9);
	public static final Thing EXTENDED_NONSTICKY_PISTON_BASE_NORTH = new Thing(Material.PISTON_BASE, 10);
	public static final Thing EXTENDED_NONSTICKY_PISTON_BASE_SOUTH = new Thing(Material.PISTON_BASE, 11);
	public static final Thing EXTENDED_NONSTICKY_PISTON_BASE_WEST  = new Thing(Material.PISTON_BASE, 12);
	public static final Thing EXTENDED_NONSTICKY_PISTON_BASE_EAST  = new Thing(Material.PISTON_BASE, 13);
	public static final Thing EXTENDED_NONSTICKY_PISTON_EXTENSION_DOWN  = new Thing(Material.PISTON_EXTENSION, 0);
	public static final Thing EXTENDED_NONSTICKY_PISTON_EXTENSION_UP    = new Thing(Material.PISTON_EXTENSION, 1);
	public static final Thing EXTENDED_NONSTICKY_PISTON_EXTENSION_NORTH = new Thing(Material.PISTON_EXTENSION, 2);
	public static final Thing EXTENDED_NONSTICKY_PISTON_EXTENSION_SOUTH = new Thing(Material.PISTON_EXTENSION, 3);
	public static final Thing EXTENDED_NONSTICKY_PISTON_EXTENSION_WEST  = new Thing(Material.PISTON_EXTENSION, 4);
	public static final Thing EXTENDED_NONSTICKY_PISTON_EXTENSION_EAST  = new Thing(Material.PISTON_EXTENSION, 5);

	public static final Thing RETRACTED_STICKY_PISTON_BASE_DOWN  = new Thing(Material.PISTON_STICKY_BASE, 0);
	public static final Thing RETRACTED_STICKY_PISTON_BASE_UP    = new Thing(Material.PISTON_STICKY_BASE, 1);
	public static final Thing RETRACTED_STICKY_PISTON_BASE_NORTH = new Thing(Material.PISTON_STICKY_BASE, 2);
	public static final Thing RETRACTED_STICKY_PISTON_BASE_SOUTH = new Thing(Material.PISTON_STICKY_BASE, 3);
	public static final Thing RETRACTED_STICKY_PISTON_BASE_WEST  = new Thing(Material.PISTON_STICKY_BASE, 4);
	public static final Thing RETRACTED_STICKY_PISTON_BASE_EAST  = new Thing(Material.PISTON_STICKY_BASE, 5);

	public static final Thing EXTENDED_STICKY_PISTON_BASE_DOWN  = new Thing(Material.PISTON_STICKY_BASE,  8);
	public static final Thing EXTENDED_STICKY_PISTON_BASE_UP    = new Thing(Material.PISTON_STICKY_BASE,  9);
	public static final Thing EXTENDED_STICKY_PISTON_BASE_NORTH = new Thing(Material.PISTON_STICKY_BASE, 10);
	public static final Thing EXTENDED_STICKY_PISTON_BASE_SOUTH = new Thing(Material.PISTON_STICKY_BASE, 11);
	public static final Thing EXTENDED_STICKY_PISTON_BASE_WEST  = new Thing(Material.PISTON_STICKY_BASE, 12);
	public static final Thing EXTENDED_STICKY_PISTON_BASE_EAST  = new Thing(Material.PISTON_STICKY_BASE, 13);
	public static final Thing EXTENDED_STICKY_PISTON_EXTENSION_DOWN  = new Thing(Material.PISTON_EXTENSION,  8);
	public static final Thing EXTENDED_STICKY_PISTON_EXTENSION_UP    = new Thing(Material.PISTON_EXTENSION,  9);
	public static final Thing EXTENDED_STICKY_PISTON_EXTENSION_NORTH = new Thing(Material.PISTON_EXTENSION, 10);
	public static final Thing EXTENDED_STICKY_PISTON_EXTENSION_SOUTH = new Thing(Material.PISTON_EXTENSION, 11);
	public static final Thing EXTENDED_STICKY_PISTON_EXTENSION_WEST  = new Thing(Material.PISTON_EXTENSION, 12);
	public static final Thing EXTENDED_STICKY_PISTON_EXTENSION_EAST  = new Thing(Material.PISTON_EXTENSION, 13);

	// == items ==

	// tools
	public static final Thing    WOOD_PICKAXE = new Thing(Material.WOOD_PICKAXE,    0);
	public static final Thing   STONE_PICKAXE = new Thing(Material.STONE_PICKAXE,   0);
	public static final Thing    IRON_PICKAXE = new Thing(Material.IRON_PICKAXE,    0);
	public static final Thing    GOLD_PICKAXE = new Thing(Material.GOLD_PICKAXE,    0);
	public static final Thing DIAMOND_PICKAXE = new Thing(Material.DIAMOND_PICKAXE, 0);

	//@formatter:on

	static { // adds the tags to the definitions, based on their names.
		try {

			for (Field field : Thing.class.getDeclaredFields()) {
				if (field.getType().equals(Thing.class) && Modifier.isStatic(field.getModifiers())) {
					String[] tagStrings = field.getName().split("_");
					Tag[] tags = new Tag[tagStrings.length];
					// wasn't there something like "apply" to make this more
					// elegant somewhere?
					for (int i = 0; i < tagStrings.length; i++) {
						tags[i] = Tag.valueOf(tagStrings[i]);
					}
					Thing thingToTag = ((Thing) (field.get(null)));
					thingToTag.tag(tags);
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
}
