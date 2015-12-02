package me.themagzuz;

import org.bukkit.entity.Player;

public class SlimeSlapPlayer {
	
	private Player player;
	private boolean inSlimeSlap;
	
	public SlimeSlapPlayer(Player thePlayer, boolean isInSlimeSlap){
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
	
	public static void SetSlimeSlapPlayer(Player toGive){
		for (int i = 0; i < SlimeSlap.players.size(); i++){
			if (SlimeSlap.players.get(i).getPlayer().equals(toGive)){
				return;
			}
		}
		SlimeSlap.players.add(new SlimeSlapPlayer(toGive));
	}
}
