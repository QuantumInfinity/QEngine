package net.quantuminfinity.engine;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.quantuminfinity.engine.bootstrap.NativeLoader;
import net.quantuminfinity.engine.util.FPSMeter;

public class Engine
{
	private static GLFWErrorCallback errorCallback;
	static
	{
		NativeLoader nl = new NativeLoader("natives");
		try
		{
			nl.extractNatives();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.setProperty("org.lwjgl.librarypath", nl.getNativePath());
	}
	
	long startTime;
	final float TPS, MAX_FRAMESKIP, FPS_LIMIT;
	FPSMeter meter;
	
	private boolean run;
	private int fps = 0;
	
	public Engine(float tps, float maxFrameskip)
	{
		this(tps, maxFrameskip, 60);	
	}
	
	public Engine(float tpsLimit, float maxFrameskip, float fpsLimit)
	{
		this.TPS = tpsLimit;
		this.MAX_FRAMESKIP = maxFrameskip;
		this.FPS_LIMIT = fpsLimit;
		meter = new FPSMeter();
	}
	
	public void onCreate(){}
	
	public void onUpdate(){}
	
	public void onRender(){}
	
	public void onDestroy(){}
	
	public void onError(Throwable e)
	{
		e.printStackTrace();
		onDestroy();
	}
	
	public final void start()
	{
		try{
			create();
			loop();
			destroy();
		}catch(Throwable e)
		{
			onError(e);
		}
	}
	
	public final void loop()
	{
		double nextTime = getMillis();
		int skippedFrames = 1;
		
		while(run)
		{	
			double currTime = getMillis();
			if((currTime - nextTime) > 500)
				nextTime = currTime;
			if(currTime >= nextTime)
			{
				nextTime += 1000.0/TPS;
				onUpdate();
                
				if((currTime < nextTime) || (skippedFrames > MAX_FRAMESKIP))
				{
					render();
					skippedFrames = 1;
				}
				else
					skippedFrames++;
			} else if (FPS_LIMIT == 0)
			{
				render();
			} else
			{
				long sleepTime = (long)(nextTime - currTime);
				if(sleepTime > 0)
				{
					try {
						Thread.sleep(sleepTime);
					} catch(InterruptedException e) {}
				}
			}
		}
	}
	
	public final void create()
	{
		run = true;
		startTime = System.nanoTime();
		
		if (GLFW.glfwInit() != GLFW.GLFW_TRUE)
			throw new RuntimeException("Unable to initialize GLFW");
		
		if (errorCallback == null)
			GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		
		onCreate();
	}
	
	public final void render()
	{
		onRender();
		fps = meter.updateFPS();
	}
	
	public final void destroy()
	{
		onDestroy();
		errorCallback.free();
		GLFW.glfwTerminate();
	}
	
	public final long getMillis()
	{
		return (System.nanoTime() - startTime)/1000000L;
	}
	
	public final int getFPS()
	{
		return fps;
	}
	
	public final void stop()
	{
		run = false;
	}
}
