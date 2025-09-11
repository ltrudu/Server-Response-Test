# Development Setup

This guide provides detailed instructions for setting up a development environment for Server Response Test, including tools, configuration, and best practices.

## 🛠️ Prerequisites

### Required Software

#### Android Studio
- **Version**: Arctic Fox (2020.3.1) or later
- **Recommended**: Latest stable version
- **Download**: [developer.android.com/studio](https://developer.android.com/studio)
- **System Requirements**:
  - 8 GB RAM minimum (16 GB recommended)
  - 4 GB available disk space
  - 1280x800 minimum screen resolution

#### Java Development Kit (JDK)
- **Version**: JDK 8 or higher
- **Recommended**: JDK 11 (Android Studio bundled)
- **Alternatives**: Oracle JDK, OpenJDK, AdoptOpenJDK

#### Version Control
- **Git**: Latest version
- **GitHub Account**: For contributing
- **Git GUI** (optional): SourceTree, GitKraken, or built-in Android Studio

### System Requirements

#### Minimum Specifications
```
OS: Windows 10/11, macOS 10.14+, Ubuntu 18.04+
RAM: 8 GB
Storage: 10 GB free space
Network: Stable internet connection
```

#### Recommended Specifications
```
OS: Latest stable versions
RAM: 16 GB or more
Storage: SSD with 20+ GB free space
Network: High-speed internet for SDK downloads
```

## 🔧 Installation Guide

### Step 1: Install Android Studio

#### Windows Installation
```bash
# Download installer from official site
# Run android-studio-xxx-windows.exe
# Follow installation wizard
# Accept Android SDK license agreements
```

#### macOS Installation
```bash
# Download DMG file
# Mount DMG and drag Android Studio to Applications
# Launch from Applications folder
# Complete setup wizard
```

#### Linux Installation
```bash
# Download tar.gz archive
tar -xzf android-studio-xxx-linux.tar.gz
sudo mv android-studio /opt/
cd /opt/android-studio/bin
./studio.sh
```

### Step 2: Configure Android SDK

#### SDK Components Setup
```
Required SDK Components:
├── Android SDK Platform-Tools
├── Android SDK Build-Tools 34.0.0
├── Android 14 (API 34) - Target SDK
├── Android 7.0 (API 24) - Minimum SDK
├── Android SDK Command-line Tools
└── Google Play services

Optional Components:
├── Android Emulator
├── Intel x86 Emulator Accelerator (HAXM)
├── Google APIs Intel x86 Atom System Images
└── Android Auto Desktop Head Unit emulator
```

#### SDK Manager Configuration
1. **Open SDK Manager**: Tools > SDK Manager
2. **SDK Platforms tab**:
   - ✅ Android 14 (API 34)
   - ✅ Android 7.0 (API 24)
   - ✅ Any intermediate versions you want to test

3. **SDK Tools tab**:
   - ✅ Android SDK Build-Tools 34.0.0
   - ✅ Android SDK Platform-Tools
   - ✅ Android SDK Command-line Tools
   - ✅ Android Emulator (if using emulator)

### Step 3: Project Setup

#### Clone Repository
```bash
# Fork the repository on GitHub first
git clone https://github.com/YOUR_USERNAME/Server-Response-Test.git
cd Server-Response-Test

# Add upstream remote
git remote add upstream https://github.com/ltrudu/Server-Response-Test.git

# Verify remotes
git remote -v
```

#### Import Project
1. **Open Android Studio**
2. **Import Project**: File > Open
3. **Select Directory**: Navigate to cloned repository
4. **Import Type**: Select "Import project from external model" > Gradle
5. **Gradle Sync**: Wait for automatic sync to complete

#### Initial Configuration
```bash
# Create local.properties (if not exists)
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# For Windows (example path)
echo "sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk" > local.properties

# For macOS (example path)
echo "sdk.dir=/Users/YourName/Library/Android/sdk" > local.properties

# For Linux (example path)
echo "sdk.dir=/home/YourName/Android/Sdk" > local.properties
```

## 📱 Device Configuration

### Physical Device Setup

#### Enable Developer Options
```
Steps for most Android devices:
1. Settings > About phone
2. Tap "Build number" 7 times
3. Return to Settings
4. Developer options should now be visible
```

#### Enable USB Debugging
```
1. Settings > Developer options
2. Enable "USB debugging"
3. Enable "Stay awake" (optional, for development)
4. Enable "OEM unlocking" (optional, for advanced testing)
```

#### Connect Device
```bash
# Verify device connection
adb devices

# Should show your device:
# List of devices attached
# abc123def456    device
```

### Android Virtual Device (AVD) Setup

#### Create AVD
1. **Open AVD Manager**: Tools > AVD Manager
2. **Create Virtual Device**: Click "Create Virtual Device"
3. **Select Hardware**: Choose device (Pixel 4 recommended)
4. **Select System Image**: 
   - API 24 (Android 7.0) for minimum testing
   - API 34 (Android 14) for target testing
5. **Configure AVD**: Name it appropriately
6. **Finish**: Create the virtual device

#### Recommended AVD Configurations
```
Development AVD:
├── Device: Pixel 4
├── API Level: 34 (Android 14)
├── RAM: 4 GB
├── Storage: 8 GB
└── Hardware: Enable hardware acceleration

Testing AVD (Minimum):
├── Device: Pixel 3a
├── API Level: 24 (Android 7.0)
├── RAM: 2 GB
├── Storage: 4 GB
└── Hardware: Basic configuration
```

## 🔨 Build Configuration

### Gradle Setup

#### Build Variants
```gradle
// Available build variants
buildTypes {
    debug {
        debuggable true
        minifyEnabled false
        applicationIdSuffix ".debug"
    }
    
    release {
        debuggable false
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

#### Dependencies Management
```gradle
// Check build.gradle files for current dependencies
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.room:room-runtime:2.5.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.2'
    // ... other dependencies
}
```

### Build Commands

#### Command Line Build
```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing configuration)
./gradlew assembleRelease

# Clean build
./gradlew clean assembleDebug

# Install on connected device
./gradlew installDebug
```

#### Android Studio Build
```
Build menu options:
├── Make Project (Ctrl+F9): Compile changes
├── Rebuild Project: Clean and build all
├── Build APK(s): Generate APK files
└── Generate Signed Bundle/APK: Release builds
```

## 🧪 Testing Environment

### Unit Testing Setup

#### Test Dependencies
```gradle
testImplementation 'junit:junit:4.13.2'
testImplementation 'androidx.room:room-testing:2.5.0'
testImplementation 'androidx.arch.core:core-testing:2.2.0'
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
```

#### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Specific test class
./gradlew test --tests="com.ltrudu.serverresponsetest.ServerRepositoryTest"
```

### Database Testing

#### In-Memory Database
```java
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private TestDatabase db;
    
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, TestDatabase.class).build();
    }
    
    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
```

## 🔧 Development Tools

### Recommended Android Studio Plugins

#### Essential Plugins
```
Pre-installed (verify enabled):
├── Android Support
├── Gradle
├── Git Integration
├── Terminal
└── Version Control

Recommended additions:
├── .ignore: Gitignore file management
├── Rainbow Brackets: Code readability
├── Material Theme UI: Enhanced interface
└── SonarLint: Code quality analysis
```

### Code Quality Tools

#### Built-in Analysis
```bash
# Lint analysis
./gradlew lint

# Lint with fix suggestions
./gradlew lintDebug

# Generate lint report
./gradlew lintDebug --continue
```

#### External Tools
```
Code quality options:
├── SonarQube: Comprehensive analysis
├── Checkstyle: Java style checking
├── PMD: Source code analyzer
└── SpotBugs: Bug pattern detection
```

## 📋 Development Workflow

### Branch Strategy

#### Git Workflow
```bash
# Start new feature
git checkout -b feature/your-feature-name

# Regular commits
git add .
git commit -m "descriptive commit message"

# Keep branch updated
git fetch upstream
git rebase upstream/master

# Push for review
git push origin feature/your-feature-name
```

#### Branch Naming Convention
```
Branch types:
├── feature/feature-name: New features
├── bugfix/issue-description: Bug fixes
├── hotfix/critical-fix: Critical fixes
├── refactor/component-name: Code refactoring
└── docs/documentation-update: Documentation
```

### Code Style Guidelines

#### Java Style
```java
// Class naming: PascalCase
public class ServerTestService extends Service {
    
    // Constants: UPPER_SNAKE_CASE
    private static final int NOTIFICATION_ID = 1001;
    
    // Variables: camelCase
    private boolean isTestRunning = false;
    
    // Methods: camelCase
    public void startTesting() {
        // Implementation
    }
}
```

#### Resource Naming
```xml
<!-- Layouts: activity/fragment/item prefix -->
activity_main.xml
fragment_test.xml
item_server_result.xml

<!-- Strings: descriptive with context -->
<string name="test_start_button">Start Test</string>
<string name="error_network_unavailable">Network unavailable</string>

<!-- IDs: component_purpose -->
<TextView
    android:id="@+id/text_server_name"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

### Debugging Setup

#### Debug Configuration
```
Debug build features:
├── Debug symbols: Enabled
├── Logging: Verbose
├── Network security: Permissive
├── Debugger attachment: Enabled
└── Performance monitoring: Available
```

#### Debugging Tools
```
Android Studio debugging:
├── Breakpoints: Line and conditional
├── Variable inspection: Real-time values
├── Call stack: Method trace
├── Logcat: System and app logs
└── Memory profiler: Memory usage analysis
```

## 🚀 Performance Development

### Profiling Tools

#### Android Studio Profiler
```
Profiling capabilities:
├── CPU Profiler: Method tracing
├── Memory Profiler: Heap analysis
├── Network Profiler: Request monitoring
└── Energy Profiler: Battery usage
```

#### Usage Instructions
```
1. Run app in debug mode
2. Open Profiler: View > Tool Windows > Profiler
3. Select device and process
4. Choose profiling type
5. Analyze performance data
```

### Memory Management

#### Memory Leak Detection
```java
// Avoid memory leaks
public class MyFragment extends Fragment {
    private Handler handler;
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
```

## 📚 Documentation Development

### Code Documentation

#### JavaDoc Standards
```java
/**
 * Tests server connectivity using HTTPS requests
 * 
 * @param server The server configuration to test
 * @param timeout Connection timeout in milliseconds
 * @return TestResult containing response time and status
 * @throws NetworkException if connection fails
 */
public TestResult testHttpServer(Server server, int timeout) throws NetworkException {
    // Implementation
}
```

### API Documentation

#### Documentation Generation
```bash
# Generate JavaDoc
./gradlew generateDebugJavadoc

# Output location
# build/docs/javadoc/debug/
```

## 🔒 Security Considerations

### Development Security

#### API Keys and Secrets
```properties
# Never commit secrets to git
# Use local.properties for sensitive data
api.key=your_api_key_here
secret.token=your_secret_token

# Add to .gitignore
local.properties
*.keystore
secrets/
```

#### Network Security
```xml
<!-- For development only -->
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="false">
```

---

## 🆘 Troubleshooting Development Issues

### Common Setup Problems

#### Gradle Sync Failures
```bash
# Clear Gradle cache
./gradlew clean
rm -rf ~/.gradle/caches/

# Invalidate Android Studio caches
# File > Invalidate Caches and Restart
```

#### SDK Issues
```bash
# Update SDK components
# Tools > SDK Manager > Install updates

# Reset SDK path
# File > Project Structure > SDK Location
```

#### Device Connection Issues
```bash
# Restart ADB
adb kill-server
adb start-server

# Check device authorization
adb devices

# Reinstall USB drivers (Windows)
# Update device drivers in Device Manager
```

---

This development setup guide provides everything needed to start contributing to Server Response Test. For specific development questions, refer to the [Contributing Guide](Contributing.md) or reach out via [GitHub Discussions](https://github.com/ltrudu/Server-Response-Test/discussions).