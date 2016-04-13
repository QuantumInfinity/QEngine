package net.quantuminfinity.engine.texture;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TextureFactory
{
	public static final TextureFactory defaultFactory = new TextureFactory();
	
	static
	{
		defaultFactory.setParameter(GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		defaultFactory.setParameter(GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		defaultFactory.setParameter(GL12.GL_TEXTURE_WRAP_R, GL11.GL_REPEAT);
		defaultFactory.setParameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		defaultFactory.setParameter(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	}
	
	private HashMap<Integer, Integer> parameters;
	private int internalformat;
	
	public TextureFactory()
	{
		parameters = new HashMap<Integer, Integer>();
		internalformat = GL11.GL_RGBA8;
	}
	
	public TextureFactory clone()
	{
		TextureFactory tf = new TextureFactory();
		tf.setInternalFormat(internalformat);
		for (Entry<Integer, Integer> par : parameters.entrySet())
			tf.setParameter(par.getKey(), par.getValue());
		return tf;
	}
	
	public TextureFactory setParameter(int key, int value)
	{
		parameters.put(key, value);
		return this;
	}
	
	public TextureFactory setInternalFormat(int internalformat)
	{
		this.internalformat = internalformat;
		return this;
	}
	
	private int generateTexture(int target)
	{
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(target, textureID);

		for (Entry<Integer, Integer> par : parameters.entrySet())
			GL11.glTexParameteri(target, par.getKey(), par.getValue());

		return textureID;
	}
	
	public int generateTex1D(int size, int format, int type, ByteBuffer pixels)
	{
		int textureID = generateTexture(GL11.GL_TEXTURE_1D);
		GL11.glTexImage1D(GL11.GL_TEXTURE_1D, 0, internalformat, size, 0, format, type, pixels);
		return textureID;
	}
	
	public int generateTex2D(int width, int height, int format, int type, ByteBuffer pixels)
	{
		int textureID = generateTexture(GL11.GL_TEXTURE_2D);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalformat, width, height, 0, format, type, pixels);
		return textureID;
	}
	
	public int generateTex3D(int width, int height, int depth, int format, int type, ByteBuffer pixels)
	{
		int textureID = generateTexture(GL12.GL_TEXTURE_3D);
		GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, internalformat, width, height, depth, 0, format, type, pixels);
		return textureID;
	}
}
