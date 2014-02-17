package me.jrneulight.dancingwalrus.DoubleJumper;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
  implements Listener
{
  public void onEnable()
  {
    getConfig().options().copyDefaults(true);
    saveConfig();
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if ((event.hasItem()) && (
      (event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))) {
      Player player = event.getPlayer();
      ItemStack item = event.getItem();
      if (item.getTypeId() == getConfig().getInt("settings.toggleTool")) {
        List enabled = getConfig().getStringList("settings.enabled");
        if (enabled.contains(player.getName())) {
          enabled.remove(player.getName());
          getConfig().set("settings.enabled", enabled);
          saveConfig();
          player.getItemInHand().removeEnchantment(Enchantment.ARROW_INFINITE);
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("general.disabledMessage")));
        } else {
          enabled.add(player.getName());
          getConfig().set("settings.enabled", enabled);
          saveConfig();
          player.getItemInHand().addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("general.enabledMessage")));
        }
      }
    }
  }

