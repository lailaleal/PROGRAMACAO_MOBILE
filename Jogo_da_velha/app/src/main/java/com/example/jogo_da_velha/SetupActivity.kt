package com.example.jogo_da_velha

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setup)

        val etPlayer1 = findViewById<TextInputEditText>(R.id.etPlayer1)
        val etPlayer2 = findViewById<TextInputEditText>(R.id.etPlayer2)
        val rbPlayer1 = findViewById<RadioButton>(R.id.rbPlayer1)
        val btnStart = findViewById<Button>(R.id.btnStartGame)

        btnStart.setOnClickListener {
            val name1 = etPlayer1.text.toString().ifBlank { "Jogador 1" }
            val name2 = etPlayer2.text.toString().ifBlank { "Jogador 2" }
            val startingPlayer = if (rbPlayer1.isChecked) 1 else 2

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("PLAYER_1_NAME", name1)
                putExtra("PLAYER_2_NAME", name2)
                putExtra("STARTING_PLAYER", startingPlayer)
            }
            startActivity(intent)
            finish()
        }
    }
}
