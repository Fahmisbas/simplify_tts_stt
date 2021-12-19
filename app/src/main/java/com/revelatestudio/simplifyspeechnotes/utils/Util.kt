package com.revelatestudio.simplifyspeechnotes.utils

import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("EEEE MM yyyy", Locale.UK)
    return sdf.format(Date())
}

fun shareTextContent(context: Context, title: String, content: String) {
    if (title.isNotEmpty() || content.isNotEmpty()) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        with(shareIntent) {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            context.startActivity(Intent.createChooser(shareIntent, "Share to"))
        }
    } else return
}

