package edu.technopolis.homework.messenger;

/**
 *
 */
public class User {
    private long id;
    private String login;
    private String about;

    public User(long id, String login, String about) {
        this.id = id;
        this.login = login;
        this.about = about;
    }

    public User(long id, String login) {
        this(id, login, null);
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public String toString() {
        return id == 0 ? "I'm not in DB" : id + ": " + login;
    }
}
