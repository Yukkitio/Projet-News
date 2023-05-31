package com.example.projetnews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    // Déclaration paresseuse des vues
    private val bottomNavigationView by lazy { findViewById<BottomNavigationView>(R.id.bottomNavigationView) }
    private val preferences by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setOnItemSelectedListener {
            // Enregistrer l'ID de l'élément de navigation sélectionné dans les préférences
            preferences.navigation = it.itemId

            // Charger le fragment correspondant à l'élément de navigation sélectionné
            when(it.itemId) {
                R.id.bottom_nav_menu_1 -> {
                    pushFragment(BaseFragment())
                    true
                }
                R.id.bottom_nav_menu_2 -> {
                    pushFragment(SearchFragment())
                    true
                }
                else -> true
            }
        }
        // Sélectionner l'élément de navigation enregistré précédemment
        bottomNavigationView.selectedItemId = preferences.navigation
    }
    private fun pushFragment(fragment: Fragment){
        // Remplacer le fragment actuel par le fragment donné
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}