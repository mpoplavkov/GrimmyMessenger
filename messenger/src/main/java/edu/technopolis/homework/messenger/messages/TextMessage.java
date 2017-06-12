package edu.technopolis.homework.messenger.messages;

import edu.technopolis.homework.messenger.net.CommandException;
import edu.technopolis.homework.messenger.net.GrimmyServer;
import edu.technopolis.homework.messenger.net.Session;

import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class TextMessage extends Message {
    private String text;

    public TextMessage() {}

    public TextMessage(Long id, Long senderId, String text) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.type = Type.MSG_TEXT;
    }

    public TextMessage(Long senderId, String text) {
        this.senderId = senderId;
        this.text = text;
        this.type = Type.MSG_TEXT;
    }

    @Override
    public void execute(Session session, GrimmyServer server) throws CommandException {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        TextMessage message = (TextMessage) other;
        return Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "text='" + text + '\'' +
                '}';
    }
}