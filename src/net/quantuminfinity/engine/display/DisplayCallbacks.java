package net.quantuminfinity.engine.display;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

public class DisplayCallbacks
{
	private ResizeCallback resizeCallback;
	private MouseMoveCallback mouseMoveCallback;
	private MouseButtonCallback mouseButtonCallback;
	private KeyboardCallback keyboardCallback;
	
	public void setCallbacks(long window)
	{
		GLFW.glfwSetWindowSizeCallback(window, resizeCallback = new ResizeCallback());
		GLFW.glfwSetCursorPosCallback(window, mouseMoveCallback = new MouseMoveCallback());
		GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback = new MouseButtonCallback());
		GLFW.glfwSetKeyCallback(window, keyboardCallback = new KeyboardCallback());
	}
	
	public void freeCallbacks()
	{
		resizeCallback.free();
		mouseMoveCallback.free();
		mouseButtonCallback.free();
		keyboardCallback.free();
	}
	
	/** mouse move*/
	
	public int getMouseX()
	{
		return mouseMoveCallback.getX();
	}
	
	public int getMouseY()
	{
		return mouseMoveCallback.getY();
	}
	
	public int getMouseDX()
	{
		return mouseMoveCallback.getDX();
	}
	
	public int getMouseDY()
	{
		return mouseMoveCallback.getDY();
	}
	
	/** window */
	
	public boolean wasResized()
	{
		return resizeCallback.wasResized();
	}
	
	public int getWidth()
	{
		return resizeCallback.getWidth();
	}
	
	public int getHeight()
	{
		return resizeCallback.getHeight();
	}
	
	public float getAspect()
	{
		return resizeCallback.getAspect();
	}
	
	/** mouse buttons */
	
	public boolean isMouseButtonDown(int button)
	{
		return mouseButtonCallback.isButtonDown(button);
	}
	
	public boolean wasMouseButtonPressed(int button)
	{
		return mouseButtonCallback.wasButtonPressed(button);
	}
	
	public boolean wasMouseButtonReleased(int button)
	{
		return mouseButtonCallback.wasButtonReleased(button);
	}
	
	public boolean isMouseButtonToggled(int button)
	{
		return mouseButtonCallback.isButtonToggled(button);
	}
	
	/** keyboard keys */
	
	public boolean isKeyDown(int key)
	{
		return keyboardCallback.isKeyDown(key);
	}
	
	public boolean wasKeyPressed(int key)
	{
		return keyboardCallback.wasKeyPressed(key);
	}
	
	public boolean wasKeyReleased(int key)
	{
		return keyboardCallback.wasKeyReleased(key);
	}

	public boolean isKeyToggled(int key)
	{
		return keyboardCallback.isKeyToggled(key);
	}
	
	public boolean isKeyRepeated(int key)
	{
		return keyboardCallback.isKeyRepeated(key);
	}
	
	/** callback classes */
	
	private class MouseMoveCallback extends GLFWCursorPosCallback
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
	
	private class ResizeCallback extends GLFWWindowSizeCallback
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
	
	private class MouseButtonCallback extends GLFWMouseButtonCallback
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
	
	private class KeyboardCallback extends GLFWKeyCallback
	{
		private static final int NUM_KEYS = GLFW.GLFW_KEY_LAST + 1;
		private static final byte MASK_DOWN = 1;
		private static final byte MASK_PRESSED = 2;
		private static final byte MASK_RELEASED = 4;
		private static final byte MASK_TOGGLED = 8;
		private static final byte MASK_REPEATED = 16;

		byte states[];
		
		public KeyboardCallback()
		{
			states = new byte[NUM_KEYS];
			Arrays.fill(states, (byte) 0);
		}
		
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods)
		{
			if (isValidKey(key))
			{
				set(key, MASK_DOWN, action == GLFW.GLFW_PRESS | action == GLFW.GLFW_REPEAT);
				set(key, MASK_PRESSED, action == GLFW.GLFW_PRESS);
				set(key, MASK_RELEASED, action == GLFW.GLFW_RELEASE);
				set(key, MASK_REPEATED, action == GLFW.GLFW_REPEAT);
				if (action == GLFW.GLFW_PRESS)
					set(key, MASK_TOGGLED, !get(key, MASK_TOGGLED));
			}
		}
		
		boolean isKeyDown(int key)
		{
			return isValidKey(key) && get(key, MASK_DOWN);
		}
		
		boolean wasKeyPressed(int key)
		{
			if (!isValidKey(key))
				return false;
			boolean flag = get(key, MASK_PRESSED);
			set(key, MASK_PRESSED, false);
			return flag;
		}
		
		boolean wasKeyReleased(int key)
		{
			if (!isValidKey(key))
				return false;
			boolean flag = get(key, MASK_RELEASED);
			set(key, MASK_RELEASED, false);
			return flag;
		}

		boolean isKeyToggled(int key)
		{
			return isValidKey(key) && get(key, MASK_TOGGLED);
		}
		
		boolean isKeyRepeated(int key)
		{
			return isValidKey(key) && get(key, MASK_REPEATED);
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
		
		private boolean isValidKey(int btn)
		{
			return btn >= 0 && btn < NUM_KEYS;
		}
	}
}
