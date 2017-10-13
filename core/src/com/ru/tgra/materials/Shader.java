package com.ru.tgra.materials;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import java.nio.FloatBuffer;

public class Shader {

    private int renderingProgramID;
    private int vertexShaderID;
    private int fragmentShaderID;

    private int positionLoc;
    private int normalLoc;

    // Light
    private int lightPosLoc;
    private int lightDifLoc;
    private int matDifLoc;
    private int matShineLoc;
    private int eyePosLoc;


    private int modelMatrixLoc;
    private int viewMatrixLoc;
    private int projectionMatrixLoc;

    String vertexShaderString;
    String fragmentShaderString;

    public Shader() {
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

        lightPosLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightPosition");
        lightDifLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightDiffuse");
        matDifLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialDiffuse");
        eyePosLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_eyePosition");


        Gdx.gl.glUseProgram(renderingProgramID);
        System.out.println(Gdx.gl.glGetShaderInfoLog(vertexShaderID));
        System.out.println(Gdx.gl.glGetShaderInfoLog(fragmentShaderID));
    }

    public void setMaterialDiffuse(float r, float g, float b, float a) {
        Gdx.gl.glUniform4f(matDifLoc, r, g, b, a);
    }

    public void setLightDiffuse(float r, float g, float b, float a) {
        Gdx.gl.glUniform4f(lightDifLoc, r, g, b, a);
    }

    public void setLightPosition(float x, float y, float z, float a) {
        Gdx.gl.glUniform4f(lightPosLoc, x, y, z, a);
    }

    public void setEyePosition(float x, float y, float z, float a) {
        Gdx.gl.glUniform4f(lightPosLoc, x, y, z, a);
    }

    public int getVertexPointer() {
        return positionLoc;
    }

    public int getNormalPointer() {
        return normalLoc;
    }

    public void setModelMatrix(FloatBuffer matrix) {
        Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, matrix);
    }

    public void setViewMatrix(FloatBuffer matrix) {
        Gdx.gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, matrix);
    }

    public void setProjectionMatrix(FloatBuffer matrix) {
        Gdx.gl.glUniformMatrix4fv(projectionMatrixLoc, 1, false, matrix);
    }

}
