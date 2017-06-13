package edu.technopolis.homework.messenger.messages;

import java.util.Objects;

public class TextMessage extends Message {

    // отправить сообщение в заданный чат, чат должен быть в списке чатов пользователя
    // (только для залогиненных пользователей)
    private long chatId;
    private String text;

    public TextMessage(long id, long senderId, long chatId, String text) {
        super(id, senderId, Type.MSG_TEXT);
        this.chatId = chatId;
        this.text = text;
    }

    public TextMessage(long senderId, long chatId, String text) {
        super(senderId, Type.MSG_TEXT);
        this.chatId = chatId;
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

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof TextMessage))
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
                super.toString() + ", " +
                "chatId='" + chatId + "\', " +
                "text='" + text + '\'' +
                '}';
    }

    public static void main(String[] args) {
        String s = "aaa";
        Object o = new Object();
        System.out.println(s instanceof Object);
        System.out.println(s instanceof String);
        System.out.println(null instanceof String);
    }

/*    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeLong(chatId);
        byte[] bytes = text.getBytes();
        objectOutput.writeInt(bytes.length);
        objectOutput.write(bytes);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        chatId = objectInput.readLong();
        int n = objectInput.readInt();
        byte[] bytes = new byte[n];
        objectInput.read(bytes);
        text = new String(bytes);
    }*/
}