package com.example.moneymind.feature.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val description: String,
    val value: Double,
    val date: Date,
    val createdAt: Date = Date(),
    val category: String = "Outros"
) {
    companion object {
        val categories = listOf(
            "Alimentação",
            "Transporte",
            "Moradia",
            "Lazer",
            "Saúde",
            "Educação",
            "Outros"
        )
    }
}