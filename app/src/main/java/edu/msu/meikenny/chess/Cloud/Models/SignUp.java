package edu.msu.meikenny.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;

public class SignUp {
    @Attribute(required = false)
    private String username;

    @Attribute(required = false)
    private int id;

    @Attribute(name = "msg", required = false)
    private String message;
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Attribute
    private String status;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public SignUp() {}

    public SignUp(String status, String msg, int id) {
        this.status = status;
        this.message = msg;
        this.id = id;
    }
}
