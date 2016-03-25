package net.quantuminfinity.engine.display;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES20;

public class Display
{
	private final long windowID;
	private final Context ctx;
	private ResizeCallback resizeCallback;
	private MouseCallback mouseCallback;
	
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
		
		GLFW.glfwSetWindowSizeCallback(windowID, resizeCallback = new ResizeCallback(width, height));
		GLFW.glfwSetCursorPosCallback(windowID, mouseCallback = new MouseCallback());
		
		GLFW.glfwShowWindow(windowID);
		
		ctx.create();
	}
	
	public void close()
	{
		mouseCallback.free();
		resizeCallback.free();
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
		return GLFW.glfwGetKey(windowID, key) == GLFW.GLFW_TRUE;
	}
	
	public boolean isMouseButtonDown(int mb)
	{
		return GLFW.glfwGetMouseButton(windowID, mb) == GLFW.GLFW_TRUE;
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
