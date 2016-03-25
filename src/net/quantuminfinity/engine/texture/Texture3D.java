package net.quantuminfinity.engine.texture;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class Texture3D
{
	public static final int BPC_8 = 8;
	public static final int BPC_16 = 16;
	
	final int bpc, w, h, d;
	long[] pixels;
	
	public Texture3D(int w, int h, int d, int bpc)
	{
		if (bpc != 8 && bpc != 16)
			throw new IllegalArgumentException("Texture3D can only have 8 and 16 bits per channel.");
		this.bpc = bpc;
		this.w = w;
		this.h = h;
		this.d = d;
		pixels = new long[w*h*d];
	}
	
	public long getRGBA(int r, int g, int b, int a)
	{
		return ((long) r << (3*bpc)) | ((long) g << (2*bpc)) | ((long) b << bpc) | (long) a;
	}
	
	// colors on [0, 1]
	public long getRGBA(float r, float g, float b, float a)
	{
		int range = getBPCRange(bpc);
		return ((long) (r*range) << (3*bpc)) | ((long) (g*range) << (2*bpc)) | ((long) (b*range) << bpc) | (long) (a*range);
	}
	
	public int getGLTexture(int wrap, int filter)
	{
		ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length*bpc/8*4);
		
		int format, type;
		
		switch(bpc)
		{
		case BPC_8:
			for (int i=0; i<pixels.length; i++)
			{
				long pixel = pixels[i];
				buffer.put((byte) ((pixel >> 24) & 0xFF));
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >>  8) & 0xFF));
				buffer.put((byte) ((pixel >>  0) & 0xFF));
			}
			format = GL11.GL_RGBA8;
			type = GL11.GL_UNSIGNED_BYTE;
			break;
		case BPC_16:
			for (int i=0; i<pixels.length; i++)
			{
				long pixel = pixels[i];
				buffer.putShort((short) ((pixel >> 48) & 0xFFFF));
				buffer.putShort((short) ((pixel >> 32) & 0xFFFF));
				buffer.putShort((short) ((pixel >> 16) & 0xFFFF));
				buffer.putShort((short) ((pixel >>  0) & 0xFFFF));
			}
			format = GL11.GL_RGBA16;
			type = GL11.GL_UNSIGNED_SHORT;
			break;
		default:
			return 0;
		}
		
		buffer.flip();
		
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureID);
		
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, wrap);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, wrap);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, wrap);
		
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, filter);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, filter);
		
		GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, format, w, h, d, 0, GL11.GL_RGBA, type, buffer);
		
		return textureID;
	}

	public void setPixel(int x, int y, int z, long c)
	{
		pixels[x + y*w + z*w*h] = c;
	}
	
	public long getPixel(int x, int y, int z)
	{
		return pixels[x + y*w + z*w*h];
	}
	
	public int getWidth()
	{
		return w;
	}
	
	public int getHeight()
	{
		return h;
	}
	
	public int getDepth()
	{
		return d;
	}
	
	public int getBPCRange(int bpc)
	{
		switch(bpc)
		{
		case BPC_8: return 256;
		case BPC_16: return 63356;
		default: return 0;
		}
	}
}
