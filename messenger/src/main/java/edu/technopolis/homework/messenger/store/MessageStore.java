package edu.technopolis.homework.messenger.store;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import edu.technopolis.homework.messenger.messages.Message;
import edu.technopolis.homework.messenger.messages.TextMessage;

public interface MessageStore {
    /**
     * получаем список ид пользователей заданного чата
     */
    List<Long> getChatsByUserId(Long userId) throws SQLException;

    /**
     * получить информацию о чате
     */
    //Chat getChatById(Long chatId);

    /**
     * Список сообщений из чата
     */
    List<TextMessage> getMessagesFromChat(Long senderId, Long chatId) throws SQLException;

    /**
     * Получить информацию о сообщении
     */
    Message getMessageById(Long messageId) throws SQLException;

    /**
     * Добавить сообщение в чат
     */
    void addMessage(TextMessage message) throws SQLException;

    /**
     * Добавить пользователя к чату
     */
    void addUserToChat(Long userId, Long chatId) throws SQLException;

    /**
     * Создать чат с заданными пользователями
     */
    long createChat(String name, Set<Long> users) throws SQLException;

}
