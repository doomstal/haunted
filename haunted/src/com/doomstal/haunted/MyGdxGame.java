package com.doomstal.haunted;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
//import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class MyGdxGame implements ApplicationListener {
	Random rand = new Random();

	static Vector3 tmpVector3 = new Vector3();

	float width = 320;
	float height = 240;
	int scale = 1;

	int viewportX;
	int viewportY;
	int viewportWidth;
	int viewportHeight;

	OrthographicCamera camera;
	FrameBuffer fbo;
	TextureRegion fboRegion;
	Mesh fboQuad;

	Texture fontTexture;
	SpriteBatch batch;
	BitmapFont font;

	AssetManager assets;

	ShaderProgram flatShader;
	ShaderProgram lightShader;

	ShapeRenderer shape;

	Light light1;
	ArrayList<Light> lightList = new ArrayList<Light>();

	ArrayList<ShadowCaster> shadowCasterList = new ArrayList<ShadowCaster>();

	ArrayList<Node> nodeList = new ArrayList<Node>();

	static int tile_width = 20;
	static int tile_height = 15;

	static float ambient_intensity = 0.1f;

	static boolean showDebugInfo = true;
	static boolean drawLights = true;
	static boolean wireFrame = false;
	static int celShadingSteps = 8;//0;

	@Override
	public void create() {
		ShadowCaster.game = this; // important!
		Node.game = this;

		Gdx.input.setInputProcessor(new MyInputProcessor());

		ByteBuffer tmpByte = ByteBuffer.allocateDirect(64);
		tmpByte.order(ByteOrder.LITTLE_ENDIAN);

		camera = new OrthographicCamera();
		camera.near = 0;
		camera.far = 512;
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		fbo = new FrameBufferNearest(Format.RGBA8888, (int)width, (int)height, true);
		fboRegion = new TextureRegion(fbo.getColorBufferTexture());
		float[] vertices = new float[] {
			-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
			 1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
			 1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
			-1.0f,  1.0f, 0.0f, 0.0f, 1.0f
		};
		fboQuad = new Mesh(true, 4, 0,
			new VertexAttribute( Usage.Position, 3, "a_position"),
			new VertexAttribute( Usage.TextureCoordinates, 2, "a_texCoord0")
		);
		fboQuad.setVertices(vertices, 0, 20);

		assets = new AssetManager();

		shape = new ShapeRenderer();

		flatShader = loadShader("flat");
		lightShader = loadShader("light");

		assets.load("data/tiles.png", Texture.class);
		assets.load("data/tiles_n.png", Texture.class);

		assets.load("data/flat.png", Texture.class);
		assets.load("data/flat_n.png", Texture.class);

		assets.load("data/pony.png", Texture.class);
		assets.load("data/pony_n.png", Texture.class);

		assets.finishLoading();

		float layer_z = -32f;

		NodeTileRect layer1 = new NodeTileRect("tiles", layer_z, tile_width, tile_height);

		int[][] tiles = layer1.tiles;
		int i,j;

		for(j=0; j<layer1.height; j++) {
			for(i=0; i<layer1.width; i++) {
				tiles[j][i] = 18;
			}
		}

		layer1.createMesh();
		nodeList.add(layer1);

		layer1 = new NodeTileRect("tiles", layer_z + 1, tile_width, tile_height);
		tiles = layer1.tiles;

		j=3;
		for(i=2; i<tile_width; i+=5) {
			tiles[j][i] = 9;
			tiles[10][i] = 9;
			NodeTorch torch = new NodeTorch("tiles", layer_z + 4, 0f);
			torch.x = -width/2 + i*16;
			torch.y = height/2 - j*16;
			torch.update(0);
			torch.createMesh();
			nodeList.add(torch);
		}

		layer1.createMesh();
		nodeList.add(layer1);

		layer1 = new NodeTileRect("tiles", 0f, tile_width, tile_height);
		tiles = layer1.tiles;

		for(i=1; i<layer1.width-1; i++) {
			tiles[0][i] = 37;
			tiles[layer1.height-1][i] = 5;
		}
		for(j=1; j<layer1.height-1; j++) {
			tiles[j][0] = 22;
			tiles[j][layer1.width-1] = 20;
		}

		for(i=1; i<10; i++) tiles[7][i] = 40;
		tiles[7][0] = 21;
		tiles[7][10] = 41;

		int off = 0;
		shadowCasterList.add(ShadowCaster.shadowBox(-width/2+off, height/2-8*16+off, 176-off*2, 16-off*2, true));
		off = 4;
		shadowCasterList.add(ShadowCaster.shadowBox(-width/2+off, height/2-8*16+off, 176-off*2, 16-off*2, false));

		tiles[0][0] = 7;
		tiles[0][layer1.width-1] = 8;
		tiles[layer1.height-1][0] = 23;
		tiles[layer1.height-1][layer1.width-1] = 24;

		layer1.createMesh();
		nodeList.add(layer1);

		Node pony = new Node("pony", -16f);
		pony.loadMesh(Gdx.files.internal("data/pony.mesh"));
		nodeList.add(pony);
		pony.y = -104f;

		light1 = new Light(
			48, 0, -16f,
			0.25f, 0.5f, 1f,
			32, 1f
		);
		lightList.add(light1);

		fontTexture = new Texture(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.png"));
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.classpath("com/badlogic/gdx/utils/arial-15.fnt"), new TextureRegion(fontTexture));
	}

	ShaderProgram loadShader(String name) {
		FileHandle vertexFile = Gdx.files.internal("data/"+name+".vertex.glsl");
		FileHandle fragmentFile = Gdx.files.internal("data/"+name+".fragment.glsl");
		ShaderProgram shader = new ShaderProgram(vertexFile.readString(), fragmentFile.readString());
		System.out.println(name+": "+shader.getLog());
		if(!shader.isCompiled()) {
			System.err.println(name+": shader not compiled!");
			Gdx.app.exit();
		} else {
			System.out.println(name+": shader compiled");
		}
		return shader;
	}

	@Override
	public void dispose() {
		lightShader.dispose();
		flatShader.dispose();
		for(Node node: nodeList) node.dispose();
		assets.dispose();
		fontTexture.dispose();
		batch.dispose();
		font.dispose();
		fbo.dispose();
		fboQuad.dispose();
	}

	@Override
	public void render() {
		pollInput();
		float dt = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClearDepthf(1f);

		fbo.begin();

		Gdx.gl.glDisable(GL20.GL_CULL_FACE);

		Gdx.gl.glColorMask(true, true, true, true);
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera.update();
		shape.setProjectionMatrix(camera.combined);

		for(Node node: nodeList) {
			node.update(dt);
		}

		//draw to depth buffer
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);

		flatShader.begin();

		flatShader.setUniformi("u_texture", 0);
		flatShader.setUniformMatrix4fv("u_projTrans", camera.combined.getValues(), 0, 16);

		renderScene(flatShader, false, false);

		flatShader.end();

		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(false);

		if(drawLights) {
			for(Light light: lightList) {
				boolean drawLight = true;
				for(ShadowCaster shadowCaster: shadowCasterList) {
					if(shadowCaster.lightInside(light)) {
						drawLight = false;
						break;
					}
				}
				if(!drawLight) continue;
	
				//draw shadow to stencil buffer
				Gdx.gl.glColorMask(false, false, false, true);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

				if(wireFrame) shape.begin(ShapeType.Line);
				else shape.begin(ShapeType.Filled);
				shape.setColor(1.0f, 1.0f, 1.0f, 0.0f);
				for(ShadowCaster shadowCaster: shadowCasterList) {
					shadowCaster.drawShadow(shape, light);
				}
				shape.end();

				//draw light
				Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
				Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE);

				Gdx.gl.glColorMask(true, true, true, true);

				lightShader.begin();

				lightShader.setUniformi("u_texture", 0);
				lightShader.setUniformi("u_normal", 1);
				lightShader.setUniformMatrix4fv("u_projTrans", camera.combined.getValues(), 0, 16);
				light.passUniforms(lightShader);
	
				lightShader.setUniformi("cel_shading_steps", celShadingSteps);

				renderScene(lightShader, true, true);

				lightShader.end();

				Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
				Gdx.gl.glDisable(GL20.GL_BLEND);
			}
		}
		Gdx.gl.glColorMask(false, false, false, true);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		fbo.end();

		//render frame buffer to screen
		Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

		Gdx.gl.glColorMask(true, true, true, true);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		flatShader.begin();

		Gdx.gl.glDisable(GL20.GL_BLEND);
		flatShader.setUniformf("pos_x", 0.0f);
		flatShader.setUniformf("pos_y", 0.0f);
		flatShader.setUniformf("offset_u", 0.0f);
		flatShader.setUniformf("offset_v", 0.0f);
		flatShader.setUniformf("ambient_intensity", 1.0f);
		flatShader.setUniformi("u_texture", 0);
		flatShader.setUniformMatrix4fv("u_projTrans", new float[] {1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1}, 0, 16);

		fboRegion.getTexture().bind(0);
		fboQuad.render(flatShader, GL20.GL_TRIANGLE_FAN);

		flatShader.end();

		if(showDebugInfo) {
			camera.projection.scale(0.5f, 0.5f, 1f);
			batch.setProjectionMatrix(camera.projection);
			batch.begin();
			fontTexture.bind(0);
			font.draw(batch, "escape: exit", -width + 5, height - 5);
			font.draw(batch, "alt+enter: toggle fullscreen", -width + 5, height - 25);
			font.draw(batch, "~: toggle debug info", -width + 5, height - 45);
			font.draw(batch, "1: toggle shadow wireframe", -width + 5, height - 65);
			font.draw(batch, "2: toggle lights", -width + 5, height - 85);
			font.draw(batch, "[/]: change cel shading steps", -width + 5, height - 105);

			font.draw(batch, "drag mouse to move light", -width + 5, -height + 160);
			font.draw(batch, "-/+: ambient light", -width + 5, -height + 140);
			font.draw(batch, "q/a: change light radius", -width + 5, -height + 120);
			font.draw(batch, "w/s: change light intensity", -width + 5, -height + 100);
			font.draw(batch, "e/d: move light along z", -width + 5, -height + 80);
			font.draw(batch, "r/f: change light red component", -width + 5, -height + 60);
			font.draw(batch, "t/g: change light green component", -width + 5, -height + 40);
			font.draw(batch, "y/h: change light blue component", -width + 5, -height + 20);
			batch.end();
		}
	}

	void renderScene(ShaderProgram shader, boolean bindNormal, boolean drawingLight) {
		for(Node node: nodeList) {
			if(drawingLight && node.fullBright) continue;
			node.render(shader, bindNormal);
		}
	}

	void pollInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

		if(Gdx.input.isTouched()) {
			light1.x = (Gdx.input.getX() - Gdx.graphics.getWidth()/2) / scale + camera.position.x;
			light1.y = (Gdx.graphics.getHeight()/2 - Gdx.input.getY()) / scale + camera.position.y;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
			light1.radius += 1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			if(light1.radius > 1) light1.radius -= 1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			light1.intensity += 0.1f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			if(light1.intensity > 0.1f) light1.intensity -= 0.1f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.E)) {
			light1.z -= 1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			light1.z += 1;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.R)) {
			light1.r += 0.05f;
			if(light1.r > 1.0f) light1.r = 1.0f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F)) {
			light1.r -= 0.05f;
			if(light1.r < 0.0f) light1.r = 0.0f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.T)) {
			light1.g += 0.05f;
			if(light1.g > 1.0f) light1.g = 1.0f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.G)) {
			light1.g -= 0.05f;
			if(light1.g < 0.0f) light1.g = 0.0f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.Y)) {
			light1.b += 0.05f;
			if(light1.b > 1.0f) light1.b = 1.0f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.H)) {
			light1.b -= 0.05f;
			if(light1.b < 0.0f) light1.b = 0.0f;
		}
	}

	@Override
	public void resize(int width, int height) {
		scale = 1;
		while(this.width*(scale+1) <= width && this.height*(scale+1) <= height) scale++;

		viewportWidth = (int)this.width * scale;
		viewportHeight = (int)this.height * scale;
		viewportX = width/2 - viewportWidth/2;
		viewportY = height/2 - viewportHeight/2;

		Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
		camera.setToOrtho(false, this.width, this.height);
		camera.position.set(0, 0, 0);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
