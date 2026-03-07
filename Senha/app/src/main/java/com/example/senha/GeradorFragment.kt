package com.example.senha

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// TODO: Rename parameter arguments
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GeradorFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gerador, container, false)
    }

    fun gerarSenha(
        tamanho: Int,
        incluirMaiusculas: Boolean,
        incluirNumeros: Boolean,
        incluirSimbolos: Boolean
    ): String {

        val letrasMinusculas = "abcdefghijklmnopqrstuvwxyz"
        val letrasMaiusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numeros = "0123456789"
        val simbolos = "!@#\$%&*()-_=+"

        var caracteresPermitidos = letrasMinusculas

        if (incluirMaiusculas) {
            caracteresPermitidos += letrasMaiusculas
        }

        if (incluirNumeros) {
            caracteresPermitidos += numeros
        }

        if (incluirSimbolos) {
            caracteresPermitidos += simbolos
        }

        // evita erro se nenhuma opção estiver marcada
        if (!incluirMaiusculas && !incluirNumeros && !incluirSimbolos) {
            caracteresPermitidos = letrasMinusculas
        }

        val senha = StringBuilder()

        repeat(tamanho) {
            val caractere = caracteresPermitidos.random()
            senha.append(caractere)
        }

        return senha.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GeradorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}