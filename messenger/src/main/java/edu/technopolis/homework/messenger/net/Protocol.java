package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.Message;

/**
 *
 */
public interface Protocol {

    Message decode(byte[] bytes) throws ProtocolException;

    byte[] encode(Message msg) throws ProtocolException;

}
