package com.example.senha

object HistoricoSenhas {
    val listaSenhas = ArrayList<String>()

    fun adicionarSenha(novaSenha: String) {

        // evita duplicatas
        if (listaSenhas.contains(novaSenha)) {
            return
        }

        // adiciona a senha no topo da lista
        listaSenhas.add(0, novaSenha)
    }
}