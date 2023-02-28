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
import com.trecobat.pointagetrecopro.data.entities.User
import com.trecobat.pointagetrecopro.databinding.AuthFragmentBinding
import com.trecobat.pointagetrecopro.utils.Resource
import com.trecobat.pointagetrecopro.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
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
        setHasOptionsMenu(false) // masque le menu pour ce fragment
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.navigationIcon = null
        binding = AuthFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Si un token existe déjà, l'utilisateur est déjà connecté, on peut l'envoyer directement sur la liste des chantiers
        viewModel.getToken().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    if (it.data?.token != null) {
                        System.setProperty("token", it.data.token)
                        viewModel.getAuthUser().observe(viewLifecycleOwner, Observer {  res ->
                            when (res.status) {
                                Resource.Status.SUCCESS -> {
                                    System.setProperty("equipe",
                                        res.data?.eqvp_id.toString()
                                    )
                                    findNavController().navigate(
                                        R.id.action_authFragment_to_tachesFragment
                                    )
                                }
                                Resource.Status.ERROR -> {}

                                Resource.Status.LOADING -> {}
                            }
                        })
                    }
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(activity, "Erreur d'authentification : ${it.message}", Toast.LENGTH_SHORT).show()
                    Timber.e(it.message)
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })
        binding.connexion.setOnClickListener {
            val user = User(
                email = binding.email.text.toString(),
                password = binding.password.text.toString()
            )
            GlobalScope.launch(Dispatchers.Main) {
                viewModel.login(user).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            Timber.e(it.data.toString())
                            if (it.data?.token != null) {
                                System.setProperty("token", it.data.token)
                                findNavController().navigate(
                                    R.id.action_authFragment_to_tachesFragment
                                )
                            } else {
                                binding.authCl.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(activity, "Erreur d'authentification : ${it.data?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Resource.Status.ERROR -> {
                            binding.authCl.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(activity, "Erreur d'authentification", Toast.LENGTH_SHORT).show()
                            Timber.e(it.message)
                        }

                        Resource.Status.LOADING -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.authCl.visibility = View.GONE
                        }
                    }
                })
            }
        }
    }
}
