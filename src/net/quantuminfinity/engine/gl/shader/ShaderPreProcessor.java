package net.quantuminfinity.engine.gl.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import net.quantuminfinity.engine.resource.ResourceLoader;

public class ShaderPreProcessor
{
	public static String process(String source)
	{
		String out = "";
		try {
			BufferedReader reader = new BufferedReader(new StringReader(source));
			String line;
			while((line = reader.readLine()) != null)
				out += line.startsWith("#include ") ? processInclude(line) : line + "\n";
			reader.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return out;
	}
	
	private static String processInclude(String include)
	{
		try	{
			return ResourceLoader.readResource(include.substring(10, include.lastIndexOf("\"")));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
