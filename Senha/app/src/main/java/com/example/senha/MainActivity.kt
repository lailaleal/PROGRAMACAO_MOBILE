package com.example.senha

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Fragment inicial
        if (savedInstanceState == null) {
            replaceFragment(GeradorFragment())
        }

        bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_gerador -> {
                    replaceFragment(GeradorFragment())
                    true
                }

                R.id.nav_historico -> {
                    replaceFragment(HistoricoFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}