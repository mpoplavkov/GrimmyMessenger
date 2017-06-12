package edu.technopolis.homework.messenger.net;

/**
 * Исключение, которое бросается при ошибке в команде
 */

public class CommandException extends Exception{

    public CommandException(String msg) {
        super(msg);
    }

    public CommandException(Throwable ex) {
        super(ex);
    }

}
