package edu.technopolis.homework.messenger.messages;

import java.util.Objects;

public class TextMessage extends Message {

    // отправить сообщение в заданный чат, чат должен быть в списке чатов пользователя
    // (только для залогиненных пользователей)
    private long chatId;
    private String text;

    public TextMessage(long id, long sender_id, String text) {
        this.id = id;
        this.senderId = sender_id;
        this.text = text;
    }

    public String getText() {
        return text;
    }
    public long getChatId() {
        return chatId;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public TextMessage(){}

    public TextMessage(long chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null || getClass() != other.getClass())
            return false;
        if (!super.equals(other))
            return false;
        TextMessage message = (TextMessage) other;
        return Objects.equals(chatId, message.chatId) && Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatId, text);
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "chatId='" + chatId + '\'' +
                "text='" + text + '\'' +
                '}';
    }
}