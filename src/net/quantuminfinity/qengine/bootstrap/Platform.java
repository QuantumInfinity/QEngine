package net.quantuminfinity.qengine.bootstrap;

import java.util.Locale;

public class Platform
{	
	private static Platform platform;
	
	private OSType operatingSystem;
	private Bitmodel architecture;
	
	private Platform()
	{
		String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0))
			operatingSystem = OSType.MACOSX;
		else if (OS.indexOf("win") >= 0)
			operatingSystem = OSType.WINDOWS;
		else if (OS.indexOf("nux") >= 0)
			operatingSystem = OSType.LINUX;
		else
			operatingSystem = OSType.OTHER;
		
		String arch = System.getProperty("sun.arch.data.model");
		if (arch.equals("64"))
			architecture = Bitmodel.X64;
		else if (arch.equals("86") || arch.equals("32"))
			architecture = Bitmodel.X32;
		else
			architecture = Bitmodel.OTHER;
	}
	
	public OSType getOS()
	{
		return operatingSystem;
	}
	
	public Bitmodel getArchitecture()
	{
		return architecture;
	}
	
	public static Platform detect()
	{
		if (platform == null)
			platform = new Platform();
		return platform;
	}
	
	public enum OSType
	{
		 WINDOWS, MACOSX, LINUX, OTHER
	};
	
	public enum Bitmodel
	{
		X32, X64, OTHER;
	};
	
	public static class UnsupportedPlatformException extends RuntimeException
	{
		private static final long serialVersionUID = -4640069963922721189L;

		public UnsupportedPlatformException(Platform p)
		{
			super("Unsupported platform: os: " + p.getOS().name() + " architecture: " + p.getArchitecture().name());
		}
	}
}
