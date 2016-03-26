package net.quantuminfinity.engine.display;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

public class Display
{
	private static long windowID = 0;
	private static Context ctx;
	private static ResizeCallback resizeCallback;
	
	public static void create(int width, int height, String title, Context ctx)
	{
		Display.ctx = ctx;
		
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		windowID = GLFW.glfwCreateWindow(width, height, "", 0, 0);
		if (windowID == 0)
			throw new RuntimeException("Failed to create the GLFW window");
		
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(windowID, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		GLFW.glfwMakeContextCurrent(windowID);
		
		if (resizeCallback == null)
			resizeCallback = new ResizeCallback();
		GLFW.glfwSetWindowSizeCallback(windowID, resizeCallback);
		Mouse.initialize(windowID);
		Keyboard.initialize(windowID);
		
		GLFW.glfwShowWindow(windowID);
		
		ctx.create();
	}
	
	public static void close()
	{
		resizeCallback.free();
		resizeCallback = null;
		Mouse.terminate();
		Keyboard.terminate();
		GLFW.glfwDestroyWindow(windowID);
		windowID = 0;
	}
	
	public static Context getContext()
	{
		return ctx;
	}
	
	public static void update()
	{
		GLFW.glfwSwapBuffers(windowID);
		GLFW.glfwPollEvents();
	}
	
	public static void setTitle(String title)
	{
		GLFW.glfwSetWindowTitle(windowID, title);
	}
	
	public static boolean isCloseRequested()
	{
		return GLFW.glfwWindowShouldClose(windowID) == GL11.GL_TRUE;
	}
	
	public static long getWindowID()
	{
		return windowID;
	}
	
	public static void setSync(int sync)
	{
		GLFW.glfwSwapInterval(sync);
	}
	
	public static boolean wasResized()
	{
		return resizeCallback.wasResized();
	}
	
	public static int getWidth()
	{
		return resizeCallback.getWidth();
	}
	
	public static int getHeight()
	{
		return resizeCallback.getHeight();
	}
	
	public static float getAspect()
	{
		return resizeCallback.getAspect();
	}
	
	public static void saveScreenShot(String name, String format)
	{
		int width = getWidth();
		int height = getHeight();
		
		ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);
		ctx.readPixels(width, height, data);
		
		new Thread(new ImageSaveThread(data, width, height, name, format)).start();
	}
	
	/** Some GLFW hints. These must be called before the display is created */
	
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
	
	private static class ResizeCallback extends GLFWWindowSizeCallback
	{
		private int width, height;
		private boolean resized;
		
		ResizeCallback()
		{
			this.resized = false;
			this.width = 0;
			this.height = 0;
		}
		
		@Override
		public void invoke(long window, int nWidth, int nHeight)
		{
			width = nWidth;
			height = nHeight;
			resized = true;
		}
		
		boolean wasResized()
		{
			boolean _resized = resized;
			resized = false;
			return _resized;
		}
		
		int getWidth()
		{
			return width;
		}
		
		int getHeight()
		{
			return height;
		}
		
		float getAspect()
		{
			return (float) getWidth() / (float) getHeight();
		}
	}
}
