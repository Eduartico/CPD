package src;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UserManager {
    private static final String USER_FILE_PATH = "users.txt";
    private static final String SCORE_DELIMITER = ",";

    private Map<String, User> users;
    private Lock fileLock;

    public UserManager() {
        users = new HashMap<>();
        fileLock = new ReentrantLock();
    }

    public void loadUserScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(SCORE_DELIMITER);
                if (tokens.length == 5) {
                    String username = tokens[0];
                    String password = tokens[1];
                    int score = Integer.parseInt(tokens[2]);
                    String token = tokens[3];
                    boolean authenticated = Boolean.parseBoolean(tokens[4]);
                    User user = new User(username, password, score, token, authenticated);
                    users.put(username, user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUserScores(User user) {
        fileLock.lock();
        try {
            updateScoreInFile(user);
        } finally {
            fileLock.unlock();
        }
    }

    private void updateScoreInFile(User user) {
        try {
            File inputFile = new File(USER_FILE_PATH);
            File tempFile = new File(USER_FILE_PATH + ".tmp");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean userUpdated = false;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(SCORE_DELIMITER);
                if (tokens.length == 5) {
                    String currUsername = tokens[0];

                    if (currUsername.equals(user.getUsername())) {
                        // Update score for the specified user
                        writer.println(user.getUsername() + SCORE_DELIMITER + user.getPassword() +
                                SCORE_DELIMITER + user.getScore());
                        userUpdated = true;
                    } else {
                        // Write the existing line as it is
                        writer.println(line);
                    }
                }
            }

            reader.close();
            writer.close();

            // Replace the original file with the updated file
            if (inputFile.delete()) {
                if (!tempFile.renameTo(inputFile)) {
                    throw new IOException("Failed to rename temporary file to the original file.");
                }
            } else {
                throw new IOException("Failed to update score in the file.");
            }

            if (!userUpdated) {
                // If the user's line was not found in the file, add it as a new line
                try (PrintWriter appendWriter = new PrintWriter(new FileWriter(inputFile, true))) {
                    appendWriter.println(user.getUsername() + SCORE_DELIMITER + user.getPassword() +
                            SCORE_DELIMITER + user.getScore());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
