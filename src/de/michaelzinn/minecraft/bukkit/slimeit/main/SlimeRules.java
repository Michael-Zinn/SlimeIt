package de.michaelzinn.minecraft.bukkit.slimeit.main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import de.michaelzinn.minecraft.bukkit.slimeit.bukkitplus.Thing;

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

	private Map<Thing, SlimeMetaData> canGetSlimeOnIt = new Hashtable<Thing, SlimeMetaData>();
	private Map<Thing, SlimeMetaData> hasSlimeOnIt = new Hashtable<Thing, SlimeMetaData>();

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
				Thing.COBBLE_STONE_BLOCK,
				Thing.MOSSY_COBBLE_STONE_BLOCK, allFaces);
		defineBidirectionalSlimeRelation(
				Thing.COBBLE_STONE_WALL,
				Thing.MOSSY_COBBLE_STONE_WALL, allFaces);

		// Stone bricks
		defineBidirectionalSlimeRelation(
				Thing.STONE_BRICK_BLOCK,
				Thing.MOSSY_STONE_BRICK_BLOCK, allFaces);
		defineUnidirectionalSlimeRelation(
				Thing.CRACKED_STONE_BRICK_BLOCK,
				Thing.MOSSY_STONE_BRICK_BLOCK, allFaces);

		// Piston bases
		defineBidirectionalSlimeRelation(
				Thing.RETRACTED_NONSTICKY_PISTON_BASE_DOWN,
				Thing.RETRACTED_STICKY_PISTON_BASE_DOWN, BlockFace.DOWN);
		defineBidirectionalSlimeRelation(
				Thing.RETRACTED_NONSTICKY_PISTON_BASE_UP,
				Thing.RETRACTED_STICKY_PISTON_BASE_UP, BlockFace.UP);
		defineBidirectionalSlimeRelation(
				Thing.RETRACTED_NONSTICKY_PISTON_BASE_NORTH,
				Thing.RETRACTED_STICKY_PISTON_BASE_NORTH, BlockFace.NORTH);
		defineBidirectionalSlimeRelation(
				Thing.RETRACTED_NONSTICKY_PISTON_BASE_SOUTH,
				Thing.RETRACTED_STICKY_PISTON_BASE_SOUTH, BlockFace.SOUTH);
		defineBidirectionalSlimeRelation(
				Thing.RETRACTED_NONSTICKY_PISTON_BASE_WEST,
				Thing.RETRACTED_STICKY_PISTON_BASE_WEST, BlockFace.WEST);
		defineBidirectionalSlimeRelation(
				Thing.RETRACTED_NONSTICKY_PISTON_BASE_EAST,
				Thing.RETRACTED_STICKY_PISTON_BASE_EAST, BlockFace.EAST);

		// Piston extensions
		defineBidirectionalSlimeRelation(
				Thing.EXTENDED_NONSTICKY_PISTON_EXTENSION_DOWN,
				Thing.EXTENDED_STICKY_PISTON_EXTENSION_DOWN, BlockFace.DOWN);
		defineBidirectionalSlimeRelation(
				Thing.EXTENDED_NONSTICKY_PISTON_EXTENSION_UP,
				Thing.EXTENDED_STICKY_PISTON_EXTENSION_UP, BlockFace.UP);
		defineBidirectionalSlimeRelation(
				Thing.EXTENDED_NONSTICKY_PISTON_EXTENSION_NORTH,
				Thing.EXTENDED_STICKY_PISTON_EXTENSION_NORTH, BlockFace.NORTH);
		defineBidirectionalSlimeRelation(
				Thing.EXTENDED_NONSTICKY_PISTON_EXTENSION_SOUTH,
				Thing.EXTENDED_STICKY_PISTON_EXTENSION_SOUTH, BlockFace.SOUTH);
		defineBidirectionalSlimeRelation(
				Thing.EXTENDED_NONSTICKY_PISTON_EXTENSION_WEST,
				Thing.EXTENDED_STICKY_PISTON_EXTENSION_WEST, BlockFace.WEST);
		defineBidirectionalSlimeRelation(
				Thing.EXTENDED_NONSTICKY_PISTON_EXTENSION_EAST,
				Thing.EXTENDED_STICKY_PISTON_EXTENSION_EAST, BlockFace.EAST);
	}

	private void defineBidirectionalSlimeRelation(Thing withoutSlime, Thing withSlime, BlockFace... facesWithSlime) {
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
	private void defineUnidirectionalSlimeRelation(Thing withoutSlime, Thing withSlime, BlockFace... facesWithSlime) {
		SlimeMetaData metaWithout = new SlimeMetaData(withoutSlime);
		SlimeMetaData metaWith = new SlimeMetaData(withSlime, facesWithSlime);
		metaWithout.addSlimeToGet = metaWith;
		canGetSlimeOnIt.put(metaWithout.materialData, metaWithout);
	}

	public boolean canGetSlimeOnIt(Block block) {
		return canGetSlimeOnIt.containsKey(Thing.in(block));
	}

	public boolean canGetSlimeOnIt(Block block, BlockFace face) {
		if (canGetSlimeOnIt(block)) {
			return canGetSlimeOnIt.get(Thing.in(block)).addSlimeToGet.facesWithSlimeOnIt.contains(face);
		}
		return false;
	}

	public boolean hasSlimeOnIt(Block block) {
		return hasSlimeOnIt.containsKey(Thing.in(block));
	}

	public boolean hasSlimeOnIt(Block block, BlockFace face) {
		if (hasSlimeOnIt(block)) {
			return hasSlimeOnIt.get(Thing.in(block)).facesWithSlimeOnIt.contains(face);
		}
		return false;
	}

	public Thing withoutSlime(Block block) {
		return hasSlimeOnIt.get(Thing.in(block)).removeSlimeToGet.materialData;
	}

	public Thing withoutSlime(Thing materialData) {
		return hasSlimeOnIt.get(materialData).removeSlimeToGet.materialData;
	}

	public Thing withSlime(Block block) {
		return canGetSlimeOnIt.get(Thing.in(block)).addSlimeToGet.materialData;
	}

	public Thing withSlime(Thing materialData) {
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
		public Thing materialData;
		public Set<BlockFace> facesWithSlimeOnIt = new HashSet<BlockFace>();

		public SlimeMetaData addSlimeToGet;
		public SlimeMetaData removeSlimeToGet;

		public SlimeMetaData(Thing materialData, BlockFace... sideWithSlimeOnIt) {
			this(materialData.material, materialData.data, sideWithSlimeOnIt);
		}

		public SlimeMetaData(Material material, byte data, BlockFace... sideWithSlimeOnIt) {
			this(material, data);
			facesWithSlimeOnIt = new HashSet<BlockFace>(Arrays.asList(sideWithSlimeOnIt));
		}

		public SlimeMetaData(Thing materialData) {
			this(materialData.material, materialData.data);
		}

		public SlimeMetaData(Material material, byte data) {
			materialData = Thing.in(material, data);
		}
	}
}
