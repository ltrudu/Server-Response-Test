# Installation Guide

This guide provides step-by-step instructions for installing and setting up the Server Response Test application on your Android device and development environment.

## 📱 End User Installation

### Option 1: Pre-built APK (Recommended)
1. **Download the APK**
   - Navigate to the [Releases](https://github.com/ltrudu/Server-Response-Test/releases) page
   - Download the latest `ServerResponseTest-vX.X.apk` file

2. **Enable Unknown Sources**
   - Go to **Settings > Security** (or **Settings > Apps & notifications > Special app access**)
   - Enable **Install unknown apps** for your file manager/browser
   - Or enable **Unknown sources** on older Android versions

3. **Install the APK**
   - Open the downloaded APK file
   - Tap **Install** when prompted
   - Grant any requested permissions

4. **Launch the App**
   - Find "Server Response Test" in your app drawer
   - Tap to open and start using

### Option 2: Google Play Store
*Note: This app is currently not available on Google Play Store. Please use the APK installation method.*

## 🛠️ Developer Installation

### Prerequisites
Before setting up the development environment, ensure you have:

- **Android Studio**: Arctic Fox (2020.3.1) or later
- **Java Development Kit (JDK)**: Version 8 or higher
- **Android SDK**: API Level 24 (Android 7.0) or higher
- **Git**: For version control
- **Minimum System Requirements**:
  - 8 GB RAM (16 GB recommended)
  - 4 GB available disk space
  - 1280x800 minimum screen resolution

### Step 1: Install Android Studio

#### Windows
1. Download Android Studio from [developer.android.com](https://developer.android.com/studio)
2. Run the installer executable
3. Follow the setup wizard
4. Install Android SDK and tools when prompted

#### macOS
1. Download the DMG file
2. Drag Android Studio to Applications
3. Launch and complete setup wizard

#### Linux
1. Download the tar.gz archive
2. Extract to `/opt/android-studio`
3. Run `./studio.sh` from the `bin/` directory

### Step 2: Configure Android SDK

1. **Open SDK Manager** (Tools > SDK Manager)
2. **Install required components**:
   - Android 7.0 (API 24) - Minimum supported version
   - Android 14 (API 34) - Target version
   - Android SDK Build-Tools 34.0.0
   - Android SDK Platform-Tools
   - Android SDK Tools

3. **Create Virtual Device** (optional for testing):
   - Open AVD Manager (Tools > AVD Manager)
   - Create new virtual device
   - Select API level 24 or higher

### Step 3: Clone and Import Project

1. **Clone the Repository**
   ```bash
   git clone https://github.com/ltrudu/Server-Response-Test.git
   cd Server-Response-Test
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory
   - Select the root folder and click "OK"

3. **Sync Project**
   - Wait for Android Studio to index files
   - Click "Sync Now" if prompted for Gradle sync
   - Wait for dependencies to download

### Step 4: Build and Run

1. **Select Build Variant**
   - Open "Build Variants" panel (View > Tool Windows > Build Variants)
   - Select "debug" for development

2. **Build Project**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio menu: Build > Make Project

3. **Run on Device/Emulator**
   - Connect Android device via USB (with USB debugging enabled)
   - Or start an Android Virtual Device (AVD)
   - Click "Run" button (▶️) or press Shift+F10

## 🔧 Configuration

### Device Requirements

#### Minimum Requirements
- **Android Version**: 7.0 (API level 24)
- **RAM**: 2 GB
- **Storage**: 50 MB free space
- **Network**: Internet connection for testing

#### Recommended Specifications
- **Android Version**: 10.0+ (API level 29+)
- **RAM**: 4 GB or more
- **Storage**: 100 MB free space
- **Network**: Wi-Fi and mobile data capability

### Permissions Setup

The app requires the following permissions:

#### Automatically Granted
- **INTERNET**: Network access for testing
- **ACCESS_NETWORK_STATE**: Network connectivity checks
- **FOREGROUND_SERVICE**: Background testing capability
- **FOREGROUND_SERVICE_DATA_SYNC**: Service type specification
- **WAKE_LOCK**: Reliable background operation

#### Runtime Permissions (Android 13+)
- **POST_NOTIFICATIONS**: For background test notifications
  - App will prompt automatically
  - Can be managed in Settings tab

### Initial Setup

1. **First Launch**
   - Open the app
   - Grant notification permission when prompted
   - Navigate through the three tabs to familiarize yourself

2. **Add Your First Server**
   - Go to "Server List" tab
   - Tap the ➕ button
   - Enter server details:
     ```
     Name: Test Server
     Address: https://google.com
     Port: (leave empty)
     Type: HTTPS
     ```

3. **Configure Settings**
   - Go to "Settings" tab
   - Set time between sessions: 5000ms (default)
   - Choose infinite or finite requests
   - Ensure notification status shows green

4. **Run First Test**
   - Go to "Test" tab
   - Tap ▶️ "Start Test"
   - Watch real-time results

## 🔍 Troubleshooting Installation

### Common Issues

#### "App not installed" Error
**Causes**: 
- Insufficient storage space
- Corrupted APK file
- Previous installation conflict

**Solutions**:
1. Free up storage space (need 50MB+)
2. Re-download APK file
3. Uninstall any previous versions
4. Clear download cache and retry

#### "Parse Error" on APK Install
**Causes**:
- Incompatible Android version
- Corrupted download
- Wrong architecture

**Solutions**:
1. Verify Android version (7.0+ required)
2. Re-download APK from official source
3. Check device architecture compatibility

#### Gradle Sync Failures (Development)
**Causes**:
- Network connectivity issues
- Outdated Android Studio
- Missing SDK components

**Solutions**:
1. Check internet connection
2. Update Android Studio to latest version
3. Install missing SDK components via SDK Manager
4. Try offline mode: File > Settings > Build > Gradle > Work offline

#### Build Errors
**Common fixes**:
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug

# Invalidate caches
# File > Invalidate Caches and Restart
```

### Permission Issues

#### Notification Permission Denied
- Open Settings app
- Navigate to Apps > Server Response Test > Notifications
- Enable "Allow notifications"
- Restart the app

#### Unknown Sources Blocked
- Settings > Security > Install unknown apps
- Find your file manager/browser
- Enable "Allow from this source"

### Development Environment Issues

#### Java Version Conflicts
```bash
# Check Java version
java -version

# Set JAVA_HOME (adjust path as needed)
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

#### Android SDK Path Issues
1. Open Android Studio preferences
2. Go to Appearance & Behavior > System Settings > Android SDK
3. Verify SDK path is correct
4. Update project's `local.properties` if needed

## 📊 Verification

### Installation Success Checklist

#### For End Users
- [ ] App appears in app drawer
- [ ] App launches without errors
- [ ] All three tabs are accessible
- [ ] Can add a test server
- [ ] Notification permission can be granted
- [ ] Test can be started and stopped

#### For Developers
- [ ] Project imports without errors
- [ ] Gradle sync completes successfully
- [ ] App builds without warnings
- [ ] Can run on device/emulator
- [ ] All features work as expected
- [ ] Tests can be executed

### Performance Verification
- App startup time: < 3 seconds
- Memory usage: < 100 MB during normal operation
- Battery usage: Minimal when not actively testing
- Network usage: Only during actual testing

## 🔄 Updates and Maintenance

### Updating the App (End Users)
1. Check [Releases](https://github.com/ltrudu/Server-Response-Test/releases) page periodically
2. Download newer APK when available
3. Install over existing version (settings preserved)
4. Review changelog for new features

### Keeping Development Environment Updated
1. Update Android Studio regularly
2. Update Android SDK components
3. Pull latest changes from repository:
   ```bash
   git pull origin master
   ```
4. Sync Gradle dependencies

## 📞 Getting Help

If you encounter issues during installation:

1. **Check the [FAQ](FAQ.md)** for common questions
2. **Review [Troubleshooting Guide](Troubleshooting.md)** for detailed solutions
3. **Search [GitHub Issues](https://github.com/ltrudu/Server-Response-Test/issues)** for similar problems
4. **Create a new issue** with:
   - Device information (model, Android version)
   - Installation method used
   - Complete error messages
   - Steps to reproduce the problem

---

**Welcome to Server Response Test!** 🚀

Once installation is complete, check out the [User Guide](User-Guide.md) to learn how to use all the app's features effectively.