package edu.technopolis.homework.messenger.messages;

import java.util.Objects;

public class InfoMessage extends Message{

    // получить всю информацию о пользователе,
    // без аргументов - о себе
    // (только для залогиненных пользователей)
    private long userId;

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
        if (other == null || getClass() != other.getClass())
            return false;
        if (!super.equals(other))
            return false;
        InfoMessage info = (InfoMessage) other;
        return Objects.equals(info, info.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId);
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "userId='" + userId + '\'' +
                '}';
    }
}