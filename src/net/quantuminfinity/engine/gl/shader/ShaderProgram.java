package net.quantuminfinity.engine.gl.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.quantuminfinity.engine.math.vector.Vector2;
import net.quantuminfinity.engine.math.vector.Vector3;
import net.quantuminfinity.engine.math.vector.Vector4;
import net.quantuminfinity.engine.resource.ResourceLoader;

public class ShaderProgram
{	
	protected ArrayList<Integer> shaders;
	protected int program;
	protected boolean compiled;
	protected FloatBuffer fbuffer;
	protected IntBuffer ibuffer;
	
	protected HashMap<String, Integer> uniforms;
	
	public ShaderProgram()
	{
		shaders = new ArrayList<Integer>();
		compiled = false;
		uniforms = new HashMap<String, Integer>();
	}
	
	public int getUniformLocation(String uniform)
	{
		Integer loc = uniforms.get(uniform);
		if (loc == null)
		{
			 loc = GL20.glGetUniformLocation(program, uniform);
			 uniforms.put(uniform, loc);
		}
		return loc;
	}
	
	public void setUniform(String uniform, int value)
	{
		GL20.glUniform1i(getUniformLocation(uniform), value);
	}
	
	public void setUniform(String uniform, int[] value)
	{
		ibuffer = BufferUtils.createIntBuffer(value.length);
		ibuffer.put(value);
		ibuffer.rewind();
		GL20.glUniform1iv(getUniformLocation(uniform), ibuffer);
	}
	
	public void setUniform(String uniform, float[] value)
	{
		fbuffer = BufferUtils.createFloatBuffer(value.length);
		fbuffer.put(value);
		fbuffer.rewind();
		GL20.glUniform1fv(getUniformLocation(uniform), fbuffer);
	}
	
	public void setUniform(String uniform, Vector2 value)
	{
		GL20.glUniform2f(getUniformLocation(uniform), value.x, value.y);
	}
	
	public void setUniform(String uniform, Vector3 value)
	{
		GL20.glUniform3f(getUniformLocation(uniform), value.x, value.y, value.z);
	}
	
	public void setUniform(String uniform, Vector4 value)
	{
		GL20.glUniform4f(getUniformLocation(uniform), value.x, value.y, value.z, value.w);
	}
	
	public void setUniform(String uniform, float x)
	{
		GL20.glUniform1f(getUniformLocation(uniform), x);
	}
	
	public void setUniform(String uniform, float x, float y)
	{
		GL20.glUniform2f(getUniformLocation(uniform), x, y);
	}
	
	public void setUniform(String uniform, float x, float y, float z)
	{
		GL20.glUniform3f(getUniformLocation(uniform), x, y, z);
	}
	
	public void setUniform(String uniform, float x, float y, float z, float w)
	{
		GL20.glUniform4f(getUniformLocation(uniform), x, y, z, w);
	}
	
	public void bind()
	{
		if(compiled)
			ARBShaderObjects.glUseProgramObjectARB(program);
	}
	
	public void release()
	{
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	public void addShader(String location, int shaderType)
	{
		if (compiled)
			throw new UnsupportedOperationException("Shader already compiled");
		int shader = 0;
		try{
			shader = createShader(location, shaderType);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if (shader == 0)
			throw new RuntimeException("Error in shader");
		shaders.add(shader);
	}
	
	public void compile()
	{
		program = ARBShaderObjects.glCreateProgramObjectARB();
		if (program == 0)
			throw new RuntimeException("Failed to create program object");
		
		for (Integer shader:shaders)
			ARBShaderObjects.glAttachObjectARB(program, shader);
		ARBShaderObjects.glLinkProgramARB(program);
		if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE)
		{
			System.err.println(getLogInfo(program));
			return;
		}
		for (Integer shader:shaders)
			ARBShaderObjects.glDetachObjectARB(program, shader);
		ARBShaderObjects.glValidateProgramARB(program);
		if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE)
		{
			System.err.println(getLogInfo(program));
			return;
		}
		compiled = true;
	}
	
	public void destroy()
	{
		if (compiled)
		{
			for (Integer shader:shaders)
				ARBShaderObjects.glDeleteObjectARB(shader);
			GL20.glUseProgram(0);
			GL20.glDeleteProgram(program);
		}
	}
	
	protected int createShader(String filename, int shaderType) throws Exception
	{
		int shader = 0;
		try {
			shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

			if(shader == 0)
				return 0;

			ARBShaderObjects.glShaderSourceARB(shader, ShaderPreProcessor.process(ResourceLoader.readResource(filename)));
			ARBShaderObjects.glCompileShaderARB(shader);
			if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
				throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

			return shader;
		}catch(Exception exc)
		{
			ARBShaderObjects.glDeleteObjectARB(shader);
			throw exc;
		}
	}
	
	protected static String getLogInfo(int obj)
	{
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	
	
}
