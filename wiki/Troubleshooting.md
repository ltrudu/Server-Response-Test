# Troubleshooting Guide

This comprehensive troubleshooting guide helps you diagnose and resolve common issues with the Server Response Test application.

## 🔍 Quick Diagnostic Steps

### First Steps for Any Issue
1. **Check app version**: Ensure you have the latest version
2. **Restart the app**: Close completely and reopen
3. **Check network connection**: Verify internet connectivity
4. **Review notification permissions**: Ensure notifications are enabled
5. **Check device storage**: Ensure adequate free space (50MB+)

### Common Indicators of Issues
- ❌ **Red status indicators**: Server connectivity problems
- 🔴 **"No servers configured"**: Need to add servers first
- ⚠️ **Orange notification status**: Permission issues
- 🔋 **High battery usage**: Configuration optimization needed
- 📱 **App crashes**: Version compatibility or corruption issues

---

## 📱 Installation & Setup Issues

### APK Installation Problems

#### Problem: "App not installed" Error
**Symptoms**: Installation fails with generic error message

**Causes & Solutions**:
```
1. Insufficient Storage Space
   ✓ Free up at least 100MB of storage
   ✓ Clear cache from other apps
   ✓ Move files to external storage

2. Previous Installation Conflict
   ✓ Uninstall any previous versions
   ✓ Clear app data completely
   ✓ Restart device before reinstalling

3. Corrupted APK File
   ✓ Re-download APK from official source
   ✓ Verify file size matches expected
   ✓ Clear download cache and retry
```

#### Problem: "Parse Error" During Installation
**Symptoms**: APK file cannot be parsed

**Solutions**:
```
1. Check Android Version Compatibility
   ✓ Ensure Android 7.0+ (API 24)
   ✓ Verify device architecture (ARM/x86)

2. Re-download APK
   ✓ Download from official GitHub releases
   ✓ Verify file integrity (not corrupted)

3. Enable Unknown Sources
   ✓ Settings > Security > Install unknown apps
   ✓ Enable for your file manager/browser
```

#### Problem: "Installation Blocked" Message
**Symptoms**: System prevents installation

**Solutions**:
```
1. Unknown Sources Permission
   Android 8+: Settings > Apps > Special access > Install unknown apps
   Android 7: Settings > Security > Unknown sources

2. Device Administrator Restrictions
   ✓ Check if device is managed by organization
   ✓ Contact IT admin for permission
   ✓ Try installing on personal device

3. Antivirus Interference
   ✓ Temporarily disable antivirus
   ✓ Add APK to antivirus whitelist
   ✓ Install, then re-enable antivirus
```

### First Launch Problems

#### Problem: App Crashes on Startup
**Symptoms**: App closes immediately after opening

**Diagnostic Steps**:
```
1. Check Device Compatibility
   ✓ Android 7.0+ required
   ✓ 2GB+ RAM recommended
   ✓ ARM/ARM64 architecture

2. Clear App Data
   ✓ Settings > Apps > Server Response Test > Storage
   ✓ Clear cache and data
   ✓ Restart app

3. Reinstall Application
   ✓ Uninstall completely
   ✓ Restart device
   ✓ Reinstall from fresh APK
```

---

## ⚙️ Configuration Issues

### Server Configuration Problems

#### Problem: "Invalid server address" Error
**Symptoms**: Cannot save server configuration

**Common Causes & Fixes**:
```
1. URL Format Issues
   ❌ Wrong: http://example.com (HTTP not supported)
   ✅ Correct: https://example.com

   ❌ Wrong: example.com/path
   ✅ Correct: https://example.com (no paths)

2. IP Address Format
   ❌ Wrong: 192.168.1
   ✅ Correct: 192.168.1.100

   ❌ Wrong: 192.168.1.100:8080 (port in address)
   ✅ Correct: 192.168.1.100 (port in separate field)

3. Special Characters
   ❌ Wrong: https://my server.com (spaces)
   ✅ Correct: https://my-server.com
```

#### Problem: Cannot Edit Server Configuration
**Symptoms**: Edit dialog doesn't open or save fails

**Solutions**:
```
1. Check App Permissions
   ✓ Ensure storage permission granted
   ✓ Restart app after granting permissions

2. Database Issues
   ✓ Settings > Reset Database (last resort)
   ✓ Export servers first if possible
   ✓ Reinstall app if corruption suspected

3. Memory Issues
   ✓ Close other apps to free memory
   ✓ Restart device
   ✓ Clear app cache
```

### Settings Configuration Issues

#### Problem: Time Between Sessions Resets
**Symptoms**: Settings don't save or revert to default

**Causes & Solutions**:
```
1. Invalid Values
   ✓ Use values between 1000ms and 999999ms
   ✓ Avoid decimal points or special characters
   ✓ Enter numbers only

2. Auto-save Timing
   ✓ Wait 2-3 seconds after typing
   ✓ Don't switch tabs immediately
   ✓ Let auto-save complete

3. Database Permission Issues
   ✓ Check app storage permissions
   ✓ Ensure sufficient storage space
   ✓ Clear app cache if problems persist
```

---

## 🔔 Notification & Permission Issues

### Notification Permission Problems

#### Problem: Orange Notification Status in Settings
**Symptoms**: Background notifications not working

**Step-by-Step Resolution**:
```
1. Tap Orange Status Indicator
   ✓ Opens system notification settings directly
   ✓ Enable "Allow notifications"

2. Manual Permission Check
   ✓ Settings > Apps > Server Response Test > Notifications
   ✓ Enable all notification categories
   ✓ Ensure "Show on lock screen" is enabled

3. Android Version Specific
   Android 13+: Runtime permission required
   Android 8-12: Notification channels
   Android 7: Basic notification settings
```

#### Problem: Permission Dialog Not Appearing
**Symptoms**: App doesn't ask for notification permission

**Solutions**:
```
1. Force Permission Request
   ✓ Go to Settings tab
   ✓ Start a test (triggers permission check)
   ✓ Permission dialog should appear

2. Manual System Settings
   ✓ Settings > Apps > Server Response Test
   ✓ Permissions > Notifications
   ✓ Enable manually

3. Reset App Permissions
   ✓ Settings > Apps > Server Response Test
   ✓ Permissions > Reset to defaults
   ✓ Restart app and try again
```

### Background Service Issues

#### Problem: Tests Stop When App is Closed
**Symptoms**: No background testing despite enabled notifications

**Diagnostic & Solutions**:
```
1. Battery Optimization
   ✓ Settings > Battery > Battery optimization
   ✓ Find "Server Response Test"
   ✓ Set to "Don't optimize"

2. Background App Limits
   ✓ Settings > Apps > Server Response Test
   ✓ Battery > Background activity
   ✓ Enable "Allow background activity"

3. Notification Permission
   ✓ Must be enabled for foreground service
   ✓ Check Settings tab for green status
   ✓ Re-enable if orange

4. Device-Specific Settings
   Samsung: Settings > Device care > Battery > App power management
   Huawei: Settings > Battery > App launch
   Xiaomi: Settings > Apps > Manage apps > Autostart
```

---

## 🌐 Network & Connectivity Issues

### HTTPS Testing Problems

#### Problem: All HTTPS Tests Failing
**Symptoms**: Red status indicators, timeout errors

**Systematic Diagnosis**:
```
1. Test Network Connectivity
   ✓ Open web browser
   ✓ Visit https://google.com
   ✓ Confirm internet works

2. Test Simple Server First
   ✓ Add https://google.com as test server
   ✓ Run single test
   ✓ If this fails, network issue exists

3. Check Firewall/Proxy
   ✓ Corporate networks may block app
   ✓ Try on mobile data vs WiFi
   ✓ Contact network administrator

4. DNS Issues
   ✓ Try IP address instead of domain name
   ✓ Change DNS servers (8.8.8.8, 1.1.1.1)
   ✓ Clear DNS cache
```

#### Problem: Specific Servers Timing Out
**Symptoms**: Some servers work, others don't

**Targeted Solutions**:
```
1. Server-Specific Issues
   ✓ Test server manually in browser
   ✓ Check server status/uptime
   ✓ Verify server supports mobile clients

2. SSL/Certificate Problems
   ✓ Ensure server has valid SSL certificate
   ✓ Check certificate expiration
   ✓ Try different HTTPS URLs

3. Server Configuration
   ✓ Remove port number if using standard 443
   ✓ Ensure URL format is correct
   ✓ Check for redirects that might cause issues
```

### Ping Testing Problems

#### Problem: Ping Tests Always Fail
**Symptoms**: All ping tests show unreachable

**Common Causes & Fixes**:
```
1. ICMP Blocked
   ✓ Many servers/firewalls block ping
   ✓ Try HTTPS test instead for web servers
   ✓ Use ping for routers/gateways only

2. Network Configuration
   ✓ Corporate networks often block ICMP
   ✓ Try on different network (mobile data)
   ✓ Test local devices first (192.168.1.1)

3. Android Limitations
   ✓ Some Android versions restrict ping
   ✓ Requires specific network permissions
   ✓ Use HTTPS for application servers
```

---

## 📊 Performance & Battery Issues

### High Battery Usage

#### Problem: App Draining Battery Quickly
**Symptoms**: Significant battery consumption in background

**Optimization Steps**:
```
1. Adjust Test Intervals
   Current: 1000ms (very frequent)
   ✓ Recommended: 30000ms+ for background monitoring
   ✓ Balance: monitoring needs vs battery life

2. Reduce Server Count
   Current: 10+ servers
   ✓ Recommended: 3-5 critical servers
   ✓ Focus on essential endpoints only

3. Use Finite Requests
   Current: Infinite requests
   ✓ Alternative: 100-200 finite requests
   ✓ Schedule specific testing windows

4. Optimize Request Types
   ✓ Use PING instead of HTTPS when possible
   ✓ PING uses less data and battery
   ✓ HTTPS for web services only
```

### App Performance Issues

#### Problem: App Runs Slowly or Freezes
**Symptoms**: UI lag, delayed responses, occasional freezes

**Performance Solutions**:
```
1. Memory Management
   ✓ Close other apps to free RAM
   ✓ Restart device if severe lag
   ✓ Clear app cache: Settings > Apps > Storage

2. Reduce Load
   ✓ Decrease number of concurrent tests
   ✓ Increase time between requests
   ✓ Use finite instead of infinite requests

3. Database Optimization
   ✓ Limit server list to <20 servers
   ✓ Export/reset database if very large
   ✓ Avoid rapid configuration changes
```

---

## 💾 Data Management Issues

### Import/Export Problems

#### Problem: Export Function Not Working
**Symptoms**: No file created or sharing fails

**Step-by-Step Resolution**:
```
1. Check Storage Permission
   ✓ Android 10+: Uses Storage Access Framework
   ✓ No additional permissions needed
   ✓ Select save location when prompted

2. Storage Space
   ✓ Ensure device has free space
   ✓ Clear unnecessary files
   ✓ Try different storage location

3. File Access Issues
   ✓ Try different file manager
   ✓ Save to Downloads folder
   ✓ Check if file was created successfully
```

#### Problem: Import Shows Success But No Data
**Symptoms**: Toast says "success" but servers don't appear

**Known Issue & Solution**:
```
This was a race condition bug fixed in version 1.1

1. For Version 1.1+
   ✓ Import should work correctly
   ✓ Data appears immediately after success toast

2. For Older Versions
   ✓ Import the file twice
   ✓ Second import will show data
   ✓ Update to latest version for fix

3. Verification Steps
   ✓ Check server count in Test tab
   ✓ Navigate to Server List tab
   ✓ Verify servers appear correctly
```

#### Problem: JSON File Format Errors
**Symptoms**: Import fails with format error

**File Format Validation**:
```
1. Check JSON Syntax
   ✓ Use JSON validator online
   ✓ Ensure all brackets/braces match
   ✓ No trailing commas

2. Required Fields
   ✓ servers: array of server objects
   ✓ settings: configuration object
   ✓ Each server needs: name, address, requestType

3. Example Valid Format:
{
  "servers": [
    {
      "name": "Test Server",
      "address": "https://example.com",
      "port": null,
      "requestType": "HTTPS"
    }
  ],
  "settings": {
    "timeBetweenRequests": 5000,
    "infiniteRequests": true,
    "numberOfRequests": 10
  }
}
```

---

## 🔧 Advanced Troubleshooting

### Database Issues

#### Problem: Persistent Data Corruption
**Symptoms**: App crashes, settings don't save, servers disappear

**Database Reset Procedure**:
```
⚠️ WARNING: This will delete all data

1. Export Data First (if possible)
   ✓ Settings > Export Servers
   ✓ Save to secure location

2. Reset Database
   ✓ Settings > Danger Zone > Reset Database
   ✓ Confirm deletion
   ✓ Restart app

3. Restore Data
   ✓ Settings > Import Servers
   ✓ Select previously exported file
   ✓ Verify data restored correctly
```

### Service Recovery

#### Problem: Background Service Won't Start
**Symptoms**: Tests don't run, no notifications appear

**Service Restart Process**:
```
1. Force Stop App
   ✓ Settings > Apps > Server Response Test
   ✓ Force Stop
   ✓ Wait 10 seconds

2. Clear Service Cache
   ✓ Settings > Apps > Server Response Test
   ✓ Storage > Clear Cache
   ✓ Don't clear data unless necessary

3. Restart and Test
   ✓ Open app
   ✓ Check notification permission (green status)
   ✓ Start simple test with one server
   ✓ Verify notification appears
```

### Network Debugging

#### Problem: Intermittent Network Failures
**Symptoms**: Tests sometimes work, sometimes fail

**Network Diagnosis Tools**:
```
1. Test Network Stability
   ✓ Use different networks (WiFi vs mobile)
   ✓ Test at different times of day
   ✓ Monitor network signal strength

2. Server Response Analysis
   ✓ Note which servers fail consistently
   ✓ Test servers manually in browser
   ✓ Check server logs if accessible

3. App Network Configuration
   ✓ Use longer timeouts (increase intervals)
   ✓ Reduce concurrent connections
   ✓ Test one server at a time
```

---

## 📋 Diagnostic Information Collection

### Information to Collect for Support

When reporting issues, include:

#### Device Information
```
- Device model: Samsung Galaxy S21
- Android version: 12
- App version: 1.1
- Available storage: 2.5 GB
- RAM usage: 45%
```

#### Network Information
```
- Connection type: WiFi / Mobile Data
- Network provider: Verizon
- Location: Corporate network / Home / Public
- Firewall/Proxy: Yes/No/Unknown
```

#### Issue Details
```
- Exact error messages
- Steps to reproduce
- When issue started
- Frequency of occurrence
- Screenshots if relevant
```

#### Configuration Details
```
- Number of servers configured
- Time between sessions setting
- Infinite vs finite requests
- Types of servers (HTTPS/Ping)
- Server addresses (sanitized if sensitive)
```

---

## 🆘 Emergency Recovery

### Complete App Reset

If all else fails, perform complete reset:

```
1. Export Critical Data
   ✓ Try Settings > Export if app partially works
   ✓ Screenshot server configurations
   ✓ Note important settings

2. Complete Uninstall
   ✓ Settings > Apps > Server Response Test
   ✓ Uninstall
   ✓ Clear all app data

3. Device Restart
   ✓ Power off device completely
   ✓ Wait 30 seconds
   ✓ Power on and wait for full boot

4. Fresh Installation
   ✓ Download latest APK
   ✓ Install clean version
   ✓ Configure from scratch or import backup
```

---

## 📞 Getting Additional Help

### Support Channels

#### GitHub Issues
- **Best for**: Bug reports, feature requests
- **URL**: https://github.com/ltrudu/Server-Response-Test/issues
- **Include**: Diagnostic information, steps to reproduce

#### GitHub Discussions
- **Best for**: General questions, usage help
- **URL**: https://github.com/ltrudu/Server-Response-Test/discussions
- **Include**: Configuration details, what you're trying to achieve

#### Email Support
- **Best for**: Private issues, sensitive information
- **Email**: support@trudu.com
- **Include**: Device info, detailed description

### Before Contacting Support

1. **Check this troubleshooting guide** thoroughly
2. **Review [FAQ](FAQ.md)** for common questions
3. **Search existing GitHub issues** for similar problems
4. **Try basic solutions** (restart app, check permissions)
5. **Collect diagnostic information** listed above

### Response Expectations
- **GitHub Issues**: 1-3 business days
- **GitHub Discussions**: Community-driven, varies
- **Email**: 2-5 business days for complex issues

---

Most issues can be resolved using this guide. If you find a solution not covered here, consider contributing to improve this documentation for other users! 🚀