package com.haniokasai.nukkit.ArrowGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;

public class events implements Listener {

	/*決まり文句*/
    Main plugin;

    public events(Main plugin){
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.MONITOR)
	public void PJ(PlayerJoinEvent e){
    	Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
			@Override
			public void run() {
				e.getPlayer().teleport(Server.getInstance().getDefaultLevel().getSpawnLocation());
			}
		},1);

    }

    @EventHandler
	public void ExplosionPrimeEvent(EntityExplosionPrimeEvent e){
        e.setBlockBreaking(false);
    }

    @EventHandler
	public void dropEvent(PlayerDropItemEvent e){
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
	public void chatEvent(PlayerChatEvent event) throws NullPointerException{
    	Player player = event.getPlayer();
		String name   = player.getName().toLowerCase();
    	if(!Main.canchat.get(name)){
    		player.sendMessage(Main.chatred+ "" +"[Arrowgun]"+lj.cantchat);
    		event.setCancelled(true);
    	}
    }


    @EventHandler
    public void gungun(PlayerMoveEvent event){
    	if(gunsys.hgun.containsKey(event.getPlayer().getName())){
    		Location from = event.getFrom();
    		Location to = event.getTo();
    		if(Math.abs(from.x-to.x) >0.1 || Math.abs(from.z-to.z) >0.1){
    			event.setCancelled(true);
    		}

    	}
    }

    @EventHandler
    public void dnb(BlockBreakEvent event){
    	int str = event.getBlock().getId();
    	if (str==174|str==79|str==58|str==57|str==56|str==30|str==287|str==26|str==16|str==15|str==14|str==13){
    		event.setCancelled(true);
    	}
    	if(str == 80){//雪
    		event.getPlayer().getInventory().addItem(Item.get(80,1,1));
    		event.getPlayer().getInventory().sendContents(event.getPlayer());
    	}
    }

    @EventHandler
    public void dnp(BlockPlaceEvent event){
    	int str = event.getBlock().getId();
    	if (str==174|str==79|str==58|str==57|str==56|str==30|str==287|str==26|str==16|str==15|str==14|str==13){
    		event.setCancelled(true);
    	}
    }

    @EventHandler
    public void chest(PlayerInteractEvent event){
    	int str = event.getBlock().getId();

    	if (str==54 | str==58| str==61){
    		event.setCancelled(true);
    	}
    	if(event.getPlayer().getInventory().getItemInHand().getId() == 259){
    		event.setCancelled(true);
    	}
    }
}
