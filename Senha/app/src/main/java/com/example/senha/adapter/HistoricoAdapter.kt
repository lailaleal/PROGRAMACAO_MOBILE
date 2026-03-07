package com.example.senha.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.senha.R

class HistoricoAdapter(private val lista: List<String>) :
    RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textSenha: TextView = itemView.findViewById(R.id.textSenhaItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_senha, parent, false)

        return HistoricoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {

        val senha = lista[position]
        holder.textSenha.text = senha
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}