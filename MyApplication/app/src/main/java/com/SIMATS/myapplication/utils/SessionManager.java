package com.SIMATS.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager handles user session using SharedPreferences.
 * Stores authentication token, user ID, and login status.
 */
public class SessionManager {

    private static final String PREF_NAME = "EducationStreamAdvisorSession";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save user session after successful login.
     *
     * @param userId    User's ID from database
     * @param authToken Authentication token for API calls
     */
    public void createLoginSession(int userId, String authToken) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Save user session with additional details.
     *
     * @param userId    User's ID from database
     * @param authToken Authentication token for API calls
     * @param email     User's email
     * @param name      User's full name
     */
    public void createLoginSession(int userId, String authToken, String email, String name) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Check if user is logged in.
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get stored authentication token.
     *
     * @return Auth token or null if not logged in
     */
    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Get stored user ID.
     *
     * @return User ID or -1 if not logged in
     */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get stored user email.
     *
     * @return User email or null
     */
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get stored user name.
     *
     * @return User name or null
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    /**
     * Clear session data on logout.
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * Get authorization header value for API calls.
     *
     * @return "Bearer <token>" or null if not logged in
     */
    public String getAuthorizationHeader() {
        String token = getAuthToken();
        if (token != null) {
            return "Bearer " + token;
        }
        return null;
    }
}
