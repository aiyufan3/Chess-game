package edu.msu.meikenny.chess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import edu.msu.meikenny.chess.Cloud.Models.GameState;


public class Chess {
    private ChessBoardActivity chessBoardActivity;
    private int marginY;
    private int marginX;
    private int boardSize;
    private float scaleFactor;
    final static float SCALE = 0.97f;
    private Bitmap board;
    private Context context;
    public ArrayList<ChessPiece> pieces = new ArrayList<>();
    public ArrayList<ChessPiece> killedPieces = new ArrayList<>();

    private Pair BoardCoords[][] = new Pair[8][8];
    /**
     * This variable is set to a piece we are dragging. If
     * we are not dragging, the variable is null.
     */
    private ChessPiece dragging = null;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    /**
     * Origin X location of touched piece
     */
    private float originX;

    /**
     * Origin Y location of touched piece
     */
    private float originY;

    /**
     * Highlight square to inform user which piece and square is selected
     */
    private boolean highlightBox = false;

    /**
     * Paint object used to draw elements on board
     */
    private final Paint paint = new Paint();

    private ChessPiece currentPiece = null;

    /**
     * Determines if it is black player's turn to move
     */
    private boolean blackTurn = false;

    private float downX = 0f;
    private float downY = 0f;

    /**
     * Determines if current player has made a move yet
     */
    private boolean hasMadeMove = false;

    /**
     * Tells if player has made a move
     * @return boolean determining if current player has moved one of their pieces
     */
    public boolean getHasMadeMove() {
        return hasMadeMove;
    }

    /**
     * The name of the bundle keys to save the puzzle
     */
    private final static String ALIVEPIECES = "Chess.alivepieces";
    private final static String TURN = "Chess.turn";
    private final static String MOVABLE = "Chess.movable";

    public Chess(Context context) {
        this.context = context;
        board = BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_board);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setAlpha(120);


        float d_width = 0.0625f;

        pieces.add(new ChessPiece(context, R.drawable.chess_rdt45, d_width, 0.0625f, true));
        pieces.add(new ChessPiece(context, R.drawable.chess_ndt45, d_width * 3, 0.0625f, true));
        pieces.add(new ChessPiece(context, R.drawable.chess_bdt45, d_width * 5, 0.0625f, true));
        pieces.add(new ChessPiece(context, R.drawable.chess_qdt45, d_width * 7, 0.0625f, true));
        pieces.add(new King(context, R.drawable.chess_kdt45, d_width * 9, 0.0625f, true));
        pieces.add(new ChessPiece(context, R.drawable.chess_bdt45, d_width * 11, 0.0625f, true));
        pieces.add(new ChessPiece(context, R.drawable.chess_ndt45, d_width * 13, 0.0625f, true));
        pieces.add(new ChessPiece(context, R.drawable.chess_rdt45, d_width * 15, 0.0625f, true));

        pieces.add(new ChessPiece(context, R.drawable.chess_rlt45, d_width, 0.9375f, false));
        pieces.add(new ChessPiece(context, R.drawable.chess_nlt45, d_width * 3, 0.9375f, false));
        pieces.add(new ChessPiece(context, R.drawable.chess_blt45, d_width * 5, 0.9375f, false));
        pieces.add(new ChessPiece(context, R.drawable.chess_qlt45, d_width * 7, 0.9375f, false));
        pieces.add(new King(context, R.drawable.chess_klt45, d_width * 9, 0.9375f, false));
        pieces.add(new ChessPiece(context, R.drawable.chess_blt45, d_width * 11, 0.9375f, false));
        pieces.add(new ChessPiece(context, R.drawable.chess_nlt45, d_width * 13, 0.9375f, false));
        pieces.add(new ChessPiece(context, R.drawable.chess_rlt45, d_width * 15, 0.9375f, false));

        pieces.add(new Pawn(context, R.drawable.chess_pdt45, d_width, 0.1875f, true));
        pieces.add(new Pawn(context, R.drawable.chess_pdt45, d_width * 3, 0.1875f, true));
        pieces.add(new Pawn(context, R.drawable.chess_pdt45, d_width * 5, 0.1875f, true));
        pieces.add(new Pawn(context, R.drawable.chess_pdt45, d_width * 7, 0.1875f, true));
        pieces.add(new Pawn(context, R.drawable.chess_pdt45, d_width * 9, 0.1875f, true));
        pieces.add(new Pawn(context, R.drawable.chess_pdt45, d_width * 11, 0.1875f, true));
        pieces.add(new Pawn(context, R.drawable.chess_pdt45, d_width * 13, 0.1875f, true));
        pieces.add(new Pawn(context, R.drawable.chess_pdt45, d_width * 15, 0.1875f, true));

        pieces.add(new Pawn(context, R.drawable.chess_plt45, d_width, 0.8125f, false));
        pieces.add(new Pawn(context, R.drawable.chess_plt45, d_width * 3, 0.8125f, false));
        pieces.add(new Pawn(context, R.drawable.chess_plt45, d_width * 5, 0.8125f, false));
        pieces.add(new Pawn(context, R.drawable.chess_plt45, d_width * 7, 0.8125f, false));
        pieces.add(new Pawn(context, R.drawable.chess_plt45, d_width * 9, 0.8125f, false));
        pieces.add(new Pawn(context, R.drawable.chess_plt45, d_width * 11, 0.8125f, false));
        pieces.add(new Pawn(context, R.drawable.chess_plt45, d_width * 13, 0.8125f, false));
        pieces.add(new Pawn(context, R.drawable.chess_plt45, d_width * 15, 0.8125f, false));

        float dCoord = 1f;
        for(int i = 0; i < 8; i++)
        {
            float xy = 0.0625f * dCoord;
            BoardCoords[0][i] = new Pair (xy, xy);
            dCoord += 2;
        }

        float dY = 3f;
        for(int i = 1; i < 8; i++)
        {
            float dX = 1f;
            float y = 0.0625f * dY;
            for(int j = 0; j < 8; j++)
            {
                float x = 0.0625f * dX;
                BoardCoords[i][j] = new Pair(x, y);
                dX += 2;
            }
            dY += 2;
        }
    }


    /**
     * Draws the chess board
     */
    public void draw(Canvas canvas) {
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();
        int minDim = Math.min(wid,hit);

        boardSize = (int) (minDim * SCALE);
        marginX = (int) ((wid - boardSize) / 2f);
        marginY = (int) ((hit - boardSize) / 2f);
        scaleFactor = (float) boardSize / (float) board.getWidth();

        canvas.save();
        canvas.translate(marginX, marginY);
        canvas.scale(scaleFactor, scaleFactor);
        canvas.drawBitmap(board, 0, 0, null);
        canvas.restore();

        if (highlightBox && currentPiece != null) {
            canvas.save();
            canvas.translate(marginX + originX * boardSize, marginY + originY * boardSize);
            canvas.scale(scaleFactor, scaleFactor);
            canvas.translate(currentPiece.getPieceWidth() / 7f, currentPiece.getPieceHeight() / 7f);
            canvas.drawRect(0,0,-currentPiece.getPieceWidth() / 3.5f,-currentPiece.getPieceHeight() / 3.5f, paint);
            canvas.restore();
        }

        for(ChessPiece piece : pieces) {
            piece.draw(canvas, marginX, marginY, boardSize, scaleFactor / 3);
        }
    }

    /**
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {
        //
        // Convert an x,y location to a relative location in the
        // board.
        //
        float relX = (event.getX() - marginX) / boardSize;
        float relY = (event.getY() - marginY) / boardSize;

        switch(event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                boolean ret = onTouched(relX, relY);
                view.invalidate();
                if(dragging != null){
                    downX =  dragging.getX();
                    downY = dragging.getY();
                }
                return ret;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return onReleased(view, relX, relY);

            case MotionEvent.ACTION_MOVE:
                // If we are dragging, move the piece and force a redraw
                if(dragging != null) {
                    dragging.move(relX - lastRelX, relY - lastRelY);
                    lastRelX = relX;
                    lastRelY = relY;
                    view.invalidate();
                    return true;
                }
                break;
        }

        return false;
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     * @param x x location for the touch, relative to the chess board - 0 to 1 over the board
     * @param y y location for the touch, relative to the chess board - 0 to 1 over the board
     * @return true if the touch is handled
     */
    private boolean onTouched(float x, float y) {
        // Check each piece to see if it has been hit
        // We do this in reverse order so we find the pieces in front
        for(int p=pieces.size()-1; p>=0;  p--) {
            if(pieces.get(p).hit(x, y, boardSize, scaleFactor / 3)) {
                // We hit a piece!
                // Hit piece has to be the same color as the current player's turn
                // Player also has to have not made a move yet
                dragging = pieces.get(p);
                if ((dragging.getBlack() && blackTurn) || (!dragging.getBlack() && !blackTurn)  && !hasMadeMove) {
                    pieces.remove(p);
                    pieces.add(dragging);
                    lastRelX = x;
                    lastRelY = y;
                } else {
                    break;
                }

                // Highlight the piece's current square
                highlightBox = true;

                if (!hasMadeMove) {
                    originX = dragging.getX();
                    originY = dragging.getY();
                    currentPiece = dragging;
                } else {
                    dragging = null;
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Handle a release of a touch message.
     * @param x x location for the touch release, relative to the chess board - 0 to 1 over the board
     * @param y y location for the touch release, relative to the chess board - 0 to 1 over the board
     * @return true if the touch is handled
     */
    private boolean onReleased(View view, float x, float y) {
        view.invalidate();
        if(dragging != null) {
            if(dragging.maybeSnap()) {
                // We have snapped into place

                // Check if piece is promotable (i.e. it's a pawn)
                if (dragging.promotable()) {
                    // Black pawn has reached end of the board
                    if (dragging.getBlack() && dragging.getY() == 0.9375)
                    {
                        // Prompt dialog box for pawn promotion
                        // Instantiate a dialog box builder
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(view.getContext());

                        // Parameterize the builder
                        builder.setTitle("Select a piece to promote To: ");
                        // Add list to dialog box
                        builder.setItems(R.array.promotion_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                promotePiece(which);
                                view.invalidate();
                            }
                        });


                        // Create the dialog box and show it
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    // White pawn has reached end of the board
                    else if (!dragging.getBlack() && dragging.getY() == 0.0625)
                    {
                        // Prompt dialog box for pawn promotion
                        // Instantiate a dialog box builder
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(view.getContext());

                        // Parameterize the builder
                        builder.setTitle("Select a piece to promote To: ");
                        // Add list to dialog box
                        builder.setItems(R.array.promotion_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                promotePiece(which);
                                view.invalidate();
                            }
                        });

                        // Create the dialog box and show it
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
                // Invalidate to redraw
                view.invalidate();
            }
            else
            {
                hasMadeMove = false;
                view.invalidate();
            }

            // If current chess piece is within kill distance from the opposite colored piece, it will kill
            for(ChessPiece piece : pieces) {
                if (piece.getBlack() != dragging.getBlack()) {
                    if((piece.getX() == dragging.getX())
                            && (piece.getY() == dragging.getY()) && piece != dragging) {
                        // Add more rules here for specific chess piece killing
                        // For now any piece will kill anything of opposite color
                        killedPieces.add(piece);
                        pieces.remove(piece);
                        view.invalidate();
                        break;
                    }
                }
            }

            isGameOver();
            /*Log.i("A","downX"+downX);
            Log.i("A","downY"+downY);
            Log.i("A","dragging.getX()"+dragging.getX());
            Log.i("A","dragging.getY()"+dragging.getY());*/
            if(Math.abs(dragging.getX() - downX) >= 0.1 || Math.abs(dragging.getY() - downY) >= 0.1){
                //when click the pieces and make it move
                //Log.i("A","move");
                hasMadeMove = true;
            }

            // Remove highlight square
            highlightBox = false;
            currentPiece = null;

            dragging = null;
            return true;
        }

        return false;
    }

    /**
     * Countdown Timer gets signal to check whether is move or not
     * @param hasMadeMove for the counter timer
     */
    public void setHasMadeMove(boolean hasMadeMove) {
        this.hasMadeMove = hasMadeMove;
    }

    /**
     * Promotes dragging piece
     * @param promotion Type of piece to promote to
     */
    private void promotePiece(int promotion) {
        ChessPiece lastPiece = pieces.get(pieces.size()-1);
        if (!lastPiece.promotable())
        {
            return;
        }
        pieces.remove(lastPiece);

        switch (promotion) {
            // Queen
            case 0:
                lastPiece = new Queen(context, lastPiece.getBlack() ? R.drawable.chess_qdt45 : R.drawable.chess_qlt45, lastPiece.getX(), lastPiece.getY(), lastPiece.getBlack());
                break;
            // Rook
            case 1:
                lastPiece = new Rook(context, lastPiece.getBlack() ? R.drawable.chess_rdt45 : R.drawable.chess_rlt45, lastPiece.getX(), lastPiece.getY(), lastPiece.getBlack());
                break;
            // Knight
            case 2:
                lastPiece = new Knight(context, lastPiece.getBlack() ? R.drawable.chess_ndt45 : R.drawable.chess_nlt45, lastPiece.getX(), lastPiece.getY(), lastPiece.getBlack());
                break;
            // Bishop
            case 3:
                lastPiece = new Bishop(context, lastPiece.getBlack() ? R.drawable.chess_bdt45 : R.drawable.chess_blt45, lastPiece.getX(), lastPiece.getY(), lastPiece.getBlack());
                break;
        }

        pieces.add(lastPiece);
    }
    /**
     * Determine if the chess game is over. Call on activity to display end screen
     * with correct winner
     */
    public void isGameOver() {
        // Initialize king booleans to false
        // Assume that there are no kings on the board, look through all pieces to prove otherwise
        boolean has_blackKing = false;
        boolean has_whiteKing = false;

        // Loop through pieces
        for (ChessPiece piece: pieces)
        {
            // Check if piece is black or white and if it is a game determining piece (i.e. a king)
            if (piece.getBlack() && piece.determineGame()) {
                has_blackKing = true;
            } else if (!piece.getBlack() && piece.determineGame()) {
                has_whiteKing = true;
            }
        }

        // If there is no black king, player 1 wins
        // If there is no white king, player 2 wins
        if (!has_blackKing) {
            chessBoardActivity.p1Wins = true;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ended = database.getReference(chessBoardActivity.gameId + "-ended");
            ended.setValue(true);
        } else if (!has_whiteKing){
            chessBoardActivity.p1Wins = false;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ended = database.getReference(chessBoardActivity.gameId + "-ended");
            ended.setValue(false);
        }
    }

    public boolean getBlackTurn() {
        return blackTurn;
    }

    /**
     * Will set the turn to the current player
     */
    void setTurn(boolean turn1) {
        //set up the condition that the button cannot
        // switch to the other side if press more than one times
        if(hasMadeMove){
            hasMadeMove = false;
            blackTurn = turn1;
        }
    }

    /**
     * Setter for the current chess activity
     */
    public void setActivity (ChessBoardActivity act)
    {
        chessBoardActivity = act;
    }

    public void saveInstanceState(Bundle bundle) {
        ChessPiece [] tempPieces = new ChessPiece[pieces.size()];

        for(int i=0;  i<pieces.size(); i++) {
            ChessPiece piece = pieces.get(i);
            tempPieces[i] = piece;
        }

        bundle.putSerializable(ALIVEPIECES, tempPieces);
        bundle.putBoolean(TURN, blackTurn);
        bundle.putBoolean(MOVABLE, hasMadeMove);
    }

    /**
     * Read the puzzle from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle) {
        ChessPiece [] tempPieces = (ChessPiece[]) bundle.getSerializable(ALIVEPIECES);
        boolean turn = bundle.getBoolean(TURN);
        boolean movable = bundle.getBoolean(MOVABLE);
        blackTurn = turn;
        hasMadeMove = movable;
        pieces.clear();
        Collections.addAll(pieces, tempPieces);
    }

    public void saveXml(String name, XmlSerializer xml) throws IOException {
        xml.startTag(null, "chess");

        xml.attribute(null, "blackTurn", Boolean.toString(blackTurn));
        xml.attribute(null, "hasMadeMove", Boolean.toString(hasMadeMove));
        for(int i=0;  i<pieces.size(); i++) {
            pieces.get(i).saveXml(xml);
        }

        xml.endTag(null,  "chess");
    }

    public void loadXml(String xml) {
        GameState state = new GameState("","",xml, pieces, context);
        blackTurn = state.isPlayer2Turn();
        hasMadeMove = state.isMoveMade();
    }

}