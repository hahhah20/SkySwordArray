package com.hahhah20.skyswordarray;

import com.hahhah20.skyswordarray.skill.SkySwordSkill;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkySwordArrayPlugin extends JavaPlugin {
    private SkySwordSkill skill;
    public void onEnable(){
        saveDefaultConfig();
        skill=new SkySwordSkill(this);
    }
    public void onDisable(){
        if(skill!=null) skill.shutdown();
    }
}
