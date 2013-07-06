package de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus;

import static de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.Thing.Tag.*;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * The beginning of the BukkitPlus library.
 * 
 * Eventually, this should me moved out of the SlimeIt plugin into its own
 * library.
 * 
 * Use this as a static import to get convenient access to the methods.
 * 
 * @author Michael Zinn (@RedNifre)
 * 
 */
public class BukkitPlus {

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
		// TODO later, this should attempt to replace while keeping the
		// orientation.
		replace(block, Thing.in(newMaterial, (byte) 0));
	}

	public static void replace(Block block, Thing newBlock) {
		Thing oldBlock = Thing.in(block);

		if (oldBlock.is(PISTON)) {
			if (newBlock.is(PISTON)) {
				// replace keeping orientation and extension state
				if (oldBlock.is(EXTENDED, PISTON)) {
					Block base = getPistonBase(block);
					Block extension = getPistonExtension(block);
					if (newBlock.is(STICKY, PISTON)) {
						base.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(), base.getData(), true);
						extension.setData((byte) (extension.getData() | 8), true);
						extension.getState().update();
					}
					else {
						base.setTypeIdAndData(Material.PISTON_BASE.getId(), base.getData(), true);
						extension.setData((byte) (extension.getData() & 7), true);
						extension.getState().update();
					}
				}
				else {
					if (newBlock.is(STICKY, PISTON)) {
						block.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(), block.getData(), true);
					}
					else {
						block.setTypeIdAndData(Material.PISTON_BASE.getId(), block.getData(), true);
					}
				}
			}
			else {
				if (oldBlock.is(EXTENDED, PISTON)) {
					// remove the extension

					Block base = getPistonBase(block);
					Block extension = getPistonExtension(block);

					base.setType(newBlock.material); // TODO preserve
														// orientation?
					extension.setType(Material.AIR);
				}
				else {
					block.setType(newBlock.material);
				}
			}
		} else {
			// TODO needs to be well defined for all rotatable blocks
			// just copying the data won't cut it.
			block.setTypeIdAndData(newBlock.material.getId(), newBlock.data, true);
		}
	}

	/**
	 * 
	 * @param pistonPart
	 * @return the extension or null if there's no extension
	 */
	public static Block getPistonExtension(Block pistonPart) {
		Thing thing = Thing.in(pistonPart);

		if (thing.isnt(EXTENDED, PISTON)) {
			return null;
		}

		if (thing.is(EXTENSION)) {
			return pistonPart;
		}

		// so it's an extended base
		return pistonPart.getRelative(thing.front);
	}

	public static Block getPistonBase(Block block) {
		Thing thing = Thing.in(block);
		if (thing.isnt(PISTON)) {
			return null;
		}

		if (thing.is(PISTON, BASE)) {
			return block;
		}

		// so it's an extension...
		return block.getRelative(thing.back);
	}

}
