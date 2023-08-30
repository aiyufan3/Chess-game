package edu.msu.meikenny.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.msu.meikenny.chess.Cloud.Cloud;

public class ChessEndActivity extends AppCompatActivity {

    String winner;
    String loser;
    TextView winnerTextView;
    TextView loserTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_end);

        winner = getIntent().getExtras().getString("WinnerName");
        loser = getIntent().getExtras().getString("LoserName");


        winnerTextView = findViewById(R.id.winnerTextBox);
        loserTextView = findViewById(R.id.loserTextBox);
        winnerTextView.append(" " + winner);
        loserTextView.append(" " + loser);

        //implement the use of android media to add a sound
        MediaPlayer congratulations = MediaPlayer.create(ChessEndActivity.this, R.raw.congratulations);
        congratulations.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                boolean success = cloud.DeleteGame(Integer.toString(getIntent().getExtras().getInt("GameId")));
                if (!success) {
                    Log.e("Game deletion", "Could not delete game correctly!!!!");
                }
                //implement thread.sleep
                // current thread to suspend execution for a 2000ms
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void onReturnToOpeningScreen(View view) {
        Intent intent = new Intent(this, GamesActivity.class);
        startActivity(intent);

        finish();
    }
}