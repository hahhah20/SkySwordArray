package com.hahhah20.skyswordarray;
import org.bukkit.command.*; import org.bukkit.entity.Player; import org.bukkit.plugin.java.JavaPlugin;
public final class SkySwordArrayPlugin extends JavaPlugin implements CommandExecutor {
 private SkySwordSkill skill;
 public void onEnable(){saveDefaultConfig(); skill=new SkySwordSkill(this); getCommand("skysword").setExecutor(this);}
 public void onDisable(){if(skill!=null)skill.shutdown();}
 public boolean onCommand(CommandSender s,Command c,String l,String[] a){if(!(s instanceof Player p)){s.sendMessage("Only players.");return true;} if(!p.hasPermission("skysword.use")){p.sendMessage("§c无权限");return true;} skill.cast(p);return true;}
}
