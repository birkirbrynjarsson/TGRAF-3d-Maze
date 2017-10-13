package com.ru.tgra.objects.maze;

import com.badlogic.gdx.graphics.Color;
import com.ru.tgra.shapes.BoxGraphic;
import com.ru.tgra.models.ModelMatrix;
import com.ru.tgra.materials.Shader;

import java.util.Random;

public class MazeBrickWall
{
    private float cellSize;
    private ModelMatrix mm;
    private float width;
    private float height;
    int hSplit;
    int vSplit;
    float fua;
    private final Color[][] colorArr;
    private final float[][] extrude;
    private final Color[] colorOptions = {
            new Color(0x6E0D0AFF),
            new Color(0xE68785FF),
            new Color(0x330100FF),
            new Color(0xAA3C39FF)
    };
    Shader shader;

    public MazeBrickWall(float cellSize, float width, float height, ModelMatrix mm, Shader shader) {
        this.cellSize = cellSize;
        this.mm = mm;
        this.width = width;
        this.height = height;
        this.hSplit = 12;
        this.vSplit = 6;
        fua = cellSize / vSplit / 18;
        colorArr = new Color[hSplit][vSplit];
        extrude = new float[hSplit][vSplit];
        setColor();
        setExtrution();
        this.shader = shader;
    }

    private void setColor(){
        Random rand = new Random();
        for(int i = 0; i < hSplit; i++){
            for(int j = 0; j < vSplit; j++){
                colorArr[i][j] = colorOptions[rand.nextInt(colorOptions.length)];
            }
        }
    }

    private void setExtrution(){
        for(int i = 0; i < hSplit; i++){
            for(int j = 0; j < vSplit; j++){
                extrude[i][j] = 0.06f + (float)Math.random() * (width/4 - 0.06f);
            }
        }
    }

    public void displayVerWall(int i, int j)
    {
        for (int h = 0; h < hSplit; h++) {
            for (int v = 0; v < vSplit; v++) {
                shader.setMaterialDiffuse(colorArr[h][v].r, colorArr[h][v].g, colorArr[h][v].b, 1.0f);
                mm.loadIdentityMatrix();
                mm.pushMatrix();
                if (h % 2 == 0) {
                    mm.addScale(width + extrude[h][v], height / hSplit - fua, cellSize / vSplit - fua);
                    mm.addTranslationBaseCoords((float) i * cellSize, height / hSplit * h + height/hSplit/3, (float) j * cellSize + (cellSize / vSplit * v));
                } else {
                    mm.addScale(width + extrude[h][v], height / hSplit - fua, cellSize / vSplit - fua);
                    mm.addTranslationBaseCoords((float) i * cellSize, height / hSplit * h + height/hSplit/3, (float) j * cellSize + (cellSize / vSplit * v) + cellSize / vSplit / 2);
                }
                shader.setModelMatrix(mm.getMatrix());
                BoxGraphic.drawSolidCube();
                mm.popMatrix();
            }
        }
        shader.setMaterialDiffuse(1f, 1f, 1f, 1.0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(width, height, cellSize);
        mm.addTranslationBaseCoords((float) i * cellSize, height / 2, (float) j * cellSize + (cellSize / 2));
        shader.setModelMatrix(mm.getMatrix());
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }
}
