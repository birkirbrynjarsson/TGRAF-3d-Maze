package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;

import java.util.Collections;
import java.util.Arrays;
import java.util.Vector;

public class Maze
{
    private int x;
    private int y;
    private float cellSize;
    private float width;
    private final int[][] maze;
    private ModelMatrix mm;
    private int colorLoc;
    private float height;
    private int positionLoc;
    private int normalLoc;
    private long time;
    MazeBrickWall brickWall;
    MazeWoodWall woodWall;

    public Maze(int x, int y, float cellSize, ModelMatrix mm, int colorLoc, int positionLoc, int normalLoc) {
        this.x = x;
        this.y = y;
        this.cellSize = cellSize;
        maze = new int[this.x][this.y];
        this.mm = mm;
        this.colorLoc = colorLoc;
        this.width = cellSize/5;
        this.height = 5f;
        generateMaze(0, 0);
        this.positionLoc = positionLoc;
        this.normalLoc = normalLoc;
        time = System.nanoTime();
        brickWall = new MazeBrickWall(cellSize, width, height, mm, colorLoc, positionLoc, normalLoc);
        woodWall = new MazeWoodWall(cellSize, width, height, mm, colorLoc, positionLoc, normalLoc);
    }

    public void display(boolean roofOn) {

        displayFloor();
        boolean playerView = roofOn;
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
                    displayVerWall(i, j, playerView);
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
                    displayHorWall(i, j, playerView);
                }
                else {
                    displayPillar(i, j);
                }
                // Nothing
            }
            displayHorWall(i, j, playerView);
        }
        // draw the bottom line
        for (j = 0; j < x; j++) {
            displayPillar(i, j);
            displayVerWall(i, j, playerView);
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

    public void levelUp(int mazeSize) {
        this.x = mazeSize;
        this.y = mazeSize;
        generateMaze(0,0);
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
        float floorThickness = 0.1f;
        Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(x * cellSize, 0.1f, y * cellSize);
        mm.addTranslationBaseCoords((x * cellSize)/2,-floorThickness/2,(y * cellSize)/2);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayRoof(){
        Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(x * cellSize, 0.1f, y * cellSize);
        mm.addTranslationBaseCoords((x * cellSize)/2, height,(y * cellSize)/2);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
    }

    private void displayHorWall(int i, int j, boolean playerView){
        if(playerView){
            woodWall.displayHorWall(i, j);
        } else {
            Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1.0f);
            mm.loadIdentityMatrix();
            mm.pushMatrix();
            mm.addScale(cellSize, height, width);
            mm.addTranslationBaseCoords((float) i * cellSize + (cellSize / 2), height / 2, (float) j * cellSize);
            mm.setShaderMatrix();
            BoxGraphic.drawSolidCube();
            mm.popMatrix();
        }
    }

    private void displayVerWall(int i, int j, boolean playerView){
        if(playerView){
            brickWall.displayVerWall(i, j);
        } else {
            Gdx.gl.glUniform4f(colorLoc, 1f,1f,1f, 1.0f);
            mm.loadIdentityMatrix();
            mm.pushMatrix();
            mm.addScale(width, height, cellSize);
            mm.addTranslationBaseCoords((float)i*cellSize, height/2, (float)j*cellSize+(cellSize/2));
            mm.setShaderMatrix();
            BoxGraphic.drawSolidCube();
            mm.popMatrix();
        }
    }

    private void displayPillar(int i, int j){
        Gdx.gl.glUniform4f(colorLoc, 0.1f,0.1f,0.1f, 0.1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(width+width/3, height+1f, width+width/3);
        mm.addTranslationBaseCoords((float)i*cellSize, height/2, (float)j*cellSize);
        mm.setShaderMatrix();
        BoxGraphic.drawSolidCube();
        mm.popMatrix();
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
        if((0 <= (int)xPoint || (int)xPoint < this.x) && (0 <= (int)zPoint || (int)zPoint < this.y)) {
            if ((maze[(int) zPoint][(int) xPoint] & 8) == 0) return false;
        }
        return true;
    }

    boolean openSouth(float x, float z){
        float xPoint = x/cellSize;
        float zPoint = z/cellSize;
        if((0 <= (int)xPoint || (int)xPoint < this.x) && (0 <= (int)zPoint || (int)zPoint < this.y)) {
            if ((maze[(int) zPoint][(int) xPoint] & 4) == 0) return false;
        }
        return true;
    }

    boolean openEast(float x, float z){
        float xPoint = x/cellSize;
        float zPoint = z/cellSize;
        if((0 <= (int)xPoint || (int)xPoint < this.x) && (0 <= (int)zPoint || (int)zPoint < this.y)) {
            if ((maze[(int) zPoint][(int) xPoint] & 2) == 0) return false;
        }
        return true;
    }

    boolean openWest(float x, float z){
        float xPoint = x/cellSize;
        float zPoint = z/cellSize;
        if((0 <= (int)xPoint || (int)xPoint < this.x) && (0 <= (int)zPoint || (int)zPoint < this.y)) {
            if((maze[(int)zPoint][(int)xPoint] & 1) == 0) return false;
        }
        return true;
    }

    public int getCellValue(float x, float z){
        float xPoint = x/cellSize;
        float zPoint = z/cellSize;
        if((0 <= (int)xPoint || (int)xPoint < this.x) && (0 <= (int)zPoint || (int)zPoint < this.y)) {
            return maze[(int) zPoint][(int) xPoint];
        } else {
            return 15; // All open
        }
    }

    public float getNorthZ(float z){
        float zPoint = z / cellSize;
        int northCell = (int)zPoint - 1;
        return (float)northCell * cellSize + cellSize/2;
    }

    public float getSouthZ(float z){
        float zPoint = z / cellSize;
        int southCell = (int)zPoint + 1;
        return (float)southCell * cellSize + cellSize/2;
    }

    public float getWestX(float x){
        float xPoint = x/cellSize;
        int westCell = (int)xPoint - 1;
        return (float)westCell * cellSize + cellSize/2;
    }

    public float getEastX(float x){
        float xPoint = x/cellSize;
        int eastCell = (int)xPoint + 1;
        return (float)eastCell * cellSize + cellSize/2;
    }
}
