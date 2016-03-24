package net.quantuminfinity.qengine.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader
{
	public static final String DOMAIN_FILE = "file:";
	public static final String DOMAIN_PACKAGE = "pkg:";
	
	public static char DOMAIN_SEPERATOR = ':';
	
	static Map<String, IResourceDomain> domains;
	
	static
	{
		domains = new HashMap<String, IResourceDomain>();
		addDomain(new FileDomain());
		addDomain(new PackageDomain());
	}
	
	public static void addDomain(IResourceDomain domain)
	{
		domains.put(domain.getPrefix(), domain);
	}
	
	public static InputStream getResourceAsStream(String file) throws IOException
	{
		String prefix = file.substring(0, file.indexOf(DOMAIN_SEPERATOR));
		
		IResourceDomain domain = domains.get(prefix);
		
		if (domain == null)
			throw new DomainNotFoundException("No domain \"" + prefix + "\" available.");
		
		return domain.openStream(file.substring(file.indexOf(DOMAIN_SEPERATOR) + 1));
	}
	
	public static String readResource(String resource) throws IOException
	{		
		BufferedReader reader = null;
		try {
			String line;
			StringBuilder out = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(getResourceAsStream(resource)));
			while((line = reader.readLine()) != null)
				out.append(line).append('\n');
			reader.close();
			return out.toString();
		} catch(IOException e)
		{
			if (reader != null)
				reader.close();			
			throw e;
		}
	}
	
	public static class DomainNotFoundException extends IOException
	{
		private static final long serialVersionUID = -448605467067509321L;
		
		public DomainNotFoundException(String msg)
		{
			super(msg);
		}
	}
}
