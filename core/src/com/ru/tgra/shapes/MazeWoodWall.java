package com.ru.tgra.shapes;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.Color;

        import java.awt.*;
        import java.util.Random;

public class MazeWoodWall
{
    private float cellSize;
    private ModelMatrix mm;
    private int colorLoc;
    private float width;
    private float height;
    private int positionLoc;
    int hSplit;
    float fua;
    private final Color[] colorArr;
    private final float[] extrude;
    private final Color[] colorOptions = {
            new Color(0x5E452FFF),
            new Color(0x3D2C1DFF),
            new Color(0x54361CFF),
            new Color(0x603815FF)
    };

    public MazeWoodWall(float cellSize, float width, float height, ModelMatrix mm, int colorLoc, int positionLoc, int normalLoc) {
        this.cellSize = cellSize;
        this.mm = mm;
        this.colorLoc = colorLoc;
        this.width = width;
        this.height = height;
        this.positionLoc = positionLoc;
        this.hSplit = 6;
        fua = cellSize / hSplit / 3;
        colorArr = new Color[hSplit];
        extrude = new float[hSplit];
        setColor();
        setExtrution();
    }

    private void setColor(){
        Random rand = new Random();
        for(int i = 0; i < hSplit; i++){
            colorArr[i] = colorOptions[rand.nextInt(colorOptions.length)];
        }
    }

    private void setExtrution(){
        for(int i = 0; i < hSplit; i++){
            extrude[i] = 0.06f + (float)Math.random() * (width/4 - 0.06f);
        }
    }

    public void displayHorWall(int i, int j)
    {
        for (int h = 0; h < hSplit; h++) {
            Gdx.gl.glUniform4f(colorLoc, colorArr[h].r, colorArr[h].g, colorArr[h].b, 1.0f);
            mm.loadIdentityMatrix();
            mm.pushMatrix();
            mm.addScale(cellSize, height / hSplit - fua, width + extrude[h]);
            mm.addTranslationBaseCoords((float)i*cellSize+(cellSize/2), height / hSplit * h + height/hSplit/2, (float)j*cellSize);
            mm.setShaderMatrix();
            BoxGraphic.drawSolidCube();
            mm.popMatrix();
        }
    }
}
