package edu.technopolis.homework.messenger.messages;

import java.util.Objects;

public class LoginMessage extends Message {

    // залогиниться (если логин не указан, то авторизоваться).
    // В случае успеха приходит вся инфа о пользователе
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        if (!super.equals(other))
            return false;
        LoginMessage message = (LoginMessage) other;
        return Objects.equals(login, message.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), login, password);
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "login='" + login + '\'' +
                "password='" + password + '\'' +
                '}';
    }
}