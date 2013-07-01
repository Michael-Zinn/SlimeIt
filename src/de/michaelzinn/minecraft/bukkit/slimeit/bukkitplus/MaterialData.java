package de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus;

import static de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.MaterialData.Tag.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

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
public class MaterialData {

	private static HashMap<Material, HashMap<Byte, MaterialData>> definitions = new HashMap<Material, HashMap<Byte, MaterialData>>();

	public final Material material;
	public final byte data;

	// weird, change later
	public Tag orientation = DOWN;
	public BlockFace front;
	public BlockFace back;

	public enum Tag {
		PISTON,
		BASE,
		EXTENSION,
		NONSTICKY,
		STICKY,
		RETRACTED,
		EXTENDED,
		DOWN((byte) 0, BlockFace.DOWN),
		UP((byte) 1, BlockFace.UP),
		NORTH((byte) 2, BlockFace.NORTH),
		SOUTH((byte) 3, BlockFace.SOUTH),
		WEST((byte) 4, BlockFace.WEST),
		EAST((byte) 5, BlockFace.EAST);

		boolean isOrientation = false;
		byte orientation = 0;
		BlockFace front;

		Tag() {
		}

		Tag(byte orientation, BlockFace front) {
			isOrientation = true;
			this.orientation = orientation;
			this.front = front;
		}
	}

	public final Set<Tag> TAGS = new HashSet<Tag>();

	public static MaterialData get(Block block) {
		return get(block.getType(), block.getData());
	}

	public static MaterialData get(Material material, byte data) {
		HashMap<Byte, MaterialData> materialMap = definitions.get(material);
		if (materialMap == null) {
			return new MaterialData(material, data);
		}

		MaterialData materialData = materialMap.get(data);
		if (materialData == null) {
			return new MaterialData(material, data);
		}

		return materialData;
	}

	private MaterialData(Block block) {
		this(block.getType(), block.getData());
	}

	private MaterialData(Material material, byte data) {
		this.material = material;
		this.data = data;

		HashMap<Byte, MaterialData> materialMap = definitions.get(material);
		if (materialMap == null) {
			materialMap = new HashMap<Byte, MaterialData>();
			definitions.put(material, materialMap);
		}

		if (materialMap.get(data) == null) {
			materialMap.put(data, this);
		}
		else {
			throw new RuntimeException("Duplicate MaterialData definition!");
		}
	}

	private MaterialData tag(Tag... tags) {
		for (Tag tag : tags) {
			TAGS.add(tag);
			if (tag.isOrientation) {
				orientation = tag;
				front = tag.front;
				back = tag.front.getOppositeFace();
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

	public void applyTo(Block block) {
		block.setType(material);
		block.setData(data);
		block.getState().update();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MaterialData) {
			MaterialData m = (MaterialData) obj;
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

	// Definitions
	// cobble
	public static final MaterialData COBBLESTONE = new MaterialData(Material.COBBLESTONE, (byte) 0);
	public static final MaterialData MOSSY_COBBLESTONE = new MaterialData(Material.MOSSY_COBBLESTONE, (byte) 0);
	public static final MaterialData COBBLE_WALL = new MaterialData(Material.COBBLE_WALL, (byte) 0);
	public static final MaterialData MOSSY_COBBLE_WALL = new MaterialData(Material.COBBLE_WALL, (byte) 1);

	// stone bricks
	public static final MaterialData SMOOTH_BRICK = new MaterialData(Material.SMOOTH_BRICK, (byte) 0);
	public static final MaterialData MOSSY_SMOOTH_BRICK = new MaterialData(Material.SMOOTH_BRICK, (byte) 1);
	public static final MaterialData CRACKED_SMOOTH_BRICK = new MaterialData(Material.SMOOTH_BRICK, (byte) 2);
	public static final MaterialData CHISELED_SMOOTH_BRICK = new MaterialData(Material.SMOOTH_BRICK, (byte) 3);

	// TODO there should be an elegant way to match the tags with the variable
	// names...
	//@formatter:off
	public static final MaterialData RETRACTED_NONSTICKY_PISTON_BASE_DOWN = new MaterialData(Material.PISTON_BASE, (byte) 0)
			                      .tag(RETRACTED,NONSTICKY,PISTON,BASE,DOWN);
	//@formatter:on
	public static final MaterialData RETRACTED_NONSTICKY_PISTON_BASE_UP = new MaterialData(Material.PISTON_BASE, (byte) 1)
			.tag(RETRACTED, NONSTICKY, PISTON, BASE, UP);
	public static final MaterialData RETRACTED_NONSTICKY_PISTON_BASE_NORTH = new MaterialData(Material.PISTON_BASE, (byte) 2)
			.tag(RETRACTED, NONSTICKY, PISTON, BASE, NORTH);
	public static final MaterialData RETRACTED_NONSTICKY_PISTON_BASE_SOUTH = new MaterialData(Material.PISTON_BASE, (byte) 3)
			.tag(RETRACTED, NONSTICKY, PISTON, BASE, SOUTH);
	public static final MaterialData RETRACTED_NONSTICKY_PISTON_BASE_WEST = new MaterialData(Material.PISTON_BASE, (byte) 4)
			.tag(RETRACTED, NONSTICKY, PISTON, BASE, WEST);
	public static final MaterialData RETRACTED_NONSTICKY_PISTON_BASE_EAST = new MaterialData(Material.PISTON_BASE, (byte) 5)
			.tag(RETRACTED, NONSTICKY, PISTON, BASE, EAST);

	public static final MaterialData EXTENDED_NONSTICKY_PISTON_BASE_DOWN = new MaterialData(Material.PISTON_BASE, (byte) 8)
			.tag(EXTENDED, NONSTICKY, PISTON, BASE, DOWN);
	public static final MaterialData EXTENDED_NONSTICKY_PISTON_BASE_UP = new MaterialData(Material.PISTON_BASE, (byte) 9)
			.tag(EXTENDED, NONSTICKY, PISTON, BASE, UP);
	public static final MaterialData EXTENDED_NONSTICKY_PISTON_BASE_NORTH = new MaterialData(Material.PISTON_BASE, (byte) 10)
			.tag(EXTENDED, NONSTICKY, PISTON, BASE, NORTH);
	public static final MaterialData EXTENDED_NONSTICKY_PISTON_BASE_SOUTH = new MaterialData(Material.PISTON_BASE, (byte) 11)
			.tag(EXTENDED, NONSTICKY, PISTON, BASE, SOUTH);
	public static final MaterialData EXTENDED_NONSTICKY_PISTON_BASE_WEST = new MaterialData(Material.PISTON_BASE, (byte) 12)
			.tag(EXTENDED, NONSTICKY, PISTON, BASE, WEST);
	public static final MaterialData EXTENDED_NONSTICKY_PISTON_BASE_EAST = new MaterialData(Material.PISTON_BASE, (byte) 13)
			.tag(EXTENDED, NONSTICKY, PISTON, BASE, EAST);
	// public static final Set<MaterialData> STICKY_PISTON_BASE;

	// public static final Set<MaterialData> RETRACTED_STICKY_PISTON_BASE;
	public static final MaterialData RETRACTED_STICKY_PISTON_BASE_DOWN = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 0)
			.tag(RETRACTED, STICKY, PISTON, BASE, DOWN);
	public static final MaterialData RETRACTED_STICKY_PISTON_BASE_UP = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 1)
			.tag(RETRACTED, STICKY, PISTON, BASE, UP);
	public static final MaterialData RETRACTED_STICKY_PISTON_BASE_NORTH = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 2)
			.tag(RETRACTED, STICKY, PISTON, BASE, NORTH);
	public static final MaterialData RETRACTED_STICKY_PISTON_BASE_SOUTH = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 3)
			.tag(RETRACTED, STICKY, PISTON, BASE, SOUTH);
	public static final MaterialData RETRACTED_STICKY_PISTON_BASE_WEST = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 4)
			.tag(RETRACTED, STICKY, PISTON, BASE, WEST);
	public static final MaterialData RETRACTED_STICKY_PISTON_BASE_EAST = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 5)
			.tag(RETRACTED, STICKY, PISTON, BASE, EAST);

	// public static final Set<MaterialData> EXTENDED_STICKY_PISTON_BASE;
	public static final MaterialData EXTENDED_STICKY_PISTON_BASE_DOWN = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 8)
			.tag(EXTENDED, STICKY, PISTON, BASE, DOWN);
	public static final MaterialData EXTENDED_STICKY_PISTON_BASE_UP = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 9)
			.tag(EXTENDED, STICKY, PISTON, BASE, UP);
	public static final MaterialData EXTENDED_STICKY_PISTON_BASE_NORTH = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 10)
			.tag(EXTENDED, STICKY, PISTON, BASE, NORTH);
	public static final MaterialData EXTENDED_STICKY_PISTON_BASE_SOUTH = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 11)
			.tag(EXTENDED, STICKY, PISTON, BASE, SOUTH);
	public static final MaterialData EXTENDED_STICKY_PISTON_BASE_WEST = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 12)
			.tag(EXTENDED, STICKY, PISTON, BASE, WEST);
	public static final MaterialData EXTENDED_STICKY_PISTON_BASE_EAST = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 13)
			.tag(EXTENDED, STICKY, PISTON, BASE, EAST);

	// public static final Set<MaterialData> PISTON_EXTENSION;
	// public static final Set<MaterialData> NONSTICKY_PISTON_EXTENSION;
	public static final MaterialData NONSTICKY_PISTON_EXTENSION_DOWN = new MaterialData(Material.PISTON_EXTENSION, (byte) 0)
			.tag(EXTENDED, NONSTICKY, PISTON, EXTENSION, DOWN);
	public static final MaterialData NONSTICKY_PISTON_EXTENSION_UP = new MaterialData(Material.PISTON_EXTENSION, (byte) 1)
			.tag(EXTENDED, NONSTICKY, PISTON, EXTENSION, UP);
	public static final MaterialData NONSTICKY_PISTON_EXTENSION_NORTH = new MaterialData(Material.PISTON_EXTENSION, (byte) 2)
			.tag(EXTENDED, NONSTICKY, PISTON, EXTENSION, NORTH);
	public static final MaterialData NONSTICKY_PISTON_EXTENSION_SOUTH = new MaterialData(Material.PISTON_EXTENSION, (byte) 3)
			.tag(EXTENDED, NONSTICKY, PISTON, EXTENSION, SOUTH);
	public static final MaterialData NONSTICKY_PISTON_EXTENSION_WEST = new MaterialData(Material.PISTON_EXTENSION, (byte) 4)
			.tag(EXTENDED, NONSTICKY, PISTON, EXTENSION, WEST);
	public static final MaterialData NONSTICKY_PISTON_EXTENSION_EAST = new MaterialData(Material.PISTON_EXTENSION, (byte) 5)
			.tag(EXTENDED, NONSTICKY, PISTON, EXTENSION, EAST);

	// public static final Set<MaterialData> STICKY_PISTON_EXTENSION;
	public static final MaterialData STICKY_PISTON_EXTENSION_DOWN = new MaterialData(Material.PISTON_EXTENSION, (byte) 8)
			.tag(EXTENDED, STICKY, PISTON, EXTENSION, DOWN);
	public static final MaterialData STICKY_PISTON_EXTENSION_UP = new MaterialData(Material.PISTON_EXTENSION, (byte) 9)
			.tag(EXTENDED, STICKY, PISTON, EXTENSION, UP);
	public static final MaterialData STICKY_PISTON_EXTENSION_NORTH = new MaterialData(Material.PISTON_EXTENSION, (byte) 10)
			.tag(EXTENDED, STICKY, PISTON, EXTENSION, NORTH);
	public static final MaterialData STICKY_PISTON_EXTENSION_SOUTH = new MaterialData(Material.PISTON_EXTENSION, (byte) 11)
			.tag(EXTENDED, STICKY, PISTON, EXTENSION, SOUTH);
	public static final MaterialData STICKY_PISTON_EXTENSION_WEST = new MaterialData(Material.PISTON_EXTENSION, (byte) 12)
			.tag(EXTENDED, STICKY, PISTON, EXTENSION, WEST);
	public static final MaterialData STICKY_PISTON_EXTENSION_EAST = new MaterialData(Material.PISTON_EXTENSION, (byte) 13)
			.tag(EXTENDED, STICKY, PISTON, EXTENSION, EAST);
}
