# Database Schema

This document provides comprehensive documentation of the Server Response Test application's database structure, relationships, and implementation details.

## 🗄️ Database Overview

### Technology Stack
- **Database**: SQLite with Room persistence library
- **ORM**: Android Room (type-safe SQLite abstraction)
- **Version**: 2 (current schema)
- **Migration Strategy**: Automated migrations with manual fallback

### Database File
- **Location**: `/data/data/com.ltrudu.serverresponsetest/databases/`
- **Filename**: `server_response_database`
- **Format**: SQLite 3.x

## 📋 Tables Overview

```sql
-- Database contains 2 main tables:
-- 1. servers    - Server configurations
-- 2. settings   - Application settings (v1.1+)
```

---

## 🖥️ Servers Table

### Table Definition
```sql
CREATE TABLE servers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    port INTEGER,
    requestType TEXT NOT NULL
);
```

### Field Specifications

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique server identifier |
| `name` | TEXT | NOT NULL | Human-readable server name |
| `address` | TEXT | NOT NULL | Server URL or IP address |
| `port` | INTEGER | NULLABLE | Optional port number |
| `requestType` | TEXT | NOT NULL | Test type: 'HTTPS' or 'PING' |

### Field Details

#### id (Primary Key)
- **Type**: Auto-incrementing integer
- **Range**: 1 to 2^63-1
- **Usage**: Internal reference for server records
- **Generated**: Automatically by database

#### name
- **Type**: Text string
- **Constraints**: Cannot be null or empty
- **Length**: Unlimited (practical limit ~1000 chars)
- **Usage**: Display name in UI
- **Examples**: 
  - "Production Web Server"
  - "Dev API Gateway" 
  - "Database Server"

#### address
- **Type**: Text string  
- **Constraints**: Cannot be null or empty
- **Format**: URL or IP address
- **Validation**: Performed at application level
- **Examples**:
  - `https://www.example.com`
  - `192.168.1.100`
  - `https://api.company.com`

#### port
- **Type**: Integer (nullable)
- **Range**: 1-65535 (standard port range)
- **Usage**: Override default ports
- **Default behavior**: 
  - HTTPS: Uses 443 if null
  - Ping: Port ignored
- **Examples**: `8080`, `3000`, `8443`

#### requestType
- **Type**: Text string (enum-like)
- **Allowed values**: `'HTTPS'`, `'PING'`
- **Storage**: String representation of RequestType enum
- **Validation**: Enforced by Room type converters

### Indexes
```sql
-- Automatic index on primary key
CREATE UNIQUE INDEX sqlite_autoindex_servers_1 ON servers(id);

-- Optional performance index (not currently implemented)
-- CREATE INDEX idx_servers_name ON servers(name);
```

### Sample Data
```sql
INSERT INTO servers (id, name, address, port, requestType) VALUES 
(1, 'Production Website', 'https://www.mycompany.com', NULL, 'HTTPS'),
(2, 'API Gateway', 'https://api.mycompany.com', 8080, 'HTTPS'),
(3, 'Database Server', '192.168.1.50', 3306, 'PING'),
(4, 'Load Balancer', 'https://lb.example.com', NULL, 'HTTPS');
```

---

## ⚙️ Settings Table

### Table Definition (Version 1.1+)
```sql
CREATE TABLE settings (
    id INTEGER PRIMARY KEY,
    time_between_requests INTEGER NOT NULL DEFAULT 5000,
    request_delay_ms INTEGER NOT NULL DEFAULT 100,
    random_min_delay_ms INTEGER NOT NULL DEFAULT 50,
    random_max_delay_ms INTEGER NOT NULL DEFAULT 100,
    infinite_requests INTEGER NOT NULL DEFAULT 1,
    number_of_requests INTEGER NOT NULL DEFAULT 10
);
```

### Field Specifications

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `id` | INTEGER | 1 | Singleton identifier |
| `time_between_requests` | INTEGER | 5000 | Milliseconds between test cycles |
| `request_delay_ms` | INTEGER | 100 | Delay between individual requests |
| `random_min_delay_ms` | INTEGER | 50 | Minimum random delay |
| `random_max_delay_ms` | INTEGER | 100 | Maximum random delay |
| `infinite_requests` | INTEGER | 1 | Boolean: 1=infinite, 0=finite |
| `number_of_requests` | INTEGER | 10 | Number of requests when finite |

### Field Details

#### id (Singleton Pattern)
- **Value**: Always 1
- **Purpose**: Ensures only one settings record exists
- **Implementation**: Single-row table pattern

#### time_between_requests
- **Type**: Integer (milliseconds)
- **Range**: 1000-999999 (1 second to ~16 minutes)
- **Default**: 5000 (5 seconds)
- **Version 1.1 change**: Changed from seconds to milliseconds
- **Migration**: Existing values multiplied by 1000

#### request_delay_ms
- **Type**: Integer (milliseconds)
- **Range**: 1-9999
- **Purpose**: Delay between individual server requests
- **Usage**: Currently unused in implementation

#### random_min_delay_ms / random_max_delay_ms
- **Type**: Integer (milliseconds)
- **Purpose**: Random delay range for request timing
- **Usage**: Currently unused in implementation
- **Future**: May be used for randomized testing intervals

#### infinite_requests
- **Type**: Integer (boolean)
- **Values**: 1 (true) = infinite, 0 (false) = finite
- **Default**: 1 (infinite requests)
- **UI**: Controlled by toggle switch in Settings

#### number_of_requests
- **Type**: Integer
- **Range**: 1-9999
- **Default**: 10
- **Usage**: Only used when infinite_requests = 0

### Sample Data
```sql
INSERT INTO settings (
    id, 
    time_between_requests, 
    request_delay_ms,
    random_min_delay_ms,
    random_max_delay_ms,
    infinite_requests, 
    number_of_requests
) VALUES (
    1,      -- Singleton ID
    5000,   -- 5 seconds between sessions
    100,    -- 100ms request delay
    50,     -- 50ms min random delay
    100,    -- 100ms max random delay
    1,      -- Infinite requests enabled
    10      -- 10 requests when finite
);
```

---

## 🔄 Database Migrations

### Version History

#### Version 1 → 2 Migration
```sql
-- Create settings table (new in v1.1)
CREATE TABLE IF NOT EXISTS settings (
    id INTEGER PRIMARY KEY,
    time_between_requests INTEGER NOT NULL DEFAULT 5000,
    request_delay_ms INTEGER NOT NULL DEFAULT 100,
    random_min_delay_ms INTEGER NOT NULL DEFAULT 50,
    random_max_delay_ms INTEGER NOT NULL DEFAULT 100,
    infinite_requests INTEGER NOT NULL DEFAULT 1,
    number_of_requests INTEGER NOT NULL DEFAULT 10
);

-- Insert default settings
INSERT OR REPLACE INTO settings (id, time_between_requests, infinite_requests, number_of_requests) 
VALUES (1, 5000, 1, 10);
```

### Migration Strategy
- **Automatic**: Room handles migrations automatically
- **Fallback**: Destructive migration if automatic fails
- **Data preservation**: User data (servers) preserved across migrations
- **Settings migration**: Default values applied for new settings

### Future Migration Considerations
```sql
-- Potential future enhancements:
-- 1. Add server groups/categories
-- ALTER TABLE servers ADD COLUMN group_id INTEGER;

-- 2. Add historical test results
-- CREATE TABLE test_results (
--     id INTEGER PRIMARY KEY,
--     server_id INTEGER,
--     timestamp INTEGER,
--     success INTEGER,
--     response_time INTEGER,
--     FOREIGN KEY(server_id) REFERENCES servers(id)
-- );

-- 3. Add user preferences
-- CREATE TABLE preferences (
--     key TEXT PRIMARY KEY,
--     value TEXT
-- );
```

---

## 🔗 Relationships & Constraints

### Current Relationships
```
settings (1) ←→ (global) application
servers  (n) ←→ (independent) no foreign keys currently
```

### Data Integrity Rules
- **Servers**: Each server is independent
- **Settings**: Single global configuration
- **Referential integrity**: Currently no foreign key constraints
- **Cascade rules**: Not applicable (no relationships)

### Constraints Summary
```sql
-- Primary key constraints
CONSTRAINT sqlite_autoindex_servers_1 PRIMARY KEY (id)
CONSTRAINT sqlite_autoindex_settings_1 PRIMARY KEY (id)

-- Not null constraints  
CONSTRAINT servers_name_not_null CHECK (name IS NOT NULL)
CONSTRAINT servers_address_not_null CHECK (address IS NOT NULL)
CONSTRAINT servers_requestType_not_null CHECK (requestType IS NOT NULL)

-- Default value constraints (settings table)
-- Enforced at application level, not database level
```

---

## 💾 Data Access Patterns

### Room DAOs

#### ServerDao Operations
```java
// Query patterns
@Query("SELECT * FROM servers ORDER BY name ASC")
LiveData<List<Server>> getAllServers();

@Query("SELECT COUNT(*) FROM servers")
LiveData<Integer> getServerCount();

@Query("SELECT * FROM servers WHERE id = :id")
LiveData<Server> getServerById(int id);

// Modification patterns
@Insert void insert(Server server);
@Update void update(Server server);  
@Delete void delete(Server server);
@Query("DELETE FROM servers") void deleteAll();
```

#### SettingsDao Operations
```java
// Singleton access patterns
@Query("SELECT * FROM settings WHERE id = 1")
LiveData<Settings> getSettings();

@Query("SELECT * FROM settings WHERE id = 1") 
Settings getSettingsSync();

// Upsert pattern (insert or update)
@Insert(onConflict = OnConflictStrategy.REPLACE)
void insert(Settings settings);
```

### Performance Considerations
- **LiveData**: Automatic UI updates when data changes
- **Background threads**: All write operations on background threads
- **Synchronous reads**: Available for service layer (getSettingsSync)
- **Transaction batching**: Multiple operations in single transaction

---

## 📊 Database Utilities

### Export Format (JSON)
```json
{
  "servers": [
    {
      "id": 1,
      "name": "Production Web Server",
      "address": "https://www.company.com", 
      "port": null,
      "requestType": "HTTPS"
    }
  ],
  "settings": {
    "id": 1,
    "timeBetweenRequests": 5000,
    "infiniteRequests": true,
    "numberOfRequests": 10
  },
  "version": "1.1",
  "timestamp": 1694361600000
}
```

### Database Reset Procedure
```java
// Complete database reset (dangerous operation)
public void resetDatabase() {
    serverDao.deleteAll();           // Clear all servers
    settingsDao.insert(Settings.getDefault()); // Reset to defaults
}
```

### Backup Strategy
- **Export function**: JSON format for portability
- **No automatic backup**: User-initiated only
- **Cloud backup**: Not implemented (privacy by design)
- **Local backup**: Export to device storage

---

## 🛠️ Development Tools

### Database Inspection
```bash
# Access database via ADB (rooted device or emulator)
adb shell
cd /data/data/com.ltrudu.serverresponsetest/databases/
sqlite3 server_response_database

# Common inspection commands
.tables                          # List all tables
.schema servers                  # Show table structure
SELECT * FROM servers;           # View all servers
SELECT * FROM settings;          # View settings
```

### Testing Utilities
```java
// Test data factory
public class TestDataFactory {
    public static Server createTestServer() {
        return new Server("Test Server", "https://example.com", null, RequestType.HTTPS);
    }
    
    public static Settings createTestSettings() {
        return Settings.getDefault();
    }
}

// Database testing
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private AppDatabase db;
    private ServerDao serverDao;
    
    @Test
    public void insertAndRetrieveServer() {
        Server server = TestDataFactory.createTestServer();
        serverDao.insert(server);
        
        List<Server> servers = serverDao.getAllServersSync();
        assertThat(servers.size(), equalTo(1));
    }
}
```

---

## 📈 Performance Metrics

### Storage Requirements
- **Empty database**: ~50 KB
- **Per server record**: ~200 bytes average
- **100 servers**: ~70 KB total
- **Settings overhead**: Negligible

### Query Performance
- **Server list**: <1ms (typical)
- **Server count**: <1ms
- **Settings fetch**: <1ms
- **Insert/Update**: 1-5ms

### Optimization Opportunities
```sql
-- Future performance indexes
CREATE INDEX idx_servers_type ON servers(requestType);
CREATE INDEX idx_servers_name_lower ON servers(LOWER(name));

-- Potential query optimizations
-- Full-text search on server names
-- Composite indexes for complex queries
```

---

This database schema provides a solid foundation for the application's data storage needs while maintaining simplicity and performance. The design supports current features and provides flexibility for future enhancements.