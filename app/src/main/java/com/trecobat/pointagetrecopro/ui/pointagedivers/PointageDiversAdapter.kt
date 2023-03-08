package com.trecobat.pointagetrecopro.ui.pointagedivers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.provider.SyncStateContract.Helpers.update
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.databinding.ItemPointageDiversBinding
import com.trecobat.pointagetrecopro.helper.DateHelper
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getDateTime
import com.trecobat.pointagetrecopro.utils.Resource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class PointageDiversAdapter(
    private val listener: PointageDiversItemListener,
    private val context: Context,
    private val viewModel: PointageDiversViewModel,
    private val lifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<PointageDiversAdapter.PointageDiversViewHolder>() {

    interface PointageDiversItemListener {
        fun onClickedPointageDivers(pointage: Pointage)
    }

    private val items = ArrayList<Pointage>()

    fun setItems(items: ArrayList<Pointage>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointageDiversViewHolder {
        val binding: ItemPointageDiversBinding =
            ItemPointageDiversBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PointageDiversViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PointageDiversViewHolder, position: Int) {
        // Détermine si l'élément est impair ou pair
        val isOdd = position % 2 == 1

        // Définit le fond de la vue en fonction de la parité
        if (isOdd) {
            holder.itemView.setBackgroundResource(R.drawable.border)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bordereven)
        }
        holder.bind(items[position], lifecycleOwner)
    }

    inner class PointageDiversViewHolder(
        private val itemBinding: ItemPointageDiversBinding,
        private val listener: PointageDiversItemListener
    ) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        private lateinit var pointageDivers: Pointage
        private var dureeHeureKeys: MutableList<String> = mutableListOf()
        private var dureeMinuteKeys: MutableList<String> = mutableListOf()

        init {
            itemBinding.pointageBtn.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Pointage, lifecycleOwner: LifecycleOwner) {
            this.pointageDivers = item
            itemBinding.buttonJour.text = DateHelper.formatDate(date = item.poi_debut, outputPattern = "dd/MM/yy")
            itemBinding.equipier.text = "${item.equipier?.eevp_prenom} ${item.equipier?.eevp_nom}"
            itemBinding.commentaire.text = Editable.Factory.getInstance().newEditable(item.poi_commentaire)
            setupDatePicker()
            setupSpinners(item)

            itemBinding.supprimer.setOnClickListener { supprimer(item, lifecycleOwner) }
            itemBinding.pointageBtn.setOnClickListener { update(item, lifecycleOwner) }
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun supprimer(item: Pointage, lifecycleOwner: LifecycleOwner) {
            val supprimerObserver = Observer<Resource<Pointage>> { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        Toast.makeText(context, "Le pointage a bien été supprimé", Toast.LENGTH_SHORT).show()
                        itemBinding.progressBar.visibility = View.GONE
                        itemBinding.pointageCl.visibility = View.VISIBLE
                    }

                    Resource.Status.ERROR -> {
                        itemBinding.progressBar.visibility = View.GONE
                        itemBinding.pointageCl.visibility = View.VISIBLE
                        Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                        Timber.e(resource.message)
                    }

                    Resource.Status.LOADING -> {
                        itemBinding.pointageCl.visibility = View.GONE
                        itemBinding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
            item.poi_deleted_at = getDateTime()
            GlobalScope.launch(Dispatchers.Main) {
                viewModel.updatePointage(item).observe(lifecycleOwner, supprimerObserver)
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun update(item: Pointage, lifecycleOwner: LifecycleOwner) {
            val modifierObserver = Observer<Resource<Pointage>> { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        Toast.makeText(context, "Le pointage a bien été modifié", Toast.LENGTH_SHORT).show()
                        itemBinding.progressBar.visibility = View.GONE
                        itemBinding.pointageCl.visibility = View.VISIBLE
                    }

                    Resource.Status.ERROR -> {
                        itemBinding.progressBar.visibility = View.GONE
                        itemBinding.pointageCl.visibility = View.VISIBLE
                        Toast.makeText(context, "Erreur lors de la modification", Toast.LENGTH_SHORT).show()
                        Timber.e(resource.message)
                    }

                    Resource.Status.LOADING -> {
                        itemBinding.pointageCl.visibility = View.GONE
                        itemBinding.progressBar.visibility = View.VISIBLE
                    }
                }
            }

            item.poi_debut = "${DateHelper.formatDate(date = itemBinding.buttonJour.text.toString(), inputPattern = "dd/MM/yy", outputPattern = "yyyy-MM-dd")} 00:00:00"
            item.poi_fin = "${DateHelper.formatDate(date = itemBinding.buttonJour.text.toString(), inputPattern = "dd/MM/yy", outputPattern = "yyyy-MM-dd")} 00:00:00"
            item.poi_duree = "${dureeHeureKeys[itemBinding.dureeHeure.selectedItemPosition]}:${dureeMinuteKeys[itemBinding.dureeMinute.selectedItemPosition]}"
            item.poi_type = itemBinding.type.selectedItem.toString()
            item.poi_commentaire = itemBinding.commentaire.text.toString()

            GlobalScope.launch(Dispatchers.Main) {
                viewModel.updatePointage(item).observe(lifecycleOwner, modifierObserver)
            }
        }

        private fun setupDatePicker()
        {
            itemBinding.buttonJour.setOnClickListener {
                val year = "20${itemBinding.buttonJour.text.toString().split("/")[2]}"
                val month = itemBinding.buttonJour.text.toString().split("/")[1]
                val day = itemBinding.buttonJour.text.toString().split("/")[0]

                // Optional customizations:
                val datePickerDialog = DatePickerDialog(context, { _, yearDate, monthOfYear, dayOfMonth ->
                    var text = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                    text += "/"
                    text += if (monthOfYear < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
                    text += "/${yearDate.toString().substring(2)}"
                    itemBinding.buttonJour.text = text
                }, Integer.parseInt( year ), Integer.parseInt( month ) - 1, Integer.parseInt( day ))

                // Optional customizations:
                datePickerDialog.show()
            }
        }

        private fun setupSpinners(pointage: Pointage)
        {
            // Type de pointage
            val valuesType = listOf(
                "Atelier",
                "Divers",
                "Fournisseur",
                "Gazole",
                "Garage",
                "Itempérie"
            )
            val adapterType = ArrayAdapter(context, android.R.layout.simple_spinner_item, valuesType)
            adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            itemBinding.type.adapter = adapterType
            itemBinding.type.setSelection(valuesType.indexOf(pointage.poi_type))

            // Duree en heure
            val dureeHeure: MutableList<String> = mutableListOf()
            for (i in 0..8) {
                dureeHeureKeys.add("0$i")
                dureeHeure.add ( if ( i < 2 ) "$i heure" else "$i heures" )
            }
            val adapterDureeHeure = ArrayAdapter(context, android.R.layout.simple_spinner_item, dureeHeure)
            adapterDureeHeure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            itemBinding.dureeHeure.adapter = adapterDureeHeure
            val indexHeure = ((pointage.poi_duree)!!.split(":")[0]).toInt()
            val valueHeure = if ( indexHeure < 2 ) "$indexHeure heure" else "$indexHeure heures"
            itemBinding.dureeHeure.setSelection(dureeHeure.indexOf(valueHeure))

            // Duree en minute
            val dureeMinute: MutableList<String> = mutableListOf()
            for (i in 0..59) {
                dureeMinuteKeys.add(if ( i < 10 ) "0$i" else "$i")
                dureeMinute.add ( if (i < 2) "$i minute" else "$i minutes" )
            }
            val adapterDureeMinute = ArrayAdapter(context, android.R.layout.simple_spinner_item, dureeMinute)
            adapterDureeMinute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            itemBinding.dureeMinute.adapter = adapterDureeMinute
            val indexMinute = ((pointage.poi_duree)!!.split(":")[1]).toInt()
            val valueMinute = if ( indexMinute < 2 ) "$indexMinute minute" else "$indexMinute minutes"
            itemBinding.dureeMinute.setSelection(dureeMinute.indexOf(valueMinute))

        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }

    }
}