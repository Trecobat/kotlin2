package com.trecobat.pointagetrecopro.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.trecobat.pointagetrecopro.R
import java.util.*


class MultiDatePickerActivity : Activity() {
    private var startDateDisplay: TextView? = null
    private var endDateDisplay: TextView? = null
    private var startPickDate: Button? = null
    private var endPickDate: Button? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var activeDateDisplay: TextView? = null
    private var activeDate: Calendar? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multidatepicker)

        /*  capture our View elements for the start date function   */
        startDateDisplay = findViewById<View>(R.id.startDateDisplay) as TextView
        startPickDate = findViewById<View>(R.id.startPickDate) as Button

        /* get the current date */startDate = Calendar.getInstance()

        /* add a click listener to the button   */
        startPickDate!!.setOnClickListener {
            showDateDialog(
                startDateDisplay,
                startDate
            )
        }

        /* capture our View elements for the end date function */
        endDateDisplay =
            findViewById<View>(R.id.endDateDisplay) as TextView
        endPickDate = findViewById<View>(R.id.endPickDate) as Button

        /* get the current date */
        endDate = Calendar.getInstance()

        /* add a click listener to the button   */
        endPickDate!!.setOnClickListener {
            showDateDialog(
                endDateDisplay,
                endDate
            )
        }

        /* display the current date (this method is below)  */
        updateDisplay(
            startDateDisplay,
            startDate
        )
        updateDisplay(endDateDisplay, endDate)
    }

    private fun updateDisplay(dateDisplay: TextView?, date: Calendar?) {
        dateDisplay!!.text = StringBuilder() // Month is 0 based so add 1
            .append(date!![Calendar.MONTH] + 1).append("-")
            .append(date[Calendar.DAY_OF_MONTH]).append("-")
            .append(date[Calendar.YEAR]).append(" ")
    }

    private fun showDateDialog(dateDisplay: TextView?, date: Calendar?) {
        activeDateDisplay = dateDisplay
        activeDate = date
        val datePickerDialog = DatePickerDialogFragment.newInstance(dateSetListener, activeDate!!)
        datePickerDialog.show(activity.supportFragmentManager, "datePicker")
    }

    private val dateSetListener =
        OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            activeDate!![Calendar.YEAR] = year
            activeDate!![Calendar.MONTH] = monthOfYear
            activeDate!![Calendar.DAY_OF_MONTH] = dayOfMonth
            updateDisplay(activeDateDisplay, activeDate)
            unregisterDateDisplay()
        }

    private fun unregisterDateDisplay() {
        activeDateDisplay = null
        activeDate = null
    }

    override fun onCreateDialog(id: Int): Dialog {
        when (id) {
            DATE_DIALOG_ID -> return DatePickerDialog(
                this, dateSetListener,
                activeDate!![Calendar.YEAR],
                activeDate!![Calendar.MONTH],
                activeDate!![Calendar.DAY_OF_MONTH]
            )
        }
        return null
    }

    override fun onPrepareDialog(id: Int, dialog: Dialog) {
        super.onPrepareDialog(id, dialog)
        when (id) {
            DATE_DIALOG_ID -> (dialog as DatePickerDialog).updateDate(
                activeDate!![Calendar.YEAR],
                activeDate!![Calendar.MONTH], activeDate!![Calendar.DAY_OF_MONTH]
            )
        }
    }

    companion object {
        const val DATE_DIALOG_ID = 0
    }
}