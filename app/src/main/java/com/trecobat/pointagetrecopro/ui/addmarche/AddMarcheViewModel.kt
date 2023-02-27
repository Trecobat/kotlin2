package com.trecobat.pointagetrecopro.ui.addmarche

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.repository.MyRepository

class AddMarcheViewModel @ViewModelInject constructor(
    private val repository: MyRepository
) : ViewModel()