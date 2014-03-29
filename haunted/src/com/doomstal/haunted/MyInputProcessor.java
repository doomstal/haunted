package com.doomstal.haunted;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class MyInputProcessor implements InputProcessor {

	@Override
	public boolean keyDown (int keycode) {
		if(keycode == Input.Keys.GRAVE) {
			MyGdxGame.showDebugInfo = !MyGdxGame.showDebugInfo;
		}
		if(keycode == Input.Keys.NUM_1) {
			MyGdxGame.wireFrame = !MyGdxGame.wireFrame;
		}
		if(keycode == Input.Keys.NUM_2) {
			MyGdxGame.drawLights = !MyGdxGame.drawLights;
		}
		if(keycode == Input.Keys.EQUALS) {
			if(MyGdxGame.ambient_intensity < 1f) MyGdxGame.ambient_intensity += 0.05f;
		}
		if(keycode == Input.Keys.MINUS){
			if(MyGdxGame.ambient_intensity > 0f) MyGdxGame.ambient_intensity -= 0.05f;
		}

		if(keycode == Input.Keys.LEFT_BRACKET) {
			if(MyGdxGame.celShadingSteps > 0) MyGdxGame.celShadingSteps--;
		}
		if(keycode == Input.Keys.RIGHT_BRACKET) {
			MyGdxGame.celShadingSteps++;
		}

		if(keycode == Input.Keys.ENTER){
			if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)
			|| Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) {
				if(Gdx.graphics.isFullscreen()) {
					Gdx.graphics.setDisplayMode(
						640,
						480,
						false
					);
				} else {
					Gdx.graphics.setDisplayMode(
						Gdx.graphics.getDesktopDisplayMode().width,
						Gdx.graphics.getDesktopDisplayMode().height,
						true
					);
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		return false;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		return false;
	}
}
