package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomizationSettings
import com.example.data.UserAccountEntity
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.FreeGreen
import com.example.ui.theme.VipGold
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CustomizationHubDialog(
    customization: CustomizationSettings,
    userAccount: UserAccountEntity,
    onDismiss: () -> Unit,
    onSetMonthlyPrice: (Double) -> Unit,
    onAdjustMonthlyPrice: (Double) -> Unit,
    onSetAnnualPrice: (Double) -> Unit,
    onAdjustAnnualPrice: (Double) -> Unit,
    onToggleVipFreePromo: (Boolean) -> Unit,
    onAdjustVipDurationDays: (Int) -> Unit,
    onGrantFreeVipPass: (Int) -> Unit,
    onMakeAllVideosFree: (Boolean) -> Unit,
    onUpdatePlaybackSpeed: (Float) -> Unit,
    onUpdateDefaultQuality: (String) -> Unit,
    onToggleAutoPlayNext: (Boolean) -> Unit,
    onToggleUltraFastMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("customization_hub_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrandCrimson.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = BrandCrimson,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Customization Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "VIP Pass Pricing, Free Mode & Playback Settings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_customization_sheet")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 1: VIP PASS & MONETIZATION CUSTOMIZER
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (customization.isVipFreePromo) Color(0xFF0F2617) else Color(0xFF221A08)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (customization.isVipFreePromo) FreeGreen else VipGold
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (customization.isVipFreePromo) FreeGreen else VipGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VIP Pass Pricing & Free Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (customization.isVipFreePromo) FreeGreen else VipGold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (customization.isVipFreePromo) FreeGreen else VipGold,
                            contentColor = Color.Black
                        ) {
                            Text(
                                text = if (customization.isVipFreePromo) "FREE PROMO ACTIVE" else "PAID TIER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. MAKE VIP PASS 100% FREE SWITCH
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Make VIP Pass 100% FREE (मुफ़्त)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Unlocks all exclusive videos for all viewers at $0.00",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        Switch(
                            checked = customization.isVipFreePromo,
                            onCheckedChange = { onToggleVipFreePromo(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = FreeGreen
                            ),
                            modifier = Modifier.testTag("vip_free_promo_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. MONTHLY VIP PASS PRICE STEPPER
                    PriceStepperCard(
                        title = "Monthly VIP Pass Price",
                        currentPrice = if (customization.isVipFreePromo) 0.0 else customization.monthlyPrice,
                        isFree = customization.isVipFreePromo || customization.monthlyPrice == 0.0,
                        onDecrease = { onAdjustMonthlyPrice(-1.0) },
                        onIncrease = { onAdjustMonthlyPrice(1.0) },
                        stepLabel = "$1.00",
                        presets = listOf(0.0, 4.99, 9.99, 14.99, 19.99),
                        onSelectPreset = { onSetMonthlyPrice(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. ANNUAL VIP PASS PRICE STEPPER
                    PriceStepperCard(
                        title = "Annual VIP Pass Price",
                        currentPrice = if (customization.isVipFreePromo) 0.0 else customization.annualPrice,
                        isFree = customization.isVipFreePromo || customization.annualPrice == 0.0,
                        onDecrease = { onAdjustAnnualPrice(-5.0) },
                        onIncrease = { onAdjustAnnualPrice(5.0) },
                        stepLabel = "$5.00",
                        presets = listOf(0.0, 39.99, 79.99, 99.99),
                        onSelectPreset = { onSetAnnualPrice(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. VIP DURATION / VALIDITY DAYS STEPPER
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = VipGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("VIP Pass Duration:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                text = "${customization.customVipDays} Days Access",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = VipGold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { onAdjustVipDurationDays(-7) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("decrease_vip_days_button")
                            ) {
                                Text("-7d", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { onAdjustVipDurationDays(7) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("increase_vip_days_button")
                            ) {
                                Text("+7d", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { onAdjustVipDurationDays(30) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("add_month_vip_days_button")
                            ) {
                                Text("+30d", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. ONE-TAP CLAIM FREE VIP PASS
                    Button(
                        onClick = { onGrantFreeVipPass(365) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (userAccount.isSubscribed) MaterialTheme.colorScheme.surfaceVariant else FreeGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("grant_free_vip_pass_button")
                    ) {
                        Icon(Icons.Default.CardMembership, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userAccount.isSubscribed) "Renew / Grant 1-Year Free VIP Pass" else "🎁 Claim 100% Free 1-Year VIP Pass",
                            fontWeight = FontWeight.Bold,
                            color = if (userAccount.isSubscribed) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 2: VIDEO CATALOG & ACCESS RULES
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = BrandCrimson, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Content Access Customization",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Make All Catalog Videos Free", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            Text("Convert all VIP masterclasses to unrestricted free playback", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = customization.areAllVideosFree,
                            onCheckedChange = { onMakeAllVideosFree(it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = BrandCrimson),
                            modifier = Modifier.testTag("all_videos_free_switch")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 3: SMOOTH UI & FAST PLAYBACK CUSTOMIZER
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fast Performance & Playback Settings",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ultra-Fast Mode Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ultra-Fast Streaming Mode", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            Text("Pre-loads buffer instantly and minimizes UI lag", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = customization.ultraFastMode,
                            onCheckedChange = { onToggleUltraFastMode(it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("ultra_fast_mode_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Auto-Play Next Video Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-Play Next Video", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            Text("Seamless continuous stream without interruption", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = customization.autoPlayNext,
                            onCheckedChange = { onToggleAutoPlayNext(it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("autoplay_next_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preferred Quality Selector
                    Text("Default Streaming Quality:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    val qualities = listOf("480p Saver", "720p HD", "1080p FHD", "4K Ultra")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        qualities.forEach { q ->
                            val isSelected = customization.defaultQuality == q
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdateDefaultQuality(q) },
                                label = { Text(q, fontSize = 12.sp) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandCrimson,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Default Playback Speed Selector
                    Text("Default Playback Speed:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        speeds.forEach { sp ->
                            val isSelected = customization.defaultPlaybackSpeed == sp
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdatePlaybackSpeed(sp) },
                                label = { Text("${sp}x", fontSize = 12.sp) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PriceStepperCard(
    title: String,
    currentPrice: Double,
    isFree: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    stepLabel: String,
    presets: List<Double>,
    onSelectPreset: (Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.35f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(
                    text = if (isFree) "FREE ($0.00)" else String.format(Locale.US, "$%.2f", currentPrice),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isFree) FreeGreen else VipGold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("decrease_${title.replace(" ", "_").lowercase()}")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isFree) FreeGreen else VipGold)
                        .testTag("increase_${title.replace(" ", "_").lowercase()}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            presets.forEach { preset ->
                val isSelected = Math.abs(currentPrice - preset) < 0.01 && (!isFree || preset == 0.0)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) (if (preset == 0.0) FreeGreen else VipGold) else Color.White.copy(alpha = 0.1f),
                    contentColor = if (isSelected) Color.Black else Color.White,
                    modifier = Modifier.clickable { onSelectPreset(preset) }
                ) {
                    Text(
                        text = if (preset == 0.0) "Free ($0)" else String.format(Locale.US, "$%.2f", preset),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
