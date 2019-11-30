package com.example.robocoinx.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robocoinx.R;
import com.example.robocoinx.contract.LoginContract;
import com.example.robocoinx.model.request.SignupRequest;
import com.example.robocoinx.model.view.ProfileView;
import com.example.robocoinx.presenter.LoginPresenter;
import com.example.robocoinx.utils.FileManager;
import com.example.robocoinx.utils.StaticValues;

public class LoginActivity extends Activity implements LoginContract.View, View.OnClickListener {

    private EditText emailInput;
    private EditText passwordInput;
    private Button buttonAction;
    private String state;
    private Button buttonUp;
    private TextView forgotPass;
    private TextView labelAction;
    private LoginContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        presenter = new LoginPresenter(this);
        setUpView();
    }

    private void showLoginForm() {
        forgotPass.setVisibility(View.VISIBLE);
        buttonAction.setText(R.string.label_login);
        buttonUp.setText(R.string.label_sign_up);
        labelAction.setText(R.string.no_account);
        state = StaticValues.STATE_LOGIN;
    }

    private void showSignUpForm(){
        forgotPass.setVisibility(View.GONE);
        buttonAction.setText(R.string.label_sign_up);
        buttonUp.setText(R.string.label_login);
        labelAction.setText(R.string.already_account);
        state = StaticValues.STATE_SIGNUP;
    }

    private void setUpView() {
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPass);
        buttonAction = findViewById(R.id.buttonAction);
        buttonUp = findViewById(R.id.buttonUp);
        forgotPass = findViewById(R.id.textViewForgotPass);
        labelAction = findViewById(R.id.labelAction);
        showLoginForm();
        buttonAction.setOnClickListener(this);
        buttonUp.setOnClickListener(this);
        forgotPass.setOnClickListener(this);

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void validateInput() {
        if(emailInput.getText() == null || !emailInput.getText().toString().equals("")){
            showMessage("email cannot be empty");
        }else if(passwordInput.getText() == null || !passwordInput.getText().toString().equals("")){
            showMessage("password cannot be empty");
        }
    }

    @Override
    public void goHome(ProfileView profileView) {
        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        intent.putExtra(StaticValues.PROFILE_VIEW, profileView);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        switch (view.getId()){
            case R.id.buttonAction:
                if(state.equalsIgnoreCase(StaticValues.STATE_LOGIN))
                    new Content(email, password, StaticValues.STATE_LOGIN).execute((Void) null);
                else
                    new Content(email, password, StaticValues.STATE_SIGNUP).execute((Void) null);
                break;
            case R.id.buttonUp:
                if(state.equalsIgnoreCase(StaticValues.STATE_SIGNUP))
                    showLoginForm();
                else
                    showSignUpForm();
                break;
            case R.id.textViewForgotPass:
                // TODO
                break;
        }
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
                    presenter.login(getApplicationContext(), mEmail, mPassword);
                }else if (StaticValues.STATE_SIGNUP.equalsIgnoreCase(mState)){
                    SignupRequest signupRequest = (SignupRequest) getIntent().getSerializableExtra(StaticValues.SIGNUP_REQ);
                    assert signupRequest != null;
                    signupRequest.email = mEmail;
                    signupRequest.password = mPassword;
                    presenter.signUp(getApplicationContext(), signupRequest);
                }
            }catch (Exception e){
                FileManager.getInstance().appendLog(e);
            }
            return null;
        }

    }
}
