package main.java.mx.com.rodel.utils;

public class Schematic {
	/*private byte[] blocks;
	private byte[] data;
	private short width;
	private short length;
	private short height;*/
	
	/*public Schematic(InputStream file){
		try {
			NBTTagCompound schematic = NBTCompressedStreamTools.a(file);
			
			blocks=schematic.getByteArray("Blocks");
			data=schematic.getByteArray("Data");
			width=schematic.getShort("Width");
			length=schematic.getShort("Length");
			height=schematic.getShort("Height");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public ArrayList<Block> paste(Location location){
		ArrayList<Block> blocksl = new ArrayList<>();
		for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                	int index = y * width * length + z * width + x;
                    Block block = new Location(location.getWorld(), x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();
                    block.setTypeIdAndData(Math.abs(blocks[index] & 0xFF), (byte) Math.abs(data[index]), true);
                   
                    blocksl.add(block);
                }
            }
        }
		return blocksl;
	}
	
	public byte[] getBlocks() {
		return blocks;
	}
	
	public byte[] getData() {
		return data;
	}

	public short getWidth() {
		return width;
	}

	public short getLength() {
		return length;
	}

	public short getHeight() {
		return height;
	}*/
}
