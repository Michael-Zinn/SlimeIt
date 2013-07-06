package de.michaelzinn.minecraft.bukkit.slimeit.main;

import static de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.BukkitPlus.*;
import static de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.Thing.Tag.*;

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

import de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.Thing;

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

	SlimeIt plugin;

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

		// Material type = block.getType();
		// byte data = block.getData();

		Thing thingInBlock = Thing.in(block);
		Location location = block.getLocation();
		World world = block.getWorld();

		ItemStack slimeBall = new ItemStack(Material.SLIME_BALL);

		// special case1: cracked stone bricks

		if (thingInBlock.is(CRACKED, STONE, BRICK, BLOCK)) {
			event.setCancelled(true);
			block.setType(Material.AIR);
			world.dropItemNaturally(location, new ItemStack(Material.SMOOTH_BRICK));
			return;
		}

		// special case 2: pistons
		// piston head
		if (thingInBlock.is(STICKY, PISTON, EXTENSION)) {
			event.setCancelled(true);
			Block base = getPistonBase(block);
			base.setType(Material.AIR);
			block.setType(Material.AIR);
			world.dropItemNaturally(base.getLocation(), new ItemStack(Material.PISTON_BASE));
			world.dropItemNaturally(block.getLocation(), slimeBall);
			return;
		}
		// sticky piston base
		if (thingInBlock.is(EXTENDED, STICKY, PISTON, BASE)) {
			event.setCancelled(true);
			Block extension = getPistonExtension(block);
			block.setType(Material.AIR);
			extension.setType(Material.AIR);
			world.dropItemNaturally(block.getLocation(), new ItemStack(Material.PISTON_BASE));
			world.dropItemNaturally(extension.getLocation(), slimeBall);
			return;
		}

		// simple cases, break according to slimeRules
		if (slimeRules.hasSlimeOnIt(block)) {
			event.setCancelled(true);
			Thing original = Thing.in(block);
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
		ItemStack playerHand = player.getItemInHand();

		switch (event.getAction()) {

		case LEFT_CLICK_BLOCK:
			if (Thing.STONE_BRICK_BLOCK.is(block)) {
				if (Thing.in(playerHand).is(PICKAXE)) {
					Thing.CRACKED_STONE_BRICK_BLOCK.applyTo(block);
				}
			}
			else if (slimeRules.hasSlimeOnIt(block, event.getBlockFace())) {
				replace(block, slimeRules.withoutSlime(block));
				world.dropItemNaturally(block.getRelative(face).getLocation(), new ItemStack(Material.SLIME_BALL, 1));
				world.playSound(block.getLocation(), Sound.SLIME_ATTACK, 1, 1);
			}
			break;

		case RIGHT_CLICK_BLOCK:
			if (playerHand.getType() == Material.SLIME_BALL) {
				if (slimeRules.canGetSlimeOnIt(block, face)) {
					int targetAmount = playerHand.getAmount() - 1;
					if (targetAmount <= 0) {
						player.setItemInHand(null);
					} else {
						playerHand.setAmount(targetAmount);
					}
					replace(block, slimeRules.withSlime(block));

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
			// if (playerHand.getType() == Material.STICK) {
			// plugin.log.log(Level.INFO, "Tags: " +
			// Thing.in(block).TAGS.size());
			// for (Tag tag : Thing.in(block).TAGS) {
			// plugin.log.log(Level.INFO, " - " + tag);
			// }
			// }
			break;
		}
	}

}
