package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mp.runand.app.R;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.mapsServices.GpsService;
import com.mp.runand.app.logic.network.JSONRequestBuilder;
import com.mp.runand.app.logic.network.LoggingManager;
import com.mp.runand.app.logic.training.TrainingConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class Login extends Activity
        implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    @InjectView(R.id.login)
    Button buttonLogin;
    @InjectView(R.id.email)
    EditText editTextEmail;
    @InjectView(R.id.password)
    EditText editTextPassword;
    @InjectView(R.id.btn_sign_in)
    SignInButton googleButton;
    @InjectView(R.id.skipLogin)
    Button skipLogin;
    @InjectView(R.id.register)
    Button register;

    @InjectView(R.id.notLoggedTraining)
    Button notLoginTrainingButton;

    @OnClick(R.id.notLoggedTraining)
    void notLoginOnClick(Button button) {
        Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
        intent.putExtra(TrainingConstants.IS_USER_LOGGED_IN, false);
        intent.putExtra(TrainingConstants.IS_ROUTE_TRAINING, false);
        startService(new Intent(this, GpsService.class));
        startActivity(intent);
    }

    //needed for g+ api
    private GoogleApiClient mGoogleApiClient;
    //A flag indicating that a PendingIntent is in progress and prevents us
    //from starting further intents.
    private boolean mIntentInProgress;
    private boolean mSignInClicked = false;
    private static final int RC_SIGN_IN = 0;

    private ConnectionResult mConnectionResult;

    //needed to email validation
    Matcher emailMatcher;

    CurrentUser currentUser;

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

        currentUser = null;
        //initialize google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, new Plus.PlusOptions.Builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mGoogleApiClient.connect();
        //setting button on click listeners
        buttonLogin.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        skipLogin.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        //do nothing in this activity
        //otherwise it will close app
        //don't add in another activities
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                // Sign in button clicked
                mSignInClicked = true;
                resolveSignInError();
                break;
            case R.id.login:
                // Classic log in
                performNormalLogging();
                break;
            case R.id.skipLogin:
                skipLogin();
                break;
            case R.id.register:
                openRegisterActivity();
                break;
        }
    }

    public void skipLogin(){
        //log in as anonymous user
        DataBaseHelper db = DataBaseHelper.getInstance(this);
        db.addCurrentUser(new CurrentUser());
        //change activity
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mSignInClicked) {
            signInWithGplus();
        }
        mSignInClicked = false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void performNormalLogging() {
        emailMatcher = VALID_EMAIL_ADDRESS_PATTERN.matcher(editTextEmail.getText().toString());
        String emailTxt = editTextEmail.getText().toString();
        boolean emailIsValid = emailMatcher.find();
        String passwordTxt = editTextPassword.getText().toString();

        if (emailIsValid && !passwordTxt.equals("")) {
            signInWithEmail(emailTxt, passwordTxt);
        } else if (emailTxt.equals("") && passwordTxt.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getText(R.string.email_and_password_are_required), Toast.LENGTH_SHORT).show();
        } else if (emailTxt.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getText(R.string.email_is_required), Toast.LENGTH_SHORT).show();
        } else if (passwordTxt.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getText(R.string.password_is_required), Toast.LENGTH_SHORT).show();
        } else if (!emailIsValid) {
            Toast.makeText(getApplicationContext(),
                    getText(R.string.email_is_not_valid), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Sign-in to our system with google plus data
     */
    private void signInWithGplus() {
        // Get user's information
        currentUser = getProfileInformation();
        revokeGplusAccess();
        //Add data to db as logged user probably here1
        if (currentUser != null && currentUser.getEmailAddress() != null) {
            new LoggingManager(this,true).execute(
                    JSONRequestBuilder.buildGPlusLogInRequestAsJson(
                            currentUser.getEmailAddress().split("@")[0],
                            currentUser.getEmailAddress()));
        }
    }

    private void signInWithEmail(String email, String password){
        new LoggingManager(this,true).execute(
                JSONRequestBuilder.buildLogInRequestAsJson(
                        email,
                        password,
                        email.split("@")[0]));
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private CurrentUser getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String login = Plus.AccountApi.getAccountName(mGoogleApiClient);
                return new CurrentUser(
                        currentPerson.getDisplayName(),
                        null,
                        login);
            } else {
                Toast.makeText(getApplicationContext(),
                        getText(R.string.can_not_get_G_data), Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Revoking access from google
     * full logout
     */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e("LOGIN.JAVA", "User access revoked!");
                            mGoogleApiClient.connect();
                        }
                    });
        }
    }

    /**
     * change activity to register
     */
    private void openRegisterActivity(){
        Intent i = new Intent(this, Register.class);
        this.startActivity(i);
    }
}
