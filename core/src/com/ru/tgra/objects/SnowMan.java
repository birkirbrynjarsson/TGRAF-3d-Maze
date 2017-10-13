package com.ru.tgra.objects;

import com.badlogic.gdx.graphics.Color;
import com.ru.tgra.models.Point3D;
import com.ru.tgra.models.Vector3D;
import com.ru.tgra.models.ModelMatrix;
import com.ru.tgra.objects.maze.Maze;
import com.ru.tgra.materials.Shader;
import com.ru.tgra.shapes.SphereGraphic;

import java.nio.FloatBuffer;
import java.util.Random;

public class SnowMan
{

    public Point3D pos;
    Vector3D u;
    Vector3D v;
    Vector3D n;

    private ModelMatrix mm;
    Shader shader;
    public float size;
    boolean moving;
    Point3D destination;
    Random rand;
    Color noseColor = new Color(0x6D0000FF);
    Vector3D up = new Vector3D(0, 1f, 0);

    int direction; // 0 north, 1 south, 2 east, 3 west

    private FloatBuffer matrixBuffer;

    public SnowMan(float x, float z, ModelMatrix mm, Shader shader)
    {
        this.mm = mm;
        this.shader = shader;
        this.size = 1f;
        moving = false;
        rand = new Random();
        moving = false;

        pos = new Point3D(x, 1.1f, z);
        u = new Vector3D(1,0,0);
        v = new Vector3D(0,1,0);
        n = new Vector3D(0,0,1);

        this.destination = pos;
    }

    public void initDirection(Maze maze){
        int i = rand.nextInt(4);
        while(!getOpen(i, maze)){
            i = (i + 1)%4;
        }
        direction = i;
        destination = destination(i, maze);
        moving = true;
    }
    
    public void display(Point3D playerPos){

        // Vectors for eye and nose direction
        Vector3D lookDirection = Vector3D.difference(playerPos, pos);
        u = up.cross(lookDirection);
        u.normalize();
        u.scale(lookDirection.length()*0.3f);
        lookDirection.normalize();
        Point3D leftEyeLook = new Point3D(playerPos.x + u.x, playerPos.y + u.y, playerPos.z + u.z);
        Point3D rightEyeLook = new Point3D(playerPos.x - u.x, playerPos.y - u.y, playerPos.z - u.z);
        Vector3D lEyeLook = Vector3D.difference(leftEyeLook, pos);
        Vector3D rEyeLook = Vector3D.difference(rightEyeLook, pos);
        lEyeLook.normalize();
        rEyeLook.normalize();

        // Bottom ball
        shader.setMaterialDiffuse(1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size, size, size);
        mm.addTranslationBaseCoords(pos.x,pos.y,pos.z);
        shader.setModelMatrix(mm.getMatrix());
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();

        // 2nd Ball
        shader.setMaterialDiffuse(1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size*0.7f, size*0.7f, size*0.7f);
        mm.addTranslationBaseCoords(pos.x,pos.y + 1.3f,pos.z);
        shader.setModelMatrix(mm.getMatrix());
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();

        // Face ball
        shader.setMaterialDiffuse(1f, 1f, 1f, 1f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size*0.5f, size*0.5f, size*0.5f);
        mm.addTranslationBaseCoords(pos.x,pos.y + 2.2f,pos.z);
        shader.setModelMatrix(mm.getMatrix());
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();

        // Nose
        shader.setMaterialDiffuse(noseColor.r, noseColor.g, noseColor.b, noseColor.a);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size*0.2f, size*0.2f, size*0.2f);
        mm.addTranslationBaseCoords(pos.x + (size*0.6f * lookDirection.x),pos.y + 2.1f,pos.z + (size*0.6f * lookDirection.z));
        shader.setModelMatrix(mm.getMatrix());
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();


        // Eye Left
        shader.setMaterialDiffuse(0f, 0f, 0f, 0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size*0.1f, size*0.1f, size*0.1f);
        mm.addTranslationBaseCoords(pos.x + (size*0.6f * lEyeLook.x),pos.y + 2.3f,pos.z + (size*0.6f * lEyeLook.z));
        shader.setModelMatrix(mm.getMatrix());
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();

        // Eye Right
        shader.setMaterialDiffuse(0f, 0f, 0f, 0f);
        mm.loadIdentityMatrix();
        mm.pushMatrix();
        mm.addScale(size*0.1f, size*0.1f, size*0.1f);
        mm.addTranslationBaseCoords(pos.x + (size*0.6f * rEyeLook.x),pos.y + 2.3f,pos.z + (size*0.6f * rEyeLook.z));
        shader.setModelMatrix(mm.getMatrix());
        SphereGraphic.drawSolidSphere();
        mm.popMatrix();
    }

    private boolean getOpen(int dir, Maze maze){
        if(dir == 0){
            return maze.openNorth(pos.x, pos.z);
        } else if(dir == 1){
            return maze.openSouth(pos.x, pos.z);
        } else if(dir == 2){
            return maze.openEast(pos.x, pos.z);
        } else if(dir == 3){
            return maze.openWest(pos.x, pos.z);
        }
        return false;
    }

    Point3D destination(int dir, Maze maze){
        if(dir == 0){
            return new Point3D(pos.x, pos.y, maze.getNorthZ(pos.z));
        } else if(dir == 1){
            return new Point3D(pos.x, pos.y, maze.getSouthZ(pos.z));
        } else if(dir == 2){
            return new Point3D(maze.getEastX(pos.x), pos.y, pos.z);
        } else if(dir == 3){
            return new Point3D(maze.getWestX(pos.x), pos.y, pos.z);
        }
        return pos;
    }

    public void newDirection(int illegalDirection, Maze maze){
        int i = rand.nextInt(4);
        while(i == illegalDirection || !getOpen(i, maze)){
            i = (i + 1)%4;
        }
        direction = i;
        moving = true;
        destination = destination(i, maze);
    }

    public void move(Maze maze, Point3D playerPos, float speed){
        if(!moving){
            int cell = maze.getCellValue(pos.x, pos.z);
            if(direction == 0){ // North
                // Check if closed in all directions
                if(cell == 4){ // North Dead must turn around
                    direction = 1; // South
                    moving = true;
                    destination.z = maze.getSouthZ(pos.z);
                } else {
                    newDirection(1, maze);
                }
            } else if(direction == 1){ // Going South
                if(cell == 8){
                    direction = 0; // North
                    moving = true;
                    destination.z = maze.getNorthZ(pos.z);
                } else {
                    newDirection(0, maze);
                }
            } else if(direction == 2){ // East
                if(cell == 1){
                    direction = 3;
                    moving = true;
                    destination.x = maze.getWestX(pos.x);
                } else {
                    newDirection(3, maze);
                }
            } else if(direction == 3){ // West
                if(cell == 2){
                    direction = 2;
                    moving = true;
                    destination.x = maze.getEastX(pos.x);
                } else {
                    newDirection(2, maze);
                }
            }
        } else {
            if(direction == 0) { // North
                if (pos.z < destination.z) {
                    moving = false;
                } else {
                    pos.z -= speed;
                }
            } else if(direction == 1) { // South
                if(pos.z > destination.z) {
                    moving = false;
                } else {
                    pos.z += speed;
                }
            } else if(direction == 2) { // East
                if(pos.x > destination.x){
                    moving = false;
                } else {
                    pos.x += speed;
                }
            } else if(direction == 3) { // West
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
