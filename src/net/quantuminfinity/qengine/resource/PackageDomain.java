package net.quantuminfinity.qengine.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PackageDomain implements IResourceDomain
{
	@Override
	public String getPrefix()
	{
		return "pkg";
	}

	@Override
	public InputStream openStream(String path) throws FileNotFoundException
	{
		return ResourceLoader.class.getResourceAsStream(path);
	}
}
