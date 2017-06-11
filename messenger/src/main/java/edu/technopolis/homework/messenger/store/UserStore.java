package edu.technopolis.homework.messenger.store;

import edu.technopolis.homework.messenger.User;

import java.sql.SQLException;

public interface UserStore {
    /**
     * Добавить пользователя в хранилище
     * Вернуть его же
     */
    User addUser(User user) throws SQLException;

    /**
     * Обновить информацию о пользователе
     */
    User updateUser(User user) throws SQLException;

    /**
     * Получить пользователя по логину/паролю
     * return null if user not found
     */
    User getUser(String login, String pass) throws SQLException;

    /**
     * Получить пользователя по id, например запрос информации/профиля
     * return null if user not found
     */
    User getUserById(Long id) throws SQLException;
}
