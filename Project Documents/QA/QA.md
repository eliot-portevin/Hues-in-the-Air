# Software Quality concept 
## JUnit tests
We are going to use JUnit tests to test our project. These tests are going to be set up in a way, so that they (if possible) test every possible input and check if the output is the expected output. <br>
The goal of testing the project is, to assure that we can make constant progress, without encountering bugs of gliches where the origin is unknown and therefore the team would waste a lot of time chasing unnecessary errors. <br>

## Logger
Our game should implement a logger, to allow for way better debugging. For example without a logger, if we get an error while sending a message from client to server, we would have to use print statements in numerous places to find the exact line of code, where it went wrong.
On the other hand if we had a logger implemented, we would just need to look at what actions did run through and from that we can see where exactly the program broke
Our logger should log every package sent from server to client and vice versa, it should log every time a user inputs anything and it should log the game in a way which makes a perfect simulation of the exact run possible.

## Java Doc
In the project we are going to use Java-Doc to document and comment our code. The comments should be made in a way that makes anybody understand what the code is used for and what the input should be and what the output is going to be.
In complex functions, there need to be subcomments, which guide the user through the code, assuring there is no unnecessary confusion.
## Coding style

