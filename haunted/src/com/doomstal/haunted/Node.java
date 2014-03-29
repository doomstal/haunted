package com.doomstal.haunted;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Node {
	static MyGdxGame game; //needs to be set outside

	static final int floats_per_vertex = 5;

	float x;
	float y;
	float z;

	Texture diffuse;
	Texture normal;
	Mesh mesh;
	float offset_u = 0;
	float offset_v = 0;

	boolean fullBright = false;
	boolean has_normals = false;

	Node(String texname, float z) {
		this.x = 0;
		this.y = 0;
		this.z = z;

		if(!game.assets.isLoaded("data/"+texname+".png")) {
			System.err.println("warning: texture not loaded");
			game.assets.load("data/"+texname+".png", Texture.class);
			game.assets.finishLoading();
		}
		diffuse = game.assets.get("data/"+texname+".png", Texture.class);
		diffuse.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		diffuse.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		if(!game.assets.isLoaded("data/"+texname+"_n.png")) {
			System.err.println("warning: texture not loaded");
			game.assets.load("data/"+texname+"_n.png", Texture.class);
			game.assets.finishLoading();
		}
		normal = game.assets.get("data/"+texname+"_n.png", Texture.class);
		normal.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		normal.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}

	void dispose() {
		mesh.dispose();
	}

	void update(float dt) {
	}

	void createMesh() {
	}

	void loadMesh(FileHandle file) {
		InputStream in = file.read();
		if(in == null) throw new RuntimeException("could not read " + file.name());
		DataInputStream dis = new DataInputStream(in);
		try {
			int vertNum = dis.readInt();
			float[] vertices = new float[vertNum * 8];
			for(int i=0; i<vertNum; i++) {
				vertices[i * 8 + 0] = dis.readFloat();	//x
				vertices[i * 8 + 1] = dis.readFloat();	//y
				vertices[i * 8 + 2] = dis.readFloat() + z;	//z
				vertices[i * 8 + 3] = dis.readFloat();	//u
				vertices[i * 8 + 4] = dis.readFloat();	//v
				vertices[i * 8 + 5] = dis.readFloat();	//nx
				vertices[i * 8 + 6] = dis.readFloat();	//ny
				vertices[i * 8 + 7] = dis.readFloat();	//nz
			}
			mesh = new Mesh(true, vertNum, 0,
				new VertexAttribute( Usage.Position, 3, "a_position"),
				new VertexAttribute( Usage.TextureCoordinates, 2, "a_texCoord0"),
				new VertexAttribute( Usage.Normal, 3, "a_normal")
			);
			mesh.setVertices(vertices, 0, 8 * vertNum);
			has_normals = true;
		} catch(IOException e) {
			System.err.println("can't load mesh");
		}
	}

	void createQuadMesh(float x, float y, float w, float h, float u1, float v1, float u2, float v2) {
		float[] vertices = new float[6 * floats_per_vertex];

		vertices[0 * floats_per_vertex + 0] = x;
		vertices[0 * floats_per_vertex + 1] = y;
		vertices[0 * floats_per_vertex + 2] = z;
		vertices[0 * floats_per_vertex + 3] = u1;
		vertices[0 * floats_per_vertex + 4] = v1;

		vertices[1 * floats_per_vertex + 0] = x+w;
		vertices[1 * floats_per_vertex + 1] = y;
		vertices[1 * floats_per_vertex + 2] = z;
		vertices[1 * floats_per_vertex + 3] = u2;
		vertices[1 * floats_per_vertex + 4] = v1;

		vertices[2 * floats_per_vertex + 0] = x+w;
		vertices[2 * floats_per_vertex + 1] = y+h;
		vertices[2 * floats_per_vertex + 2] = z;
		vertices[2 * floats_per_vertex + 3] = u2;
		vertices[2 * floats_per_vertex + 4] = v2;

		vertices[3 * floats_per_vertex + 0] = x;
		vertices[3 * floats_per_vertex + 1] = y;
		vertices[3 * floats_per_vertex + 2] = z;
		vertices[3 * floats_per_vertex + 3] = u1;
		vertices[3 * floats_per_vertex + 4] = v1;

		vertices[4 * floats_per_vertex + 0] = x+w;
		vertices[4 * floats_per_vertex + 1] = y+h;
		vertices[4 * floats_per_vertex + 2] = z;
		vertices[4 * floats_per_vertex + 3] = u2;
		vertices[4 * floats_per_vertex + 4] = v2;

		vertices[5 * floats_per_vertex + 0] = x;
		vertices[5 * floats_per_vertex + 1] = y+h;
		vertices[5 * floats_per_vertex + 2] = z;
		vertices[5 * floats_per_vertex + 3] = u1;
		vertices[5 * floats_per_vertex + 4] = v2;

		mesh = new Mesh(true, 6, 0,
			new VertexAttribute( Usage.Position, 3, "a_position"),
			new VertexAttribute( Usage.TextureCoordinates, 2, "a_texCoord0")
		);
		mesh.setVertices(vertices, 0, floats_per_vertex * 6);
	}

	void render(ShaderProgram shader, boolean bindNormal) {
		if(mesh == null) {
			System.err.println("mesh not created");
			createMesh();
			if(mesh == null) throw new RuntimeException("mesh not created!");
		}

		shader.setUniformf("pos_x", x);
		shader.setUniformf("pos_y", y);
		shader.setUniformf("offset_u", offset_u);
		shader.setUniformf("offset_v", offset_v);
		if(shader.getUniformLocation("ambient_intensity") != -1) {
			if(fullBright) shader.setUniformf("ambient_intensity", 1f);
			else shader.setUniformf("ambient_intensity", MyGdxGame.ambient_intensity);
		}
		if(bindNormal) shader.setUniformi("has_normals", has_normals?1:0);
		diffuse.bind(0);
		if(bindNormal) normal.bind(1);
		mesh.render(shader, GL10.GL_TRIANGLES);
	}
}
