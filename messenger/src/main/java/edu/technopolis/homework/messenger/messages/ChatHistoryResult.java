package edu.technopolis.homework.messenger.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatHistoryResult extends Message {
    private List<TextMessage> messages;

    public ChatHistoryResult(long id, long senderId, List<TextMessage> messages) {
        super(id, senderId, Type.MSG_CHAT_HIST_RESULT);
        this.messages = messages;
    }

    public List<TextMessage> getList() {
        return messages;
    }

    public void setList(List<TextMessage> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof ChatHistoryResult))
            return false;
        if (!super.equals(other))
            return false;
        ChatHistoryResult message = (ChatHistoryResult) other;
        return Objects.equals(messages, message.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), messages);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ChatHistoryResult{");
        stringBuilder.append(super.toString());
        stringBuilder.append(", Messages{");
        for (Message message : messages) {
            stringBuilder.append(message.getId());
            stringBuilder.append(", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}