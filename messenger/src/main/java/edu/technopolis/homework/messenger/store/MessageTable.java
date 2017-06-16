package edu.technopolis.homework.messenger.store;

import edu.technopolis.homework.messenger.messages.Message;
import edu.technopolis.homework.messenger.messages.TextMessage;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MessageTable implements MessageStore {
    private static final String GET_CHATS_BY_USER_ID_QUERY = "SELECT chat_id FROM users_chats WHERE user_id = ?";
    private static final String GET_MESSAGES_FROM_CHAT_QUERY = "SELECT * FROM get_chat_history(?, ?)";
    private static final String GET_MESSAGE_BY_ID_QUERY = "SELECT id, chat_id, sender_id, text FROM messages WHERE id = ?";
    private static final String ADD_MESSAGE_QUERY = "INSERT INTO messages (chat_id, sender_id, text, time) VALUES(?, ?, ?, current_timestamp)";
    private static final String ADD_USER_TO_CHAT_QUERY = "INSERT INTO users_chats (chat_id, user_id) VALUES(?, ?)";
    private static final String CREATE_CHAT_QUERY = "SELECT * FROM create_chat(?, ?)";

    @Override
    public List<Long> getChatsByUserId(Long userId) throws SQLException {
        List<Long> chats = new LinkedList<>();
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(GET_CHATS_BY_USER_ID_QUERY);
        preparedStatement.setLong(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            chats.add(resultSet.getLong("chat_id"));
        }
        return chats;
    }

    @Override
    public List<TextMessage> getMessagesFromChat(Long senderId, Long chatId) throws SQLException {
        List<TextMessage> messages = new LinkedList<>();
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(GET_MESSAGES_FROM_CHAT_QUERY);
        preparedStatement.setLong(1, senderId);
        preparedStatement.setLong(2, chatId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            messages.add(new TextMessage(
                    resultSet.getLong("id"),
                    resultSet.getLong("sender_id"),
                    resultSet.getLong("chat_id"),
                    resultSet.getString("text")));
        }
        return messages;
    }

    @Override
    public TextMessage getMessageById(Long messageId) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(GET_MESSAGE_BY_ID_QUERY);
        preparedStatement.setLong(1, messageId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            TextMessage message = new TextMessage(resultSet.getLong("id"), resultSet.getLong("chat_id"), resultSet.getLong("sender_id"), resultSet.getString("text"));
            return message;
        }
        return null;
    }

    @Override
    public void addMessage(TextMessage message) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(ADD_MESSAGE_QUERY);
        preparedStatement.setLong(1, message.getChatId());
        preparedStatement.setLong(2, message.getSenderId());
        preparedStatement.setString(3, message.getText());
        preparedStatement.execute();
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(ADD_USER_TO_CHAT_QUERY);
        preparedStatement.setLong(1, chatId);
        preparedStatement.setLong(2, userId);
        preparedStatement.execute();
    }

    @Override
    public long createChat(String name, Set<Long> userIds) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(CREATE_CHAT_QUERY);
        Array array = StoreConnection.getConnection().createArrayOf("BIGINT", userIds.toArray());
        preparedStatement.setString(1, name);
        preparedStatement.setArray(2, array);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong("create_chat");
        }
        return 0;
    }
}
