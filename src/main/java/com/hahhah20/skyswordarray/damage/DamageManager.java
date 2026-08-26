package com.hahhah20.skyswordarray.damage;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DamageManager {
    public static void damage(Player player, Location loc, double radius, double amount){
        for(var entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)){
            if(entity instanceof LivingEntity living && entity != player){
                living.damage(amount, player);
            }
        }
    }
}
