# Testing Guide

This comprehensive guide covers all aspects of testing the Server Response Test application, including unit tests, integration tests, manual testing procedures, and quality assurance practices.

## 🧪 Testing Overview

### Testing Philosophy
- **Quality First**: Comprehensive testing ensures reliability
- **Automated Testing**: Unit and integration tests for core functionality
- **Manual Testing**: User experience and edge case validation
- **Continuous Integration**: Automated testing on code changes
- **Performance Testing**: Resource usage and battery optimization validation

### Test Categories
- **Unit Tests**: Individual component testing
- **Integration Tests**: Component interaction testing
- **UI Tests**: User interface automation
- **Manual Tests**: Human validation and exploratory testing
- **Performance Tests**: Resource usage and optimization
- **Compatibility Tests**: Device and Android version testing

## 🔬 Unit Testing

### Test Framework Setup

#### Dependencies
```gradle
// Unit testing dependencies
testImplementation 'junit:junit:4.13.2'
testImplementation 'mockito-core:3.12.4'
testImplementation 'androidx.arch.core:core-testing:2.2.0'
testImplementation 'androidx.room:room-testing:2.5.0'

// Android testing dependencies  
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
androidTestImplementation 'androidx.test.espresso:espresso-intents:3.5.1'
```

#### Test Configuration
```java
// Base test class
public abstract class BaseUnitTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    
    @Before
    public void setUp() {
        // Common setup
    }
    
    @After
    public void tearDown() {
        // Common cleanup
    }
}
```

### Core Component Testing

#### Server Entity Tests
```java
@RunWith(JUnit4.class)
public class ServerTest extends BaseUnitTest {
    
    @Test
    public void server_creation_with_valid_data() {
        // Given
        String name = "Test Server";
        String address = "https://example.com";
        Integer port = 8080;
        RequestType type = RequestType.HTTPS;
        
        // When
        Server server = new Server(name, address, port, type);
        
        // Then
        assertEquals(name, server.getName());
        assertEquals(address, server.getAddress());
        assertEquals(port, server.getPort());
        assertEquals(type, server.getRequestType());
    }
    
    @Test
    public void server_creation_with_null_port() {
        // Given
        Server server = new Server("Test", "https://example.com", null, RequestType.HTTPS);
        
        // Then
        assertNull(server.getPort());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void server_creation_with_invalid_data() {
        // When/Then
        new Server(null, "https://example.com", 8080, RequestType.HTTPS);
    }
}
```

#### Settings Entity Tests
```java
@RunWith(JUnit4.class)
public class SettingsTest extends BaseUnitTest {
    
    @Test
    public void settings_default_values() {
        // When
        Settings settings = Settings.getDefault();
        
        // Then
        assertEquals(5000, settings.getTimeBetweenRequests());
        assertTrue(settings.isInfiniteRequests());
        assertEquals(10, settings.getNumberOfRequests());
    }
    
    @Test
    public void settings_validation_valid_values() {
        // Given
        Settings settings = new Settings();
        settings.setTimeBetweenRequests(10000);
        settings.setNumberOfRequests(50);
        
        // When/Then
        assertTrue(settings.isValid());
    }
    
    @Test
    public void settings_validation_invalid_values() {
        // Given
        Settings settings = new Settings();
        settings.setTimeBetweenRequests(500); // Too low
        
        // When/Then
        assertFalse(settings.isValid());
    }
}
```

### Repository Testing

#### ServerRepository Tests
```java
@RunWith(AndroidJUnit4.class)
public class ServerRepositoryTest extends BaseUnitTest {
    
    private AppDatabase database;
    private ServerDao serverDao;
    private SettingsDao settingsDao;
    private ServerRepository repository;
    
    @Before
    public void setUp() {
        super.setUp();
        
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
                
        serverDao = database.serverDao();
        settingsDao = database.settingsDao();
        
        // Create mock application
        Application mockApp = mock(Application.class);
        when(mockApp.getApplicationContext()).thenReturn(context);
        
        repository = new ServerRepository(mockApp);
    }
    
    @After
    public void tearDown() {
        database.close();
        super.tearDown();
    }
    
    @Test
    public void insert_server_success() throws InterruptedException {
        // Given
        Server server = TestDataFactory.createTestServer();
        
        // When
        repository.insert(server);
        
        // Then
        LiveData<List<Server>> serversLiveData = repository.getAllServers();
        List<Server> servers = LiveDataTestUtil.getValue(serversLiveData);
        
        assertEquals(1, servers.size());
        assertEquals(server.getName(), servers.get(0).getName());
    }
    
    @Test
    public void import_servers_with_callback() throws InterruptedException {
        // Given
        List<Server> servers = TestDataFactory.createServerList(3);
        Settings settings = TestDataFactory.createTestSettings();
        CountDownLatch latch = new CountDownLatch(1);
        
        // When
        repository.importServers(servers, settings, latch::countDown);
        
        // Then
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        
        LiveData<List<Server>> resultLiveData = repository.getAllServers();
        List<Server> result = LiveDataTestUtil.getValue(resultLiveData);
        assertEquals(3, result.size());
    }
}
```

### ViewModel Testing

#### ServerViewModel Tests
```java
@RunWith(JUnit4.class)
public class ServerViewModelTest extends BaseUnitTest {
    
    private ServerRepository mockRepository;
    private ServerViewModel viewModel;
    private Application mockApplication;
    
    @Before
    public void setUp() {
        super.setUp();
        
        mockApplication = mock(Application.class);
        mockRepository = mock(ServerRepository.class);
        
        // Mock LiveData
        MutableLiveData<List<Server>> serversLiveData = new MutableLiveData<>();
        MutableLiveData<Integer> countLiveData = new MutableLiveData<>();
        MutableLiveData<Settings> settingsLiveData = new MutableLiveData<>();
        
        when(mockRepository.getAllServers()).thenReturn(serversLiveData);
        when(mockRepository.getServerCount()).thenReturn(countLiveData);
        when(mockRepository.getSettings()).thenReturn(settingsLiveData);
        
        viewModel = new ServerViewModel(mockApplication) {
            @Override
            protected ServerRepository createRepository(Application application) {
                return mockRepository;
            }
        };
    }
    
    @Test
    public void insert_server_calls_repository() {
        // Given
        Server server = TestDataFactory.createTestServer();
        
        // When
        viewModel.insert(server);
        
        // Then
        verify(mockRepository).insert(server);
    }
    
    @Test
    public void get_all_servers_returns_live_data() {
        // When
        LiveData<List<Server>> result = viewModel.getAllServers();
        
        // Then
        assertNotNull(result);
        verify(mockRepository).getAllServers();
    }
}
```

## 🔗 Integration Testing

### Database Integration Tests

#### Database Migration Tests
```java
@RunWith(AndroidJUnit4.class)
public class DatabaseMigrationTest {
    
    private static final String TEST_DB_NAME = "migration_test";
    
    @Rule
    public MigrationTestHelper migrationTestHelper = new MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase.class.getCanonicalName(),
        new FrameworkSQLiteOpenHelperFactory()
    );
    
    @Test
    public void migration_1_to_2_adds_settings_table() throws IOException {
        // Given - Create database with version 1
        SupportSQLiteDatabase dbVersion1 = migrationTestHelper.createDatabase(TEST_DB_NAME, 1);
        
        // Insert test data
        dbVersion1.execSQL("INSERT INTO servers (name, address, port, requestType) " +
                          "VALUES ('Test', 'https://example.com', NULL, 'HTTPS')");
        dbVersion1.close();
        
        // When - Migrate to version 2
        SupportSQLiteDatabase dbVersion2 = migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME, 2, true, AppDatabase.MIGRATION_1_2);
        
        // Then - Verify settings table exists
        Cursor cursor = dbVersion2.query("SELECT * FROM settings WHERE id = 1");
        assertTrue(cursor.moveToFirst());
        
        assertEquals(5000, cursor.getInt(cursor.getColumnIndex("time_between_requests")));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex("infinite_requests")));
        cursor.close();
    }
}
```

#### Service Integration Tests
```java
@RunWith(AndroidJUnit4.class)
public class ServerTestServiceTest {
    
    @Rule
    public ServiceTestRule serviceRule = new ServiceTestRule();
    
    private Context context;
    
    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }
    
    @Test
    public void service_starts_and_binds_successfully() throws TimeoutException {
        // Given
        Intent serviceIntent = new Intent(context, ServerTestService.class);
        serviceIntent.setAction(ServerTestService.ACTION_START_TEST);
        
        // When
        IBinder binder = serviceRule.bindService(serviceIntent);
        
        // Then
        assertNotNull(binder);
    }
    
    @Test
    public void service_broadcasts_test_results() throws InterruptedException {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ServerTestService.BROADCAST_TEST_RESULT.equals(intent.getAction())) {
                    latch.countDown();
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(ServerTestService.BROADCAST_TEST_RESULT);
        context.registerReceiver(receiver, filter);
        
        // When
        Intent serviceIntent = new Intent(context, ServerTestService.class);
        serviceIntent.setAction(ServerTestService.ACTION_START_TEST);
        context.startService(serviceIntent);
        
        // Then
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        context.unregisterReceiver(receiver);
    }
}
```

## 🖱️ UI Testing

### Espresso Testing Framework

#### Fragment UI Tests
```java
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestFragmentUITest {
    
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = 
        new ActivityScenarioRule<>(MainActivity.class);
    
    @Before
    public void setUp() {
        // Navigate to Test tab
        onView(withText("Test")).perform(click());
    }
    
    @Test
    public void test_tab_displays_server_count() {
        // Given - servers exist in database
        // When - view is displayed
        // Then
        onView(withId(R.id.text_server_count))
            .check(matches(isDisplayed()))
            .check(matches(withText(containsString("servers"))));
    }
    
    @Test
    public void start_test_button_changes_to_stop_when_clicked() {
        // Given
        onView(withId(R.id.button_play_stop))
            .check(matches(withText("Start Test")));
        
        // When
        onView(withId(R.id.button_play_stop)).perform(click());
        
        // Then
        onView(withId(R.id.button_play_stop))
            .check(matches(withText("Stop Test")));
    }
    
    @Test
    public void remaining_requests_display_updates_during_finite_test() {
        // Given - finite requests mode
        // When - test is started
        // Then - remaining requests should be visible and updating
        onView(withId(R.id.text_remaining_requests))
            .check(matches(isDisplayed()))
            .check(matches(withText(containsString("Remaining"))));
    }
}
```

#### Settings Fragment Tests
```java
@RunWith(AndroidJUnit4.class)
public class SettingsFragmentUITest {
    
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = 
        new ActivityScenarioRule<>(MainActivity.class);
    
    @Before
    public void setUp() {
        onView(withText("Settings")).perform(click());
    }
    
    @Test
    public void notification_status_indicator_is_visible() {
        onView(withId(R.id.layout_notification_status))
            .check(matches(isDisplayed()));
            
        onView(withId(R.id.text_notification_status))
            .check(matches(isDisplayed()));
    }
    
    @Test
    public void time_between_sessions_accepts_valid_input() {
        // Given
        String validTime = "10000";
        
        // When
        onView(withId(R.id.edit_time_between_requests))
            .perform(clearText(), typeText(validTime), closeSoftKeyboard());
        
        // Then
        onView(withId(R.id.edit_time_between_requests))
            .check(matches(withText(validTime)));
    }
    
    @Test
    public void export_import_buttons_are_functional() {
        // Test export button
        onView(withId(R.id.button_export_servers))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()));
            
        // Test import button
        onView(withId(R.id.button_import_servers))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()));
    }
}
```

### Advanced UI Testing

#### Custom Matchers
```java
public class CustomMatchers {
    
    public static Matcher<View> withBackgroundColor(int colorResId) {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(View view) {
                Context context = view.getContext();
                int expectedColor = ContextCompat.getColor(context, colorResId);
                
                if (view.getBackground() instanceof ColorDrawable) {
                    ColorDrawable drawable = (ColorDrawable) view.getBackground();
                    return drawable.getColor() == expectedColor;
                }
                return false;
            }
            
            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: " + colorResId);
            }
        };
    }
    
    public static Matcher<View> withNotificationStatus(boolean enabled) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public boolean matchesSafely(TextView textView) {
                String text = textView.getText().toString();
                return enabled ? text.contains("enabled") : text.contains("disabled");
            }
            
            @Override
            public void describeTo(Description description) {
                description.appendText("with notification status: " + (enabled ? "enabled" : "disabled"));
            }
        };
    }
}
```

## 📱 Manual Testing

### Testing Procedures

#### Pre-Testing Setup
```
Environment Preparation:
├── Clean device state (fresh install or data cleared)
├── Stable network connection (WiFi and mobile data)
├── Battery level >50% for extended testing
├── Test servers available and responding
└── Screen recording tools ready (if needed)

Test Data Preparation:
├── Valid HTTPS servers (https://google.com, https://github.com)
├── Valid IP addresses for ping (8.8.8.8, 192.168.1.1)
├── Invalid servers for error testing
├── Import/export test files
└── Different server configurations
```

#### Functional Testing Checklist

##### Server Management
- [ ] **Add Server**: All fields, validation, save/cancel
- [ ] **Edit Server**: Modify existing, validation, updates
- [ ] **Delete Server**: Swipe gesture, confirmation dialog
- [ ] **Server List**: Display, scrolling, empty state
- [ ] **Input Validation**: Invalid URLs, empty fields, special characters

##### Testing Operations  
- [ ] **Start Test**: Button state change, service start, notifications
- [ ] **Stop Test**: Service stop, notification dismissal, UI reset
- [ ] **Background Testing**: App backgrounded, notifications work
- [ ] **Pause/Resume**: Notification controls, state preservation
- [ ] **Test Results**: Real-time updates, status indicators, timing

##### Settings Management
- [ ] **Time Configuration**: Valid ranges, auto-save, persistence
- [ ] **Request Mode**: Infinite/finite toggle, counter display
- [ ] **Notification Status**: Permission check, visual indicator
- [ ] **Data Management**: Export, import, share, reset

##### Notification System
- [ ] **Permission Request**: Initial prompt, educational dialog
- [ ] **Background Service**: Persistent notification, rich content
- [ ] **Action Buttons**: Pause, resume, stop functionality
- [ ] **Status Updates**: Real-time progress, server information

#### Compatibility Testing

##### Android Version Testing
```
Test Matrix:
├── Android 7.0 (API 24): Minimum supported version
├── Android 8.0 (API 26): Notification channels
├── Android 10.0 (API 29): Scoped storage
├── Android 13.0 (API 33): Runtime notification permission
└── Android 14.0 (API 34): Target version

Key differences to test:
├── Notification permission flow (13+)
├── File access methods (10+)
├── Background restrictions (8+)
└── UI rendering differences
```

##### Device Testing
```
Device Categories:
├── Phone (various screen sizes)
├── Tablet (landscape orientation)
├── Foldable (if available)
└── Android TV (if applicable)

Performance Testing:
├── Low-end devices (2GB RAM)
├── Mid-range devices (4-6GB RAM)
├── High-end devices (8GB+ RAM)
└── Battery optimization behavior
```

### Edge Case Testing

#### Network Scenarios
```
Test different network conditions:
├── No internet connection
├── Slow connection (airplane mode toggle)
├── Connection drops during testing
├── WiFi to mobile data switching
├── VPN connections
├── Corporate firewalls/proxies
└── DNS failures
```

#### Resource Constraints
```
Stress testing scenarios:
├── Low storage space (<100MB)
├── Low memory conditions (many apps running)
├── Low battery (<10%)
├── Thermal throttling (extended testing)
├── Background app limits
└── Power saving modes
```

#### Data Scenarios
```
Data edge cases:
├── Empty database (no servers)
├── Large server lists (50+ servers)
├── Very long server names/addresses
├── Special characters in server data
├── Import corrupted JSON files
└── Settings with extreme values
```

## 🚀 Performance Testing

### Performance Test Cases

#### Battery Usage Testing
```
Test procedures:
1. Full battery charge
2. Configure test scenarios:
   - High frequency (1s intervals, 10 servers)
   - Medium frequency (5s intervals, 5 servers)
   - Low frequency (30s intervals, 3 servers)
3. Run for standardized periods (1 hour, 4 hours)
4. Measure battery drain percentage
5. Document results and optimization recommendations

Expected results:
├── High frequency: 15-25% per hour
├── Medium frequency: 5-10% per hour
└── Low frequency: 2-5% per hour
```

#### Memory Usage Testing
```
Memory monitoring:
1. Use Android Studio Profiler
2. Monitor during different operations:
   - App startup
   - Server list loading
   - Active testing
   - Background operation
   - Import/export operations
3. Check for memory leaks
4. Verify garbage collection efficiency

Expected memory usage:
├── Base app: 30-50 MB
├── With 10 servers: 60-80 MB
├── Peak usage: <100 MB
└── Memory leaks: None detected
```

#### Network Performance Testing
```
Network efficiency tests:
1. Monitor data usage per test cycle
2. Test with different server counts
3. Measure response time accuracy
4. Test timeout handling
5. Verify concurrent request handling

Data usage expectations:
├── Per HTTPS request: 1-3 KB
├── Per ping request: <0.1 KB
├── Hourly usage (5 servers, 30s): ~6 MB
└── Daily usage estimate: ~150 MB
```

## 📊 Test Automation

### Continuous Integration Testing

#### GitHub Actions Configuration
```yaml
name: Android CI

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'adopt'
        
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: ~/.gradle/caches
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle') }}
        restore-keys: ${{ runner.os }}-gradle
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Run unit tests
      run: ./gradlew test
      
    - name: Run lint
      run: ./gradlew lint
      
    - name: Build APK
      run: ./gradlew assembleDebug
```

#### Automated Test Reports
```bash
# Generate test reports
./gradlew test --continue

# Test report locations:
# app/build/reports/tests/testDebugUnitTest/index.html
# app/build/reports/androidTests/connected/index.html

# Coverage reports (if configured):
# app/build/reports/coverage/debug/index.html
```

### Test Data Management

#### Test Data Factory
```java
public class TestDataFactory {
    
    public static Server createTestServer() {
        return createTestServer("Test Server", "https://example.com");
    }
    
    public static Server createTestServer(String name, String address) {
        return new Server(name, address, null, RequestType.HTTPS);
    }
    
    public static Server createPingServer(String name, String address) {
        return new Server(name, address, null, RequestType.PING);
    }
    
    public static List<Server> createServerList(int count) {
        List<Server> servers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            servers.add(createTestServer("Server " + i, "https://example" + i + ".com"));
        }
        return servers;
    }
    
    public static Settings createTestSettings() {
        Settings settings = new Settings();
        settings.setTimeBetweenRequests(5000);
        settings.setInfiniteRequests(true);
        settings.setNumberOfRequests(10);
        return settings;
    }
}
```

## 📋 Test Documentation

### Test Case Documentation

#### Test Case Template
```
Test Case ID: TC_001
Test Case Name: Add New HTTPS Server
Pre-conditions: App installed, Server List tab open
Test Steps:
1. Tap "+" button
2. Enter server name: "Test Server"
3. Enter address: "https://google.com"
4. Leave port empty
5. Select "HTTPS" type
6. Tap "Save"

Expected Results:
- Server appears in list
- Shows correct name and address
- Can be edited by tapping
- Available for testing

Post-conditions: Server saved in database
Priority: High
Test Type: Manual/Automated
```

### Bug Report Template
```
Bug ID: BUG_001
Summary: Import shows success but data not visible
Severity: Medium
Priority: High

Environment:
- Device: Samsung Galaxy S21
- Android: 12
- App Version: 1.0

Steps to Reproduce:
1. Export server configuration
2. Reset database
3. Import same configuration
4. Check server list

Expected: Servers appear in list
Actual: List remains empty, but toast shows "Import successful"

Workaround: Import file twice
Status: Fixed in v1.1
```

---

This comprehensive testing guide ensures the Server Response Test application maintains high quality, reliability, and performance across all supported devices and Android versions. Regular execution of these test procedures helps identify issues early and maintains user satisfaction.