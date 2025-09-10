# ChangeLog

## Version 1.1 - 2025-09-10

### 🆕 New Features

#### Background Service Notifications
- **Persistent Notification**: Added foreground service with rich notifications showing real-time test status
- **Notification Controls**: Added pause/resume and stop buttons directly in the notification
- **Custom Icons**: Created dedicated notification icons for better visual identification
- **Status Updates**: Real-time display of current server being tested and request progress

#### Notification Permission Management
- **Smart Permission Handling**: Automatic detection and request for notification permissions
- **Guided Setup**: User-friendly dialogs explaining why notifications are needed
- **Settings Integration**: Added notification status indicator in Settings tab with one-tap fix
- **Version-Specific Logic**: Different handling for Android 8+, 13+ permission models
- **French Localization**: Complete translation of all permission dialogs and messages

#### Request Progress Tracking
- **Remaining Requests Counter**: Visual indicator showing remaining requests when finite mode is active
- **Real-time Updates**: Counter updates automatically after each test cycle completion
- **Infinite Mode Indicator**: Clear display when running in infinite request mode
- **Localized Display**: Support for both English and French languages

#### Timing Precision Improvements
- **Millisecond Timing**: Changed time between sessions from seconds to milliseconds for precise control
- **Default Value**: Set default to 5000ms (5 seconds) maintaining existing behavior
- **Granular Control**: Users can now set precise timing like 1500ms, 2750ms, etc.
- **UI Updates**: Updated all labels and hints to reflect millisecond units

### 🐛 Bug Fixes

#### Import Synchronization
- **Race Condition Fix**: Fixed issue where import success toast appeared before data was actually updated
- **Proper Synchronization**: Added callback-based waiting for database operations to complete
- **Timeout Protection**: Added 5-second timeout to prevent infinite waits
- **Debug Logging**: Added comprehensive logging for troubleshooting import issues

### 🔧 Improvements

#### User Experience
- **Consistent Translations**: All new features fully localized in French
- **Visual Feedback**: Better status indicators and progress displays
- **Background Operation**: Tests continue running even when app is closed or in background
- **Easy Control**: Manage tests directly from notification bar without opening app

#### Technical Enhancements
- **Service Reliability**: Enhanced foreground service with proper lifecycle management
- **Permission Workflow**: Streamlined notification permission request process
- **Data Persistence**: Improved import/export reliability with proper synchronization
- **Code Organization**: Better separation of concerns and modular architecture

### 📱 Compatibility
- **Android 8+**: Full support for notification channels and foreground services
- **Android 13+**: Proper handling of runtime notification permissions
- **French Localization**: Complete UI translation for French-speaking users
- **Background Execution**: Works with modern Android background restrictions

### 🛠️ Developer Notes
- Added new broadcast actions for request progress updates
- Enhanced service with pause/resume functionality
- Implemented callback-based database operation synchronization
- Created reusable notification permission management system
- Updated timing logic throughout codebase for millisecond precision

---

**Installation**: Replace your existing APK with the new version. All settings and server configurations will be preserved.

**Migration**: Existing time between sessions values will be automatically converted from seconds to milliseconds (e.g., 5 seconds becomes 5000 milliseconds).