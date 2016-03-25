package net.quantuminfinity.engine.display;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class ImageSaveThread implements Runnable
{
	ByteBuffer data;
	int width, height;
	String file, format;
	
	public ImageSaveThread(ByteBuffer data, int width, int height, String file, String format)
	{
		this.data = data;
		this.width = width;
		this.height = height;
		this.format = format != null ? format : "png";
		this.file = file != null ? file : System.nanoTime() + "." + this.format;
	}
	
	@Override
	public void run()
	{
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		   
		for(int x = 0; x < width; x++) 
			for(int y = 0; y < height; y++)
			{
				int i = (x + (width * y)) * 4;
				int r = data.get(i) & 0xFF;
				int g = data.get(i + 1) & 0xFF;
				int b = data.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		
		try {
			ImageIO.write(image, format, new File(file));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
