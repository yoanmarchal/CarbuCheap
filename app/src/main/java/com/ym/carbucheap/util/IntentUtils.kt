package com.ym.carbucheap.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object IntentUtils {

    /**
     * Opens navigation to the given coordinates.
     * Tries Google Maps first, then falls back to generic geo intent.
     */
    fun openNavigation(context: Context, latitude: Double, longitude: Double, label: String) {
        // Try Google Maps navigation
        val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            context.startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            // Fallback: generic geo intent (Waze, other nav apps)
            val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
            val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
            try {
                context.startActivity(fallbackIntent)
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(context, "Aucune application de navigation trouvée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
