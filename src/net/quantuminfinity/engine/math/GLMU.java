package net.quantuminfinity.engine.math;

import org.lwjgl.opengl.GL11;

import net.quantuminfinity.engine.display.Display;
import net.quantuminfinity.engine.gl.util.Project;

public class GLMU
{
	public static void initGL2D(double width, double height)
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, 0, height, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	public static void initGL2D(double l, double r, double b, double t)
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(l, r, b, t, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	public static void initGL3D(int width, int height, float fov, float near, float far)
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		Project.gluPerspective(fov, (float) width / (float) height, near, far);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	//	GL11.glViewport(0, 0, width, height);
	}
	
	public static void initGL2D()
	{
		initGL2D(Display.getWidth(), Display.getHeight());
	}
	
	public static void initGL3D(float fov, float near, float far)
	{
		initGL3D(Display.getWidth(), Display.getHeight(), fov, near, far);
	}
}
