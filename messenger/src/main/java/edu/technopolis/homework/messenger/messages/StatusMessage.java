package edu.technopolis.homework.messenger.messages;

import java.util.Objects;

public class StatusMessage extends Message {

    private boolean status;
    private String info;

    public StatusMessage(boolean status, String info) {
        super(0, Type.MSG_STATUS);
        this.status = status;
        this.info = info;
    }

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
        if (!(other instanceof StatusMessage))
            return false;
        if (!super.equals(other))
            return false;
        StatusMessage message = (StatusMessage) other;
        return Objects.equals(status, message.status) && Objects.equals(info, message.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status, info);
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                super.toString() + ", " +
                "status=" + status + ", " +
                "info=" + info +
                '}';
    }
}