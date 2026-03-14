package com.example.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.Locale
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    private var expression = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.display)

        tvExpression.text = ""
        tvResult.text = "0"

        setupButtons()
    }

    private fun setupButtons() {
        val numbers = listOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9", R.id.btnDot to ".", R.id.btnExp to "E"
        )
        for ((id, value) in numbers) {
            findViewById<Button>(id).setOnClickListener { appendToExpression(value) }
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener { appendToExpression("+") }
        findViewById<Button>(R.id.btnSub).setOnClickListener { appendToExpression("-") }
        findViewById<Button>(R.id.btnMul).setOnClickListener { appendToExpression("*") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { appendToExpression("/") }
        findViewById<Button>(R.id.btnOpen).setOnClickListener { appendToExpression("(") }
        findViewById<Button>(R.id.btnClose).setOnClickListener { appendToExpression(")") }

        findViewById<Button>(R.id.btnSin).setOnClickListener { appendToExpression("sin(") }
        findViewById<Button>(R.id.btnCos).setOnClickListener { appendToExpression("cos(") }
        findViewById<Button>(R.id.btnTan).setOnClickListener { appendToExpression("tan(") }
        findViewById<Button>(R.id.btnLog).setOnClickListener { appendToExpression("log(") }
        findViewById<Button>(R.id.btnSqrt).setOnClickListener { appendToExpression("sqrt(") }
        findViewById<Button>(R.id.btnPi).setOnClickListener { appendToExpression("π") }
        findViewById<Button>(R.id.btnSquare).setOnClickListener { appendToExpression("^2") }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            expression = ""
            tvExpression.text = ""
            tvResult.text = "0"
        }

        findViewById<Button>(R.id.btnDel).setOnClickListener {
            if (expression.isNotEmpty()) {
                expression = when {
                    expression.endsWith("sin(") || expression.endsWith("cos(") || 
                    expression.endsWith("tan(") || expression.endsWith("log(") -> expression.dropLast(4)
                    expression.endsWith("sqrt(") -> expression.dropLast(5)
                    else -> expression.dropLast(1)
                }
                updateDisplay()
                if (expression.isEmpty()) tvResult.text = "0"
            }
        }

        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqual() }
    }

    private fun appendToExpression(value: String) {
        expression += value
        updateDisplay()
    }

    private fun updateDisplay() {
        tvExpression.text = expression.replace("*", "×").replace("/", "÷")
    }

    private fun onEqual() {
        try {
            if (expression.isEmpty()) return
            val result = evaluate(expression)
            val formattedResult = if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                "%.9f".format(Locale.US, result).trimEnd('0').trimEnd('.')
            }
            tvResult.text = formattedResult
        } catch (e: Exception) {
            tvResult.text = "Error"
        }
    }

    private fun evaluate(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else if (eat('E'.code)) x *= 10.0.pow(parseFactor()) // Correção EXP (Ex: 2E3 = 2000)
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()
                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code || ch == 'π'.code) {
                    if (eat('π'.code)) x = PI
                    else {
                        while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                        val func = str.substring(startPos, pos)
                        x = parseFactor()
                        x = when (func) {
                            "sin" -> sin(Math.toRadians(x))
                            "cos" -> cos(Math.toRadians(x))
                            "tan" -> tan(Math.toRadians(x))
                            "log" -> log10(x)
                            "sqrt" -> sqrt(x)
                            else -> throw RuntimeException("Unknown function")
                        }
                    }
                } else {
                    throw RuntimeException("Unexpected")
                }
                if (eat('^'.code)) x = x.pow(parseFactor())
                return x
            }
        }.parse()
    }
}
