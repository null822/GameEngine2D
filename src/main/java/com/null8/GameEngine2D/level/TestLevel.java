package com.null8.GameEngine2D.level;

import com.null8.GameEngine2D.graphics.Shader;
import com.null8.GameEngine2D.graphics.Texture;
import com.null8.GameEngine2D.graphics.VertexArray;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vector3f;

import java.util.Random;

public class TestLevel {

	private VertexArray background, fade;
	private Texture bgTexture;
	
	private int xScroll = 0;
	private int map = 0;

	private Random random = new Random();
	
	private float time = 0.0f;

	private boolean control = true, reset = false;

	public TestLevel() {
		float[] vertices = new float[] {
			-10.0f, -10.0f * 9.0f / 16.0f, 0.0f,
			-10.0f,  10.0f * 9.0f / 16.0f, 0.0f,
			  0.0f,  10.0f * 9.0f / 16.0f, 0.0f,
			  0.0f, -10.0f * 9.0f / 16.0f, 0.0f
		};
		
		byte[] indices = new byte[] {
			0, 1, 2,
			2, 3, 0
		};
		
		float[] tcs = new float[] {
			0, 1,
			0, 0,
			1, 0,
			1, 1
		};
		
		fade = new VertexArray(6);
		background = new VertexArray(vertices, indices, tcs);
		bgTexture = new Texture("background/background.png");
	}

	public boolean isGameOver() {
		return reset;
	}
	
	public void render() {
		bgTexture.bind();
		Shader.BACKGROUND.enable();
		background.bind();

		xScroll = 0;

		for (int i = map; i < map + 4; i++) {
			Shader.BACKGROUND.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(i * 10 + xScroll * 0.03f, 0.0f, 0.0f)));
			background.draw();
		}

		Shader.BACKGROUND.disable();
		bgTexture.unbind();

	}
	
}
