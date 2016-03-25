package net.quantuminfinity.engine.gl.util;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBEasyFont;

public class Timer
{	
	boolean measuring;
	long time;
	float dtime;
	int samples;
	
	float[] measurements;
	float max, mean, min;
	int idx;
	
	ByteBuffer charBuffer;
	
	public Timer(int steps)
	{
		measurements = new float[steps];
		measuring = false;
		reset();
		
		charBuffer = BufferUtils.createByteBuffer(15 * 270);
	}
	
	public void reset()
	{
		time = 0;
		dtime = 0;
		samples = 0;
		max = mean = 0;
		min = Integer.MAX_VALUE;
	}
	
	public void startMeasurement()
	{
		if (measuring)
			return;
		time = System.nanoTime();
		measuring = true;
	}
	
	public void stopMeasurement()
	{
		if (!measuring)
			return;
		measuring = false;
		addTiming((System.nanoTime() - time) / 1000000f);
	}
	
	public void addTiming(float millis)
	{
		dtime += millis;
		samples++;
	}
	
	public void update()
	{
		if (samples == 0)
			return;
		put(dtime / samples);
		reset();
		for (int i=0; i<measurements.length; i++)
		{
			max = Math.max(max, measurements[i]);
			min = Math.min(min, measurements[i]);
			mean += measurements[i];
		}
		mean /= measurements.length;
	}
	
	public void render(float w, float h)
	{
		GL11.glColor3f(1, 1, 1);
		GL11.glLineWidth(1);
		String tmax = String.format("max: %.2f ms", max);
		String tmean = String.format("mean: %.2f ms", mean);
		String tmin = String.format("min: %.2f ms", min);
		
		int textwidth = 0;
		textwidth = Math.max(textwidth, STBEasyFont.stb_easy_font_width(tmax));
		textwidth = Math.max(textwidth, STBEasyFont.stb_easy_font_width(tmean));
		textwidth = Math.max(textwidth, STBEasyFont.stb_easy_font_width(tmin));
		
		float lw = w-textwidth;
		
		int quads;
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glPushMatrix();
		GL11.glTranslatef(w-textwidth, h-1, 0);
		GL11.glScalef(1, -1, 1);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		
		quads = STBEasyFont.stb_easy_font_print(0, 0, tmax, null, charBuffer);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, quads * 4);
		
		GL11.glTranslatef(0, 10, 0);
		quads = STBEasyFont.stb_easy_font_print(0, 0, tmean, null, charBuffer);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, quads * 4);
		
		GL11.glTranslatef(0, 10, 0);
		quads = STBEasyFont.stb_easy_font_print(0, 0, tmin, null, charBuffer);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, quads * 4);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(w, 0);
			GL11.glVertex2f(w, h+1);
			GL11.glVertex2f(0, h);
		}
		GL11.glEnd();
		
		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		{
			for (int i=0; i<measurements.length; i++)
				GL11.glVertex2f(lw/measurements.length*i+1, get(-i)/max*h);
		}
		GL11.glEnd();
	}
	
	public void put(float ms)
	{
		idx = (idx + 1) % measurements.length;
		measurements[idx] = ms;
	}
	
	public float get(int relativeindex)
	{
		int index = idx + relativeindex;
		while (index < 0)
			index += measurements.length;
		index %= measurements.length;
		return measurements[index];
	}
}
