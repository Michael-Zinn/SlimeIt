package de.michaelzinn.minecraft.bukkit.slimeit.main;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
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

	/**
	 * Some constants for piston orientations.
	 */
	private final int DOWN = 0;
	private final int UP = 1;
	private final int NORTH = 2;
	private final int SOUTH = 3;
	private final int WEST = 4;
	private final int EAST = 5;

	/**
	 * the data values of stone brick walls
	 */
	private final byte DATA_CLEAN = 0;
	private final byte DATA_MOSSY = 1;
	private final byte DATA_CRACKED = 2;

	SlimeRules slimeDefinition = new SlimeRules();

	@EventHandler
	public void playerBreak(BlockBreakEvent event) {
		// blocks with slime should drop as items without slime + 1 slimeball

		Block block = event.getBlock();
		if (block == null)
			return;
		Material material = block.getType();
		byte data = block.getData();
		Location location = block.getLocation();
		World world = block.getWorld();

		ItemStack slimeBall = new ItemStack(Material.SLIME_BALL);

		// special case1: cracked stone bricks
		if (block.getType() == Material.SMOOTH_BRICK) {
			if (block.getData() == DATA_CRACKED) { // cracked
				event.setCancelled(true);
				block.setType(Material.AIR);
				world.dropItemNaturally(location, new ItemStack(Material.SMOOTH_BRICK));
				return;
			}
		}

		// special case 2: pistons
		// piston head
		if (material == Material.PISTON_EXTENSION) {
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
		if (material == Material.PISTON_STICKY_BASE) {
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

		if (slimeDefinition.hasSlimeOnIt(block)) {
			event.setCancelled(true);
			MaterialData original = MaterialData.from(block);
			block.setType(Material.AIR);
			world.dropItemNaturally(location, new ItemStack(slimeDefinition.withoutSlime(original).material));
			world.dropItemNaturally(location, slimeBall);
		}

	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {
		Bukkit.getLogger().log(Level.INFO, "slime punch!");

		Block block = event.getClickedBlock();

		if (block == null) {
			return;
		}

		BlockFace face = event.getBlockFace();
		World world = block.getWorld();
		Player player = event.getPlayer();
		ItemStack tool = player.getItemInHand();

		// block.setType(Material.GOLD_BLOCK);

		// if (tool.getType() == Material.BONE) {
		// if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
		// block.setData((byte) ((block.getData() - 1) % 16));
		// log("" + block.getData());
		// } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
		// block.setData((byte) ((block.getData() + 1) % 16));
		// log("" + block.getData());
		// }
		// return;
		// }

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			// log("data=" + block.getData());

			if (block.getType() == Material.SMOOTH_BRICK) {
				if (block.getData() == DATA_CLEAN) {
					if (isPickaxe(tool)) {
						block.setData(DATA_CRACKED);
						block.getState().update();
					}
				}
			}

			if (slimeDefinition.hasSlimeOnIt(block, event.getBlockFace())) {
				// special case for piston heads
				slimeDefinition.removeSlime(block);
				if (block.getType() == Material.PISTON_EXTENSION) {
					Block base = getPistonBase(block);
					byte data = base.getData();
					base.setTypeIdAndData(Material.PISTON_BASE.getId(), data, true);
				}

				world.dropItemNaturally(block.getRelative(face).getLocation(), new ItemStack(Material.SLIME_BALL, 1));
				world.playSound(block.getLocation(), Sound.SLIME_ATTACK, 1, 1);
			}
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			log("Right click");
			if (tool.getType() == Material.SLIME_BALL) {
				log("has a slime ball");
				if (slimeDefinition.canGetSlimeOnIt(block, event.getBlockFace())) {
					log("can get slimed");
					int targetAmount = tool.getAmount() - 1;
					if (targetAmount <= 0) {
						player.setItemInHand(null);
					} else {
						tool.setAmount(targetAmount);
					}
					slimeDefinition.addSlime(block);

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

					// special case for piston heads
					if (block.getType() == Material.PISTON_EXTENSION) {
						Block base = getPistonBase(block);
						byte data = base.getData();
						base.setTypeIdAndData(Material.PISTON_STICKY_BASE.getId(), data, true);
					}

				}
			}
		}

	}

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

	private void log(String s) {
		Bukkit.getLogger().log(Level.INFO, s);
	}

	/**
	 * Maybe all of these convenience methods should be exported to a
	 * "Bukkit made easy" library? Hm...
	 * 
	 * @param base
	 * @return
	 */
	private Block getPistonExtension(Block base) {
		switch (base.getData() & 7) {
		case DOWN:
			return base.getRelative(BlockFace.DOWN);
		case UP:
			return base.getRelative(BlockFace.UP);
		case NORTH:
			return base.getRelative(BlockFace.NORTH);
		case SOUTH:
			return base.getRelative(BlockFace.SOUTH);
		case WEST:
			return base.getRelative(BlockFace.WEST);
		case EAST:
			return base.getRelative(BlockFace.EAST);
		}

		// unreachable code (as long as the argument is actually an extension)
		return null;
	}

	private Block getPistonBase(Block extension) {
		switch (extension.getData() & 7) {
		case DOWN:
			return extension.getRelative(BlockFace.UP);
		case UP:
			return extension.getRelative(BlockFace.DOWN);
		case NORTH:
			return extension.getRelative(BlockFace.SOUTH);
		case SOUTH:
			return extension.getRelative(BlockFace.NORTH);
		case WEST:
			return extension.getRelative(BlockFace.EAST);
		case EAST:
			return extension.getRelative(BlockFace.WEST);
		}

		// unreachable code (as long as the argument is actually an extension)
		return null;
	}

	/**
	 * only call with piston extensions
	 * 
	 * @param extension
	 * @return
	 */
	private boolean isStickyExtension(Block extension) {
		return (extension.getData() & 8) != 0;
	}

	/**
	 * only call with piston bases
	 * 
	 * @param base
	 * @return
	 */
	private boolean isExtendedBase(Block base) {
		return (base.getData() & 8) != 0;
	}
}
