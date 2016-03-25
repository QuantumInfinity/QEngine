package net.quantuminfinity.engine.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Util
{	
	public static FloatBuffer asFloatBuffer(float... arr)
	{
		FloatBuffer fb = BufferUtils.createFloatBuffer(arr.length);
		fb.put(arr);
		fb.flip();
		return fb;
	}
	
	public static IntBuffer asIntBuffer(int... arr)
	{
		IntBuffer ib = BufferUtils.createIntBuffer(arr.length);
		ib.put(arr);
		ib.flip();
		return ib;
	}
}
