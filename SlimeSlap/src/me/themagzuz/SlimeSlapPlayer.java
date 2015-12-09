package me.themagzuz;

import java.io.Console;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SlimeSlapPlayer {
	@Deprecated
	private Player player;
	
	private UUID id;
	
	private boolean inSlimeSlap;
	
	@Deprecated
	private Player lastDamage;
	
	private UUID damager;
	
	private FileConfiguration config = SlimeSlap.pl.getConfig();
	private SlimeSlap pl = SlimeSlap.pl;
	private Logger logger = pl.getLogger();
	
	
	
	@Deprecated
	public SlimeSlapPlayer instance;
	@Deprecated
	public SlimeSlapPlayer(Player thePlayer, boolean isInSlimeSlap){
		
		if (SlimeSlap.HasSlimeSlapPlayer(thePlayer)){
			logger.severe("");
		}
		
		if (config.contains("Players."+thePlayer.getUniqueId().toString())){
			logger.severe("Attemped to create a slimeslap player instance for a player who already had one saved!");
		}
		
		
		player = thePlayer;
		inSlimeSlap = isInSlimeSlap;
		
	}
	@Deprecated
	public SlimeSlapPlayer(Player thePlayer){
		player = thePlayer;
		inSlimeSlap = false;
	}
	
	public SlimeSlapPlayer(UUID thePlayer, boolean inSS, UUID damager){
		if (Bukkit.getPlayer(thePlayer) != null){
			Player p = Bukkit.getPlayer(thePlayer);
			if (!SlimeSlap.HasSlimeSlapPlayer(p)){
				id = thePlayer;
				inSlimeSlap = inSS;
				this.damager = damager;
			} else {
				logger.severe(String.format("Tried to create a Slime Slap Player for a player that already has one! [%s]", p.getName()));
				
			}
		}
	}
	
	@Deprecated
	public Player getPlayer(){
		return player;
	}
	
	public boolean getInSlimeSlap(){
		return inSlimeSlap;
	}
	
	public void setInSlimeSlap(boolean set){
		inSlimeSlap = set;
		
	}
	@Deprecated
	public static SlimeSlapPlayer getSlimeSlapPlayer(Player toGet){
		for (int i = 0; i < SlimeSlap.players.size(); i++){
			if (SlimeSlap.players.get(i).getPlayer().equals(toGet)){
				return SlimeSlap.players.get(i);
			}
		}
		SlimeSlap.pl.getLogger().fine(toGet.getName() + " does not have a SlimeSlap players entry. Adding one");
		SlimeSlap.players.add(new SlimeSlapPlayer(toGet));
		return getSlimeSlapPlayer(toGet.getUniqueId());
	}
	
	public static SlimeSlapPlayer getSlimeSlapPlayer(UUID toGet){
		for (int i = 0; i < SlimeSlap.players.size(); i++){
			if (SlimeSlap.players.get(i).getPlayer().getUniqueId().equals(toGet)) return SlimeSlap.players.get(i);
			else continue;
		}
		if (SlimeSlap.pl.getConfig().contains("Players."+toGet)){

			LoadSlimeSlapPlayer(toGet);
			return getSlimeSlapPlayer(toGet);
		}
		return null;
	}
	@Deprecated
	public static void SetSlimeSlapPlayer(Player toGive){
		for (int i = 0; i < SlimeSlap.players.size(); i++){
			if (SlimeSlap.players.get(i).getPlayer().equals(toGive)){
				return;
			}
		}
		SlimeSlap.players.add(new SlimeSlapPlayer(toGive));
	}
	public Player GetLastDamage(){
		return lastDamage;
	}
	
	public void SetLastDamage(Player set){
		lastDamage = set;
	}
	public void ClearLastDamage(){
		lastDamage = null;
	}
	
	public static void LoadSlimeSlapPlayer(UUID player){
		String path = ("Players."+player);
		SlimeSlapPlayer Return;
		FileConfiguration cfg = SlimeSlap.pl.getConfig();
		Logger logger = SlimeSlap.pl.getLogger();
		boolean inSlimeSlap = cfg.getBoolean(path+".inSlimeSlap");
		UUID damage = UUID.fromString(cfg.getString(path+".lastDamage"));
		if (Bukkit.getPlayer(player) != null){
			if (!SlimeSlap.HasSlimeSlapPlayer(Bukkit.getPlayer(player))){
				Return = new SlimeSlapPlayer(player, inSlimeSlap, damage);
				SlimeSlapPlayer.SetSlimeSlapPlayer(player, Return);
			} else{
				logger.severe("Tried to load a Slime Slap Player for a player who already has one. Overriding will not occur");
				return;
			}
		}
	}
	
	public static void SetSlimeSlapPlayer(UUID id, SlimeSlapPlayer player){
		Logger logger = SlimeSlap.pl.getLogger();
		for (SlimeSlapPlayer slapper : SlimeSlap.players){
			if (slapper.getPlayer().getUniqueId().equals(id)){
				logger.severe("Tried to give a Slime Slap player to a player that already has one");
				return;
			}
			
		}
		LoadSlimeSlapPlayer (id);
		if (Bukkit.getPlayer(id) != null){
			if(!SlimeSlap.HasSlimeSlapPlayer(Bukkit.getPlayer(id))){
				SlimeSlap.players.add(new SlimeSlapPlayer(id, false, null));
			} 
				
			
		}
	}
}
