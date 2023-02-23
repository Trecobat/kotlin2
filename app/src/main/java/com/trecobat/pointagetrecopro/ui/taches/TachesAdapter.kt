package com.trecobat.pointagetrecopro.ui.taches

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.databinding.ItemTacheBinding
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.formatDate
import com.trecobat.pointagetrecopro.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class TachesAdapter(
    private val listener: TacheItemListener,
    private val context: Context,
    private val viewModel: TachesViewModel,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<TacheViewHolder>() {

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

    override fun onBindViewHolder(holder: TacheViewHolder, position: Int) = holder.bind(items[position], context, viewModel, lifecycleOwner)
}

class TacheViewHolder(private val itemBinding: ItemTacheBinding, private val listener: TachesAdapter.TacheItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var tache: Tache

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    fun bind(item: Tache, context: Context, viewModel: TachesViewModel, lifecycleOwner: LifecycleOwner) {
        this.tache = item

        itemBinding.tache.setBackgroundColor ( getItemColor ( item.nb_termine, item.nb_pointage ) )
        itemBinding.cliNom.text = item.affaire.client.cli_nom
        itemBinding.affId.text = " - ${item.affaire.aff_id}"
        itemBinding.bdctLabel.text = item.bdc_type.bdct_label
        itemBinding.startDate.text = formatDate(item.start_date)
        itemBinding.endDate.text = item.end_date?.let { formatDate(it) }
        itemBinding.cliAdresse1Chantier.text = item.affaire.client.cli_adresse1_chantier
        itemBinding.cliAdresse2Chantier.text = if (item.affaire.client.cli_adresse2_chantier != null) " - ${item.affaire.client.cli_adresse2_chantier}" else ""
        itemBinding.cliCpChantier.text = item.affaire.client.cli_cp_chantier
        itemBinding.cliVilleChantier.text = item.affaire.client.cli_ville_chantier

        itemBinding.masquer.setOnClickListener {
            item.hidden = 1
            GlobalScope.launch(Dispatchers.Main) {
                viewModel.updateTache(item).observe(lifecycleOwner, Observer { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            Toast.makeText( context, "La tâche ${item.id} a bien été masquée.", Toast.LENGTH_SHORT ).show()
                        }
                        Resource.Status.ERROR -> {
                            Toast.makeText( context, "La tâche n'a pas été masquée : ${resource?.message}", Toast.LENGTH_SHORT ).show()
                            Timber.e( "La tâche n'a pas été masquée : ${resource?.message}")
                        }
                        Resource.Status.LOADING -> {}
                    }
                })
            }
        }
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

