package net.quantuminfinity.engine.display;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Mouse
{
	private static MouseMoveCallback mouseMoveCallback;
	private static MouseButtonCallback mouseButtonCallback;
	
	public static void initialize(long window)
	{
		if (mouseMoveCallback == null)
			mouseMoveCallback = new MouseMoveCallback();
		GLFW.glfwSetCursorPosCallback(window, mouseMoveCallback);
		
		if (mouseButtonCallback == null)
			mouseButtonCallback = new MouseButtonCallback();
		GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);
	}
	
	public static void terminate()
	{
		mouseMoveCallback.free();
		mouseMoveCallback = null;
		
		mouseButtonCallback.free();
		mouseButtonCallback = null;
	}
	
	public static void setMouseGrabbed(boolean grabbed)
	{
		if (Display.getWindowID() != 0)
			GLFW.glfwSetInputMode(Display.getWindowID(), GLFW.GLFW_CURSOR, grabbed ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
	}
	
	public static boolean isMouseGrabbed()
	{
		return Display.getWindowID() != 0 && GLFW.glfwGetInputMode(Display.getWindowID(), GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
	}
	
	public static int getMouseX()
	{
		return mouseMoveCallback.getX();
	}
	
	public static int getMouseY()
	{
		return mouseMoveCallback.getY();
	}
	
	public static int getMouseDX()
	{
		return mouseMoveCallback.getDX();
	}
	
	public static int getMouseDY()
	{
		return mouseMoveCallback.getDY();
	}
	
	public static boolean isMouseButtonDown(int button)
	{
		return mouseButtonCallback.isButtonDown(button);
	}
	
	public static boolean wasMouseButtonPressed(int button)
	{
		return mouseButtonCallback.wasButtonPressed(button);
	}
	
	public static boolean wasMouseButtonReleased(int button)
	{
		return mouseButtonCallback.wasButtonReleased(button);
	}
	
	public static boolean isMouseButtonToggled(int button)
	{
		return mouseButtonCallback.isButtonToggled(button);
	}
	
	private static class MouseMoveCallback extends GLFWCursorPosCallback
	{
		private int x, y, dx, dy;
		
		MouseMoveCallback()
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
		
		int getX()
		{
			return x;
		}
		
		int getY()
		{
			return y;
		}
		
		int getDX()
		{
			int _dx = dx;
			dx = 0;
			return _dx;
		}
		
		int getDY()
		{
			int _dy = dy;
			dy = 0;
			return _dy;
		}
	}
	
	private static class MouseButtonCallback extends GLFWMouseButtonCallback
	{
		private static final int NUM_BTNS = GLFW.GLFW_KEY_LAST + 1;
		private static final byte MASK_DOWN = 1;
		private static final byte MASK_PRESSED = 2;
		private static final byte MASK_RELEASED = 4;
		private static final byte MASK_TOGGLED = 8;

		byte states[];
		
		public MouseButtonCallback()
		{
			states = new byte[NUM_BTNS];
			Arrays.fill(states, (byte) 0);
		}
		
		@Override
		public void invoke(long window, int button, int action, int mods)
		{
			if (isValidButton(button))
			{
				set(button, MASK_DOWN, action == GLFW.GLFW_PRESS | action == GLFW.GLFW_REPEAT);
				set(button, MASK_PRESSED, action == GLFW.GLFW_PRESS);
				set(button, MASK_RELEASED, action == GLFW.GLFW_RELEASE);
				if (action == GLFW.GLFW_PRESS)
					set(button, MASK_TOGGLED, !get(button, MASK_TOGGLED));
			}
		}
		
		boolean isButtonDown(int key)
		{
			return isValidButton(key) && get(key, MASK_DOWN);
		}
		
		boolean wasButtonPressed(int key)
		{
			if (!isValidButton(key))
				return false;
			boolean flag = get(key, MASK_PRESSED);
			set(key, MASK_PRESSED, false);
			return flag;
		}
		
		boolean wasButtonReleased(int key)
		{
			if (!isValidButton(key))
				return false;
			boolean flag = get(key, MASK_RELEASED);
			set(key, MASK_RELEASED, false);
			return flag;
		}

		boolean isButtonToggled(int key)
		{
			return isValidButton(key) && get(key, MASK_TOGGLED);
		}
		
		private void set(int key, int masks, boolean value)
		{
			if (value)
				states[key] |= masks;
			else
				states[key] &= ~masks;
		}
		
		private boolean get(int key, int mask)
		{
			return (states[key] & mask) != 0;
		}
		
		private boolean isValidButton(int btn)
		{
			return btn >= 0 && btn < NUM_BTNS;
		}
	}
}
