package com.trecobat.pointagetrecopro.ui.taches

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.databinding.ItemTacheBinding
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.formatDate
import timber.log.Timber
import kotlin.collections.ArrayList

class TachesAdapter(private val listener: TacheItemListener) : RecyclerView.Adapter<TacheViewHolder>() {

    interface TacheItemListener {
        fun onClickedTache(tacheId: Int)
    }

    private val items = ArrayList<Tache>()

    fun setItems(items: ArrayList<Tache>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TacheViewHolder {
        val binding: ItemTacheBinding = ItemTacheBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TacheViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TacheViewHolder, position: Int) = holder.bind(items[position])
}

class TacheViewHolder(private val itemBinding: ItemTacheBinding, private val listener: TachesAdapter.TacheItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var tache: Tache

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    fun bind(item: Tache) {
        this.tache = item

        itemBinding.tache.setBackgroundColor ( getItemColor ( item.nb_termine, item.nb_pointage ) )
        itemBinding.cliNom.text = item.affaire.client.cli_nom
        itemBinding.affId.text = " - ${item.aff_id}"
        itemBinding.bdctLabel.text = item.bdc_type.bdct_label
        itemBinding.startDate.text = formatDate(item.start_date)
        itemBinding.endDate.text = item.end_date?.let { formatDate(it) }
        itemBinding.cliAdresse1Chantier.text = item.affaire.client.cli_adresse1_chantier
        itemBinding.cliAdresse2Chantier.text = if (item.affaire.client.cli_adresse2_chantier != null) " - ${item.affaire.client.cli_adresse2_chantier}" else ""
        itemBinding.cliCpChantier.text = item.affaire.client.cli_cp_chantier
        itemBinding.cliVilleChantier.text = item.affaire.client.cli_ville_chantier
    }

    // Récupère la couleur de l'item en fonction de si le tache est à venir, en cours ou terminé
    private fun getItemColor(nb_termine: Int, nb_pointage: Int): Int
    {
        return ContextCompat.getColor(
            itemBinding.root.context,
            if ( nb_termine > 0 ) {
                R.color.tache_terminee
            } else if ( nb_pointage > 0 ) {
                R.color.tache_en_cours
            } else {
                R.color.tache_a_venir
            }
        )
    }

    override fun onClick(v: View?) {
        listener.onClickedTache(tache.id)
    }
}

