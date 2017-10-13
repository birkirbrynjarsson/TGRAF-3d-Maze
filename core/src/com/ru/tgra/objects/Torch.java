package com.ru.tgra.objects;
import com.ru.tgra.models.ModelMatrix;
import com.ru.tgra.materials.Shader;
import com.ru.tgra.shapes.SphereGraphic;

public class Torch {

    // Private variables
    private ModelMatrix mm;
    private Shader shader;
    private float size;
    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;

    Torch(ModelMatrix mm, Shader shader) {

        this.mm = mm;
        this.shader = shader;
        this.size = 0.3f;
    }

    public void display(int side, float x, float y, float z) {
        shader.setMaterialDiffuse( 1, 1, 1, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size, size, size);
        mm.addTranslationBaseCoords(x,y,z);
        shader.setModelMatrix(mm.getMatrix());
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();

        if(side == HORIZONTAL){
            shader.setMaterialDiffuse( 1, 1, 1, 1f);
            mm.loadIdentityMatrix();
            mm.pushMatrix();
            mm.addScale(size, 1f, size);
            mm.addTranslationBaseCoords(x+size,y,z);
            shader.setModelMatrix(mm.getMatrix());
            SphereGraphic.drawSolidSphere();
            mm.popMatrix();
        }
        else if(side == VERTICAL) {
            shader.setMaterialDiffuse( 1, 1, 1, 1f);
            mm.loadIdentityMatrix();
            mm.pushMatrix();
            mm.addScale(size, 1f, size);
            mm.addTranslationBaseCoords(x,y,z+size);
            shader.setModelMatrix(mm.getMatrix());
            SphereGraphic.drawSolidSphere();
            mm.popMatrix();
        }
    }
}
