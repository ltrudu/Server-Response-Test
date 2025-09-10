# Server Load Test Application Documentation

## Overview

The Server Load Test application is an Android application built using Java with ViewModel architecture and design patterns. It provides a comprehensive solution for testing server connectivity through HTTP requests or Ping operations. Version 1.1 introduces enhanced background functionality with persistent notifications, advanced permission management, and precision timing controls.

## Architecture

### Design Patterns Used
- **MVVM (Model-View-ViewModel)**: Separates business logic from UI components
- **Repository Pattern**: Manages data operations and provides a clean API for data access
- **Observer Pattern**: Uses LiveData for reactive programming
- **Singleton Pattern**: Used in Database and Repository classes

### Key Components

#### Data Layer
- **Server Entity**: Represents server information with fields for name, address, port, and request type
- **ServerDao**: Data Access Object for database operations
- **AppDatabase**: Room database implementation with type converters
- **Converters**: Type converters for enum serialization

#### Repository Layer
- **ServerRepository**: Manages data operations and provides a clean API for ViewModels

#### ViewModel Layer
- **ServerViewModel**: Handles business logic and provides data to UI components

#### UI Layer
- **MainActivity**: Main activity with ViewPager2 and TabLayout
- **TestFragment**: Displays server test results and controls test execution
- **ServerListFragment**: Manages CRUD operations for servers
- **SettingsFragment**: Handles app configuration and data import/export

#### Service Layer
- **ServerTestService**: Background foreground service for HTTP/Ping testing

### Fragments and Tabs

#### 1. Test Tab (`TestFragment`)
- Displays the number of configured servers
- Play/Stop button to control test execution
- Real-time display of server test results with status icons
- Shows response times and error states
- **NEW in v1.1**: Remaining requests counter for finite test mode
- **NEW in v1.1**: Real-time progress updates during testing
- **NEW in v1.1**: Notification permission checks before starting tests

**Key Functions:**
- `startTest()`: Initiates server testing with background service and permission checks
- `stopTest()`: Stops ongoing tests
- `handleTestResult()`: Processes broadcast results from service
- `handleRequestProgress()`: **NEW** - Updates remaining request counter in real-time
- `updateRemainingRequestsDisplay()`: **NEW** - Shows progress for finite test mode
- `updateUI()`: Updates play/stop button states

#### 2. Server List Tab (`ServerListFragment`)
- Lists all configured servers in a RecyclerView
- Add button (+) to create new server entries
- Tap to edit server configuration
- Swipe left to delete servers with confirmation dialog

**Key Functions:**
- `showAddEditServerDialog()`: Displays server configuration dialog
- `validateInput()`: Validates server configuration data
- `saveServer()`: Persists server data to database
- `showDeleteConfirmationDialog()`: Confirms server deletion

**Server Configuration:**
- Server Name (required)
- Server Address (HTTP URL or IP address, required)
- Port (optional)
- Request Type (HTTP or Ping)

#### 3. Settings Tab (`SettingsFragment`)
- **UPDATED in v1.1**: Time between sessions configuration (now in milliseconds for precision)
- Infinite requests toggle
- Number of requests (when not infinite)
- Export/Import/Share functionality for server configurations
- **NEW in v1.1**: Notification permission status indicator with one-tap fix
- **NEW in v1.1**: Visual status display (green/orange) for notification state

**Key Functions:**
- `saveSettings()`: Persists settings to database with auto-save functionality
- `exportServers()`: Exports server list and settings to JSON file
- `importServersFromUri()`: **IMPROVED** - Enhanced import with proper synchronization
- `shareServers()`: Creates shareable JSON file
- `updateNotificationStatus()`: **NEW** - Updates notification permission display
- `handleNotificationStatusClick()`: **NEW** - Opens permission settings when needed

### Background Service

#### ServerTestService
**ENHANCED in v1.1**: Advanced foreground service with rich notifications and user controls.

**Key Features:**
- Concurrent testing of multiple servers
- **UPDATED**: Configurable delays in milliseconds for precision timing
- Support for both infinite and limited request cycles
- Real-time result broadcasting to UI
- **NEW**: Rich persistent notifications with current status
- **NEW**: Notification action buttons (Pause/Resume/Stop)
- **NEW**: Real-time progress updates in notification
- **NEW**: Pause/Resume functionality without stopping tests
- **NEW**: Request progress broadcasting for finite mode

**Testing Methods:**
- `testHttpServer()`: Performs HTTP GET requests with timeout handling
- `testPingServer()`: Uses InetAddress.isReachable() for ping testing

**NEW Notification Features:**
- `updateNotification()`: Updates notification with current test status
- `pauseTesting()`: Pauses tests without stopping the service
- `resumeTesting()`: Resumes paused tests
- `broadcastRequestProgress()`: Sends progress updates for remaining requests

### Database Schema

#### Server Table
```sql
CREATE TABLE servers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    port INTEGER,
    requestType TEXT NOT NULL
);
```

#### Settings Table *(NEW in v1.1)*
```sql
CREATE TABLE settings (
    id INTEGER PRIMARY KEY,
    time_between_requests INTEGER NOT NULL DEFAULT 5000,  -- Now in milliseconds
    request_delay_ms INTEGER NOT NULL DEFAULT 100,
    random_min_delay_ms INTEGER NOT NULL DEFAULT 50,
    random_max_delay_ms INTEGER NOT NULL DEFAULT 100,
    infinite_requests INTEGER NOT NULL DEFAULT 1,
    number_of_requests INTEGER NOT NULL DEFAULT 10
);
```

### Data Flow

1. User configures servers in ServerListFragment
2. Data is persisted via ServerRepository to Room database
3. Settings are stored in SharedPreferences
4. Test execution is controlled through TestFragment
5. ServerTestService performs background testing
6. Results are broadcast back to TestFragment for display

### Material Design 3

The application implements Material Design 3 principles with:
- Dynamic color theming (light/dark modes)
- Material components (Cards, Buttons, TextInputLayouts)
- Proper elevation and shadows
- Consistent spacing and typography

### Localization

The application supports:
- English (default)
- French (fr) translation

All user-facing strings are externalized in strings.xml files.

### Import/Export Functionality

The application supports JSON-based data exchange:
- Export server configurations to external storage
- Import configurations from JSON files
- Share configurations via Android's sharing system
- Validation and error handling for corrupted data

### Permissions

Required permissions:
- `INTERNET`: For HTTP requests
- `ACCESS_NETWORK_STATE`: For network connectivity checks
- `FOREGROUND_SERVICE`: For background test execution
- `FOREGROUND_SERVICE_DATA_SYNC`: Specific foreground service type
- **NEW in v1.1**: `POST_NOTIFICATIONS`: For Android 13+ notification permission
- **NEW in v1.1**: `WAKE_LOCK`: For reliable background operation

### NEW in v1.1: Notification Permission Management

#### MainActivity Permission Handling
- **Automatic Detection**: Checks notification status on app start and resume
- **Version-Specific Logic**: Different handling for Android 8+ vs Android 13+
- **User-Friendly Dialogs**: Educational messages explaining why permissions are needed
- **Graceful Fallbacks**: Tests can run without notifications if user declines

**Key Functions:**
- `checkNotificationPermission()`: Detects current permission state
- `requestNotificationPermission()`: Handles permission request flow
- `showNotificationRationaleDialog()`: Explains permission benefits
- `openAppNotificationSettings()`: Direct access to system settings
- `areNotificationsEnabled()`: Cross-version compatibility check

### Error Handling

The application includes comprehensive error handling:
- Input validation with user-friendly error messages
- Network error handling with timeout configurations
- Database operation error handling
- File I/O error handling for import/export operations
- **IMPROVED in v1.1**: Enhanced import synchronization with timeout protection
- **NEW in v1.1**: Permission denial handling with alternative workflows

### Testing Workflow

1. User adds servers in the Server List tab
2. User configures test parameters in Settings tab
3. User initiates testing in Test tab
4. Background service performs tests according to configuration
5. Results are displayed in real-time with visual indicators
6. User can stop tests at any time

### Performance Considerations

- Background service uses thread pool for concurrent operations
- RecyclerView with DiffUtil for efficient list updates
- Room database with background thread operations
- Proper lifecycle management to prevent memory leaks
- **IMPROVED in v1.1**: Enhanced synchronization for reliable import operations
- **NEW in v1.1**: Optimized notification updates to minimize battery usage

## Version 1.1 New Features Summary

### 📱 Background Notifications
- **Rich Notifications**: Show current server being tested and progress
- **Action Buttons**: Pause, Resume, and Stop directly from notification
- **Persistent Display**: Continues showing status even when app is closed
- **Battery Optimized**: Low-priority notifications with minimal resource usage

### 🔔 Smart Permission Management  
- **Cross-Android Support**: Handles Android 8+ and 13+ permission models
- **Educational Dialogs**: Explains benefits of allowing notifications
- **Settings Integration**: Visual status indicator with one-tap access to fix issues
- **Graceful Degradation**: Tests work without notifications if user prefers

### ⏱️ Millisecond Precision Timing
- **Enhanced Control**: Time between sessions now configurable in milliseconds  
- **Default Preservation**: 5000ms default maintains existing 5-second behavior
- **Fine-Grained Testing**: Allows precise intervals like 1500ms, 2750ms
- **Backward Compatible**: Existing settings automatically migrated

### 📊 Request Progress Tracking
- **Visual Counter**: Shows "Remaining requests: X/Y" for finite test mode
- **Real-Time Updates**: Counter decreases after each completed cycle
- **Mode Indicators**: Clear display for infinite vs finite testing
- **Localized Display**: Full French translation support

### 🐛 Reliability Improvements
- **Import Synchronization**: Fixed race condition where success toast appeared too early
- **Timeout Protection**: 5-second timeout prevents infinite waits during import
- **Debug Logging**: Comprehensive logging for troubleshooting
- **Data Integrity**: Ensures all operations complete before showing success

### 🌍 Enhanced Localization
- **Complete French Support**: All new features fully translated
- **Permission Dialogs**: Localized notification permission explanations  
- **Status Messages**: French translations for all new UI elements
- **Consistent Experience**: Maintains language consistency across all features