package me.themagzuz;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;



@SuppressWarnings({"deprecation" })
public class SlimeSlap extends JavaPlugin{
	
	public static List<SlimeSlapPlayer> players = new ArrayList<SlimeSlapPlayer>();
	
	public static SlimeSlap pl;
	
	public static ItemStack killTicket = new ItemStack(Material.GOLD_INGOT, 1);
	
	public static ItemStack SlimeSlapper = new ItemStack(Material.SLIME_BALL, 1);
	
	public static Economy econ;
	
	public static Permission perms;
	
	public static Integer bonus;
	
	public static final Logger log = Logger.getLogger("Minecraft");
	
	public static final ItemStack EMPTYITEM = null;
	
	public static final DecimalFormat DOUBLE_DECIMAL = new DecimalFormat("#.##");
	
	public static EconomyResponse r;
	
	public Configuration config = getConfig();
	
	//////////*PERMISSIONS*//////////
	public static org.bukkit.permissions.Permission TicketSign = new org.bukkit.permissions.Permission("SlimeSlap.Sign.Create.Sell");
	public static org.bukkit.permissions.Permission ArenaSign = new org.bukkit.permissions.Permission("SlimeSlap.Sign.Create.Arena");
	public static org.bukkit.permissions.Permission ArenaPerm = new org.bukkit.permissions.Permission("SlimeSlap.Command.Arena");
	public static org.bukkit.permissions.Permission AdminPerm = new org.bukkit.permissions.Permission("SlimeSlap.Command.Admin");
	//////////*END PERMISSIONS*//////////
	
	/////////*LANG*/////////
	public static String NoPerm = "§cYou do not have permission to do that!";
	/////////*END LANG*/////////
	public void LoadConfig(){
		
		if (!config.contains("Bonus")){
			config.set("Bonus", 10);
		}
		
		bonus = config.getInt("Bonus");
		
		saveConfig();
		
	}
	
	public static World GetWorldByName(String name){
		ArrayList<World> worlds = (ArrayList<World>) Bukkit.getServer().getWorlds();
		for (int i = 0; i < worlds.size(); i++){
			if (worlds.get(i).getName().equals(name)){
				return worlds.get(i);
			}
		}
		return null;
	}
	
	public static void HandlePlayerDeath(UUID died, SlimeSlapPlayer ssKilled, UUID theKiller, SlimeSlapPlayer ssKiller){
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.clear();
		
		Player killer = Bukkit.getPlayer(theKiller);
		Player killed = Bukkit.getPlayer(died);
		
		drops.add(killTicket);
		
		for (int i = 0; i < getTickets(killed); i++){
			drops.add(killTicket);
			killer.sendMessage(String.valueOf(i));
		}
		for (int i = 0; i < drops.size(); i++){
			killer.getInventory().addItem(drops.get(i));
		}
		
		Double x, y, z;
		try{
			x =(Double)DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.x")).doubleValue();
			y =(Double) DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.y")).doubleValue();
			z = (Double) DOUBLE_DECIMAL.parse(SlimeSlap.pl.getConfig().getString("Spawn.z")).doubleValue();
		} catch(ParseException er){
			killed.sendMessage("§cSome of the coordinates to the spawn are not numbers! Contact the server admin immidiatly!");
			return;
		}
		Location loc = new Location(GetWorldByName(pl.getConfig().getString("World")), x, y, z);
		killed.teleport(loc);
		killed.setHealth(20.0);
		killed.sendMessage("§cYou died! You lost all of your tickets!");
		killed.getInventory().clear();
	}

	
	private void LoadSlimeSlapPlayers(){
		for (SlimeSlapPlayer p : players){
			SlimeSlapPlayer.LoadSlimeSlapPlayer(p.getUUID());
		}
		for (Player p : Bukkit.getOnlinePlayers()){
			if (HasSlimeSlapPlayer(p)) continue;
			else if (SlimeSlapPlayer.hasSavedPlayer(p.getUniqueId())){ 
				SlimeSlapPlayer.LoadSlimeSlapPlayer(p.getUniqueId());
				log.info(String.format("%s has a saved Slime Slap Player that will be loaded", p.getName()));
			} else{ 
				new SlimeSlapPlayer(p.getUniqueId());
				log.info(String.format("%s does not have a Slime Slap Player instance, a new one has been created", p.getName()));
			}
		}
	}
	
	private void InitializeSlimeSlapper(){
		ItemMeta meta = SlimeSlapper.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Slime Slapper");
		meta.addEnchant(Enchantment.KNOCKBACK, 1337, true);
		
		SlimeSlapper.setItemMeta(meta);
	}
	
	public static boolean HasSlimeSlapPlayer(Player player){
		

		
		for (int i = 0; i < players.size(); i++){
			
			if(players.get(i).getPlayer().equals(player)){
				return true;
			}
		}
		if (pl.getConfig().contains(player.getUniqueId().toString())){
			
			return true;
		}
		return false;
	}
	
	public static void AddMoney(Player player, double amount){
        r = econ.depositPlayer(player.getName(), amount);
        if (r.transactionSuccess()) {
            return;
        }
        else {
            player.sendMessage(ChatColor.RED + "An error occured when paying out your money. Please contact a server admin.");
            return;
        }
	}
	
	public void InitializeRewardItem(){
		ItemMeta meta = killTicket.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		String toAdd = (ChatColor.DARK_PURPLE + "Turn this in to get money");
		
		lore.add(toAdd);
		
		meta.setLore(lore);
		meta.setDisplayName(ChatColor.AQUA + "Kill Ticket");
		
		killTicket.setItemMeta(meta);
		
	}
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	/////////////*OnEnable*/////////////
	@Override
	@SuppressWarnings({"unused"})
	public void onEnable(){
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        InitializeSlimeSlapper();
		LoadConfig();
		new PlayerListener(this);
		new BlockListener(this);
		InitializeRewardItem();
		pl = this;
		PluginManager pm = getServer().getPluginManager();
		setupPermissions();
		DOUBLE_DECIMAL.setMinimumFractionDigits(2);
		LoadSlimeSlapPlayers();
		getLogger().info("Loaded SlimeSlap successfully!");
		}
	/////////////*END OnEnable*/////////////
	@Override
	public void onDisable(){
		
	}
	
	public static int getTickets(Player player){
		int count = 0;
		PlayerInventory inv = player.getInventory();
		ItemStack[] items = inv.getContents();
	    for (ItemStack item : items)
        {
            if ((item != null) && (item.getItemMeta().equals(killTicket.getItemMeta())) && (item.getAmount() > 0))
            {
                count += item.getAmount();
            }
        }
		return count;
	}
	
	public static void removeTickets(Player player){
	
	PlayerInventory inv = player.getInventory();
	ItemStack[] items = inv.getContents();
	
	int i = 0;
	
	
	
    for (ItemStack item : items)
    {
        if ((item != null) && (item.getItemMeta().equals(killTicket.getItemMeta())) && (item.getAmount() > 0))
        {
        
        	player.getInventory().setItem(i, EMPTYITEM);
        	
        }
        i++;
    }
	
		
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
        
    }
	
    
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		// Commands that require to be executed by a player
		if (sender instanceof Player){
			
			Player player = (Player) sender;
			
			//TODO: Rewrite the command system, so that it's more effective
			if (cmd.getName().equalsIgnoreCase("SlimeSlap") || cmd.getName().equalsIgnoreCase("ss")){
				if (args.length == 0){
					player.sendMessage(ChatColor.GREEN + "----------SlimeSlap by TheMagzuz----------");
					player.sendMessage(ChatColor.GREEN + "Do " + ChatColor.RED + "/slimeslap help" + ChatColor.GREEN + " to see availible commands");
					player.sendMessage(ChatColor.GREEN + "Version " + this.getDescription().getVersion());
					if (getDescription().getVersion().contains("DEV")){
						player.sendMessage(ChatColor.GREEN + "This is a development version! This is for testing, not for servers!");
					}
				} else if (args.length == 1){
					if (args[0].equalsIgnoreCase("help")){
						player.performCommand("help slimeslap");
						//TODO: Add an actual help subcommand
					} else if(args[0].equalsIgnoreCase("get")){
						player.sendMessage(ChatColor.GREEN + "Your Slime Slap state is: " + SlimeSlapPlayer.getSlimeSlapPlayer(player.getUniqueId()).getInSlimeSlap());
					} else if (args[0].equalsIgnoreCase("set")){
						if (perms.has(sender, AdminPerm.getName()))
							player.sendMessage(ChatColor.RED + "Usage: /slimeslap set <player> [true:false]");
						else player.sendMessage(NoPerm);
					} else if (args[0].equalsIgnoreCase("ticket")){
						player.getInventory().addItem(killTicket);
					} else if (args[0].equalsIgnoreCase("setarena")){
						if (perms.has(player, ArenaPerm.getName())){
							
						String x = DOUBLE_DECIMAL.format(player.getLocation().getX()), y = DOUBLE_DECIMAL.format(player.getLocation().getY()), z = DOUBLE_DECIMAL.format(player.getLocation().getZ());
						getConfig().set("Arena.x", x);
						getConfig().set("Arena.y", y);
						getConfig().set("Arena.z", z);
						
						player.sendMessage(String.format(ChatColor.GREEN + "Set arena to be at X:%s, Y:%s, Z:%s", x, y, z));
						
						saveConfig();
						reloadConfig();
						} else player.sendMessage(NoPerm);
					} else if (args[0].equalsIgnoreCase("getarena")){
						if (perms.has(player, AdminPerm.getName())){
						player.sendMessage(String.format("%s, %s, %s", config.getString("Arena.x"), config.getString("Arena.y"), config.getString("Arena.z")));
							}
						} else if (args[0].equalsIgnoreCase("setspawn")){
								if (perms.has(player, AdminPerm.getName())){
								String x = DOUBLE_DECIMAL.format(player.getLocation().getX()), y = DOUBLE_DECIMAL.format(player.getLocation().getY()), z = DOUBLE_DECIMAL.format(player.getLocation().getZ());
								getConfig().set("Spawn.x", x);
								getConfig().set("Spawn.y", y+1);
								getConfig().set("Spawn.z", z);
								
					
								
								
								
								player.sendMessage(String.format(ChatColor.GREEN + "Set Slime Slap spawn to be at X:%s, Y:%s, Z:%s", x, y, z));
							
								saveConfig();
								reloadConfig();
								} else player.sendMessage(NoPerm);
						} else if (args[0].equalsIgnoreCase("setworld")){
							String name = player.getWorld().getName();
							getConfig().set("World", name);
							saveConfig();
							reloadConfig();
						} else if (args[0].equalsIgnoreCase("setbonus")){
							if (perms.has(player, AdminPerm.getName())){
							player.sendMessage("§4Invalid usage");
							player.sendMessage("§4Proper usage is: §c/ss setbonus <bonus>");
							} else player.sendMessage(NoPerm);
						}
							
					else{
						player.sendMessage(ChatColor.DARK_RED + "Unknown subcommand do " + ChatColor.RED + "/slimeslap help" + ChatColor.DARK_RED + " to see availible commands");
					}
				} else if (args.length == 2){
					if (args[0].equalsIgnoreCase("set")){
						if (perms.has(sender, AdminPerm.getName())){
						if(Bukkit.getPlayer(args[1]) != null){
							Player target = Bukkit.getPlayer(args[1]);
							SlimeSlapPlayer targetSlimeSlap;
							targetSlimeSlap = SlimeSlapPlayer.getSlimeSlapPlayer(target.getUniqueId());
							targetSlimeSlap.setInSlimeSlap(!targetSlimeSlap.getInSlimeSlap());
							player.sendMessage("Toggled the Slime Slap state of " + target.getDisplayName() + " to " + targetSlimeSlap.getInSlimeSlap());
						} else player.sendMessage(String.format("§c\'%s\' is not a valid player", args[1]));
						} else player.sendMessage(NoPerm);
					} else if(args[0].equalsIgnoreCase("add")){
						if (perms.has(sender, AdminPerm.getName())){
						if (Bukkit.getPlayer(args[1]) != null){
							Player target = Bukkit.getPlayer(args[1]);
							SlimeSlapPlayer.SetSlimeSlapPlayer(target);
							player.sendMessage(ChatColor.GREEN + "Added a Slime Slap Player to " + target.getDisplayName());
						}
						} else player.sendMessage(NoPerm);
					} else if (args[0].equalsIgnoreCase("get")){
						if (Bukkit.getPlayer(args[1]) != null){
							player.sendMessage(ChatColor.GREEN + "The Slime Slap state of " + Bukkit.getPlayer(args[1]).getName() + " is " + SlimeSlapPlayer.getSlimeSlapPlayer(Bukkit.getPlayer(args[1])).getInSlimeSlap());
						} else{
							player.sendMessage(ChatColor.RED + "Player not found");
						}
					} else if (args[0].equalsIgnoreCase("ticket")){
						if (perms.has(sender, AdminPerm.getName())){
						if (Bukkit.getPlayer(args[1]) != null){
							Bukkit.getPlayer(args[1]).getInventory().addItem(killTicket);
							player.sendMessage(String.format(ChatColor.GREEN + "Gave %s a ticket", args[1]));
						} else {
							player.sendMessage(String.format("§cPlayer \'%s\' not found!", args[1]));
						}
						} else player.sendMessage(NoPerm);
					} else if (args[0].equalsIgnoreCase("setbonus")){
						if (perms.has(player, AdminPerm.getName())){
							double set;
							try{
								set = Double.parseDouble(args[1]);
							} catch (Exception e){
								player.sendMessage(String.format("§c\'%s\' is not a valid number", args[1]));
								return true;
							}
							getConfig().set("Bonus", set);
							saveConfig();
							reloadConfig();
							player.sendMessage(String.format("§aThe bonus is now §b%s", set));
						}
					}
				} else if (args.length == 3){
					if (args[0].equalsIgnoreCase("set") && Bukkit.getPlayer(args[1]) != null){
						if (perms.has(sender, AdminPerm.getName())){
						if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")){
						boolean set = Boolean.parseBoolean(args[2]);
						
						if (Bukkit.getPlayer(args[1]) != null){
						Player target = Bukkit.getPlayer(args[1]);
						SlimeSlapPlayer targetSlimeSlap = SlimeSlapPlayer.getSlimeSlapPlayer(target.getUniqueId());
						if(targetSlimeSlap != null){
						targetSlimeSlap.setInSlimeSlap(set);
						player.sendMessage("Set the Slime Slap state of " + target.getDisplayName() + " to " + args[2]);
						}
						} else player.sendMessage(String.format("§c\'%s\' is not a valid player", args[1]));
						} else {
							player.sendMessage(ChatColor.RED + "\'" + args[2] + "\' is not a valid value!");
						}
						} else player.sendMessage(NoPerm);
						
					} else if (args[0].equalsIgnoreCase("ticket")){
						if (perms.has(sender, AdminPerm.getName())){
						int toGive;
						try{
							toGive = Integer.parseInt(args[2]);
							} catch(NumberFormatException e){
								player.sendMessage(String.format("§c%s is not a valid number", args[2]));
								player.sendMessage("§cValid Usage: /slimeslap ticket [Player] [Count]");
								return true;
							}
						Player target = Bukkit.getPlayer(args[1]);
						if (target == null){
							player.sendMessage(String.format("§c%s is not a valid player", args[1]));
							player.sendMessage("§cValid Usage: /slimeslap ticket [Player] [Count]");
							return true;
						} else {
							for (int i = 0; i < toGive; i++){
								target.getInventory().addItem(killTicket);
							}
							player.sendMessage(String.format(ChatColor.GREEN + "Gave %s %s tickets", target.getName(), toGive));
						}
						} else player.sendMessage(NoPerm);
					}
				
					}
				
			}

				
		} else if (cmd.getName().equalsIgnoreCase("reloadconfig")){
			reloadConfig();
		}
		
		
		return true;
	}
	
}
