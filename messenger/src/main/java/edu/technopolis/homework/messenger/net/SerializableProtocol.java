package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableProtocol implements Protocol {
    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        Message message;
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream( bais)) {
            message = (Message)ois.readObject();
            bais.close();
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
        return message;
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(msg);
            baos.close();
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
        return baos.toByteArray();
    }
}
