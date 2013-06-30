package de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * The beginning of the BukkitPlus library.
 * 
 * Eventually, this should me moved out of the SlimeIt plugin into its own
 * library.
 * 
 * @author Michael Zinn (@RedNifre)
 * 
 */
public class B {

	/**
	 * Replaces a block in a smart way: Tries to preserve orientation, if
	 * possible. When replacing a piston with another piston, tries to preserve
	 * extension and orientation. When replacing any part of an extended piston
	 * with something other than a piston, it replaces the base with newMaterial
	 * and the extension with air.
	 * 
	 * @param block
	 * @param newMaterial
	 */
	public static void replace(Block block, Material newMaterial) {
		if (isPiston(block)) {
			if (isPiston(newMaterial)) {
				// replace keeping orientation and extension state
				if (isExtendedPiston(block)) {
					Block base = getPistonBase(block);
					Block extension = getPistonExtension(block);
					if (isStickyPiston(newMaterial)) {
						base.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(), base.getData(), true);
						extension.setTypeIdAndData(Material.PISTON_EXTENSION.getId(), (byte) (extension.getData() | 8), true);
					}
					else {
						base.setTypeIdAndData(Material.PISTON_BASE.getId(), base.getData(), true);
						extension.setTypeIdAndData(Material.PISTON_EXTENSION.getId(), (byte) (extension.getData() & 7), true);
					}
				}
				else {
					if (isStickyPiston(newMaterial)) {
						block.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(), block.getData(), true);
					}
					else {
						block.setTypeIdAndData(Material.PISTON_BASE.getId(), block.getData(), true);
					}
				}
			}
			else {
				// remove the extension
				if (isExtendedPiston(block)) {
					Block base = getPistonBase(block);
					Block extension = getPistonExtension(block);

					base.setType(newMaterial); // TODO preserve orientation?
					extension.setType(Material.AIR);
				}
				else {
					block.setType(newMaterial);
				}
			}
		} else {
			// TODO needs to be well defined for all rotatable blocks
			// just copying the data won't cut it.
			block.setTypeIdAndData(newMaterial.getId(), block.getData(), true);
		}
	}

	public static void replace(Block block, MaterialData newMaterialData) {
		// TODO this is currently mostly copy paste from the previous method.
		// Needs to
		// be refactored later
		if (isPiston(block)) {
			if (isPiston(newMaterialData)) {
				// replace keeping orientation and extension state
				if (isExtendedPiston(block)) {
					Block base = getPistonBase(block);
					Block extension = getPistonExtension(block);
					if (isStickyPiston(newMaterialData)) {
						base.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(), base.getData(), true);
						// can't set Type, since that would break the piston
						// base ???
						extension.setData((byte) (extension.getData() | 8), true);
						extension.getState().update();
					}
					else {
						base.setTypeIdAndData(Material.PISTON_BASE.getId(), base.getData(), true);
						// can't set Type, since that would break the piston
						// base ???
						extension.setData((byte) (extension.getData() & 7), true);
						extension.getState().update();
					}
				}
				else {
					if (isStickyPiston(newMaterialData)) {
						block.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(), block.getData(), true);
					}
					else {
						block.setTypeIdAndData(Material.PISTON_BASE.getId(), block.getData(), true);
					}
				}
			}
			else {
				if (isExtendedPiston(block)) {
					// remove the extension

					Block base = getPistonBase(block);
					Block extension = getPistonExtension(block);

					base.setType(newMaterialData.material); // TODO preserve
															// orientation?
					extension.setType(Material.AIR);
				}
				else {
					block.setType(newMaterialData.material);
				}
			}
		} else {
			// TODO needs to be well defined for all rotatable blocks
			// just copying the data won't cut it.
			block.setTypeIdAndData(newMaterialData.material.getId(), newMaterialData.data, true);
		}
	}

	// Pick axes //////////////////////////////////////////////

	private static final Set<Material> PICKAXES = new HashSet<Material>();
	static {
		PICKAXES.add(Material.WOOD_PICKAXE);
		PICKAXES.add(Material.STONE_PICKAXE);
		PICKAXES.add(Material.IRON_PICKAXE);
		PICKAXES.add(Material.GOLD_PICKAXE);
		PICKAXES.add(Material.DIAMOND_PICKAXE);
	}

	/**
	 * This one is a bit odd, should be removed/changed later
	 * 
	 * @param stack
	 * @return
	 */
	public static boolean isPickaxe(ItemStack stack) {
		return PICKAXES.contains(stack.getType());
	}

	// Orientations ///////////////////////////////////////////
	// TODO check if this also works for pumpkins, dispensers etc.

	// currently, these are tailored for pistons
	private static final int DOWN = 0;
	private static final int UP = 1;
	private static final int NORTH = 2;
	private static final int SOUTH = 3;
	private static final int WEST = 4;
	private static final int EAST = 5;

	// Pistons ////////////////////////////////////////////////

	public static boolean isPistonBase(Block block) {
		return MaterialData.PISTON_BASE.contains(MaterialData.get(block));
	}

	/**
	 * 
	 * @param pistonPart
	 * @return the extension or null if there's no extension
	 */
	public static Block getPistonExtension(Block pistonPart) {
		if (!isPiston(pistonPart)) {
			return null; // should this throw a runtime exception?
		}
		switch (pistonPart.getType()) {
		case PISTON_EXTENSION:
			return pistonPart;
		case PISTON_BASE:
		case PISTON_STICKY_BASE:
			if (isExtendedBase(pistonPart)) {
				switch (pistonPart.getData() & 7) {
				case DOWN:
					return pistonPart.getRelative(BlockFace.DOWN);
				case UP:
					return pistonPart.getRelative(BlockFace.UP);
				case NORTH:
					return pistonPart.getRelative(BlockFace.NORTH);
				case SOUTH:
					return pistonPart.getRelative(BlockFace.SOUTH);
				case WEST:
					return pistonPart.getRelative(BlockFace.WEST);
				case EAST:
					return pistonPart.getRelative(BlockFace.EAST);
				}
			} else {
				return null;
			}
		}

		// unreachabe code
		throw new RuntimeException("The code is wrong, isPiston() and getPistonExtension() are no longer compatible!");
	}

	public static Block getPistonBase(Block pistonPart) {
		if (!isPiston(pistonPart)) {
			return null; // should this throw a runtime exception?
		}
		switch (pistonPart.getType()) {
		case PISTON_BASE:
		case PISTON_STICKY_BASE:
			return pistonPart;
		case PISTON_EXTENSION:
			switch (pistonPart.getData() & 7) {
			case DOWN:
				return pistonPart.getRelative(BlockFace.UP);
			case UP:
				return pistonPart.getRelative(BlockFace.DOWN);
			case NORTH:
				return pistonPart.getRelative(BlockFace.SOUTH);
			case SOUTH:
				return pistonPart.getRelative(BlockFace.NORTH);
			case WEST:
				return pistonPart.getRelative(BlockFace.EAST);
			case EAST:
				return pistonPart.getRelative(BlockFace.WEST);
			}
		}

		// unreachable code
		throw new RuntimeException("The code is wrong, isPiston() and getPistonBase() are no longer compatible!");
	}

	public static boolean isPiston(Block block) {
		return isPiston(MaterialData.get(block));
	}

	public static boolean isPiston(Material material) {
		return isPiston(MaterialData.get(material, (byte) 0));
	}

	public static boolean isPiston(MaterialData materialData) {
		return MaterialData.PISTON.contains(materialData);
	}

	public static boolean isStickyPiston(Block block) {
		return isStickyPiston(MaterialData.get(block));
	}

	public static boolean isStickyPiston(MaterialData materialData) {
		return MaterialData.STICKY_PISTON.contains(materialData);
	}

	public static boolean isStickyPiston(Material material) {
		return isStickyPiston(material, (byte) 0);
	}

	public static boolean isStickyPiston(Material material, byte data) {
		return isStickyPiston(MaterialData.get(material, data));
	}

	public static boolean isStickyExtension(Block block) {
		return isStickyExtension(MaterialData.get(block));
	}

	public static boolean isStickyExtension(Material material, byte data) {
		return isStickyExtension(MaterialData.get(material, data));
	}

	public static boolean isStickyExtension(MaterialData extension) {
		return MaterialData.STICKY_PISTON_EXTENSION.contains(extension);
	}

	public static boolean isExtendedPiston(Block block) {
		return MaterialData.EXTENDED_PISTON.contains(MaterialData.get(block));
	}

	public static boolean isRetractedPiston(Block block) {
		return MaterialData.RETRACTED_PISTON.contains(MaterialData.get(block));
	}

	/**
	 * only call with piston bases
	 * 
	 * @param base
	 * @return
	 */
	private static boolean isExtendedBase(Block base) {
		return (base.getData() & 8) != 0;
	}

}
