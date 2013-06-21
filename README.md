SlimeIt
=======

Minecraft Bukkit plugin that consolidates some redundant gameplay elements into one. For more details see https://github.com/RedNifre/SlimeIt/wiki/Slime-IT

How it works:
-------------
* BlockPunchListener monitors what the player does and deals with some special cases directly.
* The easy cases are handled by the rules you can see in SlimeRules.
* MaterialData is for convenience and combines bukkit's Material and Data into one. The project 
  has several bukkit simplifications, it might be better to move those to a separate 
  "BukkitSimplified" library one day.


