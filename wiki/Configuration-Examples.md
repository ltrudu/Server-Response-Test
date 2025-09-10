# Configuration Examples

This guide provides real-world configuration examples for different use cases and scenarios. Each example includes detailed setup instructions and recommended settings.

## 🎯 Common Use Cases

### 1. Production Monitoring

**Scenario**: Monitor critical production servers with reliable alerting
**Goal**: Continuous monitoring with minimal resource impact

```
Configuration:
├── Time Between Sessions: 30000ms (30 seconds)
├── Infinite Requests: ✅ ON
├── Number of Requests: N/A
└── Notification: ✅ Enabled

Servers:
┌─────────────────────────────────────────────────┐
│ Name: Production Web Server                     │
│ Address: https://www.mycompany.com              │
│ Port: 443                                       │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: API Gateway                               │
│ Address: https://api.mycompany.com              │
│ Port: (empty)                                   │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: Database Server                           │
│ Address: 10.0.1.50                              │
│ Port: 3306                                      │
│ Type: PING                                      │
└─────────────────────────────────────────────────┘
```

**Benefits**:
- 30-second intervals balance monitoring frequency with battery life
- Infinite requests ensure continuous monitoring
- HTTPS for web services, Ping for internal servers
- Background notifications alert to issues immediately

---

### 2. Development Testing

**Scenario**: Rapid testing during development cycles
**Goal**: Quick feedback on server changes

```
Configuration:
├── Time Between Sessions: 2000ms (2 seconds)
├── Infinite Requests: ❌ OFF
├── Number of Requests: 50
└── Notification: ✅ Enabled

Servers:
┌─────────────────────────────────────────────────┐
│ Name: Dev Web Server                            │
│ Address: https://dev.mycompany.com              │
│ Port: 8080                                      │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: Local API                                 │
│ Address: https://localhost                      │
│ Port: 3000                                      │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: Test Database                             │
│ Address: 192.168.1.100                          │
│ Port: 5432                                      │
│ Type: PING                                      │
└─────────────────────────────────────────────────┘
```

**Benefits**:
- 2-second intervals provide rapid feedback
- Finite requests (50) prevent endless testing
- Multiple environments tested simultaneously
- Quick validation of changes

---

### 3. Network Diagnostics

**Scenario**: Troubleshoot network connectivity issues
**Goal**: Detailed network analysis and latency measurement

```
Configuration:
├── Time Between Sessions: 1000ms (1 second)
├── Infinite Requests: ❌ OFF
├── Number of Requests: 100
└── Notification: ✅ Enabled

Servers:
┌─────────────────────────────────────────────────┐
│ Name: Local Gateway                             │
│ Address: 192.168.1.1                            │
│ Port: (empty)                                   │
│ Type: PING                                      │
├─────────────────────────────────────────────────┤
│ Name: DNS Server                                │
│ Address: 8.8.8.8                                │
│ Port: (empty)                                   │
│ Type: PING                                      │
├─────────────────────────────────────────────────┤
│ Name: ISP Gateway                               │
│ Address: 10.0.0.1                               │
│ Port: (empty)                                   │
│ Type: PING                                      │
├─────────────────────────────────────────────────┤
│ Name: External Test                             │
│ Address: https://google.com                     │
│ Port: (empty)                                   │
│ Type: HTTPS                                     │
└─────────────────────────────────────────────────┘
```

**Benefits**:
- 1-second intervals for detailed timing analysis
- 100 requests provide statistical significance
- Multiple network hops tested
- Mix of ping and HTTPS for comprehensive testing

---

### 4. Load Balancer Testing

**Scenario**: Monitor multiple load-balanced endpoints
**Goal**: Verify load balancer health and distribution

```
Configuration:
├── Time Between Sessions: 5000ms (5 seconds)
├── Infinite Requests: ✅ ON
├── Number of Requests: N/A
└── Notification: ✅ Enabled

Servers:
┌─────────────────────────────────────────────────┐
│ Name: LB Primary Endpoint                       │
│ Address: https://lb1.mycompany.com              │
│ Port: 443                                       │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: LB Secondary Endpoint                     │
│ Address: https://lb2.mycompany.com              │
│ Port: 443                                       │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: Backend Server 1                          │
│ Address: https://backend1.internal              │
│ Port: 8080                                      │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: Backend Server 2                          │
│ Address: https://backend2.internal              │
│ Port: 8080                                      │
│ Type: HTTPS                                     │
└─────────────────────────────────────────────────┘
```

**Benefits**:
- 5-second intervals balance monitoring with server load
- Continuous monitoring detects failover scenarios
- Both load balancer and backend servers monitored
- Identifies single points of failure

---

### 5. Cloud Service Monitoring

**Scenario**: Monitor various cloud service endpoints
**Goal**: Track cloud service availability and performance

```
Configuration:
├── Time Between Sessions: 15000ms (15 seconds)
├── Infinite Requests: ✅ ON
├── Number of Requests: N/A
└── Notification: ✅ Enabled

Servers:
┌─────────────────────────────────────────────────┐
│ Name: AWS API Gateway                           │
│ Address: https://api.amazonaws.com              │
│ Port: (empty)                                   │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: Azure Functions                           │
│ Address: https://myapp.azurewebsites.net        │
│ Port: (empty)                                   │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: Google Cloud Storage                      │
│ Address: https://storage.googleapis.com         │
│ Port: (empty)                                   │
│ Type: HTTPS                                     │
├─────────────────────────────────────────────────┤
│ Name: CDN Endpoint                              │
│ Address: https://cdn.mycompany.com              │
│ Port: (empty)                                   │
│ Type: HTTPS                                     │
└─────────────────────────────────────────────────┘
```

**Benefits**:
- 15-second intervals respect cloud service rate limits
- Monitors multiple cloud providers
- Tracks CDN performance
- Identifies provider-specific issues

---

## ⚙️ Advanced Configurations

### Configuration for Different Network Types

#### Corporate Network
```
Recommended Settings:
├── Time Between Sessions: 10000ms
├── Use PING for internal servers
├── Use HTTPS for external services
└── Consider firewall restrictions

Example Internal Servers:
- 10.0.0.x range for infrastructure
- 192.168.x.x for local services
- Corporate domain names
```

#### Home Network
```
Recommended Settings:
├── Time Between Sessions: 5000ms
├── Monitor router (192.168.1.1)
├── Test external connectivity
└── Include ISP DNS servers

Example Setup:
- Router: 192.168.1.1 (PING)
- DNS: 8.8.8.8 (PING)
- Speed test: https://fast.com (HTTPS)
```

#### Mobile/LTE Network
```
Recommended Settings:
├── Time Between Sessions: 30000ms (save battery)
├── Fewer servers to reduce data usage
├── Focus on critical endpoints only
└── Monitor carrier gateways

Battery Optimization:
- Longer intervals (30s+)
- Finite requests when possible
- Disable when not needed
```

---

## 📊 Performance Scenarios

### High-Frequency Monitoring
```
Use Case: Real-time system monitoring
Time Between Sessions: 500-1000ms
Requests: Infinite
Servers: 3-5 critical endpoints
Battery Impact: High
Best For: Short-term diagnostics
```

### Standard Monitoring
```
Use Case: Regular server health checks
Time Between Sessions: 5000-10000ms
Requests: Infinite
Servers: 5-10 endpoints
Battery Impact: Medium
Best For: Daily monitoring
```

### Light Monitoring
```
Use Case: Periodic availability checks
Time Between Sessions: 60000ms (1 minute)
Requests: Finite (100-200)
Servers: 2-3 essential services
Battery Impact: Low
Best For: Background monitoring
```

---

## 🔧 Troubleshooting Configurations

### When Servers Are Unreachable

**Problem**: Getting timeout errors
**Solution Configuration**:
```
1. Increase time between sessions to 10000ms+
2. Use PING instead of HTTPS for basic connectivity
3. Reduce number of servers being tested
4. Check network connectivity first

Test Network Stack:
├── Router: 192.168.1.1 (PING)
├── DNS: 8.8.8.8 (PING)  
├── ISP: traceroute endpoint (PING)
└── Target: your server (HTTPS)
```

### When Getting Permission Errors

**Problem**: Tests not running in background
**Solution Steps**:
```
1. Enable notification permission in Settings tab
2. Check battery optimization settings
3. Verify foreground service is allowed
4. Test with shorter finite requests first

Verification Configuration:
├── Time Between Sessions: 5000ms
├── Infinite Requests: OFF
├── Number of Requests: 10
└── Single test server
```

### When Battery Drains Quickly

**Problem**: High battery usage
**Optimization Configuration**:
```
├── Time Between Sessions: 30000ms+ (longer intervals)
├── Finite Requests: 50-100 (not infinite)
├── Fewer Servers: 3-5 maximum
├── Use PING: Instead of HTTPS when possible
└── Scheduled Testing: Run at specific times only

Power-Saving Setup:
- Morning check: 50 requests at 30s intervals
- Afternoon check: 50 requests at 30s intervals  
- Evening check: 50 requests at 30s intervals
```

---

## 💾 Import/Export Examples

### Team Configuration Template
```json
{
  "servers": [
    {
      "name": "Production Web",
      "address": "https://www.company.com",
      "port": null,
      "requestType": "HTTPS"
    },
    {
      "name": "API Gateway",
      "address": "https://api.company.com",
      "port": null,
      "requestType": "HTTPS"
    },
    {
      "name": "Database Check",
      "address": "10.0.1.50",
      "port": 3306,
      "requestType": "PING"
    }
  ],
  "settings": {
    "timeBetweenRequests": 30000,
    "infiniteRequests": true,
    "numberOfRequests": 10
  }
}
```

### Development Environment Template
```json
{
  "servers": [
    {
      "name": "Local Dev Server",
      "address": "https://localhost",
      "port": 3000,
      "requestType": "HTTPS"
    },
    {
      "name": "Dev Database",
      "address": "127.0.0.1",
      "port": 5432,
      "requestType": "PING"
    }
  ],
  "settings": {
    "timeBetweenRequests": 2000,
    "infiniteRequests": false,
    "numberOfRequests": 50
  }
}
```

---

## 🎯 Best Practices Summary

### Choosing Time Intervals
- **Real-time debugging**: 1-2 seconds
- **Development testing**: 2-5 seconds  
- **Production monitoring**: 30-60 seconds
- **Background monitoring**: 5+ minutes

### Server Selection Strategy
- **Start small**: Begin with 2-3 critical servers
- **Expand gradually**: Add more as needed
- **Mix protocols**: Use HTTPS for web services, PING for infrastructure
- **Group logically**: Organize by environment or function

### Battery Optimization
- **Use finite requests** for scheduled checks
- **Increase intervals** for continuous monitoring
- **Minimize server count** when on mobile data
- **Disable when not needed** for maximum battery life

### Network Considerations
- **Test connectivity** before adding servers
- **Consider firewalls** in corporate environments
- **Respect rate limits** for cloud services
- **Monitor data usage** on mobile connections

---

Need help setting up your specific configuration? Check the [User Guide](User-Guide.md) for detailed instructions or visit [Troubleshooting](Troubleshooting.md) for common issues.