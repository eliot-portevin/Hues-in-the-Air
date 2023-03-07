# Hues in the Air
##  Concept
The idea behind Hues in the Air is to create a multiplayer Geometry Dash-like 2D platformer with 
some unique physics. This is a concept which we discovered in the Nintendo Switch game [Super One More 
Jump](https://play.google.com/store/apps/details?id=com.smgstudio.sonemorejump&hl=en&gl=US&pli=1). 
The premise for the multiplayer is that each player can choose one of 2 to 5 colours, 
depending on how many players have joined in. Each player can control the block exactly when 
it is moving on a piece of their own colour. All 2-5 players control the same block: this makes for 
a great cooperative platformer in which players have to work together to complete a level.

##  Gameplay
The game is controlled by the players pressing the space bar. When a player does so, 
the cube jumps in a realistic and reproductible way. The said cube is always moving forward, 
so the player's task is to avoid obstacles by timing the jumps correctly. As the game progresses,
the levels become increasingly complex in design and thus more difficult to complete. 
Players must achieve increasingly difficult jumps while predicting the directional changes 
of gravity (see [Gravity](#gravity)). If the need for more variety in gameplay arises, additional elements 
may be added to the game, such as moving platforms or an item that randomly changes all platform
colours in the level, forcing all players to relearn the entire level.

![Gameplay](outreach/screenshot.png)
*Example level*

Important, but not obvious, is that the character can only move on coloured blocks: while 
in Geometry Dash you die if you crash into a wall, gravity works a little differently in 
Hues in the Air; death is thus brought about exclusively by contact with a white block. 
If this is the case, the players have to start the level from scratch. This causes them 
to lose a life, which limits their chances of winning the game.

##  Gravity
The gravity in the game has a constant magnitude, however, it changes direction as the 
block moves. As can be seen in the image above, jumping on a wall will cause the block to 
keep moving, but the velocity component in the direction of the wall is set to 0. This allows 
for some interesting gameplay mechanics, such as the ability to jump around corners.

## Lives
When playing a game, the players have a limited number of lives. With this set of chances to win, 
they have to complete a certain number of levels. If they fail to do so, they lose the game. 
If they succeed, they achieve eternal glory. The number and difficulty of the said levels 
are determined by the game mode (easy, medium, hard).