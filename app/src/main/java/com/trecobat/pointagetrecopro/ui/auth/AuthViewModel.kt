package com.trecobat.pointagetrecopro.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.trecobat.pointagetrecopro.data.entities.Equipe
import com.trecobat.pointagetrecopro.data.entities.Token
import com.trecobat.pointagetrecopro.data.entities.TokenData
import com.trecobat.pointagetrecopro.data.entities.User
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.JWTUtils
import com.trecobat.pointagetrecopro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    fun getToken(): LiveData<Resource<Token>> {
        return repository.getToken()
    }

    fun login(user: User): LiveData<Resource<Token>> {
        return repository.login(user)
    }

    fun getAuthUser(token: String): LiveData<Resource<Equipe>>
    {
        val decodedToken = JWTUtils.decoded(token)
        val gson = Gson()
        val tokenData = gson.fromJson(decodedToken, TokenData::class.java)
        val email = tokenData.email
        return repository.getAuthEquipe(email)
    }
}
