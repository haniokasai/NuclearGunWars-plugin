package com.haniokasai.nukkit.ArrowGun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.math.Vector3;

public class signcommand implements Listener {

    Main plugin;

    public signcommand(Main plugin){
        this.plugin = plugin;
    }



	@EventHandler(priority= EventPriority.NORMAL)
	public void PlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if((event.getBlock().getId()!=63)&&(event.getBlock().getId()!=68))return;
		BlockEntity blockEntity = event.getBlock().getLevel().getBlockEntity(new Vector3(event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ()));
		if(!(blockEntity instanceof BlockEntitySign))return;
		BlockEntitySign sign = (BlockEntitySign)blockEntity;
		if(sign.getText()[0].equalsIgnoreCase("[Command]")){
			Server.getInstance().dispatchCommand(player,sign.getText()[3]);
			event.setCancelled(true);
		}

	}
}
