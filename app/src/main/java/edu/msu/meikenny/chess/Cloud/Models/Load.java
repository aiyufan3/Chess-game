package edu.msu.meikenny.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;

public class Load {
    @Attribute(name = "msg", required = false)
    private String message;

    @Attribute
    private String status;

    @Attribute(required = false)
    private String player1;

    @Attribute(required = false)
    private String player2;

    @Attribute(required = false)
    private int id;

    @Attribute(required = false)
    private String board;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer1() { return player1; }
    public String getPlayer2() { return player2; }
    public void setPlayer1(String p1) { this.player1 = p1; }
    public void setPlayer2(String p2) { this.player2 = p2; }
    public String getBoard() { return board; }
    public void setBoard(String b) { this.board = b; }

    public Load() {}

    public Load(String status, String msg, int id) {
        this.status = status;
        this.message = msg;
        this.id = id;
    }
}
