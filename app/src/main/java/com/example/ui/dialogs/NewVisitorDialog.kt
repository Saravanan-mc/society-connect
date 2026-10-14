package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.VisitorPurpose

@Composable
fun NewVisitorDialog(
    unitNumber: String,
    onDismiss: () -> Unit,
    onSubmit: (visitorName: String, phone: String, purpose: VisitorPurpose, vehicleNo: String) -> Unit
) {
    var visitorName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var vehicleNo by remember { mutableStateOf("") }
    var selectedPurpose by remember { mutableStateOf(VisitorPurpose.GUEST) }
    var purposeDropdownExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Pre-Approve Visitor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Generating gate pass for Unit: $unitNumber",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Purpose Selector
                Column {
                    Text(
                        text = "Visit Purpose",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = selectedPurpose.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Purpose")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { purposeDropdownExpanded = true }
                            .testTag("visitor_purpose_field"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    DropdownMenu(
                        expanded = purposeDropdownExpanded,
                        onDismissRequest = { purposeDropdownExpanded = false }
                    ) {
                        VisitorPurpose.values().forEach { purpose ->
                            DropdownMenuItem(
                                text = { Text(purpose.name) },
                                onClick = {
                                    selectedPurpose = purpose
                                    purposeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Visitor Name
                OutlinedTextField(
                    value = visitorName,
                    onValueChange = {
                        visitorName = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Visitor / Driver Name *") },
                    placeholder = { Text("e.g. Rahul Sharma or Swiggy Delivery") },
                    isError = nameError,
                    supportingText = {
                        if (nameError) Text("Please enter visitor name")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("visitor_name_input")
                )

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("e.g. +91 98765 43210") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("visitor_phone_input")
                )

                // Vehicle
                OutlinedTextField(
                    value = vehicleNo,
                    onValueChange = { vehicleNo = it },
                    label = { Text("Vehicle Number (Optional)") },
                    placeholder = { Text("e.g. MH 02 AB 1234 or Cab") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("visitor_vehicle_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (visitorName.isBlank()) {
                        nameError = true
                    } else {
                        onSubmit(visitorName, phone.ifEmpty { "Not Provided" }, selectedPurpose, vehicleNo)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("generate_visitor_pass_button")
            ) {
                Text("Generate Pass")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
