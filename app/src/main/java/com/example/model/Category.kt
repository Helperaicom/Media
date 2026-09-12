package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryItem(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val isExpense: Boolean
)

object Categories {
    val expenseCategories = listOf(
        CategoryItem("Food & Dining", Icons.Default.Fastfood, Color(0xFFF97316), true),
        CategoryItem("Groceries", Icons.Default.LocalGroceryStore, Color(0xFF10B981), true),
        CategoryItem("Shopping", Icons.Default.ShoppingBag, Color(0xFFEC4899), true),
        CategoryItem("Housing", Icons.Default.Home, Color(0xFF3B82F6), true),
        CategoryItem("Transport", Icons.Default.DirectionsCar, Color(0xFF6366F1), true),
        CategoryItem("Bills & Utilities", Icons.Default.Lightbulb, Color(0xFFEAB308), true),
        CategoryItem("Entertainment", Icons.Default.Movie, Color(0xFF8B5CF6), true),
        CategoryItem("Health", Icons.Default.LocalHospital, Color(0xFFEF4444), true),
        CategoryItem("Gifts", Icons.Default.CardGiftcard, Color(0xFFD946EF), true),
        CategoryItem("Other", Icons.Default.MoreHoriz, Color(0xFF64748B), true),
    )

    val incomeCategories = listOf(
        CategoryItem("Salary", Icons.Default.Work, Color(0xFF059669), false),
        CategoryItem("Freelance", Icons.Default.Paid, Color(0xFF0D9488), false),
        CategoryItem("Investments", Icons.Default.Savings, Color(0xFF2563EB), false),
        CategoryItem("Bank Transfer", Icons.Default.AccountBalance, Color(0xFF7C3AED), false),
        CategoryItem("Bonus / Gift", Icons.Default.CardGiftcard, Color(0xFFDB2777), false),
        CategoryItem("Other Income", Icons.Default.MoreHoriz, Color(0xFF475569), false),
    )

    fun getCategory(name: String): CategoryItem {
        return (expenseCategories + incomeCategories).find { it.name.equals(name, ignoreCase = true) }
            ?: CategoryItem(name, Icons.Default.MoreHoriz, Color(0xFF64748B), true)
    }
}
