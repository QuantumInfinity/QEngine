package net.quantuminfinity.engine.util;

public class RBuffer<T> {
	
	private T[] buffer;
	
	@SuppressWarnings("unchecked")
	public RBuffer(int size)
	{
		buffer = (T[]) new Object[size];
	}
	
	public RBuffer<T> push(T o)
	{
		for (int i=buffer.length-2; i>=0; i--)
		{
			buffer[i+1] = buffer[i];
		}
		buffer[0] = o;
		return this;
	}
	
	public int getSize()
	{
		return buffer.length;
	}
	
	public T[] getObjects()
	{
		return buffer;
	}
	
	public void setSize(int size)
	{
		@SuppressWarnings("unchecked")
		T[] tmp = (T[]) new Object[size];
		for (int i=0;i<tmp.length;i++)
		{
			if (i <= buffer.length)
			{
				tmp[i] = buffer[i];
			}else{
				tmp[i] = null;
			}
		}
		buffer = tmp;
	}
}