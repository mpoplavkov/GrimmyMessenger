package edu.technopolis.homework.messenger.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatHistoryMessage extends Message{

    // список сообщений из указанного чата
    // (только для залогиненных пользователей)
    private long chatId;

    public ChatHistoryMessage(long senderId, long chatId) {
        super(senderId, Type.MSG_CHAT_HIST);
        this.chatId = chatId;
    }

    public ChatHistoryMessage() {}

    public long getChatId() {
        return chatId;
    }
    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatId);
    }

    @Override
    public String toString() {
        return "ChatHistoryMessage{" +
                super.toString() + ", " +
                "chatId='" + chatId + '\'' +
                '}';
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeLong(chatId);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        chatId = objectInput.readLong();
    }
}