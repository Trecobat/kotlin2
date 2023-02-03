package com.trecobat.pointagetrecopro.ui.pointages

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
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.databinding.PointagesFragmentBinding
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PointagesFragment : Fragment(), PointagesAdapter.PointageItemListener {

    private var binding: PointagesFragmentBinding by autoCleared()
    private val viewModel: PointagesViewModel by viewModels()
    private lateinit var adapter: PointagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PointagesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = PointagesAdapter(this)
        binding.pointagesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.pointagesRv.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.pointages.observe(viewLifecycleOwner, Observer {
            Timber.d("Je suis dans mon observer")
            Timber.d(it.toString())
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.d(it.toString())
                    binding.progressBar.visibility = View.GONE
                    if (!it.data.isNullOrEmpty()) adapter.setItems(ArrayList(it.data))
                }
                Resource.Status.ERROR -> {
                    Timber.d("Error")
                    Timber.d(it.message)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING ->
                    binding.progressBar.visibility = View.VISIBLE
            }
        })
    }

    override fun onClickedPointage(pointageId: Int) {
        findNavController().navigate(
            R.id.action_pointagesFragment_to_pointageDetailFragment,
            bundleOf("id" to pointageId)
        )
    }
}
