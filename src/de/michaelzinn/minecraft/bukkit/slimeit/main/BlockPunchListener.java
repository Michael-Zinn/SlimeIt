package de.michaelzinn.minecraft.bukkit.slimeit.main;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles what happens when the player left clicks, right clicks or destroys a
 * block.
 * 
 * FIXME Doesn't respect protection plugins like WorldGuard yet
 * 
 * @author Michael Zinn (@RedNifre)
 * 
 */
public class BlockPunchListener implements Listener {

	private final SlimeIt plugin;
	SlimeRules slimeRules = new SlimeRules();

	public BlockPunchListener(SlimeIt main) {
		plugin = main;
	}

	@EventHandler
	public void playerBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}

		// blocks with slime should drop as items without slime + 1 slimeball

		Block block = event.getBlock();
		if (block == null) {
			return;
		}

		Material type = block.getType();
		byte data = block.getData();
		Location location = block.getLocation();
		World world = block.getWorld();

		ItemStack slimeBall = new ItemStack(Material.SLIME_BALL);

		// special case1: cracked stone bricks
		if (type == Material.SMOOTH_BRICK) {
			if (data == DATA_CRACKED) {
				event.setCancelled(true);
				block.setType(Material.AIR);
				world.dropItemNaturally(location, new ItemStack(Material.SMOOTH_BRICK));
				return;
			}
		}

		// special case 2: pistons
		// piston head
		if (type == Material.PISTON_EXTENSION) {
			if (isStickyExtension(block)) {
				event.setCancelled(true);
				Block base = getPistonBase(block);
				base.setType(Material.AIR);
				block.setType(Material.AIR);
				world.dropItemNaturally(base.getLocation(), new ItemStack(Material.PISTON_BASE));
				world.dropItemNaturally(block.getLocation(), slimeBall);
				return;
			}
		}
		// sticky piston base
		if (type == Material.PISTON_STICKY_BASE) {
			if (isExtendedBase(block)) {
				event.setCancelled(true);
				Block extension = getPistonExtension(block);
				block.setType(Material.AIR);
				extension.setType(Material.AIR);
				world.dropItemNaturally(block.getLocation(), new ItemStack(Material.PISTON_BASE));
				world.dropItemNaturally(extension.getLocation(), slimeBall);
				return;
			}
		}

		// simple cases, break according to slimeRules
		if (slimeRules.hasSlimeOnIt(block)) {
			event.setCancelled(true);
			MaterialData original = MaterialData.from(block);
			block.setType(Material.AIR);
			world.dropItemNaturally(location, new ItemStack(slimeRules.withoutSlime(original).material));
			world.dropItemNaturally(location, slimeBall);
		}

	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getClickedBlock();

		if (block == null) {
			return;
		}

		BlockFace face = event.getBlockFace();
		World world = block.getWorld();
		Player player = event.getPlayer();
		ItemStack tool = player.getItemInHand();

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

			if (block.getType() == Material.SMOOTH_BRICK) {
				if (block.getData() == DATA_CLEAN) {
					if (isPickaxe(tool)) {
						block.setData(DATA_CRACKED);
						block.getState().update();
					}
				}
			}

			if (slimeRules.hasSlimeOnIt(block, event.getBlockFace())) {
				slimeRules.removeSlime(block);

				world.dropItemNaturally(block.getRelative(face).getLocation(), new ItemStack(Material.SLIME_BALL, 1));
				world.playSound(block.getLocation(), Sound.SLIME_ATTACK, 1, 1);
			}

		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (tool.getType() == Material.SLIME_BALL) {
				if (slimeRules.canGetSlimeOnIt(block, event.getBlockFace())) {
					int targetAmount = tool.getAmount() - 1;
					if (targetAmount <= 0) {
						player.setItemInHand(null);
					} else {
						tool.setAmount(targetAmount);
					}
					slimeRules.addSlime(block);

					Sound sound = Sound.SLIME_WALK;
					switch ((int) (Math.rint(Math.random() * 1))) {
					case 0:
						sound = Sound.SLIME_WALK;
						break;
					case 1:
						sound = Sound.SLIME_WALK2;
						break;
					}
					player.getWorld().playSound(block.getLocation(), sound, 1, 1);
				}
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Everything past here should be moved to a BukkitPlus library later. E.g.,
	// when Java 8 gets released in 2014, BukkitPlus could be an interface that
	// includes everything down there as default method implementations, so this
	// BlockPunchListener could include them by "implementing" said interface.
	//
	// You can discuss this issue here:
	// https://github.com/RedNifre/SlimeIt/issues/10
	//
	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////

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

	private void log(String s) {
		plugin.log.info(s);
	}

	/**
	 * the data values of stone brick walls
	 */
	private final byte DATA_CLEAN = 0;
	private final byte DATA_MOSSY = 1;
	private final byte DATA_CRACKED = 2;

	// Pick axes //////////////////////////////////////////////

	static final Set<Material> PICKAXES = new HashSet<Material>();
	static {
		PICKAXES.add(Material.WOOD_PICKAXE);
		PICKAXES.add(Material.STONE_PICKAXE);
		PICKAXES.add(Material.IRON_PICKAXE);
		PICKAXES.add(Material.GOLD_PICKAXE);
		PICKAXES.add(Material.DIAMOND_PICKAXE);
	}

	private boolean isPickaxe(ItemStack stack) {
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

	static final Set<Material> PISTONS = new HashSet<Material>();
	static {
		PISTONS.add(Material.PISTON_BASE);
		PISTONS.add(Material.PISTON_STICKY_BASE);
		PISTONS.add(Material.PISTON_EXTENSION);
	}

	public static boolean isPiston(Block block) {
		return PISTONS.contains(block.getType());
	}

	public static boolean isPiston(Material material) {
		return PISTONS.contains(material);
	}

	public static boolean isPiston(MaterialData materialData) {
		return PISTONS.contains(materialData.material);
	}

	static final Set<Material> PISTON_BASES = new HashSet<Material>();
	static {
		PISTON_BASES.add(Material.PISTON_BASE);
		PISTON_BASES.add(Material.PISTON_STICKY_BASE);
	}

	public static boolean isPistonBase(Block block) {
		return PISTON_BASES.contains(block);
	}

	/**
	 * 
	 * @param pistonPart
	 * @return the extension or null if there's no extension
	 */
	private static Block getPistonExtension(Block pistonPart) {
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

	public static boolean isStickyPiston(Material pistonPart) {
		return pistonPart == Material.PISTON_STICKY_BASE; // there are no sticky
															// extension
															// materials
	}

	public static boolean isStickyPiston(MaterialData pistonPart) {
		return isStickyPiston(pistonPart.material, pistonPart.data);
	}

	public static boolean isStickyPiston(Block pistonPart) {
		return isStickyPiston(pistonPart.getType(), pistonPart.getData());
	}

	public static boolean isStickyPiston(Material material, byte data) {
		return material == Material.PISTON_STICKY_BASE || isStickyExtension(material, data);
	}

	public static boolean isStickyExtension(MaterialData extension) {
		return isStickyExtension(extension.material, extension.data);
	}

	public static boolean isStickyExtension(Block extension) {
		return isStickyExtension(extension.getType(), extension.getData());
	}

	public static boolean isStickyExtension(Material material, byte data) {
		return material == Material.PISTON_EXTENSION && (data & 8) != 0;
	}

	public static boolean isExtendedPiston(Block block) {
		switch (block.getType()) {
		case PISTON_EXTENSION:
			return true;
		case PISTON_BASE:
		case PISTON_STICKY_BASE:
			return isExtendedBase(block);
		default:
			return false;
		}
	}

	public static boolean isRetractedPiston(Block block) {
		switch (block.getType()) {
		case PISTON_BASE:
		case PISTON_STICKY_BASE:
			return !isExtendedBase(block);
		default:
			return false;
		}
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
