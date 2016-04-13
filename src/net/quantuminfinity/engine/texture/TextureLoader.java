package net.quantuminfinity.engine.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.quantuminfinity.engine.resource.ResourceLoader;

public class TextureLoader
{
	private static final int BYTES_PER_PIXEL = 4;
    
	public static int loadTexture(BufferedImage image, TextureFactory tf)
	{
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);

		for(int y = 0; y < image.getHeight(); y++)
			for(int x = 0; x < image.getWidth(); x++)
			{
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		
		buffer.rewind();

		if (tf == null)
			tf = TextureFactory.defaultFactory;
		return tf.generateTex2D(image.getWidth(), image.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	}
	
	public static int loadTexture(InputStream input, TextureFactory tf)
	{
		try {
			return loadTexture(ImageIO.read(input), tf);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int loadTexture(String name, TextureFactory tf)
	{
		try
		{
			return loadTexture(ResourceLoader.getResourceAsStream(name), tf);
		} catch (IOException e)
		{
			e.printStackTrace();
		};
		return 0;
	}
	
	public static int loadTexture(InputStream input)
	{
		return loadTexture(input, null);
	}
	
	public static int loadTexture(String name)
	{
		return loadTexture(name, null);
	}
}