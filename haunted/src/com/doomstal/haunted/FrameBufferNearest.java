package com.doomstal.haunted;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FrameBufferNearest extends FrameBuffer {
	public FrameBufferNearest(Pixmap.Format format, int width, int height, boolean hasDepth) {
		super(format, width, height, hasDepth);
	}

	protected void setupTexture() {
		colorTexture = new Texture(width, height, format);
		colorTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		colorTexture.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
	}

}
