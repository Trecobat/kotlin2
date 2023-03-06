package com.trecobat.pointagetrecopro.ui.pdf

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.trecobat.pointagetrecopro.databinding.PdfBinding
import com.trecobat.pointagetrecopro.helper.StringHelper.Companion.nettoyerChaine
import com.trecobat.pointagetrecopro.utils.autoCleared
import timber.log.Timber
import java.io.File
import kotlin.properties.Delegates

class PdfFragment: Fragment() {
    private var binding: PdfBinding by autoCleared()
    private val viewModel: PdfViewModel by viewModels()
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var affId by Delegates.notNull<Int>()
    private lateinit var catLabel: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetJavaScriptEnabled", "RequiresFeature")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { that ->
            affId = that.getInt("affId")
            catLabel = that.getString("catLabel").toString()
            arguments?.getInt("affId")?.let { viewModel.startAffId(it) }
            arguments?.getString("catLabel")?.let { viewModel.startCatLabel(it) }
            binding.catLabel.text = catLabel
            val directory = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                affId.toString()
            )
            val file = File(directory, "${nettoyerChaine(catLabel)}.pdf")
            binding.pdfView.fromFile(file)
                .enableSwipe(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .password(null)
                .load()
        }
    }
}