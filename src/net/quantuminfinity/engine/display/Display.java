package net.quantuminfinity.engine.display;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES20;

public class Display extends DisplayCallbacks
{
	private final long windowID;
	private final Context ctx;
	
	public Display(int width, int height, Context ctx)
	{
		this.ctx = ctx;
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		windowID = GLFW.glfwCreateWindow(width, height, "", 0, 0);
		if (windowID == 0)
			throw new RuntimeException("Failed to create the GLFW window");
		
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(windowID, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		GLFW.glfwMakeContextCurrent(windowID);
		
		setCallbacks(windowID);
		
		GLFW.glfwShowWindow(windowID);
		
		ctx.create();
	}
	
	public void close()
	{
		freeCallbacks();
		GLFW.glfwDestroyWindow(windowID);
	}
	
	public void update()
	{
		GLFW.glfwSwapBuffers(windowID);
		GLFW.glfwPollEvents();
	}
	
	public void setTitle(String title)
	{
		GLFW.glfwSetWindowTitle(windowID, title);
	}
	
	public boolean isCloseRequested()
	{
		return GLFW.glfwWindowShouldClose(windowID) == GL11.GL_TRUE;
	}
	
	public long getWindowID()
	{
		return windowID;
	}
	
	public void setSync(int sync)
	{
		GLFW.glfwSwapInterval(sync);
	}
	
	public void setMouseGrabbed(boolean grabbed)
	{
		GLFW.glfwSetInputMode(windowID, GLFW.GLFW_CURSOR, grabbed ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
	}
	
	public boolean isMouseGrabbed()
	{
		return GLFW.glfwGetInputMode(windowID, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
	}

	public void saveScreenShot(String name, String format)
	{
		int width = getWidth();
		int height = getHeight();
		
		ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);
		ctx.readPixels(width, height, data);
		
		new Thread(new ImageSaveThread(data, width, height, name, format)).start();
	}
	
	/** Some GLFW hints. These must be called before the display is created! */
	
	public static void resizeableHint(boolean resizeable)
	{
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizeable ? GL11.GL_TRUE : GLFW.GLFW_FALSE);
	}
	
	public static void accumHint(int redbits, int greenbits, int bluebits, int alphabits)
	{
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_RED_BITS, redbits);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_GREEN_BITS, greenbits);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_BLUE_BITS, bluebits);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_ALPHA_BITS, alphabits);
	}
	
	public static void versionHint(int major, int minor)
	{
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
	}
	
	private static interface IContext
	{
		public void create();
		public void readPixels(int width, int height, ByteBuffer output);
	}
	
	public static enum Context implements IContext
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
}
