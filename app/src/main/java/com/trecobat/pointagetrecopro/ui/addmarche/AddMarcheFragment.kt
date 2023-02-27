package com.trecobat.pointagetrecopro.ui.addmarche

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.trecobat.pointagetrecopro.databinding.AddMarcheFragmentBinding
import com.trecobat.pointagetrecopro.ui.auth.AuthViewModel
import com.trecobat.pointagetrecopro.utils.autoCleared

class AddMarcheFragment : Fragment()  {

    private var binding: AddMarcheFragmentBinding by autoCleared()
    private val viewModel: AuthViewModel by viewModels()

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
        setupObservers()
    }

    private fun setupObservers() {

    }

    @SuppressLint("SetTextI18n")
    private fun bind() {

    }
}