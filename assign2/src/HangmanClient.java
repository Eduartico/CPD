package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HangmanClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String serverResponse;
            /*boolean isAuthenticated = Boolean.parseBoolean(in.readLine());
            String token = in.readLine();

            if(isAuthenticated){
                System.out.println("Successfully authenticated.");

                if(token != null && !token.isEmpty()){
                    //tratar de voltar à posição na stack
                    out.println(token);
                }

            }*/
            while ((serverResponse = in.readLine()) != null) {
                System.out.println( serverResponse);

                /*if (serverResponse.startsWith("Game word prints:")) {
                    String line;
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        System.out.println("line: " + line);
                    }
                }*/

                if (!in.ready()) {
                    String userInput = stdIn.readLine();
                    if (userInput != null) {
                        out.println(userInput);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
