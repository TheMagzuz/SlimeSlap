package me.themagzuz;

import java.math.BigDecimal;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class BlockListener implements Listener{
	
	public BlockListener(SlimeSlap plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	
	@EventHandler
	public void OnSignChange(SignChangeEvent e){
		Player player = (Player) e.getPlayer();
		
		
		if(player == null) return; 
		
		if (e.getLine(0).equalsIgnoreCase("[Ticket]")){
		
			if (SlimeSlap.perms.has(player, SlimeSlap.TicketSign.getName())){
			
				int reward = 0;
				try{
					reward = Integer.parseInt(e.getLine(1));
				} catch (Exception er){
					player.sendMessage(String.format("§4\'%s\' is not a valid number! Reward goes here", e.getLine(1)));
					e.setCancelled(true);
					return;
				}
				e.setLine(1, String.format("§6$ %s", reward));
				e.setLine(0, "§6[Ticket]");
			} else{
				player.sendMessage(SlimeSlap.NoPerm);
				e.setCancelled(true);	
		}
		} else if (e.getLine(0).equalsIgnoreCase("[SlimeSlap]")){
			if (SlimeSlap.perms.has(player, SlimeSlap.ArenaSign.getName())){
				if (e.getLine(1).equalsIgnoreCase("Join")){
					e.setLine(0, ChatColor.AQUA + "[SlimeSlap]");
					e.setLine(1, ChatColor.GREEN + "Join");
				} else if (e.getLine(1).equalsIgnoreCase("Leave")){
					e.setLine(0, ChatColor.AQUA + "[SlimeSlap]");
					e.setLine(1, ChatColor.GREEN + "Leave");
				}
			} else{ 
				player.sendMessage(SlimeSlap.NoPerm);
				e.setCancelled(true);
			}
		}
		
	}
	
	
	}

