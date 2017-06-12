package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.Message;

/**
 * Created by greg on 11.06.17.
 */
public interface Command {

    /**
     * Реализация паттерна Команда. Метод execute() вызывает соответствующую реализацию,
     * для запуска команды нужна сессия, чтобы можно было сгенерить ответ клиенту и провести валидацию
     * сессии.
     * @param session - текущая сессия
     * @throws CommandException - все исключения перебрасываются как CommandException
     */
    void execute(Session session, GrimmyServer server) throws CommandException;
}
