package com.hahhah20.skyswordarray.skill;

import com.hahhah20.skyswordarray.damage.DamageManager;
import com.hahhah20.skyswordarray.effect.ImpactEffect;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SwordRain {
    private final JavaPlugin plugin;

    public SwordRain(JavaPlugin plugin){
        this.plugin=plugin;
    }

    public void execute(Player player, Location target){
        for(int i=0;i<40;i++){
            double angle=Math.PI*2*i/40;
            Location start=target.clone().add(
                Math.cos(angle)*15,25,Math.sin(angle)*15
            );
            drop(player,start,target);
        }
    }

    private void drop(Player player,Location start,Location end){
        ItemDisplay sword=start.getWorld().spawn(start,ItemDisplay.class);
        sword.setItemStack(new ItemStack(Material.NETHERITE_SWORD));

        new BukkitRunnable(){
            int tick;
            public void run(){
                if(tick>=35){
                    Location loc=sword.getLocation();
                    ImpactEffect.play(loc);
                    DamageManager.damage(player,loc,4,12);
                    sword.remove();
                    cancel();
                    return;
                }
                double p=tick/35D;
                sword.teleport(start.clone().add(
                    (end.getX()-start.getX())*p*p,
                    (end.getY()-start.getY())*p*p,
                    (end.getZ()-start.getZ())*p*p
                ));
                sword.getWorld().spawnParticle(
                    Particle.CRIT,sword.getLocation(),3
                );
                tick++;
            }
        }.runTaskTimer(plugin,0,1);
    }

    public void shutdown(){}
}
