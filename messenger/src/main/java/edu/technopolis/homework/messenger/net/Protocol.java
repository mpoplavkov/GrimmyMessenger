package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.Message;

import java.nio.ByteBuffer;

/**
 *
 */
public interface Protocol {

    Message decode(ByteBuffer byteBuffer) throws ProtocolException;

    void encode(Message msg, ByteBuffer byteBuffer) throws ProtocolException;

}
