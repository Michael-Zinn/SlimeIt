package de.michaelzinn.minecraft.bukkit.slimeit.main;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * A look up table that defines some things about blocks:
 * 
 * - Which blocks have "slime" on them (and on which face). Note that this
 * plugin counts moss as slime.
 * 
 * - Which blocks can get slime to them.
 * 
 * - Which blocks turn into which other blocks when you add or remove slime to
 * them.
 * 
 * - Only handles the easy cases. More complex things like breaking an extended
 * sticky piston are handled in BlockPunchListener (which is slightly ugly, but
 * works for now)
 * 
 * @author Michael Zinn (@RedNifre)
 * 
 */

public class SlimeRules {

	private Map<MaterialData, SlimeMetaData> canGetSlimeOnIt = new Hashtable<MaterialData, SlimeMetaData>();
	private Map<MaterialData, SlimeMetaData> hasSlimeOnIt = new Hashtable<MaterialData, SlimeMetaData>();

	public MaterialData withoutSlime(Block block) {
		return hasSlimeOnIt.get(MaterialData.from(block)).removeSlimeToGet.materialData;
	}

	public MaterialData withoutSlime(MaterialData materialData) {
		return hasSlimeOnIt.get(materialData).removeSlimeToGet.materialData;
	}

	/**
	 * Removes slime from block. Crashes when called with blocks that don't have
	 * slime on it. Doesn't drop slime balls.
	 * 
	 * @param block
	 */
	public void removeSlime(Block block) {
		BlockPunchListener.replace(block, hasSlimeOnIt.get(MaterialData.from(block)).removeSlimeToGet.materialData);
	}

	public void addSlime(Block block) {
		BlockPunchListener.replace(block, canGetSlimeOnIt.get(MaterialData.from(block)).addSlimeToGet.materialData);
	}

	/**
	 * This defines which blocks turn into which other blocks when slime is
	 * applied or removed
	 */
	public SlimeRules() {
		// FIXME This could take a good refactoring, it's way too much code.
		// Maybe even define the relations in a txt file that can get parsed?
		// See https://github.com/RedNifre/SlimeIt/issues/11

		// useful stuff
		Set<BlockFace> allFaces = new HashSet<BlockFace>();
		allFaces.add(BlockFace.DOWN);
		allFaces.add(BlockFace.UP);
		allFaces.add(BlockFace.NORTH);
		allFaces.add(BlockFace.EAST);
		allFaces.add(BlockFace.SOUTH);
		allFaces.add(BlockFace.WEST);

		// ==== Cobblestone ================

		SlimeMetaData cobble = new SlimeMetaData(Material.COBBLESTONE, (byte) 0);
		SlimeMetaData slimeyCobble = new SlimeMetaData(Material.MOSSY_COBBLESTONE, (byte) 0);
		slimeyCobble.facesWithSlimeOnIt = allFaces;

		cobble.addSlimeToGet = slimeyCobble;
		slimeyCobble.removeSlimeToGet = cobble;

		canGetSlimeOnIt.put(cobble.materialData, cobble);
		hasSlimeOnIt.put(slimeyCobble.materialData, slimeyCobble);

		// ==== Cobblestone Wall ================

		SlimeMetaData cobbleWall = new SlimeMetaData(Material.COBBLE_WALL, (byte) 0);
		SlimeMetaData slimeyCobbleWall = new SlimeMetaData(Material.COBBLE_WALL, (byte) 1);
		slimeyCobbleWall.facesWithSlimeOnIt = allFaces;

		cobbleWall.addSlimeToGet = slimeyCobbleWall;
		slimeyCobbleWall.removeSlimeToGet = cobbleWall;

		canGetSlimeOnIt.put(cobbleWall.materialData, cobbleWall);
		hasSlimeOnIt.put(slimeyCobbleWall.materialData, slimeyCobbleWall);

		// ==== Stone Bricks (These are a special case) =============

		SlimeMetaData stoneBrick = new SlimeMetaData(Material.SMOOTH_BRICK, (byte) 0);
		SlimeMetaData slimeyStoneBrick = new SlimeMetaData(Material.SMOOTH_BRICK, (byte) 1);
		SlimeMetaData crackedStoneBrick = new SlimeMetaData(Material.SMOOTH_BRICK, (byte) 2);
		// note that chiseled stone bricks aren't affected by this mod. They are
		// not treated as stone bricks

		stoneBrick.addSlimeToGet = slimeyStoneBrick;
		crackedStoneBrick.addSlimeToGet = slimeyStoneBrick;
		slimeyStoneBrick.removeSlimeToGet = stoneBrick;
		slimeyStoneBrick.facesWithSlimeOnIt = allFaces;

		canGetSlimeOnIt.put(stoneBrick.materialData, stoneBrick);
		canGetSlimeOnIt.put(crackedStoneBrick.materialData, crackedStoneBrick);
		hasSlimeOnIt.put(slimeyStoneBrick.materialData, slimeyStoneBrick);

		// ==== Piston bases (oh boy...) ===============
		// nonsticky base, can only be slimed when retracted
		SlimeMetaData pistonBaseDown = new SlimeMetaData(Material.PISTON_BASE, (byte) 0);
		SlimeMetaData pistonBaseUp = new SlimeMetaData(Material.PISTON_BASE, (byte) 1);
		SlimeMetaData pistonBaseNorth = new SlimeMetaData(Material.PISTON_BASE, (byte) 2);
		SlimeMetaData pistonBaseSouth = new SlimeMetaData(Material.PISTON_BASE, (byte) 3);
		SlimeMetaData pistonBaseWest = new SlimeMetaData(Material.PISTON_BASE, (byte) 4);
		SlimeMetaData pistonBaseEast = new SlimeMetaData(Material.PISTON_BASE, (byte) 5);

		// sticky, only has slime on it when retracted
		SlimeMetaData slimeyPistonBaseDown = new SlimeMetaData(Material.PISTON_STICKY_BASE, (byte) 0);
		SlimeMetaData slimeyPistonBaseUp = new SlimeMetaData(Material.PISTON_STICKY_BASE, (byte) 1);
		SlimeMetaData slimeyPistonBaseNorth = new SlimeMetaData(Material.PISTON_STICKY_BASE, (byte) 2);
		SlimeMetaData slimeyPistonBaseSouth = new SlimeMetaData(Material.PISTON_STICKY_BASE, (byte) 3);
		SlimeMetaData slimeyPistonBaseWest = new SlimeMetaData(Material.PISTON_STICKY_BASE, (byte) 4);
		SlimeMetaData slimeyPistonBaseEast = new SlimeMetaData(Material.PISTON_STICKY_BASE, (byte) 5);

		// mapping
		pistonBaseDown.addSlimeToGet = slimeyPistonBaseDown;
		pistonBaseUp.addSlimeToGet = slimeyPistonBaseUp;
		pistonBaseNorth.addSlimeToGet = slimeyPistonBaseNorth;
		pistonBaseSouth.addSlimeToGet = slimeyPistonBaseSouth;
		pistonBaseWest.addSlimeToGet = slimeyPistonBaseWest;
		pistonBaseEast.addSlimeToGet = slimeyPistonBaseEast;

		slimeyPistonBaseDown.removeSlimeToGet = pistonBaseDown;
		slimeyPistonBaseUp.removeSlimeToGet = pistonBaseUp;
		slimeyPistonBaseNorth.removeSlimeToGet = pistonBaseNorth;
		slimeyPistonBaseSouth.removeSlimeToGet = pistonBaseSouth;
		slimeyPistonBaseWest.removeSlimeToGet = pistonBaseWest;
		slimeyPistonBaseEast.removeSlimeToGet = pistonBaseEast;

		// faces
		slimeyPistonBaseDown.facesWithSlimeOnIt.add(BlockFace.DOWN);
		slimeyPistonBaseUp.facesWithSlimeOnIt.add(BlockFace.UP);
		slimeyPistonBaseNorth.facesWithSlimeOnIt.add(BlockFace.NORTH);
		slimeyPistonBaseSouth.facesWithSlimeOnIt.add(BlockFace.SOUTH);
		slimeyPistonBaseWest.facesWithSlimeOnIt.add(BlockFace.WEST);
		slimeyPistonBaseEast.facesWithSlimeOnIt.add(BlockFace.EAST);

		hasSlimeOnIt.put(slimeyPistonBaseDown.materialData, slimeyPistonBaseDown);
		hasSlimeOnIt.put(slimeyPistonBaseUp.materialData, slimeyPistonBaseUp);
		hasSlimeOnIt.put(slimeyPistonBaseNorth.materialData, slimeyPistonBaseNorth);
		hasSlimeOnIt.put(slimeyPistonBaseSouth.materialData, slimeyPistonBaseSouth);
		hasSlimeOnIt.put(slimeyPistonBaseWest.materialData, slimeyPistonBaseWest);
		hasSlimeOnIt.put(slimeyPistonBaseEast.materialData, slimeyPistonBaseEast);

		canGetSlimeOnIt.put(pistonBaseDown.materialData, pistonBaseDown);
		canGetSlimeOnIt.put(pistonBaseUp.materialData, pistonBaseUp);
		canGetSlimeOnIt.put(pistonBaseNorth.materialData, pistonBaseNorth);
		canGetSlimeOnIt.put(pistonBaseSouth.materialData, pistonBaseSouth);
		canGetSlimeOnIt.put(pistonBaseWest.materialData, pistonBaseWest);
		canGetSlimeOnIt.put(pistonBaseEast.materialData, pistonBaseEast);

		// ==== Piston Extension =====================
		SlimeMetaData pistonExtensionDown = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 0);
		SlimeMetaData pistonExtensionUp = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 1);
		SlimeMetaData pistonExtensionNorth = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 2);
		SlimeMetaData pistonExtensionSouth = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 3);
		SlimeMetaData pistonExtensionWest = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 4);
		SlimeMetaData pistonExtensionEast = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 5);

		SlimeMetaData slimeyPistonExtensionDown = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 8);
		SlimeMetaData slimeyPistonExtensionUp = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 9);
		SlimeMetaData slimeyPistonExtensionNorth = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 10);
		SlimeMetaData slimeyPistonExtensionSouth = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 11);
		SlimeMetaData slimeyPistonExtensionWest = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 12);
		SlimeMetaData slimeyPistonExtensionEast = new SlimeMetaData(Material.PISTON_EXTENSION, (byte) 13);

		slimeyPistonExtensionDown.facesWithSlimeOnIt.add(BlockFace.DOWN);
		slimeyPistonExtensionUp.facesWithSlimeOnIt.add(BlockFace.UP);
		slimeyPistonExtensionNorth.facesWithSlimeOnIt.add(BlockFace.NORTH);
		slimeyPistonExtensionSouth.facesWithSlimeOnIt.add(BlockFace.SOUTH);
		slimeyPistonExtensionWest.facesWithSlimeOnIt.add(BlockFace.WEST);
		slimeyPistonExtensionEast.facesWithSlimeOnIt.add(BlockFace.EAST);

		pistonExtensionDown.addSlimeToGet = slimeyPistonExtensionDown;
		pistonExtensionUp.addSlimeToGet = slimeyPistonExtensionUp;
		pistonExtensionNorth.addSlimeToGet = slimeyPistonExtensionNorth;
		pistonExtensionSouth.addSlimeToGet = slimeyPistonExtensionSouth;
		pistonExtensionWest.addSlimeToGet = slimeyPistonExtensionWest;
		pistonExtensionEast.addSlimeToGet = slimeyPistonExtensionEast;

		slimeyPistonExtensionDown.removeSlimeToGet = pistonExtensionDown;
		slimeyPistonExtensionUp.removeSlimeToGet = pistonExtensionUp;
		slimeyPistonExtensionNorth.removeSlimeToGet = pistonExtensionNorth;
		slimeyPistonExtensionSouth.removeSlimeToGet = pistonExtensionSouth;
		slimeyPistonExtensionWest.removeSlimeToGet = pistonExtensionWest;
		slimeyPistonExtensionEast.removeSlimeToGet = pistonExtensionEast;

		canGetSlimeOnIt.put(pistonExtensionDown.materialData, pistonExtensionDown);
		canGetSlimeOnIt.put(pistonExtensionUp.materialData, pistonExtensionUp);
		canGetSlimeOnIt.put(pistonExtensionNorth.materialData, pistonExtensionNorth);
		canGetSlimeOnIt.put(pistonExtensionSouth.materialData, pistonExtensionSouth);
		canGetSlimeOnIt.put(pistonExtensionWest.materialData, pistonExtensionWest);
		canGetSlimeOnIt.put(pistonExtensionEast.materialData, pistonExtensionEast);

		hasSlimeOnIt.put(slimeyPistonExtensionDown.materialData, slimeyPistonExtensionDown);
		hasSlimeOnIt.put(slimeyPistonExtensionUp.materialData, slimeyPistonExtensionUp);
		hasSlimeOnIt.put(slimeyPistonExtensionNorth.materialData, slimeyPistonExtensionNorth);
		hasSlimeOnIt.put(slimeyPistonExtensionSouth.materialData, slimeyPistonExtensionSouth);
		hasSlimeOnIt.put(slimeyPistonExtensionWest.materialData, slimeyPistonExtensionWest);
		hasSlimeOnIt.put(slimeyPistonExtensionEast.materialData, slimeyPistonExtensionEast);

	}

	public boolean canGetSlimeOnIt(Block block) {
		return canGetSlimeOnIt.containsKey(MaterialData.from(block));
	}

	public boolean canGetSlimeOnIt(Block block, BlockFace face) {
		if (canGetSlimeOnIt(block)) {
			return canGetSlimeOnIt.get(MaterialData.from(block)).addSlimeToGet.facesWithSlimeOnIt.contains(face);
		}
		return false;
	}

	public boolean hasSlimeOnIt(Block block) {
		return hasSlimeOnIt.containsKey(MaterialData.from(block));
	}

	public boolean hasSlimeOnIt(Block block, BlockFace face) {
		if (hasSlimeOnIt(block)) {
			return hasSlimeOnIt.get(MaterialData.from(block)).facesWithSlimeOnIt.contains(face);
		}
		return false;
	}

	/**
	 * Useful to express which MaterialDatas have slime on which faces.
	 * 
	 * Also allows linking slimy and non-slimy objects
	 * 
	 * @author Michael Zinn (@RedNifre)
	 * 
	 */
	private class SlimeMetaData {
		public MaterialData materialData;
		public Set<BlockFace> facesWithSlimeOnIt = new HashSet<BlockFace>();

		public SlimeMetaData addSlimeToGet;
		public SlimeMetaData removeSlimeToGet;

		public SlimeMetaData(Material material, byte data) {
			materialData = new MaterialData(material, data);
		}
	}
}
