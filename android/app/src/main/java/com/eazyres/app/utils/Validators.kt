package com.eazyres.app.utils

import android.util.Patterns

object Validators {

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= 8

    fun isValidPhone(phone: String): Boolean =
        phone.isNotBlank() && phone.length >= 9

    fun isNotBlank(value: String): Boolean = value.isNotBlank()
}
