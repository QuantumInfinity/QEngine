package net.quantuminfinity.engine.texture;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Texture2D
{	
	final int w, h, wrap, filter;
	int textureID;
	IntBuffer pixels;
	
	public Texture2D(int w, int h, int wrap, int filter)
	{
		this.w = w;
		this.h = h;
		this.wrap = wrap;
		this.filter = filter;
		pixels = BufferUtils.createIntBuffer(w * h);
		textureID = 0;
	}
	
	private float clamp(float x, float min, float max)
	{
		return Math.min(Math.max(x, min), max);
	}
	
	public int getRGBAi(int r, int g, int b, int a)
	{
		return ((int) a << 24) | ((int) b << 16) | ((int) g << 8) | r;
	}
	
	// colors on [0, 1]
	public int getRGBAf(float r, float g, float b, float a)
	{
		return getRGBAi((int) (clamp(r, 0, 1)*255), (int) (clamp(g, 0, 1)*255), (int) (clamp(b, 0, 1)*255), (int) (clamp(a, 0, 1)*255));
	}
	
	public int getGLTexture()
	{
		if (textureID != 0)
			return textureID;
		
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrap);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
		
		pixels.position(0);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,  GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_INT, pixels);
		
		return textureID;
	}
	
	// sync gpu texture with local object
	public void sync()
	{
		pixels.position(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
	}

	public void setPixel(int x, int y, int c)
	{
		pixels.put(x + y*w, c);
	}
	
	public int getPixel(int x, int y)
	{
		return pixels.get(x + y*w);
	}
	
	public int getWidth()
	{
		return w;
	}
	
	public int getHeight()
	{
		return h;
	}
}
