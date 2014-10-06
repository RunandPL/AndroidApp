package com.mp.runand.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mp.runand.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class Login extends Activity {

    @InjectView(R.id.login) Button buttonLogin;
    @InjectView(R.id.email) EditText editTextEmail;
    @InjectView(R.id.passwordReset) Button buttonPasswordReset;
    @InjectView(R.id.password) EditText editTextPassword;

    Matcher emailMatcher;

    private final static String KEY_SUCCESS = "SUCCESS";
    private final static String KEY_UID = "UID";
    private final static String KEY_USERNAME = "USER_NAME";
    private final static String KEY_EMAIL = "EMAIL";
    private final static String KEY_CREATED_AT = "CREATED_AT";
    public static final Pattern VALID_EMAIL_ADDRESS_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_login);
        //injecting controls to view
        ButterKnife.inject(this);

        /**
         * OnClick
         * Logic for password reset
         */
        buttonPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo logic for password reset
            }
        });

        /**
         * OnClick
         * Logic for log in
         * Data validation
         */
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailMatcher = VALID_EMAIL_ADDRESS_PATTERN.matcher(editTextEmail.getText().toString());
                String emailTxt = editTextEmail.getText().toString();
                boolean emailIsValid = emailMatcher.find();
                String passwordTxt = editTextPassword.getText().toString();

                if (emailIsValid && !passwordTxt.equals("")){
                    //todo networkCheck(view);
                } else if (emailTxt.equals("") && passwordTxt.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Email and Password fields are required", Toast.LENGTH_SHORT).show();
                } else if (emailTxt.equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Email field is requiredaksdjaksdj", Toast.LENGTH_SHORT).show();
                } else if (passwordTxt.equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Password field is required", Toast.LENGTH_SHORT).show();
                } else if (!emailIsValid){
                    Toast.makeText(getApplicationContext(),
                            "Incorrect email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
