package com.hahhah20.skyswordarray.effect;

import org.bukkit.Location;
import org.bukkit.Particle;

public class ChargeEffect {
    public static void play(Location loc,double radius,int tick){
        double angle=tick*0.15;
        for(int i=0;i<40;i++){
            double a=angle+Math.PI*2*i/40;
            loc.getWorld().spawnParticle(
                Particle.END_ROD,
                loc.clone().add(Math.cos(a)*radius,0.2,Math.sin(a)*radius),
                1
            );
        }
    }
}
