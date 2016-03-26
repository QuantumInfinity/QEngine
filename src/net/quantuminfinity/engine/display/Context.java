package net.quantuminfinity.engine.display;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES20;

interface IContext
{
	public void create();
	public void readPixels(int width, int height, ByteBuffer output);
}

public enum Context implements IContext
{
	OPENGL
	{
		@Override
		public void create()
		{
			GL.createCapabilities();
		}

		@Override
		public void readPixels(int width, int height, ByteBuffer output)
		{
			GL11.glReadBuffer(GL11.GL_FRONT);
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, output);
		}
	},
	
	OPENGL_ES
	{
		@Override
		public void create()
		{
			GLES.createCapabilities();
		}

		@Override
		public void readPixels(int width, int height, ByteBuffer output)
		{
			GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, output);
		}
	};
}