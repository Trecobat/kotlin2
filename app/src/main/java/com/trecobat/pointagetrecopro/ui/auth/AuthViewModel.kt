package com.trecobat.pointagetrecopro.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.entities.Token
import com.trecobat.pointagetrecopro.data.entities.User
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource

class AuthViewModel @ViewModelInject constructor(
    private val repository: MyRepository
) : ViewModel() {

    private val _token = MutableLiveData<Token>()

    fun getToken(): LiveData<Resource<Token>> {
        return repository.getToken()
    }

    fun login(user: User): LiveData<Resource<Token>> {
        return repository.login(user)
    }
}
