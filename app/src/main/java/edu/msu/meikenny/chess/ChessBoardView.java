package edu.msu.meikenny.chess;

import android.content.Context;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ChessBoardView extends View {
    private Chess chess;

    public boolean isEnableDone() {
        return enableDone;
    }

    private boolean enableDone = false;

    public ChessBoardView(Context context) {
        super(context);
        init(null, 0);
    }

    public ChessBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ChessBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return chess.onTouchEvent(this, event);
    }

    private void init(AttributeSet attrs, int defStyle) {
        chess = new Chess(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        chess.draw(canvas);
        enableDone = (chess.getHasMadeMove());
    }

    public Chess getChess() {
        return chess;
    }

    /**
     * Save the board to a bundle
     * @param bundle The bundle we save to
     */
    public void saveInstanceState(Bundle bundle) {
        chess.saveInstanceState(bundle);
    }

    /**
     * Load the board from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle) {
        chess.loadInstanceState(bundle);
    }
}