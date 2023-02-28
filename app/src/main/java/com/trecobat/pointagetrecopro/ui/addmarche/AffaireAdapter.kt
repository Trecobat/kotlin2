package com.trecobat.pointagetrecopro.ui.addmarche

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.data.entities.Affaire
import com.trecobat.pointagetrecopro.databinding.ItemAffaireBinding

class AffairesAdapter(
    private val listener: AffaireItemListener,
    private val affaires: MutableList<Affaire>
) : RecyclerView.Adapter<AffairesAdapter.AffaireViewHolder>() {

    interface AffaireItemListener {
        fun onClickedPointage(affaire: Affaire)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AffairesAdapter.AffaireViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemAffaireBinding = ItemAffaireBinding.inflate(inflater, parent, false)
        return AffaireViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = affaires.size

    override fun onBindViewHolder(holder: AffaireViewHolder, position: Int) {
        holder.bind(affaires[position])
    }

    inner class AffaireViewHolder(
        private val itemBinding: ItemAffaireBinding,
        private val listener: AffaireItemListener
    ) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        private lateinit var affaire: Affaire

        init {
            itemBinding.root.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Affaire) {
            this.affaire = item
            itemBinding.affaire.text = "${item.aff_id} - ${item.client?.cli_prenom} ${item.client?.cli_nom}"
        }

        override fun onClick(v: View?) {
            listener.onClickedPointage(affaire)
        }
    }
}