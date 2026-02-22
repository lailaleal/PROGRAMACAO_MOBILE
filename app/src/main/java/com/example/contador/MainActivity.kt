package com.example.contador

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private lateinit var tvCounter: TextView
    private lateinit var btnAumentar: Button
    private lateinit var btnDiminuir: Button
    private lateinit var btnResetar: Button

    private var contador = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        tvCounter = findViewById(R.id.tvCounter)
        btnAumentar = findViewById(R.id.btnAumentar)
        btnDiminuir = findViewById(R.id.btnDiminuir)
        btnResetar = findViewById(R.id.btnResetar)

        tvCounter.text = contador.toString()
        btnDiminuir.isEnabled = false

        btnAumentar.setOnClickListener {
            contador++
            tvCounter.text = contador.toString()
            btnDiminuir.isEnabled = true
        }

        btnDiminuir.setOnClickListener {
            if (contador > 0) {
                contador--
                tvCounter.text = contador.toString()
            }

            btnDiminuir.isEnabled = contador > 0
        }

        btnResetar.setOnClickListener {

            val dialog = AlertDialog.Builder(this)

            dialog.setTitle("Confirmar")
            dialog.setMessage("Zerar o contador?")

            dialog.setPositiveButton("Sim") { _, _ ->
                contador = 0
                tvCounter.text = contador.toString()
                btnDiminuir.isEnabled = false
            }

            dialog.setNegativeButton("Não") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

            dialog.show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}