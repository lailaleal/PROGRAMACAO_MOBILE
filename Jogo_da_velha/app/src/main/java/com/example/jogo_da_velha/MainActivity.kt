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
    private lateinit var tvLabelPlayerX: TextView
    private lateinit var tvLabelPlayerO: TextView

    private var namePlayerX = "Jogador X"
    private var namePlayerO = "Jogador O"
    private var initialStartingPlayer = 1

    private var scoreX = 0
    private var scoreO = 0
    private var scoreDraw = 0

    private val buttons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Receber dados da SetupActivity
        namePlayerX = intent.getStringExtra("PLAYER_1_NAME") ?: "Jogador X"
        namePlayerO = intent.getStringExtra("PLAYER_2_NAME") ?: "Jogador O"
        initialStartingPlayer = intent.getIntExtra("STARTING_PLAYER", 1)
        activePlayer = initialStartingPlayer

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvStatus = findViewById(R.id.tvStatus)
        tvScoreX = findViewById(R.id.tvScoreX)
        tvScoreO = findViewById(R.id.tvScoreO)
        tvScoreDraw = findViewById(R.id.tvScoreDraw)
        tvLabelPlayerX = findViewById(R.id.tvLabelPlayerX)
        tvLabelPlayerO = findViewById(R.id.tvLabelPlayerO)

        // Configurar labels com os nomes recebidos
        tvLabelPlayerX.text = namePlayerX.uppercase()
        tvLabelPlayerO.text = namePlayerO.uppercase()

        updateScoreBoard()
        updateTurnDisplay()

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
            button.setTextColor(Color.parseColor("#D4AF37"))
            activePlayer = 2
        } else {
            button.text = "O"
            button.setTextColor(Color.WHITE)
            activePlayer = 1
        }

        updateTurnDisplay()
        checkWinner()
    }

    private fun updateTurnDisplay() {
        if (isGameActive) {
            tvStatus.text = "Vez de " + if (activePlayer == 1) namePlayerX else namePlayerO
        }
    }

    private fun checkWinner() {
        for (pos in winningPositions) {
            if (gameState[pos[0]] != 0 &&
                gameState[pos[0]] == gameState[pos[1]] &&
                gameState[pos[1]] == gameState[pos[2]]
            ) {

                isGameActive = false
                val winnerName = if (gameState[pos[0]] == 1) namePlayerX else namePlayerO
                tvStatus.text = "$winnerName Venceu!"

                if (gameState[pos[0]] == 1) scoreX++ else scoreO++
                updateScoreBoard()

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
        activePlayer = initialStartingPlayer // Volta para quem foi escolhido para começar
        isGameActive = true
        gameState = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)

        updateTurnDisplay()

        for (btn in buttons) {
            btn.text = ""
            btn.setBackgroundColor(Color.parseColor("#1A1A1A"))
        }
    }
}
