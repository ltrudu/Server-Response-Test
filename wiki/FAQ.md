# Frequently Asked Questions (FAQ)

This FAQ addresses the most common questions about Server Response Test. For more detailed troubleshooting, see the [Troubleshooting Guide](Troubleshooting.md).

## 📱 General Usage

### Q: What types of servers can I test?
**A:** You can test any server that supports:
- **HTTPS**: Web servers, APIs, web applications (requires valid SSL certificate)
- **Ping (ICMP)**: Any server/device that responds to ping requests

The app supports both domain names (`https://example.com`) and IP addresses (`192.168.1.100`).

### Q: How many servers can I monitor simultaneously?
**A:** There's no hard limit, but for optimal performance and battery life:
- **Recommended**: 5-10 servers for regular monitoring
- **Maximum practical**: 20 servers 
- **Performance impact**: More servers = higher battery usage and network load

### Q: Can the app run in the background?
**A:** Yes! Version 1.1 includes robust background testing:
- ✅ **Foreground service** continues testing when app is closed
- ✅ **Rich notifications** show real-time status and controls
- ✅ **Pause/Resume/Stop** buttons directly in notification
- ⚠️ **Requires notification permission** for background operation

---

## ⚙️ Configuration & Settings

### Q: What's the difference between HTTPS and Ping testing?
**HTTPS Testing:**
- Tests actual web service availability
- Measures response time and HTTP status
- Uses more battery and data
- Best for: Web servers, APIs, applications

**Ping Testing:**
- Tests basic network connectivity
- Measures network round-trip time
- Uses minimal battery and data
- Best for: Routers, databases, internal servers

### Q: What time intervals should I use?
**Recommendations by use case:**
- **Real-time monitoring**: 1000-2000ms (high battery usage)
- **Development testing**: 2000-5000ms (balanced)
- **Production monitoring**: 30000-60000ms (battery efficient)
- **Background monitoring**: 60000ms+ (minimal impact)

### Q: Should I use infinite or finite requests?
**Infinite Requests (Recommended for monitoring):**
- ✅ Continuous monitoring until manually stopped
- ✅ Best for production monitoring and development
- ⚠️ Higher battery usage over time

**Finite Requests (Recommended for testing):**
- ✅ Runs specific number of cycles then stops
- ✅ Better for battery life and scheduled testing
- ✅ Shows remaining request counter

---

## 🔔 Notifications & Permissions

### Q: Why do I see orange notification status in Settings?
**A:** Orange status means notification permission is not properly enabled:

**For Android 13+:**
1. Tap the orange status indicator
2. Grant notification permission when prompted
3. Status should turn green

**For Android 8-12:**
1. Tap orange status to open system settings
2. Enable "Allow notifications"
3. Return to app and verify green status

### Q: Tests stop when I close the app, why?
**A:** This indicates the background service isn't running properly:

**Check these settings:**
1. **Notification permission**: Must be enabled (green status in Settings)
2. **Battery optimization**: Settings > Battery > Don't optimize this app
3. **Background activity**: Settings > Apps > Allow background activity
4. **Device-specific**: Samsung/Huawei/Xiaomi have additional restrictions

### Q: Can I test without enabling notifications?
**A:** Yes, but with limitations:
- ✅ **Foreground testing** works normally when app is open
- ❌ **Background testing** requires notification permission
- ❌ **No status updates** when app is closed

---

## 🌐 Network & Connectivity

### Q: Why do my HTTPS tests fail but websites work in browser?
**Common causes:**
1. **HTTP vs HTTPS**: App only supports HTTPS, not HTTP
2. **Certificate issues**: Server SSL certificate may be invalid
3. **Redirects**: App doesn't follow complex redirects
4. **Mobile-specific blocking**: Some servers block mobile clients

**Solutions:**
- Ensure URL starts with `https://`
- Test with `https://google.com` first
- Check server SSL certificate validity
- Try IP address instead of domain name

### Q: All my ping tests fail, what's wrong?
**A:** Ping failures are common and usually not an app issue:

**Common reasons:**
- **Firewalls**: Many servers/networks block ICMP ping
- **Corporate networks**: Often disable ping for security
- **Cloud servers**: Many cloud providers block ping by default
- **Android limitations**: Some versions restrict ping access

**Solutions:**
- Use HTTPS testing for web servers instead of ping
- Test local devices first (router: `192.168.1.1`)
- Try different network (mobile data vs WiFi)

### Q: Why do results vary between tests?
**A:** Variation is normal due to:
- **Network congestion**: Internet traffic affects response times
- **Server load**: Busy servers respond slower
- **Mobile connection**: Signal strength affects performance
- **Geographic distance**: Farther servers have higher latency

**Normal ranges:**
- **Local network**: 1-50ms
- **Same city**: 10-100ms  
- **Same country**: 50-200ms
- **International**: 100-500ms+

---

## 💾 Data Management

### Q: How do I backup my server configurations?
**A:** Use the built-in export feature:
1. Go to **Settings** tab
2. Tap **"Export Servers"**
3. Choose save location (Downloads folder recommended)
4. File saved as JSON format with all servers and settings

### Q: Can I share configurations with my team?
**A:** Yes, multiple ways:
- **Share button**: Settings > Share Servers (sends via email/messaging)
- **Export then share**: Export to file, then share file manually
- **JSON format**: Human-readable, can be edited in text editor

### Q: Import says "success" but I don't see any servers?
**A:** This was a known bug fixed in version 1.1:

**For version 1.1+:** Should work correctly
**For older versions:** Import the file twice (bug workaround)
**Best solution:** Update to latest version

---

## 🔋 Performance & Battery

### Q: Is the app draining my battery?
**A:** Battery usage depends on configuration:

**High battery usage causes:**
- Very frequent testing (1000ms intervals)
- Many servers (10+)
- Infinite requests with short intervals
- Continuous background operation

**Battery optimization tips:**
- Use 30000ms+ intervals for background monitoring
- Limit to 5 essential servers
- Use finite requests (100-200) instead of infinite
- Use ping instead of HTTPS when possible

### Q: How can I reduce data usage?
**A:** Several strategies:
- **Prefer ping over HTTPS**: Uses minimal data
- **Increase test intervals**: Fewer requests = less data
- **Reduce server count**: Test only essential servers
- **Use finite requests**: Avoid continuous testing on mobile data

---

## 🌍 Languages & Localization

### Q: Can I use the app in French?
**A:** Yes! Version 1.1 includes complete French translation:
- Change device language to French
- App automatically switches to French interface
- All features including notifications are translated

### Q: How do I add support for my language?
**A:** We welcome translation contributions:
1. Check [Contributing Guide](Contributing.md) for details
2. Create translation files for your language
3. Test thoroughly with different text lengths
4. Submit pull request with translations

---

## 🔧 Technical Questions

### Q: What Android versions are supported?
**A:** 
- **Minimum**: Android 7.0 (API 24)
- **Recommended**: Android 10+ for best experience
- **Latest tested**: Android 14

### Q: Does the app collect any personal data?
**A:** No personal data collection:
- ✅ **Local storage only**: All data stays on your device
- ✅ **No analytics**: No usage tracking or data collection
- ✅ **No network access**: Except for your configured server tests
- ✅ **Open source**: Code is publicly available for review

### Q: Can I use this on corporate networks?
**A:** Usually yes, but check with IT:
- **Firewall restrictions**: May block certain protocols
- **Proxy configuration**: App doesn't support proxy authentication
- **ICMP blocking**: Ping tests may not work
- **Security policies**: Some organizations restrict network testing tools

### Q: Is the source code available?
**A:** Yes, fully open source:
- **GitHub**: https://github.com/ltrudu/Server-Response-Test
- **License**: MIT License (free to use and modify)
- **Contributions**: Welcome via pull requests
- **Issues**: Report bugs and request features on GitHub

---

## 🚀 Version & Updates

### Q: How do I know which version I have?
**A:** Check version in several ways:
- **About section**: Usually in app settings (varies by version)
- **APK filename**: Contains version number when downloaded
- **GitHub releases**: Compare features with release notes

### Q: How do I update to the latest version?
**A:** Manual update process:
1. Visit [GitHub Releases](https://github.com/ltrudu/Server-Response-Test/releases)
2. Download latest APK file
3. Install over existing version (settings preserved)
4. Review [ChangeLog.md](../ChangeLog.md) for new features

### Q: What's new in version 1.1?
**Major features:**
- 🔔 **Background notifications** with control buttons
- 🛡️ **Smart permission management** for notifications
- ⏱️ **Millisecond precision** timing controls
- 📊 **Request progress tracking** for finite mode
- 🐛 **Import synchronization** bug fixes
- 🇫🇷 **Complete French** localization

---

## ❓ Still Have Questions?

### Can't find your answer here?

1. **Check detailed guides:**
   - [User Guide](User-Guide.md) - Complete usage instructions
   - [Troubleshooting](Troubleshooting.md) - Detailed problem solving
   - [Configuration Examples](Configuration-Examples.md) - Setup scenarios

2. **Search existing issues:**
   - [GitHub Issues](https://github.com/ltrudu/Server-Response-Test/issues)
   - [GitHub Discussions](https://github.com/ltrudu/Server-Response-Test/discussions)

3. **Contact support:**
   - **Email**: support@trudu.com
   - **Create issue**: For bugs or feature requests
   - **Start discussion**: For general questions

### Contributing to FAQ

Found this FAQ helpful? Help improve it:
- **Suggest additions**: Common questions not covered
- **Report errors**: Incorrect or outdated information  
- **Submit improvements**: Clearer explanations or examples

---

**Quick Links:**
- 📖 [User Guide](User-Guide.md) - How to use the app
- 🔧 [Troubleshooting](Troubleshooting.md) - Fix common problems
- ⚙️ [Configuration Examples](Configuration-Examples.md) - Setup scenarios
- 🤝 [Contributing](Contributing.md) - Help improve the app