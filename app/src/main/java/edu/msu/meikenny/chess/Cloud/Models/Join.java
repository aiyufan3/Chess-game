package edu.msu.meikenny.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;

public class Join {
    @Attribute(name = "msg", required = false)
    private String message;

    @Attribute
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
