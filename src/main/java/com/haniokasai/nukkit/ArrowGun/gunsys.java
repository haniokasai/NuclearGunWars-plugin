package com.haniokasai.nukkit.ArrowGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.UseItemPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class gunsys implements Listener {
	//プレイヤー名、銃の種類、最終発車時間、弾数
	static Map<String, Integer> cgun = new HashMap<String, Integer>();
	static Map<String, Integer> tgun = new HashMap<String, Integer>();
	static Map<String, Integer> timegun = new HashMap<String, Integer>();
	static Map<String, Boolean> hgun = new HashMap<String, Boolean>();
	/*決まり文句*/
    Main plugin;

    public gunsys(Main plugin){
        this.plugin = plugin;
    }


	static Map<Integer, Map<Integer,String>> whodi = new HashMap<Integer, Map<Integer,String>>();
	  public static  boolean saveshooter(long id,int kindit,Player player) {
		  whodi.put((int)id, new HashMap<Integer,String>());
		  whodi.get((int)id).put(kindit,player.getName());
		  return true;
	  }


	  @EventHandler
	  public void chin(PlayerItemHeldEvent event){
			Player player = event.getPlayer();
			Item item =event.getItem();
			String iname =item.getCustomName();
			Config gunlangjpn =Main.gunlangjpn;
			if(gunlangjpn.exists(iname.trim())){
				int kindid = gunlangjpn.getInt(iname.trim());
				if(cgun.containsKey(player.getName()+"-"+kindid)){
					int c = cgun.get(player.getName()+"-"+kindid);
					player.sendPopup(lj.usebu_remaining+c);
				}

				if(kindid >1000){
					hgun.put(player.getName(),true);
				}else if(hgun.containsKey(player.getName())){
					hgun.remove(player.getName());
				}
			}else{
				if(hgun.containsKey(player.getName())){
					hgun.remove(player.getName());
				}
			}
	  }

	  public void usebu(Player player,int rel,int cart,int kindid){
		  int now = cgun.get(player.getName()+"-"+kindid);
		  cgun.put(player.getName()+"-"+kindid,now-1);
		  player.sendPopup(lj.usebu_remaining+(now-1));

		  if(cgun.get(player.getName()+"-"+kindid) <= 0){
			  Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
					@Override
					public void run() {
						cgun.put(player.getName()+"-"+kindid,cart);
						player.sendPopup(lj.usebu_reloadcomp);
					}
				},20*rel);
			  cgun.put(player.getName()+"-"+kindid,0);
			  player.sendPopup(lj.usebu_relstart1+rel+lj.usebu_relstart2);
		  }
	  }


	  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
		public void fishingrod(DataPacketReceiveEvent event){

			Config gunlangjpn =Main.gunlangjpn;

			Player player = event.getPlayer();
			String name = player.getName();
			DataPacket pk = event.getPacket();
			UseItemPacket useItemPacket = null;
			teamsys.ctime = (int) (System.currentTimeMillis()/1000)*10;
			boolean p = false;
			try{
				useItemPacket = (UseItemPacket) pk;
				if(player.getInventory().getItemInHand().getId()==346){
					if(!timegun.containsKey(name)){
						if (pk instanceof UseItemPacket & useItemPacket.face == -1) {
							p = true;
						}
					}
					if(teamsys.ctime - timegun.get(name) >1){
						if (pk instanceof UseItemPacket & useItemPacket.face == -1) {
							p = true;
						}
					}

				}
				}catch(Exception okok){
				}

			if(p & teamsys.joinedpvp.containsKey(name)){
				timegun.put(name,teamsys.ctime);
				Item item = player.getInventory().getItemInHand();
					String iname =item.getCustomName();
					int ctime =(int) (System.currentTimeMillis()/1000);
					if (item.getId() == 346) {
						String type = "0";
						double speed = 0;
						int kindid = 0;
						double sleep =0;
						int cart =0;
						int rel =0;
						if(gunlangjpn.exists(iname.trim())){
							kindid = gunlangjpn.getInt(iname.trim());
							type = gunlangjpn.getString(kindid+"_type");
							speed = gunlangjpn.getDouble(kindid+"_speed");
							sleep = gunlangjpn.getDouble(kindid+"_sleep");
							cart = gunlangjpn.getInt(kindid+"_cart");
							rel = gunlangjpn.getInt(kindid+"_rel");
						}

						Boolean sg =false;
					  	if(!cgun.containsKey(name+"-"+kindid)){

					  		cgun.put(name+"-"+kindid,cart);
					  		tgun.put(name+"-"+kindid,ctime);
					  		sg =true;
						}else{
							sg = tgun.get(name + "-" + kindid) <= (ctime - sleep);
							if(!player.isSneaking() & kindid  >2000){
								sg = false;
							}
						}


						if(sg & cgun.get(name+"-"+kindid) > 0){
							sg = true;

						}else{
							if(sg){
								sg =false;
								player.sendPopup(lj.fishingrod_reloading);
							}
						}

						if(sg){

							ArrayList<spdata> list = new ArrayList<spdata>();
							list.add(new spdata(player,type,speed,kindid));

							tgun.put(name+"-"+kindid,ctime);
							if(kindid <1000){
								gunsys.reshooting(list);
								this.usebu(player, rel, cart,kindid);
							}else if(kindid > 1000){
								int count = 5;
								for(int i = 0;i < count; ++i){
									if(cgun.get(name+"-"+kindid) > 0){
										this.usebu(player, rel, cart,kindid);
										Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
											@Override
											public void run() {
												gunsys.reshooting(list);
											}
										},4*i);

									}
								}
							}
						}

					}
			}

		}



		private static void reshooting(ArrayList<spdata> sdata) {
			Player player = sdata.get(0).player;
			String type = sdata.get(0).type;
			double speed = sdata.get(0).speed;
			int kindid = sdata.get(0).kindid;
			int c =cgun.get(player.getName()+"-"+kindid);
			if(!player.isSneaking() & kindid  >2000){
				c = 0;
			}
			if(c > 0 ){

			CompoundTag nbt = new CompoundTag()
					.putList(new ListTag<DoubleTag>("Pos")
							.add(new DoubleTag("", player.getX()+(-Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
							.add(new DoubleTag("", player.getY()+player.getEyeHeight()-0.25))
							.add(new DoubleTag("", player.getZ()+(Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))))
					.putList(new ListTag<DoubleTag>("Motion")
							.add(new DoubleTag("",-Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))
							.add(new DoubleTag("",-Math.sin(player.pitch / 180 * Math.PI)))
							.add(new DoubleTag("", Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
					.putList(new ListTag<FloatTag>("Rotation")
							.add(new FloatTag("", (float) player.yaw))
							.add(new FloatTag("", (float) player.pitch)));
			Entity snowball = Entity.createEntity(type,player.chunk,nbt,player);
			if(kindid == 1 || kindid == 4 || kindid == 8){
				speed=speed*1.1;
			}else{
				speed=speed*1.8;
			}
			snowball.setMotion(snowball.getMotion().multiply(speed));
			snowball.spawnToAll();
			saveshooter(snowball.getId(),kindid,player);

			}
		}

		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
	    public void onProjectileHit(ProjectileHitEvent event) throws Exception
{
	        Entity snowball = event.getEntity();
	        Position loc = snowball.getLocation();
	        Integer kind = null;
	        String who = null;
	        snowball.getLevel().removeEntity(snowball);
	        if(whodi.containsKey((int)snowball.getId())){
				  for (Map.Entry<Integer, String> e : whodi.get((int)snowball.getId()).entrySet()) {
					    kind = e.getKey();
					    who = e.getValue();
				  }
			}
	        if(who !=null){
	        Player wdi =Server.getInstance().getPlayer(who);
	        if(kind == 1 || kind == 4 || kind == 8){

	        	int ds = 3;
	        	if(kind == 8){
	        		ds = 2;
	        	}
	        	if(kind == 4){
	        		ds = 5;
	        	}

	    		int x=loc.getFloorX();
	    		int y=loc.getFloorY();
	    		int z=loc.getFloorZ();

	    		ExplodeSound sound = new ExplodeSound(new Vector3(x,y,z));//サウンドオブジェクトの生成
	    		loc.getLevel().addSound(sound);//サウンドを再生
	    		ExplodeParticle particle = new ExplodeParticle(new Vector3(x,y,z));
	    		int count = 20;
	    		for(int i = 0;i < count; ++i){
	    			loc.getLevel().addParticle(particle);
	    		}
	    		for(Player playera : Server.getInstance().getOnlinePlayers().values()){
	   			 	if(Math.abs(playera.getX()-x)<=ds & Math.abs(playera.getY()-y)<=ds & Math.abs(playera.getZ()-z)<=ds & wdi != playera){
	   			 		EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(wdi,playera, EntityDamageEvent.CAUSE_ENTITY_EXPLOSION, 8);//EntityDamageEvent::CAUSE_MAGICを変えることでダメージの種類を、1を変えることでダメージの強さが変更できます
	   			 		playera.attack(ev);

	    			 }
	    		}
	    	}}
	    }

		@EventHandler
		public void onDamage(EntityDamageEvent event){
			Entity player = event.getEntity();
			if (player instanceof Player && event.getCause() == EntityDamageEvent.CAUSE_PROJECTILE) {
				event.setDamage(7);
			}
		}

		public static void rekit(Player player) {
			player.getInventory().clearAll();
			player.getInventory().clearAll();
			String name = player.getName();
			HashMap<String,Integer> map = mysql.getskills(name);
			if(Main.skillc.exists("s"+map.get("skill")+"name")){
				player.getInventory().setArmorItem(0,Item.get(Main.skillc.getInt("s"+map.get("skill")+"a"),0,1));//ヘルメット
				player.getInventory().setArmorItem(1,Item.get(Main.skillc.getInt("s"+map.get("skill")+"b"),0,1));//ヘルメット
				player.getInventory().setArmorItem(2,Item.get(Main.skillc.getInt("s"+map.get("skill")+"ｃ"),0,1));//ヘルメット
				player.getInventory().setArmorItem(3,Item.get(Main.skillc.getInt("s"+map.get("skill")+"d"),0,1));//ヘルメット
				player.getInventory().sendArmorContents(player);
				if(Main.skillc.getInt("s"+map.get("skill")+"ef1") !=0){
				player.addEffect(Effect.getEffect(Main.skillc.getInt("s"+map.get("skill")+"ef1")).setDuration(1000000).setAmplifier(2).setVisible(true));
				}
				if(Main.skillc.getInt("s"+map.get("skill")+"ef2") !=0){
				player.addEffect(Effect.getEffect(Main.skillc.getInt("s"+map.get("skill")+"ef2")).setDuration(1000000).setAmplifier(2).setVisible(true));
				}
			}
			int mai =map.get("gslot");
			int i = 1;
			while(mai >= i){
				player.getInventory().addItem(Item.get(346, 0, 1).setCustomName(Main.gunlangjpn.getString(map.get("gun"+i)+"_name")).clone());
				i++;
			}
			player.sendMessage("[ArrowGun] "+lj.rekit_comp);

		}

}

class spdata{
    Player player;
	String type;
	double speed;
	int kindid;
	spdata(Player player,String type,double speed,int kindid){
       this.player=player;
       this.type=type;
       this.speed=speed;
       this.kindid=kindid;
 }
}