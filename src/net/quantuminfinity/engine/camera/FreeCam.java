package net.quantuminfinity.engine.camera;

import org.lwjgl.glfw.GLFW;

import net.quantuminfinity.engine.display.Keyboard;
import net.quantuminfinity.engine.display.Mouse;
import net.quantuminfinity.engine.gl.util.Project;
import net.quantuminfinity.engine.math.vector.Vector2;
import net.quantuminfinity.engine.math.vector.Vector3;

public class FreeCam
{
	public Vector3 pos, dir, up;
	public Vector2 sensivity;
	public float speed = 4/60f, smod = 1, smodmod = 1.01f;
	
	public FreeCam()
	{
		this(new Vector3(0, 0, 0));
	}
	
	public FreeCam(Vector3 pos)
	{
		this(pos, new Vector3(0, 0, -1));
	}
	
	public FreeCam(Vector3 pos, Vector3 dir)
	{
		this(pos, dir, new Vector3(0, 1, 0));
	}
	
	public FreeCam(Vector3 pos, Vector3 dir, Vector3 up)
	{
		this.pos = pos;
		this.dir = dir;
		this.up = up;
		sensivity = new Vector2(.005f, .01f);
	}
	
	public void update()
	{
		if (Mouse.wasMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
		{
			Mouse.setMouseGrabbed(true);
			Mouse.getMouseDX();
			Mouse.getMouseDY();
		}
		
		if (Mouse.wasMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT) || Keyboard.wasKeyPressed(GLFW.GLFW_KEY_ESCAPE))
			Mouse.setMouseGrabbed(false);
		
		if (!Mouse.isMouseGrabbed())
			return;
		
		float phi = (float) Math.acos(dir.y);
		float theta = (float) Math.atan2(dir.z, dir.x);
		
		theta += Mouse.getMouseDX() * sensivity.x;
		phi += Mouse.getMouseDY() * sensivity.x;
		
		dir.x = (float) (Math.cos(theta) * Math.sin(phi));
		dir.y = (float) Math.cos(phi);
		dir.z = (float) (Math.sin(theta) * Math.sin(phi));
		
		float spd = speed;
		
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
			spd *= 10;
		
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_W))
			pos.add(dir, spd);
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_S))
			pos.add(dir, -spd);
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_A))
			pos.addCrossed(dir, up, -spd);
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_D))
			pos.addCrossed(dir, up, spd);
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE))
			pos.y += spd;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			pos.y -= spd;
	}
	
	public Vector3 getPos()
	{
		return pos;
	}
	
	public Vector3 getDir()
	{
		return dir;
	}
	
	public void apply()
	{
		Project.gluLookAt(pos.x, pos.y, pos.z, pos.x + dir.x, pos.y + dir.y, pos.z + dir.z, up.x, up.y, up.z);
	}
}
