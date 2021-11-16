package edu.pucmm.ecommerceapp.database.sessions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import edu.pucmm.ecommerceapp.MainActivity;

public class UserSession {

    // Shared Preferences
    private SharedPreferences sharedPreferences;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;
    // Context
    private Context context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // SharedPreferences file name
    private static final String PREF_NAME = "userSessionPref";

    // User  (make variable public to access from outside)
    public static final String USER = "user";

    public UserSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(final String username) {

        editor.putString(USER, username);
        // commit changes
        editor.commit();
    }

    public void checkLogin() {
        // Check login status
        if (get(USER) == null) {
            // user is not logged in redirect him to Login Activity
            Intent intent = new Intent(context, MainActivity.class);
            // Closing all the Activities
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            context.startActivity(intent);
        }
    }

    public boolean isLoggedIn() {
        if (get(USER) == null) {
            return false;
        }else {
            return true;
        }
    }

    public void logout() {
        editor.putString(USER, null);
        // commit changes
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public <T> T get(String key) {
        return (T) sharedPreferences.getString(key, null);
    }

    public void saveUserLocally(String key, String value) {
        editor.putString(key, value);
    }

}
