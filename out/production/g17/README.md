# Hangman Game Instructions

Welcome to the Hangman game! In this game, players take turns guessing letters to reveal hidden words.

## Setup and Running the Game

1. Open the assign2/src directory on a terminal.

2. Compile the `HangmanServer.java` file by running the following command:
   javac HangmanServer.java

3. Compile the `HangmanClient.java` file by running the following command:
   javac HangmanClient.java

4. Start the server by running the following command:
   java HangmanServer

5. Start a client by running the following command:
   java HangmanClient

6. Once enough players (in this version, 2 players) are connected, the game will start.

## Gameplay

- Each player takes turns guessing a letter to uncover the hidden word.

- If a player guesses a letter correctly, they earn 10 points, but if it is incorrect, they lose 5 points.

- If a player guesses the entire word correctly, they earn 100 points, but if it is incorrect, they lose 50 points.

- The game continues until all the words have been guessed.

- Players can only guess one letter at a time.

- You can modify the code to add more words or customize the game further.

## Known Issues

- Sometimes players might need to press "Enter" to get their screen refreshed

