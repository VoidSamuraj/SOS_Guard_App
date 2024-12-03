package com.pollub.awpfoc.data.models

import kotlinx.serialization.Serializable

@Serializable
data class JWTToken(val token: String)