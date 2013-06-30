package de.michaelzinn.minecraft.bukkit.slimeit.main;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Michael Zinn (@RedNifre)
 * 
 */
public class SlimeIt extends JavaPlugin {
	public Logger log;

	@Override
	public void onEnable() {
		super.onEnable();
		log = getLogger();
		Bukkit.getPluginManager().registerEvents(new BlockPunchListener(this), this);
	}
}
