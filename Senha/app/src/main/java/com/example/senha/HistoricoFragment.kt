package com.example.senha

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.senha.adapter.HistoricoAdapter

class HistoricoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o RecyclerView
        val recyclerHistorico = view.findViewById<RecyclerView>(R.id.recyclerHistorico)

        // Configura o Adapter com a lista que está no objeto HistoricoSenhas
        val adapter = HistoricoAdapter(HistoricoSenhas.listaSenhas)

        // Configura o RecyclerView
        recyclerHistorico.adapter = adapter
        recyclerHistorico.layoutManager = LinearLayoutManager(context)
    }
}
