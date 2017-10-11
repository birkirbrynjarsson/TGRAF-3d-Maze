package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;

import java.util.Random;

public class Token {

    // Public variables
    public float size;
    public final float x;
    public final float z;

    // Private variables
    private float y;
    private ModelMatrix mm;
    private int colorLoc;
    private float speed;
    private Random rand;

    // Constants
    private final float MAX_HEIGHT = 4f;
    private final float MIN_HEIGHT = 3f;


    public Token(float x, float y, ModelMatrix mm, int colorLoc){
        this.x = x;
        this.z = y;
        this.mm = mm;
        this.colorLoc = colorLoc;
        this.y = MIN_HEIGHT;
        speed = 1;
        size = 1.5f;
    }

    public void display() {
        Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size, size, size);
        mm.addTranslationBaseCoords(x,y,z);
        mm.setShaderMatrix();
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();
    }

    public void bounce(float deltaTime) {
        if(y <= MIN_HEIGHT && (speed < 0)) {
            y = MIN_HEIGHT + 0.01f;
            speed = -speed;
        }
        else if(y >= MAX_HEIGHT && (speed > 0)) {
            y = MAX_HEIGHT - 0.01f;
            speed = -speed;
        }
        y = y + speed * deltaTime;
    }
}
