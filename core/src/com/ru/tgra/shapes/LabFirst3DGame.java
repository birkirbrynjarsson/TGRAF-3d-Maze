package com.ru.tgra.shapes;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;

import java.util.ArrayList;
import java.util.Random;

public class LabFirst3DGame extends ApplicationAdapter {

	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;
	private int normalLoc;

	private int modelMatrixLoc;
	private int viewMatrixLoc;
	private int projectionMatrixLoc;

	// Player variables
	private final int GOD_MODE = 0;
	private final int FIRST_PERSON = 1;
 	private int playerViewMode;
	private float playerDirection;
	private int score;

	// Game variables
	private int level;
	private int mazeSize;
	private float cellSize;

	// Camera variables
	private Camera cam;
	private Camera orthoCam;
	private Camera scoreCam;
	private float orthoZoom = 20f;
	float angle;

	// Token variables
	private ArrayList<Token> tokens;
	ArrayList<Point3D> tokenPositions;
	private int tokenNumber;
	private Random rand;

	private int colorLoc;
	private float fov = 50.0f;

	private Maze maze;
	private float movementSpeed = 3f; // used with deltatime, WASD keys
	private float mouseSpeed = 10f;
	private float playerSize = 1f; // Radius of player circle, for collision and display in 2D

	@Override
	public void create () {

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
		mazeSize = 8;
		cellSize = 6f;
		maze = new Maze(mazeSize, mazeSize, cellSize, ModelMatrix.main, colorLoc, positionLoc, normalLoc);

		tokenNumber = (mazeSize*mazeSize) / 2;

		// ----------------------------------
		// 		Camera init & settings
		// ----------------------------------
		// --- Player camera ---
		cam = new Camera(viewMatrixLoc, projectionMatrixLoc);
		cam.perspectiveProjection(fov, (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight(), 0.4f, 100.0f);
		cam.look(new Point3D((cellSize/2), 3f, (cellSize/2)), new Point3D(6,3,(cellSize/2)), new Vector3D(0,1,0));
		// --- Mini map camera ---
		orthoCam = new Camera(viewMatrixLoc, projectionMatrixLoc);
		orthoCam.orthographicProjection(-orthoZoom,orthoZoom,-orthoZoom,orthoZoom,1.0f, 100.0f);
		// --- Score camera ---
		scoreCam = new Camera(viewMatrixLoc, projectionMatrixLoc);
		scoreCam.orthographicProjection(-83.3f,83.3f,-25.0f,25.0f,1.0f, 100.0f);

		// ----------------------------------
		// 		  Game play settings
		// ----------------------------------
		playerViewMode = FIRST_PERSON;
		playerDirection = 0f;
		score = 0;
		level = 1;

		// ----------------------------------
		// 		  Token settings
		// ----------------------------------
		rand = new Random();
		tokenPositions = new ArrayList<Point3D>();
		tokens = new ArrayList<Token>();
		initializeTokens();
	}


	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();

		angle += 180.0f * deltaTime;

		Gdx.input.setCursorCatched(true);

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cam.roll(90.f * deltaTime);
			playerDirection -= 90f * deltaTime;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cam.roll(-90.f * deltaTime);
			playerDirection += 90f * deltaTime;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			if(playerViewMode == GOD_MODE) {
				cam.slide(-movementSpeed * deltaTime, 0, 0);
			}
			else {
				cam.slideMaze(-movementSpeed * deltaTime, 0, 0, maze, playerSize);
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			if(playerViewMode == GOD_MODE) {
				cam.slide(movementSpeed * deltaTime, 0, 0);
			}
			else {
				cam.slideMaze(movementSpeed * deltaTime, 0, 0, maze, playerSize);
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			if(playerViewMode == GOD_MODE) {
				cam.slide(0, 0, -movementSpeed * deltaTime);
			}
			else {
				cam.slideMaze(0, 0, -movementSpeed * deltaTime, maze, playerSize);
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			if(playerViewMode == GOD_MODE){
				cam.slide(0, 0, movementSpeed * deltaTime);
			}
			else if(playerViewMode == FIRST_PERSON) {
				cam.slideMaze(0, 0, movementSpeed * deltaTime, maze, playerSize);
			}
		}
		if(playerViewMode == GOD_MODE)
		{
			if(Gdx.input.isKeyPressed(Input.Keys.R)) {
				cam.slide(0, -movementSpeed * deltaTime, 0);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.F)) {
				cam.slide(0, movementSpeed * deltaTime, 0);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
				cam.roll(-90.f * deltaTime);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.E)) {
				cam.roll(90.f * deltaTime);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
				cam.pitch(-90.f * deltaTime);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				cam.pitch(90.f * deltaTime);
			}
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.V)) {
			if(playerViewMode == GOD_MODE) {
				playerViewMode = FIRST_PERSON;
				cam.perspectiveProjection(fov, 1.0f, 0.4f, 100.0f);
				cam.look(new Point3D((cellSize/2), 3f, (cellSize/2)), new Point3D(6,3,(cellSize/2)), new Vector3D(0,1,0));
			}
			else {
				playerViewMode = GOD_MODE;
			}
		}

		if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			float acceleration = 1.1f;
			if(movementSpeed <= 8f)
				movementSpeed *= acceleration;
		}
		else
		{
			movementSpeed = 3f;
		}

		// --- Token updates ---
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

		// --- Level updates ---
		if(score == tokenNumber) {
			levelUp();
		}

		// --- Mouse movement ---

		cam.roll(-Gdx.input.getDeltaX() * deltaTime * mouseSpeed);
		cam.pitch(-Gdx.input.getDeltaY() * deltaTime * mouseSpeed);

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
				Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				cam.perspectiveProjection(fov, (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight(), 0.1f, 100.0f);
				cam.setShaderMatrices();
			}
			// -- The minimap view --
			else
			{
				int miniMapHeight = 250;
				int miniMapWidth = 250;
				Gdx.gl.glViewport((Gdx.graphics.getWidth() - miniMapWidth), Gdx.graphics.getHeight() - miniMapHeight, miniMapWidth, miniMapHeight);
				Point3D camTrace = new Point3D(cam.eye.x, cam.eye.y, cam.eye.z);
				if(orthoZoom * 2 > mazeSize * cellSize){
					camTrace.x = mazeSize * cellSize/2;
					camTrace.z = mazeSize * cellSize/2;
				} else{
					if(camTrace.x < orthoZoom){
						camTrace.x = orthoZoom - cellSize/4;
					} else if(camTrace.x > (mazeSize * cellSize) - orthoZoom){
						camTrace.x = (mazeSize * cellSize) - orthoZoom + cellSize/4;
					}
					if(camTrace.z < orthoZoom){
						camTrace.z = orthoZoom - cellSize/4;
					} else if((camTrace.z > (mazeSize * cellSize) - orthoZoom)) {
						camTrace.z = (mazeSize * cellSize) - orthoZoom + cellSize/4;
					}
				}
				orthoCam.look(new Point3D(camTrace.x, 10.0f, camTrace.z), camTrace, new Vector3D(0,0,-1));
//				orthoCam.look(new Point3D(cam.eye.x, 10.0f, cam.eye.z), cam.eye, new Vector3D(0,0,-1));
				orthoCam.setShaderMatrices();
			}


			// ----------------------------------
			// 		 Draw our MAZE here
			// ----------------------------------

			maze.display(viewNum == 0);

			for(Token token : tokens) {
				token.display();
			}

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

		// ----------------------------------
		// 		    Score display
		// ----------------------------------
		displayScore();
	}

	public void displayScore() {

		int scoreHeight = 150;
		int scoreWidth = 500;

		Gdx.gl.glViewport(0, Gdx.graphics.getHeight() - scoreHeight, scoreWidth, scoreHeight);

		scoreCam.look(new Point3D(0,40,0), new Point3D(0,1,0), new Vector3D(0,0,-1));
		scoreCam.setShaderMatrices();

		int x = 10;
		int z = -10;
		float scorebarLength = 150f;
		float scorebarHeight = 5f;
		float scoreSlotLength = (float)scorebarLength/(float)tokenNumber;

		// Drawing empty scorebar
		Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 0.5f);
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.pushMatrix();
		ModelMatrix.main.addScale(scorebarLength, 0.4f, scorebarHeight);
		ModelMatrix.main.addTranslationBaseCoords(x,1f,z);
		ModelMatrix.main.setShaderMatrix();
		BoxGraphic.drawSolidCube();
		ModelMatrix.main.popMatrix();

		// Drawing score on the scorebar
		for(int i = 0; i < score; i++) {
			Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 0f, 1f);
			ModelMatrix.main.loadIdentityMatrix();
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addScale(scoreSlotLength, 0.5f, scorebarHeight);
			ModelMatrix.main.addTranslationBaseCoords(x-(scorebarLength/2),1f,z);
			ModelMatrix.main.setShaderMatrix();
			BoxGraphic.drawSolidCube();
			ModelMatrix.main.popMatrix();
			x += scoreSlotLength;
		}
	}

	@Override
	public void render () {

		//put the code inside the update and display methods, depending on the nature of the code
		update();
		display();

	}


	private void levelUp() {
		level++;
		//mazeSize++;
		score = 0;
		initializeTokens();
		// Generate maze
		// Set player to initial position
	}

	private void initializeTokens() {
		tokens.clear();
		tokenPositions.clear();

		// Initialize game tokens
		for(int i = 0; i < tokenNumber; i++) {
			float x;
			float y;
			while(true) {
				x = ((rand.nextInt(mazeSize-1) * cellSize) + (cellSize / 2));
				y = ((rand.nextInt(mazeSize-1) * cellSize) + (cellSize / 2));

				if(!doublePosition(x, y) && !(x == (cellSize / 2) && y == (cellSize / 2))) {
					tokenPositions.add(new Point3D(x, y, 0));
					break;
				}
			}

			tokens.add(new Token(x, y, ModelMatrix.main, colorLoc));
		}
	}

	private boolean doublePosition(float x, float y){
		for(Point3D position : tokenPositions) {
			if(position.x == x && position.y == y) {
				return true;
			}
		}
		return false;
	}
}