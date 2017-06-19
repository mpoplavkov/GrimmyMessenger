package edu.technopolis.homework.messenger.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatCreateMessage extends Message {
    private Set<Long> listOfInvited;
    private String name;

    // создать новый чат,
    // список пользователей приглашенных в чат
    // (только для залогиненных пользователей).

    public ChatCreateMessage(long senderId, String name, Set<Long> listOfInvited) {
        super(senderId, Type.MSG_CHAT_CREATE);
        this.listOfInvited = listOfInvited;
        this.name = name;
    }

    public ChatCreateMessage(long senderId, Set<Long> listOfInvited) {
        this(senderId, null, listOfInvited);
    }

    public ChatCreateMessage() {}


    public Set<Long> getListOfInvited() {
        return listOfInvited;
    }

    public void setListOfInvited(Set<Long> listOfInvited) {
        this.listOfInvited = listOfInvited;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof ChatCreateMessage))
            return false;
        if (!super.equals(other))
            return false;
        ChatCreateMessage message = (ChatCreateMessage) other;
        return Objects.equals(listOfInvited, message.listOfInvited);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), listOfInvited);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("ChatCreateMessage{");
        result.append(super.toString());
        result.append(", name=");
        result.append(name);
        result.append(", InvitedUsers{");
        for (long id : listOfInvited) {
            result.append(id);
            result.append(", ");
        }
        result.delete(result.length() - 2, result.length());
        result.append("}");
        return result.toString();
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeInt(listOfInvited.size());
        for (Long l : listOfInvited) {
            objectOutput.writeLong(l);
        }
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        int n = objectInput.readInt();
        Set<Long> set = new HashSet<>(n);
        for (int i = 0; i < n; i++) {
            set.add(objectInput.readLong());
        }
        listOfInvited = set;
        name = objectInput.readLine();
    }
}