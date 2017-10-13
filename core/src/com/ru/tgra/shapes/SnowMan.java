package com.ru.tgra.shapes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Random;

public class SnowMan
{

    public Point3D pos;
    Vector3D u;
    Vector3D v;
    Vector3D n;

    private ModelMatrix mm;
    int colorLoc;
    float size;
    boolean moving;
    Point3D destination;
    Random rand;

    int direction; // 1 north, 2 south, 3 east, 4 west

    private FloatBuffer matrixBuffer;

    public SnowMan(float x, float z, ModelMatrix mm, int colorLoc)
    {
        this.mm = mm;
        this.colorLoc = colorLoc;
        this.size = 1f;
        moving = false;
        rand = new Random();
        moving = false;
        direction = 2;

        pos = new Point3D(x, 1.1f, z);
        u = new Vector3D(1,0,0);
        v = new Vector3D(0,1,0);
        n = new Vector3D(0,0,1);
    }
    
    public void display(){
        // Bottom ball
        Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size, size, size);
        mm.addTranslationBaseCoords(pos.x,pos.y,pos.z);
        mm.setShaderMatrix();
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();
        // 2nd Ball
        Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size*0.7f, size*0.7f, size*0.7f);
        mm.addTranslationBaseCoords(pos.x,pos.y + 1.3f,pos.z);
        mm.setShaderMatrix();
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();
        // Face ball
        Gdx.gl.glUniform4f(colorLoc, 1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size*0.5f, size*0.5f, size*0.5f);
        mm.addTranslationBaseCoords(pos.x,pos.y + 2.2f,pos.z);
        mm.setShaderMatrix();
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();
    }

    private boolean getOpen(int dir, Maze maze){
        if(dir == 1){
            return maze.openNorth(pos.x, pos.z);
        } else if(dir == 2){
            return maze.openSouth(pos.x, pos.z);
        } else if(dir == 3){
            return maze.openEast(pos.x, pos.z);
        } else if(dir == 4){
            return maze.openWest(pos.x, pos.z);
        }
        return false;
    }

    Point3D destination(int dir, Maze maze){
        if(dir == 1){
            return new Point3D(pos.x, pos.y, maze.getNorthZ(pos.z));
        } else if(dir == 2){
            return new Point3D(pos.x, pos.y, maze.getSouthZ(pos.z));
        } else if(dir == 3){
            return new Point3D(maze.getEastX(pos.x), pos.y, pos.z);
        } else if(dir == 4){
            return new Point3D(maze.getWestX(pos.x), pos.y, pos.z);
        }
        System.out.println("Failed to update destination of snowman");
        return pos;
    }

    public void move(Maze maze, Point3D playerPos, float speed){
        if(!moving){
            int i = 0;
            int cell = maze.getCellValue(pos.x, pos.z);
            if(direction == 1){ // North
                // Check if closed in all directions
                if(cell == 4){ // North Dead must turn around
                    direction = 2; // South
                    moving = true;
                    destination.z = maze.getSouthZ(pos.z);
                } else {
                    i = rand.nextInt(3);
                    while(!getOpen(i, maze)){
                        i = (i + 1)%3;
                    }
                    direction = i;
                    moving = true;
                    destination = destination(i, maze);
                }
            } else if(direction == 2){ // Going South
                if(cell == 8){
                    direction = 1; // North
                    moving = true;
                    destination.z = maze.getNorthZ(pos.z);
                } else {
                    i = rand.nextInt(3);
                    while(!getOpen(i, maze)){
                        i = (i + 1)%3;
                    }
                    direction = i;
                    moving = true;
                    destination = destination(i, maze);
                }
            } else if(direction == 3){ // East
                if(cell == 1){
                    direction = 4;
                    moving = true;
                    destination.x = maze.getWestX(pos.x);
                } else {
                    i = rand.nextInt(3);
                    while(!getOpen(i, maze)){
                        i = (i + 1)%3;
                    }
                    direction = i;
                    moving = true;
                    destination = destination(i, maze);
                }
            } else if(direction == 4){ // West
                if(cell == 2){
                    direction = 3;
                    moving = true;
                    destination.x = maze.getEastX(pos.x);
                } else {
                    i = rand.nextInt(3);
                    while(!getOpen(i, maze)){
                        i = (i + 1)%3;
                    }
                    direction = i;
                    moving = true;
                    destination = destination(i, maze);
                }
            }
        } else {
            if(direction == 1) { // North
                if (pos.z < destination.z) {
                    moving = false;
                } else {
                    pos.z -= speed;
                }
            } else if(direction == 2) { // South
                if(pos.z > destination.z) {
                    moving = false;
                } else {
                    pos.z += speed;
                }
            } else if(direction == 3) { // East
                if(pos.x > destination.x){
                    moving = false;
                } else {
                    pos.x += speed;
                }
            } else if(direction == 4) { // West
                if(pos.x < destination.x){
                    moving = false;
                } else {
                    pos.x -= speed;
                }
            }
        }
    }

    public void look(Point3D pos, Point3D center, Vector3D up)
    {
        this.pos.set(pos.x, pos.y, pos.z);
        n = Vector3D.difference(pos, center);
        u = up.cross(n);
        n.normalize();
        u.normalize();
        v = n.cross(u);
    }

    public void setEye(float x, float y, float z) {
        pos.set(x, y, z);
    }

    public void slide(float delU, float delV, float delN)
    {
        pos.x += delU*u.x + delV*v.x + delN*n.x;
        pos.y += delU*u.y + delV*v.y + delN*n.y;
        pos.z += delU*u.z + delV*v.z + delN*n.z;
    }

    public void slideMaze(float delU, float delV, float delN, Maze maze, float radius){
        float posX = pos.x + (delU*u.x + delV*v.x + delN*n.x);
        float posZ = pos.z + (delU*u.z + delV*v.z + delN*n.z);

        if((pos.z - posZ) > 0.0f) { // Moving North
            float limitNorth = maze.cellLimitNorth(posZ);
//            System.out.println("Moving North");
//            System.out.println("Limit North: " + limitNorth);
            if (posZ - radius > limitNorth) {
                pos.z = posZ;
            } else if (maze.openNorth(posX, posZ)) {
                if (pos.x - radius > maze.cellLimitWest(posX) && pos.x + radius < maze.cellLimitEast(posX)) {
                    pos.z = posZ;
                }
            }
        } else { // Moving South
            float limitSouth = maze.cellLimitSouth(posZ);
//            System.out.println("Moving South");
//            System.out.println("Limit South: " + limitSouth);
            if(posZ + radius < limitSouth){
                pos.z = posZ;
            } else if(maze.openSouth(posX, posZ)){
                if(pos.x - radius > maze.cellLimitWest(posX) && pos.x + radius < maze.cellLimitEast(posX)){
                    pos.z = posZ;
                }
            }
        }
        if((pos.x - posX) < 0.0f){ // Moving East
            float limitEast = maze.cellLimitEast(posX);
//            System.out.println("Moving East");
//            System.out.println("Limit East: " + limitEast);
            if(posX + radius < limitEast){
                pos.x = posX;
            } else if(maze.openEast(posX, posZ)){
                if(pos.z + radius < maze.cellLimitSouth(posZ) && pos.z - radius > maze.cellLimitNorth(posZ)){
                    pos.x = posX;
                }
            }
        } else { // Moving West
            float limitWest = maze.cellLimitWest(posX);
//            System.out.println("Moving West");
//            System.out.println("Limit West: " + limitWest);
            if(posX - radius > limitWest){
                pos.x = posX;
            } else if(maze.openWest(posX, posZ)){
                if(pos.z + radius < maze.cellLimitSouth(posZ) && pos.z - radius > maze.cellLimitNorth(posZ)){
                    pos.x = posX;
                }
            }
        }
    }
}
