package com.trecobat.pointagetrecopro.ui.tachedetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.GedFiles
import com.trecobat.pointagetrecopro.databinding.ItemPlanBinding
import kotlinx.android.synthetic.main.item_plan.view.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class PlansAdapter(private val listener: PlanItemListener) : RecyclerView.Adapter<PlanViewHolder>() {

    interface PlanItemListener {
        fun onClickedPlan(gdf_fo_id: String)
    }

    private val items = ArrayList<GedFiles>()

    fun setItems(items: ArrayList<GedFiles>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding: ItemPlanBinding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) = holder.bind(items[position])
}

class PlanViewHolder(private val itemBinding: ItemPlanBinding, private val listener: PlansAdapter.PlanItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var plan: GedFiles

    init {
        itemBinding.root.plan_btn.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: GedFiles) {
        this.plan = item
        itemBinding.planBtn.text = item.gdf_cat_label
        itemBinding.planBtn.isEnabled = true
    }

    override fun onClick(v: View?) {
        listener.onClickedPlan(plan.gdf_fo_id)
    }
}

