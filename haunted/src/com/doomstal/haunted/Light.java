package com.doomstal.haunted;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Light {
	static float[] tmpBuffer = new float[6];

	float x, y, z;
	float r, g, b;
	float radius;
	float intensity;

	Light(float x, float y, float z, float r, float g, float b, float radius, float intensity) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.g = g;
		this.b = b;
		this.radius = radius;
		this.intensity = intensity;  
	}

	void passUniforms(ShaderProgram lightShader) {
		tmpBuffer[0] = x;
		tmpBuffer[1] = y;
		tmpBuffer[2] = z;
		tmpBuffer[3] = r;
		tmpBuffer[4] = g;
		tmpBuffer[5] = b;

		lightShader.setUniform3fv("light_position", tmpBuffer, 0, 3);
		lightShader.setUniform3fv("light_color", tmpBuffer, 3, 3);
		lightShader.setUniformf("light_radius", radius);
		lightShader.setUniformf("light_intensity", intensity);
	}
}
