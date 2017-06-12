package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.LoginMessage;
import edu.technopolis.homework.messenger.messages.Message;
import edu.technopolis.homework.messenger.messages.TextMessage;
import edu.technopolis.homework.messenger.messages.Type;

import static edu.technopolis.homework.messenger.messages.Type.MSG_LOGIN;
import static java.lang.Integer.parseInt;
import static javax.xml.bind.DatatypeConverter.parseString;

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

            case MSG_LOGIN:
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setLogin(parseString(tokens[1]));
                loginMessage.setPassword(parseInt(tokens[2]));
                loginMessage.setType(MSG_LOGIN);
                return loginMessage;

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

            case MSG_LOGIN:
                LoginMessage sendMsg = (LoginMessage) msg;
                builder.append(String.valueOf(sendMsg.getType())).append(DELIMITER);
                builder.append(String.valueOf(sendMsg.getLogin())).append(DELIMITER);
                builder.append(sendMsg.getPassword()).append(DELIMITER);
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