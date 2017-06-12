package edu.technopolis.homework.messenger.messages;

import java.io.*;
import java.util.Objects;

/**
 *
 * Вопрос: если использовать Externalizable и методы writeExternal() и readExternal(),
 * то java сама запишет при сериализации класс объекта? Иначе непонятно, как потом
 * работает механизм восстановления.
 * Поскольку с механизмом Externalizable есть вопросы, здесь пока что будет Serializable.
 *
 */
public abstract class Message implements Serializable {

    private long id;
    private long senderId;
    private Type type;

    protected Message(long id, long senderId, Type type) {
        this.id = id;
        this.senderId = senderId;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public long getSenderId() {
        return senderId;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Message))
            return false;
        Message message = (Message) other;
        return Objects.equals(id, message.id) && Objects.equals(senderId, message.senderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, senderId);
    }

    @Override
    public String toString() {
        return "type = " + type + ", " +
                "id='" + id + "', " +
                "senderId='" + senderId + "'";
    }

    /*

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }
*/

    /*


    public void setType(Type type) {
        this.type = type;
    }
    */

    /*@Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(id);
        objectOutput.writeLong(senderId);
        //objectOutput.writeByte(type.ordinal());
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        id = objectInput.readLong();
        senderId = objectInput.readLong();
        //type = Type.values()[objectInput.readByte()];
    }*/
}
