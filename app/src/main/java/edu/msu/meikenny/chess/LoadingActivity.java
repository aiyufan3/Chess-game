package edu.msu.meikenny.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
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