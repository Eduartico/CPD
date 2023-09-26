import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
 
/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class SumServer {
 
    public static void main(String[] args) {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println("Server is listening on port " + port);

            ConcurrentHashMap<Socket, AtomicLong> clientSums = new ConcurrentHashMap<>();
            AtomicLong globalSum = new AtomicLong(0);

            while(true) {
                Socket socket = serverSocket.accept();

                // Start a new thread to handle client requests
                Thread clientThread = new Thread(() -> {
                    try{
                        InputStream input = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                        OutputStream output = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true);

                        // Keep track of the local sum for this client
                        clientSums.put(socket, new AtomicLong(0));
                        
                        String inputLine;
                        while (true) {
                            inputLine = reader.readLine();
                            System.out.println("input: " + inputLine);
                            if(inputLine.equals("exit")){
                                System.out.println("server exit");
                                break;
                            }
                            try {
                                long number = Long.parseLong(inputLine.trim());

                                // Update the local sum for this client
                                long clientSum = clientSums.get(socket).addAndGet(number);

                                // Update the global sum with concurrency control
                                long currentGlobalSum = globalSum.addAndGet(number);

                                // Send the client the sum of all numbers it has supplied so far
                                writer.println(clientSum);

                            } catch (NumberFormatException e) {
                                writer.println("Invalid input: " + inputLine);
                            }
                        }

                        // Send the client the final sum for this session
                        long finalClientSum = clientSums.get(socket).get();
                        writer.println(finalClientSum);

                        // Update the global sum with concurrency control
                        long finalGlobalSum = globalSum.addAndGet(finalClientSum);

                        // Send the client the final global sum
                        writer.println(finalGlobalSum);

                        // Clean up by removing the client's local sum
                        clientSums.remove(socket);
                    } catch (IOException e) {
                        System.err.println("Error handling client: " + e);
                    }
                });

                clientThread.start();
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}