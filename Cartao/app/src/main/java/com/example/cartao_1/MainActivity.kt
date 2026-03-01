package com.example.cartao_1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var cardFront: LinearLayout
    private lateinit var cardBack: LinearLayout

    private lateinit var tilNumero: TextInputLayout
    private lateinit var tilNome: TextInputLayout
    private lateinit var tilValidade: TextInputLayout
    private lateinit var tilCvv: TextInputLayout

    private lateinit var etNumero: TextInputEditText
    private lateinit var etNome: TextInputEditText
    private lateinit var etValidade: TextInputEditText
    private lateinit var etCvv: TextInputEditText

    private lateinit var tvNumero: TextView
    private lateinit var tvNome: TextView
    private lateinit var tvValidade: TextView
    private lateinit var tvCvv: TextView
    private lateinit var imgBandeira: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardFront = findViewById(R.id.cardFront)
        cardBack = findViewById(R.id.cardBack)

        tilNumero = findViewById(R.id.tilNumero)
        tilNome = findViewById(R.id.tilNome)
        tilValidade = findViewById(R.id.tilValidade)
        tilCvv = findViewById(R.id.tilCvv)

        etNumero = findViewById(R.id.etNumeroCartao)
        etNome = findViewById(R.id.etNome)
        etValidade = findViewById(R.id.etValidade)
        etCvv = findViewById(R.id.etCvv)

        tvNumero = findViewById(R.id.tvNumeroCartao)
        tvNome = findViewById(R.id.tvNomeCartao)
        tvValidade = findViewById(R.id.tvValidadeCartao)
        tvCvv = findViewById(R.id.tvCvvCartao)
        imgBandeira = findViewById(R.id.imgBandeira)

        val btnValidar = findViewById<Button>(R.id.btnValidar)

        // MÁSCARA NÚMERO CARTÃO (0000 0000 0000 0000)
        etNumero.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val clean = s.toString().replace(" ", "")
                val formatted = StringBuilder()
                for (i in clean.indices) {
                    formatted.append(clean[i])
                    if ((i + 1) % 4 == 0 && i + 1 != clean.length && i < 15) {
                        formatted.append(" ")
                    }
                }
                
                etNumero.setText(formatted.toString())
                etNumero.setSelection(formatted.length)
                
                tvNumero.text = if (formatted.isEmpty()) "0000 0000 0000 0000" else formatted.toString()

                // Identifica Bandeira
                if (clean.isNotEmpty()) {
                    when {
                        clean.startsWith("4") -> {
                            imgBandeira.setImageResource(R.drawable.visa)
                            imgBandeira.visibility = View.VISIBLE
                            tilNumero.error = null
                        }
                        clean.startsWith("5") -> {
                            imgBandeira.setImageResource(R.drawable.mastercard)
                            imgBandeira.visibility = View.VISIBLE
                            tilNumero.error = null
                        }
                        else -> {
                            imgBandeira.visibility = View.GONE
                            tilNumero.error = "Bandeira não aceita"
                        }
                    }
                } else {
                    imgBandeira.visibility = View.GONE
                    tilNumero.error = null
                }
                
                isUpdating = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // NOME
        etNome.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tvNome.text = if (s.isNullOrEmpty()) "NOME SOBRENOME" else s.toString().uppercase()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // MÁSCARA VALIDADE (MM/AA) - Limitado a 5 caracteres (2 mes + / + 2 ano)
        etValidade.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                var clean = s.toString().replace("/", "")
                if (clean.length > 4) clean = clean.substring(0, 4)

                val formatted = if (clean.length >= 2) {
                    clean.substring(0, 2) + "/" + clean.substring(2)
                } else {
                    clean
                }

                etValidade.setText(formatted)
                etValidade.setSelection(formatted.length)
                tvValidade.text = if (formatted.isEmpty()) "MM/AA" else formatted
                
                isUpdating = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // CVV
        etCvv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tvCvv.text = if (s.isNullOrEmpty()) "000" else s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // VALIDAR AO SAIR DOS CAMPOS (FOCUS CHANGE)
        etNumero.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateNumero()
        }
        etNome.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateNome()
        }
        etValidade.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateValidade()
        }
        etCvv.setOnFocusChangeListener { v, hasFocus ->
            // Flip Card
            if (hasFocus) {
                cardFront.visibility = View.GONE
                cardBack.visibility = View.VISIBLE
            } else {
                cardFront.visibility = View.VISIBLE
                cardBack.visibility = View.GONE
                validateCvv()
            }
        }

        btnValidar.setOnClickListener {
            if (validateNumero() && validateNome() && validateValidade() && validateCvv()) {
                Toast.makeText(this, "Cartão salvo com sucesso!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Verifique os campos com erro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateNumero(): Boolean {
        val numero = etNumero.text.toString().replace(" ", "")
        return if (numero.length != 16) {
            tilNumero.error = "O número deve ter 16 dígitos"
            false
        } else if (!numero.startsWith("4") && !numero.startsWith("5")) {
            tilNumero.error = "Somente Visa (4) ou MasterCard (5)"
            false
        } else {
            tilNumero.error = null
            true
        }
    }

    private fun validateNome(): Boolean {
        val nome = etNome.text.toString().trim()
        val partes = nome.split(" ").filter { it.isNotBlank() }
        
        return if (partes.size < 2) {
            tilNome.error = "Digite o nome completo (nome e sobrenome)"
            false
        } else if (nome.length <= 3) {
            tilNome.error = "Nome muito curto"
            false
        } else {
            tilNome.error = null
            true
        }
    }

    private fun validateValidade(): Boolean {
        val validade = etValidade.text.toString()
        if (validade.length != 5) {
            tilValidade.error = "Formato MM/AA"
            return false
        }

        return try {
            val partes = validade.split("/")
            val mes = partes[0].toInt()
            val ano = partes[1].toInt()

            val calendar = Calendar.getInstance()
            val mesAtual = calendar.get(Calendar.MONTH) + 1
            val anoAtualShort = calendar.get(Calendar.YEAR) % 100

            if (mes < 1 || mes > 12) {
                tilValidade.error = "Mês inválido"
                false
            } else if (ano < anoAtualShort || (ano == anoAtualShort && mes <= mesAtual)) {
                tilValidade.error = "Cartão expirado"
                false
            } else {
                tilValidade.error = null
                true
            }
        } catch (e: Exception) {
            tilValidade.error = "Data inválida"
            false
        }
    }

    private fun validateCvv(): Boolean {
        val cvv = etCvv.text.toString()
        return if (cvv.length != 3) {
            tilCvv.error = "CVV deve ter 3 dígitos"
            false
        } else {
            tilCvv.error = null
            true
        }
    }
}
