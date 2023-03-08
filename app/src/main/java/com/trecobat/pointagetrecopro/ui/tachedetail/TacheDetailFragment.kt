package com.trecobat.pointagetrecopro.ui.tachedetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.util.Base64
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.*
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.*
import com.trecobat.pointagetrecopro.databinding.TacheDetailFragmentBinding
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.formatDate
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getDay
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getHour
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getMinute
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getMonth
import com.trecobat.pointagetrecopro.helper.DateHelper.Companion.getYear
import com.trecobat.pointagetrecopro.helper.StringHelper.Companion.nettoyerChaine
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.set

@AndroidEntryPoint
class TacheDetailFragment : Fragment(), PlansAdapter.PlanItemListener, PointagesAdapter.PointageItemListener {
    private var binding: TacheDetailFragmentBinding by autoCleared()
    private val viewModel: TacheDetailViewModel by viewModels()
    private lateinit var planAdapter: PlansAdapter
    private lateinit var pointagesAdapter: PointagesAdapter
    private lateinit var equipiersKeys: List<Int>
    private lateinit var tempsPausesKeys: List<String>

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
        val tacheObserver = Observer<Resource<Tache>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    bindTache(resource.data!!)
                    binding.progressBar.visibility = View.GONE
                    binding.tacheCl.visibility = View.VISIBLE
                }

                Resource.Status.ERROR ->
                    Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tacheCl.visibility = View.GONE
                }
            }
        }
        viewModel.tache.observe(viewLifecycleOwner, tacheObserver)

        val gedFilesObserver = Observer<Resource<List<GedFiles>>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    if (!resource.data.isNullOrEmpty()) planAdapter.setItems(ArrayList(resource.data))
                    binding.progressBar.visibility = View.GONE
                    binding.plansRv.visibility = View.VISIBLE
                    binding.plansRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.plansRv.visibility = View.GONE
                }
            }
        }
        viewModel.gedFiles.observe(viewLifecycleOwner, gedFilesObserver)

        val pointagesObserver = Observer<Resource<List<Pointage>>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    if (!resource.data.isNullOrEmpty()) pointagesAdapter.setItems(ArrayList(resource.data))
                    binding.progressBar.visibility = View.GONE
                    binding.pointagesRv.visibility = View.VISIBLE
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.plansRv.visibility = View.GONE
                }
            }
        }
        viewModel.pointages.observe(viewLifecycleOwner, pointagesObserver)

        val corpsEtatObserver = Observer<Resource<List<BdcType>>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    if (!resource.data.isNullOrEmpty()) {
                        val bdct = resource.data.map { it.bdct_label }
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
                    Toast.makeText(activity, resource.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {

                }
            }
        }
        viewModel.corpsEtat.observe(viewLifecycleOwner, corpsEtatObserver)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun bindTache(tache: Tache) {
        binding.cliNom.text = tache.affaire?.client?.cli_nom
        binding.affId.text = " - ${tache.affaire?.aff_id}"
        binding.bdctLabel.text = tache.bdc_type.bdct_label
        binding.startDate.text = formatDate(tache.start_date)
        binding.endDate.text = tache.end_date?.let { formatDate(it) }
        binding.cliAdresse1Chantier.text = tache.affaire?.client?.cli_adresse1_chantier
        binding.cliAdresse2Chantier.text =
            if (tache.affaire?.client?.cli_adresse2_chantier != null) " - ${tache.affaire?.client!!.cli_adresse2_chantier}" else ""
        binding.cliCpChantier.text = tache.affaire?.client?.cli_cp_chantier
        binding.cliVilleChantier.text = tache.affaire?.client?.cli_ville_chantier
        binding.buttonJour.text = "${getDay()}/${getMonth()}/${getYear(true)}"
        val heure = "${getHour()}:${getMinute()}"
        binding.buttonHeureDeb.text = heure
        binding.buttonHeureFin.text = heure
        binding.equipiers.visibility = View.GONE

        setupSpinners()
        setupDatePicker()
        setupTimesPicker()
        setupCheckboxEquipe()

        binding.terminerBtn.setOnClickListener { terminer(tache) }

        binding.pointageBtn.setOnClickListener { pointer(tache) }

        binding.mapBtn.setOnClickListener { goToGmap(tache) }

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

    @OptIn(DelicateCoroutinesApi::class)
    private fun terminer(tache: Tache) {
        val terminerTacheObserver = Observer<Resource<Tache>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tacheCl.visibility = View.VISIBLE
                    findNavController().navigate(
                        R.id.action_pointageDiversFragment_to_tachesFragment
                    )
                }
                Resource.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tacheCl.visibility = View.VISIBLE
                    Toast.makeText( context, "La tache n'a pas été terminée", Toast.LENGTH_SHORT ).show()
                    Timber.e(resource.message)
                }
                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tacheCl.visibility = View.GONE
                }
            }
        }
        tache.termine = 1
        Timber.e(tache.toString())
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.updateTache(tache).observe(viewLifecycleOwner, terminerTacheObserver)
        }
    }

    private fun goToGmap(tache: Tache)
    {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val locationName = "${tache.affaire?.client?.cli_adresse1_chantier} ${tache.affaire?.client?.cli_adresse2_chantier} ${tache.affaire?.client?.cli_cp_chantier} ${tache.affaire?.client?.cli_ville_chantier} France"
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
                                    android.R.layout.simple_spinner_item,
                                    equipiers.values.toList()
                                )
                                adapterEquipiers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
        val adapterTypePointage = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valuesTypePointage)
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

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // Temps de pause
        val valuesTempsPause = mutableMapOf<String, String>()
        valuesTempsPause["00:30"] = "30 minutes"
        valuesTempsPause["01:00"] = "1 heure"
        valuesTempsPause["02:00"] = "2 heures"
        valuesTempsPause["Autre"] = "Autre"

        tempsPausesKeys = valuesTempsPause.keys.toList()
        val adapterTempsPause = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valuesTempsPause.values.toList())
        adapterTempsPause.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tempsPause.adapter = adapterTempsPause
        binding.tempsPause.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValue = tempsPausesKeys[position]
                if (selectedValue == "Autre") {
                    binding.tempsPauseAutre.visibility = View.VISIBLE
                } else {
                    binding.tempsPauseAutre.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

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
        val adapterNatureErreur = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, valuesNatureErreur)
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
        pointage.poi_pause = if ( binding.tempsPause.selectedItem == "Autre" ) "0${binding.tempsPauseAutre.text}:00" else tempsPausesKeys[binding.tempsPause.selectedItemPosition]

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
        val pointage = makePointage(tache)

        if (!equipe.isChecked) {
            pointage.poi_eq_id = equipiersKeys[binding.equipiers.selectedItemPosition]
        }
        postPointage(pointage)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun postPointage(pointage: Pointage) {
        GlobalScope.launch(Dispatchers.Main) {
            val postPointageObserver = Observer<Resource<List<Pointage>>> { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tacheCl.visibility = View.VISIBLE
//                        Toast.makeText( context, "Le pointage ${resource.data?.poi_id} a bien été ajouté.", Toast.LENGTH_SHORT ).show()
                        Timber.d("SUCCESS : ${resource.data}")
                    }
                    Resource.Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tacheCl.visibility = View.VISIBLE
                        Toast.makeText( context, "Erreur lors de l'ajout du pointage", Toast.LENGTH_SHORT ).show()
                        Timber.e("ERROR : ${resource.data}")
                    }
                    Resource.Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tacheCl.visibility = View.GONE
                        Timber.d("LOADING : ${resource.data}")
                    }
                }
            }
            viewModel.postPointage(pointage).observe(viewLifecycleOwner, postPointageObserver)
        }
    }

    @SuppressLint("SetWorldReadable", "SetWorldWritable")
    override fun onClickedPlan(ged_file: GedFiles) {
        binding.tacheCl.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        val directory = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            ged_file.gdf_obj_id.toString()
        )
        val file = File(directory, "${nettoyerChaine(ged_file.gdf_cat_label)}.pdf")
        if (file.exists()) {
            findNavController().navigate(
                R.id.action_tacheDetailFragment_to_pdfFragment,
                bundleOf("affId" to ged_file.gdf_obj_id, "catLabel" to ged_file.gdf_cat_label)
            )
        } else {
            val fileObserver = Observer<Resource<MyFile>> { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        val requestPermissionLauncher =
                            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                                if (isGranted) {
                                    if (resource.data?.file_content != null) {
                                        val pdfData = Base64.decode(resource.data.file_content, Base64.DEFAULT)

                                        val newDirectory = File(
                                            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                                            ged_file.gdf_obj_id.toString()
                                        )

                                        if (!directory.exists()) {
                                            directory.mkdirs()
                                        }

                                        val newFile = File(newDirectory, "${nettoyerChaine( ged_file.gdf_cat_label )}.pdf")
                                        newFile.createNewFile()
                                        newFile.setReadable(true, false) // autoriser la lecture
                                        newFile.setWritable(true, false) // autoriser l'écriture
                                        val outputStream = FileOutputStream(file.absolutePath)

                                        val reader = PdfReader(pdfData)
                                        val n = reader.numberOfPages
                                        val document = Document(reader.getPageSizeWithRotation(1))
                                        val writer = PdfCopy(document, outputStream)
                                        document.open()
                                        var i = 0
                                        while (i < n) {
                                            i++
                                            document.newPage()
                                            val page = writer.getImportedPage(reader, i)
                                            writer.addPage(page)
                                        }

                                        // Fermer le document
                                        document.close()

                                        if (newFile.exists()) {
                                            findNavController().navigate(
                                                R.id.action_tacheDetailFragment_to_pdfFragment,
                                                bundleOf("affId" to ged_file.gdf_obj_id, "catLabel" to ged_file.gdf_cat_label)
                                            )
                                        }
                                    }
                                } else {
                                    // La permission a été refusée, informer l'utilisateur
                                    Toast.makeText(requireContext(), "La permission a été refusée", Toast.LENGTH_SHORT).show()
                                }
                            }

                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    Resource.Status.ERROR -> {
                        binding.tacheCl.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        Timber.e("ERROR get_file")
                        Timber.e(resource.message)
                    }
                    Resource.Status.LOADING -> {
                        Timber.d("LOADING get_file")
                    }
                }
            }
            viewModel.getFile(ged_file.gdf_fo_id).observe(viewLifecycleOwner, fileObserver)
        }
    }

    override fun onClickedPointage(pointage: Pointage) {
        TODO("Not yet implemented")
    }

    private fun startSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
        startActivityForResult(intent, 100)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            100 -> {
                if (resultCode == RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val commentaire = binding.commentaire
                    commentaire.setText(result?.get(0))
                }
            }
        }
    }
}