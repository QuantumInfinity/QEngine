package net.quantuminfinity.engine.gl.fbo;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.quantuminfinity.engine.math.GLMU;
import net.quantuminfinity.engine.texture.TextureFactory;

public class FBO
{
	int fbo, width, height;
	TextureFactory tf;
	int[] attachments;
	IntBuffer drawbuffers;
	
	public FBO(int width, int height, int nattachments, TextureFactory tf)
	{
		this.width = width;
		this.height = height;
		this.attachments = new int[nattachments];
		this.tf = tf;
		
		genFBO();
	}
	
	public FBO(int width, int height, int nattachments)
	{
		this(width, height, nattachments, TextureFactory.defaultFactory);
	}
	
	public int getID()
	{
		return fbo;
	}
	
	public void bind()
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		if (drawbuffers != null)
			GL20.glDrawBuffers(drawbuffers);
	}
		
	public void release()
	{
		GL20.glDrawBuffers(0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void bindPushAttrib()
	{
		GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GLMU.initGL2D(width, height);
		GL11.glViewport(0, 0, width, height);
		bind();
	}
	
	public void releasePopAttrib()
	{
		release();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopAttrib();
	}
	
	public int[] getData()
	{
		return attachments;
	}
	
	public int getData(int n)
	{
		n %= attachments.length;
		n += attachments.length;
		n %= attachments.length;
		return attachments[n];
	}
	
	public void setData(int... newattachments)
	{
		if (attachments.length != newattachments.length)
			throw new RuntimeException("attachments != newattachments");
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		
		bind();
		for (int i=0; i<attachments.length; i++)
		{
			attachments[i] = newattachments[i];
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, GL11.GL_TEXTURE_2D, attachments[i], 0);
		}
		release();
	}
	
	public void destroy()
	{
		GL30.glDeleteFramebuffers(fbo);
		for (int i : attachments)
			GL11.glDeleteTextures(i);
	}
	
	private void genFBO()
	{
		fbo = GL30.glGenFramebuffers();
		bind();
		for (int i=0; i<attachments.length; i++)
		{
			attachments[i] = tf.generateTex2D(width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, GL11.GL_TEXTURE_2D, attachments[i], 0);
		}
		release();
		
		drawbuffers = BufferUtils.createIntBuffer(attachments.length);
		for (int i = 0; i < attachments.length; i++)
			drawbuffers.put(GL30.GL_COLOR_ATTACHMENT0 + i);
		drawbuffers.rewind();
	}
}
