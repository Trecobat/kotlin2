package com.trecobat.pointagetrecopro.ui.mespointages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.databinding.MesPointagesFragmentBinding
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.ArrayList

@AndroidEntryPoint
class MesPointagesFragment: Fragment(), PointageAdapter.PointageItemListener {
    private var binding: MesPointagesFragmentBinding by autoCleared()
    private val viewModel: MesPointagesViewModel by viewModels()
    private lateinit var pointageAdapter: PointageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MesPointagesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObserver()
    }

    private fun setupRecyclerView() {
        pointageAdapter = PointageAdapter(this, requireContext(), viewModel, viewLifecycleOwner)
        binding.pointagesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.pointagesRv.adapter = pointageAdapter
    }

    private fun setupObserver() {
        val mesPointagesObserver = Observer<Resource<List<Pointage>>> { resource ->
            when(resource.status) {
                Resource.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    binding.pointagesRv.visibility = View.VISIBLE
                    if (!resource.data.isNullOrEmpty()) pointageAdapter.setItems(ArrayList(resource.data))
                }
                Resource.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.pointagesRv.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Une erreur est survenue lors de la récupération des pointages", Toast.LENGTH_SHORT).show()
                    Timber.e(resource.message)
                }
                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.pointagesRv.visibility = View.GONE
                }
            }
        }
        viewModel.pointages.observe(viewLifecycleOwner, mesPointagesObserver)
    }

    override fun onClickedPointage(pointage: Pointage) {
        TODO("Not yet implemented")
    }
}