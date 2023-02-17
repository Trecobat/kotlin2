package com.trecobat.pointagetrecopro.ui.tachedetail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.trecobat.pointagetrecopro.data.entities.GedFiles
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.local.AppDatabase
import com.trecobat.pointagetrecopro.databinding.TacheDetailFragmentBinding
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.formatDate
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getDay
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getHour
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getMinute
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getMonth
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getYear
import com.trecobat.pointagetrecopro.helper.ViewHelper
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
import kotlin.collections.set


@AndroidEntryPoint
class TacheDetailFragment : Fragment(), PlansAdapter.PlanItemListener, PointagesAdapter.PointageItemListener {
    private var binding: TacheDetailFragmentBinding by autoCleared()
    private val viewModel: TacheDetailViewModel by viewModels()
    private lateinit var planAdapter: PlansAdapter
    private lateinit var pointagesAdapter: PointagesAdapter
    private lateinit var equipiersKeys: List<Int>

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
        setupObservers(view)
    }

    private fun setupRecyclerView() {
        planAdapter = PlansAdapter(this)
        binding.plansRv.layoutManager = LinearLayoutManager(requireContext())
        binding.plansRv.adapter = planAdapter

        pointagesAdapter = PointagesAdapter(this)
        binding.pointagesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.pointagesRv.adapter = pointagesAdapter
    }

    private fun setupObservers(view: View) {
        viewModel.tache.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    bindTache(it.data!!, view)
                    binding.progressBar.visibility = View.GONE
                    binding.jour.visibility = View.GONE
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
                    if (!it.data.isNullOrEmpty()) planAdapter.setItems(ArrayList(it.data))
                    binding.progressBar.visibility = View.GONE
//                    binding.plansRv.visibility = View.VISIBLE
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.plansRv.visibility = View.GONE
                }
            }
        })

        viewModel.pointages.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) pointagesAdapter.setItems(ArrayList(it.data))
                    binding.progressBar.visibility = View.GONE
                    binding.pointagesRv.visibility = View.VISIBLE
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.plansRv.visibility = View.GONE
                }
            }
        })

        viewModel.corpsEtat.observe(viewLifecycleOwner, Observer { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        val bdct = it.data.map { it.bdct_label }
                        val adapterBdct = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            bdct
                        )
                        adapterBdct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.corpsEtat.adapter = adapterBdct
                    }
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {

                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun bindTache(tache: Tache, view: View) {
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
        ViewHelper.setupDatePicker(view)
        setupTimesPicker()
        setupCheckboxEquipe()

        binding.pointageBtn.setOnClickListener { pointer(tache) }
    }

    private fun setupCheckboxEquipe() {
        binding.equipe.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.equipiers.visibility = View.GONE
            } else {
                viewModel.getEquipiers(5).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            if (!it.data.isNullOrEmpty()) {
                                val equipiers = mutableMapOf<Int, String>()
                                for (row in it.data) {
                                    val key = row.eevp_id
                                    val value = "${row.eevp_prenom} ${row.eevp_nom}"
                                    equipiers[key] = value
                                }

                                equipiersKeys = equipiers.keys.toList()
                                val adapterEquipiers = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    equipiers.values.toList()
                                )
                                adapterEquipiers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.equipiers.adapter = adapterEquipiers
                            }
                        }

                        Resource.Status.ERROR -> {
                            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                        }

                        Resource.Status.LOADING -> {

                        }
                    }
                })
                binding.equipiers.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    private fun setupTimesPicker() {
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

        binding.heureDeb.hour = getHour().toInt()
        binding.heureDeb.minute = getMinute().toInt()
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

        binding.heureFin.layoutParams.height = Resources.getSystem().displayMetrics.heightPixels - 350
        binding.heureFin.hour = getHour().toInt()
        binding.heureFin.minute = getMinute().toInt()
        binding.heureFin.setIs24HourView(true)
        binding.heureFin.setOnTimeChangedListener { _, hourOfDay, minute ->
            var text = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
            text += ":"
            text += if (minute < 10) "0$minute" else "$minute"
            binding.buttonHeureFin.text = text
        }
        binding.heureFin.bringToFront()
    }

    private fun setupSpinners() {
        // Type de pointage
        val valuesTypePointage = listOf("Marché", "TS", "SAV")
        val adapterTypePointage =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valuesTypePointage)
        adapterTypePointage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typePointage.adapter = adapterTypePointage

        binding.typePointage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValue = valuesTypePointage[position]
                if (selectedValue == "Marché") {
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
        val valuesNatureErreur = listOf(
            "Erreur artisan",
            "Erreur Métreur",
            "Erreur Dessinateur",
            "Erreur Conducteur",
            "Erreur Fournisseur"
        )
        val adapterNatureErreur =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valuesNatureErreur)
        adapterNatureErreur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.natureErreur.adapter = adapterNatureErreur
    }

    private fun makePointage(tache: Tache): Pointage
    {
        val dateEl = binding.jour
        val heureDebEl = binding.heureDeb
        val heureFinEl = binding.heureFin
        val coffretElecEl = binding.coffretElec
        val remblaisEl = binding.remblais
        val date = "${dateEl.year}-${dateEl.month + 1}-${dateEl.dayOfMonth}"

        val pointage = Pointage()
        pointage.poi_tache_id = tache.id
        pointage.poi_type = binding.typePointage.selectedItem.toString()
        pointage.poi_debut = "$date ${heureDebEl.hour}:${heureDebEl.minute}:00"
        pointage.poi_fin = "$date ${heureFinEl.hour}:${heureFinEl.minute}:00"
        pointage.poi_commentaire = binding.commentaire.text.toString()

        if (pointage.poi_type === "Marché") {
            pointage.poi_coffret = if (coffretElecEl.isChecked) 1 else 0
            pointage.poi_remblais = if (remblaisEl.isChecked) 1 else 0
        } else {
            pointage.poi_corps_etat = binding.corpsEtat.selectedItem as String
            pointage.poi_nature_erreur = binding.natureErreur.selectedItem as String
        }

        return pointage
    }

    private fun pointer(tache: Tache) {
        val equipe = binding.equipe

        if (!equipe.isChecked) {
            val pointage = makePointage(tache)
            pointage.poi_eq_id = equipiersKeys[binding.equipiers.selectedItemPosition]
            postPointage(pointage)
        } else {
            var isHandled = false
            viewModel.equipiers.observe(viewLifecycleOwner, Observer { result ->
                if (!isHandled) {
                    isHandled = true
                    when (result.status) {
                        Resource.Status.SUCCESS -> {
                            val equipiers = result.data
                            if (!equipiers.isNullOrEmpty()) {
                                Timber.e(equipiers.toString())
                                for (row in equipiers) {
                                    val pointage = makePointage(tache)
                                    pointage.poi_eq_id = row.eevp_id
                                    postPointage(pointage)
                                }
                            }
                        }
                        Resource.Status.ERROR -> {
                            val error = result.message ?: "Une erreur s'est produite"
                            Timber.e(error)
                        }
                        Resource.Status.LOADING -> {
                            // Afficher une indication de chargement à l'utilisateur
                        }
                    }
                }
            })
        }
    }

    private fun postPointage(pointage: Pointage) {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.postPointage(pointage).observe(viewLifecycleOwner, Observer { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
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
                    val directory = File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                        ged_file.gdf_obj_id.toString()
                    )

                    if (!directory.exists()) {
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

    override fun onClickedPointage(pointage: Pointage) {
        TODO("Not yet implemented")
    }
}