package edu.technopolis.homework.messenger.store;

import edu.technopolis.homework.messenger.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserTable implements UserStore {
    private static final String GET_USER_QUERY = "SELECT id, login, about FROM users WHERE login = ? AND password = ?";
    private static final String GET_USER_BY_ID_QUERY = "SELECT id, login, about FROM users WHERE id = ?";
    private static final String ADD_USER_QUERY = "INSERT INTO users (login, password) VALUES (?, ?) RETURNING *";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET (login, password) = (?, ?) WHERE id = ? RETURNING *";

    @Override
    public User addUser(User user) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(ADD_USER_QUERY);
        preparedStatement.setString(1, user.getLogin());
        //preparedStatement.setInt(2, user.getPassword());
        ResultSet resultSet = preparedStatement.executeQuery();
        return ejectUser(resultSet);
    }

    @Override
    public User updateUser(User user) throws SQLException {
        if (user.getId() == 0) throw new IllegalArgumentException("User must has id");
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(UPDATE_USER_QUERY);
        preparedStatement.setString(1, user.getLogin());
        //preparedStatement.setInt(2, user.getPassword());
        preparedStatement.setLong(3, user.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        return ejectUser(resultSet);
    }

    @Override
    public User getUser(String login, int pass) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(GET_USER_QUERY);
        preparedStatement.setString(1, login);
        preparedStatement.setInt(2, pass);
        ResultSet resultSet = preparedStatement.executeQuery();
        return ejectUser(resultSet);
    }

    @Override
    public User getUserById(Long id) throws SQLException {
        PreparedStatement preparedStatement = StoreConnection.getConnection().prepareStatement(GET_USER_BY_ID_QUERY);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        return ejectUser(resultSet);
    }

    private User ejectUser(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            throw new SQLException("No such user in database.");
        }
        return new User(resultSet.getLong("id"), resultSet.getString("login"), resultSet.getString("about"));
    }
}
