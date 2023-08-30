package edu.msu.meikenny.chess;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import edu.msu.meikenny.chess.Cloud.Cloud;
import edu.msu.meikenny.chess.Cloud.Models.ChessUser;

public class LoginActivity extends AppCompatActivity {

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String USER_KEY = "user";
    public static final String PASS_KEY = "pass";
    public static final String REMEMBER_KEY = "remember_me";
    // variable for shared preferences.
    SharedPreferences sharedpreferences;

    String user,pw;
    EditText usernameText;
    EditText passwordText;
    CheckBox check_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        usernameText = (EditText)findViewById(R.id.usernameText);
        passwordText = (EditText)findViewById(R.id.passwordText);
        check_box = findViewById(R.id.checkBox);

        if (sharedpreferences.getBoolean(REMEMBER_KEY, false)) {
            loginUser(sharedpreferences.getString(USER_KEY, ""), sharedpreferences.getString(PASS_KEY, ""));
            transitionToHome();
        }
    }

    private boolean validateData(String user, String password) {
        if(!(user.length() > 0)) {
            usernameText.setError("Username is invalid");
            return false;
        }
        if(!(password.length() > 0)) {
            passwordText.setError("Password is invalid");
            return false;
        }
        return true;
    }

    private void loginUser(String user, String pw) {
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        fbAuth.signInWithEmailAndPassword(user + "@cse476project2.com",pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Account has been created
                    Log.d("Login User", fbAuth.getCurrentUser().getEmail().replace("@cse476project2.com",""));
                    if (check_box.isChecked()) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(REMEMBER_KEY, true);
                        editor.putString(USER_KEY, user);
                        editor.putString(PASS_KEY, pw);
                        editor.apply();
                    }
                    transitionToHome();
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void transitionToHome() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(LoginActivity.this, "Could not login account", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), GamesActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onLogin(View view) {
        user = usernameText.getText().toString();
        pw = passwordText.getText().toString();

        if (!validateData(user, pw)) {
            return;
        }

        loginUser(user,pw);


        // Attempt to login
//        else {
//            /*
//             * Create a thread to login
//             */
//            new Thread(new Runnable() {
//
//                ChessUser chessUser;
//                @Override
//                public void run() {
//                    Cloud cloud = new Cloud();
//                    chessUser = cloud.Login(user, pw);
//                    Log.d("Userid ", Integer.toString(chessUser.getId()));
//                    // User logged in successfully
//                    if (chessUser.getName() != null) {
//
//                        // Update shared preferences
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//
//                        editor.putBoolean(REMEMBER_KEY, check_box.isChecked());
//                        editor.putString(USER_KEY, user);
//                        editor.putString(PASS_KEY, pw);
//
//                        editor.apply();
//
//                        Intent intent = new Intent(getApplicationContext(), GamesActivity.class);
//                        intent.putExtra("currUser", chessUser);
//                        startActivity(intent);
//                        finish();
//                    }
//                    // Error with login
//                    else {
//                        view.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                String toastMessage;
//                                switch (chessUser.getId()) {
//                                    case -2:
//                                        toastMessage = "Invalid username";
//                                        break;
//                                    case -3:
//                                        toastMessage = "Invalid password";
//                                        break;
//                                    default:
//                                        toastMessage = "ERROR";
//                                        break;
//                                }
//                                Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//            }).start();
//        }
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (!user.equals("") && !pw.equals("")) {
//            new Thread(new Runnable() {
//                ChessUser chessUser;
//                @Override
//                public void run() {
//                    Cloud cloud = new Cloud();
//                    chessUser = cloud.Login(user, pw);
//                    Log.d("Userid ", Integer.toString(chessUser.getId()));
//                    // User logged in successfully
//                    if (chessUser.getName() != null) {
//                        Intent intent = new Intent(getApplicationContext(), GamesActivity.class);
//                        intent.putExtra("currUser", chessUser);
//                        startActivity(intent);
//                        finish();
//                    }
//                    // Error with login
//                    else {
//                        // Cannot login with remembered values, just fill out the editText fields so user can press login button
//                        usernameText.setText(sharedpreferences.getString(USER_KEY,""));
//                        passwordText.setText(sharedpreferences.getString(PASS_KEY,""));
//                        check_box.setChecked(sharedpreferences.getBoolean(REMEMBER_KEY,false));
//                    }
//                }
//            }).start();
//        }
//    }
    public void onNewUser(View view) {
        Intent intent = new Intent(this, NewUserActivity.class);
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