# Society Connect — Residential Community Management App

A modern, clean, and intuitive Android application designed for residential societies and gated communities. It simplifies daily community operations, member communications, visitor approvals, service requests/complaints, and maintenance dues tracking with local JSON persistence.

Built for the **Horizon Broadband Technical Assessment** using **Kotlin**, **Jetpack Compose**, and **Material Design 3**.

---

## 📱 Features Implemented

### 1. Digital Notice Board & Society Announcements
- **Categorized Notices**: View official circulars tagged as *Notice*, *Maintenance*, *Security*, or *Events*.
- **Urgent Broadcasts**: High-priority notices marked with urgent badge styling.
- **Search & Filtering**: Filter by category or search through notices in real-time.
- **Admin Publishing**: Admins can draft and publish new society notices directly to the board.

### 2. Service Requests & Complaint Management
- **Issue Tracking**: Resident tickets categorized by *Plumbing*, *Electrical*, *Elevator*, *Parking*, *Security*, etc.
- **Priority & Status Badges**: High/Medium/Low priority levels with status progression (*Open*, *In Progress*, *Resolved*).
- **Interactive Resolution**: Click to inspect tickets, view management remarks, and allow Admins to update status and add resolution notes.
- **Resident Ticket Creation**: Form with category dropdown, priority picker, and issue description.

### 3. Digital Visitor Management & Gate Passes
- **Pre-Approved Gate Passes**: Residents generate 6-digit access passes (e.g., `HZ-4091`) with visitor name, phone, vehicle number, and purpose (*Guest*, *Delivery*, *Cab*, *Service*).
- **Gate Check-In & Check-Out**: Security guards and residents can mark visitor arrivals and departures with automatic entry/exit timestamps.
- **Visitor Search**: Instant filter by status (*Expected*, *Checked In*, *Checked Out*) or search by passcode and vehicle number.

### 4. Maintenance Payment Tracking & Digital Receipts
- **Billing Summary**: Real-time overview of total pending dues and current billing cycle for the unit.
- **Itemized Breakdown**: View transparent charge allocations (Society Maintenance, Sinking Fund, Water Charges).
- **Mock Payment Checkout**: Integrated payment dialog supporting *UPI (GPay/PhonePe/Paytm)*, *Credit/Debit Cards*, and *Net Banking*.
- **Digital Receipt Generation**: Instant receipt with unique Transaction ID, payment timestamp, and breakdown stored to society records.

### 5. Emergency & Community Services Directory
- **Essential Contacts**: Quick access to Security Gate, Estate Office, Electrician, Plumber, Police Station, Hospital/Ambulance, and Fire Brigade.
- **One-Tap Dialing**: Directly triggers the device phone dialer with telephone URI.
- **One-Tap Copy**: Copy contact numbers to clipboard with visual toast confirmation.
- **Availability Info**: Service hours and 24/7 emergency tags.

### 6. Role-Based Switcher (Resident / Admin / Security)
- Seamless persona switcher accessible from the top bar to evaluate the application from different perspectives:
  - **Resident (Alex Morgan, Unit A-402)**: Raise complaints, pre-approve visitors, pay maintenance dues.
  - **Society Admin (Rajesh Sharma, Board Member)**: Publish announcements, update complaint statuses, view society metrics.
  - **Security Guard (Suraj Singh, Gate 1)**: Verify entry codes, check-in and check-out visitors at the gate.
- **Storage Inspector & Reset**: View the JSON storage file path, file size in KB, and a 1-tap option to restore sample data.

---

## 💾 Data Architecture & JSON Storage

Per the assignment specification, all application data is stored in a structured JSON file:

- **Storage Location**: `context.filesDir/society_community_data.json`
- **Serialization Engine**: Moshi with Kotlin Reflection & Code-Gen.
- **Thread Safety**: Disk read/write operations execute on `Dispatchers.IO` with reactive `StateFlow` updates back to the UI.
- **State Persistence**: Every ticket submission, status update, gate check-in, payment transaction, or announcement immediately persists to the local JSON file.
- **Factory Reset**: Ability to restore factory sample data at any time from the role switcher sheet.

---

## 🏗 Architecture & Design Principles

The application adopts the **MVVM (Model-View-ViewModel)** and **Repository Pattern** with unidirectional data flow (UDF):

```
+-------------------------------------------------------------+
|                      Jetpack Compose UI                     |
|  (NoticesScreen, ComplaintsScreen, VisitorsScreen,          |
|   PaymentsScreen, DirectoryScreen, Dialogs, TopBar)         |
+------------------------------▲------------------------------+
                               | UI State (StateFlow)
                               | User Events
+------------------------------▼------------------------------+
|                      SocietyViewModel                       |
|   - Holds SocietyUiState                                    |
|   - Manages filter/search queries, dialog visibility        |
|   - Executes business logic & dispatches actions            |
+------------------------------▲------------------------------+
                               | Coroutines & Flows
+------------------------------▼------------------------------+
|                   SocietyJsonRepository                     |
|   - Thread-safe JSON file I/O on Dispatchers.IO             |
|   - Moshi JSON serialization / deserialization              |
|   - Emits reactive SocietyData via StateFlow                |
+------------------------------▲------------------------------+
                               | Read / Write JSON
+------------------------------▼------------------------------+
|           Internal Storage: society_community_data.json     |
+-------------------------------------------------------------+
```

### Design Patterns Used
- **MVVM Pattern**: Separation of UI layer from domain logic and state management.
- **Repository Pattern**: Abstracted JSON storage operations from the UI presentation layer.
- **Single Source of Truth**: UI reacts to `StateFlow<SocietyUiState>` emitted by the ViewModel.
- **Material Design 3**: Modern color palette with semantic tokens, 8dp spacing grid, and 48dp touch targets.
- **Clean Code & Modularity**: Structured packages (`data/model`, `data/repository`, `ui/components`, `ui/screens`, `ui/dialogs`, `ui/theme`).

---

## 🛠 Technology Stack & Libraries

| Technology / Library | Version | Purpose |
|----------------------|---------|---------|
| **Kotlin** | 2.2.10 | Core programming language |
| **Jetpack Compose** | Bom 2024.09.00 | Modern declarative UI toolkit |
| **Material 3** | Latest | Design system, components, and theming |
| **Moshi Kotlin** | 1.15.2 | JSON serialization & deserialization |
| **Coroutines & Flow**| 1.10.2 | Asynchronous operations and reactive streams |
| **Lifecycle ViewModel Compose** | 2.8.7 | Architecture component & state holder |
| **Robolectric** | 4.16.1 | Local JVM testing for critical journeys |
| **Roborazzi** | 1.59.0 | Automated screenshot verification |

---

## 🚀 Setup & Build Instructions

1. **Prerequisites**:
   - Android Studio Ladybug (or newer)
   - JDK 11 or JDK 17
   - Android SDK API 36 (Android 14 / 15 support, minimum SDK 24)

2. **Clone & Open**:
   ```bash
   git clone <repository_url>
   cd <repository_folder>
   ```

3. **Build the Application**:
   ```bash
   gradle assembleDebug
   ```

4. **Run Local Unit Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## 🔄 Application Flow

1. **Home / Dashboard**: Opens with the active user persona displayed in the top bar with pending dues and open ticket count.
2. **Notice Board**: Browse community announcements with category filters and search.
3. **Service Requests**: Browse open/in-progress/resolved maintenance tickets. Residents can raise tickets; Admins can update status and add resolution remarks.
4. **Visitor Passes**: Pre-approve visitors and generate 6-digit entry codes; Security can check visitors in and out with real-time timestamps.
5. **Maintenance Dues**: Inspect pending dues, review itemized charges, execute simulated payments (UPI/Card), and view digital receipts.
6. **Emergency Directory**: Access contact numbers for security, management, and emergency services with instant calling and clipboard copy.
7. **Switch Roles**: Tap the role badge in the top bar to switch between Resident, Admin, and Security perspectives instantly.

---

## 📌 Known Limitations & Future Improvements

- **Known Limitations**:
  - Push notifications are simulated in-app via Snackbars rather than FCM (Firebase Cloud Messaging).
  - Telephony dialer opens system dialer; actual phone calling requires cellular SIM hardware.
- **Future Improvements**:
  - Clubhouse and sports facility time-slot reservation system.
  - Community polls and voting for AGM resolutions.
  - Buy/Sell/Rent community marketplace section for residents.
  - Biometric authentication (fingerprint / face unlock) for resident gate pass approval.
