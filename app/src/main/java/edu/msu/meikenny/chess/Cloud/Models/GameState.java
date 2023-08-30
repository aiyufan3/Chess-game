package edu.msu.meikenny.chess.Cloud.Models;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import edu.msu.meikenny.chess.ChessPiece;
import edu.msu.meikenny.chess.King;
import edu.msu.meikenny.chess.Pawn;
import edu.msu.meikenny.chess.R;

public class GameState {
    boolean player2Turn = false;
    boolean moveMade = false;
    String player1;
    String player2;

    public ChessPiece createPiece(Context context, int id, float x, float y) {
        switch(id) {
            case R.drawable.chess_plt45:
                return new Pawn(context, id, x, y, false);
            case R.drawable.chess_rlt45:
            case R.drawable.chess_blt45:
            case R.drawable.chess_nlt45:
            case R.drawable.chess_qlt45:
                return new ChessPiece(context, id, x, y, false);
            case R.drawable.chess_klt45:
                return new King(context, id, x, y, false);
            case R.drawable.chess_pdt45:
                return new Pawn(context, id, x, y, true);
            case R.drawable.chess_rdt45:
            case R.drawable.chess_bdt45:
            case R.drawable.chess_ndt45:
            case R.drawable.chess_qdt45:
                return new ChessPiece(context, id, x, y, true);
            case R.drawable.chess_kdt45:
                return new King(context, id, x, y, true);
        }
        return null;
    }

    public GameState(String p1, String p2, String state, ArrayList<ChessPiece> pieces, Context context) {
        player1 = p1;
        player2 = p2;
        String [] delim = state.replace(" ", "").split("\\?");
        if (delim.length != 2) {
            Log.e("XML Load", "Server did not load game correctly" + delim.length);
        } else {
            String [] gameParams = delim[0].split(",");
            player2Turn = Boolean.parseBoolean(gameParams[0]);
            moveMade = Boolean.parseBoolean(gameParams[1]);
            pieces.clear();
            for(String piecesParams : delim[1].split("\\|")) {
                String [] pieceParams = piecesParams.split(",");
                int pId = Integer.parseInt(pieceParams[0]);
                float pX = Float.parseFloat(pieceParams[1]);
                float pY = Float.parseFloat(pieceParams[2]);
                pieces.add(createPiece(context, pId, pX, pY));
            }
        }
    }

    public String getPlayer1() { return player1; }
    public String getPlayer2() { return player2; }

    public boolean isMoveMade() {
        return moveMade;
    }

    public boolean isPlayer2Turn() {
        return player2Turn;
    }
}
