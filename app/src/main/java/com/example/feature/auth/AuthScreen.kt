package com.example.feature.auth

import android.app.Activity
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.common.Resource
import com.example.core.designsystem.VidyaAmberSecondary
import com.example.core.designsystem.VidyaBluePrimary
import com.example.core.designsystem.component.VidyaOutlinedButton
import com.example.core.designsystem.component.VidyaPrimaryButton
import com.example.core.di.LocalAppContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: (userId: String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Phone, 1: Email
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Phone State
    var phoneNumber by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var otpCode by remember { mutableStateOf("") }
    var resendCountdown by remember { mutableIntStateOf(0) }

    // Email State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    // Countdown Timer for OTP
    LaunchedEffect(resendCountdown) {
        if (resendCountdown > 0) {
            delay(1000L)
            resendCountdown -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // App Logo & Headline
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(VidyaBluePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.auth_welcome),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.auth_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // Primary Google Sign-In Button (Mandatory & prominent per Firebase integration guidelines)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isLoading) {
                    if (activity != null) {
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            val res = container.authRepository.signInWithGoogleCredential(activity)
                            isLoading = false
                            when (res) {
                                is Resource.Success -> onAuthSuccess(res.data)
                                is Resource.Error -> errorMessage = res.error.userMessage
                                Resource.Loading -> {}
                            }
                        }
                    }
                }
                .testTag("google_sign_in_button")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEA4335)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.auth_sign_in_google),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.surfaceVariant)
            Text(
                text = "  OR  ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.surfaceVariant)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tab Selection: Phone OTP vs Email/Password
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0; errorMessage = null },
                text = { Text(stringResource(R.string.auth_phone_tab)) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1; errorMessage = null },
                text = { Text(stringResource(R.string.auth_email_tab)) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Error message banner
        if (!errorMessage.isNullOrEmpty()) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // TAB 0: PHONE OTP FLOW
        if (selectedTabIndex == 0) {
            if (verificationId == null) {
                // Phone number input
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10) phoneNumber = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(R.string.auth_phone_label)) },
                    placeholder = { Text("9876543210") },
                    prefix = { Text("+91  ", fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_phone_number")
                )

                Spacer(modifier = Modifier.height(18.dp))

                VidyaPrimaryButton(
                    text = stringResource(R.string.auth_send_otp),
                    onClick = {
                        if (phoneNumber.length != 10) {
                            errorMessage = context.getString(R.string.error_invalid_phone)
                            return@VidyaPrimaryButton
                        }
                        if (activity != null) {
                            isLoading = true
                            errorMessage = null
                            scope.launch {
                                container.authRepository.sendPhoneVerificationCode(
                                    phoneNumber = "+91$phoneNumber",
                                    activity = activity,
                                    onCodeSent = { vId ->
                                        isLoading = false
                                        verificationId = vId
                                        resendCountdown = 30
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    tag = "btn_send_otp"
                )
            } else {
                // OTP Code Input & Resend
                Text(
                    text = "${stringResource(R.string.auth_enter_otp)} sent to +91 $phoneNumber",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it.filter { ch -> ch.isDigit() } },
                    label = { Text("6-Digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_otp_code")
                )

                Spacer(modifier = Modifier.height(18.dp))

                VidyaPrimaryButton(
                    text = stringResource(R.string.auth_verify_otp),
                    onClick = {
                        if (otpCode.length != 6) {
                            errorMessage = context.getString(R.string.error_invalid_otp)
                            return@VidyaPrimaryButton
                        }
                        val vId = verificationId ?: return@VidyaPrimaryButton
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            val res = container.authRepository.verifyPhoneOtp(vId, otpCode)
                            isLoading = false
                            when (res) {
                                is Resource.Success -> onAuthSuccess(res.data)
                                is Resource.Error -> errorMessage = res.error.userMessage
                                Resource.Loading -> {}
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    tag = "btn_verify_otp"
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (resendCountdown > 0) {
                    Text(
                        text = "Resend OTP in ${resendCountdown}s",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    TextButton(
                        onClick = {
                            if (activity != null) {
                                resendCountdown = 30
                                scope.launch {
                                    container.authRepository.sendPhoneVerificationCode(
                                        phoneNumber = "+91$phoneNumber",
                                        activity = activity,
                                        onCodeSent = { vId -> verificationId = vId },
                                        onError = { err -> errorMessage = err }
                                    )
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.auth_resend_otp))
                    }
                }
            }
        }

        // TAB 1: EMAIL & PASSWORD FLOW
        if (selectedTabIndex == 1) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.auth_email_label)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_email")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.auth_password_label)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_password")
            )

            Spacer(modifier = Modifier.height(18.dp))

            VidyaPrimaryButton(
                text = stringResource(
                    if (isRegisterMode) R.string.auth_register_button else R.string.auth_login_button
                ),
                onClick = {
                    if (!email.contains("@") || !email.contains(".")) {
                        errorMessage = context.getString(R.string.error_invalid_email)
                        return@VidyaPrimaryButton
                    }
                    if (password.length < 6) {
                        errorMessage = context.getString(R.string.error_weak_password)
                        return@VidyaPrimaryButton
                    }
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        val res = if (isRegisterMode) {
                            container.authRepository.signUpWithEmail(email.trim(), password)
                        } else {
                            container.authRepository.signInWithEmail(email.trim(), password)
                        }
                        isLoading = false
                        when (res) {
                            is Resource.Success -> onAuthSuccess(res.data)
                            is Resource.Error -> errorMessage = res.error.userMessage
                            Resource.Loading -> {}
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                tag = if (isRegisterMode) "btn_register_submit" else "btn_login_submit"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { isRegisterMode = !isRegisterMode; errorMessage = null },
                modifier = Modifier.testTag("toggle_register_mode")
            ) {
                Text(
                    text = if (isRegisterMode) "Already have an account? Login" else "Don't have an account? Register",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.auth_terms_agreement),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
