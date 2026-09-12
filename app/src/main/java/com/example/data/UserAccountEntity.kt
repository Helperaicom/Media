package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey
    val id: Long = 1,
    val name: String = "Alex Rivera",
    val email: String = "alex.viewer@example.com",
    val isCreator: Boolean = false,
    val subscriptionTier: String = "FREE", // "FREE", "MONTHLY", "ANNUAL", "LIFETIME"
    val subscriptionExpiryMillis: Long = 0L,
    val isSubscribed: Boolean = false,
    val totalWatchMinutes: Int = 184
)
