package net.quantuminfinity.engine.gl.shader.compute;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL44;

public class ShaderStorageBuffer
{
	final int loc;
	final IntBuffer id;
	
	public ShaderStorageBuffer(int loc)
	{
		this.loc = loc;
		id = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers(id);
	}
	
	public void setData(FloatBuffer data)
	{
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, id.get(0));
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, data, GL15.GL_STATIC_DRAW);
	}
	
	public void setData(ByteBuffer data)
	{
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, id.get(0));
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, data, GL15.GL_STATIC_DRAW);
	}
	
	public void bind()
	{
		GL44.glBindBuffersBase(GL43.GL_SHADER_STORAGE_BUFFER, loc, id);
	}
	
	public void bind(int location)
	{
		GL44.glBindBuffersBase(GL43.GL_SHADER_STORAGE_BUFFER, location, id);
	}
	
	public int getID()
	{
		return id.get(0);
	}
	
	public void destroy()
	{
		GL15.glDeleteBuffers(id);
	}
}
