package src;

public class User {
    private final String username;
    private final String password;
    private Integer score;
    private String token;
    private Boolean authenticated;
    private Integer posQueue;

    public User(String username, String password, Integer score, String token, Boolean authenticated){
        this.username = username;
        this.password = password;
        this.score = score;
        this.token = token;
        this.authenticated = authenticated;
        this.posQueue = -1;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getScore() { return score; }

    public String getToken() {
        return token;
    }

    public Boolean getAuthenticated() { return authenticated; }

    public Integer getPosQueue() { return posQueue;}

    public void setScore(int score) { this.score = score; }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAuthenticated(Boolean authenticated) { this.authenticated = authenticated; }

    public void setPosQueue(Integer newPosQueue) {this.posQueue = newPosQueue;}
}
