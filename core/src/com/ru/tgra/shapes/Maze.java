package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;

import java.util.Collections;
import java.util.Arrays;

public class Maze
{
    private final int x;
    private final int y;
    private float cellSize;
    private float width;
    private final int[][] maze;
    private ModelMatrix mm;
    private int colorLoc;
    private float height;
    private int positionLoc;
    private int normalLoc;

    public Maze(int x, int y, float cellSize, ModelMatrix mm, int colorLoc, int positionLoc, int normalLoc) {
        this.x = x;
        this.y = y;
        this.cellSize = cellSize;
        maze = new int[this.x][this.y];
        this.mm = mm;
        this.colorLoc = colorLoc;
        this.width = cellSize/5;
        this.height = 10f;
        generateMaze(0, 0);
        this.positionLoc = positionLoc;
        this.normalLoc = normalLoc;
    }

    public void display() {
        int i, j;
        for (i = 0; i < y; i++) {
            // draw the north edge
            for (j = 0; j < x; j++) {
                //System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
                if((maze[j][i] & 1) == 0 ) {
                    displayHorWall(i, j);
                }
                else {
                    displayPillar(i, j);
                }
            }
            //System.out.println("+");
            displayPillar(i, j);
            // draw the west edge
            for (j = 0; j < x; j++) {
                //System.out.print((maze[j][i] & 8) == 0 ? "|   " : "    ");
                if((maze[j][i] & 8) == 0){
                    displayVerWall(i, j);
                }
                // Nothing
            }
            displayVerWall(i, j);
            //System.out.println("|");
        }
        // draw the bottom line
        for (j = 0; j < x; j++) {
            displayHorWall(i, j);
            //System.out.print("+---");
        }
        displayPillar(i, j);
        //System.out.println("+");
    }

    public void display2D() {
        int i, j;
        for (i = 0; i < y; i++) {
            // draw the north edge
            for (j = 0; j < x; j++) {
                System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
            }
            System.out.println("+");
            // draw the west edge
            for (j = 0; j < x; j++) {
                System.out.print((maze[j][i] & 8) == 0 ? "|   " : "    ");
                // Nothing
            }
            System.out.println("|");
        }
        // draw the bottom line
        for (j = 0; j < x; j++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }

    private void generateMaze(int cx, int cy) {
        DIR[] dirs = DIR.values();
        Collections.shuffle(Arrays.asList(dirs));
        for (DIR dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            if (between(nx, x) && between(ny, y)
                    && (maze[nx][ny] == 0)) {
                maze[cx][cy] |= dir.bit;
                maze[nx][ny] |= dir.opposite.bit;
                generateMaze(nx, ny);
            }
        }
    }

    private static boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }

    private enum DIR {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
        private final int bit;
        private final int dx;
        private final int dy;
        private DIR opposite;

        // use the static initializer to resolve forward references
        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }

        private DIR(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    }

    private void displayVerWall(int i, int j){
        Gdx.gl.glUniform4f(colorLoc, 0.6f,0.0f,0.6f, 1.0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(cellSize, height, width);
        mm.addTranslationBaseCoords((float)i*cellSize+(cellSize/2), 0, (float)j*cellSize);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayHorWall(int i, int j){
        Gdx.gl.glUniform4f(colorLoc, 0.6f,0.0f,0.6f, 1.0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(width, height, cellSize);
        mm.addTranslationBaseCoords((float)i*cellSize, 0, (float)j*cellSize+(cellSize/2));
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayPillar(int i, int j){
        Gdx.gl.glUniform4f(colorLoc, 0.6f,0.0f,0.6f, 1.0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(width, height, width);
        mm.addTranslationBaseCoords((float)i*cellSize, 0, (float)j*cellSize);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }
}
