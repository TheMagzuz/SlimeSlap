package me.themagzuz;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class PlayerListener implements Listener{
	
	public PlayerListener(SlimeSlap plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent e){
		Player player = (Player) e.getPlayer();
		
		SlimeSlap.perms.playerAdd(player, "SlimeSlap.Signs.Create.Sell");
		
		if (!SlimeSlap.HasSlimeSlapPlayer(player)){
			SlimeSlap.pl.getLogger().info(player.getName() + " does not have a SlimeSlapPlayer instance. Creating one");
			SlimeSlap.players.add(new SlimeSlapPlayer(player));
			SlimeSlap.pl.getLogger().info("Created a SlimeSlapPlayer instance for " + player.getName());
		}
	}
	
	/*@EventHandler
	public void OnPlayerDeath(PlayerDeathEvent e){
		Player killed = (Player) e.getEntity();
		Player killer;
		Integer toGive = SlimeSlap.getTickets(killed);
		killed.sendMessage(killed.getKiller().getType().toString());
		if(killed.getKiller().getType().equals(killed.getType())){
			killer = (Player) e.getEntity().getKiller();
		
		if (SlimeSlap.HasSlimeSlapPlayer(killed) && SlimeSlap.HasSlimeSlapPlayer(killer) && killer != null && killed != null){
			
			SlimeSlapPlayer killedSS = SlimeSlapPlayer.getSlimeSlapPlayer(killed);
			SlimeSlapPlayer killerSS = SlimeSlapPlayer.getSlimeSlapPlayer(killer);
			if (killerSS.getInSlimeSlap() && killedSS.getInSlimeSlap()){
					e.getDrops().clear();
					e.getDrops().add(SlimeSlap.killTicket);
					for (int i = 0; i < toGive; i++){
							e.getDrops().add(SlimeSlap.killTicket);
					
					}
					for (int i = 0; i < e.getDrops().size(); i++){
						killer.getInventory().addItem(e.getDrops().get(i));
					}
					e.getDrops().clear();
					Double x, y, z;
					try{
					x =(Double)SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.x"));
					y =(Double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.y"));
					z = (Double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.z"));
					} catch(ParseException er){
						killed.sendMessage("§cSome of the coordinates to the spawn are not numbers! Contact the server admin immidiatly!");
						return;
					}
					Location loc = new Location(SlimeSlap.GetWorldByName(SlimeSlap.pl.getConfig().getString("World")), x, y, z);
					killed.setHealth(20.0);
					killed.teleport(loc);
				}
			}
		}
	}*/
	
	@EventHandler
	public void OnEntityDamage(EntityDamageByEntityEvent e){
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			Player damaged = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			
			if (SlimeSlap.HasSlimeSlapPlayer(damaged) && SlimeSlap.HasSlimeSlapPlayer(damager)){
				SlimeSlapPlayer ssKiller = SlimeSlapPlayer.getSlimeSlapPlayer(damager);
				SlimeSlapPlayer ssKilled = SlimeSlapPlayer.getSlimeSlapPlayer(damager);
				ssKilled.SetLastDamage(damager);
				
			}
				
			if (damaged.getHealth() - e.getDamage() < 1){
				
				if (SlimeSlap.HasSlimeSlapPlayer(damaged) && SlimeSlap.HasSlimeSlapPlayer(damager)){
					SlimeSlapPlayer killed = SlimeSlapPlayer.getSlimeSlapPlayer(damaged);
					SlimeSlapPlayer killer = SlimeSlapPlayer.getSlimeSlapPlayer(damager);
					if(killed.getInSlimeSlap() && killer.getInSlimeSlap()){
						killed.SetLastDamage(damager);
						SlimeSlap.HandlePlayerDeath(damaged, killed, damager, killer, e);
						
						e.setCancelled(true);
					}
				}
			}
			
		}
	}
	
	@EventHandler
	public void OnEntityDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			
			Player player = (Player) e.getEntity();
			SlimeSlapPlayer ss = SlimeSlapPlayer.getSlimeSlapPlayer(player);
					if (ss.GetLastDamage() != null){
						
						if (player.getHealth() - e.getDamage() < 1){
							player.sendMessage("The error is probably in the player death handler");
							SlimeSlap.HandlePlayerDeath(player, ss, ss.GetLastDamage(), SlimeSlapPlayer.getSlimeSlapPlayer(ss.GetLastDamage()), e);
						}
					} else return;
				}
				
			}
			
		
	
	
	/*@EventHandler
	public void OnPlayerRespawn(PlayerRespawnEvent e){
		Player player = e.getPlayer();
		SlimeSlapPlayer ss = SlimeSlapPlayer.getSlimeSlapPlayer(player);
		if (ss != null && ss.getInSlimeSlap()){
			Double x, y, z;
			try{
			x =(Double)SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.x"));
			y =(Double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.y"));
			z = (Double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.z"));
			} catch(ParseException er){
				player.sendMessage("§cSome of the coordinates to the spawn are not numbers! Contact the server admin immidiatly!");
				return;
			}
			Location loc = new Location(SlimeSlap.GetWorldByName(SlimeSlap.pl.getConfig().getString("World")), x, y, z);
			e.setRespawnLocation(loc);
			player.teleport(loc);
		}
	}*/
	
	@EventHandler
	public void OnBlockRightClick(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player player = (Player) e.getPlayer();
			if (e.getClickedBlock().getState() instanceof Sign){
				
				
				
				Sign sign = (Sign) e.getClickedBlock().getState();
				
				if(sign.getLine(0).equalsIgnoreCase("§6[Ticket]")){
					int toGive = 0;
					int reward;
					double bonus = SlimeSlap.pl.getConfig().getDouble("Bonus");

					int tickets = SlimeSlap.getTickets(player);
					reward = Integer.parseInt(sign.getLine(1).substring(4, sign.getLine(1).length()));
					toGive+=tickets*reward;

					SlimeSlap.removeTickets(player);
					
					if (bonus != 0){
						toGive += bonus;
					}
					
					if (toGive > 0){
					
				    player.sendMessage(String.format(ChatColor.GREEN + "Traded in %s tickets for $%s", String.valueOf(tickets), SlimeSlap.DOUBLE_DECIMAL.format(toGive)));
					} else player.sendMessage("§cYou don't have any tickets!");
				    if (bonus != 0 && toGive > 0){
				    	player.sendMessage(ChatColor.AQUA + String.format("You were given $%s as a bonus", SlimeSlap.DOUBLE_DECIMAL.format(bonus)));
				    }
				    SlimeSlap.AddMoney(player, toGive);
				} else if (sign.getLine(0).equalsIgnoreCase(ChatColor.AQUA + "[SlimeSlap]")){
					if (sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Join")){	
						double x, y, z;
						try {
							x = (double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Arena.x")).doubleValue();
							y= (double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Arena.y")).doubleValue(); 
							z= (double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Arena.z")).doubleValue();
						} catch(ParseException er){
							player.sendMessage("§cSome of the coordinates to the arena are not numbers! Contact the server admin immidiately!");
							return;
						}
						Location loc = new Location(player.getWorld(), x, y, z);
						player.teleport(loc);
						SlimeSlapPlayer.getSlimeSlapPlayer(player).setInSlimeSlap(true);
						player.sendMessage(ChatColor.GREEN + "Joined Slime Slap!");
						player.getInventory().addItem(SlimeSlap.SlimeSlapper);
					} else if (sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Leave")){
						if (SlimeSlapPlayer.getSlimeSlapPlayer(player).getInSlimeSlap()){
							double x, y, z;
							World world;
							try {
								x = (double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.x")).doubleValue();
								y= (double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.y")).doubleValue(); 
								z= (double) SlimeSlap.DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.z")).doubleValue();
								world = SlimeSlap.GetWorldByName(SlimeSlap.pl.getConfig().getString("World"));
							} catch(ParseException er){
								player.sendMessage("§cSome of the coordinates to the arena are not numbers! Contact the server admin immidiately!");
								return;
							}
						
							Location loc;
						
							if (world != null){
								loc = new Location(world, x, y, z);
							} else {
								player.sendMessage(String.format("§cThe world \'%s\' does not exist", SlimeSlap.pl.getConfig().getString("World")));
								return;
							}
						
							player.teleport(loc);
							player.getInventory().remove(SlimeSlap.SlimeSlapper);
							SlimeSlapPlayer.getSlimeSlapPlayer(player).setInSlimeSlap(false);
						} else player.sendMessage("§cYou are not in Slime Slap!");
					}
				}
			}
		}
	}
	

	
	}

