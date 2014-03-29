package com.doomstal.haunted;

public class NodeTorch extends Node {
	static final float light_x = 8;
	static final float light_y = 8;

	float x, y;
	int frame;
	float frameTime;
	Light light;

	float light_ox, light_oy, light_og;
	float light_nx, light_ny, light_ng;
	float light_move_time = 0;

	NodeTorch(String tex, float z, float light_z) {
		super(tex, z);
		fullBright = true;
		light = new Light(
			x + light_x, y + light_y, z + light_z,
			1f, 0.7f, 0f,
			64, 1.5f
		);
		game.lightList.add(light);
		light_ox = light_nx = 0;
		light_oy = light_ny = 0;
		light_og = light_ng = 0.7f;
		frame = game.rand.nextInt(10);
		frameTime = game.rand.nextFloat();
	}

	@Override
	void update(float dt) {
		light.x = x + light_x + light_ox * (1 - light_move_time) + light_nx * light_move_time;
		light.y = y + light_y + light_oy * (1 - light_move_time) + light_ny * light_move_time;
		light.g = light_og * (1 - light_move_time) + light_ng * light_move_time;
		offset_u = 0.0625f * frame;
		frameTime += 10*dt;
		while(frameTime > 1) {
			frameTime -= 1;
			frame++;
			if(frame >= 10) frame = 0;
		}
		light_move_time += dt*10;
		if(light_move_time > 1) {
			light_move_time = 0;
			light_ox = light_nx;
			light_oy = light_ny;
			light_og = light_ng;
			light_nx = game.rand.nextFloat() * 1 - 0.5f;
			light_ny = game.rand.nextFloat() * 1 - 0.5f;
			light_ng = 0.65f + game.rand.nextFloat() * 0.1f;
		}
	}

	@Override
	void createMesh() {
		createQuadMesh(x, y, 16, 32, 0.0f, 1f, 0.0625f, 0.875f);
	}
}
