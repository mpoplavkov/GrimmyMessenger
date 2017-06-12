package edu.technopolis.homework.messenger.messages;

import java.util.Objects;

public class ChatHistoryMessage extends Message{

    // список сообщений из указанного чата
    // (только для залогиненных пользователей)
    private long chatId;

    public ChatHistoryMessage(long id, long senderId, long chatId) {
        super(id, senderId, Type.MSG_CHAT_HIST);
        this.chatId = chatId;
    }

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
}