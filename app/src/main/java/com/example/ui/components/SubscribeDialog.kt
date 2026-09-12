package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomizationSettings
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.FreeGreen
import com.example.ui.theme.VipGold
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribeDialog(
    currentTier: String,
    isSubscribed: Boolean,
    onDismiss: () -> Unit,
    onSubscribe: (tier: String, days: Int) -> Unit,
    onCancelSubscription: () -> Unit,
    customization: CustomizationSettings = CustomizationSettings(),
    onOpenCustomization: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPlan by remember { mutableStateOf("ANNUAL") }

    val isFreePromo = customization.isVipFreePromo
    val annualPriceStr = if (isFreePromo || customization.annualPrice == 0.0) "FREE ($0.00)" else String.format(Locale.US, "$%.2f / year", customization.annualPrice)
    val monthlyPriceStr = if (isFreePromo || customization.monthlyPrice == 0.0) "FREE ($0.00)" else String.format(Locale.US, "$%.2f / month", customization.monthlyPrice)
    val lifetimePriceStr = if (isFreePromo) "FREE ($0.00)" else String.format(Locale.US, "$%.2f once", customization.lifetimePrice)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("subscribe_bottom_sheet")
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(VipGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = VipGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "CreatorStream VIP",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Promotion Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4C0519),
                                Color(0xFF831843),
                                Color(0xFF1E1B4B)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = VipGold,
                        contentColor = Color.Black
                    ) {
                        Text(
                            text = "UNLIMITED ACCESS PASS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Unlock all exclusive masterclasses & offline downloads",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Support the creator directly and stream high-bitrate video with zero ads.",
                        color = Color(0xFFE2E8F0),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Subscriber Benefits list
            Text(
                text = "What You Get:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            PerkItem(icon = Icons.Default.LockOpen, text = "Full Access to all VIP & Masterclass videos")
            PerkItem(icon = Icons.Default.FileDownload, text = "Download any video for offline viewing")
            PerkItem(icon = Icons.Default.HighQuality, text = "Ad-free 1080p & 4K Ultra HD playback")
            PerkItem(icon = Icons.Default.People, text = "Direct creator Q&A & Community discussions")

            Spacer(modifier = Modifier.height(18.dp))

            // Subscription Tier Selector
            Text(
                text = "Choose Your Membership Plan:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Plan 1: Annual VIP (Best Value)
            PlanCard(
                title = "Annual VIP Pass",
                price = annualPriceStr,
                badge = if (isFreePromo || customization.annualPrice == 0.0) "100% FREE PROMO" else "SAVE 35% • MOST POPULAR",
                isBadgeHighlighted = true,
                isSelected = selectedPlan == "ANNUAL",
                onClick = { selectedPlan = "ANNUAL" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Plan 2: Monthly Fan Pass
            PlanCard(
                title = "Monthly Fan Pass",
                price = monthlyPriceStr,
                badge = if (isFreePromo || customization.monthlyPrice == 0.0) "100% FREE PROMO" else "Flexible • Cancel Anytime",
                isBadgeHighlighted = false,
                isSelected = selectedPlan == "MONTHLY",
                onClick = { selectedPlan = "MONTHLY" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Plan 3: Lifetime Supporter
            PlanCard(
                title = "Lifetime Supporter",
                price = lifetimePriceStr,
                badge = if (isFreePromo) "FREE LIFETIME" else "Forever Access",
                isBadgeHighlighted = false,
                isSelected = selectedPlan == "LIFETIME",
                onClick = { selectedPlan = "LIFETIME" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Customize VIP Pricing Shortcut Button
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onOpenCustomization()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customize_vip_pricing_button")
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = VipGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⚙️ Customize VIP Prices & Free Mode",
                    color = VipGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            val isCurrentPlanFree = isFreePromo ||
                (selectedPlan == "ANNUAL" && customization.annualPrice == 0.0) ||
                (selectedPlan == "MONTHLY" && customization.monthlyPrice == 0.0) ||
                (selectedPlan == "LIFETIME" && customization.lifetimePrice == 0.0)

            Button(
                onClick = {
                    val days = when (selectedPlan) {
                        "ANNUAL" -> 365
                        "LIFETIME" -> -1
                        else -> customization.customVipDays
                    }
                    onSubscribe(selectedPlan, days)
                    onDismiss()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentPlanFree) FreeGreen else BrandCrimson
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_subscribe_button")
            ) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCurrentPlanFree) {
                        "Claim Free VIP Pass ($0.00)"
                    } else when (selectedPlan) {
                        "ANNUAL" -> "Subscribe Annual VIP ($annualPriceStr)"
                        "LIFETIME" -> "Get Lifetime VIP ($lifetimePriceStr)"
                        else -> "Subscribe Monthly ($monthlyPriceStr)"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            if (isSubscribed) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Status: Active ($currentTier Plan)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        onCancelSubscription()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Subscription", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PerkItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(BrandCrimson.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandCrimson,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    badge: String,
    isBadgeHighlighted: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            )
            .background(
                if (isSelected) BrandCrimson.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isBadgeHighlighted) VipGold else MaterialTheme.colorScheme.surface,
                        contentColor = if (isBadgeHighlighted) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = price,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) BrandCrimson else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (isSelected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
