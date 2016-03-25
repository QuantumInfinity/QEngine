package net.quantuminfinity.engine.display;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

@Deprecated
public class GLWindow
{
	private final long windowID;
	private ResizeCallback resizeCallback;
	private MouseCallback mouseCallback;
	private GLFWErrorCallback errorCallback;
	
	public GLWindow(int width, int height, String title, boolean resizable)
	{
		if (GLFW.glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GL11.GL_TRUE : GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_RED_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_GREEN_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_BLUE_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_ALPHA_BITS, 8);

		windowID = createWindow(width, height, title);
		update();
	}
	
	public GLWindow(int width, int height, String title, boolean resizable, int major, int minor, boolean compat)
	{
		if (GLFW.glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GL11.GL_TRUE : GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_RED_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_GREEN_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_BLUE_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_ALPHA_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
		if (compat)
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);

		windowID = createWindow(width, height, title);
		update();
	}
	
	private long createWindow(int width, int height, String title)
	{
		long windowID = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if (windowID == 0)
			throw new RuntimeException("Failed to create the GLFW window");
		
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

		GLFW.glfwSetWindowPos(windowID, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		GLFW.glfwMakeContextCurrent(windowID);
		GLFW.glfwShowWindow(windowID);
		GL.createCapabilities();
		
		resizeCallback = new ResizeCallback(width, height);
		mouseCallback = new MouseCallback();
		
		GLFW.glfwSetWindowSizeCallback(windowID, resizeCallback);
		GLFW.glfwSetCursorPosCallback(windowID, mouseCallback);
		return windowID;
	}
	
	public void setTitle(String title)
	{
		GLFW.glfwSetWindowTitle(windowID, title);
	}
	
	public void update()
	{
		GLFW.glfwSwapBuffers(windowID);
		GLFW.glfwPollEvents();
	}
	
	public void close()
	{
		errorCallback.free();
		mouseCallback.free();
		resizeCallback.free();
		GLFW.glfwDestroyWindow(windowID);
		GLFW.glfwTerminate();
	}
	
	public boolean isCloseRequested()
	{
		return GLFW.glfwWindowShouldClose(windowID) == GL11.GL_TRUE;
	}
	
	public long getWindowID()
	{
		return windowID;
	}
	
	public int getWidth()
	{
		return resizeCallback.getWidht();
	}
	
	public int getHeight()
	{
		return resizeCallback.getHeight();
	}
	
	public float getAspect()
	{
		return (float) getWidth() / (float) getHeight();
	}
	
	public void setSync(int sync)
	{
		GLFW.glfwSwapInterval(sync);
	}
	
	public boolean iskeyDown(int key)
	{
		return GLFW.glfwGetKey(windowID, key) == 1;
	}
	
	public boolean isMouseButtonDown(int mb)
	{
		return GLFW.glfwGetMouseButton(windowID, mb) == 1;
	}
	
	public float getMouseX()
	{
		return mouseCallback.getX();
	}
	
	public float getMouseY()
	{
		return mouseCallback.getY();
	}
	
	public float getMouseDX()
	{
		return mouseCallback.getDX();
	}
	
	public float getMouseDY()
	{
		return mouseCallback.getDY();
	}
	
	public void setMouseGrabbed(boolean grabbed)
	{
		GLFW.glfwSetInputMode(windowID, GLFW.GLFW_CURSOR, grabbed ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
	}
	
	public boolean isMouseGrabbed()
	{
		return GLFW.glfwGetInputMode(windowID, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
	}
	
	public boolean wasResized()
	{
		return resizeCallback.wasResized();
	}
	
	public void saveScreenShot(String name, String format)
	{
		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = getWidth();
		int height = getHeight();
		
		ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		
		new Thread(new ScreenShotSaveThread(data, width, height, name, format)).start();
	}
	
	private class ScreenShotSaveThread implements Runnable
	{
		ByteBuffer data;
		int width, height;
		String file, format;
		
		public ScreenShotSaveThread(ByteBuffer data, int width, int height, String file, String format)
		{
			this.data = data;
			this.width = width;
			this.height = height;
			this.format = format != null ? format : "png";
			this.file = file != null ? file : System.nanoTime() + "." + this.format;
		}
		
		@Override
		public void run()
		{
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			   
			for(int x = 0; x < width; x++) 
				for(int y = 0; y < height; y++)
				{
					int i = (x + (width * y)) * 4;
					int r = data.get(i) & 0xFF;
					int g = data.get(i + 1) & 0xFF;
					int b = data.get(i + 2) & 0xFF;
					image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
				}
			
			try {
				ImageIO.write(image, format, new File(file));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public class ResizeCallback extends GLFWWindowSizeCallback
	{
		private int width, height;
		private boolean resized;
		
		public ResizeCallback(int width, int height)
		{
			this.resized = false;
			this.width = width;
			this.height = height;
		}
		
		@Override
		public void invoke(long window, int nWidth, int nHeight)
		{
			width = nWidth;
			height = nHeight;
			resized = true;
		}
		
		public boolean wasResized()
		{
			boolean _resized = resized;
			resized = false;
			return _resized;
		}
		
		public int getWidht()
		{
			return width;
		}
		
		public int getHeight()
		{
			return height;
		}
	}
	
	public class MouseCallback extends GLFWCursorPosCallback
	{
		private int x, y, dx, dy;
		
		public MouseCallback()
		{
			x = 0;
			y = 0;
			dx = 0;
			dy = 0;
		}
		
		@Override
		public void invoke(long window, double nx, double ny)
		{
			dx = (int) nx - x;
			dy = (int) ny - y;
			
			x = (int) nx;
			y = (int) ny;
		}
		
		public int getX()
		{
			return x;
		}
		
		public int getY()
		{
			return y;
		}
		
		public int getDX()
		{
			int _dx = dx;
			dx = 0;
			return _dx;
		}
		
		public int getDY()
		{
			int _dy = dy;
			dy = 0;
			return _dy;
		}
	}
}