package net.quantuminfinity.qengine.bootstrap;

import net.quantuminfinity.qengine.Application;

public class Launcher
{
	public static void launch(Class<? extends Application> app) throws Exception
	{
		NativeLoader nl = new NativeLoader();
		nl.extractNatives();
		
		QEngineClassLoader qecl = new QEngineClassLoader();
		@SuppressWarnings("unchecked")
		Class<Bootstrap> bootstrapClass = (Class<Bootstrap>) qecl.findClass(Bootstrap.class.getCanonicalName());
		IBootstrap bootstrap = bootstrapClass.newInstance();
		
		bootstrap.start(app, nl);
		
		bootstrapClass = null;
		bootstrap = null;
		qecl = null;
		System.gc();
		nl.deleteNatives();
	}
}
