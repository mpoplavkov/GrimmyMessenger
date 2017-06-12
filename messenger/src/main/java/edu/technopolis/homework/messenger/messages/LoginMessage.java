package edu.technopolis.homework.messenger.messages;

import edu.technopolis.homework.messenger.User;
import edu.technopolis.homework.messenger.net.CommandException;
import edu.technopolis.homework.messenger.net.GrimmyServer;
import edu.technopolis.homework.messenger.net.Session;

import java.util.Objects;

public class LoginMessage extends Message {

    // залогиниться (если логин не указан, то авторизоваться).
    // В случае успеха приходит вся инфа о пользователе
    private String login;
    private int password;

    public LoginMessage() {}

    public LoginMessage(String login, int password) {
        this.login = login;
        this.password = password;
        this.type = Type.MSG_LOGIN;
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
    public void execute(Session session, GrimmyServer server) throws CommandException {
        //Нужно залогинить пользователя

        //Проверяем, авторизировался ли такой пользователь
        boolean isUserExists = false;
        for(User user : server.getUsers()) {
            if(user.getLogin().equals(login)) {
                isUserExists = true;
                break;
            }
        }

        if(isUserExists) {
            server.getLoggedInUsers().add(session.getUser());
        }

        //пользователь успешно залогирован, теперь нужно отправить на клиент подтверждение:
        //MSG_INFO_RESULT






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
