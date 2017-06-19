package edu.technopolis.homework.messenger.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatListResult extends Message {
    private List<Long> chats;

    public ChatListResult(List<Long> chats) {
        super(0, Type.MSG_CHAT_LIST_RESULT);
        this.chats = chats;
    }

    public ChatListResult() {}

    public List<Long> getChats() {
        return chats;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof ChatListResult))
            return false;
        if (!super.equals(other))
            return false;
        ChatListResult result = (ChatListResult) other;
        return Objects.equals(chats, result.chats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chats);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ChatListResult{");
        stringBuilder.append(super.toString());
        stringBuilder.append(", Chats{");
        for (Long l : chats) {
            stringBuilder.append(l);
            stringBuilder.append(", ");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeInt(chats.size());
        for (int i = 0; i < chats.size(); i++) {
            objectOutput.writeLong(chats.get(i));
        }
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        int n = objectInput.readInt();
        chats = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            chats.add(objectInput.readLong());
        }
    }
}
