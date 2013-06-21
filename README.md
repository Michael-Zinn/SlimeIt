SlimeIt
=======

Minecraft Bukkit plugin that consolidates some redundant gameplay elements into one.
For information what this is and what you should do with it see the wiki on github:
https://github.com/RedNifre/SlimeIt/wiki

Implementation overview:
------------------------
* BlockPunchListener monitors what the player does and deals with some special cases directly.
* The easy cases are handled by the rules you can see in SlimeRules.
* MaterialData is for convenience and combines bukkit's Material and Data into one. The project 
  has several bukkit simplifications, it might be better to move those to a separate 
  "BukkitSimplified" library one day.


