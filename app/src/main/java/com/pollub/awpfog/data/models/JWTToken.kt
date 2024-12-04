package com.pollub.awpfog.data.models

import kotlinx.serialization.Serializable

@Serializable
data class JWTToken(val token: String)