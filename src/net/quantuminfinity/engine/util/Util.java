package net.quantuminfinity.engine.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

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
	
	public static ShortBuffer asShortBuffer(short... arr)
	{
		ShortBuffer sb = BufferUtils.createShortBuffer(arr.length);
		sb.put(arr);
		sb.flip();
		return sb;
	}
	
	public static ByteBuffer asByteBuffer(byte... arr)
	{
		ByteBuffer bb = BufferUtils.createByteBuffer(arr.length);
		bb.put(arr);
		bb.flip();
		return bb;
	}
	
	public static void checkGLError()
	{
		int err;
		if ((err = GL11.glGetError()) != 0)
			System.out.println(err);
	}
}
