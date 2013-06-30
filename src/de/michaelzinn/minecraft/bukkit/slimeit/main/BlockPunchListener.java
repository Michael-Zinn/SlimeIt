package de.michaelzinn.minecraft.bukkit.slimeit.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.B;
import de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.MaterialData;

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

	// private final SlimeIt plugin;
	SlimeRules slimeRules = new SlimeRules();

	public BlockPunchListener(SlimeIt main) {
		// plugin = main;
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

		// Material type = block.getType();
		// byte data = block.getData();

		MaterialData materialData = MaterialData.get(block);
		Location location = block.getLocation();
		World world = block.getWorld();

		ItemStack slimeBall = new ItemStack(Material.SLIME_BALL);

		// special case1: cracked stone bricks

		if (MaterialData.CRACKED_SMOOTH_BRICK.matches(block)) {
			event.setCancelled(true);
			block.setType(Material.AIR);
			world.dropItemNaturally(location, new ItemStack(Material.SMOOTH_BRICK));
			return;
		}

		// special case 2: pistons
		// piston head
		if (MaterialData.STICKY_PISTON_EXTENSION.contains(materialData)) {
			event.setCancelled(true);
			Block base = B.getPistonBase(block);
			base.setType(Material.AIR);
			block.setType(Material.AIR);
			world.dropItemNaturally(base.getLocation(), new ItemStack(Material.PISTON_BASE));
			world.dropItemNaturally(block.getLocation(), slimeBall);
			return;
		}
		// sticky piston base
		if (MaterialData.EXTENDED_STICKY_PISTON_BASE.contains(materialData)) {
			event.setCancelled(true);
			Block extension = B.getPistonExtension(block);
			block.setType(Material.AIR);
			extension.setType(Material.AIR);
			world.dropItemNaturally(block.getLocation(), new ItemStack(Material.PISTON_BASE));
			world.dropItemNaturally(extension.getLocation(), slimeBall);
			return;
		}

		// simple cases, break according to slimeRules
		if (slimeRules.hasSlimeOnIt(block)) {
			event.setCancelled(true);
			MaterialData original = MaterialData.get(block);
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

		switch (event.getAction()) {

		case LEFT_CLICK_BLOCK:
			if (MaterialData.SMOOTH_BRICK.matches(block)) {
				if (B.isPickaxe(tool)) {
					MaterialData.CRACKED_SMOOTH_BRICK.applyTo(block);
				}
			}
			else if (slimeRules.hasSlimeOnIt(block, event.getBlockFace())) {
				B.replace(block, slimeRules.withoutSlime(block));
				world.dropItemNaturally(block.getRelative(face).getLocation(), new ItemStack(Material.SLIME_BALL, 1));
				world.playSound(block.getLocation(), Sound.SLIME_ATTACK, 1, 1);
			}
			break;

		case RIGHT_CLICK_BLOCK:
			if (tool.getType() == Material.SLIME_BALL) {
				if (slimeRules.canGetSlimeOnIt(block, event.getBlockFace())) {
					int targetAmount = tool.getAmount() - 1;
					if (targetAmount <= 0) {
						player.setItemInHand(null);
					} else {
						tool.setAmount(targetAmount);
					}
					B.replace(block, slimeRules.withSlime(block));

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
			break;
		}
	}

}
