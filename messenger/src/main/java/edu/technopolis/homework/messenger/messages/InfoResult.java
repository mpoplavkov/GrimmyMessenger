package edu.technopolis.homework.messenger.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class InfoResult extends Message{
    private long userId;
    private String login;
    private String about;

    public InfoResult(long userId, String login, String about) {
        super(0, Type.MSG_INFO_RESULT);
        this.userId = userId;
        this.login = login;
        this.about = about;
    }

    public InfoResult() {}

    public String getLogin() {
        return login;
    }

    public String getAbout() {
        return about;
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof InfoResult))
            return false;
        if (!super.equals(other))
            return false;
        InfoResult result = (InfoResult) other;
        return Objects.equals(login, result.login) && Objects.equals(about, result.about);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), login, about);
    }

    @Override
    public String toString() {
        return "InfoResult{" +
                super.toString() + ", " +
                "login=" + login + ", " +
                "about=" + about + "}";
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeLong(userId);
        objectOutput.writeBytes(login + " " + about);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        userId = objectInput.readLong();
        String s = objectInput.readLine();
        int split = s.indexOf(' ');
        login = s.substring(0, split);
        about = s.substring(split + 1, s.length());
    }
}
