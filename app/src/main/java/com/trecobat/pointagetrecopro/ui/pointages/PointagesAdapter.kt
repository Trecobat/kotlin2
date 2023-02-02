package com.trecobat.pointagetrecopro.ui.pointages

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.databinding.ItemPointageBinding
import timber.log.Timber

class PointagesAdapter(private val listener: PointageItemListener) :
    RecyclerView.Adapter<PointageViewHolder>() {

    interface PointageItemListener {
        fun onClickedPointage(pointageId: Int)
    }

    private val items = ArrayList<Pointage>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<Pointage>) {
        Timber.d( "Je passe dans le setItems" )
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
        Timber.d( this.items.size.toString() )
        Timber.d( this.items.toString() )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointageViewHolder {
        Timber.d( "Je passe dans le onCreateViewHolder" )
        val binding: ItemPointageBinding =
            ItemPointageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PointageViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = 2
//    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PointageViewHolder, position: Int) {
        Timber.d( "Je passe dans le onBindViewHolder" )
        holder.bind(items[position])
    }

}

class PointageViewHolder(
    private val itemBinding: ItemPointageBinding,
    private val listener: PointagesAdapter.PointageItemListener
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var pointage: Pointage

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Pointage) {
        Timber.d( "Je passe dans le bind" )
        this.pointage = item
        Timber.d("Mon pointage : ")
        Timber.d(this.pointage.toString())
        itemBinding.itemId.text = item.poi_id.toString()
    }

    override fun onClick(v: View?) {
        Timber.d( "Je passe dans le onClick" )
        listener.onClickedPointage(pointage.poi_id)
    }
}