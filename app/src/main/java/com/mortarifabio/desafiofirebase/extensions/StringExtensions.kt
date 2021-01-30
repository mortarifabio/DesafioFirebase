package com.mortarifabio.desafiofirebase.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.mortarifabio.desafiofirebase.R

fun String.showInSnackBar(view: View) {
    val snackbar = Snackbar.make(view, this, Snackbar.LENGTH_INDEFINITE)
    snackbar.apply {
        setAction(R.string.close){
            dismiss()
        }
        show()
    }
}
