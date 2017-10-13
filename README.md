# 3D Maze - aMazeBalls - Assignment 3-4
### Computer Graphics - Fall 2017
#### by Birkir Brynjarsson and Unnur Sól Ingimarsdóttir

## Build and run
1. Start off by importing the gradle project into your IDE.
2. Set the `Working directory` in your build/run configurations to the `core/assets/` folder.
3. Build & Run

## Controls
- `WASD` Movement
- `← → ↑ ↓` Look around
- `SHIFT` Move faster, this makes the evil snowman move faster as well
- `Mouse` The mouse can also be used to look around

## Gameplay
The goal of the game is collect all the token/balls in the maze, when that is completed you levelup into a new larger maze.
Each maze is randomly generated.
Watch out for the evil snowman, if he gets you, you lose. (It's possible to pass him if you're tight up to a wall)

## Known bugs
It is possible to get through walls eventually if you wait due to update lag.

## Cool features that we spent time on
### Random maze generation
We found and used a [random maze generator](https://rosettacode.org/wiki/Maze_generation#Java) from Rosetta code. The generator uses integer values to describe each cell where bitmask operations are used to grab the info. A cell with the value `12` or `1100` in binary `(NSEW)` has the North and South edge marked as open `1` but East and West as closed `0`
### Wall collision
Each cell has border-limits on the X and Z axis in world space, the player is not allow to exceed these borders unless the cell is open in that direction. If the cell is open we also check if the player is within the perpendicular axis. This means that if a player tries to go south in a cell which is open to the south, he also has to be within the east-west borders of the cell.
### Texture effects
The walls have either a brick wall or wooden wall look, we spent a good time to randomly generate this look. The brick-wall consists 72 boxes (bricks) + 1 solid wall box within.
### Snowman AI
The Evil snowman spawns randomly on a diagonal spot in the maze and travels to adjacent cells never looking back (turning around) unless hitting a dead end. At any cell if he has an option to turn right/left or go on his choice is random.
When the player collides with the snowman he looses the game.
### Snowman looking at the player
We display eyes and nose on the snowman based on the directional vector between the player and the snowman so that the snowman is always looking at the player.
### Mouse look-around
Being able to look around with the mouse without affecting the movement of the player inside the maze.
