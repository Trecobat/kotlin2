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
import com.trecobat.pointagetrecopro.databinding.AddMarcheFragmentBinding
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.tache_detail_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber

@AndroidEntryPoint
class AddMarcheFragment : Fragment() {

    private var binding: AddMarcheFragmentBinding by autoCleared()
    private val viewModel: AddMarcheViewModel by viewModels()

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
        val autoCompleteTextView = binding.affaire
        val affaires = mutableListOf<String>()
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, affaires)
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    if (s.toString().length > 3) {
                        val query = "%${s.toString()}%"
                        Timber.d(query)
                        // Effacer la liste des suggestions actuelles
                        affaires.clear()
                        // Appeler l'API pour récupérer les suggestions
                        viewModel.getAffairesByAffIdOrCliNom(query).observe(viewLifecycleOwner, Observer {
                            when (it.status) {
                                Resource.Status.SUCCESS -> {
                                    if (!it.data.isNullOrEmpty()) {
                                        for (row in it.data) {
                                            val suggestion = "${row.aff_id}"
                                            affaires.add(suggestion)
                                        }
                                        binding.progressBar.visibility = View.GONE
                                        binding.bdctId.visibility = View.VISIBLE
                                        adapter.notifyDataSetChanged()
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
                                }
                            }
                        })
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}