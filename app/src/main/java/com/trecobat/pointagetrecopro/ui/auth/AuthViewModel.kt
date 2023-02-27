package com.trecobat.pointagetrecopro.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.trecobat.pointagetrecopro.data.entities.*
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.JWTUtils
import com.trecobat.pointagetrecopro.utils.Resource
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import timber.log.Timber
import java.security.Key

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

    fun getAuthUser(): LiveData<Resource<Equipe>>
    {
        val decodedToken = System.getProperty("token")?.let { JWTUtils.decoded(it) }
        val gson = Gson()
        val tokenData = gson.fromJson(decodedToken, TokenData::class.java)
        val email = tokenData.email
        return repository.getAuthEquipe(email)
    }
}
