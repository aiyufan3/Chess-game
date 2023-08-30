package edu.msu.meikenny.chess;

import android.content.Context;

public class King extends ChessPiece{
    public King(Context context, int id, float finalX, float finalY, boolean black) {
        super(context, id, finalX, finalY, black);
    }

    @Override
    public boolean determineGame() {
        return true;
    }
}
