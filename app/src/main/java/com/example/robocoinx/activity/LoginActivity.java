package com.example.robocoinx.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.example.robocoinx.model.UserCache;
import com.google.gson.Gson;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout layoutSignUp;
    private LinearLayout layoutLogin;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpView();
    }

    private void setUpView() {
        TextView signupAction = findViewById(R.id.textViewSignup);
        TextView loginAction = findViewById(R.id.textViewLogin);
        Button btnLogin = findViewById(R.id.buttonLogin);
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutSignUp = findViewById(R.id.layoutSignup);
        email = findViewById(R.id.editTextEmailLogin);
        password = findViewById(R.id.editTextPassLogin);

        loginAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLogin.setVisibility(View.VISIBLE);
                layoutSignUp.setVisibility(View.INVISIBLE);
            }
        });

        signupAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLogin.setVisibility(View.INVISIBLE);
                layoutSignUp.setVisibility(View.VISIBLE);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Content(email.getText().toString(),password.getText().toString()).execute((Void)null);
            }

        });
    }

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
                Map<String, Object> result = RoboHandler.parsingLoginResponse(getApplicationContext(), mEmail, mPassword);
                if(result == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Sorry login filed!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    intent.putExtra(StaticValues.PROFILE_VIEW, (ProfileView)result.get(StaticValues.PROFILE_VIEW));
                    startActivity(intent);
                }
            }catch (Exception e){
                e.getStackTrace();
            }
            return null;
        }
    }
}
