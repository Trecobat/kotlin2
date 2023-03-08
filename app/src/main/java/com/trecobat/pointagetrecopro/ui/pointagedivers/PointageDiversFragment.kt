package com.trecobat.pointagetrecopro.ui.pointagedivers

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.trecobat.pointagetrecopro.data.entities.Equipier
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.databinding.PointageDiversFragmentBinding
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.formatDate
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getDate
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.trecobat.pointagetrecopro.data.entities.GedFiles
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PointageDiversFragment: Fragment(), PointageDiversAdapter.PointageDiversItemListener {

    private var binding: PointageDiversFragmentBinding by autoCleared()
    private val viewModel: PointageDiversViewModel by viewModels()
    private lateinit var adapter: PointageDiversAdapter

    private var dureeHeureKeys: MutableList<String> = mutableListOf()
    private var dureeMinuteKeys: MutableList<String> = mutableListOf()
    private lateinit var equipiersKeys: List<Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PointageDiversFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()
        setupSpinners()
        setupRecyclerView()
        setupCheckboxEquipe()
        setupTextToSpeech()

        binding.ajouterPointageDivers.setOnClickListener { pointer() }
    }

    private fun setupSpinners()
    {
        // Type de pointage
        val valuesType = listOf(
            "Atelier",
            "Divers",
            "Fournisseur",
            "Gazole",
            "Garage",
            "Itempérie"
        )
        val adapterType = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, valuesType)
        adapterType.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.type.adapter = adapterType

        // Duree en heure
        val dureeHeure: MutableList<String> = mutableListOf()
        for (i in 0..8) {
            dureeHeureKeys.add("0$i")
            dureeHeure.add ( if ( i < 2 ) "$i heure" else "$i heures" )
        }
        val adapterDureeHeure = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, dureeHeure)
        adapterDureeHeure.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.dureeHeure.adapter = adapterDureeHeure

        // Duree en minute
        val dureeMinute: MutableList<String> = mutableListOf()
        for (i in 0..59) {
            dureeMinuteKeys.add(if ( i < 10 ) "0$i" else "$i")
            dureeMinute.add ( if (i < 2) "$i minute" else "$i minutes" )
        }
        val adapterDureeMinute = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, dureeMinute)
        adapterDureeMinute.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.dureeMinute.adapter = adapterDureeMinute
    }

    private fun setupDatePicker()
    {
        binding.buttonJour.text = formatDate(date = getDate(), inputPattern = "yyyy-MM-dd", outputPattern = "dd/MM/yy")
        binding.buttonJour.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, yearDate, monthOfYear, dayOfMonth ->
                var text = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                text += "/"
                text += if (monthOfYear < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
                text += "/${yearDate.toString().substring(2)}"
                binding.buttonJour.text = text
            }, year, month, day)

            // Optional customizations:
            datePickerDialog.show()
        }
    }

    private fun setupCheckboxEquipe() {
        binding.equipiers.visibility = View.GONE
        binding.equipe.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.equipiers.visibility = View.GONE
            } else {
                val equipiersObserver = Observer<Resource<List<Equipier>>> { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            if (!resource.data.isNullOrEmpty()) {
                                val equipiers = mutableMapOf<Int, String>()
                                for (row in resource.data) {
                                    val key = row.eevp_id
                                    val value = "${row.eevp_prenom} ${row.eevp_nom}"
                                    equipiers[key] = value
                                }

                                equipiersKeys = equipiers.keys.toList()
                                val adapterEquipiers = ArrayAdapter(
                                    requireContext(),
                                    R.layout.simple_spinner_item,
                                    equipiers.values.toList()
                                )
                                adapterEquipiers.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                                binding.equipiers.adapter = adapterEquipiers
                            }
                        }

                        Resource.Status.ERROR -> {
                            Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                        }

                        Resource.Status.LOADING -> {

                        }
                    }
                }
                viewModel.getEquipiers().observe(viewLifecycleOwner, equipiersObserver)
                binding.equipiers.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTextToSpeech() {
        binding.commentaire.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.commentaire.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (binding.commentaire.right - drawableEnd.bounds.width())) {
                    // Action à effectuer lors du toucher sur le drawable end
                    startSpeechInput()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun startSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(com.trecobat.pointagetrecopro.R.string.speech_prompt))
        startActivityForResult(intent, 100)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            100 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val commentaire = binding.commentaire
                    commentaire.setText(result?.get(0))
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PointageDiversAdapter(this, requireContext(), viewModel, viewLifecycleOwner)
        binding.pointagesDiversRv.layoutManager = LinearLayoutManager(requireContext())
        binding.pointagesDiversRv.adapter = adapter

        val pointagesDiversObserver = Observer<Resource<List<Pointage>>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    if (!resource.data.isNullOrEmpty()) adapter.setItems(ArrayList(resource.data))
                    binding.progressBar.visibility = View.GONE
                    binding.pointagesDiversRv.visibility = View.VISIBLE
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.pointagesDiversRv.visibility = View.GONE
                }
            }
        }
        viewModel.pointageDivers.observe(viewLifecycleOwner, pointagesDiversObserver)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun pointer()
    {
        val pointage = Pointage()
        pointage.poi_debut = "${formatDate( date = binding.buttonJour.text.toString(), inputPattern = "dd/MM/yy", outputPattern = "yyyy-MM-dd" )} 00:00:00"
        pointage.poi_fin = "${formatDate( date = binding.buttonJour.text.toString(), inputPattern = "dd/MM/yy", outputPattern = "yyyy-MM-dd" )} 00:00:00"
        pointage.poi_duree = "${dureeHeureKeys[binding.dureeHeure.selectedItemPosition]}:${dureeMinuteKeys[binding.dureeMinute.selectedItemPosition]}"
        pointage.poi_type = binding.type.selectedItem.toString()
        pointage.poi_commentaire = binding.commentaire.text.toString()

        val postPointageObserver = Observer<Resource<List<Pointage>>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "Le pointage a bien été créé", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.pointageCl.visibility = View.VISIBLE
                }

                Resource.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.pointageCl.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Erreur lors du pointage", Toast.LENGTH_SHORT).show()
                    Timber.e(resource.message)
                }

                Resource.Status.LOADING -> {
                    binding.pointageCl.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        val equipe = binding.equipe

        GlobalScope.launch(Dispatchers.Main) {
            if (!equipe.isChecked) {
                pointage.poi_eq_id = equipiersKeys[binding.equipiers.selectedItemPosition]
            }
            viewModel.postPointage(pointage).observe(viewLifecycleOwner, postPointageObserver)
        }
    }

    override fun onClickedPointageDivers(pointage: Pointage) {
        TODO("Not yet implemented")
    }
}