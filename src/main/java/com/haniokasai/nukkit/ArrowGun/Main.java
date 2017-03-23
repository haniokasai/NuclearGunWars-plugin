package com.haniokasai.nukkit.ArrowGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.*;
import java.util.*;


public class Main extends PluginBase implements Listener {
/////////////
	static Config gunlangjpn;
	static Config config;
	static Config skillc;
	static Map<String, Boolean> canchat = new HashMap<String, Boolean>();
	static TextFormat chatred = TextFormat.RED;
	static TextFormat chatblue = TextFormat.BLUE;
	static TextFormat chataqua = TextFormat.AQUA;
	static TextFormat chatlpurple = TextFormat.LIGHT_PURPLE;
	static int gametype = 0;
	static int servertime = 60;
	static int servertime1 = 15;

	/*gametype
	同チーム攻撃
	/c /stat /pvp
	称号　
	をチェック*/

	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new gunsys(this), this);
		this.getServer().getPluginManager().registerEvents(new teamsys(this), this);
		this.getServer().getPluginManager().registerEvents(new events(this), this);
		this.getServer().getPluginManager().registerEvents(new signcommand(this), this);

		getDataFolder().mkdir();
		mysql.load();

		File b = c_reader("config.json");
		config = new Config(b,Config.JSON);
		lj.sellang(config.getString("lang"));

		File a = c_reader("gun-"+config.getString("lang")+".json");
		gunlangjpn = new Config(a,Config.JSON);

		File c = c_reader("skill.json");
		skillc = new Config(c,Config.JSON);
		teamsys.teamhp1 = Main.config.getInt("teamhp");
		teamsys.teamhp2 = Main.config.getInt("teamhp");
		String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		gametype = config.getInt("gametype");
        this.getServer().getLogger().info("[ArrowGun] Loaded");

        try {
            FileWriter fw = new FileWriter(Server.getInstance().getDataPath()+"pid", false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            //内容を指定する
            pw.println(pid);
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Server.getInstance().getScheduler().scheduleRepeatingTask(new Runnable() {
            public void run() {
            	 try {
                     FileWriter fw = new FileWriter(Server.getInstance().getDataPath()+"time", false);
                     PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
                     //内容を指定する
                     pw.println((int) (System.currentTimeMillis()/1000));
                     pw.close();
                 } catch (IOException ex) {
                     ex.printStackTrace();

                 }
            }
        }, (20*60*1));

        if(gametype !=0){

        	Server.getInstance().getScheduler().scheduleRepeatingTask(new Runnable() {
        		@Override
				public void run() {
					Server.getInstance().broadcastMessage("[再起動] 残り"+servertime+"分です");
					if(servertime == 1){
						 Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(new Runnable() {
					            public void run() {
					            	Server.getInstance().broadcastMessage("[再起動] 残り"+servertime1+"秒です");
					            	if(servertime1==0){
					            		Server.getInstance().shutdown();
					            	}
					            	servertime1--;
					        	}
				        	},20*15,20);

					}
					servertime--;
				}
        	},20*60);
        }
	}


	public void onDisable() {
		mysql.shutdown();

		for(Player playera : Server.getInstance().getOnlinePlayers().values()){
			playera.kick("Server Restart");
		}

		if(Main.config.getBoolean("rmfile")){
		Runtime r = Runtime.getRuntime();
		try {
			if(Main.config.getBoolean("is_win")){
				r.exec(new String[]{"rmdir", "/q", Server.getInstance().getDataPath() + "worlds/world"});
			}else {
				r.exec(new String[]{"/bin/rm", "-rf", Server.getInstance().getDataPath() + "worlds/world"});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(Main.config.getBoolean("is_win")){
				r.exec(new String[]{"rmdir", "/q", Server.getInstance().getDataPath() + "players"});
			}else {
				r.exec(new String[]{"/bin/rm", "-rf", Server.getInstance().getDataPath() + "players"});
			}
			//test
			//r.exec(new String[]{ "/bin/cp","-r",Server.getInstance().getDataPath()+"bp/comp1/world",Server.getInstance().getDataPath()+"worlds"});


		} catch (IOException e) {
			e.printStackTrace();
		}

		int n =0;
		int h =1;
		while(config.exists("world_"+h)){
			h++;
		}
		while(!config.exists("world_"+n)){
			n = (new Random()).nextInt(h+1)+1;
		}

		try {
			Server.getInstance().broadcastMessage("[copying]"+config.getString("world_"+n));
			if(Main.config.getBoolean("is_win")){
				r.exec(new String[]{"xcopy", config.getString("world_" + n), Server.getInstance().getDataPath() + "worlds"});
			}else {
				r.exec(new String[]{"/bin/cp", "-r", config.getString("world_" + n), Server.getInstance().getDataPath() + "worlds"});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		}

		//System.exit(0);
	}

    @EventHandler
	public void joEvent(PlayerJoinEvent e){
        mysql.cre8(e.getPlayer().getName());
        Date date = new Date(((long)mysql.get(e.getPlayer().getName(), "llogin") * 1000));
        e.getPlayer().sendMessage(chatred + "" +"Last Login:"+date);
        teamsys.ctime = (int) (System.currentTimeMillis()/1000);
        mysql.set(e.getPlayer().getName(),"llogin",teamsys.ctime);

        if(mysql.get(e.getPlayer().getName(),"isvip" ) ==3){
        	e.getPlayer().setOp(true);
        }
        setn(e.getPlayer());

        if(mysql.canchat(e.getPlayer().getName())){
        	canchat.put(e.getPlayer().getName(),true);
        }else{
        	canchat.put(e.getPlayer().getName(),false);
        }

    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	String name = sender.getName();
        switch (command.getName()) {
            case "mi":{
        			HashMap<String,Integer> pmap  = mysql.getskills(name);
        			sender.sendMessage(chatred + "" + "/*/*/*/*/PlayerData/*/*/*/*/");
        			sender.sendMessage(chatred + "" + "Money : "+pmap.get("money"));
        			int mai =pmap.get("gslot");
        			int i = 1;
        			while(mai >= i){
        				sender.sendMessage(chatred + "Gun"+i+" : "+"GunName:"+gunlangjpn.getString(pmap.get("gun"+i)+"_name")+"　Gun Num.:"+pmap.get("gun"+i)+"  Price:"+gunlangjpn.getString(pmap.get("gun"+i)+"_hm"));
        				i++;
        			}
        			if(Main.skillc.exists("s"+pmap.get("skill")+"name")){
        				sender.sendMessage(chatred + "" + "Skill : "+skillc.getString("s"+pmap.get("skill")+"name"));

        			}
        			sender.sendMessage(chatred + "" + "/*/*/*/*/*/*/*/*/*/*/*/*/*//");
        			//ChatInWorld a = new ChatInWorld();
        			//a.sendchat("aaa", "aaaa",Server.getInstance().getDefaultLevel().getName());

        		break;}

            case "stat":
            	if(gametype == 0){
        			sender.sendMessage(chatred + "" + "" + "/*/*/*/*/TEAM DATA/*/*/*/*/");
        			sender.sendMessage(chatred + "" + "Team S  HP: "+teamsys.teamhp1+" Players: "+teamsys.p1team.size());
        			sender.sendMessage(chatred + "" + "Team M  HP: "+teamsys.teamhp2+" Players: "+teamsys.p2team.size());
        			sender.sendMessage(chatred + "" + "/*/*/*/*/*/*/*/*/*/*/*/*/*/");
            	}

        		break;

            case "rekit":
        			gunsys.rekit((Player)sender);
        		break;

            case "cg":
        			if(args.length < 1){
         				sender.sendMessage(chatred + "" + "[ArrowGun] "+lj.cmd_cg_help);
        			}else{
        				if(gunlangjpn.exists(args[0]+"_hm")){
        					sender.sendMessage(chatred + "" + "[ArrowGun] GunName:"+gunlangjpn.getString(args[0]+"_name")+"　GunNum:"+args[0]+"  GunPrice:"+gunlangjpn.getString(args[0]+"_hm"));
        				}else{
        					sender.sendMessage(chatred + "" + "[ArrowGun] "+lj.cmd_cg_nogun);
        				}
         			}

        		break;
///////チェック
            case "ch":
    			if(args.length != 1 || canchat.get(name)){
     				sender.sendMessage(chatred + "" + "[ArrowGun] "+lj.cantchat);
    			}else{
    				if(args[0].equals("nuclearcraft")){
    					sender.sendMessage(chatblue + "" + "[ArrowGun] "+lj.cmd_ch_true);
    					mysql.set(name,"chat",2);
    					canchat.put(name,true);
    					mysql.add(name,"money1",300);
    				}else{
    					sender.sendMessage(chatblue + "" + "[ArrowGun] "+lj.cmd_ch_diff);
    				}
     			}

    		break;

            case "c":
            	if(gametype == 0){
            		String msg = "";
					for (String arg : args) {
						msg += arg + " ";
					}
            		if(!teamsys.joinedpvp.containsKey(name)){
            			sender.sendMessage(chatred + "" + "[ArrowGun]"+lj.cmd_c_inpvp);
            			break;
            		}
            		if(!canchat.get(name)){
            			sender.sendMessage(chatred+ "" +"[Arrowgun]"+lj.cantchat);
            			break;
            		}
            		if (msg.length() > 0) {
            			msg = msg.substring(0, msg.length());
            			if(teamsys.p1team.containsKey(name)){
            				Map<UUID, Player>  onlinePlayers = Server.getInstance().getOnlinePlayers();
            				for (Map.Entry<UUID, Player> online : onlinePlayers.entrySet()) {
            					if(teamsys.p1team.containsKey(online.getValue().getName())){
        							online.getValue().sendMessage(chatblue+ "" +"[TEAMCHAT]"+sender.getName()+":"+msg);
        						}
        					}
            		}else if(teamsys.p2team.containsKey(name)){
        				Map<UUID, Player>  onlinePlayers = Server.getInstance().getOnlinePlayers();
    					for (Map.Entry<UUID, Player> online : onlinePlayers.entrySet()) {
        					if(teamsys.p2team.containsKey(online.getValue().getName())){
        						online.getValue().sendMessage(chatblue+ "" +"[TEAMCHAT]"+sender.getName()+":"+msg);
        						}
    						}
        				}
                	}
            	}
        		break;

            case "pvp":
        			if(teamsys.joinedpvp.containsKey(name)){
        				sender.sendMessage(chatred + "" + "[ArrowGun] "+lj.cmd_pvp_inpvp);
        			}else{
        				Vector3 pos1;
            			teamsys.joinedpvp.put(name,1);
            			if(teamsys.teamse(name)){
            				if(gametype == 0){
            					sender.sendMessage(chatlpurple + "" + "[ArrowGun] "+lj.cmd_pvp_ts);
            				}
            				pos1 = new Vector3(config.getInt("pos1x"),config.getInt("pos1y"),config.getInt("pos1z"));//座標を指定
            			}else{
            				if(gametype == 0){
            					sender.sendMessage(chataqua + "" + "[ArrowGun] "+lj.cmd_pvp_tm);
            				}
            				pos1 = new Vector3(config.getInt("pos2x"),config.getInt("pos2y"),config.getInt("pos2z"));//座標を指定
            			}
            			((Player) sender).teleport(pos1);
            			teamsys.ctime = (int) (System.currentTimeMillis()/1000);
            			teamsys.pteamti.put(name,teamsys.ctime);
            			((Player) sender).getInventory().clearAll();
            			gunsys.rekit((Player)sender);
            			int d = 350;
            			if(gametype == 0){
            			 d = 200;
            			}
            			((Player) sender).addEffect(Effect.getEffect(11).setDuration(d).setAmplifier(1000).setVisible(true));
            			((Player) sender).getInventory().addItem(Item.get(364,0,5).clone());
            			((Player) sender).getInventory().addItem(Item.get(257,0,1).clone());
            			setn((Player)sender);
            			((Player) sender).getInventory().sendContents(((Player) sender));
        			}


        		break;

            case "pban":
            	if(mysql.get(name,"isvip" ) >= 2 || sender.isOp()){
            		try{
            		if(Server.getInstance().getPlayer(args[0]) != null){
            			Player pp =Server.getInstance().getPlayer(args[0]);
            			Server.getInstance().getIPBans().addBan(pp.getAddress(),"PBAN:"+sender.getName(), null,args.toString());//ip-ban
    					Server.getInstance().getNameBans().addBan(pp.getName(),"PBAN:"+sender.getName(), null,args.toString());//ba
    					pp.kick(" [BAN] "+pp.getName()+" was banned."+pp.getAddress());
    					Server.getInstance().broadcastMessage(" [BAN] "+pp.getName()+" was banned."+pp.getAddress());
            		}else{
            			sender.sendMessage(chataqua + "" + "[ArrowGun] Who is this !");
                	}

            	}catch(ArrayIndexOutOfBoundsException e){
            		sender.sendMessage(chataqua + "" + "[ArrowGun] Who is this");
            	}
            	}
            	break;
	}
		return true;}

    @EventHandler
    public void noop(PlayerCommandPreprocessEvent event){
    	String[] str = event.getMessage().split(" ");
		if (str[0].equals("/op") ){
			Player player = event.getPlayer();
			String name   = player.getName();
			String reason = " [ArrowGun] "+name+" was banned. because he use /op";
			String ip = player.getAddress();
			Server.getInstance().broadcastMessage(" [OPBAN] "+name+" was banned."+ip);
			Server.getInstance().getIPBans().addBan(ip,"OPBAN", null,reason);//ip-ban
			player.kick(" [OPBAN] "+name+" was banned."+ip);

		}
    }



    public static void setn(Player player){
    	String name = player.getName();
    	String teamname ="";
    	TextFormat tcolor =TextFormat.RESET;
    	String r2 = "";
    	String op = "";
    	if(gametype == 0){
    		if(teamsys.p1team.containsKey(name)){
    			teamname = "[S]";
    			tcolor = TextFormat.LIGHT_PURPLE ;
    		}else if(teamsys.p2team.containsKey(name)){
    			teamname = "[M]";
    			tcolor = chataqua ;
    		}
    	}
		String r = mysql.getname(name);
		if(r.length() > 2){
					try {
						r2 =  "["+new String(Base64.getDecoder().decode(r.toString()),"UTF-8")+"]";
					} catch (UnsupportedEncodingException e) {

						e.printStackTrace();
					}
		}


		if(player.isOp()){
			op ="[OP]";
		}else if(mysql.get(name,"isvip") == 1){
			op ="[VIP]";
		}
		String q =mysql.getregion(name)==1?"":"[ENG]";

		int kk = mysql.get(name,"ap1");
		player.setDisplayName(tcolor+ "" +op+q+teamname+r2+name+ "[" +kk+"AP]");
		player.setNameTag(tcolor+ "" +op+q+teamname+r2+name+ "[" +kk+ "AP]");
    }

    public File c_reader(String config){
    	File file = new File(getDataFolder()+"/"+config);
    	return file;
	}}
