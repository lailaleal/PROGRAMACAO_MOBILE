package com.example.senha

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class GeradorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gerador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializar os componentes da UI
        val textSenha = view.findViewById<TextView>(R.id.textSenha)
        val seekBarTamanho = view.findViewById<SeekBar>(R.id.seekBarTamanho)
        val textTamanho = view.findViewById<TextView>(R.id.textTamanho)
        val switchMaiusculas = view.findViewById<SwitchMaterial>(R.id.switchMaiusculas)
        val switchNumeros = view.findViewById<SwitchMaterial>(R.id.switchNumeros)
        val switchSimbolos = view.findViewById<SwitchMaterial>(R.id.switchSimbolos)
        val buttonGerar = view.findViewById<MaterialButton>(R.id.buttonGerar)
        val buttonCopiar = view.findViewById<MaterialButton>(R.id.buttonCopiar)

        // Configuração inicial
        seekBarTamanho.progress = 10
        textTamanho.text = "Tamanho: ${seekBarTamanho.progress}"

        // 2. Listener da SeekBar para atualizar o texto do tamanho em tempo real
        seekBarTamanho.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textTamanho.text = "Tamanho: $progress"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 3. Lógica do botão Gerar
        buttonGerar.setOnClickListener {
            val tamanho = seekBarTamanho.progress
            
            if (tamanho == 0) {
                Toast.makeText(context, "Selecione um tamanho maior que 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gera a senha
            val senhaGerada = gerarSenha(
                tamanho,
                switchMaiusculas.isChecked,
                switchNumeros.isChecked,
                switchSimbolos.isChecked
            )

            // Exibe a senha
            textSenha.text = senhaGerada
            
            // Salva no histórico automaticamente
            HistoricoSenhas.adicionarSenha(senhaGerada)
        }

        // 4. Lógica do botão Copiar
        buttonCopiar.setOnClickListener {
            val senha = textSenha.text.toString()
            
            if (senha.isNotEmpty() && senha != "Sua senha aparecerá aqui") {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Senha Gerada", senha)
                clipboard.setPrimaryClip(clip)
                
                Toast.makeText(context, "Senha copiada para a área de transferência!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gere uma senha primeiro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gerarSenha(
        tamanho: Int,
        incluirMaiusculas: Boolean,
        incluirNumeros: Boolean,
        incluirSimbolos: Boolean
    ): String {
        val letrasMinusculas = "abcdefghijklmnopqrstuvwxyz"
        val letrasMaiusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numeros = "0123456789"
        val simbolos = "!@#$%&*()-_=+"

        var caracteresPermitidos = letrasMinusculas

        if (incluirMaiusculas) caracteresPermitidos += letrasMaiusculas
        if (incluirNumeros) caracteresPermitidos += numeros
        if (incluirSimbolos) caracteresPermitidos += simbolos

        // Caso o usuário desmarque tudo, garantimos ao menos letras minúsculas
        if (caracteresPermitidos.isEmpty()) caracteresPermitidos = letrasMinusculas

        val senha = StringBuilder()
        repeat(tamanho) {
            val caractere = caracteresPermitidos.random()
            senha.append(caractere)
        }

        return senha.toString()
    }
}
