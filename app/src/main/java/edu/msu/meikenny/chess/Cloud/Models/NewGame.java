package edu.msu.meikenny.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;

public class NewGame {
    @Attribute(name = "msg", required = false)
    private String message;

    @Attribute
    private String status;

    @Attribute(required = false)
    private String id;

    public String getId() { return id; }
    private void setId(String id) { this.id = id; }
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
