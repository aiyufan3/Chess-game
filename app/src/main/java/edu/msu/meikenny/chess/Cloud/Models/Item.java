package edu.msu.meikenny.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "game")
public final class Item {
    @Attribute
    private String id;

    @Attribute(name = "player1")
    private String name;

    @Attribute(name = "player2")
    private String opponent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Item(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public Item() {}

}
