package com.bureng.robocoinx.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bureng.robocoinx.R;
import com.bureng.robocoinx.contract.LoginContract;
import com.bureng.robocoinx.model.db.Fingerprint;
import com.bureng.robocoinx.model.request.SignUpRequest;
import com.bureng.robocoinx.model.view.ProfileView;
import com.bureng.robocoinx.presenter.LoginPresenter;
import com.bureng.robocoinx.utils.CacheContext;
import com.bureng.robocoinx.utils.FileManager;
import com.bureng.robocoinx.utils.StaticValues;
import com.squareup.picasso.Picasso;

public class LoginActivity extends Activity implements LoginContract.View, View.OnClickListener {

    private EditText emailInput;
    private EditText passwordInput;
    private Button buttonAction;
    private String state;
    private Button buttonUp;
    private TextView forgotPass;
    private TextView labelAction;
    private LoginContract.Presenter presenter;
    private ImageView captchaImage;
    private TextView captchaLabel;
    private EditText captchaInput;
    private String captchaNet;
    private String fingerprint;

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
        captchaInput.setVisibility(View.GONE);
        captchaLabel.setVisibility(View.GONE);
        captchaImage.setVisibility(View.GONE);
    }

    private void showSignUpForm(){
        forgotPass.setVisibility(View.GONE);
        buttonAction.setText(R.string.label_sign_up);
        buttonUp.setText(R.string.label_login);
        labelAction.setText(R.string.already_account);
        state = StaticValues.STATE_SIGNUP;
        String path = "https://captchas.freebitco.in/botdetect/e/live/images/"+captchaNet+".jpeg";
        Picasso.get().load(path).into(captchaImage);
        captchaInput.setVisibility(View.VISIBLE);
        captchaLabel.setVisibility(View.VISIBLE);
        captchaImage.setVisibility(View.VISIBLE);
    }

    private void setUpView() {
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPass);
        buttonAction = findViewById(R.id.buttonAction);
        buttonUp = findViewById(R.id.buttonUp);
        forgotPass = findViewById(R.id.textViewForgotPass);
        labelAction = findViewById(R.id.labelAction);
        captchaImage = findViewById(R.id.captcha);
        captchaLabel = findViewById(R.id.labelCaptcha);
        captchaInput = findViewById(R.id.editTextCaptcha);
        showLoginForm();
        buttonAction.setOnClickListener(this);
        buttonUp.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
        this.fingerprint = new CacheContext<>(Fingerprint.class, getApplicationContext())
                .get(StaticValues.FINGERPRINT).fingerprint2;

    }

    @Override
    public void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void setCaptchaNet(String captchaNet) {
        this.runOnUiThread(() -> {
            if(captchaNet != null)
                this.captchaNet = captchaNet;
        });
    }

    @Override
    public void onClick(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String captcha = captchaInput.getText().toString();
        switch (view.getId()){
            case R.id.buttonAction:
                if(state.equalsIgnoreCase(StaticValues.STATE_LOGIN))
                    new Content(email, password, StaticValues.STATE_LOGIN, null).execute((Void) null);
                else
                    new Content(email, password, StaticValues.STATE_SIGNUP, captcha).execute((Void) null);
                break;
            case R.id.buttonUp:
                if(state.equalsIgnoreCase(StaticValues.STATE_SIGNUP))
                    showLoginForm();
                else
                    new Content(null, null, StaticValues.STATE_BEFORE_SIGNUP, null).execute((Void)null);
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
        private final String mCaptResp;

        Content(String email, String password, String state, String captResp) {
            mEmail = email;
            mPassword = password;
            mState = state;
            mCaptResp = captResp;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(StaticValues.STATE_LOGIN.equalsIgnoreCase(mState)){
                    presenter.login(getApplicationContext(), mEmail, mPassword);
                }else if (StaticValues.STATE_SIGNUP.equalsIgnoreCase(mState)){
                    SignUpRequest signupRequest = (SignUpRequest) getIntent().getSerializableExtra(StaticValues.SIGNUP_REQ);
                    assert signupRequest != null;
                    signupRequest.email = mEmail;
                    signupRequest.password = mPassword;
                    signupRequest.fingerprint = fingerprint;
                    signupRequest.captchaNet = captchaNet;
                    signupRequest.captchaResp = mCaptResp;
                    presenter.signUp(getApplicationContext(), signupRequest);
                }else if(StaticValues.STATE_BEFORE_SIGNUP.equalsIgnoreCase(mState)){
                    presenter.getCaptchaNet(fingerprint);
                }
            }catch (Exception e){
                FileManager.getInstance().appendLog(e);
                showMessage(e.getMessage());
            }
            return null;
        }

    }
}
