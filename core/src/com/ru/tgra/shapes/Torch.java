package com.ru.tgra.shapes;
import com.badlogic.gdx.Gdx;

public class Torch {

    // Private variables
    private  ModelMatrix mm;
    private int colorLoc;
    private float size;
    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;

    Torch(ModelMatrix mm, int colorLoc) {

        this.mm = mm;
        this.colorLoc = colorLoc;
        this.size = 0.3f;
    }

    public void display(int side, float x, float y, float z) {
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
