package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.parseBoolean;

/**
 * Простейший протокол передачи данных
 */
public class StringProtocol implements Protocol {

    private static final String DELIMITER = ";";

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        String str = new String(bytes);
        System.out.println("decoded: " + str);
        String[] tokens = str.split(DELIMITER);
        Type type = Type.valueOf(tokens[0]);
        switch (type) {
            case MSG_TEXT:
                TextMessage textMsg = new TextMessage();
                textMsg.setChatId(parseLong(tokens[1]));
                textMsg.setText(tokens[2]);
                textMsg.setType(type);
                return textMsg;
            case MSG_LOGIN:
                LoginMessage loginMsg = new LoginMessage();
                loginMsg.setLogin(tokens[1]);
                loginMsg.setPassword(tokens[2]);
                loginMsg.setType(type);
                return loginMsg;
            case MSG_STATUS:
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setStatus(parseBoolean(tokens[1]));
                statusMessage.setInfo(tokens[2]);
                statusMessage.setType(type);
                return statusMessage;
            case MSG_CHAT_LIST:
                ChatListMessage chatListMessage = new ChatListMessage();
                List<Long> mes = new ArrayList<>();
                for (int i = 1; i < tokens.length; i++) {
                    mes.add(parseLong(tokens[i]));
                }
                chatListMessage.setList(mes);
                chatListMessage.setType(type);
                return chatListMessage;
            case MSG_CHAT_CREATE:
                ChatCreateMessage chatCreateMsg = new ChatCreateMessage();
                Set<Long> list = new HashSet<>();
                for (int i = 1; i < tokens.length; i++)
                    list.add(parseLong(tokens[i]));
                chatCreateMsg.setListOfInvited(list);
                chatCreateMsg.setType(type);
                return chatCreateMsg;
            case MSG_CHAT_HIST:
                ChatHistoryMessage chatHistoryMsg = new ChatHistoryMessage();
                chatHistoryMsg.setChatId(parseLong(tokens[1]));
                chatHistoryMsg.setType(type);
                return chatHistoryMsg;
            case MSG_CHAT_HIST_RESULT:
                ChatHistoryResult chatHistoryResult = new ChatHistoryResult();
                List<TextMessage> messages = new ArrayList<>();
                for (int i = 1; i < tokens.length - 1; i++) {
                    messages.add(new TextMessage(parseLong(tokens[i]),tokens[i+1]));
                    i++;
                }
                chatHistoryResult.setList(messages);
                chatHistoryResult.setType(Type.MSG_CHAT_HIST_RESULT);
                return chatHistoryResult;
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
                builder.append(String.valueOf(sendMessage.getChatId())).append(DELIMITER).append(sendMessage.getText());
                break;
            case MSG_LOGIN:
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(loginMessage.getLogin()).append(DELIMITER).append(loginMessage.getPassword());
                break;
            case MSG_STATUS:
                StatusMessage statusMessage = (StatusMessage) msg;
                builder.append(statusMessage.getStatus()).append(DELIMITER).append(statusMessage.getInfo());
                break;
            case MSG_CHAT_LIST:
                ChatListMessage chatListMessage = (ChatListMessage) msg;
                for (long mes : chatListMessage.getList())
                    builder.append(mes).append(DELIMITER);
                break;
            case MSG_CHAT_CREATE:
                ChatCreateMessage ccMessage = (ChatCreateMessage) msg;
                for (long whoInvited : ccMessage.getListOfInvited())
                    builder.append(whoInvited).append(DELIMITER);
                break;
            case MSG_CHAT_HIST:
                ChatHistoryMessage chMessage = (ChatHistoryMessage) msg;
                builder.append(chMessage.getChatId()).append(DELIMITER);
                break;
            case MSG_CHAT_LIST_RESULT:
                break;
            case MSG_CHAT_HIST_RESULT:
                ChatHistoryResult chatHistoryResult = (ChatHistoryResult) msg;
                for (int i = 0; i < chatHistoryResult.getList().size(); i++) {
                    builder.append(chatHistoryResult.getList().get(i).getSenderId()).append(DELIMITER);
                    builder.append(chatHistoryResult.getList().get(i).getText()).append(DELIMITER);
                }
                break;
            case MSG_INFO_RESULT:
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