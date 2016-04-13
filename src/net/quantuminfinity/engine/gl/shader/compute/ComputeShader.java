package net.quantuminfinity.engine.gl.shader.compute;

import java.util.HashMap;

import org.lwjgl.opengl.ARBComputeShader;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;

import net.quantuminfinity.engine.gl.shader.ShaderProgram;

public class ComputeShader extends ShaderProgram
{
	int numGroupsX, numGroupsY, numGroupsZ;
	
	public ComputeShader(String shaderloc, int numGroupsX, int numGroupsY, int numGroupsZ)
	{
		addShader(shaderloc, ARBComputeShader.GL_COMPUTE_SHADER);
		compile();
		
		setGroups(numGroupsX, numGroupsY, numGroupsZ);
	}
	
	public ComputeShader(String shaderloc, int numGroupsX, int numGroupsY, int numGroupsZ, HashMap<String, String> defs)
	{
		defines.putAll(defs);
		addShader(shaderloc, ARBComputeShader.GL_COMPUTE_SHADER);
		compile();
		
		setGroups(numGroupsX, numGroupsY, numGroupsZ);
	}
	
	public void setGroups(int numGroupsX, int numGroupsY, int numGroupsZ)
	{
		this.numGroupsX = numGroupsX;
		this.numGroupsY = numGroupsY;
		this.numGroupsZ = numGroupsZ;
	}
	
	public void dispatch()
	{
		GL43.glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ);
		GL42.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
		GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
	}
}
