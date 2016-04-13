package net.quantuminfinity.engine.math.vector;

public class Vector2
{
	public float x;
	public float y;

	public Vector2(float x, float y)
	{
		this.setX(x);
		this.setY(y);
	}

	public Vector2(Vector2 copyFrom)
	{
		this.setX(copyFrom.getX());
		this.setY(copyFrom.getY());
	}

	public Vector2 neg()
	{
		setX(getX() * -1);
		setY(getY() * -1);
		return this;
	}

	public Vector2 clone()
	{
		return new Vector2(this);
	}

	public static Vector2 neg(Vector2 v)
	{

		return new Vector2(v.getX() * -1, v.getY() * -1);
	}

	public Vector2 set(float x, float y)
	{
		this.setX(x);
		this.setY(y);
		return this;
	}
	
	public Vector2 set(Vector2 v)
	{
		this.setX(v.getX());
		this.setY(v.getY());
		return this;
	}

	public Vector2 add(Vector2 v)
	{
		this.setX(v.getX() + getX());
		this.setY(v.getY() + getY());
		return this;
	}
	
	public Vector2 add(float x, float y)
	{
		this.setX(x + getX());
		this.setY(y + getY());
		return this;
	}

	public static Vector2 add(Vector2 v, Vector2 v1)
	{
		return new Vector2(v.getX() + v1.getX(), v.getY() + v1.getY());
	}

	public static Vector2 sub(Vector2 v, Vector2 v1)
	{
		return new Vector2(v.getX() - v1.getX(), v.getY() - v1.getY());
	}

	public Vector2 sub(Vector2 v)
	{
		this.setX(getX() - v.getX());
		this.setY(getY() - v.getY());
		return this;
	}
	
	public Vector2 sub(float x, float y)
	{
		this.setX(getX() - x);
		this.setY(getY() - y);
		return this;
	}
	
	public Vector2 rotate(float angle)
	{
		float l = length();
		float a = getAngRad() + angle;
		setX((float) Math.cos(a) * l);
		setY((float) Math.sin(a) * l);
		return this;
	}

	public Vector2 mul(Vector2 v)
	{
		this.setX(v.getX() * getX());
		this.setY(v.getY() * getY());
		return this;
	}

	public Vector2 scale(float v)
	{
		this.setX(v * getX());
		this.setY(v * getY());
		return this;
	}

	public Vector2 div(Vector2 v)
	{
		this.setX(getX() / v.getX());
		this.setY(getY() / v.getY());
		return this;
	}
	
	public Vector2 div(float x, float y)
	{
		this.setX(getX() / x);
		this.setY(getY() / y);
		return this;
	}
	
	public Vector2 div(float x)
	{
		this.setX(getX() / x);
		this.setY(getY() / x);
		return this;
	}
	
	public Vector2 min(Vector2 v)
	{
		setX(Math.min(getX(), v.getX()));
		setY(Math.min(getY(), v.getY()));
		return this;
	}
	
	public Vector2 max(Vector2 v)
	{
		setX(Math.max(getX(), v.getX()));
		setY(Math.max(getY(), v.getY()));
		return this;
	}
	
	public float distanceTo(Vector2 v)
	{
		float dx = getX() - v.getX();
		float dy = getY() - v.getY();
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	public float dot(Vector2 v)
	{
		return v.getX() * getX() + v.getY() * getY();
	}

	public float length()
	{
		return (float) Math.sqrt(getX() * getX() + getY() * getY());
	}
	
	public Vector2 normalize()
	{
		float l = length();
		setX(getX()/l);
		setY(getY()/l);
		return this;
	}

	public Vector2 setLength(float l)
	{
		normalize();
		scale(l);
		return this;
	}

	public float getAngDeg()
	{
		return (float) Math.toDegrees(Math.atan2(getY(), getX()));
	}
	
	public float getAngRad()
	{
		return (float) Math.atan2(getY(), getX());
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}