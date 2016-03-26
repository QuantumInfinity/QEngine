package net.quantuminfinity.engine.gl.shader;

import java.util.ArrayList;

import org.lwjgl.opengl.GL20;

public class Shader
{
	protected ArrayList<Integer> shaders;
	protected int program;
	protected boolean compiled;
	
	public Shader()
	{
		shaders = new ArrayList<Integer>();
		compiled = false;
	}
	
	public void bind()
	{
		if(compiled)
			GL20.glUseProgram(program);
	}
	
	public void release()
	{
		GL20.glUseProgram(0);
	}
}
