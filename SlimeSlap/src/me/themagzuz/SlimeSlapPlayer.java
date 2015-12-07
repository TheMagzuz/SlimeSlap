package me.themagzuz;

import java.io.Console;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SlimeSlapPlayer {
	
	private Player player;
	private boolean inSlimeSlap;
	private Player lastDamage;
	
	private FileConfiguration config = SlimeSlap.pl.getConfig();
	private SlimeSlap pl = SlimeSlap.pl;
	private Logger logger = pl.getLogger();
	
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
	public SlimeSlapPlayer(Player thePlayer){
		player = thePlayer;
		inSlimeSlap = false;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public boolean getInSlimeSlap(){
		return inSlimeSlap;
	}
	
	public void setInSlimeSlap(boolean set){
		inSlimeSlap = set;
		
	}
	
	public static SlimeSlapPlayer getSlimeSlapPlayer(Player toGet){
		for (int i = 0; i < SlimeSlap.players.size(); i++){
			if (SlimeSlap.players.get(i).getPlayer().equals(toGet)){
				return SlimeSlap.players.get(i);
			}
		}
		SlimeSlap.pl.getLogger().fine(toGet.getName() + " does not have a SlimeSlap players entry. Adding one");
		SlimeSlap.players.add(new SlimeSlapPlayer(toGet));
		return getSlimeSlapPlayer(toGet);
	}
	
	public static SlimeSlapPlayer getSlimeSlapPlayer(UUID toGet){
		for (int i = 0; i < SlimeSlap.players.size(); i++){
			if (SlimeSlap.players.get(i).getPlayer().getUniqueId().equals(toGet));
		}
	}
	
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
		
	}
}
