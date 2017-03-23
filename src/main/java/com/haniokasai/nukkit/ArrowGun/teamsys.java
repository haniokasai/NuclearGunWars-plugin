package com.haniokasai.nukkit.ArrowGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class teamsys implements Listener {

	/*決まり文句*/
    Main plugin;

    public teamsys(Main plugin){
        this.plugin = plugin;
    }


	static Map<String,Integer> joinedpvp = new HashMap<String,Integer>();
	static Map<String, Integer> p1team = new HashMap<String, Integer>();
	static Map<String, Integer> p2team = new HashMap<String, Integer>();
	static Map<String, Integer> pteamti = new HashMap<String, Integer>();
	static Map<String, Integer> prep = new HashMap<String, Integer>();
	static Map<String, Integer> ap = new HashMap<String, Integer>();
	static Map<String, Integer> apc = new HashMap<String, Integer>();
	static Map<String, Integer> bmc = new HashMap<String, Integer>();

	HashMap<Player, Player> lastHit = new HashMap<Player, Player>();
	static int teamhp1;
	static int teamhp2;
	static int restart;
	static int ctime =(int) (System.currentTimeMillis()/1000);
	Map<UUID, Player>  onlinePlayers = Server.getInstance().getOnlinePlayers();


	public static boolean teamse(String name){
		ctime =(int) (System.currentTimeMillis()/1000);
		if(p1team.containsKey(name)){
			if(ctime - pteamti.get(name) <= 180){
				if(p2team.containsKey(name)){
					p2team.remove(name);
				}
				return true;
			}
		}else if(p2team.containsKey(name)){
			if(ctime - pteamti.get(name) <= 180){
				if(p1team.containsKey(name)){
					p1team.remove(name);
				}
				return false;
			}
		}

	if(p1team.size() <= p2team.size()){
		p1team.put(name, 1);
			if(p2team.containsKey(name)){
				p2team.remove(name);
			}
		return true;
	}else{
		p2team.put(name, 1);
		if(p1team.containsKey(name)){
			p1team.remove(name);
		}
		return false;
	}


	}



	@EventHandler
	public void clall(PlayerQuitEvent e){
		String name =e.getPlayer().getName();
		teamsys.pteamti.put(name,teamsys.ctime);
		joinedpvp.remove(name);
		lastHit.remove(name);
        if(mysql.get(e.getPlayer().getName(),"isvip" ) ==3){
        	e.getPlayer().setOp(false);
        }
		if(ap.containsKey(name)){
			apc.remove(name);
			mysql.addap(name,ap.get(name));
			ap.remove(name);
		}
	}




	@EventHandler
	public void deathpoin(PlayerDeathEvent event){
		Player death = event.getEntity();
		String name = death.getName();
		joinedpvp.remove(death.getName());
		Main.setn(death);
		ctime = (int) (System.currentTimeMillis()/1000);
		try{
			Player killer= (Player)(((EntityDamageByEntityEvent) death.getLastDamageCause())).getDamager();
			if(killer instanceof Player){
	            mysql.killed(killer.getName(),death.getName());
	            teamsys.pteamti.put(death.getName(),teamsys.ctime);
	            Main.setn(killer);
			}

			if(ap.containsKey(name)){
				apc.remove(name);
				mysql.addap(name,ap.get(name));
				ap.remove(name);
			}
			if(ap.containsKey(killer.getName())){
				apc.remove(killer.getName());
				mysql.addap(killer.getName(),ap.get(killer.getName()));
				ap.remove(killer.getName());
			}
		}catch(Exception e){

		}

try{
		if((((EntityDamageByEntityEvent) death.getLastDamageCause())).getCause()==EntityDamageEvent.CAUSE_FALL){
		}
	}catch(Exception e){
		if(lastHit.containsKey(name)){
			if(ap.containsKey(name)){
				apc.remove(name);
				mysql.addap(name,ap.get(name));
				ap.remove(name);
			}
			String killer = lastHit.get(name).getName();
			if(ap.containsKey(killer)){
				apc.remove(killer);
				mysql.addap(killer,ap.get(killer));
				ap.remove(killer);
			}
			mysql.killed(killer,death.getName());
			Server.getInstance().broadcastMessage("[ArrowGun]"+killer+lj.damage_killfall_1+name);
		}

	}
		lastHit.remove(name);

	}

	@EventHandler
	public void damage(EntityDamageEvent event) throws Exception{
		Player reciever = null;
		try{
				reciever = (Player) event.getEntity();//おかしいっぽい
		}catch(java.lang.ClassCastException e){
		}
				if(event instanceof EntityDamageByEntityEvent){
					Player sender = (Player)((EntityDamageByEntityEvent) event).getDamager();
				if(sender instanceof Player && reciever instanceof Player) {
					String sname = sender.getName();
					String rname = reciever.getName();
					lastHit.put(reciever,sender);
					if(!joinedpvp.containsKey(sname)){
						sender.sendPopup(TextFormat.YELLOW+lj.damage_nojoinpvp);
						event.setCancelled(true);
					}else{

						if(event.getCause() == EntityDamageEvent.CAUSE_ENTITY_ATTACK){
							sender.sendPopup(TextFormat.YELLOW+lj.damage_attackbyhand);
							event.setCancelled(true);
						}

						if(p1team.containsKey(sname) &  p1team.containsKey(rname) &Main.gametype ==0){
							sender.sendPopup(TextFormat.YELLOW+lj.damage_attackbyhand);
							event.setCancelled(true);
						}else if(p2team.containsKey(sname) &  p2team.containsKey(rname) &Main.gametype ==0){
							sender.sendPopup(TextFormat.YELLOW+lj.damage_attackbyhand);
							event.setCancelled(true);

						}else if(joinedpvp.containsKey(sname)){

								int nw = ap.containsKey(sname) ? ap.get(sname) : 1;
								int nwc = apc.containsKey(sname) ?apc.get(sname) : 1;
								int a = Math.round(nwc*(8/10));
								int g = a>0 ? a : 1;
								ap.put(sname,nw+g);
								apc.put(sname,nwc+1);


						}
					}
				}}





	}

	@EventHandler
	public void core(BlockBreakEvent event){
		Player player = event.getPlayer();
		String name = player.getName();
		int id = event.getBlock().getId();
		int dam = event.getBlock().getDamage();
		String teamname = null;
		Boolean ok =false;
		Boolean ng =true;
		String lteamname = null;
		if(id == 247 & (dam == 1|| dam  == 2) & teamhp1>=0& teamhp2>=0 &Main.gametype ==0){

			if(dam  == 1 & p2team.containsKey(name)){
				///人数における、コアhp、値段変更/////////////
				int pc = Server.getInstance().getOnlinePlayers().size();
				int ff = 0;
				if(pc == 1 || teamhp1 <= 0 || teamhp1  < 0 ||(1<pc & pc <=4)){
					ff =0;
					teamhp1 = teamhp1 - 1;
					teamhp2 = teamhp2 +1;
				}else if(4<pc & pc <=20){
					ff =2;
					teamhp1 = teamhp1 - 1;
					teamhp2 = teamhp2 +1;
				}else if(20< pc & pc <=40){
					ff =3;
					teamhp1 = teamhp1 - 2;
					teamhp2 = teamhp2 +2;
				}else if(40<pc & pc <=50){
					ff =4;
					teamhp1 = teamhp1 - 3;
					teamhp2 = teamhp2 +3;
				}else if(50<pc){
					ff =5;
					teamhp1 = teamhp1 - 4;
					teamhp2 = teamhp2 +4;
				}
					mysql.add(name,"money1",ff);
					teamname ="S";
				if(teamhp1 <= 0){
					lteamname ="M";
					ok =true;
				}

			}else if(dam  == 2 & p1team.containsKey(name)){
				///人数における、コアhp、値段変更/////////////
				int pc = Server.getInstance().getOnlinePlayers().size();
				int ff = 0;
				if(pc == 1 || teamhp1 <= 0 || teamhp1  < 0 ||(1<pc & pc <=4)){
					ff =0;
					teamhp1 = teamhp1 + 1;
					teamhp2 = teamhp2 -1;
				}else if(4<pc & pc <=20){
					ff =2;
					teamhp2 = teamhp2 - 1;
					teamhp1 = teamhp1 +1;
				}else if(20< pc & pc <=40){
					ff =3;
					teamhp2 = teamhp2 - 2;
					teamhp1 = teamhp1 +2;
				}else if(40<pc & pc <=50){
					ff =4;
					teamhp2 = teamhp2 - 3;
					teamhp1 = teamhp1 +3;
				}else if(50<pc){
					ff =5;
					teamhp2 = teamhp2 - 4;
					teamhp1 = teamhp1 +4;
				}
					mysql.add(name,"money1",ff);
					teamname ="M";
				if(teamhp2 <= 0){
					lteamname ="S";
					ok =true;
				}

			}else{
				ng = false;
				if(bmc.containsKey(name)){
					bmc.put(name, bmc.get(name)+1);
					player.sendMessage(lj.brkhimcore+(10-bmc.get(name)));
					if(bmc.get(name)>10){
						String reason = " [ArrowGun] "+name+" was banned. because he break his team core";
						String ip = player.getAddress();
						Server.getInstance().broadcastMessage(" [BreakCoreBAN] "+name+" was banned."+ip);
						Server.getInstance().getIPBans().addBan(ip,"BreakCoreBAN", null,reason);//ip-ban
						player.kick(" [BreakCoreBAN] "+name+" was banned."+ip);

					}
				}else{
					bmc.put(name, 1);
					player.sendMessage("あと"+(10-bmc.get(name)+"回壊すとbanされます。"));
				}

			}




			if(ng){
				for (Map.Entry<UUID, Player> online : Server.getInstance().getOnlinePlayers().entrySet()) {
					online.getValue().sendPopup(TextFormat.YELLOW+name+lj.core_attacked1+teamname+lj.core_attacked2);
				}

				if(ok){

				Server.getInstance().broadcastMessage(TextFormat.RED +"[ArrowGun]"+lteamname+lj.core_win);
				Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
					@Override
					public void run() {
						for (Map.Entry<UUID, Player> online : Server.getInstance().getOnlinePlayers().entrySet()) {
							if(teamsys.joinedpvp.containsKey(online.getValue().getName())){
								Main.setn(online.getValue());
								online.getValue().teleport(online.getValue().getSpawn());
								p1team.clear();
								p2team.clear();
								pteamti.clear();
								joinedpvp.clear();
								teamhp1 = Main.config.getInt("teamhp");
								teamhp2 = Main.config.getInt("teamhp");
								if(ap.containsKey(online.getValue().getName())){
									apc.remove(online.getValue().getName());
									mysql.addap(online.getValue().getName(),ap.get(online.getValue().getName()));
								ap.remove(online.getValue().getName());
								 Main.setn(online.getValue());
								}
							}
						}
					}
					},20*10);


				restart= restart + 1;
				if(restart>=1){
					Server.getInstance().broadcastMessage(TextFormat.RED +"[ArrowGun]"+lteamname+lj.core_serverrestart);
					 Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
							@Override
							public void run() {
								Server.getInstance().shutdown();
							}
						},20*20);
				}
				}
			}
			event.setCancelled(true);}
		//event.setCancelled(true);
	}






}
