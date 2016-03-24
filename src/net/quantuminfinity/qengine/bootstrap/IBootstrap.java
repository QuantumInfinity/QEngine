package net.quantuminfinity.qengine.bootstrap;

import net.quantuminfinity.qengine.Application;

public interface IBootstrap
{
	public void start(Class<? extends Application> appMain, NativeLoader nl) throws InstantiationException, IllegalAccessException;
}
