package edu.technopolis.homework.messenger.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatHistoryResult extends Message{

    private List<TextMessage> messages = new ArrayList<>();

    public List<TextMessage> getList() {
        return messages;
    }
    public void setList(List<TextMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), messages);
    }
}