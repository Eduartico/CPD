package src;

import java.net.*;
import java.io.*;
 
/**
 * This program demonstrates a simple TCP/IP socket client.
 *
 * @author www.codejava.net
 */
public class SumClient {
 
    public static void main(String[] args) {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
 
        try (Socket socket = new Socket(hostname, port)) {

            System.out.println("Connected to server " + hostname + " on port " + port);
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            long localSum = 0;

            while (true) {
                userInput = consoleReader.readLine();
                if(userInput.equals("exit")){
                    System.out.println("exitrrr");
                    writer.println(userInput);
                    break;
                }
                System.out.println(userInput);
                try {
                    long number = Long.parseLong(userInput.trim());

                    // Send the number to the server and read the sum of all numbers sent so far by this client
                    writer.println(number);
                    String serverResponse = reader.readLine();
                    long serverSum = Long.parseLong(serverResponse);

                    System.out.println("Local sum for this client: " + (localSum += number));
                    System.out.println("Sum of all numbers sent so far by this client: " + serverSum);

                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + userInput);
                }
            }
 
            System.out.println("done");

            // Read the final sum for this session
            String finalClientSumResponse = reader.readLine();
            long finalClientSum = Long.parseLong(finalClientSumResponse);

            System.out.println("Final sum for this client: " + finalClientSum);

            // Read the final global sum
            String finalGlobalSumResponse = reader.readLine();
            long finalGlobalSum = Long.parseLong(finalGlobalSumResponse);

            System.out.println("Final global sum for all clients: " + finalGlobalSum);
 
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}