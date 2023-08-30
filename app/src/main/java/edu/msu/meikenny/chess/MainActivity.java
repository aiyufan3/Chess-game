package edu.msu.meikenny.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText p1;
    EditText p2;

    private String player1;
    private String player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String getPlayer1() {
        return this.player1;
    }

    public String getPlayer2() {
        return this.player2;
    }

    public void onStartChess(View view) {

        p1 = findViewById(R.id.editTextPlayer1);
        p2 = findViewById(R.id.editTextPlayer2);
        player1 = p1.getText().toString();
        player2 = p2.getText().toString();

        if (player1.matches("")) {
            player1 = "Player 1";
        }

        if (player2.matches("")) {
            player2 = "Player 2";
        }

        Intent intent = new Intent(this, ChessBoardActivity.class);

        intent.putExtra("Player1", player1);
        intent.putExtra("Player2", player2);
        startActivity(intent);

        finish();
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
}