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
  @EventHandler
  public void onPlayerToggleFlight(PlayerToggleFlightEvent event)
  {
    Player player = event.getPlayer();
    boolean canJump = getConfig().getStringList("settings.enabled").contains(player.getName());
    if ((canJump) && (player.getGameMode() != GameMode.CREATIVE)) {
      event.setCancelled(true);
      player.setAllowFlight(false);
      player.setFlying(false);
      player.setVelocity(player.getLocation().getDirection().multiply(getConfig().getDouble("settings.velocityMultiply")).setY(getConfig().getDouble("settings.velocityHeight")));
    }
  }
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    boolean canJump = getConfig().getStringList("settings.enabled").contains(player.getName());
    if ((!canJump) && (player.getGameMode() != GameMode.CREATIVE))
      player.setFlying(false);
    else if ((canJump) && (player.getGameMode() != GameMode.CREATIVE) && (player.getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.AIR) && (!player.isFlying()))
      player.setAllowFlight(true);
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event)
  {
    if (((event.getEntity() instanceof Player)) && 
      (event.getCause() == EntityDamageEvent.DamageCause.FALL))
      event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    if (getConfig().getBoolean("settings.speedBoost"))
      event.getPlayer().setWalkSpeed((float)getConfig().getDouble("settings.speed"));
  }

  public void sendHelpMessage(CommandSender sender)
  {
    sender.sendMessage("§bDoubleJumper || By DancingWalrus and Jrneulight");
    sender.sendMessage("§bCommands:");
    sender.sendMessage("§b/doublejumper reload");
  }

  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (cmd.getName().equalsIgnoreCase("doublejumper")) {
      if (args.length == 0)
        sendHelpMessage(sender);
      else if (args[0].equalsIgnoreCase("reload")) {
        if (sender.hasPermission("doublejumper.reload")) {
          reloadConfig();
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("general.reloadMessage")));
        } else {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("general.noPermMessage")));
        }
      }
      else sendHelpMessage(sender);
    }

    return true;
  }
}

