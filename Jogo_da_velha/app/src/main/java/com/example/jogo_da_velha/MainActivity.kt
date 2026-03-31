package com.example.jogo_da_velha

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var activePlayer = 1
    private var gameState = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

    private val winningPositions = arrayOf(
        intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
    )

    private var isGameActive = true

    private lateinit var tvStatus: TextView
    private lateinit var tvScoreX: TextView
    private lateinit var tvScoreO: TextView
    private lateinit var tvScoreDraw: TextView

    private var scoreX = 0
    private var scoreO = 0
    private var scoreDraw = 0

    private val buttons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvStatus = findViewById(R.id.tvStatus)
        tvScoreX = findViewById(R.id.tvScoreX)
        tvScoreO = findViewById(R.id.tvScoreO)
        tvScoreDraw = findViewById(R.id.tvScoreDraw)

        // Inicializar placar com 0
        updateScoreBoard()

        for (i in 0..8) {
            val resID = resources.getIdentifier("btn$i", "id", packageName)
            val btn = findViewById<Button>(resID)
            buttons.add(btn)

            btn.setOnClickListener {
                onBoardClick(btn, i)
            }
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            resetGame()
        }
    }

    private fun onBoardClick(button: Button, index: Int) {
        if (gameState[index] != 0 || !isGameActive) return

        gameState[index] = activePlayer

        if (activePlayer == 1) {
            button.text = "X"
            button.setTextColor(Color.parseColor("#D4AF37")) // Dourado do layout
            tvStatus.text = "Vez do Jogador O"
            activePlayer = 2
        } else {
            button.text = "O"
            button.setTextColor(Color.WHITE)
            tvStatus.text = "Vez do Jogador X"
            activePlayer = 1
        }

        checkWinner()
    }

    private fun checkWinner() {
        for (pos in winningPositions) {
            if (gameState[pos[0]] != 0 &&
                gameState[pos[0]] == gameState[pos[1]] &&
                gameState[pos[1]] == gameState[pos[2]]
            ) {

                isGameActive = false
                val winner = if (gameState[pos[0]] == 1) "Jogador X" else "Jogador O"
                tvStatus.text = "$winner Venceu!"

                if (gameState[pos[0]] == 1) scoreX++ else scoreO++
                updateScoreBoard()

                // Destacar botões vencedores
                for (i in pos) {
                    buttons[i].setBackgroundColor(Color.parseColor("#33D4AF37"))
                }
                return
            }
        }

        if (!gameState.contains(0)) {
            isGameActive = false
            tvStatus.text = "Empate!"
            scoreDraw++
            updateScoreBoard()
        }
    }

    private fun updateScoreBoard() {
        tvScoreX.text = scoreX.toString()
        tvScoreO.text = scoreO.toString()
        tvScoreDraw.text = scoreDraw.toString()
    }

    private fun resetGame() {
        activePlayer = 1
        isGameActive = true
        gameState = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

        tvStatus.text = "Vez do Jogador X"

        for (btn in buttons) {
            btn.text = ""
            btn.setBackgroundColor(Color.parseColor("#1A1A1A")) // Cor original do botão
        }
    }
}
