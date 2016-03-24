package net.quantuminfinity.qengine.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface IResourceDomain
{
	/**
	 * Get the domain name of this resource domain
	 * @return The name of this domain
	 */
	public String getPrefix();
	
	/**
	 * Open a stream to a file.
	 * @param path
	 * The path to open, without prefix
	 * @return An input stream to the path
	 * @throws FileNotFoundException
	 */
	public InputStream openStream(String path) throws FileNotFoundException;
}
