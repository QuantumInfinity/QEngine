package net.quantuminfinity.engine.gl.shader;

import java.io.IOException;
import java.util.HashMap;

import net.quantuminfinity.engine.resource.ResourceLoader;
import net.quantuminfinity.engine.resource.ResourceLoader.LineCallback;

public class ShaderPreProcessor
{
	public static String tryread(String source, HashMap<String, String> defines)
	{
		try
		{
			return read(source, defines);
		} catch (IOException e)
		{
			System.out.println("test");
			e.printStackTrace();
		}
		return null;
	}
	
	public static String read(String resource, HashMap<String, String> defines) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		ResourceLoader.readResource(resource, new LineCallback()
		{
			@Override
			public void onLine(String line, int ln)
			{
				if (line.startsWith("#include "))
					sb.append(processInclude(line));
				else if (line.startsWith("#ppdefine "))
				{
					String var = line.substring(10, line.length());
					if (!defines.containsKey(var))
						throw new IllegalStateException("Define value " + var + " is not set!");
					sb.append("#define " + var + " " + defines.get(var));
				}
				else
					sb.append(line);
				sb.append("\n");
			}
		});
		return sb.toString();
	}
	
	private static String processInclude(String include)
	{
		try	{
			return read(include.substring(10, include.lastIndexOf("\"")), null);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
