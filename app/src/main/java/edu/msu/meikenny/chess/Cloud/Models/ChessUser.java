package edu.msu.meikenny.chess.Cloud.Models;

import java.io.Serializable;

public class ChessUser implements Serializable {
    private String name = null;
    private int id = -1;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
