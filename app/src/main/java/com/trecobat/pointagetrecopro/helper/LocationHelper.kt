package com.trecobat.pointagetrecopro.helper

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.suspendCoroutine

class LocationHelper {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        suspend fun getLastLocation(fragment: Fragment) : Location? = suspendCoroutine { continuation ->
            val context = fragment.requireContext()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Demande la permission à l'utilisateur
//                ActivityCompat.requestPermissions(
//                    fragment,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                    LOCATION_PERMISSION_REQUEST_CODE
//                )
            } else {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//                fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
//                    continuation.resume(location)
//                }
            }
        }
    }
}
