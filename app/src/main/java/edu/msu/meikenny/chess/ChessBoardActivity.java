package edu.msu.meikenny.chess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import edu.msu.meikenny.chess.Cloud.Cloud;
import edu.msu.meikenny.chess.Cloud.Models.Load;
import edu.msu.meikenny.chess.Cloud.Models.SaveGame;

public class ChessBoardActivity extends AppCompatActivity {

    TextView player1TurnTextView, player2TurnTextView;
    int gameId;
    String p1;
    String p2;
    String p1Text;
    String p2Text;
    String gameXml;
    boolean p1Wins = true;
    private FirebaseUser currUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    AlertDialog waitDlgBox;

    //variable for counter timer
    TextView timer;
    CountDownTimer cdt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chess_board_activity);

        if(savedInstanceState != null) {
            // We have saved state
            getChessView().loadInstanceState(savedInstanceState);
        }

        //Toggle countdown timer
        reverseTimer();

        //identifying view by looking for the player 1's ID
        p1 = getIntent().getExtras().getString("Player1");
        //identifying view by looking for the player 2's ID
        p2 = getIntent().getExtras().getString("Player2");

        gameXml = getIntent().getExtras().getString("GameState");
        gameId = getIntent().getExtras().getInt("GameId");
        myRef = database.getReference(Integer.toString(gameId));
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        waitDlgBox = new AlertDialog.Builder(ChessBoardActivity.this)
                .setTitle("Not your turn")
                .setMessage("Waiting for other player to finish their turn")
                .setCancelable(false)
                .create();


        //identifying view by looking for the text view of turn of player
        player1TurnTextView = findViewById(R.id.editTextPlayer1);
        player2TurnTextView = findViewById(R.id.editTextPlayer2);

        //tracking the name to show in the start of game screen
        p1Text = p1 + "'s turn - White";
        p2Text = p2 + "'s turn - Black" ;


        getChessView().getChess().setActivity(this);

        //show the turn and name in the beginning of game screen
        player2TurnTextView.setText(p2Text);
        player1TurnTextView.setText(p1Text);
        player1TurnTextView.setVisibility(getChess().getBlackTurn() ? View.GONE: View.VISIBLE);
        player2TurnTextView.setVisibility(getChess().getBlackTurn() ? View.VISIBLE : View.GONE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("Firebase Data Changed", "Value is: " + value);

                if(timer != null){
                    reverseTimer();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Get game data from server
                        Cloud cloud = new Cloud();
                        Load game = cloud.LoadBoard(String.valueOf(gameId));
                        if (game == null) {
                            // Game data could not be retrieved, show toast
                            View view = getChessView();
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(), "Could not retrieve game", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Game data retrieved successfully, start game
                            loadXml(game.getBoard(), game.getPlayer1(), game.getPlayer2());
                        }
                    }
                }).start();

                checkWaitTurn(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase Data", "Failed to read value.", error.toException());
            }
        });
        DatabaseReference ended = database.getReference(gameId + "-ended");
        ended.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.getValue().equals("no")) {
                    waitDlgBox.dismiss();
                    Intent intent = new Intent(ChessBoardActivity.this, ChessEndActivity.class);
                    intent.putExtra("WinnerName", (boolean)snapshot.getValue() ? p1 : p2);
                    intent.putExtra("LoserName", !(boolean)snapshot.getValue() ? p1 : p2);
                    intent.putExtra("GameId", gameId);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getChessView().invalidate();
    }

    public void checkWaitTurn(String value){
        Log.d("Check Wait", "Checking if wait dlg should display");
        Log.d("Check Wait", "Value is: " + value + " Current User is: " + currUser.getEmail().replace("@cse476project2.com", ""));
        if (value.equals(currUser.getEmail().replace("@cse476project2.com", ""))) {
            // Display a dlg box blocking them from moving a piece
            // Says waiting for next player's move
            Log.d("Check Wait", "WAIT DLG CALLED");
            getChessView().post(new Runnable() {
                @Override
                public void run() {
                    waitDlgBox.show();
                }
            });
        } else {
            waitDlgBox.dismiss();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (gameXml != null && !gameXml.equals("")) {
            loadXml(gameXml, p1, p2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chess, menu);
        return true;
    }

    /**
     * Handle options menu selections
     * @param item Menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_instructions) {
            InstructionsDlg dlg = new InstructionsDlg();
            dlg.show(getSupportFragmentManager(), "Instructions");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get the chess board view
     * @return ChessBoard reference
     */
    private ChessBoardView getChessView() {
        return this.findViewById(R.id.ChessView);
    }

    /**
     * Get the chess game
     * @return Chess reference
     */
    private Chess getChess() {
        return getChessView().getChess();
    }

    /**
     * Represents the "Done" button on the game page
     * Implement the turn and show message
     * */
    public void onDone(View view) {

        if(getChessView().isEnableDone()){
            // Toggle turn
            switchTurn();
            getChessView().invalidate();
            if (gameId != 0) {
                gameXml = getXml();
                reverseTimer();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cloud cloud = new Cloud();
                        boolean success = cloud.SaveGame(Integer.toString(gameId), gameXml);
                        if (!success) {
                            // Game state could not be saved successfully
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(), "Could not save game", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.d("FB Save Player name", currUser.getEmail().replace("@cse476project2.com", ""));
                            myRef.setValue(getChess().getBlackTurn() ? p1 : p2);
                        }
                    }
                }).start();
            }
        }
    }

    /**
     * Handles updating text views for player turns and switches turn in chess game
     */
    public void switchTurn() {
        reverseTimer();
        getChess().setTurn(!getChess().getBlackTurn());
        player1TurnTextView.setVisibility(getChess().getBlackTurn() ? View.GONE: View.VISIBLE);
        player2TurnTextView.setVisibility(getChess().getBlackTurn() ? View.VISIBLE : View.GONE);
    }

    /**
     * Handles updating text views for countdown timer
     */
    public void reverseTimer() {
        //set up a count-down timer for users
        timer = (TextView) findViewById(R.id.timer);
        final String msg1 = "Seconds Remaining: ";
          //if the countdown timer is working, then refresh for each user's turn
          if (cdt != null) {
              cdt.cancel();
          }

          cdt = new CountDownTimer(50000, 1000) {
              public void onTick(long millisUntilFinished) {
                  timer.setText(msg1 + millisUntilFinished / 1000);

              }

//            final String msg = "Time up!! You lost your turn!!!";
              public void onFinish() {
//                timer.setText(msg);
                  getChessView().getChess().setHasMadeMove(true);
                  switchTurn();
              }
          }.start();
    }


    /**
     * Save the instance state into a bundle
     * @param bundle the bundle to save into
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        getChessView().saveInstanceState(bundle);
    }

    /**
     * When resign button is clicked, determine which player clicked it and end the game
     * @param view Current view
     */
    public void onResign(View view) {
        p1Wins = getChess().getBlackTurn();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ended = database.getReference(gameId + "-ended");
        ended.setValue(p1Wins);

    }

    public String getXml() {
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);

            xml.startDocument("UTF-8", true);
            getChess().saveXml("name", xml);

            xml.endDocument();

        } catch (IOException e) {
            // This won't occur when writing to a string
            return "NULL";
        }

        return writer.toString();
    }

    public void loadXml(String xml, String p1, String p2) {
        getChess().loadXml(xml);
        getChessView().invalidate();
    }
}