# Server Load Test Application Documentation

## Overview

The Server Load Test application is an Android application built using Java with ViewModel architecture and design patterns. It provides a comprehensive solution for testing server connectivity through HTTP requests or Ping operations.

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

**Key Functions:**
- `startTest()`: Initiates server testing with background service
- `stopTest()`: Stops ongoing tests
- `handleTestResult()`: Processes broadcast results from service
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
- Time between requests configuration (seconds)
- Infinite requests toggle
- Number of requests (when not infinite)
- Export/Import/Share functionality for server configurations

**Key Functions:**
- `saveSettings()`: Persists settings to SharedPreferences
- `exportServers()`: Exports server list to JSON file
- `importServers()`: Imports servers from JSON file
- `shareServers()`: Creates shareable JSON file

### Background Service

#### ServerTestService
Foreground service that performs HTTP requests and ping operations in the background.

**Key Features:**
- Concurrent testing of multiple servers
- Configurable delays between requests
- Support for both infinite and limited request cycles
- Real-time result broadcasting to UI
- Proper foreground service implementation with notifications

**Testing Methods:**
- `testHttpServer()`: Performs HTTP GET requests with timeout handling
- `testPingServer()`: Uses InetAddress.isReachable() for ping testing

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

### Error Handling

The application includes comprehensive error handling:
- Input validation with user-friendly error messages
- Network error handling with timeout configurations
- Database operation error handling
- File I/O error handling for import/export operations

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