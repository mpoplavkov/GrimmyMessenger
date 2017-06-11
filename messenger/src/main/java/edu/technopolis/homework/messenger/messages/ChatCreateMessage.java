package edu.technopolis.homework.messenger.messages;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatCreateMessage extends Message{

    // создать новый чат,
    // список пользователей приглашенных в чат
    // (только для залогиненных пользователей).
    private Set<Long> listOfInvited = new HashSet<>();

    public Set<Long> getListOfInvited() {
        return listOfInvited;
    }
    public void setListOfInvited(Set<Long> listOfInvited) {
        this.listOfInvited = listOfInvited;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), listOfInvited);
    }

    @Override
    public String toString() {
        String result = "ChatCreateMessage{ Users who was invited = ";
        for (long id : listOfInvited)
            result += id + " ";
        result += " }";
        return result;
    }
}