package com.hahhah20.skyswordarray.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class ImpactEffect {
    public static void play(Location loc){
        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 2);
        loc.getWorld().spawnParticle(Particle.CRIT, loc, 80, 1,0.5,1,0.2);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }
}
