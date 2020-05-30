package br.com.kingo;

import java.io.File;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;

public class BukkitMain extends JavaPlugin implements Listener {

	public static BukkitMain getInstance() {
		return getPlugin(BukkitMain.class);
	}
	
	//public BukkitViaAPI viaAPI;

	@Override
	public void onEnable() {
		//viaAPI=new BukkitViaAPI(ViaVersionPlugin.getPlugin(ViaVersionPlugin.class));
		BO3Constructor bo3 = new BO3Constructor();
		bo3.spawn(Bukkit.getWorld("world").getSpawnLocation(), new File(getDataFolder() + "/we-a3.bo3"));
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onJoin(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		changeBlock(player, Material.EMERALD_BLOCK, 0);
	}

	@SuppressWarnings("deprecation")
	private void changeBlock(@Nonnull Player player, Material material, int data) {
		if (material == null)
			material = Material.AIR;
		World worldbukkit = player.getWorld();
		Location location = player.getLocation().subtract(0, 1, 0);
		net.minecraft.server.v1_8_R3.World world = ((CraftWorld) worldbukkit).getHandle();
		BlockPosition bp = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(world, bp);
		int combined = material.getId() + ((byte) data << 12);
		packet.block = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

}
