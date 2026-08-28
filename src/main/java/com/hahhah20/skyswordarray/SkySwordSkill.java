package com.hahhah20.skyswordarray;

import com.hahhah20.skyswordarray.skill.FinalSword;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SkySwordSkill {
    private final JavaPlugin plugin;
    private final Map<UUID, Long> cd = new ConcurrentHashMap<>();
    private final Set<BukkitTask> tasks = ConcurrentHashMap.newKeySet();
    private final double range, ar, sh, damage, ir;
    private final int charge, count, waves, interval, fall, delay;

    public SkySwordSkill(JavaPlugin p) {
        plugin = p;
        var c = p.getConfig();
        range = c.getDouble("skill.range", 32);
        ar = c.getDouble("visual.array-radius", 7);
        charge = Math.max(1, c.getInt("skill.charge-ticks", 40));
        count = Math.max(1, c.getInt("skill.sword-count", 24));
        waves = Math.max(1, c.getInt("skill.waves", 3));
        interval = Math.max(1, c.getInt("skill.wave-interval-ticks", 8));
        sh = c.getDouble("skill.sword-height", 18);
        fall = Math.max(1, c.getInt("skill.sword-fall-ticks", 14));
        damage = c.getDouble("skill.damage", 8);
        ir = c.getDouble("skill.impact-radius", 2.5);
        delay = Math.max(0, c.getInt("skill.final-delay-ticks", 12));
    }

    public void cast(Player p) {
        if (!p.isOnline() || p.isDead()) return;
        long now = System.currentTimeMillis();
        long last = cd.getOrDefault(p.getUniqueId(), 0L);
        long cool = (long) (plugin.getConfig().getDouble("skill.cooldown-seconds", 5) * 1000L);
        if (now - last < cool) {
            p.sendActionBar("§c冷却中");
            return;
        }
        Block b = p.getTargetBlockExact((int) Math.ceil(range));
        if (b == null) {
            p.sendActionBar("§c请对准目标");
            return;
        }
        cd.put(p.getUniqueId(), now);
        new Cast(p, b.getLocation().add(.5, 1, .5)).start();
    }

    private final class Cast extends BukkitRunnable {
        final Player p;
        final Location c;
        int t;

        Cast(Player p, Location c) {
            this.p = p;
            this.c = c;
        }

        void start() {
            tasks.add(runTaskTimer(plugin, 0L, 1L));
        }

        @Override
        public void run() {
            if (!p.isOnline() || p.isDead() || c.getWorld() == null) {
                stop();
                return;
            }
            if (t++ < charge) {
                array(t);
                return;
            }
            stop();
            rain();
            BukkitTask finalTask = new BukkitRunnable() {
                @Override
                public void run() {
                    tasks.remove(this);
                    if (p.isOnline() && !p.isDead() && c.getWorld() != null) {
                        FinalSword.strike(plugin, p, c);
                    }
                }
            }.runTaskLater(plugin, delay);
            tasks.add(finalTask);
        }

        void rain() {
            Random r = new Random();
            for (int w = 0; w < waves; w++) {
                int wi = w;
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        tasks.remove(this);
                        if (!p.isOnline() || p.isDead() || c.getWorld() == null) return;
                        int a = count * wi / waves;
                        int b = count * (wi + 1) / waves;
                        for (int i = a; i < b; i++) {
                            double ang = 2 * Math.PI * i / count + r.nextDouble() * .12;
                            double rad = .7 + r.nextDouble() * Math.max(.1, ar - .7);
                            fall(
                                    c.clone().add(Math.cos(ang) * rad, sh + r.nextDouble() * 2, Math.sin(ang) * rad),
                                    c.clone().add(Math.cos(ang) * rad, .35, Math.sin(ang) * rad),
                                    .9f, fall, damage, ir, false
                            );
                        }
                    }
                }.runTaskLater(plugin, (long) wi * interval);
                tasks.add(task);
            }
        }

        void fall(Location s, Location e, float scale, int dur, double dmg, double rad, boolean fin) {
            if (s.getWorld() == null || e.getWorld() == null) return;
            ItemDisplay d = s.getWorld().spawn(s, ItemDisplay.class);
            d.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
            d.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
            d.setBillboard(Display.Billboard.FIXED);
            d.setPersistent(false);
            d.setTransformation(new Transformation(
                    new Vector3f(-.5f, -.5f, -.5f),
                    new AxisAngle4f((float) Math.PI, 1, 0, 0),
                    new Vector3f(scale, scale, scale),
                    new AxisAngle4f()
            ));
            BukkitTask task = new BukkitRunnable() {
                int age;

                @Override
                public void run() {
                    if (!d.isValid() || !p.isOnline() || p.isDead()) {
                        d.remove();
                        stop();
                        return;
                    }
                    double q = Math.min(1, age / (double) Math.max(1, dur));
                    double z = q * q;
                    Location n = s.clone().add(
                            (e.getX() - s.getX()) * z,
                            (e.getY() - s.getY()) * z,
                            (e.getZ() - s.getZ()) * z
                    );
                    d.teleport(n);
                    World w = n.getWorld();
                    w.spawnParticle(fin ? Particle.END_ROD : Particle.CRIT, n, fin ? 7 : 2, .08, .12, .08, .02);
                    if (age++ >= dur) {
                        impact(n, dmg, rad, fin);
                        d.remove();
                        stop();
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
            tasks.add(task);
        }

        void impact(Location l, double dmg, double rad, boolean fin) {
            World w = l.getWorld();
            if (w == null) return;
            w.spawnParticle(fin ? Particle.EXPLOSION_EMITTER : Particle.EXPLOSION, l, fin ? 1 : 2);
            w.spawnParticle(Particle.CRIT, l, fin ? 100 : 25, fin ? 2.5 : .7, .2, fin ? 2.5 : .7, .2);
            w.playSound(l, fin ? Sound.ENTITY_LIGHTNING_BOLT_THUNDER : Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, fin ? 1.6f : .7f);
            for (Entity e : w.getNearbyEntities(l, rad, rad, rad)) {
                if (e instanceof LivingEntity v && v != p) v.damage(dmg, p);
            }
        }

        void array(int tick) {
            World w = c.getWorld();
            if (w == null) return;
            double rot = tick * .08;
            int n = Math.max(8, plugin.getConfig().getInt("visual.ring-points", 48));
            for (int i = 0; i < n; i++) {
                double a = rot + 2 * Math.PI * i / n;
                w.spawnParticle(Particle.END_ROD, c.clone().add(Math.cos(a) * ar, sh, Math.sin(a) * ar), 1);
            }
            for (int i = 0; i < 32; i++) {
                double a = 2 * Math.PI * i / 32;
                double r = ar * tick / (double) charge;
                w.spawnParticle(Particle.CRIT, c.clone().add(Math.cos(a) * r, .05, Math.sin(a) * r), 1);
            }
        }

        void stop() {
            tasks.remove(this);
            cancel();
        }
    }

    public void shutdown() {
        for (BukkitTask t : tasks) t.cancel();
        tasks.clear();
    }
}
