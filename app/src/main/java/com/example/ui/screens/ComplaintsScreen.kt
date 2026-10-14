package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Complaint
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.UserRole
import com.example.ui.SocietyUiState
import com.example.ui.components.EmptyStateView

@Composable
fun ComplaintsScreen(
    uiState: SocietyUiState,
    onSearchChange: (String) -> Unit,
    onStatusFilterSelected: (ComplaintStatus?) -> Unit,
    onNewComplaintClick: () -> Unit,
    onComplaintSelected: (Complaint) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredComplaints = remember(
        uiState.societyData.complaints,
        uiState.searchQuery,
        uiState.complaintStatusFilter
    ) {
        uiState.societyData.complaints.filter { comp ->
            val matchesStatus = uiState.complaintStatusFilter == null || comp.status == uiState.complaintStatusFilter
            val matchesSearch = uiState.searchQuery.isBlank() ||
                comp.title.contains(uiState.searchQuery, ignoreCase = true) ||
                comp.description.contains(uiState.searchQuery, ignoreCase = true) ||
                comp.unitNumber.contains(uiState.searchQuery, ignoreCase = true)
            matchesStatus && matchesSearch
        }
    }

    Scaffold(
        floatingActionButton = {
            // Residents and Admins can raise tickets
            if (uiState.currentUser.role != UserRole.SECURITY) {
                ExtendedFloatingActionButton(
                    onClick = onNewComplaintClick,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Raise Request") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("raise_complaint_fab")
                )
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("complaint_search_input"),
                placeholder = { Text("Search complaints, unit #, keywords...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Status Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.complaintStatusFilter == null,
                        onClick = { onStatusFilterSelected(null) },
                        label = { Text("All (${uiState.societyData.complaints.size})") },
                        modifier = Modifier.testTag("filter_all_complaints")
                    )
                }
                items(ComplaintStatus.values()) { status ->
                    val count = uiState.societyData.complaints.count { it.status == status }
                    FilterChip(
                        selected = uiState.complaintStatusFilter == status,
                        onClick = { onStatusFilterSelected(status) },
                        label = { Text("${status.name.replace("_", " ")} ($count)") },
                        modifier = Modifier.testTag("filter_status_${status.name.lowercase()}")
                    )
                }
            }

            if (filteredComplaints.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Build,
                    title = "No Requests Found",
                    description = if (uiState.searchQuery.isNotBlank()) {
                        "No complaints matched your search filter."
                    } else {
                        "Great news! There are no unresolved complaints in this category."
                    }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredComplaints, key = { it.id }) { complaint ->
                        ComplaintCard(
                            complaint = complaint,
                            onClick = { onComplaintSelected(complaint) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplaintCard(
    complaint: Complaint,
    onClick: () -> Unit
) {
    val categoryIcon = when (complaint.category) {
        ComplaintCategory.PLUMBING -> Icons.Default.Plumbing
        ComplaintCategory.ELECTRICAL -> Icons.Default.ElectricBolt
        ComplaintCategory.ELEVATOR -> Icons.Default.Elevator
        ComplaintCategory.SECURITY -> Icons.Default.Security
        ComplaintCategory.PARKING -> Icons.Default.LocalParking
        else -> Icons.Default.Build
    }

    val (statusColor, statusBg, statusIcon) = when (complaint.status) {
        ComplaintStatus.OPEN -> Triple(Color(0xFF2563EB), Color(0xFFDBEAFE), Icons.Default.Pending)
        ComplaintStatus.IN_PROGRESS -> Triple(Color(0xFFD97706), Color(0xFFFEF3C7), Icons.Default.HourglassTop)
        ComplaintStatus.RESOLVED -> Triple(Color(0xFF059669), Color(0xFFD1FAE5), Icons.Default.CheckCircle)
    }

    val priorityColor = when (complaint.priority) {
        ComplaintPriority.HIGH -> Color(0xFFDC2626)
        ComplaintPriority.MEDIUM -> Color(0xFFD97706)
        ComplaintPriority.LOW -> Color(0xFF059669)
    }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("complaint_card_${complaint.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category & Unit
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = complaint.category.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Unit ${complaint.unitNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = complaint.status.name.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = complaint.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = complaint.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (complaint.adminRemarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Admin Note: ${complaint.adminRemarks}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority Tag
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = priorityColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${complaint.priority.name} PRIORITY",
                        color = priorityColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = complaint.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
