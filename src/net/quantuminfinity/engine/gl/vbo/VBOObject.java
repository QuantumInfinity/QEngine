package net.quantuminfinity.engine.gl.vbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.quantuminfinity.engine.gl.GLObject;
import net.quantuminfinity.engine.util.Util;

public class VBOObject implements GLObject
{
	public int vao;
	public int verticeBuffer;
	public int indiceBuffer;
	public int indiceCount;
	public int rendermode;
	
	public VBOObject(float[] vertices, int[] indices, int rendermode)
	{
		indiceCount = indices.length;
		this.rendermode = rendermode;
		
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		verticeBuffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, verticeBuffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.asFloatBuffer(vertices), GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
		
		indiceBuffer = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, indiceBuffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.asIntBuffer(indices), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void render()
	{
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer);
		GL11.glDrawElements(rendermode, indiceCount, GL11.GL_UNSIGNED_INT, 0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	
	
	public void render(int rendermode)
	{
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer);
		GL11.glDrawElements(rendermode, indiceCount, GL11.GL_UNSIGNED_INT, 0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	public void destroy()
	{
		GL20.glDisableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(verticeBuffer);
		GL15.glDeleteBuffers(indiceBuffer);
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vao);
	}
}
