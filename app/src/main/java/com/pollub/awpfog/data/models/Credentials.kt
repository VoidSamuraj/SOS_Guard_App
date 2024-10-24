package com.pollub.awpfog.data.models

import kotlinx.serialization.Serializable

/**
 * Data class representing user credentials for authentication.
 *
 * @property login The username or email of the user.
 * @property password The password associated with the user's account.
 */
@Serializable
data class Credentials(
    val login: String,
    val password: String
)