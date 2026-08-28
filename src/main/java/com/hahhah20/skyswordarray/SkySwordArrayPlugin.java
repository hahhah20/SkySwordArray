package com.hahhah20.skyswordarray;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkySwordArrayPlugin extends JavaPlugin {
    private SkySwordSkill skill;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        skill = new SkySwordSkill(this);

        if (getCommand("skysword") != null) {
            getCommand("skysword").setExecutor((CommandSender sender, Command command, String label, String[] args) -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§c只有玩家可以释放天降剑阵。");
                    return true;
                }
                skill.cast(player);
                return true;
            });
        }
    }

    @Override
    public void onDisable() {
        if (skill != null) {
            skill.shutdown();
        }
    }
}
