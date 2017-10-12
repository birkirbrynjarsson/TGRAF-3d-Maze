package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;

import java.util.Collections;
import java.util.Arrays;
import java.util.Vector;

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
    private long time;

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
        time = System.nanoTime();

    }

    public void display(boolean roofOn) {

        displayFloor();
        if(roofOn){
            displayRoof();
        }
        int i, j;
        for (i = 0; i < y; i++) {
            // draw the north edge
            for (j = 0; j < x; j++) {
                //System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
                if((maze[j][i] & 1) == 0 ) {
                    displayPillar(i, j);
                    displayVerWall(i, j, false);
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
                    displayHorWall(i, j, false);
                }
                else {
                    displayPillar(i, j);
                }
                // Nothing
            }
            displayHorWall(i, j, false);
        }
        // draw the bottom line
        for (j = 0; j < x; j++) {
            displayPillar(i, j);
            displayVerWall(i, j, false);
        }
        displayPillar(i, j);
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

    private void displayFloor(){
        Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(x * cellSize, 1f, y * cellSize);
        mm.addTranslationBaseCoords((x * cellSize)/2,0.5f,(y * cellSize)/2);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayRoof(){
        Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(x * cellSize, height, y * cellSize);
        mm.addTranslationBaseCoords((x * cellSize)/2,0.5f,(y * cellSize)/2);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayHorWall(int i, int j, boolean extraLength){
//        Gdx.gl.glUniform4f(colorLoc, 0.6f,0.0f,0.6f, 1.0f);
        Gdx.gl.glUniform4f(colorLoc, 1f,1f,1f, 1.0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        if(extraLength){
            mm.addScale(cellSize+width, height, width);
        } else {
            mm.addScale(cellSize, height, width);
        }
        mm.addTranslationBaseCoords((float)i*cellSize+(cellSize/2), 0, (float)j*cellSize);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayVerWall(int i, int j, boolean extraLength){
        Gdx.gl.glUniform4f(colorLoc, 1f,1f,1f, 1.0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        if(extraLength){
            mm.addScale(width, height, cellSize+width);
        } else {
            mm.addScale(width, height, cellSize);
        }
        mm.addTranslationBaseCoords((float)i*cellSize, 0, (float)j*cellSize+(cellSize/2));
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayHorWall(int startI, int endI, int j){
        Gdx.gl.glUniform4f(colorLoc, 1f,1f,1f, 1.0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(cellSize*(endI - startI), height, width);
        float translationX = (float)(startI + endI) / 2;
        mm.addTranslationBaseCoords(translationX*cellSize, 0, (float)j*cellSize);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayPillar(int i, int j){
        Gdx.gl.glUniform4f(colorLoc, 1f,1f,1f, 1.0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(width+width/3, height, width+width/3);
        mm.addTranslationBaseCoords((float)i*cellSize, 0, (float)j*cellSize);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    public void isWalls(Point3D point){
        float xPoint = point.x/cellSize;
        float zPoint = point.z/cellSize;
        if((System.nanoTime() - time) > 2000000000l){
            String ourPosition = "x: " + point.x + " y: " + point.y + " z: " + point.z;
            System.out.println(ourPosition);
            System.out.println("xPoint = " + xPoint + ", zPoint = " + zPoint);
            System.out.println("INT, X: " + (int)xPoint + ", Z: " + (int)zPoint);
            // Use X point in 2nd array
            System.out.println("CELL: " + maze[(int)zPoint][(int)xPoint]);
            if((maze[(int)zPoint][(int)xPoint] & 8) == 0){
                System.out.println("Wall North");
            }
            if((maze[(int)zPoint][(int)xPoint] & 4) == 0){
                System.out.println("Wall South");
            }
            if((maze[(int)zPoint][(int)xPoint] & 2) == 0){
                System.out.println("Wall East");
            }
            if((maze[(int)zPoint][(int)xPoint] & 1) == 0){
                System.out.println("Wall West");
            }
            time = System.nanoTime();
        }
    }

    public float cellLimitNorth(float z){
        float zPoint = z/cellSize;
        return (float)(int)zPoint * cellSize + width/2;
    }

    public float cellLimitSouth(float z){
        float zPoint = z/cellSize;
        return (float)(int)zPoint * cellSize - width/2 + cellSize;
    }

    public float cellLimitEast(float x){
        float xPoint = x/cellSize;
        return (float)(int)xPoint * cellSize - width/2 + cellSize;
    }

    public float cellLimitWest(float x){
        float xPoint = x/cellSize;
        return (float)(int)xPoint * cellSize + width/2;
    }

    boolean openNorth(float x, float z){
        float xPoint = x/cellSize;
        float zPoint = z/cellSize;
        if((maze[(int)zPoint][(int)xPoint] & 8) == 0) return false;
        return true;
    }

    boolean openSouth(float x, float z){
        float xPoint = x/cellSize;
        float zPoint = z/cellSize;
        if((maze[(int)zPoint][(int)xPoint] & 4) == 0) return false;
        return true;
    }

    boolean openEast(float x, float z){
        float xPoint = x/cellSize;
        float zPoint = z/cellSize;
        if((maze[(int)zPoint][(int)xPoint] & 2) == 0) return false;
        return true;
    }

    boolean openWest(float x, float z){
        float xPoint = x/cellSize;
        float zPoint = z/cellSize;
        if((maze[(int)zPoint][(int)xPoint] & 1) == 0) return false;
        return true;
    }
}
