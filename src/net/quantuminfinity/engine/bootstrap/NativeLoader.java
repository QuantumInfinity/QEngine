package net.quantuminfinity.engine.bootstrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.quantuminfinity.engine.bootstrap.Platform.Bitmodel;
import net.quantuminfinity.engine.bootstrap.Platform.OSType;
import net.quantuminfinity.engine.bootstrap.Platform.UnsupportedPlatformException;

public class NativeLoader
{
	static String nativesPackage = "/natives/";
	
	File nativePath;
	
	public NativeLoader()
	{
		this("natives");
	}
	
	public NativeLoader(String nativePath)
	{
		this(new File(nativePath));
	}

	public NativeLoader(File nativePath)
	{
		this.nativePath = nativePath;
	}
	
	public void extractNatives() throws IOException
	{
		PlatformLibrary.getLibraryForPlatform(Platform.detect()).extractTo(nativePath);
	}
	
	public String getNativePath()
	{
		return nativePath.getAbsolutePath() + File.separatorChar;
	}
	
	private static void copyFromJar(String from, File to) throws IOException
	{
		if (!from.startsWith("/"))
			throw new IllegalArgumentException("The path has to be absolute (start with '/').");

		if (!to.exists())
			to.createNewFile();
		
		byte[] buffer = new byte[1024];
		int readBytes;
	 
		InputStream is = NativeLoader.class.getResourceAsStream(from);
		if (is == null)
			throw new FileNotFoundException("File " + to + " was not found inside JAR.");
	
		OutputStream os = new FileOutputStream(to);
		try
		{
			while ((readBytes = is.read(buffer)) != -1)
				os.write(buffer, 0, readBytes);
		}finally
		{
			os.close();
			is.close();
		}
	}
	
	static enum PlatformLibrary
	{
		WIN64(OSType.WINDOWS, Bitmodel.X64, "glfw.dll", "jemalloc.dll", "lwjgl.dll", "OpenAL.dll"),
		WIN32(OSType.WINDOWS, Bitmodel.X32, "glfw32.dll", "jemalloc32.dll", "lwjgl32.dll", "OpenAL32.dll"),
		LIN64(OSType.LINUX,   Bitmodel.X64, "libglfw.so", "libjemalloc.so", "liblwjgl.so", "libopenal.so"),
		LIN32(OSType.LINUX,   Bitmodel.X32, "libglfw.so", "libjemalloc.so", "liblwjgl.so", "libopenal.so"),
		OSX64(OSType.MACOSX,  Bitmodel.X64, "libglfw.dylib", "libjemalloc.dylib", "liblwjgl.dylib", "libopenal.dylib");
		
		final OSType os;
		final Bitmodel arch;
		final String[] natives;
		
		PlatformLibrary(OSType os, Bitmodel arch, String... natives)
		{
			this.os = os;
			this.arch = arch;
			this.natives = natives;
		}
		
		public void extractTo(File directory) throws IOException
		{			
			if (directory.exists() && directory.isFile())
				directory.delete();
			if (!directory.exists())
				directory.mkdirs();

			File f;
			for (String library : natives)
			{
				f = new File(directory, library);
				copyFromJar(nativesPackage + library, f);
			}
		}
		
		public static PlatformLibrary getLibraryForPlatform(Platform p)
		{
			for (PlatformLibrary pl : PlatformLibrary.values())
				if (pl.os == p.getOS() && pl.arch == p.getArchitecture())
					return pl;
			throw new UnsupportedPlatformException(p);
		}
	}
}
