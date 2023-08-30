package edu.msu.meikenny.chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class ChessPiece implements Serializable {
    /**
     * THe image for the actual piece.
     */
    private Bitmap piece;

    /**
     * x location.
     * We use relative x locations in the range 0-1 for the center
     * of the puzzle piece.
     */
    private float x = 0.259f;

    /**
     * y location
     */
    private float y = 0.238f;

    private int oldXIndex;
    private int oldYIndex;

    /**
     * The puzzle piece ID
     */
    private int id;

    /**
     * We consider a piece to be in the right location if within
     * this distance.
     */
    final static float SNAP_DISTANCE = 0.08f;

    /**
     * Determines if piece is black or white
     */
    private boolean isBlack;

    public boolean getBlack() { return isBlack; }

    public float getX() {
        return x;
    }
    public float getY() { return y; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public int getPieceWidth() { return -piece.getWidth(); }
    public int getPieceHeight() { return -piece.getHeight(); }

    private Pair board[][] = new Pair[8][8];

    public boolean promotable() {
        return false;
    }

    public boolean determineGame() { return false; }

    public int getId() {
        return id;
    }

    public ChessPiece(Context context, int id, float X, float Y, boolean black) {
        this.x = X;
        this.y = Y;
        this.id = id;
        this.isBlack = black;

        piece = BitmapFactory.decodeResource(context.getResources(), id);
    }



    /**
     * Draw the chess piece
     * @param canvas Canvas we are drawing on
     * @param marginX Margin x value in pixels
     * @param marginY Margin y value in pixels
     * @param boardSize Size we draw the chess board in pixels
     * @param scaleFactor Amount we scale the chess pieces when we draw them
     */
    public void draw(Canvas canvas, int marginX, int marginY, int boardSize, float scaleFactor) {
        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + x * boardSize, marginY + y * boardSize);

        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-piece.getWidth() / 2f, -piece.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(piece, 0, 0, null);
        canvas.restore();
    }

    /**
     * Move the chess piece by dx, dy
     * @param dx x amount to move
     * @param dy y amount to move
     */
    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /**
     * Test to see if we have touched a chess piece
     * @param testX X location as a normalized coordinate (0 to 1)
     * @param testY Y location as a normalized coordinate (0 to 1)
     * @param boardSize the size of the chess board in pixels
     * @param scaleFactor the amount to scale a piece by
     * @return true if we hit the piece
     */
    public boolean hit(float testX, float testY,
                       int boardSize, float scaleFactor) {

        if(board[0][0] == null) {
            float dCoord = 1f;
            for (int i = 0; i < 8; i++) {
                float xCoord = 0.0625f * dCoord;
                board[0][i] = new Pair(xCoord, 0.0625f);
                dCoord += 2;
            }

            float dY = 3f;
            for (int i = 1; i < 8; i++) {
                float dX = 1f;
                float y = 0.0625f * dY;
                for (int j = 0; j < 8; j++) {
                    float x = 0.0625f * dX;
                    board[i][j] = new Pair(x, y);
                    dX += 2;
                }
                dY += 2;
            }
        }

        for(int i = 0; i < 8; i ++) {
            for(int j = 0; j < 8; j++) {
                if(Math.abs(x - board[i][j].getX_coord()) < SNAP_DISTANCE &&
                        Math.abs(y - board[i][j].getY_coord()) < SNAP_DISTANCE) {

                    oldXIndex = i;
                    oldYIndex = j;
                }
            }
        }

        // Make relative to the location and size to the piece size
        int pX = (int)((testX - x) * boardSize / scaleFactor) +
                piece.getWidth() / 2;
        int pY = (int)((testY - y) * boardSize / scaleFactor) +
                piece.getHeight() / 2;

        if(pX < 0 || pX >= piece.getWidth() ||
                pY < 0 || pY >= piece.getHeight()) {
            return false;
        }

        // We are within the rectangle of the piece.
        // Are we touching actual picture?
        return (piece.getPixel(pX, pY) & 0xff000000) != 0;
    }

    /**
     * If we are within SNAP_DISTANCE of the correct
     * answer, snap to the correct answer exactly.
     * @return
     */
    public boolean maybeSnap() {
        for(int i = 0; i < 8; i ++) {
            for(int j = 0; j < 8; j++) {
                if(Math.abs(x - board[i][j].getX_coord()) < SNAP_DISTANCE &&
                        Math.abs(y - board[i][j].getY_coord()) < SNAP_DISTANCE) {

                    x = board[i][j].getX_coord();
                    y = board[i][j].getY_coord();
                    return true;
                }
            }
        }
        x = board[oldXIndex][oldYIndex].getX_coord();
        y = board[oldXIndex][oldYIndex].getY_coord();
        return false;
    }

    public void saveXml(XmlSerializer xml) throws IOException {
        xml.startTag(null, "piece");

        xml.attribute(null, "type", Integer.toString(id));
        xml.attribute(null, "x", Float.toString(x));
        xml.attribute(null, "y", Float.toString(y));

        xml.endTag(null, "piece");
    }
}


