package net.quantuminfinity.qengine.bootstrap;

import net.quantuminfinity.qengine.Application;

public class Bootstrap implements IBootstrap
{	
	public Bootstrap()
	{
		
	}

	@Override
	public void start(Class<? extends Application> appMain, NativeLoader nl) throws InstantiationException, IllegalAccessException
	{
		System.setProperty("org.lwjgl.librarypath", nl.getNativePath());
	}
}
