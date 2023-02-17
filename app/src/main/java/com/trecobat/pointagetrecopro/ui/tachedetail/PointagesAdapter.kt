package com.trecobat.pointagetrecopro.ui.tachedetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.databinding.ItemPointageBinding
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.formatDate
import com.trecobat.pointagetrecopro.helper.ViewHelper
import kotlinx.android.synthetic.main.item_pointage.view.*
import java.util.*

class PointagesAdapter(private val listener: PointageItemListener) : RecyclerView.Adapter<PointageViewHolder>() {
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
        val view: View = inflater.inflate(R.layout.item_pointage, parent, false)
        return PointageViewHolder(binding, listener, view)
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
        holder.bind(items[position])
    }
}

class PointageViewHolder(
    private val itemBinding: ItemPointageBinding,
    private val listener: PointagesAdapter.PointageItemListener,
    private val view: View
) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

    private lateinit var pointage: Pointage

    init {
        itemBinding.root.pointage_btn.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Pointage) {
        this.pointage = item
        itemBinding.jour.visibility = View.GONE
        itemBinding.heureDeb.visibility = View.GONE
        itemBinding.validerHeureDeb.visibility = View.GONE
        itemBinding.heureFin.visibility = View.GONE
        itemBinding.validerHeureFin.visibility = View.GONE

        itemBinding.buttonJour.text = formatDate(date = item.poi_debut, outputPattern = "dd/MM/yy")
        itemBinding.buttonHeureDeb.text = formatDate(date = item.poi_debut, outputPattern = "HH:mm")
        itemBinding.buttonHeureFin.text = formatDate(date = item.poi_fin, outputPattern = "HH:mm")

        itemBinding.coffretElec.isChecked = item.poi_coffret == 1
        itemBinding.remblais.isChecked = item.poi_remblais == 1

        itemBinding.equipier.text = "${item.equipier?.eevp_prenom} ${item.equipier?.eevp_nom}"

        ViewHelper.setupDatePicker(view)
//        itemBinding..text = item.gdf_cat_label

    }

    override fun onClick(v: View?) {
        listener.onClickedPointage(pointage)
    }
}

