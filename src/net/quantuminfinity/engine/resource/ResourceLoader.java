package net.quantuminfinity.engine.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader
{
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
		int idx = file.indexOf(DOMAIN_SEPERATOR);
		if (idx == -1)
			throw new IllegalArgumentException("No domain specified.");
		String prefix = file.substring(0, idx);
		
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
	
	public static void readResource(String resource, LineCallback cbck) throws IOException
	{
		BufferedReader reader = null;
		try {
			String line;
			int ln = 0;
			reader = new BufferedReader(new InputStreamReader(getResourceAsStream(resource)));
			while((line = reader.readLine()) != null)
				cbck.onLine(line, ln++);
			reader.close();
		} catch(IOException e)
		{
			if (reader != null)
				reader.close();			
			throw e;
		}
	}
	
	public static interface LineCallback
	{
		public void onLine(String line, int ln);
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
