package com.example.data.model

import com.squareup.moshi.JsonClass

enum class UserRole {
    RESIDENT,
    ADMIN,
    SECURITY
}

@JsonClass(generateAdapter = true)
data class SocietyUser(
    val id: String,
    val name: String,
    val role: UserRole,
    val unitNumber: String,
    val phone: String,
    val email: String
)

enum class AnnouncementCategory {
    NOTICE,
    MAINTENANCE,
    SECURITY,
    EVENT
}

@JsonClass(generateAdapter = true)
data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val category: AnnouncementCategory,
    val isUrgent: Boolean,
    val date: String,
    val postedBy: String
)

enum class ComplaintCategory {
    PLUMBING,
    ELECTRICAL,
    ELEVATOR,
    SECURITY,
    CLEANLINESS,
    NOISE,
    PARKING,
    OTHER
}

enum class ComplaintPriority {
    HIGH,
    MEDIUM,
    LOW
}

enum class ComplaintStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED
}

@JsonClass(generateAdapter = true)
data class Complaint(
    val id: String,
    val title: String,
    val description: String,
    val category: ComplaintCategory,
    val priority: ComplaintPriority,
    val status: ComplaintStatus,
    val unitNumber: String,
    val residentName: String,
    val createdAt: String,
    val adminRemarks: String = ""
)

enum class VisitorStatus {
    EXPECTED,
    CHECKED_IN,
    CHECKED_OUT
}

enum class VisitorPurpose {
    GUEST,
    DELIVERY,
    CAB,
    SERVICE,
    OTHER
}

@JsonClass(generateAdapter = true)
data class VisitorPass(
    val id: String,
    val visitorName: String,
    val phone: String,
    val unitNumber: String,
    val purpose: VisitorPurpose,
    val vehicleNumber: String,
    val passCode: String,
    val expectedDate: String,
    val status: VisitorStatus,
    val checkInTime: String? = null,
    val checkOutTime: String? = null
)

enum class PaymentStatus {
    PENDING,
    PAID,
    OVERDUE
}

@JsonClass(generateAdapter = true)
data class MaintenanceBill(
    val id: String,
    val billPeriod: String,
    val unitNumber: String,
    val amount: Double,
    val dueDate: String,
    val status: PaymentStatus,
    val breakdown: String,
    val paidDate: String? = null,
    val transactionId: String? = null,
    val paymentMethod: String? = null
)

@JsonClass(generateAdapter = true)
data class EmergencyContact(
    val id: String,
    val name: String,
    val roleTitle: String,
    val phoneNumber: String,
    val category: String,
    val timing: String
)

@JsonClass(generateAdapter = true)
data class SocietyData(
    val users: List<SocietyUser> = emptyList(),
    val currentUserId: String = "user_resident_1",
    val announcements: List<Announcement> = emptyList(),
    val complaints: List<Complaint> = emptyList(),
    val visitors: List<VisitorPass> = emptyList(),
    val maintenanceBills: List<MaintenanceBill> = emptyList(),
    val emergencyContacts: List<EmergencyContact> = emptyList()
)
