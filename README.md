# 3D Maze - aMazeBalls - Assignment 3-4

**Computer Graphics - Fall 2017**.

## Build and run

`gradle run`

1. Start off by importing the gradle project into your IDE.
1. Set the `Working directory` in your build/run configurations to the `core/assets/` folder.
1. Build & Run

## Controls

- `WASD` Movement.
- `← → ↑ ↓` Look around.
- `SHIFT` Move faster, this makes the evil snowman move faster as well.
- `Mouse` The mouse can also be used to look around.
- `V` God Mode, makes it possible to go through walls, fly around based on directional/looking vector and enables other buttons such as:
  - `R` Move down.
  - `F` Move up.

## Gameplay

The goal of the game is collect all the token/balls in the maze, when that is completed you levelup into a new larger maze.
Each maze is randomly generated.
Watch out for the evil snowman, if he gets you, you lose. (It's possible to pass him if you keep tight up to a wall).

## Known bugs

Due to some update lag it is eventually possible to get through walls if you're patient enough and keep colliding into them.

## Cool features that we spent time on

### Random maze generation

We found and used a [random maze generator](https://rosettacode.org/wiki/Maze_generation#Java) from Rosetta code. The generator uses integer values to describe each cell where bitmask operations are used to grab the info. A cell with the value `12` or `1100` in binary `(NSEW)` has the North and South edge marked as open `1` but East and West as closed `0`.

### Wall collision

Each cell has border-limits on the X and Z axis in world space, the player is not allow to exceed these borders unless the cell is open in that direction. If the cell is open we also check if the player is within the perpendicular axis. This means that if a player tries to go south in a cell which is open to the south, he also has to be within the east-west borders of the cell.

### Texture effects

The walls have either a brick wall or wooden wall look, we spent a good time to randomly generate this look. The brick-wall consists of 72 boxes (bricks) + 1 solid wall box within.

### Snowman AI

The Evil snowman spawns randomly on a diagonal spot in the maze and travels to adjacent cells never looking back (turning around) unless hitting a dead end. At any cell if he has an option to not only move forward, his choice will be random.
When the player collides with the snowman he looses the game.

### Snowman looking at the player

We display the eyes and nose on the snowman based on the directional vector between the player and the snowman so that the snowman is always looking at the player.

### Mouse look-around

Being able to look around with the mouse without affecting the movement of the player inside the maze.

### Other

- 2D camera.
- Following the player in the 2D camera but also snapping the 2D view to the edges of the maze.
- Score indicator bar as you collect the tokens/balls.
- Fix Z-fighting object jitter, avoided by creating the pillars between all the walls. Makes for nice aesthetics too.

## Assignment 4 - Shaders and Lighting

### Description of the lighting model

We've spent a good amount of time tweaking our shader class and the vertex shader for some specular higlighting. We never got a satisfying look so we've just fallen back to our original basic diffuse light. We'd love to spend some more time on this, but our brains are fried and learning from 4 hour youtube videos is a terrible medium for tired minds.

So our model has only 1 light source, a light that hovers over the player and illuminates the scene. It does look pretty good though :)

### The entire code of our shaders

We've removed the variable declerations in the shaders for less text, this is the difference between the 3 types of variables that we declear.

- `attribute` variables is the vertex list sent each time into main as the shaders gets executed.
- `uniform` variables are only used within the shader. These are the common variables that we've used and have handles to in our Java program.
- `varying` variables brought along the entire OpenGl pipeline, can send along as many as we want. v_color used in rasterization and therefor found again in the fragment shader.

### Vertex shader - Simple3D.vert
```c++
void main()
{
    vec4 position = vec4(a_position.x, a_position.y, a_position.z, 1.0);
    position = u_modelMatrix * position; // The position of the object

    vec4 normal = vec4(a_normal.x, a_normal.y, a_normal.z, 0.0);
    normal = u_modelMatrix * normal; // The normal vector of the object

    v_n = normal;
    v_s = u_lightPosition - position; // Vector pointing to the light

    position = u_viewMatrix * position; // The final position in the game

    gl_Position = u_projectionMatrix * position; // Setting the position
}
```

Some specular calculations that we ended up not using.

```c++
    // For use with specular color calculations, we calculated v
    // which is the vector from surface vertex to eye.
    vec4 v = u_eyePosition - position; // Vector pointing to the camera
    // v_h is the vector addition of the vectors 'source to light'
    // and 'source to eye' to use for specular highlight.
    v_h = v_s + v;
```

### Fragment shader - Simple3D.frag
```c++
void main()
{
    // Lambert calculates the strength of the color based on the corner
    // between the vertex's normal and vector from the vertex to the light source.
    // This is a value between 0.0 and 1.0
    float lambert = dot(v_n, v_s) / (length(v_n) * length(v_s)); // How light hits the objects

    // Value as a strength unit multiplied to the color.
    // Diffuse is a value independent from the position of the looking eye.
    vec4 color = (lambert * u_lightDiffuse * u_materialDiffuse); // The final color of the object

    gl_FragColor = color; // Setting the color
}
```

Specular calculations in the fragment shader.

```c++
    // Like lambert, phong is the intensity value based on v_h and the viewers eye.
    // It is most intense in the vertex that would reflect the light source in the surface.
    float phong = dot(v_n, v_h) / (length(v_n) * length(v_h));
    vec4 color = lambert * u_lightDiffuse * u_materialDiffuse;
    // As material shininess is increased the specular highlight becomes
    // smaller as the strength diminishes faster.
    // Phong is 1 where it's the strongest but fades to zero 'fast'
    // as when < 1 values are put to any power they become smaller.
    color += pow(phong, u_materialShininess) * u_lightDiffuse * vec4(1.0f,1.0f,1.0f,1.0f);
```

## Authors

- [Birkir Brynjarsson](https://github.com/birkirbrynjarsson/)
- [Unnur Sól Ingimarsdóttir](https://github.com/unnursol/)
