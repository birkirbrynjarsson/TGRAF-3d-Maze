package com.ru.tgra.objects;

import com.badlogic.gdx.graphics.Color;
import com.ru.tgra.models.ModelMatrix;
import com.ru.tgra.materials.Shader;
import com.ru.tgra.shapes.SphereGraphic;

import java.util.Random;

public class Token {

    // Public variables
    public float size;
    public final float x;
    public final float z;

    // Private variables
    private float y;
    private ModelMatrix mm;
    private Shader shader;
    private float speed;
    private Random rand;
    private Color color;

    // Constants
    private final float MAX_HEIGHT = 2.5f;
    private final float MIN_HEIGHT = 1.5f;


    public Token(float x, float y, ModelMatrix mm, Shader shader){
        this.x = x;
        this.z = y;
        this.mm = mm;
        this.shader = shader;
        rand = new Random();
        color = new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),1);

        this.y = rand.nextFloat() + MIN_HEIGHT;
        speed = 1;
        size = 0.5f;
    }

    public void display() {
        shader.setMaterialDiffuse(color.r, color.g, color.b, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size, size, size);
        mm.addTranslationBaseCoords(x,y,z);
        shader.setModelMatrix(mm.getMatrix());
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
