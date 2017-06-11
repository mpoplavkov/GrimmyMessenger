package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.Message;
import edu.technopolis.homework.messenger.messages.TextMessage;
import edu.technopolis.homework.messenger.messages.Type;

/**
 * Простейший протокол передачи данных
 */
public class StringProtocol implements Protocol {

    public static final String DELIMITER = ";";

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        String str = new String(bytes);
        System.out.println("decoded: " + str);
        String[] tokens = str.split(DELIMITER);
        Type type = Type.valueOf(tokens[0]);
        switch (type) {
            case MSG_TEXT:
                TextMessage textMsg = new TextMessage();
                textMsg.setSenderId(parseLong(tokens[1]));
                textMsg.setText(tokens[2]);
                textMsg.setType(type);
                return textMsg;
            default:
                throw new ProtocolException("Invalid type: " + type);
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        StringBuilder builder = new StringBuilder();
        Type type = msg.getType();
        builder.append(type).append(DELIMITER);
        switch (type) {
            case MSG_TEXT:
                TextMessage sendMessage = (TextMessage) msg;
                builder.append(String.valueOf(sendMessage.getSenderId())).append(DELIMITER);
                builder.append(sendMessage.getText()).append(DELIMITER);
                break;
            default:
                throw new ProtocolException("Invalid type: " + type);


        }
        System.out.println("encoded: " + builder);
        return builder.toString().getBytes();
    }

    private Long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            // who care
        }
        return null;
    }
}