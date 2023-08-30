package edu.msu.meikenny.chess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import edu.msu.meikenny.chess.Cloud.Cloud;
import edu.msu.meikenny.chess.Cloud.Models.ChessUser;

public class NewUserActivity extends AppCompatActivity {
    EditText userText, passText, passConfText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        userText = (EditText)findViewById(R.id.usernameText);
        passText = (EditText)findViewById(R.id.passwordText);
        passConfText = (EditText)findViewById(R.id.passwordConfirmedText);
    }

    private boolean validateData(String user, String password, String confirmPassword) {
        if(!(user.length() > 0)) {
            userText.setError("Username is invalid");
            return false;
        }
        if(password.length() < 6 || !password.matches("^[a-zA-Z0-9]*$")) {
            passText.setError("Password is invalid");
            return false;
        }
        if(!confirmPassword.equals(password)) {
            passConfText.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void createAccount(String user, String pw) {
        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        fbAuth.createUserWithEmailAndPassword(user + "@cse476project2.com",pw).addOnCompleteListener(NewUserActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Account has been created
                    if (fbAuth.getCurrentUser() == null) {
                        Toast.makeText(NewUserActivity.this, "Could not create new account", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), GamesActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(NewUserActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onCreateUser(View view) {
        String user = userText.getText().toString();
        String pw = passText.getText().toString();
        String pwConf = passConfText.getText().toString();

        if(!validateData(user,pw,pwConf)) {
            return;
        }

        createAccount(user, pw);

        // Atempt to create new account
//        else {
//            /*
//             * Create a thread to create new user
//             */
//            new Thread(new Runnable() {
//
//                ChessUser chessUser;
//                @Override
//                public void run() {
//                    Cloud cloud = new Cloud();
//                    chessUser = cloud.CreateNewUser(user, pw);
//                    Log.d("Userid on create new user", Integer.toString(chessUser.getId()));
//                    // Username does not exist and has been signed up successfully
//                    if (chessUser.getName() != null) {
//                        Intent intent = new Intent(getApplicationContext(), GamesActivity.class);
//                        intent.putExtra("currUser", chessUser.getName());
//                        startActivity(intent);
//                        finish();
//                    }
//                    // Error with signup
//                    else {
//                        view.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                String toastMessage;
//                                switch (chessUser.getId()) {
//                                    case -2:
//                                        toastMessage = "Invalid username";
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