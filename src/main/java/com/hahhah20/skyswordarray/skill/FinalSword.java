package com.hahhah20.skyswordarray.skill;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FinalSword {
    public static void strike(Player player, Location loc){
        new SwordRain(player.getServer()
            .getPluginManager()
            .getPlugin("SkySwordArray"))
            .execute(player,loc);
    }
}
