package com.trecobat.pointagetrecopro.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.entities.*
import com.trecobat.pointagetrecopro.databinding.AuthFragmentBinding
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var binding: AuthFragmentBinding by autoCleared()
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.navigationIcon = null
        binding = AuthFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var executed = false
        // Si un token existe déjà, l'utilisateur est déjà connecté, on peut l'envoyer directement sur la liste des chantiers
        val authUserObserver = Observer<Resource<Equipe>> { resource ->
            if (!executed) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        Timber.e(resource.data.toString())
                        System.setProperty("equipe",
                            resource.data?.eqvp_id.toString()
                        )
                        executed = true
                        findNavController().navigate(
                            R.id.action_authFragment_to_tachesFragment
                        )
                    }
                    Resource.Status.ERROR -> {}

                    Resource.Status.LOADING -> {}
                }
            }
        }

        val getTokenObserver = Observer<Resource<Token>> { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    if (resource.data?.token != null) {
                        System.setProperty("token", resource.data.token)
                        viewModel.getAuthUser(resource.data.token).observe(viewLifecycleOwner, authUserObserver)
                    }
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, "Erreur d'authentification : ${resource.message}", Toast.LENGTH_SHORT).show()
                    Timber.e(resource.message)
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.getToken().observe(viewLifecycleOwner, getTokenObserver)
        binding.connexion.setOnClickListener {
            val user = User(
                email = binding.email.text.toString(),
                password = binding.password.text.toString()
            )
            GlobalScope.launch(Dispatchers.Main) {
                val loginObserver = Observer<Resource<Token>> { resource ->
                    if (!executed) {
                        when (resource.status) {
                            Resource.Status.SUCCESS -> {
                                Timber.e(resource.data.toString())
                                if (resource.data?.token != null) {
                                    System.setProperty("token", resource.data.token)
                                    viewModel.getAuthUser(resource.data.token).observe(viewLifecycleOwner, authUserObserver)
                                } else {
                                    binding.authCl.visibility = View.VISIBLE
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        activity,
                                        "Erreur d'authentification : ${resource.data?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            Resource.Status.ERROR -> {
                                binding.authCl.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    activity,
                                    "Erreur d'authentification",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Timber.e(resource.message)
                            }

                            Resource.Status.LOADING -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.authCl.visibility = View.GONE
                            }
                        }
                    }
                }
                viewModel.login(user).observe(viewLifecycleOwner, loginObserver)
            }
        }
    }
}
