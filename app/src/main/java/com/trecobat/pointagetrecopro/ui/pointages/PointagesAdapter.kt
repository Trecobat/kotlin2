package com.trecobat.pointagetrecopro.ui.pointages

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.databinding.ItemPointageBinding

class PointagesAdapter(private val listener: PointageItemListener) : RecyclerView.Adapter<PointageViewHolder>() {

    interface PointageItemListener {
        fun onClickedPointage(pointageId: Int)
    }

    private val items = ArrayList<Pointage>()

    fun setItems(items: ArrayList<Pointage>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointageViewHolder {
        val binding: ItemPointageBinding = ItemPointageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PointageViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PointageViewHolder, position: Int) = holder.bind(items[position])
}

class PointageViewHolder(private val itemBinding: ItemPointageBinding, private val listener: PointagesAdapter.PointageItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var pointage: Pointage

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Pointage) {
        this.pointage = item
        itemBinding.name.text = item.poi_id.toString()
        itemBinding.speciesAndStatus.text = item.poi_id.toString()
    }

    override fun onClick(v: View?) {
        listener.onClickedPointage(pointage.poi_id)
    }
}

