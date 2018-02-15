package com.vaha.android.feature.auth

class ValidationResult<out T> private constructor(
    val isValid: Boolean,
    val reason: String?,
    val data: T
) {
    companion object {
        fun <T> success(t: T): ValidationResult<T> {
            return ValidationResult(true, null, t)
        }

        fun <T> failure(reason: String?, data: T): ValidationResult<T> {
            return ValidationResult(false, reason, data)
        }
    }
}
