package net.quantuminfinity.engine.gl.vbo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL15;

public class VBO
{
	int vbo;
	
	public VBO()
	{
		vbo = GL15.glGenBuffers();
	}
	
	public int id()
	{
		return vbo;
	}
	
	public void setData(FloatBuffer buffer)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void setData(ByteBuffer buffer)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void destroy()
	{
		GL15.glDeleteBuffers(vbo);
	}
}
