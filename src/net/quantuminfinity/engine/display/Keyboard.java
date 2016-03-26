package net.quantuminfinity.engine.display;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Keyboard
{
	private static KeyboardCallback keyboardCallback;
	
	public static void initialize(long window)
	{
		if (keyboardCallback == null)
			keyboardCallback = new KeyboardCallback();
		GLFW.glfwSetKeyCallback(window, keyboardCallback);
	}
	
	public static void terminate()
	{
		keyboardCallback.free();
		keyboardCallback = null;
	}
	
	public static boolean isKeyDown(int key)
	{
		return keyboardCallback.isKeyDown(key);
	}
	
	public static boolean wasKeyPressed(int key)
	{
		return keyboardCallback.wasKeyPressed(key);
	}
	
	public static boolean wasKeyReleased(int key)
	{
		return keyboardCallback.wasKeyReleased(key);
	}

	public static boolean isKeyToggled(int key)
	{
		return keyboardCallback.isKeyToggled(key);
	}
	
	public static boolean isKeyRepeated(int key)
	{
		return keyboardCallback.isKeyRepeated(key);
	}
	
	private static class KeyboardCallback extends GLFWKeyCallback
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
