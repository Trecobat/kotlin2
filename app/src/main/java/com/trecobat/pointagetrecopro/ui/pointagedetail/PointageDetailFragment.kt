package com.trecobat.pointagetrecopro.ui.pointagedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.databinding.PointageDetailFragmentBinding
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PointageDetailFragment : Fragment() {

    private var binding: PointageDetailFragmentBinding by autoCleared()
    private val viewModel: PointageDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PointageDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("id")?.let { viewModel.start(it) }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.pointage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    bindPointage(it.data!!)
                    binding.progressBar.visibility = View.GONE
                    binding.pointageCl.visibility = View.VISIBLE
                }

                Resource.Status.ERROR ->
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.pointageCl.visibility = View.GONE
                }
            }
        })
    }

    private fun bindPointage(pointage: Pointage) {
        binding.name.text = pointage.poi_id.toString()
        binding.species.text = pointage.poi_id.toString()
        binding.status.text = pointage.poi_id.toString()
        binding.gender.text = pointage.poi_id.toString()
    }
}