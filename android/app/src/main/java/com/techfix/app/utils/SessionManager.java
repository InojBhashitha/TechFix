package com.techfix.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user session using SharedPreferences.
 * Stores JWT token, user details, and role for role-based navigation.
 */
public class SessionManager {

    private static final String PREF_NAME = "techfix_session";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save user session after successful login or registration.
     */
    public void saveSession(String token, Long userId, String fullName, String email, String role) {
        editor.putString(KEY_TOKEN, token);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public Long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "CUSTOMER");
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getRole());
    }

    public boolean isStaff() {
        return "STAFF".equalsIgnoreCase(getRole());
    }

    public boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(getRole());
    }

    /**
     * Clear session and log user out.
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
