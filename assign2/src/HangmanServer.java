package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HangmanServer {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS = 10;
    private static final int WORDS = 2;
    private final List<String> gameWords;
    private UserRepo userRepository;


    private List<HangmanServerThread> players;
    private static List<String> words;
    private List<List<Character>> gameWordPrints;
    private Queue<HangmanServerThread> connectedPlayers;
    private int number_games;


    public HangmanServer() throws IOException {
        
        userRepository = new UserRepo();
        loadUsersFromFile("assign2/doc/users.txt");
        players = new ArrayList<>();
        words = new ArrayList<>();
        gameWords = new ArrayList<>();
        connectedPlayers = new LinkedList<HangmanServerThread>();
    }

    public void loadWordsFromFile(String filename) {
        try {
            words = Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUsersFromFile(String filename) throws IOException {
        userRepository = new UserRepo();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();

        while(line != null){
            String[] separatedWords = line.split(",");
            //System.out.println(separatedWords[0] + " " + separatedWords[1] + " " + separatedWords[2]);
            userRepository.addUser(new User(separatedWords[0], separatedWords[1], Integer.parseInt(separatedWords[2]) ,"0", false));
            line = reader.readLine();
        }

    }

    public synchronized void removePlayer(HangmanServerThread player) {
        players.remove(player);
        System.out.println("Client disconnected: " + player.getClientSocket().getInetAddress());
    }

    public synchronized List<HangmanServerThread> getPlayers() {
        return players;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {

                Socket clientSocket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                //user authentication
                Integer isAuthenticated = 1;
                String username = "";
                String password;
                Boolean isToken = false;
                User tokenUser = new User("", "", 0, "", false);

                while(isAuthenticated != 0){
                    writer.println("username: ");
                    username = reader.readLine();
                    //check if it is a valid token
                    for(int i = 0; i < players.size() ; i++) {
                        String token = players.get(i).getUser().getToken();
                        if (username.equals(token)) {
                            System.out.println("entrou");
                            isToken = true;
                            isAuthenticated = 0;
                            tokenUser = players.get(i).getUser();
                            break;
                        }
                    }
                    if(!isToken){
                        writer.println("password: ");
                        password = reader.readLine();
                        isAuthenticated = userRepository.isValidUser(username, password);

                        if(isAuthenticated == 1)
                            writer.print("user already authenticated\n");
                        else if(isAuthenticated == 2)
                            writer.print("credentials do not match\n");
                    }
                }

                if(!isToken){
                    User user = userRepository.getUser(username);
                    String token = UUID.randomUUID().toString();
                    user.setToken(token);

                    System.out.println(token);

                    //send confirmation to client and the token
                    //writer.println(isAuthenticated);
                    //writer.print(token);

                    HangmanServerThread clientThread = new HangmanServerThread(clientSocket, user, this);
                    players.add(clientThread);
                    connectedPlayers.add(clientThread);
                    user.setPosQueue(connectedPlayers.size()-1);
                }
                else{
                    //ver em que posição estava e ajeitar a fila para ficar na posição certa
                    //verificar se tem que criar uma nova thread com os mesmos dados do user

                    for(int j = 0; j < tokenUser.getPosQueue(); j++){
                        HangmanServerThread tempUserThread = connectedPlayers.poll();
                        connectedPlayers.add(tempUserThread);
                    }

                    for (HangmanServerThread element : connectedPlayers) {
                        System.out.println(element.getUser().getUsername() + ",");
                    }

                    connectedPlayers.poll();
                    HangmanServerThread clientThread = new HangmanServerThread(clientSocket, tokenUser, this);
                    connectedPlayers.add(clientThread);

                    for(int i = tokenUser.getPosQueue()+1; i < connectedPlayers.size(); i++){
                        HangmanServerThread tempUserThread = connectedPlayers.poll();
                        connectedPlayers.add(tempUserThread);
                    }
                }

                System.out.println("Queue size: " + connectedPlayers.size());
                for (HangmanServerThread element : connectedPlayers) {
                    System.out.println(element.getUser().getUsername());
                }
                System.out.println("Client connected: " + clientSocket.getInetAddress());


                //initGame();
                if(connectedPlayers.size() % 2 == 0){
                    initGame();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        HangmanServer server = new HangmanServer();
        server.loadWordsFromFile("assign2/src/words.txt");
        server.start();
    }

    private void initGame(){
        HangmanServerThread player1 = connectedPlayers.poll();
        HangmanServerThread player2 = connectedPlayers.poll();
        //connectedPlayers.add(player1);
        //connectedPlayers.add(player2);

        List<String> gameWords = getRandomWords(WORDS);

        System.out.println(gameWords);
        player1.set_gameWords(gameWords);
        player2.set_gameWords(gameWords);

        player1.set_turn(true);
        player2.set_turn(false);

        player1.set_oponent(player2);
        player2.set_oponent(player1);

        player1.start();
        player2.start();

    }

    private synchronized List<String> getRandomWords(int count) {
        List<String> randomWords = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(words.size());
            randomWords.add(words.get(index));
        }
        return randomWords;
    }
}