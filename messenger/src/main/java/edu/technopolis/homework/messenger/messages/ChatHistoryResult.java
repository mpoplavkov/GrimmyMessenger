package edu.technopolis.homework.messenger.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ChatHistoryResult extends Message {
    private List<TextMessage> messages;

    public ChatHistoryResult(List<TextMessage> messages) {
        super(0, Type.MSG_CHAT_HIST_RESULT);
        this.messages = messages;
    }

    public ChatHistoryResult() {}

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

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeInt(messages.size());
        for (TextMessage tm : messages) {
            tm.writeExternal(objectOutput);
        }
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        int n = objectInput.readInt();
        messages = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            TextMessage textMessage = new TextMessage();
            textMessage.readExternal(objectInput);
            messages.add(textMessage);
        }
    }
}