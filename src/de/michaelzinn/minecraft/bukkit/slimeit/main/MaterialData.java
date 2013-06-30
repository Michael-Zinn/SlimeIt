package de.michaelzinn.minecraft.bukkit.slimeit.main;

import org.bukkit.Material;
import org.bukkit.block.Block;

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
	public final Material material;
	public final byte data;

	private MaterialData(Block block) {
		this(block.getType(), block.getData());
	}

	private MaterialData(Material material, byte data) {
		this.material = material;
		this.data = data;
	}

	public static MaterialData get(Block block) {
		// TODO cache them to not create objects all the time
		return new MaterialData(block);
	}

	public static MaterialData get(Material material, byte data) {
		// TODO cache them to not create objects all the time
		return new MaterialData(material, data);
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

	// useful definitions (incomplete list):
	public static final MaterialData COBBLESTONE = new MaterialData(Material.COBBLESTONE, (byte) 0);
	public static final MaterialData MOSSY_COBBLESTONE = new MaterialData(Material.MOSSY_COBBLESTONE, (byte) 0);
	public static final MaterialData COBBLE_WALL = new MaterialData(Material.COBBLE_WALL, (byte) 0);
	public static final MaterialData MOSSY_COBBLE_WALL = new MaterialData(Material.COBBLE_WALL, (byte) 1);

	public static final MaterialData SMOOTH_BRICK = new MaterialData(Material.SMOOTH_BRICK, (byte) 0);
	public static final MaterialData MOSSY_SMOOTH_BRICK = new MaterialData(Material.SMOOTH_BRICK, (byte) 1);
	public static final MaterialData CRACKED_SMOOTH_BRICK = new MaterialData(Material.SMOOTH_BRICK, (byte) 2);
	public static final MaterialData CHISELED_SMOOTH_BRICK = new MaterialData(Material.SMOOTH_BRICK, (byte) 3);

	public static final MaterialData PISTON_BASE_RETRACTED_DOWN = new MaterialData(Material.PISTON_BASE, (byte) 0);
	public static final MaterialData PISTON_BASE_RETRACTED_UP = new MaterialData(Material.PISTON_BASE, (byte) 1);
	public static final MaterialData PISTON_BASE_RETRACTED_NORTH = new MaterialData(Material.PISTON_BASE, (byte) 2);
	public static final MaterialData PISTON_BASE_RETRACTED_SOUTH = new MaterialData(Material.PISTON_BASE, (byte) 3);
	public static final MaterialData PISTON_BASE_RETRACTED_WEST = new MaterialData(Material.PISTON_BASE, (byte) 4);
	public static final MaterialData PISTON_BASE_RETRACTED_EAST = new MaterialData(Material.PISTON_BASE, (byte) 5);
	public static final MaterialData PISTON_STICKY_BASE_RETRACTED_DOWN = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 0);
	public static final MaterialData PISTON_STICKY_BASE_RETRACTED_UP = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 1);
	public static final MaterialData PISTON_STICKY_BASE_RETRACTED_NORTH = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 2);
	public static final MaterialData PISTON_STICKY_BASE_RETRACTED_SOUTH = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 3);
	public static final MaterialData PISTON_STICKY_BASE_RETRACTED_WEST = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 4);
	public static final MaterialData PISTON_STICKY_BASE_RETRACTED_EAST = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 5);

	public static final MaterialData PISTON_EXTENSION_DOWN = new MaterialData(Material.PISTON_EXTENSION, (byte) 0);
	public static final MaterialData PISTON_EXTENSION_UP = new MaterialData(Material.PISTON_EXTENSION, (byte) 1);
	public static final MaterialData PISTON_EXTENSION_NORTH = new MaterialData(Material.PISTON_EXTENSION, (byte) 2);
	public static final MaterialData PISTON_EXTENSION_SOUTH = new MaterialData(Material.PISTON_EXTENSION, (byte) 3);
	public static final MaterialData PISTON_EXTENSION_WEST = new MaterialData(Material.PISTON_EXTENSION, (byte) 4);
	public static final MaterialData PISTON_EXTENSION_EAST = new MaterialData(Material.PISTON_EXTENSION, (byte) 5);
	public static final MaterialData PISTON_STICKY_EXTENSION_DOWN = new MaterialData(Material.PISTON_EXTENSION, (byte) 8);
	public static final MaterialData PISTON_STICKY_EXTENSION_UP = new MaterialData(Material.PISTON_EXTENSION, (byte) 9);
	public static final MaterialData PISTON_STICKY_EXTENSION_NORTH = new MaterialData(Material.PISTON_EXTENSION, (byte) 10);
	public static final MaterialData PISTON_STICKY_EXTENSION_SOUTH = new MaterialData(Material.PISTON_EXTENSION, (byte) 11);
	public static final MaterialData PISTON_STICKY_EXTENSION_WEST = new MaterialData(Material.PISTON_EXTENSION, (byte) 12);
	public static final MaterialData PISTON_STICKY_EXTENSION_EAST = new MaterialData(Material.PISTON_EXTENSION, (byte) 13);

}
