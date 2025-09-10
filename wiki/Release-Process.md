# Release Process

This document outlines the complete release process for Server Response Test, including version management, testing procedures, build processes, and distribution.

## 🏷️ Version Management

### Semantic Versioning

#### Version Format
```
MAJOR.MINOR.PATCH (e.g., 1.1.0)

MAJOR: Breaking changes or major feature overhauls
MINOR: New features, backwards compatible
PATCH: Bug fixes and minor improvements
```

#### Version Examples
```
Version History:
├── 1.0.0: Initial release
├── 1.1.0: Background notifications, permission management, timing precision
├── 1.1.1: Bug fixes for import synchronization
└── 1.2.0: (Future) Historical data tracking, server groups
```

#### Version Code Management
```gradle
android {
    defaultConfig {
        versionCode 2        // Integer, increments with each release
        versionName "1.1.0"  // String, semantic version
    }
}

Version code calculation:
- Major * 10000 + Minor * 100 + Patch
- Example: 1.1.0 = 1*10000 + 1*100 + 0 = 10100
```

### Branch Strategy

#### Release Branches
```
Branch structure:
├── master: Stable release code
├── develop: Development integration
├── feature/*: Feature development
├── release/*: Release preparation
├── hotfix/*: Critical fixes
└── docs/*: Documentation updates

Release flow:
1. feature/* → develop
2. develop → release/vX.X.X
3. release/vX.X.X → master (after testing)
4. master → tagged release
```

## 📋 Pre-Release Checklist

### Code Quality Verification

#### Automated Checks
```bash
# Run all tests
./gradlew test
./gradlew connectedAndroidTest

# Code quality analysis
./gradlew lint
./gradlew checkstyle (if configured)

# Build verification
./gradlew assembleDebug
./gradlew assembleRelease

# Security scan
./gradlew dependencyCheckAnalyze (if configured)
```

#### Manual Code Review
```
Review checklist:
├── Code follows style guidelines
├── No TODOs or debug code in release
├── All features properly documented
├── Security considerations addressed
├── Performance impact assessed
├── Error handling comprehensive
└── Unit tests cover new functionality
```

### Feature Verification

#### Feature Completeness Check
```
Version 1.1.0 features verification:
├── ✅ Background service notifications
├── ✅ Notification permission management
├── ✅ Millisecond timing precision
├── ✅ Request progress tracking
├── ✅ Import synchronization fixes
├── ✅ Complete French localization
└── ✅ All features documented
```

#### Regression Testing
```
Core functionality verification:
├── Server CRUD operations
├── HTTPS and Ping testing
├── Settings persistence
├── Import/export functionality
├── Background operation
├── Notification system
└── Multi-language support
```

### Documentation Updates

#### Required Documentation
```
Pre-release documentation:
├── ChangeLog.md: Complete version history
├── README.md: Updated feature list and screenshots
├── documentation.md: Technical details updated
├── documentation_fr.md: French documentation current
├── Wiki: All pages reflect new features
└── API Reference: Code documentation complete
```

#### Version-Specific Updates
```
For each release:
1. Update version numbers in documentation
2. Add new features to README highlights
3. Update installation instructions if needed
4. Verify all links work correctly
5. Update screenshots if UI changed
6. Review and update FAQ
7. Update troubleshooting guide
```

## 🧪 Release Testing

### Testing Matrix

#### Device Testing
```
Minimum testing devices:
├── Android 7.0 (API 24): Minimum supported
├── Android 10.0 (API 29): Scoped storage
├── Android 13.0 (API 33): Runtime permissions
└── Android 14.0 (API 34): Target version

Device categories:
├── Low-end: 2-4GB RAM, slow CPU
├── Mid-range: 4-6GB RAM, moderate CPU
├── High-end: 8GB+ RAM, fast CPU
└── Tablet: Large screen, landscape orientation
```

#### Feature Testing Scenarios
```
Test scenarios for each device:
├── Fresh installation
├── Upgrade from previous version
├── Background operation (4+ hours)
├── Battery optimization testing
├── Network condition variations
├── Storage limitation scenarios
└── Permission denial/grant flows
```

### Performance Validation

#### Battery Life Testing
```
Battery test configurations:
├── Light usage: 5 servers, 60s intervals, 8+ hours
├── Normal usage: 10 servers, 30s intervals, 6+ hours
├── Heavy usage: 15 servers, 10s intervals, 4+ hours

Acceptance criteria:
├── Light: <5% battery per hour
├── Normal: <10% battery per hour
├── Heavy: <20% battery per hour
```

#### Memory Usage Validation
```
Memory testing:
├── Baseline: <50MB idle
├── Active testing: <100MB with 10 servers
├── Peak usage: <150MB during intensive operations
├── Memory leaks: None detected over 4-hour test
└── Garbage collection: Efficient, no stuttering
```

### Compatibility Testing

#### Android Version Compatibility
```
Version-specific testing:
├── API 24: Basic functionality, no crashes
├── API 26+: Notification channels work correctly
├── API 29+: Scoped storage functions properly
├── API 33+: Runtime notification permission
└── All versions: Core features identical
```

#### Network Environment Testing
```
Network scenarios:
├── Corporate WiFi (with firewalls)
├── Home WiFi (unrestricted)
├── Mobile data (various carriers)
├── Public WiFi (limited/captive portal)
├── VPN connections
└── Poor connectivity (edge conditions)
```

## 🔨 Build Process

### Release Build Configuration

#### Gradle Configuration
```gradle
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.ltrudu.serverresponsetest"
        minSdk 24
        targetSdk 34
        versionCode 10100
        versionName "1.1.0"
    }
    
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            debuggable false
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

#### ProGuard Configuration
```proguard
# Keep Room entities
-keep class com.ltrudu.serverresponsetest.data.** { *; }

# Keep enum values for serialization
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep service classes
-keep class com.ltrudu.serverresponsetest.service.** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
```

### Build Commands

#### Release Build Steps
```bash
# 1. Clean previous builds
./gradlew clean

# 2. Run tests
./gradlew test

# 3. Run lint analysis
./gradlew lint

# 4. Build release APK
./gradlew assembleRelease

# 5. Verify APK
./gradlew assembleRelease --info

# Output location:
# app/build/outputs/apk/release/app-release.apk
```

#### Build Verification
```bash
# Check APK contents
unzip -l app/build/outputs/apk/release/app-release.apk

# Verify signing
jarsigner -verify -verbose -certs app-release.apk

# Check minimum SDK
aapt dump badging app-release.apk | grep sdkVersion

# Verify permissions
aapt dump permissions app-release.apk
```

### Code Signing

#### Release Signing Configuration
```gradle
android {
    signingConfigs {
        release {
            storeFile file('release-keystore.jks')
            storePassword System.getenv("KEYSTORE_PASSWORD")
            keyAlias System.getenv("KEY_ALIAS")
            keyPassword System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

#### Security Considerations
```
Keystore security:
├── Store keystore securely (never commit to git)
├── Use environment variables for passwords
├── Backup keystore in secure location
├── Document key alias and validity period
└── Plan for key rotation if needed

APK verification:
├── Verify APK is properly signed
├── Check that debug symbols are stripped
├── Confirm obfuscation is applied
├── Validate resource optimization
└── Test installation on clean device
```

## 📦 Release Distribution

### GitHub Release Process

#### Creating GitHub Release
```bash
# 1. Tag the release
git tag -a v1.1.0 -m "Version 1.1.0 - Background notifications and timing precision"
git push origin v1.1.0

# 2. Create release on GitHub
# - Go to repository releases page
# - Click "Create a new release"
# - Select tag v1.1.0
# - Add release title: "Version 1.1.0"
# - Add release description (copy from ChangeLog.md)
# - Upload APK file
# - Mark as latest release
```

#### Release Description Template
```markdown
# Version 1.1.0 - Background Notifications & Timing Precision

## 🆕 New Features
- **Background Service Notifications**: Control tests from notification bar
- **Smart Permission Management**: Automated notification permission handling
- **Millisecond Timing Precision**: Fine-grained timing controls (default: 5000ms)
- **Request Progress Tracking**: Visual remaining requests counter
- **Complete French Localization**: All features fully translated

## 🐛 Bug Fixes
- Fixed import synchronization race condition
- Improved notification permission detection
- Enhanced error handling for network timeouts

## 📱 Compatibility
- **Minimum**: Android 7.0 (API 24)
- **Target**: Android 14 (API 34)
- **Recommended**: Android 10+ for best experience

## 📥 Installation
1. Download `ServerResponseTest-v1.1.0.apk`
2. Enable "Install unknown apps" in device settings
3. Install APK file
4. Grant notification permission when prompted

## 🔄 Upgrade Notes
- Settings and server configurations preserved
- Notification permission may need to be granted
- Time intervals automatically converted to milliseconds

## 🔗 Links
- [Complete Changelog](ChangeLog.md)
- [User Guide](wiki/User-Guide.md)
- [Technical Documentation](documentation.md)

**Full APK**: `ServerResponseTest-v1.1.0.apk` (15.2 MB)
```

### Asset Preparation

#### Release Assets
```
Required files for release:
├── ServerResponseTest-v1.1.0.apk (main application)
├── SHA256SUMS (checksum verification)
├── CHANGELOG.md (version history)
└── README.md (updated documentation)

Optional assets:
├── ServerResponseTest-v1.1.0-debug.apk (debug version)
├── mapping.txt (ProGuard mapping file)
└── Screenshots/ (updated UI screenshots)
```

#### Checksum Generation
```bash
# Generate checksums for verification
sha256sum ServerResponseTest-v1.1.0.apk > SHA256SUMS
md5sum ServerResponseTest-v1.1.0.apk > MD5SUMS

# Verify checksums
sha256sum -c SHA256SUMS
```

## 📢 Release Communication

### Release Announcement

#### Internal Communication
```
Development team notification:
├── Version released and tagged
├── GitHub release created with assets
├── Documentation updated
├── Known issues (if any)
└── Next version planning

Stakeholder communication:
├── Feature summary
├── User impact assessment
├── Support considerations
└── Feedback collection plan
```

#### Public Communication
```
Release channels:
├── GitHub release notes
├── README.md updates
├── Social media (if applicable)
└── Email notifications (if mailing list exists)

Key messaging:
├── Highlight major features
├── Emphasize user benefits
├── Provide clear upgrade instructions
├── Include support contact information
└── Thank contributors
```

### User Migration Guide

#### Upgrade Instructions
```markdown
# Upgrading to Version 1.1.0

## Before Upgrading
1. **Export your data**: Settings > Export Servers
2. **Note your settings**: Screenshot or write down current configuration
3. **Check battery optimization**: Ensure app isn't being optimized

## Installation Process
1. Download v1.1.0 APK from GitHub releases
2. Install over existing version (data preserved)
3. Grant notification permission when prompted
4. Verify settings are preserved

## After Upgrade
1. **Check notification status**: Settings tab should show green
2. **Verify timing units**: Values now in milliseconds (5000ms = 5 seconds)
3. **Test background operation**: Start test and minimize app
4. **Review new features**: Progress counter, notification controls

## Troubleshooting
- If import issues occur, use exported data to restore
- If notifications don't work, check permission in Settings tab
- If battery drains quickly, increase time between sessions
```

## 🔄 Post-Release Process

### Release Monitoring

#### User Feedback Collection
```
Feedback channels:
├── GitHub Issues: Bug reports and feature requests
├── GitHub Discussions: General questions and feedback
├── Email: Direct user communication
└── Analytics: Usage patterns (if implemented)

Monitoring priorities:
├── Crash reports and stability issues
├── Performance complaints
├── Feature usage patterns
├── Battery life impact
└── User satisfaction indicators
```

#### Issue Triage
```
Issue classification:
├── Critical: App crashes, data loss, security issues
├── High: Major feature broken, significant performance issues
├── Medium: Minor feature issues, usability problems
├── Low: Enhancement requests, minor UI issues

Response timeline:
├── Critical: 24 hours acknowledgment, fix within 48 hours
├── High: 48 hours acknowledgment, fix within 1 week
├── Medium: 1 week acknowledgment, fix in next release
├── Low: Acknowledge and schedule for future release
```

### Hotfix Process

#### Critical Issue Response
```
Hotfix workflow:
1. Create hotfix branch from master
2. Implement minimal fix
3. Test fix thoroughly
4. Create patch release (e.g., 1.1.1)
5. Fast-track release process
6. Communicate urgency to users

Hotfix criteria:
├── Security vulnerabilities
├── Data corruption issues
├── Critical feature failures
├── Severe performance regressions
└── Legal/compliance issues
```

### Version Planning

#### Next Release Planning
```
Post-release activities:
1. Review user feedback and issues
2. Analyze feature usage data
3. Plan next version features
4. Update development roadmap
5. Schedule next release timeline

Release cadence:
├── Major releases: 6-12 months
├── Minor releases: 2-4 months
├── Patch releases: As needed
└── Hotfixes: Immediate (critical issues)
```

---

## 📊 Release Metrics

### Success Metrics

#### Technical Metrics
```
Release quality indicators:
├── Crash rate: <0.1% of sessions
├── ANR rate: <0.05% of sessions
├── Install success rate: >95%
├── Upgrade success rate: >98%
└── Performance regression: None

User adoption metrics:
├── Download rate
├── Install retention (7-day, 30-day)
├── Feature adoption rate
├── User rating/feedback
└── Support ticket volume
```

#### Process Metrics
```
Release process efficiency:
├── Time from code freeze to release
├── Number of issues found in testing
├── Build success rate
├── Documentation completeness
└── Team satisfaction with process
```

This comprehensive release process ensures consistent, high-quality releases while maintaining user satisfaction and minimizing risks. Regular review and improvement of this process helps deliver better software more efficiently.