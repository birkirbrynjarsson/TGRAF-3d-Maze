package com.ru.tgra.shapes;
import com.badlogic.gdx.Gdx;

public class Torch {

    // Public variables
    public final float x;
    public final float z;
    public final float y;

    // Private variables
    private  ModelMatrix mm;
    private int colorLoc;
    private float size;
    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;

    Torch(float x, float y, float z, ModelMatrix mm, int colorLoc) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mm = mm;
        this.colorLoc = colorLoc;
        this.size = 0.3f;
    }

    public void display(int side) {
        Gdx.gl.glUniform4f(colorLoc, 1, 1, 1, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size, size, size);
        mm.addTranslationBaseCoords(x,y,z);
        mm.setShaderMatrix();
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();

        if(side == HORIZONTAL){
            Gdx.gl.glUniform4f(colorLoc, 1, 1, 1, 1f);
            mm.loadIdentityMatrix();
            mm.pushMatrix();
            mm.addScale(size, 1f, size);
            mm.addTranslationBaseCoords(x+size,y,z);
            mm.setShaderMatrix();
            SphereGraphic.drawSolidSphere();
            mm.popMatrix();
        }
        else if(side == VERTICAL) {
            Gdx.gl.glUniform4f(colorLoc, 1, 1, 1, 1f);
            mm.loadIdentityMatrix();
            mm.pushMatrix();
            mm.addScale(size, 1f, size);
            mm.addTranslationBaseCoords(x,y,z+size);
            mm.setShaderMatrix();
            SphereGraphic.drawSolidSphere();
            mm.popMatrix();
        }
    }
}
