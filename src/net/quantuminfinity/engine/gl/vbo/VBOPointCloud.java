package net.quantuminfinity.engine.gl.vbo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class VBOPointCloud
{
	int vbo, points;
	
	public VBOPointCloud(int points)
	{
		this.points = points;
		vbo = GL15.glGenBuffers();
	}
	
	public void render(int mode)
	{
		enable();
		GL11.glDrawArrays(mode, 0, points);
		disable();
	}
	
	public void enable()
	{
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
	}
	
	public void disable()
	{
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}
	
	public void vertexBufferData(FloatBuffer buffer)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public void indexBufferData(IntBuffer buffer)
	{
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	public void destroy()
	{
		GL15.glDeleteBuffers(vbo);
	}
}
