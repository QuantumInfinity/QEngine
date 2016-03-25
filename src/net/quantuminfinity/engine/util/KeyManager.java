package net.quantuminfinity.engine.util;

import java.util.HashMap;
import java.util.Map.Entry;

import net.quantuminfinity.engine.display.GLWindow;

public class KeyManager
{
	public static final int STATE_DOWN = 0x1;
	public static final int STATE_PRESSED = 0x2;
	
	HashMap<Integer, Integer> keys;
	
	public KeyManager()
	{
		keys = new HashMap<Integer, Integer>();
	}
	
	public void addKey(int key)
	{
		keys.put(key, 0);
	}
	
	public void removeKey(int key)
	{
		keys.remove(key); // make sure it calls the right method.
	}
	
	public void update(GLWindow w)
	{
		for (Entry<Integer, Integer> e:keys.entrySet())
		{
			if (w.iskeyDown(e.getKey()))
			{
				if ((e.getValue() & STATE_DOWN) == 0) // first test
					e.setValue(e.getValue() | STATE_PRESSED);
				e.setValue(e.getValue() | STATE_DOWN);
			}else
			{
				e.setValue(e.getValue() & ~STATE_DOWN);
			}
		}
	}
	
	public boolean isDown(int key)
	{
		if (!keys.containsKey(key))
			return false;
		return (keys.get(key) & STATE_DOWN) != 0;
	}
	
	public boolean wasPressed(int key)
	{
		if (!keys.containsKey(key))
			return false;
		int v = keys.get(key);
		boolean pressed = (v & STATE_PRESSED) != 0;
		keys.put(key, v & ~STATE_PRESSED);
		return pressed;
	}
}
