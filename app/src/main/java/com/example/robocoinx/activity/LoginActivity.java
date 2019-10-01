package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.UserCache;

public class LoginActivity extends AppCompatActivity {

    private TextView signupAction;
    private TextView loginAction;
    private LinearLayout layoutSignup;
    private LinearLayout layoutLogin;
    private Button btnLogin;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpView();
    }

    private void setUpView() {
        signupAction = findViewById(R.id.textViewSignup);
        loginAction = findViewById(R.id.textViewLogin);
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutSignup = findViewById(R.id.layoutSignup);
        btnLogin = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.editTextEmailLogin);
        password = findViewById(R.id.editTextPassLogin);

        loginAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLogin.setVisibility(View.VISIBLE);
                layoutSignup.setVisibility(View.INVISIBLE);
            }
        });

        signupAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLogin.setVisibility(View.INVISIBLE);
                layoutSignup.setVisibility(View.VISIBLE);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCache user = RoboHandler.parsingLoginResponse(email.getText().toString(), password.getText().toString());
                if(user == null){
                    Toast.makeText(getApplicationContext(), "Sorry login filed!!", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }

        });
    }
}
