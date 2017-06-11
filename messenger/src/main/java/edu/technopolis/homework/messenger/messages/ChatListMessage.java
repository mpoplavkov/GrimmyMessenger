package edu.technopolis.homework.messenger.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatListMessage extends Message{

    // получить список чатов пользователя
    // (только для залогиненных пользователей).
    // От сервера приходит список id чатов
    private List<Long> list = new ArrayList<>();

    public List<Long> getList() {
        return list;
    }
    public void setList(List<Long> list) {
        this.list = list;
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
        for (long elem : list)
            if (elem != message.getId())
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), list);
    }

    @Override
    public String toString() {
        String result = "ChatListMessage{ Id = ";
        for (long elem : list)
            result += elem + " ";
        result += " }";
        return result;
    }
}