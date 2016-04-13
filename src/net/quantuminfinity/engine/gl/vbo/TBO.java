package net.quantuminfinity.engine.gl.vbo;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;

public class TBO
{
	int tbo, tex;
	
	public TBO(int format, ByteBuffer data)
	{
		tbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, tbo);
		GL15.glBufferData(GL31.GL_TEXTURE_BUFFER, data, GL15.GL_STATIC_DRAW);
		
		tex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL31.glTexBuffer(GL31.GL_TEXTURE_BUFFER, format, tbo);
		
		GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, 0);
	}
	
	public int tbo()
	{
		return tbo;
	}
	
	public int tex()
	{
		return tex;
	}

	public void destroy()
	{
		GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, tbo);
		GL11.glDeleteTextures(tex);
		GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, 0);
		GL15.glDeleteBuffers(tbo);
	}
}
