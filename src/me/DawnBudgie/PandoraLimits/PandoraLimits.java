package me.DawnBudgie.pandoralimits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PandoraLimits extends JavaPlugin
  implements Listener
{
  private YamlConfiguration config;
  private YamlConfiguration PEXconfig;
  private Logger log = Logger.getLogger("Minecraft");
  
  public void onEnable()
  {
    LoadConfig();
    Bukkit.getServer().getPluginManager().registerEvents(this, this);
  }
  
  public void LoadConfig()
  {
    File configFile = new File(getDataFolder(), "config.yml");
    this.config = YamlConfiguration.loadConfiguration(configFile);
    if (!configFile.exists())
    {
      this.config.set("Groups.mason", Integer.valueOf(20000));
      this.config.set("Groups.adept", Integer.valueOf(20000));
      this.config.set("Groups.artisan", Integer.valueOf(75000));
      this.config.set("Groups.helper", Integer.valueOf(75000));
      this.config.set("Groups.Legend", Integer.valueOf(300000));
      ArrayList<String> ignored = new ArrayList<String>();
      ignored.add("guest");
      this.config.set("Ignore", ignored);
      try
      {
        this.config.save(configFile);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    Plugin pex = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
    File pexFile = new File(pex.getDataFolder(), "permissions.yml");
    this.PEXconfig = YamlConfiguration.loadConfiguration(pexFile);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  private void onPlayerLogin(PlayerJoinEvent event)
  {
    Player player = event.getPlayer();
    
    @SuppressWarnings("unchecked")
	ArrayList<String> playergroups = (ArrayList<String>)this.PEXconfig.get("users." + player.getName() + ".group");
    String group = (String)playergroups.get(0);
    this.log.info("Player is of " + group);
    
    @SuppressWarnings("unchecked")
	List<String> ignorelist = (List<String>)this.config.get("Ignore");
    if (!ignorelist.contains(group))
    {
      String command = "pex user " + player.getName() + " add worldedit.limit";
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
      Integer blocklimit = Integer.valueOf(this.config.getInt("Groups." + group));
      player.chat("//limit " + blocklimit);
      String command2 = "pex user " + player.getName() + " remove worldedit.limit";
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command2);
    }
  }
}
