package com.example.data

import com.example.data.model.Announcement
import com.example.data.model.AnnouncementCategory
import com.example.data.model.Complaint
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.EmergencyContact
import com.example.data.model.MaintenanceBill
import com.example.data.model.PaymentStatus
import com.example.data.model.SocietyData
import com.example.data.model.SocietyUser
import com.example.data.model.UserRole
import com.example.data.model.VisitorPass
import com.example.data.model.VisitorPurpose
import com.example.data.model.VisitorStatus

object DefaultSocietyData {

    val users = listOf(
        SocietyUser(
            id = "user_resident_1",
            name = "Alex Morgan",
            role = UserRole.RESIDENT,
            unitNumber = "A-402",
            phone = "+91 98765 43210",
            email = "alex.morgan@horizonresidence.com"
        ),
        SocietyUser(
            id = "user_admin_1",
            name = "Rajesh Sharma (Admin)",
            role = UserRole.ADMIN,
            unitNumber = "B-101",
            phone = "+91 98111 22334",
            email = "admin.rajesh@horizonresidence.com"
        ),
        SocietyUser(
            id = "user_security_1",
            name = "Suraj Singh (Gate 1)",
            role = UserRole.SECURITY,
            unitNumber = "Security Post A",
            phone = "+91 98222 55667",
            email = "gate1.security@horizonresidence.com"
        )
    )

    val announcements = listOf(
        Announcement(
            id = "ann_1",
            title = "Scheduled Water Supply Maintenance",
            content = "Due to overhead tank desiltation and pump servicing, water supply will be suspended tomorrow from 10:00 AM to 2:00 PM. Please store sufficient water in advance.",
            category = AnnouncementCategory.MAINTENANCE,
            isUrgent = true,
            date = "08 Sep 2026",
            postedBy = "Management Committee"
        ),
        Announcement(
            id = "ann_2",
            title = "Annual General Body Meeting (AGM) 2026",
            content = "The 12th AGM will be held on Sunday, September 20 at 6:30 PM in the Community Clubhouse Hall. Agenda includes FY26 audit review and clubhouse renovation proposal.",
            category = AnnouncementCategory.EVENT,
            isUrgent = false,
            date = "06 Sep 2026",
            postedBy = "Society Secretary"
        ),
        Announcement(
            id = "ann_3",
            title = "Updated EV Charging Guidelines",
            content = "Residents wishing to install dedicated EV chargers in their basement parking bays are requested to submit NOC forms to the estate office before connecting to meter boxes.",
            category = AnnouncementCategory.NOTICE,
            isUrgent = false,
            date = "04 Sep 2026",
            postedBy = "Technical Committee"
        ),
        Announcement(
            id = "ann_4",
            title = "Security Check: Visitor Pre-Approval Mandatory",
            content = "To maintain maximum safety, all residents are requested to issue digital gate passes via the app for guests and delivery personnel before arrival.",
            category = AnnouncementCategory.SECURITY,
            isUrgent = false,
            date = "01 Sep 2026",
            postedBy = "Chief Security Officer"
        )
    )

    val complaints = listOf(
        Complaint(
            id = "comp_1",
            title = "Water seepage near master balcony drain",
            description = "Rainwater seepage noticed around exterior balcony skirting. Need maintenance technician to inspect waterproofing.",
            category = ComplaintCategory.PLUMBING,
            priority = ComplaintPriority.HIGH,
            status = ComplaintStatus.IN_PROGRESS,
            unitNumber = "A-402",
            residentName = "Alex Morgan",
            createdAt = "07 Sep 2026, 04:15 PM",
            adminRemarks = "Plumber appointed for inspection on Tuesday morning."
        ),
        Complaint(
            id = "comp_2",
            title = "Passenger Lift 2 jerking during descent",
            description = "Block B elevator stops with a vibration on the 3rd floor. Please schedule Otis safety inspection.",
            category = ComplaintCategory.ELEVATOR,
            priority = ComplaintPriority.HIGH,
            status = ComplaintStatus.OPEN,
            unitNumber = "B-304",
            residentName = "Priya Nambiar",
            createdAt = "08 Sep 2026, 09:30 AM",
            adminRemarks = ""
        ),
        Complaint(
            id = "comp_3",
            title = "Basement Level 1 corridor tube light flickering",
            description = "Pillar 14 near visitor parking has a malfunctioning LED fixture making buzzing sound.",
            category = ComplaintCategory.ELECTRICAL,
            priority = ComplaintPriority.LOW,
            status = ComplaintStatus.RESOLVED,
            unitNumber = "A-102",
            residentName = "Rohan Verma",
            createdAt = "05 Sep 2026, 08:00 AM",
            adminRemarks = "Replaced with new 20W LED batten by electrician."
        ),
        Complaint(
            id = "comp_4",
            title = "Unauthorised car parked in bay A-402",
            description = "White Honda City (DL 09 CE 3421) parked in designated private parking slot.",
            category = ComplaintCategory.PARKING,
            priority = ComplaintPriority.MEDIUM,
            status = ComplaintStatus.RESOLVED,
            unitNumber = "A-402",
            residentName = "Alex Morgan",
            createdAt = "03 Sep 2026, 11:20 AM",
            adminRemarks = "Security contacted guest owner and vehicle moved to visitor bay."
        )
    )

    val visitors = listOf(
        VisitorPass(
            id = "vis_1",
            visitorName = "BlueDart Express Courier",
            phone = "+91 91234 56789",
            unitNumber = "A-402",
            purpose = VisitorPurpose.DELIVERY,
            vehicleNumber = "MH 12 AB 9942",
            passCode = "HZ-4091",
            expectedDate = "08 Sep 2026",
            status = VisitorStatus.CHECKED_IN,
            checkInTime = "11:20 AM"
        ),
        VisitorPass(
            id = "vis_2",
            visitorName = "Vikram & Neha Kapoor",
            phone = "+91 98333 44112",
            unitNumber = "A-402",
            purpose = VisitorPurpose.GUEST,
            vehicleNumber = "KA 05 MN 1204",
            passCode = "HZ-8823",
            expectedDate = "08 Sep 2026",
            status = VisitorStatus.EXPECTED
        ),
        VisitorPass(
            id = "vis_3",
            visitorName = "Aquaguard Service Technician",
            phone = "+91 97444 88123",
            unitNumber = "A-402",
            purpose = VisitorPurpose.SERVICE,
            vehicleNumber = "Two Wheeler",
            passCode = "HZ-1194",
            expectedDate = "07 Sep 2026",
            status = VisitorStatus.CHECKED_OUT,
            checkInTime = "02:15 PM",
            checkOutTime = "03:10 PM"
        ),
        VisitorPass(
            id = "vis_4",
            visitorName = "Uber Cab (Arun)",
            phone = "+91 96555 12345",
            unitNumber = "B-201",
            purpose = VisitorPurpose.CAB,
            vehicleNumber = "DL 1Y 5500",
            passCode = "HZ-6721",
            expectedDate = "08 Sep 2026",
            status = VisitorStatus.CHECKED_OUT,
            checkInTime = "08:45 AM",
            checkOutTime = "08:55 AM"
        )
    )

    val maintenanceBills = listOf(
        MaintenanceBill(
            id = "bill_sep_2026",
            billPeriod = "September 2026",
            unitNumber = "A-402",
            amount = 3850.00,
            dueDate = "15 Sep 2026",
            status = PaymentStatus.PENDING,
            breakdown = "Society Maintenance: ₹2,800 | Sinking Fund: ₹550 | Water Charges: ₹500"
        ),
        MaintenanceBill(
            id = "bill_aug_2026",
            billPeriod = "August 2026",
            unitNumber = "A-402",
            amount = 3850.00,
            dueDate = "15 Aug 2026",
            status = PaymentStatus.PAID,
            breakdown = "Society Maintenance: ₹2,800 | Sinking Fund: ₹550 | Water Charges: ₹500",
            paidDate = "10 Aug 2026",
            transactionId = "TXN-HZ-20260810-9482",
            paymentMethod = "UPI (Google Pay)"
        ),
        MaintenanceBill(
            id = "bill_jul_2026",
            billPeriod = "July 2026",
            unitNumber = "A-402",
            amount = 3850.00,
            dueDate = "15 Jul 2026",
            status = PaymentStatus.PAID,
            breakdown = "Society Maintenance: ₹2,800 | Sinking Fund: ₹550 | Water Charges: ₹500",
            paidDate = "08 Jul 2026",
            transactionId = "TXN-HZ-20260708-4109",
            paymentMethod = "Net Banking (HDFC)"
        )
    )

    val emergencyContacts = listOf(
        EmergencyContact(
            id = "cont_1",
            name = "Main Gate Security Desk",
            roleTitle = "Security Head (Suraj)",
            phoneNumber = "+91 98222 55667",
            category = "Security",
            timing = "24/7 Available"
        ),
        EmergencyContact(
            id = "cont_2",
            name = "Society Estate Manager",
            roleTitle = "Estate Office (Mr. Paul)",
            phoneNumber = "+91 98450 11223",
            category = "Facility",
            timing = "9:00 AM - 6:00 PM"
        ),
        EmergencyContact(
            id = "cont_3",
            name = "Emergency Electrician",
            roleTitle = "On-Call Electrical Support",
            phoneNumber = "+91 98190 33445",
            category = "Technical",
            timing = "24/7 On-Call"
        ),
        EmergencyContact(
            id = "cont_4",
            name = "Emergency Plumber",
            roleTitle = "Ramesh Kumar (Plumbing)",
            phoneNumber = "+91 98200 66778",
            category = "Technical",
            timing = "8:00 AM - 8:00 PM"
        ),
        EmergencyContact(
            id = "cont_5",
            name = "Local Police Station",
            roleTitle = "Sector 14 Police Post",
            phoneNumber = "112",
            category = "Emergency",
            timing = "24/7 Emergency"
        ),
        EmergencyContact(
            id = "cont_6",
            name = "Nearest Trauma Hospital / Ambulance",
            roleTitle = "Horizon Care Medical Center",
            phoneNumber = "108",
            category = "Medical",
            timing = "24/7 Emergency"
        ),
        EmergencyContact(
            id = "cont_7",
            name = "Fire Station Control Room",
            roleTitle = "District Fire Brigade",
            phoneNumber = "101",
            category = "Emergency",
            timing = "24/7 Emergency"
        )
    )

    fun getInitialData(): SocietyData {
        return SocietyData(
            users = users,
            currentUserId = "user_resident_1",
            announcements = announcements,
            complaints = complaints,
            visitors = visitors,
            maintenanceBills = maintenanceBills,
            emergencyContacts = emergencyContacts
        )
    }
}
