package edu.msu.meikenny.chess;

import android.content.Context;
import android.util.Log;

public class Pawn extends ChessPiece{
    public Pawn(Context context, int id, float finalX, float finalY, boolean black) {
        super(context, id, finalX, finalY, black);
    }

    @Override
    public boolean promotable() {
        return true;
    }
}
