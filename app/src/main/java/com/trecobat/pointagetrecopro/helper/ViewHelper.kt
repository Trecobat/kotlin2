package com.trecobat.pointagetrecopro.helper

import android.icu.util.Calendar
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import com.trecobat.pointagetrecopro.R

object  ViewHelper {
    fun setupDatePicker(rootView: View) {
        val buttonJour = rootView.findViewById<Button>(R.id.buttonJour)
        val jour = rootView.findViewById<DatePicker>(R.id.jour)
        val pointageBtn = rootView.findViewById<Button>(R.id.pointage_btn)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // TACHE DETAIL FRAGMENT BINDING
        buttonJour.setOnClickListener {
            jour.visibility = View.VISIBLE
            pointageBtn.visibility = View.GONE
        }

        jour.init(year, month, day, null)
        jour.setOnDateChangedListener { _: DatePicker, yearDate: Int, monthOfYear: Int, dayOfMonth: Int ->
            var text = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            text += "/"
            text += if (monthOfYear < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
            text += "/${yearDate.toString().substring(2)}"
            buttonJour.text = text
            jour.visibility = View.GONE
            pointageBtn.visibility = View.VISIBLE
        }
        jour.bringToFront()
    }
}