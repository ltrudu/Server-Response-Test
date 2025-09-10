# API Reference

This document provides comprehensive code documentation for the Server Response Test application, including class references, method signatures, and implementation details.

## 📦 Package Structure

```
com.ltrudu.serverresponsetest/
├── 📁 data/           # Data models and database entities
├── 📁 repository/     # Data access layer
├── 📁 viewmodel/      # UI state management
├── 📁 fragment/       # UI fragments
├── 📁 adapter/        # RecyclerView adapters
├── 📁 service/        # Background services
└── 📁 ui/            # Custom UI components
```

## 🗄️ Data Layer

### Server Entity

**File**: `data/Server.java`

```java
@Entity(tableName = "servers")
public class Server {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String name;
    
    @NonNull
    private String address;
    
    private Integer port;
    
    @NonNull
    private RequestType requestType;
    
    // Constructors, getters, setters...
}
```

#### Properties
- **id**: `int` - Auto-generated primary key
- **name**: `String` - Server display name (required)
- **address**: `String` - Server URL or IP address (required)
- **port**: `Integer` - Port number (optional, nullable)
- **requestType**: `RequestType` - Test type (HTTPS or PING)

#### Methods
```java
// Constructors
public Server(String name, String address, Integer port, RequestType requestType)
public Server() // Default constructor for Room

// Standard getters and setters
public int getId()
public void setId(int id)
public String getName()
public void setName(String name)
// ... additional getters/setters
```

### Settings Entity

**File**: `data/Settings.java`

```java
@Entity(tableName = "settings")
public class Settings {
    @PrimaryKey
    private int id = 1; // Singleton pattern
    
    private int timeBetweenRequests = 5000; // milliseconds
    private int requestDelayMs = 100;
    private int randomMinDelayMs = 50;
    private int randomMaxDelayMs = 100;
    private boolean infiniteRequests = true;
    private int numberOfRequests = 10;
    
    // Getters, setters, and static factory methods...
}
```

#### Key Methods
```java
// Factory method for default settings
public static Settings getDefault()

// Validation methods
public boolean isValid()
public void validateAndFix()
```

### RequestType Enum

**File**: `data/RequestType.java`

```java
public enum RequestType {
    HTTPS("HTTPS"),
    PING("PING");
    
    private final String value;
    
    RequestType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static RequestType fromString(String value) {
        // Implementation for string conversion
    }
}
```

### Data Access Objects (DAOs)

#### ServerDao

**File**: `data/ServerDao.java`

```java
@Dao
public interface ServerDao {
    @Query("SELECT * FROM servers ORDER BY name ASC")
    LiveData<List<Server>> getAllServers();
    
    @Query("SELECT * FROM servers WHERE id = :id")
    LiveData<Server> getServerById(int id);
    
    @Insert
    void insert(Server server);
    
    @Update
    void update(Server server);
    
    @Delete
    void delete(Server server);
    
    @Query("DELETE FROM servers")
    void deleteAll();
    
    @Query("SELECT COUNT(*) FROM servers")
    LiveData<Integer> getServerCount();
}
```

#### SettingsDao

**File**: `data/SettingsDao.java`

```java
@Dao
public interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    LiveData<Settings> getSettings();
    
    @Query("SELECT * FROM settings WHERE id = 1")
    Settings getSettingsSync();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Settings settings);
    
    @Update
    void update(Settings settings);
}
```

### AppDatabase

**File**: `data/AppDatabase.java`

```java
@Database(
    entities = {Server.class, Settings.class},
    version = 2,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract ServerDao serverDao();
    public abstract SettingsDao settingsDao();
    
    private static volatile AppDatabase INSTANCE;
    
    public static AppDatabase getDatabase(final Context context) {
        // Singleton implementation with thread safety
    }
    
    // Migration strategies
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Migration logic for version 1 to 2
        }
    };
}
```

## 🏪 Repository Layer

### ServerRepository

**File**: `repository/ServerRepository.java`

```java
public class ServerRepository {
    private ServerDao serverDao;
    private SettingsDao settingsDao;
    private LiveData<List<Server>> allServers;
    private LiveData<Settings> settings;
    
    public ServerRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        serverDao = db.serverDao();
        settingsDao = db.settingsDao();
        allServers = serverDao.getAllServers();
        settings = settingsDao.getSettings();
    }
    
    // Server operations
    public LiveData<List<Server>> getAllServers() { return allServers; }
    public LiveData<Integer> getServerCount() { return serverDao.getServerCount(); }
    public void insert(Server server) { /* Background thread implementation */ }
    public void update(Server server) { /* Background thread implementation */ }
    public void delete(Server server) { /* Background thread implementation */ }
    public void deleteAll() { /* Background thread implementation */ }
    
    // Settings operations
    public LiveData<Settings> getSettings() { return settings; }
    public Settings getSettingsSync() { return settingsDao.getSettingsSync(); }
    public void updateSettings(Settings settings) { /* Background thread implementation */ }
    
    // Import/Export operations
    public void importServers(List<Server> servers, Settings settings, Runnable onComplete)
    public List<Server> exportServers()
}
```

#### Key Methods

```java
// Asynchronous server operations
public void insert(Server server) {
    AppDatabase.databaseWriteExecutor.execute(() -> {
        serverDao.insert(server);
    });
}

// Synchronous settings access (for service)
public Settings getSettingsSync() {
    return settingsDao.getSettingsSync();
}

// Import with callback for synchronization
public void importServers(List<Server> servers, Settings settings, Runnable onComplete) {
    AppDatabase.databaseWriteExecutor.execute(() -> {
        // Clear existing data
        serverDao.deleteAll();
        // Insert new servers
        for (Server server : servers) {
            serverDao.insert(server);
        }
        // Update settings
        settingsDao.insert(settings);
        // Notify completion
        if (onComplete != null) {
            onComplete.run();
        }
    });
}
```

## 🧠 ViewModel Layer

### ServerViewModel

**File**: `viewmodel/ServerViewModel.java`

```java
public class ServerViewModel extends AndroidViewModel {
    private ServerRepository repository;
    private LiveData<List<Server>> allServers;
    private LiveData<Integer> serverCount;
    private LiveData<Settings> settings;
    
    public ServerViewModel(Application application) {
        super(application);
        repository = new ServerRepository(application);
        allServers = repository.getAllServers();
        serverCount = repository.getServerCount();
        settings = repository.getSettings();
    }
    
    // Server operations
    public LiveData<List<Server>> getAllServers() { return allServers; }
    public LiveData<Integer> getServerCount() { return serverCount; }
    public void insert(Server server) { repository.insert(server); }
    public void update(Server server) { repository.update(server); }
    public void delete(Server server) { repository.delete(server); }
    public void deleteAll() { repository.deleteAll(); }
    
    // Settings operations
    public LiveData<Settings> getSettings() { return settings; }
    public void updateSettings(Settings settings) { repository.updateSettings(settings); }
    
    // Import/Export operations
    public void importServers(List<Server> servers, Settings settings, Runnable onComplete) {
        repository.importServers(servers, settings, onComplete);
    }
    
    public List<Server> exportServers() { return repository.exportServers(); }
}
```

## 📱 UI Layer - Fragments

### TestFragment

**File**: `fragment/TestFragment.java`

```java
public class TestFragment extends Fragment {
    // UI Components
    private TextView serverCountText;
    private TextView remainingRequestsText;
    private Button playStopButton;
    private RecyclerView serverResultsRecyclerView;
    
    // Data and Adapters
    private ServerViewModel viewModel;
    private ServerResultAdapter adapter;
    private List<Server> servers;
    private boolean isTestRunning = false;
    
    // Broadcast receiver for service communication
    private BroadcastReceiver testResultReceiver;
    private BroadcastReceiver requestProgressReceiver;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment initialization
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Setup UI components and observers
        setupRecyclerView();
        setupObservers();
        setupBroadcastReceivers();
    }
    
    // Core functionality methods
    private void startTest() {
        // Permission checks and service start logic
    }
    
    private void stopTest() {
        // Service stop logic
    }
    
    private void handleTestResult(Intent intent) {
        // Process broadcast results from service
    }
    
    private void handleRequestProgress(Intent intent) {
        // Update remaining requests counter
    }
    
    private void updateRemainingRequestsDisplay(int remaining, int total) {
        // UI update for progress display
    }
}
```

### ServerListFragment

**File**: `fragment/ServerListFragment.java`

```java
public class ServerListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ServerAdapter adapter;
    private ServerViewModel viewModel;
    private FloatingActionButton addButton;
    
    // Dialog management
    private AlertDialog currentDialog;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment setup
    }
    
    // Server management methods
    private void showAddEditServerDialog(Server server) {
        // Server configuration dialog
    }
    
    private void saveServer(Server server) {
        // Validation and save logic
    }
    
    private boolean validateInput(String name, String address, String port) {
        // Input validation logic
    }
    
    private void showDeleteConfirmationDialog(Server server, int position) {
        // Deletion confirmation dialog
    }
    
    // UI event handlers
    private void onServerClick(Server server, int position) {
        // Edit server handler
    }
    
    private void onServerSwipeLeft(int position) {
        // Delete server handler
    }
}
```

### SettingsFragment

**File**: `fragment/SettingsFragment.java`

```java
public class SettingsFragment extends Fragment {
    // UI Components
    private EditText timeBetweenRequestsEdit;
    private Switch infiniteRequestsSwitch;
    private EditText numberOfRequestsEdit;
    private TextView notificationStatusText;
    private LinearLayout notificationStatusLayout;
    
    // Data
    private ServerViewModel viewModel;
    private Settings currentSettings;
    
    // Text watchers for auto-save
    private TextWatcher timeBetweenRequestsWatcher;
    private TextWatcher numberOfRequestsWatcher;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment initialization
    }
    
    // Settings management
    private void saveSettings() {
        // Auto-save settings logic
    }
    
    private void setupTextWatchers() {
        // Debounced text watchers for performance
    }
    
    // Data management methods
    private void exportServers() {
        // Export to JSON file
    }
    
    private void importServersFromUri(Uri uri) {
        // Import from selected file
    }
    
    private void shareServers() {
        // Share via Android sharing system
    }
    
    // Notification permission management
    private void updateNotificationStatus() {
        // Check and display notification permission status
    }
    
    private void handleNotificationStatusClick() {
        // Open notification settings if needed
    }
}
```

## ⚙️ Service Layer

### ServerTestService

**File**: `service/ServerTestService.java`

```java
public class ServerTestService extends Service {
    // Service constants
    public static final String ACTION_START_TEST = "START_TEST";
    public static final String ACTION_STOP_TEST = "STOP_TEST";
    public static final String ACTION_PAUSE_TEST = "PAUSE_TEST";
    public static final String ACTION_RESUME_TEST = "RESUME_TEST";
    
    // Broadcast actions
    public static final String BROADCAST_TEST_RESULT = "TEST_RESULT";
    public static final String BROADCAST_REQUEST_PROGRESS = "REQUEST_PROGRESS";
    
    // Service state
    private boolean isRunning = false;
    private boolean isPaused = false;
    private List<Server> servers;
    private Settings settings;
    
    // Threading
    private ExecutorService executorService;
    private Handler mainHandler;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle service commands and start foreground
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Started service, not bound
    }
    
    // Core testing methods
    private void startTesting() {
        // Initialize and start testing cycle
    }
    
    private void stopTesting() {
        // Stop testing and cleanup
    }
    
    private void pauseTesting() {
        // Pause without stopping service
    }
    
    private void resumeTesting() {
        // Resume paused testing
    }
    
    // Testing implementation
    private void testServers() {
        // Execute tests for all servers
    }
    
    private void testHttpServer(Server server) {
        // HTTPS request implementation
    }
    
    private void testPingServer(Server server) {
        // Ping implementation using InetAddress.isReachable()
    }
    
    // Notification management
    private void createNotificationChannel() {
        // Android 8+ notification channel setup
    }
    
    private void updateNotification(String status, String details) {
        // Update foreground service notification
    }
    
    // Broadcasting
    private void broadcastTestResult(Server server, boolean success, long responseTime, String error) {
        // Send test results to UI
    }
    
    private void broadcastRequestProgress(int remaining, int total) {
        // Send progress updates to UI
    }
}
```

#### Key Service Methods

```java
// HTTPS testing implementation
private void testHttpServer(Server server) {
    try {
        String url = server.getAddress();
        if (server.getPort() != null) {
            url += ":" + server.getPort();
        }
        
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        long startTime = System.currentTimeMillis();
        int responseCode = connection.getResponseCode();
        long responseTime = System.currentTimeMillis() - startTime;
        
        boolean success = (responseCode >= 200 && responseCode < 300);
        broadcastTestResult(server, success, responseTime, null);
        
    } catch (Exception e) {
        broadcastTestResult(server, false, 0, e.getMessage());
    }
}

// Ping testing implementation
private void testPingServer(Server server) {
    try {
        String address = server.getAddress().replaceAll("https?://", "");
        InetAddress inet = InetAddress.getByName(address);
        
        long startTime = System.currentTimeMillis();
        boolean reachable = inet.isReachable(5000);
        long responseTime = System.currentTimeMillis() - startTime;
        
        broadcastTestResult(server, reachable, responseTime, null);
        
    } catch (Exception e) {
        broadcastTestResult(server, false, 0, e.getMessage());
    }
}
```

## 🎨 UI Components

### ServerAdapter

**File**: `adapter/ServerAdapter.java`

```java
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {
    private List<Server> servers;
    private OnServerClickListener clickListener;
    private OnServerSwipeListener swipeListener;
    
    public interface OnServerClickListener {
        void onServerClick(Server server, int position);
    }
    
    public interface OnServerSwipeListener {
        void onServerSwipeLeft(int position);
    }
    
    public static class ServerViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView addressText;
        TextView typeText;
        
        public ServerViewHolder(@NonNull View itemView) {
            // ViewHolder initialization
        }
        
        public void bind(Server server) {
            // Bind server data to views
        }
    }
    
    // Adapter methods
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ViewHolder creation
    }
    
    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        // Data binding
    }
    
    @Override
    public int getItemCount() {
        return servers != null ? servers.size() : 0;
    }
    
    public void setServers(List<Server> servers) {
        // Update server list with DiffUtil for efficiency
    }
}
```

### ServerResultAdapter

**File**: `adapter/ServerResultAdapter.java`

```java
public class ServerResultAdapter extends RecyclerView.Adapter<ServerResultAdapter.ResultViewHolder> {
    private List<Server> servers;
    private Map<Integer, TestResult> results;
    
    public static class TestResult {
        boolean success;
        long responseTime;
        String error;
        long timestamp;
    }
    
    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView statusText;
        TextView responseTimeText;
        View statusIndicator;
        
        public void bind(Server server, TestResult result) {
            // Bind server and result data
            // Update status indicator color
            // Format response time display
        }
    }
    
    public void updateResult(int serverId, boolean success, long responseTime, String error) {
        // Update specific server result
        // Notify adapter of changes
    }
    
    public void clearResults() {
        // Clear all test results
    }
}
```

## 🔧 Utility Classes

### Type Converters

**File**: `data/Converters.java`

```java
public class Converters {
    @TypeConverter
    public static String fromRequestType(RequestType requestType) {
        return requestType != null ? requestType.getValue() : null;
    }
    
    @TypeConverter
    public static RequestType toRequestType(String value) {
        return value != null ? RequestType.fromString(value) : null;
    }
}
```

### JSON Data Models

**File**: `data/ExportData.java`

```java
public class ExportData {
    private List<Server> servers;
    private Settings settings;
    private String version;
    private long timestamp;
    
    // Constructors, getters, setters
    
    public static ExportData fromJson(String json) throws JsonException {
        // JSON deserialization logic
    }
    
    public String toJson() {
        // JSON serialization logic
    }
}
```

## 🧪 Testing Utilities

### Test Data Factories

```java
public class TestDataFactory {
    public static Server createTestServer(String name, String address) {
        return new Server(name, address, null, RequestType.HTTPS);
    }
    
    public static Settings createTestSettings() {
        return Settings.getDefault();
    }
    
    public static List<Server> createServerList(int count) {
        // Generate test server list
    }
}
```

### Mock Repository

```java
public class MockServerRepository extends ServerRepository {
    private MutableLiveData<List<Server>> servers = new MutableLiveData<>();
    private MutableLiveData<Settings> settings = new MutableLiveData<>();
    
    @Override
    public LiveData<List<Server>> getAllServers() {
        return servers;
    }
    
    // Override other methods for testing
}
```

## 📋 Constants and Configuration

### Application Constants

```java
public class Constants {
    // Database
    public static final String DATABASE_NAME = "server_response_database";
    public static final int DATABASE_VERSION = 2;
    
    // Service
    public static final int NOTIFICATION_ID = 1001;
    public static final String NOTIFICATION_CHANNEL_ID = "server_test_channel";
    
    // Timeouts
    public static final int HTTP_CONNECT_TIMEOUT = 5000;
    public static final int HTTP_READ_TIMEOUT = 5000;
    public static final int PING_TIMEOUT = 5000;
    
    // Limits
    public static final int MAX_SERVERS = 100;
    public static final int MIN_TIME_BETWEEN_REQUESTS = 1000;
    public static final int MAX_TIME_BETWEEN_REQUESTS = 999999;
}
```

---

This API reference provides a comprehensive overview of the codebase structure and key implementation details. For specific implementation questions or examples, refer to the source code or contact the development team.