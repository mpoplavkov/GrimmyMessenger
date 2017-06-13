package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SerializableProtocol implements Protocol {
    @Override
    public Message decode(ByteBuffer byteBuffer) throws ProtocolException {
        Message message;
        byte[] bytes = new byte[byteBuffer.position()];
        byteBuffer.flip();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = byteBuffer.get();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            message = (Message)ois.readObject();
            bais.close();
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
        return message;
    }

    @Override
    public void encode(Message msg, ByteBuffer byteBuffer) throws ProtocolException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(msg);
            baos.close();
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
        byteBuffer.clear();
        byteBuffer.put(baos.toByteArray());
    }
}
