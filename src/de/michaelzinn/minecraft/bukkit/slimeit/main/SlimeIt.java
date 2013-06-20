package de.michaelzinn.minecraft.bukkit.slimeit.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SlimeIt extends JavaPlugin {

	@Override
	public void onEnable() {
		super.onEnable();
		Bukkit.getPluginManager().registerEvents(new BlockPunchListener(), this);
	}
}
