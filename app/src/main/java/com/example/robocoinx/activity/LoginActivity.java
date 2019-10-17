package com.example.robocoinx.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.StaticValues;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout layoutSignUp;
    private LinearLayout layoutLogin;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_login);
        setUpView();
    }

    private void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void showLoginForm() {
        layoutLogin.setVisibility(View.VISIBLE);
        layoutSignUp.setVisibility(View.INVISIBLE);
    }

    private void showSignUpForm(){
        layoutLogin.setVisibility(View.INVISIBLE);
        layoutSignUp.setVisibility(View.VISIBLE);
    }

    private void setUpView() {
        TextView signupAction = findViewById(R.id.textViewSignup);
        TextView loginAction = findViewById(R.id.textViewLogin);
        Button btnLogin = findViewById(R.id.buttonLogin);
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutSignUp = findViewById(R.id.layoutSignup);
        email = findViewById(R.id.editTextEmailLogin);
        password = findViewById(R.id.editTextPassLogin);

        showLoginForm();
        loginAction.setOnClickListener(v -> showLoginForm());

        signupAction.setOnClickListener(v -> showSignUpForm());

        btnLogin.setOnClickListener(v -> new Content(email.getText().toString(),password.getText().toString()).execute((Void)null));
    }

    @SuppressLint("StaticFieldLeak")
    public class Content extends AsyncTask<Void, Void, Void> {

        private final String mEmail;
        private final String mPassword;

        Content(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Object obj = RoboHandler.parsingLoginResponse(getApplicationContext(), mEmail, mPassword);
                if(obj instanceof ProfileView){

                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    intent.putExtra(StaticValues.PROFILE_VIEW, (ProfileView)obj);
                    startActivity(intent);
                }else {
                    // TODO show error
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), (String)obj, Toast.LENGTH_SHORT).show());
                }
            }catch (Exception e){
//                FileManager.getInstance().appendLog(e);
            }
            return null;
        }
    }
}
