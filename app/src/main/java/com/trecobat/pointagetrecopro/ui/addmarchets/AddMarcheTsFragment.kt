package com.trecobat.pointagetrecopro.ui.addmarchets

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.*
import com.trecobat.pointagetrecopro.data.entities.String
import com.trecobat.pointagetrecopro.databinding.AddMarcheTsFragmentBinding
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getDay
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getMonth
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getYear
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AddMarcheTsFragment : Fragment(), AffairesAdapter.AffaireItemListener {

    private var binding: AddMarcheTsFragmentBinding by autoCleared()
    private val viewModel: AddMarcheTsViewModel by viewModels()
    private var affaires = ArrayList<Affaire>()
    private lateinit var affaireAdapter: AffairesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMarcheTsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("type")?.let { viewModel.start(it) }
        affaireAdapter = AffairesAdapter(this, affaires)
        setupSpinner()
        setupAutocomplete()

        binding.addMarche.setOnClickListener { addMarche() }
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
                    binding.progressBar.visibility = View.GONE
                    Timber.e(it.message)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
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
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Effacer la liste des suggestions actuelles
                affaires.clear()
                if (s.toString().isNotEmpty()) {
                    if (s.toString().length > 3) {
                        val query = String(string = "%${s.toString()}%")
                        viewModel.getAffairesByAffIdOrCliNom(query).observe(viewLifecycleOwner, Observer {
                            Timber.e(it.data.toString())
                            when (it.status) {
                                Resource.Status.SUCCESS -> {
                                    binding.progressBar.visibility = View.GONE
                                    if (!it.data.isNullOrEmpty()) {
                                        affaires.clear()
                                        affaires.addAll(it.data)
                                        if (affaires.isNotEmpty()) {
                                            binding.affairesSv.visibility = View.VISIBLE
                                        }
                                        //On signale à l'adapteur qu'il y a eu un changement
                                        affaireAdapter.notifyDataSetChanged()
                                    }
                                }
                                Resource.Status.ERROR -> {
                                    Timber.e(it.message)
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText( requireContext(), it.message, Toast.LENGTH_SHORT ).show()
                                }
                                Resource.Status.LOADING -> {
                                    binding.progressBar.visibility = View.VISIBLE
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
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun addMarche() {
        val tache = PostTache(
            aff_id = Integer.parseInt( binding.affId.text.toString() ),
            bdct_label = binding.bdctId.selectedItem.toString(),
            text = "${viewModel.type.value} ${binding.bdctId.selectedItem}",
            start_date = "${getYear()}-${getMonth()}-${getDay()}"
        )

        Timber.e(tache.toString())

        GlobalScope.launch(Dispatchers.Main) {
            viewModel.addTache(tache).observe(viewLifecycleOwner, Observer { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        findNavController().navigate(
                            if ( viewModel.type.value == "Marché" ) R.id.action_addMarcheFragment_to_tacheDetailFragment else R.id.action_addTsFragment_to_tacheDetailFragment,
                            bundleOf("id" to resource.data?.id)
                        )
                    }
                    Resource.Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText( context, "Erreur lors de l'ajout du marché", Toast.LENGTH_SHORT ).show()
                        Timber.e("ERROR : $resource.data")
                    }
                    Resource.Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Timber.d("LOADING : $resource.data")
                    }
                }
            })
        }
    }

    override fun onClickedPointage(affaire: Affaire) {
        binding.affaire.text = SpannableStringBuilder("${affaire.aff_id} - ${affaire.client?.cli_prenom} ${affaire.client?.cli_nom}")
        binding.affId.text = affaire.aff_id.toString()
        affaires.clear()
        binding.affairesSv.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }
}