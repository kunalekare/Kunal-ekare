package com.example

import com.example.core.model.LanguagePreference
import com.example.core.model.StudentLevel
import com.example.core.model.SubjectStream
import com.example.core.model.TargetExam
import com.example.core.model.UserProfile
import com.example.core.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthAndProfileUnitTest {

    @Test
    fun `test phone validator accepts only valid 10-digit Indian numbers`() {
        fun isValidIndianPhone(phone: String): Boolean =
            phone.length == 10 && phone.all { it.isDigit() } && phone.first() in '6'..'9'

        assertTrue(isValidIndianPhone("9876543210"))
        assertTrue(isValidIndianPhone("7012345678"))
        assertFalse(isValidIndianPhone("12345"))
        assertFalse(isValidIndianPhone("98765432101"))
        assertFalse(isValidIndianPhone("2876543210"))
    }

    @Test
    fun `test OTP validator accepts 6 digits`() {
        fun isValidOtp(otp: String): Boolean =
            otp.length == 6 && otp.all { it.isDigit() }

        assertTrue(isValidOtp("123456"))
        assertFalse(isValidOtp("12345"))
        assertFalse(isValidOtp("12345a"))
    }

    @Test
    fun `test DOB calculation identifies minors under 18`() {
        fun isMinorFromDob(dob: String, currentYear: Int = 2026): Boolean {
            val parts = dob.split("/")
            if (parts.size != 3) return false
            val birthYear = parts[2].trim().toIntOrNull() ?: return false
            return (currentYear - birthYear) < 18
        }

        assertTrue("Born in 2010 is minor in 2026 (age 16)", isMinorFromDob("15/08/2010"))
        assertTrue("Born in 2009 is minor in 2026 (age 17)", isMinorFromDob("01/01/2009"))
        assertFalse("Born in 2005 is adult in 2026 (age 21)", isMinorFromDob("15/08/2005"))
        assertFalse("Born in 2000 is adult in 2026", isMinorFromDob("10/12/2000"))
    }

    @Test
    fun `test UserProfile construction and defaults`() {
        val profile = UserProfile(
            uid = "user_123",
            userId = "user_123",
            fullName = "Aarav Patil",
            city = "Pune",
            role = UserRole.STUDENT,
            level = StudentLevel.CLASS_12,
            stream = SubjectStream.SCIENCE_PCM,
            targetExam = TargetExam.JEE_MAIN,
            language = LanguagePreference.MARATHI,
            dateOfBirth = "12/04/2008",
            isMinor = true,
            parentConsentGiven = true,
            parentEmailOrPhone = "9822001122",
            termsAccepted = true,
            isProfileComplete = true
        )

        assertEquals("user_123", profile.userId)
        assertEquals("Aarav Patil", profile.fullName)
        assertEquals(StudentLevel.CLASS_12, profile.level)
        assertTrue(profile.isMinor)
        assertTrue(profile.isProfileComplete)
        assertEquals(UserRole.STUDENT, profile.role)
    }

    @Test
    fun `test start destination state machine`() {
        fun computeStartDestination(
            isOnboardingDone: Boolean,
            hasAuthSession: Boolean,
            isProfileDone: Boolean
        ): String {
            return when {
                !isOnboardingDone -> "onboarding"
                !hasAuthSession -> "auth"
                !isProfileDone -> "profile_setup"
                else -> "home"
            }
        }

        assertEquals("onboarding", computeStartDestination(false, false, false))
        assertEquals("auth", computeStartDestination(true, false, false))
        assertEquals("profile_setup", computeStartDestination(true, true, false))
        assertEquals("home", computeStartDestination(true, true, true))
    }
}
