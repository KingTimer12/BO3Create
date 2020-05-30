package br.com.kingo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;

public class BO3Constructor {

	public List<Block> spawn(Location location, File file) {
		BufferedReader reader;
		ArrayList<Block> blocks = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.contains("Block(") || !line.contains(",") || line.contains("#")) {
					continue;
				}
				String[] parts = line.replace("Block(", "").replace(")", "").split(",");
				int x = Integer.valueOf(parts[0]);
				int y = Integer.valueOf(parts[1]);
				int z = Integer.valueOf(parts[2]);
				Material material = Material.valueOf(parts[3]);
				byte data = (parts.length > 4) ? Byte.valueOf(parts[4]) : 0;
				Location loc = new Location(location.getWorld(), 
						location.getBlockX() + x, 
						location.getBlockY() + y, 
						location.getBlockZ() + z);
				setBlockFast(loc, material, data);
				blocks.add(location.getWorld().getBlockAt(location.getBlockX() + Integer.valueOf(x),
						location.getBlockY() + Integer.valueOf(y), location.getBlockZ() + Integer.valueOf(z)));
			}
			reader.close();
		} catch (Exception e) {
			System.out.println(
					"Error to spawn the bo2file " + file.getName() + " in the location " + location.toString());
			e.printStackTrace();
		}
		return blocks;
	}

	public List<FutureBlock> load(Location location, File file) {
		BufferedReader reader;
		ArrayList<FutureBlock> blocks = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.contains(",") || !line.contains(":")) {
					continue;
				}
				String[] parts = line.split(":");
				String[] coordinates = parts[0].split(",");
				String[] blockData = parts[1].split("\\.");
				blocks.add(new FutureBlock(
						location.clone().add(Integer.valueOf(coordinates[0]), Integer.valueOf(coordinates[2]),
								Integer.valueOf(coordinates[1])),
						Integer.valueOf(blockData[0]), blockData.length > 1 ? Byte.valueOf(blockData[1]) : 0));
			}
			reader.close();
		} catch (Exception e) {
			System.out
					.println("Error to load the bo2file " + file.getName() + " in the location " + location.toString());
			e.printStackTrace();
		}
		return blocks;
	}

	@SuppressWarnings("deprecation")
	public boolean setBlockFast(Location location, Material material, byte data) {
		if (location.getBlockY() >= 255 || location.getBlockY() < 0) {
			return false;
		}
		World worldbukkit = BukkitMain.getInstance().getServer().getWorld("world");
		net.minecraft.server.v1_8_R3.World world = ((CraftWorld) worldbukkit).getHandle();
		Location l = location;
		net.minecraft.server.v1_8_R3.Chunk chunk = world.getChunkAt(l.getBlockX() >> 4, l.getBlockZ() >> 4);
		BlockPosition bp = new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
		int combined = material.getId() + (data << 12);
		IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
		chunk.a(bp, ibd);
		location.getWorld().refreshChunk(location.getBlockX(), location.getBlockZ());
		return false;
	}

	public class FutureBlock {
		private Location location;
		private int id;
		private byte data;

		public FutureBlock(Location location, int id, byte data) {
			this.location = location;
			this.id = id;
			this.data = data;
		}

		public byte getData() {
			return data;
		}

		public Location getLocation() {
			return location;
		}

		public int getId() {
			return id;
		}

		@SuppressWarnings("deprecation")
		public void place() {
			location.getBlock().setTypeIdAndData(id, data, true);
		}
	}

}
