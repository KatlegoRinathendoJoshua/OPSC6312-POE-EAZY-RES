package com.eazyres.app.utils

object Validators {

    private val emailRegex =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && emailRegex.matches(email)

    fun isValidPassword(password: String): Boolean =
        password.length >= 8

    fun isValidPhone(phone: String): Boolean =
        phone.isNotBlank() && phone.length >= 9

    fun isNotBlank(value: String): Boolean =
        value.isNotBlank()
}