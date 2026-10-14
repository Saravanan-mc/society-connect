package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.model.VisitorPass
import com.example.data.model.VisitorPurpose
import com.example.data.model.VisitorStatus
import com.example.ui.SocietyUiState
import com.example.ui.components.EmptyStateView

@Composable
fun VisitorsScreen(
    uiState: SocietyUiState,
    onSearchChange: (String) -> Unit,
    onStatusFilterSelected: (VisitorStatus?) -> Unit,
    onNewVisitorClick: () -> Unit,
    onUpdateVisitorStatus: (String, VisitorStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredVisitors = remember(
        uiState.societyData.visitors,
        uiState.searchQuery,
        uiState.visitorStatusFilter
    ) {
        uiState.societyData.visitors.filter { vis ->
            val matchesStatus = uiState.visitorStatusFilter == null || vis.status == uiState.visitorStatusFilter
            val matchesSearch = uiState.searchQuery.isBlank() ||
                vis.visitorName.contains(uiState.searchQuery, ignoreCase = true) ||
                vis.passCode.contains(uiState.searchQuery, ignoreCase = true) ||
                vis.unitNumber.contains(uiState.searchQuery, ignoreCase = true) ||
                vis.vehicleNumber.contains(uiState.searchQuery, ignoreCase = true)
            matchesStatus && matchesSearch
        }
    }

    Scaffold(
        floatingActionButton = {
            if (uiState.currentUser.role != UserRole.SECURITY) {
                ExtendedFloatingActionButton(
                    onClick = onNewVisitorClick,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Pre-Approve Visitor") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("pre_approve_visitor_fab")
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
                    .testTag("visitor_search_input"),
                placeholder = { Text("Search by name, pass code, vehicle...") },
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
                        selected = uiState.visitorStatusFilter == null,
                        onClick = { onStatusFilterSelected(null) },
                        label = { Text("All (${uiState.societyData.visitors.size})") },
                        modifier = Modifier.testTag("filter_all_visitors")
                    )
                }
                items(VisitorStatus.values()) { status ->
                    val count = uiState.societyData.visitors.count { it.status == status }
                    FilterChip(
                        selected = uiState.visitorStatusFilter == status,
                        onClick = { onStatusFilterSelected(status) },
                        label = { Text("${status.name.replace("_", " ")} ($count)") },
                        modifier = Modifier.testTag("filter_visitor_${status.name.lowercase()}")
                    )
                }
            }

            if (filteredVisitors.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Badge,
                    title = "No Visitor Passes",
                    description = if (uiState.searchQuery.isNotBlank()) {
                        "No visitors found matching \"${uiState.searchQuery}\""
                    } else {
                        "No gate passes registered in this category."
                    }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredVisitors, key = { it.id }) { visitor ->
                        VisitorPassCard(
                            visitor = visitor,
                            currentRole = uiState.currentUser.role,
                            onStatusChange = { newStatus ->
                                onUpdateVisitorStatus(visitor.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VisitorPassCard(
    visitor: VisitorPass,
    currentRole: UserRole,
    onStatusChange: (VisitorStatus) -> Unit
) {
    val purposeIcon = when (visitor.purpose) {
        VisitorPurpose.DELIVERY -> Icons.Default.LocalShipping
        VisitorPurpose.CAB -> Icons.Default.DirectionsCar
        VisitorPurpose.GUEST -> Icons.Default.People
        else -> Icons.Default.Person
    }

    val (statusColor, statusBg) = when (visitor.status) {
        VisitorStatus.EXPECTED -> Pair(Color(0xFFD97706), Color(0xFFFEF3C7))
        VisitorStatus.CHECKED_IN -> Pair(Color(0xFF059669), Color(0xFFD1FAE5))
        VisitorStatus.CHECKED_OUT -> Pair(Color(0xFF64748B), Color(0xFFF1F5F9))
    }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visitor_card_${visitor.id}")
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
                // Purpose and Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = purposeIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = visitor.visitorName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${visitor.purpose.name} • Unit ${visitor.unitNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Pass Code Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.testTag("pass_code_${visitor.passCode}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = visitor.passCode,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Phone: ${visitor.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Vehicle: ${visitor.vehicleNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusBg
                    ) {
                        Text(
                            text = visitor.status.name.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    val timeLabel = when (visitor.status) {
                        VisitorStatus.EXPECTED -> "Expected: ${visitor.expectedDate}"
                        VisitorStatus.CHECKED_IN -> "Entry: ${visitor.checkInTime ?: "Just now"}"
                        VisitorStatus.CHECKED_OUT -> "Exit: ${visitor.checkOutTime ?: "Earlier"}"
                    }
                    Text(
                        text = timeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Action Buttons (Security Guard and Resident can check in / check out)
            if (visitor.status != VisitorStatus.CHECKED_OUT) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (visitor.status == VisitorStatus.EXPECTED) {
                        Button(
                            onClick = { onStatusChange(VisitorStatus.CHECKED_IN) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("action_checkin_${visitor.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check-In at Gate")
                        }
                    } else if (visitor.status == VisitorStatus.CHECKED_IN) {
                        OutlinedButton(
                            onClick = { onStatusChange(VisitorStatus.CHECKED_OUT) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("action_checkout_${visitor.id}")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark Check-Out")
                        }
                    }
                }
            }
        }
    }
}
