package com.ru.tgra.shapes;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.utils.BufferUtils;

public class LabFirst3DGame extends ApplicationAdapter implements InputProcessor {

	private FloatBuffer matrixBuffer;

	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;
	private int normalLoc;

	private int modelMatrixLoc;
	private int viewMatrixLoc;
	private int projectionMatrixLoc;

	// Player variables
	private boolean firstPerson;
	private boolean thirdPerson;
	private float playerDirection;
	private int score;

	// Camera variables
	private Camera cam;
	private Camera orthoCam;
	private Camera scoreCam;
	float angle;

	// Token variables
	private ArrayList<Token> tokens;
	private int TOKEN_NUMBER = 5;
	private Random rand;

	private int colorLoc;
	private float fov = 90.0f;

	private Maze maze;
	private float movementSpeed = 3f; // used with deltatime, WASD keys
	private float playerSize = 1f; // Radius of player circle, for collision and display in 2D

	@Override
	public void create () {

		Gdx.input.setInputProcessor(this);

		String vertexShaderString;
		String fragmentShaderString;

		vertexShaderString = Gdx.files.internal("shaders/simple3D.vert").readString();
		fragmentShaderString =  Gdx.files.internal("shaders/simple3D.frag").readString();

		vertexShaderID = Gdx.gl.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = Gdx.gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);

		Gdx.gl.glShaderSource(vertexShaderID, vertexShaderString);
		Gdx.gl.glShaderSource(fragmentShaderID, fragmentShaderString);

		Gdx.gl.glCompileShader(vertexShaderID);
		Gdx.gl.glCompileShader(fragmentShaderID);

		renderingProgramID = Gdx.gl.glCreateProgram();

		Gdx.gl.glAttachShader(renderingProgramID, vertexShaderID);
		Gdx.gl.glAttachShader(renderingProgramID, fragmentShaderID);

		Gdx.gl.glLinkProgram(renderingProgramID);

		positionLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_position");
		Gdx.gl.glEnableVertexAttribArray(positionLoc);

		normalLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_normal");
		Gdx.gl.glEnableVertexAttribArray(normalLoc);

		modelMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_modelMatrix");
		viewMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_viewMatrix");
		projectionMatrixLoc	= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_projectionMatrix");

		colorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_color");

		Gdx.gl.glUseProgram(renderingProgramID);

		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);

		BoxGraphic.create(positionLoc, normalLoc);
		SphereGraphic.create(positionLoc, normalLoc);
		SincGraphic.create(positionLoc);
		CoordFrameGraphic.create(positionLoc);

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		ModelMatrix.main = new ModelMatrix();
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.setShaderMatrix(modelMatrixLoc);

		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);




		// Birkir and his amazing maze
		int mazeSize = 12;
		float cellSize = 6f;
		maze = new Maze(mazeSize, mazeSize, cellSize, ModelMatrix.main, colorLoc, positionLoc, normalLoc);

		// ----------------------------------
		// 		Camera init & settings
		// ----------------------------------
		// --- Player camera ---
		cam = new Camera(viewMatrixLoc, projectionMatrixLoc);
		cam.perspectiveProjection(fov, 1.0f, 0.4f, 100.0f);
//		cam.look(new Point3D(-13f, 3f, 0f), new Point3D(0,3,0), new Vector3D(0,1,0));
		cam.look(new Point3D((cellSize/2), 3f, (cellSize/2)), new Point3D(6,3,(cellSize/2)), new Vector3D(0,1,0));
		// --- Mini map camera ---
		orthoCam = new Camera(viewMatrixLoc, projectionMatrixLoc);
		orthoCam.orthographicProjection(-30.0f,30.0f,-30.0f,30.0f,1.0f, 100.0f);
		// --- Score camera ---
		scoreCam = new Camera(viewMatrixLoc, projectionMatrixLoc);
		scoreCam.orthographicProjection(-83.3f,83.3f,-25.0f,25.0f,1.0f, 100.0f);

		// ----------------------------------
		// 		  Game play settings
		// ----------------------------------
		firstPerson = true;
		thirdPerson = false;
		playerDirection = 0f;
		score = 0;

		// ----------------------------------
		// 		  Token settings
		// ----------------------------------
		rand = new Random();

		tokens = new ArrayList<Token>();
		// Initialize game tokens
		for(int i = 0; i < TOKEN_NUMBER; i++) {
			float x = ((rand.nextInt(mazeSize) * cellSize) + (cellSize/2));
			float y = ((rand.nextInt(mazeSize) * cellSize) + (cellSize/2));

			tokens.add(new Token(x, y, ModelMatrix.main, colorLoc));
		}

	}


	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();

		angle += 180.0f * deltaTime;

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cam.yaw(-90.f * deltaTime);
			playerDirection -= 90f * deltaTime;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cam.yaw(90.f * deltaTime);
			playerDirection += 90f * deltaTime;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
//			cam.slide(-movementSpeed * deltaTime, 0, 0);
			cam.slideMaze(-movementSpeed * deltaTime, 0, 0, maze, playerSize);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
//			cam.slide(movementSpeed * deltaTime, 0, 0);
			cam.slideMaze(movementSpeed * deltaTime, 0, 0, maze, playerSize);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			// Moving straight forward
//			Vector3D forwardVector = new Vector3D(-cam.n.x * (movementSpeed * deltaTime), -cam.n.y * (movementSpeed * deltaTime), -cam.n.z * (movementSpeed * deltaTime));
//			Vector3D newForward = maze.correctedToWalls(cam.eye, forwardVector, playerSize);

//			System.out.println("N Vector, x: " + forwardVector.x + ", y: " + forwardVector.y + ", z: " + forwardVector.z);

//			cam.slide(0, 0, -movementSpeed * deltaTime);
			cam.slideMaze(0, 0, -movementSpeed * deltaTime, maze, playerSize);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
//			cam.slide(0, 0, movementSpeed * deltaTime);
			cam.slideMaze(0, 0, movementSpeed * deltaTime, maze, playerSize);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.R)) {
			cam.slide(0, -movementSpeed * deltaTime, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F)) {
			cam.slide(0, movementSpeed * deltaTime, 0);
		}

		maze.isWalls(cam.eye);

		Token removedToken = null;

		for(Token token : tokens) {
			token.bounce(deltaTime);
			if(cam.gotToken(token)) {
				removedToken = token;
				score++;
			}
		}

		if(removedToken != null)
			tokens.remove(removedToken);

	}

	private void display()
	{
		//do all actual drawing and rendering here
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		for(int viewNum = 0; viewNum < 2; viewNum++)
		{
			// --- The player view ---
			if(viewNum == 0)
			{
				if(firstPerson) {
					Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					cam.perspectiveProjection(fov, 1.4f, 0.1f, 100.0f);
					cam.setShaderMatrices();
				}
				else if(thirdPerson) {

				}
			}
			// -- The minimap view --
			else
			{
				int miniMapHeight = 250;
				int miniMapWidth = 250;
				Gdx.gl.glViewport((Gdx.graphics.getWidth() - miniMapWidth), Gdx.graphics.getHeight() - miniMapHeight, miniMapWidth, miniMapHeight);
				orthoCam.look(new Point3D(cam.eye.x, 10.0f, cam.eye.z), cam.eye, new Vector3D(0,0,-1));
				orthoCam.setShaderMatrices();
			}


			// ----------------------------------
			// 		 Draw our MAZE here
			// ----------------------------------

			maze.display(viewNum == 0);

			for(Token token : tokens) {
				token.display();
			}

//			ModelMatrix.main.loadIdentityMatrix();
//			ModelMatrix.main.pushMatrix();
//			ModelMatrix.main.addScale(2.0f, 2.0f, 2.0f);
//			ModelMatrix.main.addTranslationBaseCoords(9, 5,-2);
//			ModelMatrix.main.setShaderMatrix();
//			BoxGraphic.drawSolidCube();

			// --- Our position in the mini map ---
			if(viewNum == 1)
			{
				Gdx.gl.glUniform4f(colorLoc, 0.6f,0.0f,0.6f, 1.0f);

				ModelMatrix.main.loadIdentityMatrix();
				//ModelMatrix.main.addTranslation(250, 250, 0);
				ModelMatrix.main.pushMatrix();
//				ModelMatrix.main.addScale(2.0f, 2.0f, 2.0f);
				ModelMatrix.main.addScale(playerSize, playerSize, playerSize);
				ModelMatrix.main.addTranslationBaseCoords(cam.eye.x, cam.eye.y,cam.eye.z);
				ModelMatrix.main.addRotationY(playerDirection);
				ModelMatrix.main.setShaderMatrix();
				SphereGraphic.drawSolidSphere();
//				BoxGraphic.drawSolidCube();
				//.main.popMatrix();

				// --- Background in the mini map ---
				Gdx.gl.glUniform4f(colorLoc, 0f, 0f, 0f, 1f);
				ModelMatrix.main.loadIdentityMatrix();
				ModelMatrix.main.addScale(1000f, 0.4f, 1000f);
				ModelMatrix.main.addTranslationBaseCoords(1,0.2f,1);
				ModelMatrix.main.setShaderMatrix();
				BoxGraphic.drawSolidCube();
				ModelMatrix.main.popMatrix();
			}
		}

		int scoreHeight = 150;
		int scoreWidth = 500;
		int scoreSlotX = scoreWidth/TOKEN_NUMBER;
		int scoreSlotY = scoreHeight/2;
		Gdx.gl.glViewport(0, Gdx.graphics.getHeight() - scoreHeight, scoreWidth, scoreHeight);

		scoreCam.look(new Point3D(-100,10,-100), new Point3D(-100,1,-100), new Vector3D(0,0,-1));
		scoreCam.setShaderMatrices();

		int x = -150;
		int z = -110;
		for(int i = 0; i < score; i++) {
			Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 0f, 1f);
			ModelMatrix.main.loadIdentityMatrix();
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addScale(5f, 0.4f, 5f);
			ModelMatrix.main.addTranslationBaseCoords(x,1f,z);
			ModelMatrix.main.setShaderMatrix();
			SphereGraphic.drawSolidSphere();
			ModelMatrix.main.popMatrix();
			x += 20;
		}
	}

	@Override
	public void render () {

		//put the code inside the update and display methods, depending on the nature of the code
		update();
		display();

	}


	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}


}