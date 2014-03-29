package com.doomstal.haunted;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "haunted";
		cfg.useGL20 = true;

		MyGdxGame game = new MyGdxGame();

		cfg.width = 640;
		cfg.height = 480;
		
		new LwjglApplication(game, cfg);
	}
}
