package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultSocietyData
import com.example.data.model.Announcement
import com.example.data.model.AnnouncementCategory
import com.example.data.model.Complaint
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.MaintenanceBill
import com.example.data.model.PaymentStatus
import com.example.data.model.SocietyData
import com.example.data.model.SocietyUser
import com.example.data.model.UserRole
import com.example.data.model.VisitorPass
import com.example.data.model.VisitorPurpose
import com.example.data.model.VisitorStatus
import com.example.data.repository.SocietyJsonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class SocietyNavTab {
    NOTICES,
    COMPLAINTS,
    VISITORS,
    PAYMENTS,
    DIRECTORY
}

data class SocietyUiState(
    val isLoading: Boolean = true,
    val societyData: SocietyData = DefaultSocietyData.getInitialData(),
    val selectedTab: SocietyNavTab = SocietyNavTab.NOTICES,
    val searchQuery: String = "",
    val noticeCategoryFilter: AnnouncementCategory? = null,
    val complaintStatusFilter: ComplaintStatus? = null,
    val visitorStatusFilter: VisitorStatus? = null,
    val userMessage: String? = null,
    val showNewComplaintDialog: Boolean = false,
    val showNewVisitorDialog: Boolean = false,
    val showNewNoticeDialog: Boolean = false,
    val billToPay: MaintenanceBill? = null,
    val paymentReceipt: MaintenanceBill? = null,
    val selectedComplaintDetail: Complaint? = null,
    val showRoleSwitcherSheet: Boolean = false,
    val jsonFileStoragePath: String = "",
    val jsonFileSize: Long = 0L
) {
    val currentUser: SocietyUser
        get() = societyData.users.firstOrNull { it.id == societyData.currentUserId }
            ?: societyData.users.firstOrNull()
            ?: DefaultSocietyData.users.first()

    val pendingDuesCount: Int
        get() = societyData.maintenanceBills.count { it.status != PaymentStatus.PAID }

    val openComplaintsCount: Int
        get() = societyData.complaints.count { it.status == ComplaintStatus.OPEN || it.status == ComplaintStatus.IN_PROGRESS }

    val activeVisitorsCount: Int
        get() = societyData.visitors.count { it.status == VisitorStatus.CHECKED_IN || it.status == VisitorStatus.EXPECTED }
}

class SocietyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SocietyJsonRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(SocietyUiState())
    val uiState: StateFlow<SocietyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initialize()
            refreshStorageDetails()
            repository.societyDataFlow.collect { data ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        societyData = data
                    )
                }
                refreshStorageDetails()
            }
        }
    }

    private fun refreshStorageDetails() {
        val (path, size) = repository.getStorageFileDetails()
        _uiState.update { it.copy(jsonFileStoragePath = path, jsonFileSize = size) }
    }

    fun selectTab(tab: SocietyNavTab) {
        _uiState.update { it.copy(selectedTab = tab, searchQuery = "") }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setNoticeCategoryFilter(category: AnnouncementCategory?) {
        _uiState.update { it.copy(noticeCategoryFilter = category) }
    }

    fun setComplaintStatusFilter(status: ComplaintStatus?) {
        _uiState.update { it.copy(complaintStatusFilter = status) }
    }

    fun setVisitorStatusFilter(status: VisitorStatus?) {
        _uiState.update { it.copy(visitorStatusFilter = status) }
    }

    fun switchRole(role: UserRole) {
        viewModelScope.launch {
            repository.switchUserRole(role)
            _uiState.update {
                it.copy(
                    showRoleSwitcherSheet = false,
                    userMessage = "Switched to ${role.name.lowercase().replaceFirstChar { c -> c.uppercase() }} profile"
                )
            }
        }
    }

    fun openNewComplaintDialog(open: Boolean) {
        _uiState.update { it.copy(showNewComplaintDialog = open) }
    }

    fun openNewVisitorDialog(open: Boolean) {
        _uiState.update { it.copy(showNewVisitorDialog = open) }
    }

    fun openNewNoticeDialog(open: Boolean) {
        _uiState.update { it.copy(showNewNoticeDialog = open) }
    }

    fun setBillToPay(bill: MaintenanceBill?) {
        _uiState.update { it.copy(billToPay = bill) }
    }

    fun setPaymentReceipt(bill: MaintenanceBill?) {
        _uiState.update { it.copy(paymentReceipt = bill) }
    }

    fun setSelectedComplaintDetail(complaint: Complaint?) {
        _uiState.update { it.copy(selectedComplaintDetail = complaint) }
    }

    fun openRoleSwitcherSheet(open: Boolean) {
        _uiState.update { it.copy(showRoleSwitcherSheet = open) }
    }

    fun submitComplaint(
        title: String,
        category: ComplaintCategory,
        priority: ComplaintPriority,
        description: String
    ) {
        val user = _uiState.value.currentUser
        val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val newComplaint = Complaint(
            id = "comp_" + UUID.randomUUID().toString().take(6),
            title = title.trim(),
            description = description.trim(),
            category = category,
            priority = priority,
            status = ComplaintStatus.OPEN,
            unitNumber = user.unitNumber,
            residentName = user.name,
            createdAt = dateFormatted,
            adminRemarks = ""
        )

        viewModelScope.launch {
            repository.addComplaint(newComplaint)
            _uiState.update {
                it.copy(
                    showNewComplaintDialog = false,
                    userMessage = "Complaint raised successfully (#${newComplaint.id})"
                )
            }
        }
    }

    fun updateComplaintStatus(
        complaintId: String,
        newStatus: ComplaintStatus,
        remarks: String?
    ) {
        viewModelScope.launch {
            repository.updateComplaintStatus(complaintId, newStatus, remarks)
            _uiState.update {
                it.copy(
                    selectedComplaintDetail = null,
                    userMessage = "Complaint status updated to ${newStatus.name.replace("_", " ")}"
                )
            }
        }
    }

    fun submitVisitorPass(
        visitorName: String,
        phone: String,
        purpose: VisitorPurpose,
        vehicleNo: String
    ) {
        val user = _uiState.value.currentUser
        val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val randomPassCode = "HZ-" + (1000..9999).random()

        val newPass = VisitorPass(
            id = "vis_" + UUID.randomUUID().toString().take(6),
            visitorName = visitorName.trim(),
            phone = phone.trim(),
            unitNumber = user.unitNumber,
            purpose = purpose,
            vehicleNumber = vehicleNo.trim().ifEmpty { "None" },
            passCode = randomPassCode,
            expectedDate = dateFormatted,
            status = VisitorStatus.EXPECTED
        )

        viewModelScope.launch {
            repository.addVisitor(newPass)
            _uiState.update {
                it.copy(
                    showNewVisitorDialog = false,
                    userMessage = "Pass created for $visitorName (Code: $randomPassCode)"
                )
            }
        }
    }

    fun updateVisitorStatus(visitorId: String, status: VisitorStatus) {
        viewModelScope.launch {
            repository.updateVisitorStatus(visitorId, status)
            val actionText = when (status) {
                VisitorStatus.CHECKED_IN -> "Checked in successfully"
                VisitorStatus.CHECKED_OUT -> "Checked out successfully"
                VisitorStatus.EXPECTED -> "Marked as expected"
            }
            _uiState.update {
                it.copy(userMessage = "Visitor $actionText")
            }
        }
    }

    fun submitAnnouncement(
        title: String,
        content: String,
        category: AnnouncementCategory,
        isUrgent: Boolean
    ) {
        val user = _uiState.value.currentUser
        val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val newNotice = Announcement(
            id = "ann_" + UUID.randomUUID().toString().take(6),
            title = title.trim(),
            content = content.trim(),
            category = category,
            isUrgent = isUrgent,
            date = dateFormatted,
            postedBy = user.name
        )

        viewModelScope.launch {
            repository.addAnnouncement(newNotice)
            _uiState.update {
                it.copy(
                    showNewNoticeDialog = false,
                    userMessage = "Announcement published to Notice Board"
                )
            }
        }
    }

    fun payBill(billId: String, paymentMethod: String) {
        viewModelScope.launch {
            val paidBill = repository.payMaintenanceBill(billId, paymentMethod)
            _uiState.update {
                it.copy(
                    billToPay = null,
                    paymentReceipt = paidBill,
                    userMessage = "Payment successful! Receipt generated."
                )
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetToDefaultData()
            _uiState.update {
                it.copy(
                    userMessage = "Society data restored to initial JSON default state",
                    showRoleSwitcherSheet = false
                )
            }
        }
    }
}
