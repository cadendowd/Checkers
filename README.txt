Matthew Leone and Caden Dowd
Professor Ferguson
Artifical Intelligence
02/11/2020
netId: mleone10, cdowd4

This code was written in java and can be compiled with the command 'javac Checkers.java' and run with 'java Checkers' in the terminal. 

We implemented the Checkers game by creating a class called Checkers that has a 2d char array of a board, and has a linked list of possible next states.
Maximum jumps were implemented with a recursive method that would check the current max jumps, and if that number was smaller than the path we are on, gets replaced and 
updated.

For the purposes of our game, we limited the computer to playing as black and the human to playing as white. To play the game, simply run the code. We were only able
to get our heuristic to run quickly for a depth of 8. It is playable at about 9 depth, but the moves take a little bit longer.

Our heuristic is a simple one, which consists of maximizes kings, your own pieces, and minimizes the enemy's piece and kings. We run minimax by expanding states and getting their
value, and returning the best board assuming the opponent plays optimally. Then we take input for the players move, and run the loop again. The game ends when someone is in a terminal state, or when the turn limit is reached. The limit is currently at 60. The alpha beta pruning was implemented by keeping track of a global min and max, and pruning away branches that would never see actual play. 

Movement and captures occur according to project specification, and entering an invalid move will prompt you to try a different one. At any point in time, enter quit to end the game. While there are some boundaries in place for entering valid moves, we decided that the amount of effort required to insure that every edge case of input was not the point of the project, and there is some expectation on the player to enter valid moves, and we are assuming the player knows how to play the game. For example, if the user is to put in an entirely invalid move, the game will call a null pointer exception and terminate the code. 

To move a piece enter coordinate-coordinate ex. 'a3-b4'
To capture a piece enter coordinatexcoordinite ex. 'a3xb4'
For multi captures enter coordinatexcoordinatexcoordinate ex. 'a3xb4xc5'