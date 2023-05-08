package com.example.projetnews;

import android.content.Context;

class PreferencesManager(context: Context) {
    companion object {
        val PREFS_NAME = "prefs"
        val NAV_KEY = "nav_key"
    }

    private val preferences by lazy { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var navigation: Int
        get() = preferences.getInt(NAV_KEY, -1)
        set(value) {
            preferences.edit()
                .putInt(NAV_KEY, value)
                .apply()
        }
}
