package net.quantuminfinity.engine.gl.fbo;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;

import net.quantuminfinity.engine.gl.fbo.FBO.Attachment;
import net.quantuminfinity.engine.math.GLMU;
import net.quantuminfinity.engine.math.vector.Vector2;

public class PingPongFBO
{
	int fbo = 0, ping = 0, pong = 1, textures;
	Vector2 size;
	int [] FBOs = new int[2];
	int [][] FBOData;
	IntBuffer drawbuffers;
	int bpp;
	
	public PingPongFBO(int textures, int size, int bpp)
	{
		this(textures, new Vector2(size, size), bpp);
	}
	
	public PingPongFBO(int textures, Vector2 size, int bpp)
	{
		this.bpp = bpp;
		this.textures = textures;
		this.size = size;
		FBOData = new int[2][textures];
		
		genPing();
		genPong();
		
		drawbuffers = BufferUtils.createIntBuffer(textures);
		for (int i=0;i<textures;i++)
			drawbuffers.put(Attachment.get(i).GL_ATTACHMENT);
		drawbuffers.rewind();
	}
	
	public void bind()
	{
		fbo = next();
		GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GLMU.initGL2D((int) size.x, (int) size.y);
		GL11.glViewport(0, 0, (int) size.x, (int) size.y);
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, FBOs[fbo]);
		GL20.glDrawBuffers(drawbuffers);
	}
	
	public void release()
	{
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopAttrib();
	}
	
	public int getData(int n)
	{
		while (n < 0) n += textures;
		while (n > textures) n -= textures;
		return FBOData[next()][n];
	}
	
	public int[] getData()
	{
		return FBOData[next()];
	}
	
	public void setData(int... ntextures)
	{
		if (ntextures.length != textures)
			throw new RuntimeException("textures != new textures");
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, FBOs[ping]);
		for (int i=0; i<textures; i++)
		{
			FBOData[ping][i] = ntextures[i];
			EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, Attachment.get(i).FBO_ATTACHMENT, GL11.GL_TEXTURE_2D, FBOData[ping][i], 0);
		}
		
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, FBOs[pong]);
		for (int i=0; i<textures; i++)
		{
			FBOData[pong][i] = ntextures[i];
			EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, Attachment.get(i).FBO_ATTACHMENT, GL11.GL_TEXTURE_2D, FBOData[pong][i], 0);
		}		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}
	
	private int next()
	{
		return (fbo + 1) % 2;
	}
	
	private void genPing()
	{
		FBOs[ping] = EXTFramebufferObject.glGenFramebuffersEXT();
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, FBOs[ping]);
		
		for (int i=0; i<textures; i++)
		{
			FBOData[ping][i] = getTex(size);
			EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, Attachment.get(i).FBO_ATTACHMENT, GL11.GL_TEXTURE_2D, FBOData[ping][i], 0);
		}
		
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}
	
	private void genPong()
	{
		FBOs[pong] = EXTFramebufferObject.glGenFramebuffersEXT();
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, FBOs[pong]);
		
		for (int i=0; i<textures; i++)
		{
			FBOData[pong][i] = getTex(size);
			EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, Attachment.get(i).FBO_ATTACHMENT, GL11.GL_TEXTURE_2D, FBOData[pong][i], 0);
		}
		
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
	}
	
	public void destroy()
	{
		EXTFramebufferObject.glDeleteFramebuffersEXT(FBOs[ping]);
		EXTFramebufferObject.glDeleteFramebuffersEXT(FBOs[pong]);
		for (int i:FBOData[ping])
			GL11.glDeleteTextures(i);
		for (int i:FBOData[pong])
			GL11.glDeleteTextures(i);
	}
	
	public int getTex(Vector2 size)
	{
		int tex = GL11.glGenTextures();        
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, bpp, (int) size.x, (int) size.y, 0, GL11.GL_RGBA, GL11.GL_INT, (java.nio.ByteBuffer) null);
		return tex;
	}
}
