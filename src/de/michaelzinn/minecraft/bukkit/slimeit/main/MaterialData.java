package de.michaelzinn.minecraft.bukkit.slimeit.main;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Useful to express things like "mossy cobblestone wall"
 * 
 * @author michael
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
		// Questionable, but can be fixed later, since it doesn't get persisted
		// (at least I hope so)
		return (material.getId() << 8) | data;
	}
}
