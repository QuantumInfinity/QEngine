package net.quantuminfinity.engine.util;


public class FPSMeter
{
	long lastFrame;
	int fps, lastfps = 0;
	long lastFPS;
	
	public FPSMeter()
	{
		lastFPS = getTime();
	}
	
	public int updateFPS()
	{
		if (getTime() - lastFPS > 1000)
		{
			lastfps = fps;
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
		return lastfps;
	}   
	
	public long getTime()
	{
	    return System.currentTimeMillis();
	}		
}
