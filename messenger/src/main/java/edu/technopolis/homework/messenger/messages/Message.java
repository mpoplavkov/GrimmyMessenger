package edu.technopolis.homework.messenger.messages;

import edu.technopolis.homework.messenger.net.Command;
import edu.technopolis.homework.messenger.net.CommandException;
import edu.technopolis.homework.messenger.net.Session;

import java.io.Serializable;

/**
 *
 */
public abstract class Message implements Serializable, Command {

    protected Long id;
    protected Long senderId;
    protected Type type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
