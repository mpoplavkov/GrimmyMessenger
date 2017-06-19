package edu.technopolis.homework.messenger.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

/**
 * Created by mihailpoplavkov on 19.06.17.
 */
public class UserCreateMessage extends Message {
    private String login;
    private int password;

    public UserCreateMessage(long senderId, String login, String password) {
        super(senderId, Type.MSG_USER_CREATE);
        this.login = login;
        this.password = password.hashCode();
    }

    public UserCreateMessage(String login, String password) {
        this(0, login, password);
    }

    public UserCreateMessage() {}

    public String getLogin() {
        return login;
    }

    public int getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof UserCreateMessage))
            return false;
        if (!super.equals(other))
            return false;
        UserCreateMessage message = (UserCreateMessage) other;
        return Objects.equals(login, message.login) && Objects.equals(password, message.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), login, password);
    }

    @Override
    public String toString() {
        return "UserCreateMessage{" + super.toString() + ", login=" + login + ", password=" + password + "}";
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeInt(password);
        objectOutput.writeBytes(login);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        password = objectInput.readInt();
        login = objectInput.readLine();
    }
}
