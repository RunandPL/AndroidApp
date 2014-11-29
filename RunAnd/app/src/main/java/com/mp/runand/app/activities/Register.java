package com.mp.runand.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.network.JSONRequestBuilder;
import com.mp.runand.app.logic.network.LoggingManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class Register extends Activity {

    @InjectView(R.id.email)
    EditText editTextEmail;
    @InjectView(R.id.password)
    EditText editTextPassword;
    @InjectView(R.id.password2)
    EditText editTextPassword2;
    @InjectView(R.id.register)
    Button register;

    Matcher emailMatcher;
    public static final Pattern VALID_EMAIL_ADDRESS_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_register);
        //injecting controls to view
        ButterKnife.inject(this);
    }

    /**
     * begin registering
     */
    @OnClick(R.id.register)
    public void rejestruj() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String password2 = editTextPassword2.getText().toString();

        if (validateEmail(email) && comparePasswords(password, password2) && validatePassword(password)) {
            new LoggingManager(this, true).execute(
                    JSONRequestBuilder.buildRegisterRequestAsJson(
                            email,
                            password));
            //Toast.makeText(this,"register",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Email validation
     *
     * @param email email to valadate
     * @return true if ok else false
     */
    private boolean validateEmail(String email) {
        emailMatcher = VALID_EMAIL_ADDRESS_PATTERN.matcher(email);
        if (email.equals("")) {
            Toast.makeText(this, getText(R.string.email_is_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (emailMatcher.find()) {
            return true;
        } else {
            Toast.makeText(this, getText(R.string.email_is_not_valid), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * check if both passwords are identical
     *
     * @param password1 password1
     * @param password2 repeated password
     * @return true if ok else false
     */
    private boolean comparePasswords(String password1, String password2) {
        if (password1.equals("")) {
            Toast.makeText(this, getText(R.string.password_is_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password1.equals(password2)) {
            Toast.makeText(this, getText(R.string.passwords_must_be_identical), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * password validation
     *
     * @param password password
     * @return true if ok else false
     */
    boolean validatePassword(String password) {
        if (password.length() > 4) {
            return true;
        } else {
            Toast.makeText(this, getText(R.string.password_too_short), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
