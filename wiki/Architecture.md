# Architecture Overview

This document provides a comprehensive overview of the Server Response Test application architecture, design patterns, and technical implementation details.

## 🏗️ Application Architecture

### MVVM Architecture Pattern

The application follows the **Model-View-ViewModel (MVVM)** architectural pattern, providing clear separation of concerns and testable code:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│      View       │◄──►│   ViewModel     │◄──►│     Model       │
│   (Fragments)   │    │   (Business     │    │  (Repository    │
│                 │    │     Logic)      │    │   & Database)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Core Components

#### 📱 Presentation Layer (UI)
- **MainActivity**: Main container with ViewPager2 and TabLayout
- **TestFragment**: Real-time test execution and monitoring
- **ServerListFragment**: Server configuration management (CRUD operations)
- **SettingsFragment**: Application settings and data management

#### 🧠 Business Logic Layer (ViewModels)
- **ServerViewModel**: Manages server data and business logic
- **LiveData**: Reactive data binding for UI updates
- **Repository Pattern**: Clean data access abstraction

#### 💾 Data Layer
- **Room Database**: Local SQLite database with type-safe access
- **ServerRepository**: Data access coordination and caching
- **SharedPreferences**: Application settings persistence

#### ⚙️ Service Layer
- **ServerTestService**: Background foreground service for testing operations
- **Notification System**: Rich notifications with action controls
- **Broadcast Receivers**: Inter-component communication

## 🗄️ Database Design

### Room Database Schema

#### Servers Table
```sql
CREATE TABLE servers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    port INTEGER,
    requestType TEXT NOT NULL  -- 'HTTP' or 'PING'
);
```

#### Settings Table (v1.1+)
```sql
CREATE TABLE settings (
    id INTEGER PRIMARY KEY,
    time_between_requests INTEGER NOT NULL DEFAULT 5000,  -- milliseconds
    request_delay_ms INTEGER NOT NULL DEFAULT 100,
    random_min_delay_ms INTEGER NOT NULL DEFAULT 50,
    random_max_delay_ms INTEGER NOT NULL DEFAULT 100,
    infinite_requests INTEGER NOT NULL DEFAULT 1,
    number_of_requests INTEGER NOT NULL DEFAULT 10
);
```

### Type Converters
- **RequestType Enum**: Converts between enum and string storage
- **Thread-Safe Operations**: All database operations on background threads

## 🎯 Design Patterns Implementation

### 1. Repository Pattern
**Purpose**: Centralized data access with clean API

```java
public class ServerRepository {
    private ServerDao serverDao;
    private LiveData<List<Server>> allServers;
    
    // Clean API for ViewModels
    public LiveData<List<Server>> getAllServers();
    public void insert(Server server);
    public void update(Server server);
    public void delete(Server server);
}
```

### 2. Observer Pattern
**Purpose**: Reactive UI updates with LiveData

```java
// ViewModel exposes LiveData
public LiveData<List<Server>> getAllServers() {
    return repository.getAllServers();
}

// Fragment observes changes
viewModel.getAllServers().observe(this, servers -> {
    adapter.setServers(servers);
});
```

### 3. Singleton Pattern
**Purpose**: Single instance of database and repository

```java
@Database(entities = {Server.class, Settings.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(/* ... */).build();
                }
            }
        }
        return INSTANCE;
    }
}
```

### 4. Service Locator Pattern
**Purpose**: Dependency injection for repositories

```java
public class ServerViewModel extends AndroidViewModel {
    private ServerRepository repository;
    
    public ServerViewModel(Application application) {
        super(application);
        repository = new ServerRepository(application);
    }
}
```

## 🔄 Data Flow Architecture

### 1. User Input Flow
```
User Action → Fragment → ViewModel → Repository → Database
                ↓
        UI Update ← LiveData ← Repository ← Database Response
```

### 2. Background Testing Flow
```
User Start → TestFragment → ServerTestService → HTTP/Ping Tests
                                    ↓
Notification ← Broadcast ← TestFragment ← Results
```

### 3. Settings Management Flow
```
User Input → SettingsFragment → SharedPreferences/Database
                ↓
Auto-Save → Debounced TextWatcher → Immediate Persistence
```

## 📱 UI Architecture

### Material Design 3 Implementation

#### Theme System
- **Dynamic Colors**: Adapts to system theme (light/dark)
- **Color Schemes**: Material You color system
- **Typography**: Roboto font family with proper scaling

#### Component Usage
- **Cards**: Server list items with elevation
- **TextInputLayouts**: Material text fields with validation
- **Buttons**: Material buttons with appropriate styles
- **RecyclerView**: Efficient list management with DiffUtil

### Navigation Architecture
```
MainActivity (ViewPager2)
├── TestFragment (Tab 0)
├── ServerListFragment (Tab 1)
└── SettingsFragment (Tab 2)
```

#### Fragment Lifecycle Management
- **Proper State Management**: ViewModel survives configuration changes
- **Memory Leak Prevention**: Lifecycle-aware components
- **Resource Cleanup**: onDestroy() and onDetach() implementation

## ⚙️ Service Architecture

### ServerTestService (Foreground Service)

#### Service Design
```java
public class ServerTestService extends Service {
    // Foreground service with notification
    // Concurrent server testing
    // Real-time result broadcasting
    // Pause/Resume functionality
}
```

#### Notification System
- **Rich Notifications**: Status, progress, and action buttons
- **Action Buttons**: Pause, Resume, Stop controls
- **Real-time Updates**: Progress and current server display
- **Channel Management**: Proper notification channel setup

#### Threading Model
```
Main Thread (Service)
├── UI Thread (Broadcasts)
├── Worker Thread Pool (HTTP Tests)
└── Single Thread (Ping Tests)
```

## 🔧 Performance Optimizations

### Database Optimizations
- **Background Operations**: All DB operations on background threads
- **Efficient Queries**: Optimized SQL with proper indexing
- **Connection Pooling**: Room handles connection management
- **Transaction Batching**: Multiple operations in single transaction

### UI Performance
- **RecyclerView**: DiffUtil for efficient list updates
- **ViewBinding**: Type-safe view references
- **Debounced TextWatchers**: Prevents excessive database calls
- **Lifecycle Awareness**: Prevents memory leaks

### Memory Management
- **WeakReferences**: For callback handlers
- **Proper Cleanup**: onDestroy() implementations
- **Resource Management**: Close streams and connections
- **Context Awareness**: Application vs Activity context usage

## 🔐 Security Considerations

### Network Security
- **HTTPS Only**: Enforced secure connections
- **Certificate Validation**: Proper SSL/TLS handling
- **Timeout Management**: Prevents hanging connections
- **Error Handling**: Secure error message display

### Data Security
- **Local Storage**: SQLite database (no external storage of sensitive data)
- **Input Validation**: Server configuration validation
- **SQL Injection Prevention**: Room parameterized queries
- **No Sensitive Data Logging**: Careful logging implementation

## 🌍 Internationalization Architecture

### Resource Management
```
res/
├── values/strings.xml (English - default)
├── values-fr/strings.xml (French)
└── values-{locale}/strings.xml (Future languages)
```

### Implementation Strategy
- **Externalized Strings**: All user-facing text in resources
- **Dynamic Text Sizing**: Handles longer translations
- **RTL Support**: Ready for right-to-left languages
- **Pluralization**: Proper plural form handling

## 🔄 State Management

### Application State
- **ViewModel State**: Survives configuration changes
- **Database State**: Persistent across app restarts
- **SharedPreferences**: Settings and user preferences
- **Service State**: Background testing state persistence

### Background State Management
- **Service Persistence**: Foreground service maintains state
- **Notification State**: Reflects current testing status
- **Broadcast Communication**: Real-time state updates
- **Recovery Mechanisms**: Service restart handling

## 🧪 Testing Architecture

### Unit Testing Strategy
- **ViewModel Testing**: Business logic validation
- **Repository Testing**: Data access layer testing
- **Database Testing**: Room database testing with in-memory DB
- **Utility Testing**: Helper function validation

### Integration Testing
- **Service Testing**: Background service functionality
- **Fragment Testing**: UI component testing
- **Database Integration**: Full database operation testing
- **Notification Testing**: System integration testing

## 🚀 Build Architecture

### Gradle Configuration
- **Module Structure**: Single module application
- **Dependency Management**: Version catalogs for consistency
- **Build Variants**: Debug and release configurations
- **ProGuard**: Code obfuscation for release builds

### CI/CD Considerations
- **Automated Testing**: Unit and integration test execution
- **APK Generation**: Automated build artifacts
- **Code Quality**: Lint checks and static analysis
- **Version Management**: Semantic versioning implementation

---

This architecture provides a solid foundation for maintainable, scalable, and testable Android application development while following modern Android development best practices.