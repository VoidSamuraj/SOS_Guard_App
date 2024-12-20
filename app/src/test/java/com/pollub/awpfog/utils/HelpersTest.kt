package com.pollub.awpfog.utils

import org.junit.Test

class HelpersTest {
    @Test
    fun `isUsernameValid returns true for valid username`() {
        assert(isUsernameValid("ValidUser"))
        assert(isUsernameValid("abc")) // exactly 3 characters
        assert(isUsernameValid("a".repeat(40))) // exactly 40 characters
    }

    @Test
    fun `isUsernameValid returns false for invalid username`() {
        assert(!isUsernameValid("ab")) // less than 3 characters
        assert(!isUsernameValid("a".repeat(41))) // more than 40 characters
        assert(!isUsernameValid("")) // empty string
    }

    @Test
    fun `isLoginValid returns true for valid login username`() {
        assert(isLoginValid("ValidLogin"))
        assert(isLoginValid("abc")) // exactly 3 characters
        assert(isLoginValid("a".repeat(20))) // exactly 20 characters
    }

    @Test
    fun `isLoginValid returns false for invalid login username`() {
        assert(!isLoginValid("ab")) // less than 3 characters
        assert(!isLoginValid("a".repeat(21))) // more than 20 characters
        assert(!isLoginValid("")) // empty string
    }

    @Test
    fun `isPasswordValid returns true for valid password`() {
        assert(isPasswordValid("Password1!"))
        assert(isPasswordValid("Aa1@asdf"))
        assert(isPasswordValid("StrongP@ssw0rd"))
    }

    @Test
    fun `isPasswordValid returns false for invalid password`() {
        assert(!isPasswordValid("password")) // no uppercase, digit, or special character
        assert(!isPasswordValid("PASSWORD1")) // no lowercase or special character
        assert(!isPasswordValid("Passw1")) // less than 8 characters
        assert(!isPasswordValid("Passw0rd")) // no special character
    }

    @Test
    fun `isPhoneValid returns true for valid phone number`() {
        assert(isPhoneValid("1234567890")) // exactly 10 digits
        assert(isPhoneValid("+123456789012")) // with '+' and 12 digits
        assert(isPhoneValid("1234567890123")) // exactly 13 digits
    }

    @Test
    fun `isPhoneValid returns false for invalid phone number`() {
        assert(!isPhoneValid("123")) // less than 10 digits
        assert(!isPhoneValid("+12345678901234")) // more than 13 digits
        assert(!isPhoneValid("abc1234567")) // contains letters
        assert(!isPhoneValid("123-456-7890")) // contains non-digit characters
    }

    @Test
    fun `isEmailValid returns true for valid email`() {
        assert(isEmailValid("test@example.com"))
        assert(isEmailValid("user.name+alias@domain.co"))
        assert(isEmailValid("email@sub.domain.com"))
    }

    @Test
    fun `isEmailValid returns false for invalid email`() {
        assert(!isEmailValid("plainaddress")) // no '@'
        assert(!isEmailValid("@missinglocalpart.com")) // no local part
        assert(!isEmailValid("user@.com")) // invalid domain
        assert(!isEmailValid("user@com")) // missing TLD
        assert(!isEmailValid("")) // empty string
    }
}