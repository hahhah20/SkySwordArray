package com.hahhah20.skyswordarray.skill;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.bukkit.util.Transformation;

public final class FinalSword {
    private FinalSword() {}

    public static void strike(JavaPlugin plugin, Player player, Location target) {
        if (target == null || target.getWorld() == null || !player.isOnline()) return;

        World world = target.getWorld();
        Location start = target.clone().add(0, 28, 0);
        Location end = target.clone().add(0, 0.55, 0);
        ItemDisplay sword = world.spawn(start, ItemDisplay.class);
        sword.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
        sword.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        sword.setBillboard(Display.Billboard.FIXED);
        sword.setPersistent(false);
        sword.setTransformation(new Transformation(
                new Vector3f(-0.5f, -0.5f, -0.5f),
                new AxisAngle4f((float) Math.PI, 1, 0, 0),
                new Vector3f(2.2f, 2.2f, 2.2f),
                new AxisAngle4f()
        ));

        new BukkitRunnable() {
            int tick;
            final int duration = 32;

            @Override
            public void run() {
                if (!sword.isValid() || !player.isOnline() || player.isDead()) {
                    sword.remove();
                    cancel();
                    return;
                }

                double progress = Math.min(1.0, tick / (double) duration);
                double eased = progress * progress;
                Location pos = start.clone().add(
                        (end.getX() - start.getX()) * eased,
                        (end.getY() - start.getY()) * eased,
                        (end.getZ() - start.getZ()) * eased
                );
                sword.teleport(pos);
                world.spawnParticle(Particle.END_ROD, pos, 6, 0.12, 0.12, 0.12, 0.02);

                if (tick++ >= duration) {
                    impact(world, end, player);
                    sword.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void impact(World world, Location location, Player owner) {
        world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 1);
        world.spawnParticle(Particle.END_ROD, location, 120, 2.5, 0.6, 2.5, 0.12);
        world.spawnParticle(Particle.CRIT, location, 80, 2.0, 0.25, 2.0, 0.08);
        world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.2f, 1.45f);

        double radius = 6.0;
        double damage = 25.0;
        for (var entity : world.getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof LivingEntity living && living != owner) {
                living.damage(damage, owner);
            }
        }
    }
}
