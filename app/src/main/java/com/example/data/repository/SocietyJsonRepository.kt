package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.DefaultSocietyData
import com.example.data.model.Announcement
import com.example.data.model.Complaint
import com.example.data.model.ComplaintStatus
import com.example.data.model.MaintenanceBill
import com.example.data.model.PaymentStatus
import com.example.data.model.SocietyData
import com.example.data.model.UserRole
import com.example.data.model.VisitorPass
import com.example.data.model.VisitorStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SocietyJsonRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter = moshi.adapter(SocietyData::class.java).indent("  ")
    private val storageFile = File(context.filesDir, "society_community_data.json")

    private val _societyDataFlow = MutableStateFlow(DefaultSocietyData.getInitialData())
    val societyDataFlow: StateFlow<SocietyData> = _societyDataFlow.asStateFlow()

    suspend fun initialize() = withContext(ioDispatcher) {
        try {
            if (!storageFile.exists()) {
                val initial = DefaultSocietyData.getInitialData()
                val json = jsonAdapter.toJson(initial)
                storageFile.writeText(json)
                _societyDataFlow.value = initial
            } else {
                val json = storageFile.readText()
                val loaded = jsonAdapter.fromJson(json)
                if (loaded != null) {
                    _societyDataFlow.value = loaded
                } else {
                    val initial = DefaultSocietyData.getInitialData()
                    storageFile.writeText(jsonAdapter.toJson(initial))
                    _societyDataFlow.value = initial
                }
            }
        } catch (e: Exception) {
            Log.e("SocietyRepository", "Error loading JSON data: ${e.message}", e)
            val fallback = DefaultSocietyData.getInitialData()
            _societyDataFlow.value = fallback
        }
    }

    private suspend fun persistCurrentState(data: SocietyData) = withContext(ioDispatcher) {
        try {
            val json = jsonAdapter.toJson(data)
            storageFile.writeText(json)
            _societyDataFlow.value = data
        } catch (e: Exception) {
            Log.e("SocietyRepository", "Error saving JSON data: ${e.message}", e)
        }
    }

    suspend fun switchUserRole(role: UserRole) {
        val current = _societyDataFlow.value
        val user = current.users.firstOrNull { it.role == role } ?: return
        val updated = current.copy(currentUserId = user.id)
        persistCurrentState(updated)
    }

    suspend fun addComplaint(complaint: Complaint) {
        val current = _societyDataFlow.value
        val updatedList = listOf(complaint) + current.complaints
        val updated = current.copy(complaints = updatedList)
        persistCurrentState(updated)
    }

    suspend fun updateComplaintStatus(
        complaintId: String,
        newStatus: ComplaintStatus,
        adminRemarks: String?
    ) {
        val current = _societyDataFlow.value
        val updatedList = current.complaints.map { comp ->
            if (comp.id == complaintId) {
                comp.copy(
                    status = newStatus,
                    adminRemarks = adminRemarks ?: comp.adminRemarks
                )
            } else comp
        }
        val updated = current.copy(complaints = updatedList)
        persistCurrentState(updated)
    }

    suspend fun addVisitor(visitor: VisitorPass) {
        val current = _societyDataFlow.value
        val updatedList = listOf(visitor) + current.visitors
        val updated = current.copy(visitors = updatedList)
        persistCurrentState(updated)
    }

    suspend fun updateVisitorStatus(visitorId: String, newStatus: VisitorStatus) {
        val current = _societyDataFlow.value
        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val updatedList = current.visitors.map { vis ->
            if (vis.id == visitorId) {
                when (newStatus) {
                    VisitorStatus.CHECKED_IN -> vis.copy(status = newStatus, checkInTime = timeNow)
                    VisitorStatus.CHECKED_OUT -> vis.copy(status = newStatus, checkOutTime = timeNow)
                    VisitorStatus.EXPECTED -> vis.copy(status = newStatus)
                }
            } else vis
        }
        val updated = current.copy(visitors = updatedList)
        persistCurrentState(updated)
    }

    suspend fun addAnnouncement(announcement: Announcement) {
        val current = _societyDataFlow.value
        val updatedList = listOf(announcement) + current.announcements
        val updated = current.copy(announcements = updatedList)
        persistCurrentState(updated)
    }

    suspend fun payMaintenanceBill(billId: String, method: String): MaintenanceBill? {
        val current = _societyDataFlow.value
        val dateNow = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val txnId = "TXN-HZ-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-${(1000..9999).random()}"
        var paidBill: MaintenanceBill? = null

        val updatedList = current.maintenanceBills.map { bill ->
            if (bill.id == billId) {
                val paid = bill.copy(
                    status = PaymentStatus.PAID,
                    paidDate = dateNow,
                    transactionId = txnId,
                    paymentMethod = method
                )
                paidBill = paid
                paid
            } else bill
        }
        val updated = current.copy(maintenanceBills = updatedList)
        persistCurrentState(updated)
        return paidBill
    }

    suspend fun resetToDefaultData() = withContext(ioDispatcher) {
        val initial = DefaultSocietyData.getInitialData()
        persistCurrentState(initial)
    }

    fun getStorageFileDetails(): Pair<String, Long> {
        return Pair(storageFile.absolutePath, if (storageFile.exists()) storageFile.length() else 0L)
    }
}
