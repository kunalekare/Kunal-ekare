package com.example.core.common

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val error: AppError, val cause: Throwable? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data
}

sealed class AppError(open val userMessage: String) {
    data class Network(override val userMessage: String = "No internet connection. Please check your network.") : AppError(userMessage)
    data class Authentication(override val userMessage: String = "Authentication failed. Please verify credentials.") : AppError(userMessage)
    data class InvalidInput(override val userMessage: String = "Invalid input details provided.") : AppError(userMessage)
    data class PermissionDenied(override val userMessage: String = "You do not have permission to access this resource.") : AppError(userMessage)
    data class NotFound(override val userMessage: String = "Requested content was not found.") : AppError(userMessage)
    data class Server(override val userMessage: String = "Server error occurred. Please try again.") : AppError(userMessage)
    data class Timeout(override val userMessage: String = "Connection timed out. Please retry.") : AppError(userMessage)
    data class Unknown(override val userMessage: String = "An unexpected error occurred. Please try again.") : AppError(userMessage)

    companion object {
        fun fromThrowable(throwable: Throwable?): AppError {
            if (throwable == null) return Unknown()
            val msg = throwable.message.orEmpty()
            return when {
                msg.contains("network", ignoreCase = true) || msg.contains("connection", ignoreCase = true) ->
                    Network()
                msg.contains("PERMISSION_DENIED", ignoreCase = true) ->
                    PermissionDenied()
                msg.contains("timeout", ignoreCase = true) ->
                    Timeout()
                msg.contains("auth", ignoreCase = true) || msg.contains("credential", ignoreCase = true) ->
                    Authentication(msg)
                else ->
                    Unknown(if (msg.isNotBlank()) msg else "An unexpected error occurred.")
            }
        }
    }
}
