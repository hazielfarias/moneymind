package com.example.moneymind.utils


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatAsDate(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this)
}

fun String.formatAsCurrency(): String {
    return try {
        val value = this.toDouble()
        java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
    } catch (e: Exception) {
        "R$ 0,00"
    }
}