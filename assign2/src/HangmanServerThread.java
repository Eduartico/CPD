package src;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;




public class HangmanServerThread extends Thread {
    private final Socket clientSocket;
    private final HangmanServer server;
    private PrintWriter out;
    private Scanner in;
    private  List<String> gameWords;
    private List<List<Character>> gameWordPrints;
    private final User user;
    private final UserManager userManager = new UserManager();
    private int score = 0;
    private int oponent_score = 0;
    private boolean connected;
    private int total_attempts;
    private boolean wainting_room;
    private HangmanServerThread oponent;
    private boolean turn;
    private boolean failed_search_player = false;
    private final int letter_correct = 10;
    private final int letter_wrong = -5;
    private final int word_correct = 100;
    private final int word_wrong = -50;
    private List <String> tried_letters;
    private List <String> tried_words;
    private  int count_letter_correct = 0;


    public HangmanServerThread(Socket clientSocket, User user, HangmanServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.user = user;
        this.wainting_room = false;
        this.gameWordPrints = new ArrayList<>();
        this.tried_letters = new ArrayList<>();
        this.tried_words = new ArrayList<>();
        score = 0;
        connected = true;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public synchronized void incrementScore(int add_score) {
        score += add_score ;
    }

    public synchronized int getScore() {
        return score;
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized User getUser(){return user;}

    public synchronized boolean iswainting() {
        return wainting_room;
    }

    public synchronized void set_oponent(HangmanServerThread oponent) {
        this.oponent = oponent;
    }

    public synchronized void set_turn(boolean turn) {
        this.turn = turn;
    }
    public void set_gameWords (List<String> gameWords) { this.gameWords= gameWords; }
    public void set_gameWordPrints (List<List<Character>> gameWordPrints) { this.gameWordPrints = gameWordPrints;}
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new Scanner( clientSocket.getInputStream() );
            //buffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            in.useDelimiter("\\R");

            init_gameWordPrints();

            out.println("Welcome to Hangman!\n");

            while(connected){
                game();
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void set_gameWordPrints() {
    }

    private void  game() throws IOException, InterruptedException {
        printMenu();
        String option_aux;
        int option = -1;
        while(true){

            option_aux = in.nextLine();
            //if(in.hasNextInt()){
            if(option_aux.equals("0") || option_aux.equals("1")  || option_aux.equals("2")){
                option = Integer.parseInt(option_aux);
                break;
            }
            else
                out.println("Is not an Option!");
                out.println("Option:");
        }

        out.println(option);
        switch (option){
            case 0:
                clientSocket.close();
            case 1:
                wainting_room = true;
                while (connected) {
                    waiting_room();
                    if(failed_search_player){
                        break;
                    }
                    playgame();
                }
                out.println("sai");
        }
    }
    private void playgame() throws  InterruptedException {
        if (gameWords.isEmpty()) {
            out.println("Congratulations! You found all the words!");
            user.setScore(score);
            userManager.saveUserScores(user);
            connected = false;
            oponent.connected = false;
            server.removePlayer(this);
            cleanup();
            oponent.cleanup();
        }
        if(!oponent.turn && turn){
            printGameWordPrints();
            out.println("My Score: " + score);
            out.println("Opponente Score: " + oponent.score);
            out.print("Tried Letters:");
            out.println(tried_letters);
            out.print("Tried Words:");
            out.println(tried_words);
            out.println("Input:");
            while(true){
                String input = in.next();
                if (input.length()>=1) {
                    out.println("Input -> " + input);
                    handleInput(input);
                    if(input.length()==1){
                        tried_letters.add(input);
                        oponent.tried_letters.add(input);
                    }
                    else {
                        tried_words.add(input);
                        oponent.tried_words.add(input);
                    }
                    break;
                }
                else {
                    out.println("Invalid Input!");
                    out.println("Input:");
                }
            }

            turn = false;
            oponent.set_turn(true);
        }
        else {
            out.println("Waiting for players turn...");
            while(!turn) {
                Thread.currentThread().sleep(500);
            }
            out.println("Your turn");
            in.nextLine();
        }
    }

    public void waiting_room () throws InterruptedException {
        int max_search_time_sec = 10;
        if(!oponent.iswainting()){
            out.println("Waiting for players...");
            for(int i = 1; i <= 2 * max_search_time_sec; i ++){
                if(oponent.iswainting()){
                    break;
                }
                Thread.currentThread().sleep(500);
                if(i == (2*max_search_time_sec)){
                    out.println("Failed Search Player");
                    failed_search_player = true;
                }
            }
        }
    }
    public void init_gameWordPrints () {
        for (String word : gameWords) {
            List<Character> wordPrint = new ArrayList<>();
            for (int i = 0; i < word.length(); i++) {
                wordPrint.add('-');
            }
            gameWordPrints.add(wordPrint);
        }
    }
    private void handleInput(String input) {

        if (input.length() == 1) {
            // Handle letter input
            char letter = input.charAt(0);

            for (int i = 0; i < gameWords.size(); i++) {
                String word = gameWords.get(i);
                List<Character> wordPrint = gameWordPrints.get(i);

                for (int j = 0; j < word.length(); j++) {
                    if (word.charAt(j) == letter && wordPrint.get(j) == '-') {
                        wordPrint.set(j, letter);
                        count_letter_correct ++;
                    }
                }

                if (!wordPrint.contains('-')) {
                    out.println("Word found -> " + gameWords.get(i));
                    gameWords.remove(i);
                    gameWordPrints.remove(i);
                    i--;
                }

            }

            if(count_letter_correct >= 1){
                incrementScore(count_letter_correct*letter_correct);
                out.println("Correct letters: " + count_letter_correct);
            }
            else{
                incrementScore(letter_wrong);
                out.println("Incorrect Letter!");
            }
            count_letter_correct = 0;

        } else {
            // Handle word input
            boolean wordFound = false;

            for (int i = 0; i < gameWords.size(); i++) {
                String word = gameWords.get(i);
                List<Character> wordPrint = gameWordPrints.get(i);

                if (word.equalsIgnoreCase(input)) {
                    wordPrint.clear();
                    for (int j = 0; j < word.length(); j++) {
                        wordPrint.add(word.charAt(j));
                    }
                    incrementScore(word_correct);
                    out.println("Word found -> " + gameWords.get(i));
                    gameWords.remove(i);
                    gameWordPrints.remove(i);
                    wordFound = true;
                    break;
                }
            }

            if (!wordFound) {
                incrementScore(word_wrong);
                out.println("Word not found!");
            }
        }

        total_attempts += 1;
        out.println("Total Attempts: " + total_attempts );
        oponent.set_gameWordPrints(gameWordPrints);
        if (!connected) {
            cleanup(); // Cleanup resources and remove the player
        }
    }



    private void printMenu(){
        out.println(
                """
                        |========================================================================|
                        |                  __  __                                                |
                        |                 / / / /___ _____  ____ _____ ___  ____ _____           |
                        |                / /_/ / __ `/ __ \\\\/ __ `/ __ `__ \\\\/ __ `/ __ \\\\       |
                        |               / __  / /_/ / / / / /_/ / / / / / / /_/ / / / /          |
                        |              /_/ /_/\\\\__,_/_/ /_/\\\\__, /_/ /_/ /_/\\\\__,_/_/ /_/        |
                        |                                  /____/                                |
                        |========================================================================|
                        |      Play Game                       [1]                               |
                        |      Rules                           [2]                               |
                        |      Exit                            [0]                               |
                        |========================================================================|
                        """);
        out.println("Option:");

    }

    private void printGameWordPrints() {
        out.print("Game word prints:\n");
        for (List<Character> wordPrint : gameWordPrints) {
            StringBuilder sb = new StringBuilder();
            for (char c : wordPrint) {
                sb.append(c).append(" ");
            }
            out.println(sb);
        }
        out.println();

        // Check if all words have been found
        /*if (gameWords.isEmpty()) {
            if(score > oponent_score){
                out.println("Congratulations! You Win");
            }
            else{
                out.println("You lose!\nTry again!");
            }
        }*/
    }


    private void cleanup() {
        try {

            if(score > oponent_score){
                out.println("Congratulations! You Win");
            }
            else if (score == oponent_score){
                out.println("It's a tie!");
            }
            else{
                out.println("You lose!\nTry again!");
            }

            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            userManager.saveUserScores(user);
            user.setAuthenticated(false);
            connected = false;
            oponent.connected = false;
            server.removePlayer(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
