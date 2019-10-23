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
import com.example.robocoinx.model.request.SignupRequest;
import com.example.robocoinx.model.view.ProfileView;
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
        Button btnSignUp = findViewById(R.id.buttonSignup);
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutSignUp = findViewById(R.id.layoutSignup);
        email = findViewById(R.id.editTextEmailLogin);
        password = findViewById(R.id.editTextPassLogin);

        showLoginForm();
        loginAction.setOnClickListener(v -> showLoginForm());

        signupAction.setOnClickListener(v -> showSignUpForm());

        btnLogin.setOnClickListener(v ->
                {
                    String emaiVal = email.getText().toString();
                    String passVal = password.getText().toString();
                    String input = validateInput(emaiVal, passVal);
                    if(input == null) new Content(emaiVal, passVal, StaticValues.STATE_LOGIN).execute((Void) null);
                    else runOnUiThread(() -> Toast.makeText(getApplicationContext(), input, Toast.LENGTH_SHORT).show());
                }
        );
        btnSignUp.setOnClickListener(v ->
                {
                    String emaiVal = email.getText().toString();
                    String passVal = password.getText().toString();
                    String input = validateInput(emaiVal, passVal);
                    if(input == null) new Content(emaiVal, passVal, StaticValues.STATE_SIGNUP).execute((Void) null);
                    else runOnUiThread(() -> Toast.makeText(getApplicationContext(), input, Toast.LENGTH_SHORT).show());
                }
        );
    }

    private String validateInput(String email, String pass) {
        if(email == null || email.length() == 0) return "Please..";
        if(pass == null || pass.length() == 0) return "Please..";
        if(!email.contains("@")) return "Please..";
        if(pass.length() < 8) return "please..";
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    public class Content extends AsyncTask<Void, Void, Void> {

        private final String mEmail;
        private final String mPassword;
        private final String mState;

        Content(String email, String password, String state) {
            mEmail = email;
            mPassword = password;
            mState = state;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(StaticValues.STATE_LOGIN.equalsIgnoreCase(mState)){
                    Object obj = RoboHandler.parsingLoginResponse(getApplicationContext(), mEmail, mPassword);
                    showView(obj);
                }else if (StaticValues.STATE_SIGNUP.equalsIgnoreCase(mState)){
                    SignupRequest signupRequest = (SignupRequest) getIntent().getSerializableExtra(StaticValues.SIGNUP_REQ);
                    signupRequest.email = mEmail;
                    signupRequest.password = mPassword;
                    Object obj = RoboHandler.parsingSignUpResponse(getApplicationContext(), signupRequest);
                    showView(obj);
                }

            }catch (Exception e){
                FileManager.getInstance().appendLog(e);
            }
            return null;
        }

        private void showView(Object obj) {
            if(obj instanceof ProfileView){

                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                intent.putExtra(StaticValues.PROFILE_VIEW, (ProfileView)obj);
                startActivity(intent);
            }else {
                // TODO show error
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), (String)obj, Toast.LENGTH_SHORT).show());
            }
        }
    }
}
