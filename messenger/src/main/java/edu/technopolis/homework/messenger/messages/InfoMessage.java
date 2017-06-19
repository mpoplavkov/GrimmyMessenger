package edu.technopolis.homework.messenger.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class InfoMessage extends Message {

    // получить всю информацию о пользователе,
    // без аргументов - о себе
    // (только для залогиненных пользователей)
    private long userId;

    public InfoMessage(long senderId, long userId) {
        super(senderId, Type.MSG_INFO);
        this.userId = userId;
    }

    public InfoMessage(long senderId) {
        this(senderId, senderId);
    }

    public InfoMessage() {}

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof InfoMessage))
            return false;
        if (!super.equals(other))
            return false;
        InfoMessage info = (InfoMessage) other;
        return Objects.equals(userId, info.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId);
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                super.toString() + ", " +
                "userId='" + userId + '\'' +
                '}';
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeLong(userId);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        userId = objectInput.readLong();
    }
}