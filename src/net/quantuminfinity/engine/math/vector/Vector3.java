package net.quantuminfinity.engine.math.vector;

import java.util.Locale;

public class Vector3 {
	
	public float x;
	public float y;
	public float z;
	
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector3(Vector3 v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public float length()
	{
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	public float lengthSq()
	{
		return x*x + y*y + z*z;
	}
	
	public Vector3 clone()
	{
		return new Vector3(x, y, z);
	}
	
	public Vector3 set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Vector3 set(Vector3 v)
	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
	}
	
	public Vector3 min(Vector3 v)
	{
		this.x = Math.min(x, v.x);
		this.y = Math.min(y, v.y);
		this.z = Math.min(z, v.z);
		return this;
	}
	
	public Vector3 max(Vector3 v)
	{
		this.x = Math.max(x, v.x);
		this.y = Math.max(y, v.y);
		this.z = Math.max(z, v.z);
		return this;
	}
	
	public Vector3 add(Vector3 v)
	{
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	public Vector3 add(Vector3 v, float scale)
	{
		this.x += v.x * scale;
		this.y += v.y * scale;
		this.z += v.z * scale;
		return this;
	}
	
	public Vector3 addCrossed(Vector3 v, Vector3 c, float scale)
	{
		float x = (v.y*c.z - v.z*c.y);
		float y = (v.z*c.x - v.x*c.z);
		float z = (v.x*c.y - v.y*c.x);
		float l = (float) Math.sqrt(x*x + y*y + z*z);
		
		this.x += x/l * scale;
		this.y += y/l * scale;
		this.z += z/l * scale;
		return this;
	}
	
	public Vector3 sub(Vector3 v)
	{
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}

	public Vector3 normalise()
	{
		float length = length();
		this.x /= length;
		this.y /= length;
		this.z /= length;
		return this;
	}
	
	public Vector3 scale(float n)
	{
		this.x *= n;
		this.y *= n;
		this.z *= n;
		return this;
	}
	

	public Vector3 div(float n)
	{
		this.x /= n;
		this.y /= n;
		this.z /= n;
		return this;
	}
	
	public Vector3 addl(float length)
	{		
		Vector3 v = clone().normalise().scale(length);
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}

	public boolean is(Vector3 v)
	{
		if (v.x == this.x && v.y == this.y && v.z == this.z)
			return true;
		return false;
	}
	
	public float dot(Vector3 b)
	{
		return x * b.x + y * b.y + z * b.z;
	}
	
	public Vector3 cross(Vector3 v)
	{
		float tx = y*v.z - z*v.y;
		float ty = z*v.x - x*v.z;
		z = x*v.y - y*v.x;
		y = ty;
		x = tx;
		
		return this;
	}
	
	float tmpx,tmpy,tmpz;
	public Vector3 rotate(float angle,Vector3 axis)
	{
		axis.normalise();
		float s = (float) Math.sin(angle);
	    float c = (float) Math.cos(angle);
	    float oc = 1.0f - c;
		x = x*(oc * axis.x * axis.x + c) + y*(oc * axis.x * axis.y - axis.z * s) + z*(oc * axis.z * axis.x + axis.y * s);
		y = x*(oc * axis.x * axis.y + axis.z * s) + y*(oc * axis.y * axis.y + c) + z*(oc * axis.y * axis.z - axis.x * s);
		z = x*(oc * axis.z * axis.x - axis.y * s) + y*(oc * axis.y * axis.z + axis.x * s) + z*(oc * axis.z * axis.z + c);
		return this;
	}
	
	@Override
	public String toString()
	{
		return String.format(Locale.ENGLISH, "(%.2f, %.2f, %.2f)", x, y, z);
	}
}
