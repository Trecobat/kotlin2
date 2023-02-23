package com.trecobat.pointagetrecopro.ui.tachedetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        setupObservers()
    }

    private fun setupRecyclerView() {
        planAdapter = PlansAdapter(this)
        binding.plansRv.layoutManager = LinearLayoutManager(requireContext())
        binding.plansRv.adapter = planAdapter

        pointagesAdapter = PointagesAdapter(this, requireContext(), viewModel, viewLifecycleOwner)
        binding.pointagesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.pointagesRv.adapter = pointagesAdapter
    }

    private fun setupObservers() {
        viewModel.tache.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    bindTache(it.data!!)
                    binding.progressBar.visibility = View.GONE
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
                    binding.plansRv.visibility = View.VISIBLE
                    binding.plansRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
    private fun bindTache(tache: Tache) {
        binding.cliNom.text = tache.affaire.client.cli_nom
        binding.affId.text = " - ${tache.affaire.aff_id}"
        binding.bdctLabel.text = tache.bdc_type.bdct_label
        binding.startDate.text = formatDate(tache.start_date)
        binding.endDate.text = tache.end_date?.let { formatDate(it) }
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

        binding.mapBtn.setOnClickListener { goToGmap(tache) }
    }

    private fun goToGmap(tache: Tache)
    {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val locationName = "${tache.affaire.client.cli_adresse1_chantier} ${tache.affaire.client.cli_adresse2_chantier} ${tache.affaire.client.cli_cp_chantier} ${tache.affaire.client.cli_ville_chantier} France"
        val addresses: List<Address> = geocoder.getFromLocationName(locationName, 1) as List<Address>
        if (addresses.isNotEmpty()) {
            val latitude = addresses[0].latitude
            val longitude = addresses[0].longitude
            val uri = "http://maps.google.com/maps?q=$latitude,$longitude"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    private fun setupDatePicker()
    {
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

    private fun makeTimePickerDialog(binding: TacheDetailFragmentBinding, timing: String)
    {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minuteOfHour ->
            var text = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
            text += ":"
            text += if (minuteOfHour < 10) "0$minuteOfHour" else "$minuteOfHour"

            val buttonBinding = if ( timing == "deb" ) binding.buttonHeureDeb else binding.buttonHeureFin
            buttonBinding.text = text
        }, hour, minute, true)

        // Optional customizations:
        timePickerDialog.setCanceledOnTouchOutside(false) // Prevent dialog from being dismissed when user touches outside of it
        timePickerDialog.show()
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    private fun setupTimesPicker() {
        // HEURE DEBUT
        binding.buttonHeureDeb.setOnClickListener {
            makeTimePickerDialog(binding, "deb")
        }

        // HEURE FIN
        binding.buttonHeureFin.setOnClickListener {
            makeTimePickerDialog(binding, "fin")
        }
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
        val coffretElecEl = binding.coffretElec
        val remblaisEl = binding.remblais

        val pointage = Pointage()
        pointage.poi_tache_id = tache.id
        pointage.poi_type = binding.typePointage.selectedItem.toString()
        pointage.poi_commentaire = binding.commentaire.text.toString()

        pointage.poi_debut = "${formatDate ( date = binding.buttonJour.text as String, inputPattern = "dd/MM/yy", outputPattern = "yyyy-MM-dd" )} ${binding.buttonHeureDeb.text}:00"
        pointage.poi_fin = "${formatDate ( date = binding.buttonJour.text as String, inputPattern = "dd/MM/yy", outputPattern = "yyyy-MM-dd" )} ${binding.buttonHeureFin.text}:00"

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
                        Toast.makeText( context, "Le pointage ${pointage.poi_id} a bien été ajouté.", Toast.LENGTH_SHORT ).show()
                        Timber.d("SUCCESS : $pointage")
                    }
                    Resource.Status.ERROR -> {
                        Timber.e("ERROR : $pointage")
                    }
                    Resource.Status.LOADING -> {
                        Toast.makeText( context, "Erreur lors de l'ajout du pointage", Toast.LENGTH_SHORT ).show()
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