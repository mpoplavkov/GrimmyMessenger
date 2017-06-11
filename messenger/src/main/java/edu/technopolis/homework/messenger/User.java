package edu.technopolis.homework.messenger;

/**
 *
 */
public class User {
    private long id;
    private String login;
    private int password;

    public User(long id, String login, int password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public User(String login, int password) {
        this.login = login;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public int getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return id == 0 ? "I'm not in DB" : id + ": " + login + " (" + password + ")";
    }
}
