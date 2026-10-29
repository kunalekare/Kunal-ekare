package com.example.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.common.Resource
import com.example.core.designsystem.VidyaAmberSecondary
import com.example.core.designsystem.VidyaBluePrimary
import com.example.core.designsystem.VidyaEmeraldTertiary
import com.example.core.designsystem.component.VidyaFilterChip
import com.example.core.designsystem.component.VidyaOutlinedButton
import com.example.core.designsystem.component.VidyaPrimaryButton
import com.example.core.di.LocalAppContainer
import com.example.core.model.LanguagePreference
import com.example.core.model.StudentLevel
import com.example.core.model.SubjectStream
import com.example.core.model.TargetExam
import com.example.core.model.UserProfile
import com.example.core.model.UserRole
import kotlinx.coroutines.launch

@Composable
fun ProfileSetupScreen(
    userId: String,
    onProfileSetupComplete: () -> Unit
) {
    val context = LocalContext.current
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableIntStateOf(1) } // Steps 1 to 5
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Step 1: Personal Details
    var fullName by remember { mutableStateOf("Kunal Ekare") }
    var city by remember { mutableStateOf("Mumbai") }

    // Step 2: Academic Level
    var selectedLevel by remember { mutableStateOf(StudentLevel.CLASS_12) }

    // Step 3: Stream / Exam
    var selectedStream by remember { mutableStateOf(SubjectStream.SCIENCE_PCM) }
    var selectedExam by remember { mutableStateOf(TargetExam.JEE_MAIN) }

    // Step 4: Language
    var selectedLanguage by remember { mutableStateOf(LanguagePreference.ENGLISH) }

    // Step 5: Compliance & Goal
    var dateOfBirth by remember { mutableStateOf("15/08/2005") } // DD/MM/YYYY
    var isMinor by remember { mutableStateOf(false) }
    var parentContact by remember { mutableStateOf("") }
    var parentConsentGiven by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(true) }
    var dailyGoalMinutes by remember { mutableIntStateOf(60) }
    var reminderTime by remember { mutableStateOf("19:00") }

    // Check age logic
    fun checkMinor(dob: String): Boolean {
        return try {
            val parts = dob.split("/")
            if (parts.size == 3) {
                val year = parts[2].trim().toIntOrNull() ?: 2000
                (2026 - year) < 18
            } else false
        } catch (_: Exception) {
            false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("profile_setup_screen")
    ) {
        // Top Navigation Header with Step Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                IconButton(onClick = { currentStep -= 1; errorMsg = null }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Text(
                text = "Step $currentStep of 5",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { currentStep / 5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!errorMsg.isNullOrEmpty()) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMsg ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // ==========================================
        // STEP 1: PERSONAL DETAILS
        // ==========================================
        if (currentStep == 1) {
            Text(
                text = stringResource(R.string.profile_setup_step_1),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Let us know your name and city to personalize local batches and study groups",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(VidyaBluePrimary.copy(alpha = 0.15f))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = VidyaBluePrimary,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(stringResource(R.string.profile_full_name)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_full_name")
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(stringResource(R.string.profile_city)) },
                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_city")
            )
        }

        // ==========================================
        // STEP 2: ACADEMIC LEVEL
        // ==========================================
        if (currentStep == 2) {
            Text(
                text = stringResource(R.string.profile_setup_step_2),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Choose your current study level. VidyaSetu personalizes your courses and syllabus accordingly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            StudentLevel.values().forEach { level ->
                val isSelected = selectedLevel == level
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedLevel = level }
                        .testTag("level_option_${level.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedLevel = level }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = level.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = level.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // STEP 3: STREAM / TARGET EXAM / TRACK
        // ==========================================
        if (currentStep == 3) {
            Text(
                text = stringResource(R.string.profile_setup_step_3),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Specify your focus area to tailor question banks, previous year papers, and mentor sessions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            when (selectedLevel) {
                StudentLevel.CLASS_11, StudentLevel.CLASS_12 -> {
                    Text(
                        text = "Choose Subject Stream:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        SubjectStream.SCIENCE_PCM,
                        SubjectStream.SCIENCE_PCB,
                        SubjectStream.COMMERCE,
                        SubjectStream.ARTS
                    ).forEach { stream ->
                        SelectableRowItem(
                            title = stream.displayName,
                            isSelected = selectedStream == stream,
                            onSelect = { selectedStream = stream },
                            tag = "stream_${stream.name}"
                        )
                    }
                }
                StudentLevel.COMPETITIVE -> {
                    Text(
                        text = "Choose Primary Target Exam:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        TargetExam.JEE_MAIN,
                        TargetExam.NEET_UG,
                        TargetExam.CUET,
                        TargetExam.MHT_CET,
                        TargetExam.NDA,
                        TargetExam.CA_FOUNDATION
                    ).forEach { exam ->
                        SelectableRowItem(
                            title = exam.displayName,
                            isSelected = selectedExam == exam,
                            onSelect = { selectedExam = exam },
                            tag = "exam_${exam.code}"
                        )
                    }
                }
                StudentLevel.BACHELORS -> {
                    Text(
                        text = "Select Career & Skill Track:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        SubjectStream.ENGINEERING_CS,
                        SubjectStream.DATA_AI,
                        SubjectStream.GENERAL
                    ).forEach { stream ->
                        SelectableRowItem(
                            title = stream.displayName,
                            isSelected = selectedStream == stream,
                            onSelect = { selectedStream = stream },
                            tag = "bachelors_track_${stream.name}"
                        )
                    }
                }
                StudentLevel.CLASS_10 -> {
                    Text(
                        text = "Class 10 Board Target:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(TargetExam.CBSE_10, TargetExam.STATE_BOARD).forEach { exam ->
                        SelectableRowItem(
                            title = exam.displayName,
                            isSelected = selectedExam == exam,
                            onSelect = { selectedExam = exam },
                            tag = "exam_${exam.code}"
                        )
                    }
                }
            }
        }

        // ==========================================
        // STEP 4: LANGUAGE PREFERENCE
        // ==========================================
        if (currentStep == 4) {
            Text(
                text = stringResource(R.string.profile_setup_step_4),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Select your preferred language for video subtitles, PDF study notes, and tests",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            LanguagePreference.values().forEach { lang ->
                val isSelected = selectedLanguage == lang
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedLanguage = lang }
                        .testTag("lang_option_${lang.code}")
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = lang.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = lang.nativeName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // STEP 5: AGE, CONSENT & STUDY GOAL
        // ==========================================
        if (currentStep == 5) {
            Text(
                text = stringResource(R.string.profile_setup_step_5),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Compliance with India DPDP Act & setting your daily learning targets",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = {
                    dateOfBirth = it
                    isMinor = checkMinor(it)
                },
                label = { Text(stringResource(R.string.profile_dob)) },
                placeholder = { Text("DD/MM/YYYY e.g. 15/08/2006") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_dob")
            )

            // Parental Consent Box if under 18
            if (isMinor) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VidyaAmberSecondary.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = stringResource(R.string.profile_parent_consent_title),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = VidyaAmberSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = parentContact,
                            onValueChange = { parentContact = it },
                            label = { Text(stringResource(R.string.profile_parent_contact)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_parent_contact")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = parentConsentGiven,
                                onCheckedChange = { parentConsentGiven = it },
                                modifier = Modifier.testTag("checkbox_parent_consent")
                            )
                            Text(
                                text = "Parent / Guardian acknowledges and consents to student learning on VidyaSetu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Daily Study Goal
            Text(
                text = "Daily Study Target Goal",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(30, 60, 120).forEach { mins ->
                    VidyaFilterChip(
                        selected = dailyGoalMinutes == mins,
                        onClick = { dailyGoalMinutes = mins },
                        label = if (mins >= 60) "${mins / 60} Hour" else "$mins Mins",
                        tag = "goal_${mins}_mins"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Terms Agreement Checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    modifier = Modifier.testTag("checkbox_terms")
                )
                Text(
                    text = "I agree to VidyaSetu Terms of Service, Honor Code, and Privacy Policy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Next / Finish Action Button
        val isLastStep = currentStep == 5
        VidyaPrimaryButton(
            text = stringResource(
                if (isLastStep) R.string.profile_finish else R.string.profile_save_continue
            ),
            onClick = {
                when (currentStep) {
                    1 -> {
                        if (fullName.isBlank()) {
                            errorMsg = "Please enter your full name."
                            return@VidyaPrimaryButton
                        }
                        currentStep = 2
                        errorMsg = null
                    }
                    2 -> {
                        currentStep = 3
                        errorMsg = null
                    }
                    3 -> {
                        currentStep = 4
                        errorMsg = null
                    }
                    4 -> {
                        currentStep = 5
                        errorMsg = null
                    }
                    5 -> {
                        if (!termsAccepted) {
                            errorMsg = context.getString(R.string.error_terms_required)
                            return@VidyaPrimaryButton
                        }
                        if (isMinor && (!parentConsentGiven || parentContact.isBlank())) {
                            errorMsg = context.getString(R.string.error_parent_consent_required)
                            return@VidyaPrimaryButton
                        }

                        // Save Profile to Firestore and DataStore
                        isLoading = true
                        errorMsg = null
                        scope.launch {
                            val profile = UserProfile(
                                uid = userId,
                                userId = userId,
                                fullName = fullName.trim(),
                                city = city.trim(),
                                role = UserRole.STUDENT,
                                level = selectedLevel,
                                stream = selectedStream,
                                targetExam = selectedExam,
                                language = selectedLanguage,
                                dateOfBirth = dateOfBirth.trim(),
                                isMinor = isMinor,
                                parentConsentGiven = parentConsentGiven,
                                parentEmailOrPhone = parentContact.trim(),
                                termsAccepted = termsAccepted,
                                dailyGoalMinutes = dailyGoalMinutes,
                                reminderTime = reminderTime,
                                isProfileComplete = true
                            )

                            val res = container.userRepository.saveUserProfile(profile)
                            isLoading = false
                            when (res) {
                                is Resource.Success -> {
                                    container.preferencesManager.setProfileCompleted(true)
                                    onProfileSetupComplete()
                                }
                                is Resource.Error -> {
                                    errorMsg = res.error.userMessage
                                }
                                Resource.Loading -> {}
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            tag = if (isLastStep) "btn_profile_complete" else "btn_profile_next"
        )
    }
}

@Composable
fun SelectableRowItem(
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onSelect)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
