package edu.msu.meikenny.chess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import edu.msu.meikenny.chess.Cloud.Cloud;
import edu.msu.meikenny.chess.Cloud.Models.ChessUser;
import edu.msu.meikenny.chess.Cloud.Models.Load;

public class GamesActivity extends AppCompatActivity {
    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    private FirebaseUser currUser;

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String currName;
    WaitingDlg dlg = new WaitingDlg();
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_games);
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        currName = currUser.getEmail().replace("@cse476project2.com", "");
        ListView list = (ListView) findViewById(R.id.games_listView);
        Button logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                // clear the data in shared prefs.
                editor.clear();

                // below line will apply empty data to shared prefs.
                editor.apply();

                FirebaseAuth fbAuth = FirebaseAuth.getInstance();
                fbAuth.signOut();

                // starting LoginActivity after
                // clearing values in shared preferences.
                Intent i = new Intent(GamesActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        final Cloud.CatalogAdapter adapter = new Cloud.CatalogAdapter(list);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Get the id of the one we want to load
                String catId = adapter.getId(position);
                String catName = adapter.getName(position);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cloud cloud = new Cloud();

                        // Attempt to join game (i.e. set player2 to current player)

                        boolean success = cloud.JoinGame(catId, currName);
                        Log.d("BULLSHIT", catId);
                        Log.d("BULLSHIT-Name", currUser.getEmail().replace("@cse476project2.com", ""));
                        if (!success) {
                            // Could not join game, show toast
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(), "Could not join game", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Game joined successfully on server, attempt to start game
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // Get game data from server
                                    Cloud cloud = new Cloud();
                                    Load game = cloud.LoadBoard(catId);
                                    if (game == null) {
                                        // Game data could not be retrieved, show toast
                                        view.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(view.getContext(), "Could not retrieve game", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        // Game data retrieved successfully, start game
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        myRef = database.getReference(catId);
                                        myRef.setValue(currUser.getEmail().replace("@cse476project2.com", ""));

                                        Intent intent = new Intent(getApplicationContext(), ChessBoardActivity.class);
                                        intent.putExtra("GameId", game.getId());
                                        intent.putExtra("Player1", game.getPlayer1());
                                        intent.putExtra("Player2", game.getPlayer2());
                                        intent.putExtra("GameState", game.getBoard());
                                        startActivity(intent);
                                        DatabaseReference started = database.getReference(catId + "-started");
                                        started.setValue("yes");
                                        finish();
                                    }
                                }
                            }).start();
                        }
                    }
                }).start();


            }

        });

        list.setAdapter(adapter);

    }

    public void onCreateNewGame(View view) {
        // Call new game on server
        dlg.setCancelable(false);
        dlg.show(getSupportFragmentManager(), "Waiting");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                String catId = cloud.NewGame(currUser.getEmail().replace("@cse476project2.com", ""));

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference started = database.getReference(catId + "-started");
                DatabaseReference ended = database.getReference(catId + "-ended");
                ended.setValue("no");
                started.setValue("no");
                // if status not successful, could not make new game, show toast
                if (catId == null) {
                    // Could not join game, show toast
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), "Could not create new game", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // New Game was created successfully
                    // Waiting dlg box

                    started.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent intent = new Intent(getApplicationContext(), ChessBoardActivity.class);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Cloud cloud1 = new Cloud();
                                    Load game = cloud1.LoadBoard(catId);
                                    if (game == null) {
                                        // Game data could not be retrieved, show toast
                                        view.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(view.getContext(), "Could not retrieve game", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else if(snapshot.getValue().equals("yes")) {
                                        dlg.dismiss();
                                        intent.putExtra("GameId", game.getId());
                                        intent.putExtra("Player1", game.getPlayer1());
                                        intent.putExtra("Player2", game.getPlayer2());
                                        intent.putExtra("GameState", game.getBoard());
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).start();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Dismiss Waiting Dlg", "Could not dismiss dlg to join game");
                        }
                    });
                }
            }
        }).start();
    }

    public void onLogout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();

    }
}