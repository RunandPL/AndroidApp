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
import com.mp.runand.app.logic.database.CurrentUserDAO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class Login extends Activity implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    @InjectView(R.id.login) Button buttonLogin;
    @InjectView(R.id.email) EditText editTextEmail;
    @InjectView(R.id.passwordReset) Button buttonPasswordReset;
    @InjectView(R.id.password) EditText editTextPassword;
    @InjectView(R.id.btn_sign_in) SignInButton googleButton;

    @InjectView(R.id.button) Button newButton;
    @InjectView(R.id.button2) Button newButton2;

    //needed for g+ api
    private GoogleApiClient mGoogleApiClient;
    //A flag indicating that a PendingIntent is in progress and prevents us
    //from starting further intents.
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private static final int RC_SIGN_IN = 0;

    private ConnectionResult mConnectionResult;

    //needed to email validation
    Matcher emailMatcher;

    public static final Pattern VALID_EMAIL_ADDRESS_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    Person currentPerson;
    String personName;
    String personPhotoUrl;
    String personGooglePlusProfile;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_login);
        //injecting controls to view
        ButterKnife.inject(this);

        updateUI(false);//read data from db in the future now default = not logged

        //initialize google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, new Plus.PlusOptions.Builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        //setting button on click listeners
        buttonLogin.setOnClickListener(this);
        buttonPasswordReset.setOnClickListener(this);
        googleButton.setOnClickListener(this);
            //tmpbutton for map checking
            newButton.setVisibility(View.GONE);
            newButton.setOnClickListener(this);
            newButton2.setVisibility(View.GONE);
            newButton2.setOnClickListener(this);
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
                signInWithGplus();
                break;
            case R.id.login:
                // Classic log in
                performNormalLogging();

                break;
            //to remove after testing
                        case R.id.button:
                            //redirect to map view
                            double[] table = new double[]{21,45,32,45,67,12};
                            Intent intent = new Intent(getBaseContext(), MapLook.class);
                            intent.putExtra("POSITIONS", table);
                            startActivity(intent);
                            break;

                        case R.id.button2:
                            signOutFromGplus();
                            updateUI(false);
                            break;
                        case R.id.passwordReset:
                            Toast.makeText(this,
                                    "Name: " + personName + ", plusProfile: "
                                            + personGooglePlusProfile + ", email: " + email
                                            + ", Image: " + personPhotoUrl,
                                    Toast.LENGTH_LONG).show();

            ////////////////////////
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        // Get user's information
        getProfileInformation();
        //Add data to db as logged user probably here
        if (email != null){
            //todo pass data to server get token
            int sessionId = 1;
            CurrentUserDAO cu = new CurrentUserDAO(personName, sessionId, email, this);
        }

        // Update the UI after signing wont be used later
        updateUI(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        updateUI(false);
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

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    private void performNormalLogging(){
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
                    "Email field is required", Toast.LENGTH_SHORT).show();
        } else if (passwordTxt.equals("")){
            Toast.makeText(getApplicationContext(),
                    "Password field is required", Toast.LENGTH_SHORT).show();
        } else if (!emailIsValid){
            Toast.makeText(getApplicationContext(),
                    "Incorrect email", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     * */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            googleButton.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.GONE);
            newButton.setVisibility(View.VISIBLE);
            buttonPasswordReset.setVisibility(View.GONE);
            newButton2.setVisibility(View.VISIBLE);
        } else {
            googleButton.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.VISIBLE);
            newButton.setVisibility(View.GONE);
            buttonPasswordReset.setVisibility(View.VISIBLE);
            newButton2.setVisibility(View.GONE);
        }
    }

    /**
     * Method to resolve any signin errors
     * */
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
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }

        if (!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                personName = currentPerson.getDisplayName();
                personPhotoUrl = currentPerson.getImage().getUrl();
                personGooglePlusProfile = currentPerson.getUrl();
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e("LOGIN.JAVA", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                Toast.makeText(getApplicationContext(),
                        "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Revoking access from google
     * */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e("LOGIN.JAVA", "User access revoked!");
                            mGoogleApiClient.connect();
                            updateUI(false);
                        }
                    });
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            currentPerson = null;
            personName = "";
            personPhotoUrl = "";
            personGooglePlusProfile = "";
            email = "";

            updateUI(false);
        }
    }
}
