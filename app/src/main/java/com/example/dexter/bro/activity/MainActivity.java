package com.example.dexter.bro.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dexter.bro.R;
import com.example.dexter.bro.app.Config;
import com.example.dexter.bro.app.Endpoints;
import com.example.dexter.bro.app.MyApplication;
import com.example.dexter.bro.gcm.GcmIntentService;
import com.example.dexter.bro.helper.MyPreferenceManager;
import com.example.dexter.bro.model.User;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int RC_SIGN_IN = 9001;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    GoogleApiClient mGoogleApiClient;
    MyPreferenceManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = new MyPreferenceManager(getApplicationContext());
        //Check against the Sharedpreference
        if (pref.isLoggedIn()) {
            loggedIn();
        }

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Config.SERVER_CLIENT_ID)
                .requestProfile()
                .requestId()
                .requestServerAuthCode(Config.SERVER_CLIENT_ID,false)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void loggedIn(){
        Intent intent = new Intent(MainActivity.this, SmartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.i("SB", acct.getDisplayName() + " : " + acct.getId() + " : " + acct.getEmail() + " : " + acct.getServerAuthCode() + " : " + acct.getIdToken() + " : " + acct.getPhotoUrl() + " : " + acct.getServerAuthCode() + " : " + acct.getGrantedScopes());

            final String g_id = acct.getId();
            final String name = acct.getDisplayName();
            final String email = acct.getEmail();
            final String ServerAuthCode = acct.getServerAuthCode();
            final String IdToken = acct.getIdToken();

            Intent register = new Intent(this, GcmIntentService.class);

            register.putExtra(GcmIntentService.GID, g_id);
            register.putExtra(GcmIntentService.NAME, name);
            register.putExtra(GcmIntentService.EMAIL, email);
            register.putExtra(GcmIntentService.SERVERAUTHCODE, ServerAuthCode);
            register.putExtra(GcmIntentService.IDTOKEN, IdToken);

            startService(register);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
