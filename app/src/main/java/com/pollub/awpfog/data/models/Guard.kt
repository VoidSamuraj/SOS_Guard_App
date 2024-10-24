package com.pollub.awpfog.data.models

import kotlinx.serialization.Serializable


/**
 * Data class representing a simplified version of a Guard.
 *
 * This class excludes sensitive data related to credentials and focuses on the personal information
 * necessary for identifying and managing the guard within the system.
 *
 * @property id Unique identifier for the guard.
 * @property name First name of the guard.
 * @property surname Last name of the guard.
 * @property phone Contact phone number of the guard.
 * @property email Email address of the guard.
 * @property statusCode Status code indicating the current status of the guard, which can be one of the following:
 * [Guard.GuardStatus.AVAILABLE], [Guard.GuardStatus.UNAVAILABLE], or [Guard.GuardStatus.INTERVENTION].
 * @property location Physical location or assigned area of the guard.
 * @property account_deleted Indicates whether the guard's account has been marked as deleted.
 *
 * @constructor Creates a GuardInfo instance with the specified details.
 */
@Serializable
data class GuardInfo(
    val id: Int,
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    var statusCode: Int,
    var location: String,
    val account_deleted: Boolean,
    var token: String? = null
)

/**
 * Data class representing a Guard.
 *
 * This class includes all necessary details about a guard, including their login credentials,
 * personal information, and status within the system.
 *
 * @property id Unique identifier for the guard.
 * @property login The login name for the guard's account, used for authentication.
 * @property password The password for the guard's account, which should be securely handled.
 * @property name First name of the guard.
 * @property surname Last name of the guard.
 * @property phone Contact phone number of the guard.
 * @property email Email address of the guard.
 * @property statusCode Status code indicating the current status of the guard, which can be one of the following:
 * [Guard.GuardStatus.AVAILABLE], [Guard.GuardStatus.UNAVAILABLE], or [Guard.GuardStatus.INTERVENTION].
 * @property location Physical location or assigned area of the guard.
 * @property account_deleted Indicates whether the guard's account has been marked as deleted.
 *
 * @constructor Creates a Guard instance with the specified details, including sensitive credentials.
 */
@Serializable
data class Guard(
    val id: Int,
    val login: String,
    val password: String,
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    var statusCode: Int,
    var location: String,
    val account_deleted: Boolean,
    var token: String? = null
) {


    companion object {
        /**
         * Creates a Guard instance from GuardInfo, allowing optional login and password.
         *
         * @param guardInfo The GuardInfo object containing the basic details of the guard.
         * @param login Optional login for the guard. Defaults to an empty string if not provided.
         * @param password Optional password for the guard. Defaults to an empty string if not provided.
         * @return A Guard object populated with data from the provided GuardInfo and optional credentials.
         */
        fun fromGuardInfo(
            guardInfo: GuardInfo,
            login: String? = null,
            password: String? = null
        ): Guard {
            return Guard(
                id = guardInfo.id,
                login = login ?: "",
                password = password ?: "",
                name = guardInfo.name,
                surname = guardInfo.surname,
                phone = guardInfo.phone,
                email = guardInfo.email,
                statusCode = guardInfo.statusCode,
                location = guardInfo.location,
                account_deleted = guardInfo.account_deleted,
                token = guardInfo.token
            )
        }
    }

    /**
     * Enum representing status of Guard.
     *
     * Contains 3 states: [GuardStatus.AVAILABLE], [GuardStatus.UNAVAILABLE], [GuardStatus.INTERVENTION]
     */
    enum class GuardStatus(val status: Int) {
        AVAILABLE(0),
        UNAVAILABLE(1),
        INTERVENTION(2);

        companion object {
            /**
             * Function to convert int to class instance.
             * @param value value of enum
             * @throws IllegalArgumentException if enum not contains object with such number
             */
            fun fromInt(value: Int) = entries.firstOrNull { it.status == value }
                ?: throw IllegalArgumentException("Unknown ReportStatus for value: $value")
        }
    }

    /**
     * Property representing the status of the Guard.
     *
     * This property derives its value from the statusCode field by converting it to the corresponding
     * GuardStatus instance using the fromInt method.
     */
    val status: GuardStatus
        get() = GuardStatus.fromInt(statusCode)

    /**
     * Converts the Guard instance to an GuardInfo instance.
     *
     * @return An GuardInfo instance containing the guard's details.
     */
    fun toGuardInfo(): GuardInfo {
        return GuardInfo(
            id,
            name,
            surname,
            phone,
            email,
            statusCode,
            location,
            account_deleted,
            token
        )
    }

}
