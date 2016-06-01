package com.example.dexter.bro.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dexter.bro.R;
import com.example.dexter.bro.activity.SmartActivity;
import com.example.dexter.bro.app.Config;
import com.example.dexter.bro.app.Endpoints;
import com.example.dexter.bro.app.MyApplication;
import com.example.dexter.bro.model.User;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dexter on 5/28/2016.
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();
    public GcmIntentService() {super(TAG);}

    public static final String EMAIL = "email";
    public static final String SERVERAUTHCODE = "serverauthcode";
    public static final String IDTOKEN = "idtoken";
    public static final String NAME = "name";


    @Override
    protected void onHandleIntent(Intent intent) {
       // String EMAIL = intent.getStringExtra(EMAIL);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = null;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i("TAGG", token + intent.getStringExtra(NAME));
            //Send this token to App server
            sendGCMtokenToAppServer(intent.getStringExtra(IDTOKEN),intent.getStringExtra(SERVERAUTHCODE),intent.getStringExtra(NAME), intent.getStringExtra(EMAIL), token );

            //sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
            //sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }


    private void sendGCMtokenToAppServer(final String idToken, final String serauthcode, final String name, final String email, final String token) {
        String endPoint = Endpoints.LOGIN;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {

                        User user = new User(obj.getString("name"),obj.getString("email"));
                        String gcmtoken = obj.getString("gcmtoken");
                        MyApplication.getInstance().getPrefManager().storeUser(user);
                        MyApplication.getInstance().getPrefManager().storeGCM(gcmtoken);
                        Intent startActivity = new Intent(getBaseContext(), SmartActivity.class);
                        startActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(startActivity);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to send gcm registration id to our sever. " + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null && error.getMessage() !=null){
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();

                }

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("gcm_registration_id", token);
                params.put("email", email);
                params.put("name",name);
                params.put("server_auth_code", serauthcode);
                params.put("id_token", idToken);
                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

    }

}
