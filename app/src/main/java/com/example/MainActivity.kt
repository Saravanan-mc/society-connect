package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SocietyNavTab
import com.example.ui.SocietyViewModel
import com.example.ui.components.SocietyBottomNav
import com.example.ui.components.SocietyTopBar
import com.example.ui.dialogs.ComplaintDetailDialog
import com.example.ui.dialogs.NewComplaintDialog
import com.example.ui.dialogs.NewNoticeDialog
import com.example.ui.dialogs.NewVisitorDialog
import com.example.ui.dialogs.PayBillDialog
import com.example.ui.dialogs.PaymentReceiptDialog
import com.example.ui.dialogs.RoleSwitcherSheet
import com.example.ui.screens.ComplaintsScreen
import com.example.ui.screens.DirectoryScreen
import com.example.ui.screens.NoticesScreen
import com.example.ui.screens.PaymentsScreen
import com.example.ui.screens.VisitorsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SocietyApp()
            }
        }
    }
}

@Composable
fun SocietyApp(viewModel: SocietyViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        topBar = {
            SocietyTopBar(
                uiState = uiState,
                onOpenRoleSwitcher = { viewModel.openRoleSwitcherSheet(true) }
            )
        },
        bottomBar = {
            SocietyBottomNav(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .testTag("society_app_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading Society Connect...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                AnimatedContent(
                    targetState = uiState.selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        SocietyNavTab.NOTICES -> NoticesScreen(
                            uiState = uiState,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onCategorySelected = { viewModel.setNoticeCategoryFilter(it) },
                            onNewNoticeClick = { viewModel.openNewNoticeDialog(true) }
                        )
                        SocietyNavTab.COMPLAINTS -> ComplaintsScreen(
                            uiState = uiState,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onStatusFilterSelected = { viewModel.setComplaintStatusFilter(it) },
                            onNewComplaintClick = { viewModel.openNewComplaintDialog(true) },
                            onComplaintSelected = { viewModel.setSelectedComplaintDetail(it) }
                        )
                        SocietyNavTab.VISITORS -> VisitorsScreen(
                            uiState = uiState,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onStatusFilterSelected = { viewModel.setVisitorStatusFilter(it) },
                            onNewVisitorClick = { viewModel.openNewVisitorDialog(true) },
                            onUpdateVisitorStatus = { id, status -> viewModel.updateVisitorStatus(id, status) }
                        )
                        SocietyNavTab.PAYMENTS -> PaymentsScreen(
                            uiState = uiState,
                            onPayBillClick = { viewModel.setBillToPay(it) },
                            onViewReceiptClick = { viewModel.setPaymentReceipt(it) }
                        )
                        SocietyNavTab.DIRECTORY -> DirectoryScreen(
                            uiState = uiState,
                            onSearchChange = { viewModel.setSearchQuery(it) }
                        )
                    }
                }
            }
        }

        // Active Dialogs & Sheets
        if (uiState.showNewComplaintDialog) {
            NewComplaintDialog(
                unitNumber = uiState.currentUser.unitNumber,
                onDismiss = { viewModel.openNewComplaintDialog(false) },
                onSubmit = { title, category, priority, desc ->
                    viewModel.submitComplaint(title, category, priority, desc)
                }
            )
        }

        if (uiState.showNewVisitorDialog) {
            NewVisitorDialog(
                unitNumber = uiState.currentUser.unitNumber,
                onDismiss = { viewModel.openNewVisitorDialog(false) },
                onSubmit = { name, phone, purpose, vehicle ->
                    viewModel.submitVisitorPass(name, phone, purpose, vehicle)
                }
            )
        }

        if (uiState.showNewNoticeDialog) {
            NewNoticeDialog(
                onDismiss = { viewModel.openNewNoticeDialog(false) },
                onSubmit = { title, content, category, isUrgent ->
                    viewModel.submitAnnouncement(title, content, category, isUrgent)
                }
            )
        }

        uiState.billToPay?.let { bill ->
            PayBillDialog(
                bill = bill,
                onDismiss = { viewModel.setBillToPay(null) },
                onConfirmPay = { id, method -> viewModel.payBill(id, method) }
            )
        }

        uiState.paymentReceipt?.let { bill ->
            PaymentReceiptDialog(
                bill = bill,
                onDismiss = { viewModel.setPaymentReceipt(null) }
            )
        }

        uiState.selectedComplaintDetail?.let { complaint ->
            ComplaintDetailDialog(
                complaint = complaint,
                currentUserRole = uiState.currentUser.role,
                onDismiss = { viewModel.setSelectedComplaintDetail(null) },
                onUpdateStatus = { id, status, remarks ->
                    viewModel.updateComplaintStatus(id, status, remarks)
                }
            )
        }

        if (uiState.showRoleSwitcherSheet) {
            RoleSwitcherSheet(
                uiState = uiState,
                onDismiss = { viewModel.openRoleSwitcherSheet(false) },
                onSelectRole = { viewModel.switchRole(it) },
                onResetData = { viewModel.resetAllData() }
            )
        }
    }
}
