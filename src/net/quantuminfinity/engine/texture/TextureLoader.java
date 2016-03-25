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
    
	public static int loadTexture(BufferedImage image, int filter, int bpp, int wraps, int wrapt)
	{
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);

		for(int y = 0; y < image.getHeight(); y++)
		{
			for(int x = 0; x < image.getWidth(); x++)
			{
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wraps);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapt);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, bpp, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		return textureID;
	}
	
	public static int loadTexture(InputStream input, int filter, int bpp, int wraps, int wrapt)
	{
		try {
			return loadTexture(ImageIO.read(input), filter, bpp, wraps, wrapt);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int loadTexture(String name, int filter, int bpp, int wraps, int wrapt)
	{
		try
		{
			return loadTexture(ResourceLoader.getResourceAsStream(name), filter, bpp, wraps, wrapt);
		} catch (IOException e)
		{
			e.printStackTrace();
		};
		return 0;
	}
	
	public static int loadTexture(String name, int filter, int bpp, int wrapping)
	{
		return loadTexture(name, filter, bpp, wrapping, wrapping);
	}
	
	public static int loadTexture(InputStream input, int filter, int bpp, int wrapping)
	{
		return loadTexture(input,filter, bpp, wrapping);
	}
	
	public static int loadTexture(String name, int filter, int bpp)
	{
		return loadTexture(name, filter, bpp, GL11.GL_REPEAT);
	}
	
	public static int loadTexture(InputStream input, int filter, int bpp)
	{
		return loadTexture(input,filter, bpp, GL11.GL_REPEAT);
	}
	
	public static int loadTexture(String name)
	{
		return loadTexture(name, GL11.GL_LINEAR, GL11.GL_RGBA);
	}
	
	public static int loadTexture(InputStream input)
	{
		return loadTexture(input, GL11.GL_LINEAR, GL11.GL_RGBA);
	}
}