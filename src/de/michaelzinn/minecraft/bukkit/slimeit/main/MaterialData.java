package de.michaelzinn.minecraft.bukkit.slimeit.main;

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
 * @author Michael Zinn (@RedNifre)
 * 
 */
public class MaterialData {
	public Material material;
	public byte data;

	public MaterialData(Material material, byte data) {
		this.material = material;
		this.data = data;
	}

	public static MaterialData from(Block block) {
		return new MaterialData(block.getType(), block.getData());
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
}
