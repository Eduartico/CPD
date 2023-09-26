package src;

import java.util.HashMap;
import java.util.Map;

public class UserRepo {
    private Map<String, User> users;

    public UserRepo() {
        users = new HashMap<>();
    }

    public void addUser(User user){
        users.put(user.getUsername(), user);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getUser(String username){
        return users.get(username);
    }

    public Integer isValidUser(String username, String password){
        User user = getUser(username);
        if (user != null && user.getPassword().equals(password)){
            if (!user.getAuthenticated()){
                user.setAuthenticated(true);
                return 0;
            }
            return 1;
        }
        return 2;
    }
}
