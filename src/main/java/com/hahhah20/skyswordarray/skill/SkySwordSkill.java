package com.hahhah20.skyswordarray.skill;

import org.bukkit.plugin.java.JavaPlugin;

public class SkySwordSkill {
    private final SwordController controller;
    public SkySwordSkill(JavaPlugin plugin){
        controller=new SwordController(plugin);
    }
    public void shutdown(){
        controller.shutdown();
    }
}
