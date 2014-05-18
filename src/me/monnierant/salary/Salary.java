package me.monnierant.salary;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.monnierant.salary.Tracer.eNiveau;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Salary extends JavaPlugin
{
	private Boolean m_isDebug = false;

	private Translator m_translator = null;

	private Tracer m_tracer = null;
	
	private Boolean m_onStart = false;
	
	private Boolean m_onTimer = false;
	
	private Boolean m_withPermission = true;
	
	private Double m_timer;
	
	private String m_world;
	
	public static Permission permission = null;
    public static Economy economy = null;

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

	private boolean checkConfigFile()
	{

		boolean result = false;

		ArrayList<String> paths = new ArrayList<String>();
		ArrayList<Object> values = new ArrayList<Object>();
		paths.add("settings.debug");
		values.add(false);
		paths.add("settings.lng");
		values.add("EN");
		paths.add("settings.tracer.chemin");
		values.add("salary.log");
		paths.add("settings.tracer.permission");
		values.add("salary.trace");
		paths.add("settings.tracer.param");
		values.add(15);
		paths.add("settings.Salary.onStart");
		values.add(true);
		paths.add("settings.Salary.onTimer");
		values.add(false);
		paths.add("settings.Salary.withPermissions");
		values.add(true);
		paths.add("settings.Salary.Timer");
		values.add(100);
		
		

		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists())
		{

			for (int t = 0; t < paths.size(); t++)
			{
				getConfig().addDefault(paths.get(t), values.get(t));
			}
			result = true;
		}
		else
		{
			for (int t = 0; t < paths.size(); t++)
			{
				if (!getConfig().contains(paths.get(t)))
				{
					getConfig().addDefault(paths.get(t), values.get(t));
					result = true;
				}
			}
		}

		// Load Configuration
		getConfig().options().copyDefaults(true);
		saveConfig();

		return result;
	}
	
	private String formatDate(Calendar date)
	{
		String value="";
		
		value=date.get(Calendar.DAY_OF_MONTH)+"/"+date.get(Calendar.MONTH)+"/"+date.get(Calendar.YEAR);
		
		return value;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
	{

		if (m_translator == null)
		{
			m_translator = new Translator(getConfig().getString("settings.lng"));
		}

		if (cmnd.getName().equalsIgnoreCase("salary") || cmnd.getName().equalsIgnoreCase("slr") || cmnd.getName().equalsIgnoreCase("sal"))
		{
			if (strings.length == 0 || strings.length == 2 || strings.length > 3)
			{
				ArrayList<String> message = m_translator.get("help");
				for (int t = 0; t < message.size(); t++)
				{
					cs.sendMessage(message.get(t));
				}

			}
			else if (strings.length == 1)
			{
				if (strings[0].equalsIgnoreCase("reload"))
				{
					if (cs.hasPermission("salary.reload"))
					{
						reloadConfig();
						saveConfig();

						onReload();

						cs.sendMessage(m_translator.get("reload", 0));
						cs.sendMessage(m_translator.get("reload", 1));
					}
					else
					{
						cs.sendMessage(m_translator.get("error", 0));
					}
				}
				else if (strings[0].equalsIgnoreCase("pay"))
				{
					if (cs.hasPermission("salary.pay"))
					{
						checkPayment(getServer());
						cs.sendMessage(m_translator.get("pay", 0));
					}
					else
					{
						cs.sendMessage(m_translator.get("error", 0));
					}
				}
				else if (strings[0].equalsIgnoreCase("list"))
				{
					if (cs.hasPermission("salary.list"))
					{
						cs.sendMessage(m_translator.get("pay", 3));
						List<String> groups=getConfig().getStringList("settings.Salary.Groups");
						String result="§d";
						for(int t=0;t<groups.size();t++)
						{
							result+=groups.get(t)+",";
						}
						cs.sendMessage(result);
					}
					else
					{
						cs.sendMessage(m_translator.get("error", 0));
					}
				}
			}
			else if (strings.length == 3)
			{
				if (strings[0].equalsIgnoreCase("add"))
				{
					if(!m_withPermission)
					{
						String groupe=strings[1];
						if (cs.hasPermission("salary.add") || cs.hasPermission("salary.add."+groupe))
						{
							List<String> groupes=getConfig().getStringList("settings.Salary.Groups");
							if(groupes.contains(groupe))
							{
								String joueur=strings[2];
								Calendar rightNow=Calendar.getInstance();
								List<String> joueurs=getConfig().getStringList("settings.Salary.SalaryDetails."+groupe+".members");
								if(!joueurs.contains(joueur))
								{
									joueurs.add(joueur);
									getConfig().set("settings.Salary.SalaryDetails."+groupe+".members",joueurs);
									saveConfig();
									m_tracer.log(eNiveau.INFO, "["+formatDate(rightNow)+"] "+groupe+" => add "+joueur, this.getServer().getConsoleSender());
									cs.sendMessage(m_translator.get("pay", 1));
								}
								else
								{
									cs.sendMessage(m_translator.get("error", 6));
								}
								
							}
							else
							{
								cs.sendMessage(m_translator.get("error", 5)+":"+groupe);
							}
							
						}
						else
						{
							cs.sendMessage(m_translator.get("error", 0));
						}
					}
					else
					{
						cs.sendMessage(m_translator.get("error", 3));
						cs.sendMessage(m_translator.get("error", 4));
					}
				}
				else if (strings[0].equalsIgnoreCase("remove"))
				{
					if(!m_withPermission)
					{
						String groupe=strings[1];
						if (cs.hasPermission("salary.remove") || cs.hasPermission("salary.remove."+groupe))
						{
							List<String> groupes=getConfig().getStringList("settings.Salary.Groups");
							if(groupes.contains(groupe))
							{
								String joueur=strings[2];
								Calendar rightNow=Calendar.getInstance();
								List<String> joueurs=getConfig().getStringList("settings.Salary.SalaryDetails."+groupe+".members");
								if(joueurs.contains(joueur))
								{
									joueurs.remove(joueur);
									getConfig().set("settings.Salary.SalaryDetails."+groupe+".members",joueurs);
									saveConfig();
									m_tracer.log(eNiveau.INFO, "["+formatDate(rightNow)+"] "+groupe+" => add "+joueur, this.getServer().getConsoleSender());
									cs.sendMessage(m_translator.get("pay", 2));
								}
								else
								{
									cs.sendMessage(m_translator.get("error", 7));
								}
								
							}
							else
							{
								cs.sendMessage(m_translator.get("error", 5)+":"+groupe);
							}
							
						}
						else
						{
							cs.sendMessage(m_translator.get("error", 0));
						}
					}
					else
					{
						cs.sendMessage(m_translator.get("error", 3));
						cs.sendMessage(m_translator.get("error", 4));
					}
				}
					
			}

		}
		return true;
	}
	
	public void checkPayment(Server srv)
	{
		Calendar rightNow = Calendar.getInstance();
		if(getConfig().getList("settings.Salary.Groups")!=null)
		{
			
			for(int t=0;t<getConfig().getList("settings.Salary.Groups").size();t++)
			{
				String groupe=getConfig().getStringList("settings.Salary.Groups").get(t);
				Double ammount=getConfig().getDouble("settings.Salary.SalaryDetails."+groupe+".amount");
				Integer dayNumber=getConfig().getInt("settings.Salary.SalaryDetails."+groupe+".dayNumber");
				Integer dayOfMonth=getConfig().getInt("settings.Salary.SalaryDetails."+groupe+".dayOfMonth");
				String oldDate= getConfig().getString("settings.Salary.SalaryDetails."+groupe+".lastDate");
				
				if(m_isDebug)
				{
					if(oldDate!=null)
					{
						m_tracer.log(eNiveau.INFO, "["+formatDate(rightNow)+"] |TRY-"+oldDate+"| "+groupe+" => "+ammount.toString(), srv.getConsoleSender());
					}
					else
					{
						m_tracer.log(eNiveau.INFO, "["+formatDate(rightNow)+"] |TRY-null| "+groupe+" => "+ammount.toString(), srv.getConsoleSender());
					}
				}
				
				if(oldDate==null || (!oldDate.equals(formatDate(rightNow))))
				{
					if(dayOfMonth > 0)
					{
						if(m_isDebug)
						{
							m_tracer.log(eNiveau.INFO, "["+formatDate(rightNow)+"] |TRY_MONTH| "+rightNow.get(Calendar.DAY_OF_MONTH)+" / "+dayOfMonth, srv.getConsoleSender());
						}
						if(rightNow.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
						{
							payPermission(srv,ammount,groupe);
							getConfig().set("settings.Salary.SalaryDetails."+groupe+".lastDate",formatDate(rightNow));
							saveConfig();
							m_tracer.log(eNiveau.INFO, "["+formatDate(rightNow)+"] "+groupe+" => "+ammount.toString(), srv.getConsoleSender());
						}
					}
					else
					{
						if(dayNumber > 0)
						{
							dayNumber++;
							if(dayNumber>7)
							{
								dayNumber=1;
							}
							if(m_isDebug)
							{
								m_tracer.log(eNiveau.INFO, "["+formatDate(rightNow)+"] |TRY_DAY| "+rightNow.get(Calendar.DAY_OF_WEEK)+" / "+dayNumber, srv.getConsoleSender());
							}
							if(rightNow.get(Calendar.DAY_OF_WEEK) == dayNumber)
							{
								payPermission(srv,ammount,groupe);
								getConfig().set("settings.Salary.SalaryDetails."+groupe+".lastDate",formatDate(rightNow));
								saveConfig();
								m_tracer.log(eNiveau.INFO, "["+formatDate(rightNow)+"] "+groupe+" => "+ammount.toString(), srv.getConsoleSender());
							}
						}
					}
				}
			}
		}
		else
		{
			srv.getConsoleSender().sendMessage(m_translator.get("error", 1));
		}
	}
	
	public void payPermission(Server srv,Double ammount,String group)
	{
		if(m_withPermission)
		{
			if(permission!=null)
			{
				String perm="salary.get."+group;
				OfflinePlayer[] players=srv.getOfflinePlayers();
				for(int t=0;t<players.length;t++)
				{
					if(permission.has(m_world, players[t].getName(), perm))
					{
						economy.depositPlayer(players[t].getName(), ammount);
						m_tracer.log(eNiveau.INFO, "["+players[t].getName()+"] => "+ammount.toString(), srv.getConsoleSender());
					}
				}
			}
			else
			{
				srv.getConsoleSender().sendMessage(m_translator.get("error", 2));
			}
		}
		else
		{
			List<String> playersToPay=getConfig().getStringList("settings.Salary.SalaryDetails."+group+".members");
			if(playersToPay!=null)
			{
				for(int t=0;t<playersToPay.size();t++)
				{
					if(srv.getOfflinePlayer(playersToPay.get(t))!=null)
					{
						economy.depositPlayer(playersToPay.get(t), ammount);
						m_tracer.log(eNiveau.INFO, "["+playersToPay.get(t)+"] => "+ammount.toString(), srv.getConsoleSender());
					}
				}
			}
			else
			{
				m_tracer.log(eNiveau.WARNING, "["+group+"] => Have no member to pay", srv.getConsoleSender());
			}
		}
	}

	@Override
	public void onDisable()
	{
		if (m_tracer != null)
		{
			m_tracer.close();
		}

	}

	@Override
	public void onEnable()
	{

		checkConfigFile();

		onReload();
		
		if (!setupEconomy() || !setupPermissions() ) {
            m_tracer.log(eNiveau.SEVERE,String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()),getServer().getConsoleSender());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		if(m_onStart)
		{
			checkPayment(getServer());
		}
		if(m_onTimer)
		{
			BukkitScheduler scheduler = getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
				@Override
				public void run() {
					checkPayment(getServer());				
				}
	        }, m_timer.longValue()*20L, m_timer.longValue()*20L);
		}

	}

	private void onReload()
	{
		checkConfigFile();

		m_isDebug = getConfig().getBoolean("settings.debug");
		m_translator = new Translator(getConfig().getString("settings.lng"));
		if (m_tracer != null)
		{
			m_tracer.close();
		}
		m_tracer = new Tracer(getDataFolder() + "/" + getConfig().getString("settings.tracer.chemin"), this.getServer().getConsoleSender(), getConfig().getInt("settings.tracer.param"), getConfig().getString("settings.tracer.permission"));
		
		m_onStart = getConfig().getBoolean("settings.Salary.onStart");
		
		m_onTimer = getConfig().getBoolean("settings.Salary.onTimer");

		m_timer = getConfig().getDouble("settings.Salary.Timer");
		
		m_world = getConfig().getString("settings.Salary.World");
		
		m_withPermission = getConfig().getBoolean("settings.Salary.withPermissions");
		
	}
}
