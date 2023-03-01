package com.trecobat.pointagetrecopro.ui.addmarche

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.trecobat.pointagetrecopro.data.entities.Affaire
import com.trecobat.pointagetrecopro.data.entities.String
import com.trecobat.pointagetrecopro.databinding.AddMarcheFragmentBinding
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddMarcheFragment : Fragment(), AffairesAdapter.AffaireItemListener {

    private var binding: AddMarcheFragmentBinding by autoCleared()
    private val viewModel: AddMarcheViewModel by viewModels()
    private var affaires = ArrayList<Affaire>()
    private lateinit var affaireAdapter: AffairesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMarcheFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.test.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        Timber.d(it.data.toString())
                    }
                }
                Resource.Status.ERROR -> {
                    Timber.e(it.message)
                }
                Resource.Status.LOADING -> {}
            }
        })
        affaireAdapter = AffairesAdapter(this, affaires)
        setupSpinner()
        setupAutocomplete()
    }

    private fun setupSpinner() {
        viewModel.bdcTypes.observe(viewLifecycleOwner, Observer { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        binding.progressBar.visibility = View.GONE
                        val bdct = it.data.map { it.bdct_label }
                        val adapterBdct = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            bdct
                        )
                        adapterBdct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.bdctId.adapter = adapterBdct
                    }
                }
                Resource.Status.ERROR -> {
                    Timber.e(it.message)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING ->
                    binding.progressBar.visibility = View.VISIBLE
            }
        })
    }

    private fun setupAutocomplete() {
        val resultsRecyclerView = binding.affairesRv
        resultsRecyclerView.layoutManager = LinearLayoutManager(context)
        resultsRecyclerView.adapter = affaireAdapter

        val autoCompleteTextView = binding.affaire
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Effacer la liste des suggestions actuelles
                affaires.clear()
                if (s.toString().isNotEmpty()) {
                    if (s.toString().length > 3) {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.bdctId.visibility = View.GONE
                        binding.affairesSv.visibility = View.GONE
                            val query = String(text = "%${s.toString()}%")
                            viewModel.getAffairesByAffIdOrCliNom(query).observe(viewLifecycleOwner, Observer {
                                Timber.e(it.data.toString())
                                when (it.status) {
                                    Resource.Status.SUCCESS -> {
                                        if (!it.data.isNullOrEmpty()) {
                                            affaires.addAll(it.data)
                                            binding.progressBar.visibility = View.GONE
                                            binding.bdctId.visibility = View.VISIBLE
                                            if (affaires.isNotEmpty()) {
                                                binding.affairesSv.visibility = View.VISIBLE
                                            }
                                        }
                                    }
                                    Resource.Status.ERROR -> {
                                        Timber.e(it.message)
                                        binding.progressBar.visibility = View.GONE
                                        binding.bdctId.visibility = View.VISIBLE
                                        Toast.makeText( requireContext(), it.message, Toast.LENGTH_SHORT ).show()
                                    }
                                    Resource.Status.LOADING -> {
                                        binding.progressBar.visibility = View.VISIBLE
                                        binding.bdctId.visibility = View.GONE
                                        binding.affairesSv.visibility = View.GONE
                                    }
                                }
                            })
                    } else {
                        binding.affairesSv.visibility = View.GONE
                    }
                } else {
                    binding.affairesSv.visibility = View.GONE
                }
                //On signale à l'adapteur qu'il y a eu un changement
                affaireAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onClickedPointage(affaire: Affaire) {
        Toast.makeText(requireContext(), affaire.toString(), Toast.LENGTH_SHORT).show()
        binding.affairesSv.visibility = View.GONE
    }
}