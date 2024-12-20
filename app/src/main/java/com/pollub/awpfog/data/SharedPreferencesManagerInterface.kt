package com.pollub.awpfog.data

import com.pollub.awpfog.data.models.Guard

interface SharedPreferencesManagerInterface {
    fun saveStatus(status: Guard.GuardStatus)
    fun getStatus(): Int
    fun saveSecureToken(token:String)
    fun getSecureToken():String?
    fun removeSecureToken()
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveReportId(reportId: Int)
    fun getReportId(): Int
    fun saveGuard(guard: Guard)
    fun getGuard(): Guard
    fun getGuardName():String
    fun clear()
}