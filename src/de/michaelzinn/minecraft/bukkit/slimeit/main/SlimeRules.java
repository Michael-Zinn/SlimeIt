package de.michaelzinn.minecraft.bukkit.slimeit.main;

import java.util.Arrays;
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

	// useful stuff
	private static final BlockFace[] allFaces = {
			BlockFace.DOWN,
			BlockFace.UP,
			BlockFace.NORTH,
			BlockFace.EAST,
			BlockFace.SOUTH,
			BlockFace.WEST
	};

	/**
	 * This defines which blocks turn into which other blocks when slime is
	 * applied or removed
	 */
	public SlimeRules() {
		// FIXME This could take a good refactoring, it's way too much code.
		// Maybe even define the relations in a txt file that can get parsed?
		// See https://github.com/RedNifre/SlimeIt/issues/11

		// Cobble
		defineBidirectionalSlimeRelation(
				MaterialData.COBBLESTONE,
				MaterialData.MOSSY_COBBLESTONE, allFaces);
		defineBidirectionalSlimeRelation(
				MaterialData.COBBLE_WALL,
				MaterialData.MOSSY_COBBLE_WALL, allFaces);

		// Stone bricks
		defineBidirectionalSlimeRelation(
				MaterialData.SMOOTH_BRICK,
				MaterialData.MOSSY_SMOOTH_BRICK, allFaces);
		defineUnidirectionalSlimeRelation(
				MaterialData.CRACKED_SMOOTH_BRICK,
				MaterialData.MOSSY_SMOOTH_BRICK, allFaces);

		// Piston bases
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_BASE_RETRACTED_DOWN,
				MaterialData.PISTON_STICKY_BASE_RETRACTED_DOWN, BlockFace.DOWN);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_BASE_RETRACTED_UP,
				MaterialData.PISTON_STICKY_BASE_RETRACTED_UP, BlockFace.UP);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_BASE_RETRACTED_NORTH,
				MaterialData.PISTON_STICKY_BASE_RETRACTED_NORTH, BlockFace.NORTH);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_BASE_RETRACTED_SOUTH,
				MaterialData.PISTON_STICKY_BASE_RETRACTED_SOUTH, BlockFace.SOUTH);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_BASE_RETRACTED_WEST,
				MaterialData.PISTON_STICKY_BASE_RETRACTED_WEST, BlockFace.WEST);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_BASE_RETRACTED_EAST,
				MaterialData.PISTON_STICKY_BASE_RETRACTED_EAST, BlockFace.EAST);

		// Piston extensions
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_EXTENSION_DOWN,
				MaterialData.PISTON_STICKY_EXTENSION_DOWN, BlockFace.DOWN);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_EXTENSION_UP,
				MaterialData.PISTON_STICKY_EXTENSION_UP, BlockFace.UP);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_EXTENSION_NORTH,
				MaterialData.PISTON_STICKY_EXTENSION_NORTH, BlockFace.NORTH);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_EXTENSION_SOUTH,
				MaterialData.PISTON_STICKY_EXTENSION_SOUTH, BlockFace.SOUTH);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_EXTENSION_WEST,
				MaterialData.PISTON_STICKY_EXTENSION_WEST, BlockFace.WEST);
		defineBidirectionalSlimeRelation(
				MaterialData.PISTON_EXTENSION_EAST,
				MaterialData.PISTON_STICKY_EXTENSION_EAST, BlockFace.EAST);
	}

	private void defineBidirectionalSlimeRelation(MaterialData withoutSlime, MaterialData withSlime, BlockFace... facesWithSlime) {
		SlimeMetaData metaWithout = new SlimeMetaData(withoutSlime);
		SlimeMetaData metaWith = new SlimeMetaData(withSlime, facesWithSlime);

		metaWithout.addSlimeToGet = metaWith;
		metaWith.removeSlimeToGet = metaWithout;

		canGetSlimeOnIt.put(metaWithout.materialData, metaWithout);
		hasSlimeOnIt.put(metaWith.materialData, metaWith);
	}

	/**
	 * Special case, so that a cracked stone can turn into a mossy stone, but a
	 * mossy stone will always revert to a non-cracked stone
	 * 
	 * @param withoutSlime
	 * @param withSlime
	 * @param facesWithSlime
	 */
	private void defineUnidirectionalSlimeRelation(MaterialData withoutSlime, MaterialData withSlime, BlockFace... facesWithSlime) {
		SlimeMetaData metaWithout = new SlimeMetaData(withoutSlime);
		SlimeMetaData metaWith = new SlimeMetaData(withSlime, facesWithSlime);
		metaWithout.addSlimeToGet = metaWith;
		canGetSlimeOnIt.put(metaWithout.materialData, metaWithout);
	}

	public boolean canGetSlimeOnIt(Block block) {
		return canGetSlimeOnIt.containsKey(MaterialData.get(block));
	}

	public boolean canGetSlimeOnIt(Block block, BlockFace face) {
		if (canGetSlimeOnIt(block)) {
			return canGetSlimeOnIt.get(MaterialData.get(block)).addSlimeToGet.facesWithSlimeOnIt.contains(face);
		}
		return false;
	}

	public boolean hasSlimeOnIt(Block block) {
		return hasSlimeOnIt.containsKey(MaterialData.get(block));
	}

	public boolean hasSlimeOnIt(Block block, BlockFace face) {
		if (hasSlimeOnIt(block)) {
			return hasSlimeOnIt.get(MaterialData.get(block)).facesWithSlimeOnIt.contains(face);
		}
		return false;
	}

	public MaterialData withoutSlime(Block block) {
		return hasSlimeOnIt.get(MaterialData.get(block)).removeSlimeToGet.materialData;
	}

	public MaterialData withoutSlime(MaterialData materialData) {
		return hasSlimeOnIt.get(materialData).removeSlimeToGet.materialData;
	}

	public MaterialData withSlime(Block block) {
		return canGetSlimeOnIt.get(MaterialData.get(block)).addSlimeToGet.materialData;
	}

	public MaterialData withSlime(MaterialData materialData) {
		return canGetSlimeOnIt.get(materialData).addSlimeToGet.materialData;
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

		public SlimeMetaData(MaterialData materialData, BlockFace... sideWithSlimeOnIt) {
			this(materialData.material, materialData.data, sideWithSlimeOnIt);
		}

		public SlimeMetaData(Material material, byte data, BlockFace... sideWithSlimeOnIt) {
			this(material, data);
			facesWithSlimeOnIt = new HashSet<BlockFace>(Arrays.asList(sideWithSlimeOnIt));
		}

		public SlimeMetaData(MaterialData materialData) {
			this(materialData.material, materialData.data);
		}

		public SlimeMetaData(Material material, byte data) {
			materialData = MaterialData.get(material, data);
		}
	}
}
