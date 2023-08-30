package edu.msu.meikenny.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;

public class Login {

    @Attribute(name = "name", required = false)
    private String username;

    @Attribute(required = false)
    private int id;

    @Attribute(name = "msg", required = false)
    private String message;

    @Attribute(name = "pass", required = false)
    private String pw;

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

    public Login() {}

    public Login(String status, String msg, int id) {
        this.status = status;
        this.message = msg;
        this.id = id;
    }
}
