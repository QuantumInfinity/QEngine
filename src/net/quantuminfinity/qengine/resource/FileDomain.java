package net.quantuminfinity.qengine.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileDomain implements IResourceDomain
{
	@Override
	public String getPrefix()
	{
		return "file";
	}

	@Override
	public InputStream openStream(String path) throws FileNotFoundException
	{
		return new FileInputStream(new File(path));
	}
}
