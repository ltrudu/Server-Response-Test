# User Guide

Welcome to the Server Response Test User Guide! This comprehensive guide will help you get the most out of the application's features and capabilities.

## 🚀 Getting Started

### First Launch
When you first open Server Response Test, you'll see a clean interface with three main tabs:
- **Test** - Monitor and control server testing
- **Server List** - Manage your server configurations
- **Settings** - Configure test parameters and app preferences

### Quick Setup (5 Minutes)
1. **Add Your First Server** (Server List tab)
2. **Configure Test Settings** (Settings tab)  
3. **Start Testing** (Test tab)
4. **Enable Notifications** (for background testing)

## 📋 Tab-by-Tab Guide

### 1. 🖥️ Server List Tab

The Server List tab is where you manage all your server configurations.

#### Adding a New Server
1. **Tap the ➕ button** at the bottom of the screen
2. **Fill in the server details:**
   - **Server Name**: A descriptive name (e.g., "Production Web Server")
   - **Server Address**: HTTPS URL or IP address
     - ✅ `https://example.com`
     - ✅ `192.168.1.100`
     - ❌ `http://example.com` (HTTP not supported)
   - **Port** (Optional): Specific port number
   - **Request Type**: Choose between HTTPS or Ping
3. **Tap Save** to add the server

#### Managing Existing Servers
- **Edit**: Tap on any server to modify its configuration
- **Delete**: Swipe left on a server and confirm deletion
- **View Details**: Each server shows its name, address, and type

#### Server Configuration Examples
```
Name: Production API
Address: https://api.mycompany.com
Port: 443
Type: HTTPS

Name: Database Server
Address: 192.168.1.50
Port: 3306
Type: Ping

Name: Load Balancer
Address: https://lb.example.com
Port: (empty)
Type: HTTPS
```

### 2. ⚡ Test Tab

The Test tab is your mission control for server monitoring.

#### Starting Tests
1. **Ensure you have servers configured** (check the counter at top)
2. **Tap the ▶️ Play button** to start testing
3. **Grant notification permission** when prompted (for background testing)
4. **Monitor real-time results** as they appear

#### Understanding Test Results
Each server displays:
- **🟢 Green Circle**: Server responding normally
- **🔴 Red Circle**: Server not responding or error
- **Response Time**: Milliseconds for HTTPS requests
- **Status**: "OK", "Timeout", "Error", etc.

#### Test Controls
- **▶️ Play Button**: Start testing all configured servers
- **⏹️ Stop Button**: Stop all running tests
- **Server Counter**: Shows number of configured servers
- **Remaining Requests**: Shows progress when using finite test mode

#### Background Testing
When tests are running in the background:
- **Persistent Notification**: Shows current status
- **Notification Controls**: Pause, Resume, Stop buttons
- **Progress Updates**: Real-time status in notification bar
- **App Independence**: Tests continue even when app is closed

### 3. ⚙️ Settings Tab

The Settings tab lets you fine-tune test behavior and manage data.

#### Test Configuration
- **Time Between Sessions**: Interval between test cycles (in milliseconds)
  - Default: 5000ms (5 seconds)
  - Range: 1000ms to 999999ms
  - Example: 2500ms for 2.5-second intervals

- **Infinite Requests**: Toggle for continuous testing
  - ✅ **ON**: Tests run until manually stopped
  - ❌ **OFF**: Tests run for specified number of cycles

- **Number of Requests**: How many test cycles to run (when finite)
  - Default: 10 cycles
  - Range: 1 to 999+ cycles

#### Notification Settings
- **Status Indicator**: Shows notification permission state
  - 🟢 **Green**: Notifications enabled and working
  - 🟠 **Orange**: Notifications disabled or not available
- **One-Tap Fix**: Tap the status to open system settings

#### Data Management
- **Export Servers**: Save configurations to JSON file
- **Import Servers**: Restore from previously exported file
- **Share Servers**: Send configurations to team members
- **Reset Database**: ⚠️ **Danger Zone** - Clears all data

## 🔔 Notification System

### Permission Setup
1. **Automatic Prompt**: App will request notification permission
2. **Educational Dialog**: Explains benefits of allowing notifications
3. **System Settings**: Direct access if permission is denied
4. **Visual Indicator**: Settings tab shows current status

### Notification Features
When testing in background, you'll see:
- **Rich Status Display**: Current server being tested
- **Progress Information**: Request counts and timing
- **Action Buttons**:
  - **⏸️ Pause**: Temporarily stop testing
  - **▶️ Resume**: Continue paused tests  
  - **⏹️ Stop**: End all testing

### Background Behavior
- **Persistent Service**: Tests continue when app is closed
- **Battery Optimized**: Efficient resource usage
- **System Integration**: Proper Android foreground service
- **Reliable Operation**: Survives device sleep and app switching

## 📊 Understanding Test Results

### HTTPS Testing
- **Success**: Server responds with HTTP 200 status
- **Response Time**: Measured in milliseconds (ms)
- **Common Results**:
  - 50-200ms: Excellent response time
  - 200-500ms: Good response time
  - 500ms+: Slow response (may indicate issues)
  - Timeout: Server not responding within limit

### Ping Testing  
- **Success**: Server is reachable via ICMP
- **Response Time**: Network round-trip time
- **Common Results**:
  - <50ms: Excellent network connectivity
  - 50-100ms: Good connectivity
  - 100ms+: Higher latency (distance/network issues)
  - Unreachable: Network or firewall blocking

### Status Indicators
- **🟢 Green**: All tests passing
- **🔴 Red**: Tests failing or timing out
- **🟡 Yellow**: Mixed results or warnings
- **⚪ Gray**: Not tested yet or disabled

## 💾 Data Management

### Exporting Configurations
1. **Go to Settings tab**
2. **Tap "Export Servers"**
3. **Choose save location** (Downloads folder by default)
4. **File format**: JSON with all servers and settings

### Importing Configurations
1. **Go to Settings tab**
2. **Tap "Import Servers"**
3. **Select JSON file** from device storage
4. **Confirm import** - existing data will be merged
5. **Success confirmation** appears when complete

### Sharing Configurations
1. **Go to Settings tab**
2. **Tap "Share Servers"**
3. **Choose sharing method** (email, messaging, etc.)
4. **JSON file** is automatically attached

### Export File Format
```json
{
  "servers": [
    {
      "name": "Production API",
      "address": "https://api.example.com",
      "port": 443,
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

## 🌍 Language Support

### Switching Languages
- **System Language**: App follows device language setting
- **Supported Languages**:
  - 🇺🇸 English (default)
  - 🇫🇷 Français (French)

### French Interface
When device is set to French:
- All menus and buttons in French
- Notification dialogs in French
- Error messages in French
- Help text and tooltips in French

## 🔧 Troubleshooting

### Common Issues

#### "No servers configured"
**Problem**: Test tab shows "No servers configured"
**Solution**: Add servers in the Server List tab first

#### "Tests not starting"
**Problem**: Play button doesn't start tests
**Solutions**: 
- Check internet connectivity
- Verify server addresses are correct
- Grant notification permission when prompted

#### "Background tests stopping"
**Problem**: Tests stop when app is closed
**Solutions**:
- Enable notification permission
- Check battery optimization settings
- Ensure foreground service is allowed

#### "Import not working"
**Problem**: Import shows success but no data appears
**Solutions**:
- Verify JSON file format is correct
- Check file isn't corrupted
- Try importing again (synchronization issue)

#### "Notifications not appearing"
**Problem**: No background test notifications
**Solutions**:
- Check notification permission in Settings tab
- Tap orange status indicator to fix
- Enable notifications in system settings

### Performance Tips

#### Optimize Test Intervals
- **Frequent Testing**: 1000-3000ms for rapid monitoring
- **Normal Monitoring**: 5000-10000ms for regular checks
- **Light Monitoring**: 30000ms+ for minimal resource usage

#### Battery Conservation
- **Use Ping** instead of HTTPS when possible (lighter)
- **Increase intervals** for longer battery life
- **Finite requests** instead of infinite for automated testing

#### Network Considerations
- **Local servers**: Use IP addresses for faster resolution
- **Remote servers**: Use HTTPS URLs with proper certificates
- **Firewall**: Ensure ICMP (ping) is allowed if using ping tests

## 🎯 Advanced Usage

### Testing Strategies

#### Production Monitoring
```
Time Between Sessions: 30000ms (30 seconds)
Infinite Requests: ON
Request Type: HTTPS
Purpose: Continuous production monitoring
```

#### Development Testing
```
Time Between Sessions: 2000ms (2 seconds)  
Infinite Requests: OFF
Number of Requests: 50
Purpose: Rapid development testing
```

#### Network Diagnostics
```
Time Between Sessions: 1000ms (1 second)
Request Type: Ping
Infinite Requests: OFF
Number of Requests: 100
Purpose: Network latency analysis
```

### Multiple Server Monitoring
- **Grouped Testing**: All servers tested simultaneously
- **Independent Results**: Each server shows individual status
- **Concurrent Execution**: No waiting between servers
- **Aggregated View**: Overall system health at a glance

### Integration with Workflows
- **Export for Documentation**: Share server lists with team
- **Scheduled Testing**: Use finite requests for automated runs
- **Alert Workflows**: Monitor notification results for issues
- **Performance Baselines**: Track response times over time

---

## 📞 Need More Help?

- **FAQ**: Check [Frequently Asked Questions](FAQ.md)
- **Troubleshooting**: Visit [Troubleshooting Guide](Troubleshooting.md)
- **Support**: Email support@trudu.com
- **Community**: [GitHub Discussions](https://github.com/your-repo/server-response-test/discussions)

**Happy Monitoring!** 🚀