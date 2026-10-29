package com.example.core.data.repository

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.core.common.AppError
import com.example.core.common.Resource
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

interface AuthRepository {
    val currentUserId: String?
    val currentUser: FirebaseUser?
    val authStateFlow: Flow<FirebaseUser?>

    suspend fun signInWithGoogleCredential(activity: Activity): Resource<String>
    suspend fun sendPhoneVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (String) -> Unit
    )
    suspend fun verifyPhoneOtp(verificationId: String, code: String): Resource<String>
    suspend fun signInWithEmail(email: String, pass: String): Resource<String>
    suspend fun signUpWithEmail(email: String, pass: String): Resource<String>
    suspend fun signOut(context: Context)
}

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) : AuthRepository {

    override val currentUserId: String? get() = auth.currentUser?.uid
    override val currentUser: FirebaseUser? get() = auth.currentUser

    override val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fbAuth ->
            trySend(fbAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGoogleCredential(activity: Activity): Resource<String> {
        return try {
            val context = activity.applicationContext
            val clientId = try {
                context.getString(R.string.default_web_client_id)
            } catch (e: Exception) {
                return Resource.Error(AppError.Authentication("Web Client ID configuration missing"))
            }

            val credentialManager = CredentialManager.create(activity)
            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
            val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val uid = authResult.user?.uid ?: return Resource.Error(AppError.Authentication("User ID null"))
                Resource.Success(uid)
            } else {
                Resource.Error(AppError.Authentication("Unexpected credential type received"))
            }
        } catch (e: GetCredentialCancellationException) {
            Resource.Error(AppError.Authentication("Google Sign-In was cancelled."))
        } catch (e: Exception) {
            Resource.Error(AppError.fromThrowable(e), e)
        }
    }

    override suspend fun sendPhoneVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-retrieval or instant verification
                auth.signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                onError(e.localizedMessage ?: "Phone verification failed")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun verifyPhoneOtp(verificationId: String, code: String): Resource<String> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user?.uid ?: return Resource.Error(AppError.Authentication("User ID null"))
            Resource.Success(uid)
        } catch (e: Exception) {
            Resource.Error(AppError.fromThrowable(e), e)
        }
    }

    override suspend fun signInWithEmail(email: String, pass: String): Resource<String> {
        // Direct users to provisioned Google Sign-In as mandated by Firebase Provisioning backend
        return Resource.Error(AppError.Authentication("Email/password provider is unprovisioned on this backend. Please use 'Sign in with Google'."))
    }

    override suspend fun signUpWithEmail(email: String, pass: String): Resource<String> {
        return Resource.Error(AppError.Authentication("Email/password provider is unprovisioned on this backend. Please use 'Sign in with Google'."))
    }

    override suspend fun signOut(context: Context) {
        try {
            auth.signOut()
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {}
    }
}
