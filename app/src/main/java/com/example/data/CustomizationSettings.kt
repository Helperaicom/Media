package com.example.data

data class CustomizationSettings(
    // VIP Pass Pricing & Access Customization
    val monthlyPrice: Double = 9.99,
    val annualPrice: Double = 79.99,
    val lifetimePrice: Double = 199.00,
    val isVipFreePromo: Boolean = false, // When true, VIP pass is 100% Free ($0.00) & unlocked for everyone
    val customVipDays: Int = 30, // Default pass duration in days
    val areAllVideosFree: Boolean = false,

    // Playback & Performance Customization
    val defaultQuality: String = "1080p FHD", // "480p Saver", "720p HD", "1080p FHD", "4K Ultra"
    val defaultPlaybackSpeed: Float = 1.0f,
    val autoPlayNext: Boolean = true,
    val ultraFastMode: Boolean = true // Instant buffer, zero-lag UI transitions
)
