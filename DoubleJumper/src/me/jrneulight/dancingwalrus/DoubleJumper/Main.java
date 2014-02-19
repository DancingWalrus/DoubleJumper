package me.jrneulight.dancingwalrus.DoubleJumper;

import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable()
  {
    saveDefaultConfig();
    saveConfig();
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if ((event.hasItem()) && ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))) {
      Player player = event.getPlayer();
      ItemStack item = event.getItem();
      if (item.getType().equals(Material.getMaterial(getConfig().getString("settings.toggleTool")))) {
        List<String> enabled = getConfig().getStringList("settings.enabled");
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
    if ((canJump) && (player.getGameMode() != GameMode.CREATIVE) && getConfig().getStringList("settings.worldsEnabled").contains(player.getWorld().getName()) || getConfig().getStringList("settings.worldsEnabled").contains("all") && player.hasPermission("doublejumper.use")) {
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
    else if ((canJump) && (player.getGameMode() != GameMode.CREATIVE) && (player.getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.AIR) && (!player.isFlying()) && getConfig().getStringList("settings.worldsEnabled").contains(player.getWorld().getName()) || getConfig().getStringList("settings.worldsEnabled").contains("all") && player.hasPermission("doublejumper.use"))
      player.setAllowFlight(true);
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event)
  {
    if (((event.getEntity() instanceof Player)) && (event.getCause() == EntityDamageEvent.DamageCause.FALL) && getConfig().getBoolean("settings.damageEnabled"))  {
      event.setCancelled(true);
      }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    if (getConfig().getBoolean("settings.speedBoost"))
      event.getPlayer().setWalkSpeed((float)getConfig().getDouble("settings.speed"));
  }

  public void sendHelpMessage(CommandSender sender)
  {
    sender.sendMessage(ChatColor.AQUA + "DoubleJumper || By DancingWalrus and Jrneulight");
    sender.sendMessage(ChatColor.AQUA + "Commands:");
    sender.sendMessage(ChatColor.AQUA + "/doublejumper reload");
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

