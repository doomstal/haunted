package com.doomstal.haunted;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class NodeTileRect extends Node {
	static final int vertexes_per_tile = 6;
	final int width, height;
	int[][] tiles;

	NodeTileRect(String texname, float z, int width, int height) {
		super(texname, z);

		this.width = width;
		this.height = height;

		tiles = new int[height][width];
	}

	@Override
	void createMesh() {
		float[] tileVertices = new float[vertexes_per_tile * floats_per_vertex * width * height];

		int vertexCount = 0;

		for(int j=0; j<height; j++) {
			for(int i=0; i<width; i++) {
				if(tiles[j][i] <= 0) continue;
				int tile = tiles[j][i] - 1;

				float tex_step = 1.0f / 16.0f;
				float u = (tile % 16) * tex_step;
				float v = (tile / 16) * tex_step;
				tileVertices[(vertexCount+0)*5+0] = -game.width/2 + (i+0)*16;
				tileVertices[(vertexCount+0)*5+1] = game.height/2 - (j+0)*16;
				tileVertices[(vertexCount+0)*5+2] = z;
				tileVertices[(vertexCount+0)*5+3] = u;
				tileVertices[(vertexCount+0)*5+4] = v;

				tileVertices[(vertexCount+1)*5+0] = -game.width/2 + (i+1)*16;
				tileVertices[(vertexCount+1)*5+1] = game.height/2 - (j+0)*16;
				tileVertices[(vertexCount+1)*5+2] = z;
				tileVertices[(vertexCount+1)*5+3] = u+tex_step;
				tileVertices[(vertexCount+1)*5+4] = v;

				tileVertices[(vertexCount+2)*5+0] = -game.width/2 + (i+1)*16;
				tileVertices[(vertexCount+2)*5+1] = game.height/2 - (j+1)*16;
				tileVertices[(vertexCount+2)*5+2] = z;
				tileVertices[(vertexCount+2)*5+3] = u+tex_step;
				tileVertices[(vertexCount+2)*5+4] = v+tex_step;

				tileVertices[(vertexCount+3)*5+0] = -game.width/2 + (i+1)*16;
				tileVertices[(vertexCount+3)*5+1] = game.height/2 - (j+1)*16;
				tileVertices[(vertexCount+3)*5+2] = z;
				tileVertices[(vertexCount+3)*5+3] = u+tex_step;
				tileVertices[(vertexCount+3)*5+4] = v+tex_step;

				tileVertices[(vertexCount+4)*5+0] = -game.width/2 + (i+0)*16;
				tileVertices[(vertexCount+4)*5+1] = game.height/2 - (j+1)*16;
				tileVertices[(vertexCount+4)*5+2] = z;
				tileVertices[(vertexCount+4)*5+3] = u;
				tileVertices[(vertexCount+4)*5+4] = v+tex_step;

				tileVertices[(vertexCount+5)*5+0] = -game.width/2 + (i+0)*16;
				tileVertices[(vertexCount+5)*5+1] = game.height/2 - (j+0)*16;
				tileVertices[(vertexCount+5)*5+2] = z;
				tileVertices[(vertexCount+5)*5+3] = u;
				tileVertices[(vertexCount+5)*5+4] = v;

				vertexCount += vertexes_per_tile;
			}
		}

		mesh = new Mesh(true, vertexCount, 0,
			new VertexAttribute( Usage.Position, 3, "a_position"),
			new VertexAttribute( Usage.TextureCoordinates, 2, "a_texCoord0")
		);
		mesh.setVertices(tileVertices, 0, floats_per_vertex * vertexCount);
	}

}
