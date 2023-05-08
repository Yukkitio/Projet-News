package com.example.projetnews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private val bottomNavigationView by lazy { findViewById<BottomNavigationView>(R.id.bottomNavigationView) }
    private val preferences by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setOnItemSelectedListener {
            preferences.navigation = it.itemId

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
        bottomNavigationView.selectedItemId = preferences.navigation
    }
    private fun pushFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}