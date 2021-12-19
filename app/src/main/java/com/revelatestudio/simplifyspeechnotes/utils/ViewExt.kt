package com.revelatestudio.simplifyspeechnotes.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.revelatestudio.simplifyspeechnotes.R


fun Context.showToast(msg: Any) {
    Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.showAlertDialog(title : String, msg : String, positiveButton : () -> Unit, negativeButton : () -> Unit) : AlertDialog.Builder {
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setMessage(msg)
        .setPositiveButton("Yes") { _, _ ->
            positiveButton.invoke()
        }
        .setNegativeButton("No") { _, _ ->
            negativeButton.invoke()
        }
}