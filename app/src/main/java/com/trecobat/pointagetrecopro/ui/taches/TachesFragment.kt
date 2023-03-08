package com.trecobat.pointagetrecopro.ui.taches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.databinding.TachesFragmentBinding
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TachesFragment : Fragment(), TachesAdapter.TacheItemListener {

    private var binding: TachesFragmentBinding by autoCleared()
    private val viewModel: TachesViewModel by viewModels()
    private lateinit var adapter: TachesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TachesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        binding.diversClBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_tachesFragment_to_pointageDiversFragment
            )
        }
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            findNavController().navigate(
                R.id.action_tachesFragment_refresh
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = TachesAdapter(this, requireContext(), viewModel, viewLifecycleOwner)
        binding.tachesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.tachesRv.adapter = adapter
    }

    private fun setupObservers() {
        val observer = Observer<Resource<List<Tache>>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    if (!resource.data.isNullOrEmpty()) adapter.setItems(ArrayList(resource.data))
                }
                Resource.Status.ERROR -> {
                    Timber.e(resource.message)
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING -> binding.progressBar.visibility = View.VISIBLE
            }
        }
        viewModel.taches.observe(viewLifecycleOwner, observer)
    }

    override fun onClickedTache(tacheId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tachesRv.visibility = View.GONE
        findNavController().navigate(
            R.id.action_tachesFragment_to_tacheDetailFragment,
            bundleOf("id" to tacheId)
        )
    }
}
