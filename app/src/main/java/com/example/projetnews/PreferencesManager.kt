package com.example.projetnews;

import android.content.Context;

class PreferencesManager(context: Context) {
    companion object {
        val PREFS_NAME = "prefs"    // Nom du fichier de préférences
        val NAV_KEY = "nav_key"     // Clé pour la valeur de navigation
    }

    // Lazy initialization pour accéder aux préférences partagées
    private val preferences by lazy { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    // Propriété pour obtenir ou définir la valeur de navigation
    var navigation: Int
        get() = preferences.getInt(NAV_KEY, -1) // Obtient la valeur de navigation en utilisant la clé NAV_KEY, -1 étant la valeur par défaut
        set(value) {
            preferences.edit()
                .putInt(NAV_KEY, value)                 // Définit la valeur de navigation en utilisant la clé NAV_KEY
                .apply()                                // Applique les modifications
        }
}
