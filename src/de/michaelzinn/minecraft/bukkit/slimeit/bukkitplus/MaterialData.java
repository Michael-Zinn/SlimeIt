package de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus;

import java.util.HashSet;
import java.util.Set;

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

	public static MaterialData get(Block block) {
		// TODO cache them to not create objects all the time
		return new MaterialData(block);
	}

	public static MaterialData get(Material material, byte data) {
		// TODO cache them to not create objects all the time
		return new MaterialData(material, data);
	}

	private MaterialData(Block block) {
		this(block.getType(), block.getData());
	}

	private MaterialData(Material material, byte data) {
		this.material = material;
		this.data = data;
	}

	public boolean matches(Block block) {
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

	// pistons
	//@formatter:off
	// heh, this looks like a config file that wants to break free :)
	// TODO should be TAGs
	public static final Set<MaterialData> PISTON;
	public static final Set<MaterialData> 	PISTON_BASE;
	public static final Set<MaterialData> 		NONSTICKY_PISTON_BASE;
	public static final Set<MaterialData> 			RETRACTED_NONSTICKY_PISTON_BASE;
	public static final     MaterialData 					RETRACTED_NONSTICKY_PISTON_BASE_DOWN  = new MaterialData(Material.PISTON_BASE, (byte)  0);
	public static final     MaterialData 					RETRACTED_NONSTICKY_PISTON_BASE_UP    = new MaterialData(Material.PISTON_BASE, (byte)  1);
	public static final     MaterialData 					RETRACTED_NONSTICKY_PISTON_BASE_NORTH = new MaterialData(Material.PISTON_BASE, (byte)  2);
	public static final     MaterialData 					RETRACTED_NONSTICKY_PISTON_BASE_SOUTH = new MaterialData(Material.PISTON_BASE, (byte)  3);
	public static final     MaterialData 					RETRACTED_NONSTICKY_PISTON_BASE_WEST  = new MaterialData(Material.PISTON_BASE, (byte)  4);
	public static final     MaterialData 					RETRACTED_NONSTICKY_PISTON_BASE_EAST  = new MaterialData(Material.PISTON_BASE, (byte)  5);
	public static final Set<MaterialData> 			EXTENDED_NONSTICKY_PISTON_BASE; 
	public static final     MaterialData 					EXTENDED_NONSTICKY_PISTON_BASE_DOWN   = new MaterialData(Material.PISTON_BASE, (byte)  8);
	public static final     MaterialData 					EXTENDED_NONSTICKY_PISTON_BASE_UP     = new MaterialData(Material.PISTON_BASE, (byte)  9);
	public static final     MaterialData 					EXTENDED_NONSTICKY_PISTON_BASE_NORTH  = new MaterialData(Material.PISTON_BASE, (byte) 10);
	public static final     MaterialData 					EXTENDED_NONSTICKY_PISTON_BASE_SOUTH  = new MaterialData(Material.PISTON_BASE, (byte) 11);
	public static final     MaterialData 					EXTENDED_NONSTICKY_PISTON_BASE_WEST   = new MaterialData(Material.PISTON_BASE, (byte) 12);
	public static final     MaterialData 					EXTENDED_NONSTICKY_PISTON_BASE_EAST   = new MaterialData(Material.PISTON_BASE, (byte) 13);
	public static final Set<MaterialData> 		STICKY_PISTON_BASE;
	public static final Set<MaterialData> 			RETRACTED_STICKY_PISTON_BASE;
	public static final     MaterialData 					RETRACTED_STICKY_PISTON_BASE_DOWN     = new MaterialData(Material.PISTON_STICKY_BASE, (byte)  0);
	public static final     MaterialData 					RETRACTED_STICKY_PISTON_BASE_UP       = new MaterialData(Material.PISTON_STICKY_BASE, (byte)  1);
	public static final     MaterialData 					RETRACTED_STICKY_PISTON_BASE_NORTH    = new MaterialData(Material.PISTON_STICKY_BASE, (byte)  2);
	public static final     MaterialData 					RETRACTED_STICKY_PISTON_BASE_SOUTH    = new MaterialData(Material.PISTON_STICKY_BASE, (byte)  3);
	public static final     MaterialData 					RETRACTED_STICKY_PISTON_BASE_WEST     = new MaterialData(Material.PISTON_STICKY_BASE, (byte)  4);
	public static final     MaterialData 					RETRACTED_STICKY_PISTON_BASE_EAST     = new MaterialData(Material.PISTON_STICKY_BASE, (byte)  5);
	public static final Set<MaterialData> 			EXTENDED_STICKY_PISTON_BASE;
	public static final     MaterialData 					EXTENDED_STICKY_PISTON_BASE_DOWN      = new MaterialData(Material.PISTON_STICKY_BASE, (byte)  8);
	public static final     MaterialData 					EXTENDED_STICKY_PISTON_BASE_UP        = new MaterialData(Material.PISTON_STICKY_BASE, (byte)  9);
	public static final     MaterialData 					EXTENDED_STICKY_PISTON_BASE_NORTH     = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 10);
	public static final     MaterialData 					EXTENDED_STICKY_PISTON_BASE_SOUTH     = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 11);
	public static final     MaterialData 					EXTENDED_STICKY_PISTON_BASE_WEST      = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 12);
	public static final     MaterialData 					EXTENDED_STICKY_PISTON_BASE_EAST      = new MaterialData(Material.PISTON_STICKY_BASE, (byte) 13);
	public static final Set<MaterialData> 	PISTON_EXTENSION;
	public static final Set<MaterialData> 		NONSTICKY_PISTON_EXTENSION;
	public static final     MaterialData 				NONSTICKY_PISTON_EXTENSION_DOWN  = new MaterialData(Material.PISTON_EXTENSION, (byte)  0);
	public static final     MaterialData 				NONSTICKY_PISTON_EXTENSION_UP    = new MaterialData(Material.PISTON_EXTENSION, (byte)  1);
	public static final     MaterialData 				NONSTICKY_PISTON_EXTENSION_NORTH = new MaterialData(Material.PISTON_EXTENSION, (byte)  2);
	public static final     MaterialData 				NONSTICKY_PISTON_EXTENSION_SOUTH = new MaterialData(Material.PISTON_EXTENSION, (byte)  3);
	public static final     MaterialData 				NONSTICKY_PISTON_EXTENSION_WEST  = new MaterialData(Material.PISTON_EXTENSION, (byte)  4);
	public static final     MaterialData 				NONSTICKY_PISTON_EXTENSION_EAST  = new MaterialData(Material.PISTON_EXTENSION, (byte)  5);
	public static final Set<MaterialData> 		STICKY_PISTON_EXTENSION;
	public static final     MaterialData 				STICKY_PISTON_EXTENSION_DOWN     = new MaterialData(Material.PISTON_EXTENSION, (byte)  8);
	public static final     MaterialData 				STICKY_PISTON_EXTENSION_UP       = new MaterialData(Material.PISTON_EXTENSION, (byte)  9);
	public static final     MaterialData 				STICKY_PISTON_EXTENSION_NORTH    = new MaterialData(Material.PISTON_EXTENSION, (byte) 10);
	public static final     MaterialData 				STICKY_PISTON_EXTENSION_SOUTH    = new MaterialData(Material.PISTON_EXTENSION, (byte) 11);
	public static final     MaterialData 				STICKY_PISTON_EXTENSION_WEST     = new MaterialData(Material.PISTON_EXTENSION, (byte) 12);
	public static final     MaterialData 				STICKY_PISTON_EXTENSION_EAST     = new MaterialData(Material.PISTON_EXTENSION, (byte) 13);
	
	// breaking the tree structure... it's really time to turn this into tags...
	public static final Set<MaterialData> RETRACTED_PISTON_BASE;
	public static final Set<MaterialData> EXTENDED_PISTON_BASE;
	public static final Set<MaterialData> NONSTICKY_PISTON;
	public static final Set<MaterialData> STICKY_PISTON;
	
	// gah, so much pain!
	public static final Set<MaterialData> RETRACTED_PISTON;
	public static final Set<MaterialData> EXTENDED_PISTON;
	
	//@formatter:on

	static {
		// filling the sets bottom up, depth first.
		NONSTICKY_PISTON_EXTENSION = new HashSet<MaterialData>();
		NONSTICKY_PISTON_EXTENSION.add(NONSTICKY_PISTON_EXTENSION_DOWN);
		NONSTICKY_PISTON_EXTENSION.add(NONSTICKY_PISTON_EXTENSION_UP);
		NONSTICKY_PISTON_EXTENSION.add(NONSTICKY_PISTON_EXTENSION_NORTH);
		NONSTICKY_PISTON_EXTENSION.add(NONSTICKY_PISTON_EXTENSION_SOUTH);
		NONSTICKY_PISTON_EXTENSION.add(NONSTICKY_PISTON_EXTENSION_WEST);
		NONSTICKY_PISTON_EXTENSION.add(NONSTICKY_PISTON_EXTENSION_EAST);

		STICKY_PISTON_EXTENSION = new HashSet<MaterialData>();
		STICKY_PISTON_EXTENSION.add(STICKY_PISTON_EXTENSION_DOWN);
		STICKY_PISTON_EXTENSION.add(STICKY_PISTON_EXTENSION_UP);
		STICKY_PISTON_EXTENSION.add(STICKY_PISTON_EXTENSION_NORTH);
		STICKY_PISTON_EXTENSION.add(STICKY_PISTON_EXTENSION_SOUTH);
		STICKY_PISTON_EXTENSION.add(STICKY_PISTON_EXTENSION_WEST);
		STICKY_PISTON_EXTENSION.add(STICKY_PISTON_EXTENSION_EAST);

		PISTON_EXTENSION = new HashSet<MaterialData>();
		PISTON_EXTENSION.addAll(NONSTICKY_PISTON_EXTENSION);
		PISTON_EXTENSION.addAll(STICKY_PISTON_EXTENSION);

		RETRACTED_NONSTICKY_PISTON_BASE = new HashSet<MaterialData>();
		RETRACTED_NONSTICKY_PISTON_BASE.add(RETRACTED_NONSTICKY_PISTON_BASE_DOWN);
		RETRACTED_NONSTICKY_PISTON_BASE.add(RETRACTED_NONSTICKY_PISTON_BASE_UP);
		RETRACTED_NONSTICKY_PISTON_BASE.add(RETRACTED_NONSTICKY_PISTON_BASE_NORTH);
		RETRACTED_NONSTICKY_PISTON_BASE.add(RETRACTED_NONSTICKY_PISTON_BASE_SOUTH);
		RETRACTED_NONSTICKY_PISTON_BASE.add(RETRACTED_NONSTICKY_PISTON_BASE_WEST);
		RETRACTED_NONSTICKY_PISTON_BASE.add(RETRACTED_NONSTICKY_PISTON_BASE_EAST);

		EXTENDED_NONSTICKY_PISTON_BASE = new HashSet<MaterialData>();
		EXTENDED_NONSTICKY_PISTON_BASE.add(EXTENDED_NONSTICKY_PISTON_BASE_DOWN);
		EXTENDED_NONSTICKY_PISTON_BASE.add(EXTENDED_NONSTICKY_PISTON_BASE_UP);
		EXTENDED_NONSTICKY_PISTON_BASE.add(EXTENDED_NONSTICKY_PISTON_BASE_NORTH);
		EXTENDED_NONSTICKY_PISTON_BASE.add(EXTENDED_NONSTICKY_PISTON_BASE_SOUTH);
		EXTENDED_NONSTICKY_PISTON_BASE.add(EXTENDED_NONSTICKY_PISTON_BASE_WEST);
		EXTENDED_NONSTICKY_PISTON_BASE.add(EXTENDED_NONSTICKY_PISTON_BASE_EAST);

		NONSTICKY_PISTON_BASE = new HashSet<MaterialData>();
		NONSTICKY_PISTON_BASE.addAll(RETRACTED_NONSTICKY_PISTON_BASE);
		NONSTICKY_PISTON_BASE.addAll(EXTENDED_NONSTICKY_PISTON_BASE);

		RETRACTED_STICKY_PISTON_BASE = new HashSet<MaterialData>();
		RETRACTED_STICKY_PISTON_BASE.add(RETRACTED_STICKY_PISTON_BASE_DOWN);
		RETRACTED_STICKY_PISTON_BASE.add(RETRACTED_STICKY_PISTON_BASE_UP);
		RETRACTED_STICKY_PISTON_BASE.add(RETRACTED_STICKY_PISTON_BASE_NORTH);
		RETRACTED_STICKY_PISTON_BASE.add(RETRACTED_STICKY_PISTON_BASE_SOUTH);
		RETRACTED_STICKY_PISTON_BASE.add(RETRACTED_STICKY_PISTON_BASE_WEST);
		RETRACTED_STICKY_PISTON_BASE.add(RETRACTED_STICKY_PISTON_BASE_EAST);

		EXTENDED_STICKY_PISTON_BASE = new HashSet<MaterialData>();
		EXTENDED_STICKY_PISTON_BASE.add(EXTENDED_STICKY_PISTON_BASE_DOWN);
		EXTENDED_STICKY_PISTON_BASE.add(EXTENDED_STICKY_PISTON_BASE_UP);
		EXTENDED_STICKY_PISTON_BASE.add(EXTENDED_STICKY_PISTON_BASE_NORTH);
		EXTENDED_STICKY_PISTON_BASE.add(EXTENDED_STICKY_PISTON_BASE_SOUTH);
		EXTENDED_STICKY_PISTON_BASE.add(EXTENDED_STICKY_PISTON_BASE_WEST);
		EXTENDED_STICKY_PISTON_BASE.add(EXTENDED_STICKY_PISTON_BASE_EAST);

		STICKY_PISTON_BASE = new HashSet<MaterialData>();
		STICKY_PISTON_BASE.addAll(RETRACTED_STICKY_PISTON_BASE);
		STICKY_PISTON_BASE.addAll(EXTENDED_STICKY_PISTON_BASE);

		PISTON_BASE = new HashSet<MaterialData>();
		PISTON_BASE.addAll(NONSTICKY_PISTON_BASE);
		PISTON_BASE.addAll(STICKY_PISTON_BASE);

		PISTON = new HashSet<MaterialData>();
		PISTON.addAll(PISTON_BASE);
		PISTON.addAll(PISTON_EXTENSION);

		// bonus:
		RETRACTED_PISTON_BASE = new HashSet<MaterialData>();
		RETRACTED_PISTON_BASE.addAll(RETRACTED_NONSTICKY_PISTON_BASE);
		RETRACTED_PISTON_BASE.addAll(RETRACTED_STICKY_PISTON_BASE);

		EXTENDED_PISTON_BASE = new HashSet<MaterialData>();
		EXTENDED_PISTON_BASE.addAll(EXTENDED_NONSTICKY_PISTON_BASE);
		EXTENDED_PISTON_BASE.addAll(EXTENDED_STICKY_PISTON_BASE);

		NONSTICKY_PISTON = new HashSet<MaterialData>();
		NONSTICKY_PISTON.addAll(NONSTICKY_PISTON_BASE);
		NONSTICKY_PISTON.addAll(NONSTICKY_PISTON_EXTENSION);

		STICKY_PISTON = new HashSet<MaterialData>();
		STICKY_PISTON.addAll(STICKY_PISTON_BASE);
		STICKY_PISTON.addAll(STICKY_PISTON_EXTENSION);

		// ok, this is clearly not working ._.
		RETRACTED_PISTON = new HashSet<MaterialData>();
		RETRACTED_PISTON.addAll(RETRACTED_PISTON_BASE);

		EXTENDED_PISTON = new HashSet<MaterialData>();
		EXTENDED_PISTON.addAll(EXTENDED_PISTON_BASE);
		EXTENDED_PISTON.addAll(PISTON_EXTENSION);
	}

}
