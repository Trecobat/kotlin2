package com.trecobat.pointagetrecopro.ui.tachedetail

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.databinding.ItemPointageBinding
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.formatDate
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getDateTime
import com.trecobat.pointagetrecopro.utils.Resource
import kotlinx.android.synthetic.main.item_pointage.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.lifecycle.Observer
import java.util.*

class PointagesAdapter(
    private val listener: PointageItemListener,
    private val context: Context,
    private val viewModel: TacheDetailViewModel,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PointagesAdapter.PointageViewHolder>() {

    interface PointageItemListener {
        fun onClickedPointage(pointage: Pointage)
    }

    private val items = ArrayList<Pointage>()

    fun setItems(items: ArrayList<Pointage>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPointageBinding = ItemPointageBinding.inflate(inflater, parent, false)
        return PointageViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PointageViewHolder, position: Int) {
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

    inner class PointageViewHolder(
        private val itemBinding: ItemPointageBinding,
        private val listener: PointageItemListener
    ) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        private lateinit var pointage: Pointage

        init {
            itemBinding.root.pointage_btn.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Pointage, lifecycleOwner: LifecycleOwner) {
            this.pointage = item

            itemBinding.buttonJour.text = formatDate(date = item.poi_debut, outputPattern = "dd/MM/yy")
            itemBinding.buttonHeureDeb.text = formatDate(date = item.poi_debut, outputPattern = "HH:mm")
            itemBinding.buttonHeureFin.text = item.poi_fin?.let { formatDate(date = it, outputPattern = "HH:mm") }
            itemBinding.typePointage.text = item.poi_type

            if ( item.poi_type == "Marché" ) {
                itemBinding.coffretElec.isChecked = item.poi_coffret == 1
                itemBinding.remblais.isChecked = item.poi_remblais == 1
            } else {
                itemBinding.coffretElec.visibility = View.GONE
                itemBinding.remblais.visibility = View.GONE
                itemBinding.corpsEtat.visibility = View.VISIBLE
                itemBinding.natureErreur.visibility = View.VISIBLE
                itemBinding.corpsEtat.text = item.bdc_type?.bdct_label
                itemBinding.natureErreur.text = item.poi_nature_erreur
            }

            itemBinding.equipier.text = "${item.equipier?.eevp_prenom} ${item.equipier?.eevp_nom}"
            itemBinding.commentaire.text = Editable.Factory.getInstance().newEditable(item.poi_commentaire)

            setupDatePicker()
            setupTimesPicker()

            itemBinding.supprimerBtn.setOnClickListener {
                item.poi_deleted_at = getDateTime()
                GlobalScope.launch(Dispatchers.Main) {
                    viewModel.updatePointage(item).observe(lifecycleOwner, Observer { resource ->
                        when (resource.status) {
                            Resource.Status.SUCCESS -> {
                                Toast.makeText( context, "Le pointage ${resource.data?.poi_id} a bien été supprimé.", Toast.LENGTH_SHORT ).show()
                            }
                            Resource.Status.ERROR -> {
                                Toast.makeText( context, "Erreur lors de la suppression du pointage", Toast.LENGTH_SHORT ).show()
                            }
                            Resource.Status.LOADING -> {}
                        }
                    })
                }
            }

            itemBinding.pointageBtn.setOnClickListener {
                item.poi_debut = "${formatDate ( date = itemBinding.buttonJour.text as String, inputPattern = "dd/MM/yy", outputPattern = "yyyy-MM-dd" )} ${itemBinding.buttonHeureDeb.text}:00"
                item.poi_fin = "${formatDate ( date = itemBinding.buttonJour.text as String, inputPattern = "dd/MM/yy", outputPattern = "yyyy-MM-dd" )} ${itemBinding.buttonHeureFin.text}:00"
                item.poi_commentaire = itemBinding.commentaire.text.toString()

                GlobalScope.launch(Dispatchers.Main) {
                    viewModel.updatePointage(item).observe(lifecycleOwner, Observer { resource ->
                        when (resource.status) {
                            Resource.Status.SUCCESS -> {
                                itemBinding.progressBar.visibility = View.GONE
                                itemBinding.pointage.visibility = View.VISIBLE
                                Toast.makeText( context, "Le pointage ${resource.data?.poi_id} a bien été modifié.", Toast.LENGTH_SHORT ).show()
                            }
                            Resource.Status.ERROR -> {
                                itemBinding.progressBar.visibility = View.GONE
                                itemBinding.pointage.visibility = View.VISIBLE
                                Toast.makeText( context, "Erreur lors de la modification du pointage", Toast.LENGTH_SHORT ).show()
                            }
                            Resource.Status.LOADING -> {
                                itemBinding.progressBar.visibility = View.VISIBLE
                                itemBinding.pointage.visibility = View.GONE
                            }
                        }
                    })
                }
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

        private fun makeTimePickerDialog(binding: ItemPointageBinding, timing: String)
        {
            val heure = if ( timing == "deb" ) binding.buttonHeureDeb.text.toString() else binding.buttonHeureFin.text.toString()
            val hour = heure.split(":")[0]
            val minute = heure.split(":")[1]

            val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minuteOfHour ->
                var text = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
                text += ":"
                text += if (minuteOfHour < 10) "0$minuteOfHour" else "$minuteOfHour"

                val buttonBinding = if ( timing == "deb" ) binding.buttonHeureDeb else binding.buttonHeureFin
                buttonBinding.text = text
            }, Integer.parseInt( hour ), Integer.parseInt( minute ), true)

            // Optional customizations:
            timePickerDialog.setCanceledOnTouchOutside(false) // Prevent dialog from being dismissed when user touches outside of it
            timePickerDialog.show()
        }

        @SuppressLint("SetTextI18n", "DiscouragedApi")
        private fun setupTimesPicker() {
            // HEURE DEBUT
            itemBinding.buttonHeureDeb.setOnClickListener {
                makeTimePickerDialog(itemBinding, "deb")
            }

            // HEURE FIN
            itemBinding.buttonHeureFin.setOnClickListener {
                makeTimePickerDialog(itemBinding, "fin")
            }
        }

        override fun onClick(v: View?) {
            listener.onClickedPointage(pointage)
        }
    }
}

