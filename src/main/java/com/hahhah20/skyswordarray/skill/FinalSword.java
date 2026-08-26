package com.hahhah20.skyswordarray.skill;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class FinalSword {


    public static void strike(
            JavaPlugin plugin,
            Player player,
            Location loc
    ){


        new SwordRain(plugin)
                .execute(
                        player,
                        loc
                );


    }


}