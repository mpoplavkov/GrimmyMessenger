package edu.technopolis.homework.messenger.messages;

import edu.technopolis.homework.messenger.net.CommandException;
import edu.technopolis.homework.messenger.net.GrimmyServer;
import edu.technopolis.homework.messenger.net.Session;

/**
 * Сообщение - ответ от сервера после логирования
 */
public class MsgInfoResult extends Message {
    private String login;

    public MsgInfoResult(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public void execute(Session session, GrimmyServer server) throws CommandException {

    }
}
