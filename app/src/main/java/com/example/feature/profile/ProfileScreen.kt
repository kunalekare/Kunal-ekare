package com.example.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.VidyaAmberSecondary
import com.example.core.designsystem.VidyaBluePrimary
import com.example.core.designsystem.VidyaEmeraldTertiary
import com.example.core.di.LocalAppContainer
import com.example.core.model.LanguagePreference
import com.example.core.model.StudentLevel
import com.example.core.model.UserRole
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onNavigateToAdmin: () -> Unit = {}
) {
    val container = LocalAppContainer.current
    val coroutineScope = rememberCoroutineScope()

    val selectedLevel by container.preferencesManager.selectedLevelFlow
        .collectAsState(initial = StudentLevel.CLASS_12)

    val currentRole by container.preferencesManager.userRoleFlow
        .collectAsState(initial = UserRole.STUDENT)

    val currentLanguage by container.preferencesManager.languageFlow
        .collectAsState(initial = LanguagePreference.ENGLISH)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "K",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Kunal Ekare",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "kunalekare02@gmail.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = selectedLevel.shortName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = VidyaAmberSecondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Role: ${currentRole.name}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = VidyaAmberSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(
                            value = "5 Days",
                            label = "Current Streak",
                            icon = Icons.Default.LocalFireDepartment,
                            tint = VidyaAmberSecondary
                        )
                        ProfileStatItem(
                            value = "1,250",
                            label = "Earned XP",
                            icon = Icons.Default.Star,
                            tint = VidyaBluePrimary
                        )
                        ProfileStatItem(
                            value = "3 Courses",
                            label = "Enrolled",
                            icon = Icons.Default.School,
                            tint = VidyaEmeraldTertiary
                        )
                    }
                }
            }
        }

        // Academic Goal Settings
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ACADEMIC PREFERENCES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.School,
                            title = "Academic Target Level",
                            subtitle = selectedLevel.title,
                            onClick = {
                                // Cycle level for demonstration
                                val nextLevel = when (selectedLevel) {
                                    StudentLevel.CLASS_10 -> StudentLevel.CLASS_11
                                    StudentLevel.CLASS_11 -> StudentLevel.CLASS_12
                                    StudentLevel.CLASS_12 -> StudentLevel.COMPETITIVE
                                    StudentLevel.COMPETITIVE -> StudentLevel.BACHELORS
                                    StudentLevel.BACHELORS -> StudentLevel.CLASS_10
                                }
                                coroutineScope.launch {
                                    container.preferencesManager.setStudentLevel(nextLevel)
                                }
                            }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        ProfileMenuItem(
                            icon = Icons.Default.Language,
                            title = "Study Language",
                            subtitle = "${currentLanguage.displayName} (${currentLanguage.nativeName})",
                            onClick = {
                                val nextLang = when (currentLanguage) {
                                    LanguagePreference.ENGLISH -> LanguagePreference.HINDI
                                    LanguagePreference.HINDI -> LanguagePreference.MARATHI
                                    LanguagePreference.MARATHI -> LanguagePreference.ENGLISH
                                }
                                coroutineScope.launch {
                                    container.preferencesManager.setLanguage(nextLang)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Role & Teacher Mode Switcher
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "APP ROLE & ACCESS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Teacher / Admin Mode",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Enable course publishing & doubt moderation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = currentRole != UserRole.STUDENT,
                            onCheckedChange = { isChecked ->
                                coroutineScope.launch {
                                    container.preferencesManager.setUserRole(
                                        if (isChecked) UserRole.TEACHER_ADMIN else UserRole.STUDENT
                                    )
                                }
                            },
                            modifier = Modifier.testTag("switch_teacher_mode")
                        )
                    }
                }
            }
        }

        // General Tools & Compliance
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RESOURCES & SETTINGS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.Download,
                            title = "Offline Downloads",
                            subtitle = "4 video lectures, 8 PDF notes cached",
                            onClick = {}
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        ProfileMenuItem(
                            icon = Icons.Default.ReceiptLong,
                            title = "Purchase History & Invoices",
                            subtitle = "View Razorpay transactions & receipts",
                            onClick = {}
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        ProfileMenuItem(
                            icon = Icons.Default.HelpCenter,
                            title = "Help & Support",
                            subtitle = "FAQs and Student WhatsApp Hotline",
                            onClick = {}
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        ProfileMenuItem(
                            icon = Icons.Default.Policy,
                            title = "Privacy Policy & DPDP Terms",
                            subtitle = "Data safety and student account rights",
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(
    value: String,
    label: String,
    icon: ImageVector,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
