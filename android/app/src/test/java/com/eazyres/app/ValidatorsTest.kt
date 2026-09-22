package com.eazyres.app

import com.eazyres.app.utils.Validators
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the input-validation logic used across the
 * Register / Login / Settings screens (R1, R5 in the design document).
 */
class ValidatorsTest {

    @Test
    fun `valid email is accepted`() {
        assertTrue(Validators.isValidEmail("student@university.ac.za"))
    }

    @Test
    fun `email without at symbol is rejected`() {
        assertFalse(Validators.isValidEmail("student.university.ac.za"))
    }

    @Test
    fun `blank email is rejected`() {
        assertFalse(Validators.isValidEmail(""))
    }

    @Test
    fun `password shorter than 8 characters is rejected`() {
        assertFalse(Validators.isValidPassword("abc123"))
    }

    @Test
    fun `password of 8 or more characters is accepted`() {
        assertTrue(Validators.isValidPassword("securePassword123"))
    }

    @Test
    fun `blank phone number is rejected`() {
        assertFalse(Validators.isValidPhone(""))
    }

    @Test
    fun `valid south african phone number is accepted`() {
        assertTrue(Validators.isValidPhone("+27721234567"))
    }
}
