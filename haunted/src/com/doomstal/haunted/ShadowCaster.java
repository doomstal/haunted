package com.doomstal.haunted;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ShadowCaster {
	static MyGdxGame game; //must be set outside

	float[] x;
	float[] y;
	private int vertexCount;

	ShadowCaster(float[] x, float[] y) {
		if(x.length != y.length) throw new RuntimeException("could not create ShadowCaster: arrays' lengths does not match");
		if(x.length < 2) throw new RuntimeException("could not create ShadowCaster: must have at lease 2 points");
		this.x = x;
		this.y = y;
		this.vertexCount = x.length;
	}

	static ShadowCaster shadowBox(float x, float y, float w, float h, boolean inverse) {
		if(inverse) {
			return new ShadowCaster(new float[] {x+w, x+w, x, x}, new float[] {y, y+h, y+h, y});
		}
		return new ShadowCaster(new float[] {x, x, x+w, x+w}, new float[] {y, y+h, y+h, y});
	}

//	static ShadowCaster shadowLine(float x1, float y1, float x2, float y2) {
//		return new ShadowCaster(new float[] {x1, x2}, new float[] {y1, y2});
//	}

	boolean lightInside(Light light) {
		if(vertexCount == 2) return false;

		for(int i=1; i<vertexCount; i++) {
			if((light.x - x[i])*(y[i-1] - y[i]) + (light.y - y[i])*(x[i] - x[i-1]) > 0) return false;
		}
		if((light.x - x[0])*(y[vertexCount-1] - y[0]) + (light.y - y[0])*(x[0] - x[vertexCount-1]) > 0) return false;

		return true;
	}

	void drawShadow(ShapeRenderer shape, Light light) {
		for(int i=1; i<vertexCount; i++) {
			if((light.x - x[i])*(y[i-1] - y[i]) + (light.y - y[i])*(x[i] - x[i-1]) > 0) drawShadow(shape, light, x[i-1], y[i-1], x[i], y[i]);
		}

		if(vertexCount == 2) return;

		if((light.x - x[0])*(y[vertexCount-1] - y[0]) + (light.y - y[0])*(x[0] - x[vertexCount-1]) > 0) drawShadow(shape, light, x[vertexCount-1], y[vertexCount-1], x[0], y[0]);
	}

	void drawShadow(ShapeRenderer shape, Light light, float x1, float y1, float x2, float y2) {
		float bx1, by1, bx2, by2;
		int side1, side2;
		final int top = 1, bottom = 2, left = 3, right = 4;

		if(y1 > light.y) {
			bx1 = light.x + ((x1 - light.x)*(game.height/2 - light.y)) / (y1 - light.y);
			by1 = game.height/2;
			side1 = top;
		} else {
			bx1 = light.x + ((x1 - light.x)*(-game.height/2 - light.y)) / (y1 - light.y);
			by1 = -game.height/2;
			side1 = bottom;
		}
		if(bx1 < -game.width/2 || bx1 > game.width/2) {
			if(x1 > light.x) {
				bx1 = game.width/2;
				by1 = light.y + ((y1 - light.y)*(game.width/2 - light.x)) / (x1 - light.x);
				side1 = right;
			} else {
				bx1 = -game.width/2;
				by1 = light.y + ((y1 - light.y)*(-game.width/2 - light.x)) / (x1 - light.x);
				side1 = left;
			}
		}

		if(y2 > light.y) {
			bx2 = light.x + ((x2 - light.x)*(game.height/2 - light.y)) / (y2 - light.y);
			by2 = game.height/2;
			side2 = top;
		} else {
			bx2 = light.x + ((x2 - light.x)*(-game.height/2 - light.y)) / (y2 - light.y);
			by2 = -game.height/2;
			side2 = bottom;
		}
		if(bx2 < -game.width/2 || bx2 > game.width/2) {
			if(x2 > light.x) {
				bx2 = game.width/2;
				by2 = light.y + ((y2 - light.y)*(game.width/2 - light.x)) / (x2 - light.x);
				side2 = right;
			} else {
				bx2 = -game.width/2;
				by2 = light.y + ((y2 - light.y)*(-game.width/2 - light.x)) / (x2 - light.x);
				side2 = left;
			}
		}

		shape.triangle(x1, y1, bx1, by1, bx2, by2);
		shape.triangle(x1, y1, bx2, by2, x2, y2);

		if(side1 > side2) {
			float t = bx1; bx1 = bx2; bx2 = t;
			t = by1; by1 = by2; by2 = t;
			int ti = side1; side1 = side2; side2 = ti;
		}

		if(side1 == top && side2 == bottom) {
			float tx, ty1, ty2;
			if(light.x < bx1 || light.x < bx2) {
				tx = game.width/2;
				ty1 = game.height/2;
				ty2 = -game.height/2;
			} else {
				tx = -game.width/2;
				ty1 = game.height/2;
				ty2 = -game.height/2;
			}
			shape.triangle(bx1, by1, tx, ty1, tx, ty2);
			shape.triangle(bx1, by1, tx, ty2, bx2, by2);
		}
		if(side1 == left && side2 == right) {
			float tx1, tx2, ty;
			if(light.y < by1 || light.y < by2) {
				tx1 = -game.width/2;
				tx2 = game.width/2;
				ty = game.height/2;
			} else {
				tx1 = -game.width/2;
				tx2 = game.width/2;
				ty = -game.height/2;
			}
			shape.triangle(bx1, by1, tx1, ty, tx2, ty);
			shape.triangle(bx1, by1, tx2, ty, bx2, by2);
		}
		if(side1 == top && side2 == right) {
			shape.triangle(bx1, by1, game.width/2, game.height/2, bx2, by2);
		}
		if(side1 == bottom && side2 == right) {
			shape.triangle(bx1, by1, game.width/2, -game.height/2, bx2, by2);
		}
		if(side1 == top && side2 == left) {
			shape.triangle(bx1, by1, -game.width/2, game.height/2, bx2, by2);
		}
		if(side1 == bottom && side2 == left) {
			shape.triangle(bx1, by1, -game.width/2, -game.height/2, bx2, by2);
		}

	}
}
