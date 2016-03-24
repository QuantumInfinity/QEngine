package net.quantuminfinity.qengine.bootstrap;

import net.quantuminfinity.qengine.Application;

public class Launcher
{
	public static void launch(Class<? extends Application> app) throws Exception
	{
		NativeLoader nl = new NativeLoader();
		nl.extractNatives();
		System.setProperty("org.lwjgl.librarypath", nl.getNativePath());
		app.newInstance();
	}
}
