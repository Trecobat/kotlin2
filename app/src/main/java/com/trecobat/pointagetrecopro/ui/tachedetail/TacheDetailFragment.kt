package com.trecobat.pointagetrecopro.ui.tachedetail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.GedFiles
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.local.AppDatabase
import com.trecobat.pointagetrecopro.databinding.TacheDetailFragmentBinding
import com.trecobat.pointagetrecopro.helper.DateHelper
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.formatDate
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getDay
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getHour
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getMinute
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getMonth
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getTime
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getYear
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_pointage.*
import kotlinx.android.synthetic.main.item_tache.*
import kotlinx.android.synthetic.main.tache_detail_fragment.*
import kotlinx.android.synthetic.main.tache_detail_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*


@AndroidEntryPoint
class TacheDetailFragment : Fragment(), PlansAdapter.PlanItemListener {
    private var binding: TacheDetailFragmentBinding by autoCleared()
    private val viewModel: TacheDetailViewModel by viewModels()
    private lateinit var adapter: PlansAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TacheDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("id")?.let { viewModel.start(it) }
        setupRecyclerView()
        setupObservers()
//        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayoutTacheDetailFragment)
//        swipeRefreshLayout.setOnRefreshListener {
//            findNavController().navigate(
//                R.id.action_tachesFragment_refresh
//            )
//        }
    }

    private fun setupRecyclerView() {
        adapter = PlansAdapter(this)
        binding.plansRv.layoutManager = LinearLayoutManager(requireContext())
        binding.plansRv.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.tache.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    bindTache(it.data!!)
                    binding.progressBar.visibility = View.GONE
                    binding.jour.visibility = View.GONE
                    binding.validerJour.visibility = View.GONE
                    binding.heureDeb.visibility = View.GONE
                    binding.validerHeureDeb.visibility = View.GONE
                    binding.heureFin.visibility = View.GONE
                    binding.validerHeureFin.visibility = View.GONE
                    binding.tacheCl.visibility = View.VISIBLE
                }

                Resource.Status.ERROR ->
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tacheCl.visibility = View.GONE
                }
            }
        })

        viewModel.gedFiles.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) adapter.setItems(ArrayList(it.data))
                    binding.progressBar.visibility = View.GONE
//                    binding.plansRv.visibility = View.VISIBLE
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    Timber.e(it.message)
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.plansRv.visibility = View.GONE
                }
            }
        })

        viewModel.corpsEtat.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        val bdct = it.data.map { it.bdct_label }
                        val adapterBdct = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bdct)
                        adapterBdct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.corpsEtat.adapter = adapterBdct
                    }
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    Timber.e(it.message)
                }

                Resource.Status.LOADING -> {

                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun bindTache(tache: Tache) {
        Timber.i("Ma tache : $tache")
        binding.cliNom.text = tache.affaire.client.cli_nom
        binding.affId.text = " - ${tache.affaire.aff_id}"
        binding.bdctLabel.text = tache.bdc_type.bdct_label
        binding.startDate.text = formatDate(tache.start_date)
        binding.endDate.text = formatDate(tache.end_date)
        binding.cliAdresse1Chantier.text = tache.affaire.client.cli_adresse1_chantier
        binding.cliAdresse2Chantier.text =
            if (tache.affaire.client.cli_adresse2_chantier != null) " - ${tache.affaire.client.cli_adresse2_chantier}" else ""
        binding.cliCpChantier.text = tache.affaire.client.cli_cp_chantier
        binding.cliVilleChantier.text = tache.affaire.client.cli_ville_chantier
        binding.buttonJour.text = "${getDay()}/${getMonth()}/${getYear(true)}"
        val heure = "${getHour()}:${getMinute()}"
        binding.buttonHeureDeb.text = heure
        binding.buttonHeureFin.text = heure
        binding.equipiers.visibility = View.GONE

        setupSpinners()
        setupDatePicker()
        setupTimesPicker()
        setupCheckboxEquipe()

        binding.pointageBtn.setOnClickListener { pointer(tache) }
    }

    private fun setupCheckboxEquipe()
    {
        binding.equipe.setOnCheckedChangeListener { _, isChecked ->
            if ( isChecked ) {
                Timber.d("Je passe par là avant une action humaine")
                binding.equipiers.visibility = View.GONE
            } else {
                viewModel.getEquipiers(5).observe(viewLifecycleOwner, Observer {
                    Timber.e(it.toString())
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            if (!it.data.isNullOrEmpty()) {
                                val equipier = it.data.map { "${it.eevp_prenom ?: ""} ${it.eevp_nom ?: ""}" }
                                val adapterEquipiers = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, equipier)
                                adapterEquipiers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.equipiers.adapter = adapterEquipiers
                            }
                        }

                        Resource.Status.ERROR -> {
                            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                            Timber.e("Erreur lors de la récupération des equipiers : ${it.message}")
                        }

                        Resource.Status.LOADING -> {

                        }
                    }
                })
                binding.equipiers.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupDatePicker()
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.buttonJour.setOnClickListener {
            binding.jour.visibility = View.VISIBLE
            binding.validerJour.visibility = View.VISIBLE
            binding.pointageBtn.visibility = View.GONE
        }

        binding.jour.init(year, month, day, null)
        binding.jour.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            var text = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            text +="/"
            text += if (monthOfYear < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
            text +="/${year.toString().substring(2)}"
            binding.buttonJour.text =  text
        }
        binding.jour.bringToFront()


        binding.validerJour.setOnClickListener {
            binding.jour.visibility = View.GONE
            binding.validerJour.visibility = View.GONE
            binding.pointageBtn.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupTimesPicker()
    {
        // HEURE DEBUT
        binding.buttonHeureDeb.setOnClickListener {
            binding.heureDeb.visibility = View.VISIBLE
            binding.validerHeureDeb.visibility = View.VISIBLE
            binding.pointageBtn.visibility = View.GONE
        }

        binding.validerHeureDeb.setOnClickListener {
            binding.heureDeb.visibility = View.GONE
            binding.validerHeureDeb.visibility = View.GONE
            binding.pointageBtn.visibility = View.VISIBLE
        }

        binding.heureDeb.hour = DateHelper.getHour()
        binding.heureDeb.minute = DateHelper.getMinute()
        binding.heureDeb.setIs24HourView(true)
        binding.heureDeb.setOnTimeChangedListener { _, hourOfDay, minute ->
            var text = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
            text += ":"
            text += if (minute < 10) "0$minute" else "$minute"
            binding.buttonHeureDeb.text = text
        }
        binding.heureDeb.bringToFront()

        // HEURE FIN
        binding.buttonHeureFin.setOnClickListener {
            binding.heureFin.visibility = View.VISIBLE
            binding.validerHeureFin.visibility = View.VISIBLE
            binding.pointageBtn.visibility = View.GONE
        }

        binding.validerHeureFin.setOnClickListener {
            binding.heureFin.visibility = View.GONE
            binding.validerHeureFin.visibility = View.GONE
            binding.pointageBtn.visibility = View.VISIBLE
        }

        binding.heureFin.hour = DateHelper.getHour()
        binding.heureFin.minute = DateHelper.getMinute()
        binding.heureFin.setIs24HourView(true)
        binding.heureFin.setOnTimeChangedListener { _, hourOfDay, minute ->
            var text = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
            text += ":"
            text += if (minute < 10) "0$minute" else "$minute"
            binding.buttonHeureFin.text = text
        }
        binding.heureFin.bringToFront()
    }

    private fun setupSpinners()
    {
        // Type de pointage
        val valuesTypePointage = listOf("Marché", "TS", "SAV")
        val adapterTypePointage = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valuesTypePointage)
        adapterTypePointage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typePointage.adapter = adapterTypePointage

        binding.typePointage.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedValue = valuesTypePointage[position]
                if ( selectedValue == "Marché" ) {
                    binding.coffretElec.visibility = View.VISIBLE
                    binding.remblais.visibility = View.VISIBLE
                    binding.corpsEtat.visibility = View.GONE
                    binding.natureErreur.visibility = View.GONE
                } else {
                    binding.coffretElec.visibility = View.GONE
                    binding.remblais.visibility = View.GONE
                    binding.corpsEtat.visibility = View.VISIBLE
                    binding.natureErreur.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        // Nature de l'erreur
        val valuesNatureErreur = listOf("Erreur artisan", "Erreur Métreur", "Erreur Dessinateur", "Erreur Conducteur", "Erreur Fournisseur")
        val adapterNatureErreur = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valuesNatureErreur)
        adapterNatureErreur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.natureErreur.adapter = adapterNatureErreur
    }

    private fun pointer(tache: Tache) {
        GlobalScope.launch(Dispatchers.Main) {
            Timber.d("GlobalScope.launch")

            val pointage = Pointage(
                poi_tache_id = tache.id,
                poi_debut = getTime(),
                poi_eq_id = 5 // à changer avec l'équipe du user connecté
            )

            Timber.d("Mon pointage : $pointage")

            viewModel.postPointage(pointage).observe(viewLifecycleOwner, Observer { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        findNavController().navigate(
                            R.id.action_tacheDetailFragment_refresh,
                            bundleOf("id" to pointage.poi_tache_id)
                        )
                        Timber.d("SUCCESS : $pointage")
                    }
                    Resource.Status.ERROR -> {
                        Timber.e("ERROR : $pointage")
                    }
                    Resource.Status.LOADING -> {
                        Timber.d("LOADING : $pointage")
                    }
                }
            })

//            val location = getLastLocation(this@TacheDetailFragment)
//            Timber.d("Location : ")
//            if (location != null) {
//                Timber.i(location.toString())
//            } else {
//                Timber.e("Pas de location")
//            }
        }
    }

    override fun onClickedPlan(ged_file: GedFiles) {
//        val url = "http://intranet.trecoland.fr/files/$gdf_fo_id"
        val url = "https://www.orimi.com/pdf-test.pdf"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.e(call.toString())
                Timber.e(e.toString())
                Toast.makeText(
                    activity,
                    "Erreur lors du téléchargement du plan",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    val directory = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), ged_file.gdf_obj_id.toString())

                    if ( !directory.exists() ) {
                        directory.mkdirs()
                    }

                    val file = File(directory, "${ged_file.gdf_cat_label}.pdf")
                    val inputStream = URL(url).openStream()
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }

                    if (ged_file.local_storage == null) {
                        AppDatabase.getDatabase(requireContext()).myDao()
                            .updateLocalStorage(ged_file.gdf_fo_id, file.absolutePath)
                    }

                    if (file.exists()) {
                        val uri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.fileprovider",
                            file
                        )
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(uri, "application/pdf")
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        requireContext().startActivity(intent)
                    }
                }
            }
        })
    }
}