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
import com.example.dexter.bro.helper.MyPreferenceManager;
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

    public static final String GID = "gid";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String SERVERAUTHCODE = "serverauthcode";
    public static final String IDTOKEN = "idtoken";



    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String gcmtoken= null;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            gcmtoken = instanceID.getToken(getString(R.string.gcm_defaultSenderId),GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            String name = intent.getStringExtra(NAME);
            String email = intent.getStringExtra(EMAIL);
            String gid = intent.getStringExtra(GID);
            String serverauthtoken = intent.getStringExtra(SERVERAUTHCODE);
            String idtoken = intent.getStringExtra(IDTOKEN);

            //Send GCM Token to App Server
            sendGCMtokenToAppServer(name, email, gid, serverauthtoken, idtoken, gcmtoken);

        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
        }
    }


    private void sendGCMtokenToAppServer(final String name, final String email, final String gid, final String serverauthtoken, final String idtoken, final String gcmtoken) {
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
                        JSONObject data = obj.getJSONObject("data");

                        String name = data.getString("name");
                        String gcmtoken = data.getString("gcmtoken");
                        String email = data.getString("email");
                        String gid = data.getString("gid");

                        User user = new User(gid,name, email, gcmtoken);
                        MyPreferenceManager pref = new MyPreferenceManager(getApplicationContext());

                        pref.storeUser(user);
                        Toast.makeText(getApplicationContext(),obj.getString("message") , Toast.LENGTH_LONG).show();
                        Intent startActivity = new Intent(getBaseContext(), SmartActivity.class);
                        startActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(startActivity);
                    } else {
                        Toast.makeText(getApplicationContext(),"GCM could not be sent to Application Server" + obj.getString("message") , Toast.LENGTH_LONG).show();
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
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                }

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if(gid!=null) params.put("gid", gid);
                if(name!=null)params.put("name", name);
                if(email!=null)params.put("email", email);
                if(gcmtoken!=null)params.put("gcmtoken",gcmtoken);
                if(idtoken!=null)params.put("idtoken", idtoken);
                if(serverauthtoken!=null)params.put("serverauthcode", serverauthtoken);
                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

    }

}
