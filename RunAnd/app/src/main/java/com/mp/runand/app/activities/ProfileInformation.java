package com.mp.runand.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.runand.app.R;
import com.mp.runand.app.logic.database.DataBaseHelper;
import com.mp.runand.app.logic.entities.CurrentUser;
import com.mp.runand.app.logic.network.JSONRequestBuilder;
import com.mp.runand.app.logic.network.PasswordSetter;
import com.mp.runand.app.logic.network.ProfileActualizer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProfileInformation extends Activity {

    CurrentUser currentUser;

    //user email header
    @InjectView(R.id.emailHeaderProfileInfo)
    TextView emailHeaderProfileInfo;
    //user email address
    @InjectView(R.id.profileEmailAddress)
    TextView profileEmailAddress;
    //trainer email header
    @InjectView(R.id.trainerHeaderProfileInfo)
    TextView trainerHeaderProfileInfo;
    //trainer email or none if user has not got trainer
    @InjectView(R.id.profileTrainerEmailAddress)
    TextView profileTrainerEmailAddress;
    //password header or no connection info
    @InjectView(R.id.passwordMessage)
    TextView passwordMessage;
    //password set fields
    @InjectView(R.id.password1)
    EditText password1;
    @InjectView(R.id.password1txt)
    TextView password1txt;
    @InjectView(R.id.password2txt)
    TextView password2txt;
    @InjectView(R.id.password2)
    EditText password2;
    //buttons
    @InjectView(R.id.removeTrainerButton)
    Button removeTrainerButton;
    @InjectView(R.id.setPasswordButton)
    Button setPasswordButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_information);
        ButterKnife.inject(this);
        currentUser = DataBaseHelper.getInstance(this).getCurrentUser();
        setLoggedUserInfo();
        new ProfileActualizer(this,currentUser).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.refreshProfileInformation:
                new ProfileActualizer(this,currentUser).execute();
                return true;
            case R.id.logoutProfileInfo:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update UI after logged user is entering this activity
     * hiding all fields which can be not coherent with database until web check is done
     */
    void setLoggedUserInfo(){
        setTitle(getText(R.string.user) + currentUser.getUserName());
        profileEmailAddress.setText(currentUser.getEmailAddress());
        profileEmailAddress.setText(currentUser.getEmailAddress());

        //hidding controls at start activity
        trainerHeaderProfileInfo.setVisibility(View.GONE);
        profileTrainerEmailAddress.setVisibility(View.GONE);
        passwordMessage.setVisibility(View.GONE);
        password1.setVisibility(View.GONE);
        password2.setVisibility(View.GONE);
        password1txt.setVisibility(View.GONE);
        password2txt.setVisibility(View.GONE);
        removeTrainerButton.setVisibility(View.GONE);
        setPasswordButton.setVisibility(View.GONE);
    }

    /**
     * update View if connection with server is impossible
     */
    public void updateView(){
        //hidding controls at start activity
        trainerHeaderProfileInfo.setVisibility(View.VISIBLE);
        trainerHeaderProfileInfo.setTextColor(Color.RED);
        trainerHeaderProfileInfo.setText("Aktualizacja danych nie powiodła się! \nSprawdź połączenie z internetem");
        profileTrainerEmailAddress.setVisibility(View.GONE);
        passwordMessage.setVisibility(View.GONE);
        password1.setVisibility(View.GONE);
        password2.setVisibility(View.GONE);
        password1txt.setVisibility(View.GONE);
        password2txt.setVisibility(View.GONE);
        removeTrainerButton.setVisibility(View.GONE);
        setPasswordButton.setVisibility(View.GONE);
    }

    /**
     * Update view if all data checked
     * @param isPasswordSet set to true if password for this acc is already created
     * @param isTrainerAdded set to true if user has already a trainer
     * @param trainerEmail trainer email address if user got trainer "none" if user don't have trainer
     */
    public void updateView(boolean isPasswordSet, boolean isTrainerAdded, String trainerEmail){
        if(isPasswordSet){
            passwordMessage.setText(getText(R.string.change_password_txt));
            passwordMessage.setVisibility(View.VISIBLE);
            password1.setVisibility(View.VISIBLE);
            password2.setVisibility(View.VISIBLE);
            password1txt.setVisibility(View.VISIBLE);
            password2txt.setVisibility(View.VISIBLE);
            setPasswordButton.setText(getText(R.string.change_password_btn));
            setPasswordButton.setVisibility(View.VISIBLE);
        }else{
            passwordMessage.setTextColor(Color.RED);
            passwordMessage.setText(getText(R.string.set_password_txt));
            passwordMessage.setVisibility(View.VISIBLE);
            password1.setVisibility(View.VISIBLE);
            password2.setVisibility(View.VISIBLE);
            password1txt.setVisibility(View.VISIBLE);
            password2txt.setVisibility(View.VISIBLE);
            setPasswordButton.setText(getText(R.string.set_password_btn));
            setPasswordButton.setVisibility(View.VISIBLE);
        }

        if(isTrainerAdded){
            trainerHeaderProfileInfo.setVisibility(View.VISIBLE);
            profileTrainerEmailAddress.setVisibility(View.VISIBLE);
            profileTrainerEmailAddress.setText(trainerEmail);
            removeTrainerButton.setVisibility(View.VISIBLE);
        }else{
            trainerHeaderProfileInfo.setVisibility(View.VISIBLE);
            profileTrainerEmailAddress.setText(getText(R.string.no_trainer_profile_info));
        }
    }

    /**
     * updating view after password change
     * @param status
     */
    public void updateView(boolean status){
        if(status){
            trainerHeaderProfileInfo.setTextColor(Color.BLACK);
            trainerHeaderProfileInfo.setText(getText(R.string.change_password_txt));
        }
    }

    /**
     * logout user
     */
    private void logout(){
        DataBaseHelper.getInstance(this).deleteCurrentUser();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    /**
     * begin password change
     */
    @OnClick(R.id.setPasswordButton)
    public void setPassword(){
        String p1 = password1.getText().toString();
        String p2 = password2.getText().toString();

        if(validatePasswords(p1, p2)){
            new PasswordSetter(this,currentUser).execute(JSONRequestBuilder.buildSetPasswordRequestAsJson(p1));
        }
    }

    /**
     * password validation
     * @param p1 password
     * @param p2 repeated password
     * @return true is validatet otherwise false
     */
    boolean validatePasswords(String p1, String p2){
        if(p1.length()<5 || p2.length()<5){
            Toast.makeText(this,getText(R.string.password_too_short),Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!p1.equals(p2)){
            Toast.makeText(this,getText(R.string.passwords_must_be_identical),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
