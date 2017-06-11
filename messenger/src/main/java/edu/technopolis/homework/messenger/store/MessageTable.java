package edu.technopolis.homework.messenger.store;

import edu.technopolis.homework.messenger.messages.TextMessage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MessageTable implements MessageStore {
    private static final String GET_CHATS_BY_USER_ID_QUERY = "SELECT chat_id FROM users_chats WHERE user_id = ?";
    private static final String GET_MESSAGES_FROM_CHAT_QUERY = "SELECT id FROM messages WHERE chat_id = ?";
    private static final String GET_MESSAGE_BY_ID_QUERY = "SELECT id, sender_id, text FROM messages WHERE id = ?";
    private static final String ADD_MESSAGE_QUERY = "INSERT INTO messages (chat_id, sender_id, text, time) VALUES(?, ?, ?, current_timestamp)";
    private static final String ADD_USER_TO_CHAT = "INSERT INTO users_chats (chat_id, user_id) VALUES(?, ?)";

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
    public List<Long> getMessagesFromChat(Long chatId) throws SQLException {
        List<Long> messages = new LinkedList<>();
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(GET_MESSAGES_FROM_CHAT_QUERY);
        preparedStatement.setLong(1, chatId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            messages.add(resultSet.getLong("id"));
        }
        return messages;
    }

    @Override
    public TextMessage getMessageById(Long messageId) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(GET_MESSAGE_BY_ID_QUERY);
        preparedStatement.setLong(1, messageId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            TextMessage message = new TextMessage(resultSet.getLong("id"), resultSet.getLong("sender_id"), resultSet.getString("text"));
            return message;
        }
        return null;
    }

    @Override
    public void addMessage(Long chatId, TextMessage message) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(ADD_MESSAGE_QUERY);
        preparedStatement.setLong(1, chatId);
        preparedStatement.setLong(2, message.getSenderId());
        preparedStatement.setString(3, message.getText());
        preparedStatement.execute();
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(ADD_USER_TO_CHAT);
        preparedStatement.setLong(1, chatId);
        preparedStatement.setLong(2, userId);
        preparedStatement.execute();
    }
}
