package com.example.dexter.bro.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.dexter.bro.model.User;

/**
 * Created by Dexter on 5/28/2016.
 */
public class MyPreferenceManager {
    private String TAG = MyPreferenceManager.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "bro_prefmanager";

    private static final String KEY_GID = "gid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GCMTOKEN = "gcmtoken";

    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void storeUser(User user) {
        editor.putString(KEY_GID, user.getGid());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_GCMTOKEN, user.getGcmtoken());

        editor.commit();
        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }


    public void clear() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        if(pref.getString(KEY_GID,null)!=null){
            return true;
        }
        return false;
    }
    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public String getEmail(){
        return pref.getString(KEY_EMAIL,null);
    }

    public String getKeyGcmtoken(){
        return pref.getString(KEY_GCMTOKEN,null);
    }

}
