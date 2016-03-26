package net.quantuminfinity.engine.gl.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import net.quantuminfinity.engine.display.Context;
import net.quantuminfinity.engine.display.Display;
import net.quantuminfinity.engine.math.vector.Vector2;
import net.quantuminfinity.engine.math.vector.Vector3;
import net.quantuminfinity.engine.math.vector.Vector4;
import net.quantuminfinity.engine.resource.ResourceLoader;

public class ShaderProgram
{
	public static final int SHADERTYPE_VERTEX = GL20.GL_VERTEX_SHADER;
	public static final int SHADERTYPE_FRAGMENT = GL20.GL_FRAGMENT_SHADER;
	public static final int SHADERTYPE_GEOMETRY = GL32.GL_GEOMETRY_SHADER;
	public static final int SHADERTYPE_TESS_EVALUATION = GL40.GL_TESS_EVALUATION_SHADER;
	public static final int SHADERTYPE_TESS_CONTROL = GL40.GL_TESS_CONTROL_SHADER;
	public static final int SHADERTYPE_COMPUTE = GL43.GL_COMPUTE_SHADER;
	
	protected ArrayList<Integer> shaders;
	protected int program;
	protected boolean compiled;
	protected FloatBuffer fbuffer;
	protected IntBuffer ibuffer;
	
	protected HashMap<String, Integer> uniforms;
	
	public ShaderProgram()
	{
		if (Display.getContext() != Context.OPENGL)
			throw new UnsupportedOperationException("Unsupported context: " + Display.getContext().name());
		shaders = new ArrayList<Integer>();
		compiled = false;
		uniforms = new HashMap<String, Integer>();
	}
	
	public ShaderProgram bind()
	{
		if(compiled)
			GL20.glUseProgram(program);
		return this;
	}
	
	public ShaderProgram release()
	{
		GL20.glUseProgram(0);
		return this;
	}
	
	public void destroy()
	{
		if (compiled)
		{
			for (Integer shader:shaders)
				GL20.glDeleteShader(shader);
			GL20.glUseProgram(0);
			GL20.glDeleteProgram(program);
		}
	}
	
	public ShaderProgram addShader(String location, int shaderType)
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
		return this;
	}
	
	public ShaderProgram compile()
	{
		program = GL20.glCreateProgram();
		if (program == 0)
			throw new RuntimeException("Failed to create program object");
		
		for (Integer shader:shaders)
			GL20.glAttachShader(program, shader);
		GL20.glLinkProgram(program);
		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
		{
			System.err.println(getLogInfo(program));
			return this;
		}
		for (Integer shader:shaders)
			GL20.glDetachShader(program, shader);
		GL20.glValidateProgram(program);
		if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE)
		{
			System.err.println(getLogInfo(program));
			return this;
		}
		compiled = true;
		return this;
	}
	
	protected int createShader(String filename, int shaderType) throws Exception
	{
		int shader = 0;
		try {
			shader = GL20.glCreateShader(shaderType);

			if(shader == 0)
				return 0;

			GL20.glShaderSource(shader, ShaderPreProcessor.process(ResourceLoader.readResource(filename)));
			GL20.glCompileShader(shader);
			if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
				throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

			return shader;
		}catch(Exception exc)
		{
			GL20.glDeleteShader(shader);
			throw exc;
		}
	}
	
	protected static String getLogInfo(int obj)
	{
		return GL20.glGetShaderInfoLog(obj, GL20.glGetShaderi(obj, GL20.GL_INFO_LOG_LENGTH));
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
}
