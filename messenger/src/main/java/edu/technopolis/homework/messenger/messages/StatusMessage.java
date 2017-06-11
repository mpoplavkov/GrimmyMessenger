package edu.technopolis.homework.messenger.messages;

import java.util.Objects;

public class StatusMessage extends Message{

    private boolean status;
    private String info;

    public boolean getStatus() {
        return status;
    }
    public String getInfo() {
        return info;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        if (!super.equals(other))
            return false;
        StatusMessage message = (StatusMessage) other;
        return Objects.equals(message, message.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status, info);
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "status='" + status + '\'' +
                "info='" + info + '\'' +
                '}';
    }
}