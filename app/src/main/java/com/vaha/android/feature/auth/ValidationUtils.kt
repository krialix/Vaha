package com.vaha.android.feature.auth

import java.util.regex.Pattern

object ValidationUtils {

    private val TEXT_WITH_EMAIL_ADDRESS_REGEX = (
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\""
                    + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\"
                    + "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])"
                    + "?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]"
                    + "?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:"
                    + "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b"
                    + "\\x0c\\x0e-\\x7f])+)\\])")

    private val USERNAME_REGEX = "^[a-zA-Z][a-zA-Z._0-9]{2,19}$"
    private val TEXT_WITH_FOUR_CONSECUTIVE_NUMBERS_REGEX = ".*[0-9]{5,}.*"

    fun isValidUsername(username: String): ValidationResult<String> {
        if (username.isEmpty()) {
            return ValidationResult.failure(null, username)
        }

        if (username.length < 3) {
            return ValidationResult.failure("name should have 3 or more characters", username)
        }

        val mPattern = Pattern.compile(USERNAME_REGEX)
        val matcher = mPattern.matcher(username)
        val isValid = matcher.find()

        return if (isValid) {
            ValidationResult.success(username)
        } else ValidationResult.failure(
            "username should contain only alphanumeric characters", username
        )
    }

    @JvmStatic
    fun isValidDisplayName(displayName: String): ValidationResult<String> {
        return if (displayName.length < 3) {
            ValidationResult.failure("name should have 3 or more characters", displayName)
        } else ValidationResult.success(displayName)
    }

    @JvmStatic
    fun isValidPassword(password: String): ValidationResult<String> {
        if (password.isEmpty()) {
            return ValidationResult.failure(null, password)
        }

        return when {
            password.length < 6 -> ValidationResult.failure(
                "password should have 6 or more characters",
                password
            )
            else -> ValidationResult.success(password)
        }
    }

    fun containsFourConsecutiveNumbers(text: String): Boolean {
        val mPattern = Pattern.compile(TEXT_WITH_FOUR_CONSECUTIVE_NUMBERS_REGEX)
        val matcher = mPattern.matcher(text)
        return matcher.find()
    }

    @JvmStatic
    fun isValidEmailAddress(text: String): ValidationResult<String> {
        if (text.isEmpty()) {
            return ValidationResult.failure(null, text)
        }

        val mPattern = Pattern.compile(TEXT_WITH_EMAIL_ADDRESS_REGEX)
        val matcher = mPattern.matcher(text)
        val isValid = matcher.find()

        return when {
            isValid -> ValidationResult.success(text)
            else -> ValidationResult.failure("Please enter correct email address", text)
        }
    }
}
