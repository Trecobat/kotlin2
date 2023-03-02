package com.trecobat.pointagetrecopro.ui.pdf

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.trecobat.pointagetrecopro.databinding.PdfBinding
import com.trecobat.pointagetrecopro.utils.autoCleared
import kotlinx.android.synthetic.main.pdf.*
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

        scaleGestureDetector = ScaleGestureDetector(requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                binding.pdf.scaleX = scaleFactor
                binding.pdf.scaleY = scaleFactor
                return true
            }
        })

        binding.pdf.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            affId = it.getInt("affId")
            catLabel = it.getString("catLabel").toString()
        }
        arguments?.getInt("affId")?.let { viewModel.startAffId(it) }
        arguments?.getString("catLabel")?.let { viewModel.startCatLabel(it) }
        val directory = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            affId.toString()
        )
        val file = File(directory, "$catLabel.pdf")
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(fileDescriptor)

        // Afficher la première page du PDF dans une ImageView
        val page = renderer.openPage(0)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        binding.pdf.setImageBitmap(bitmap)
        binding.catLabel.text = catLabel

        // Fermer le renderer et la page
        page.close()
        renderer.close()
    }
}