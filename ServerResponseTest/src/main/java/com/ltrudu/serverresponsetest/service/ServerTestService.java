package com.ltrudu.serverresponsetest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.ltrudu.serverresponsetest.R;
import com.ltrudu.serverresponsetest.data.Server;
import com.ltrudu.serverresponsetest.repository.ServerRepository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerTestService extends Service {
    
    private static final String TAG = "ServerTestService";
    private static final String CHANNEL_ID = "ServerTestChannel";
    private static final int NOTIFICATION_ID = 1;
    
    public static final String ACTION_TEST_RESULT = "com.ltrudu.serverresponsetest.TEST_RESULT";
    public static final String ACTION_TEST_STARTED = "com.ltrudu.serverresponsetest.TEST_STARTED";
    public static final String ACTION_TEST_STOPPED = "com.ltrudu.serverresponsetest.TEST_STOPPED";
    public static final String ACTION_SERVER_TESTING = "com.ltrudu.serverresponsetest.SERVER_TESTING";
    public static final String ACTION_REQUEST_PROGRESS = "com.ltrudu.serverresponsetest.REQUEST_PROGRESS";
    public static final String ACTION_STOP_SERVICE = "com.ltrudu.serverresponsetest.STOP_SERVICE";
    public static final String ACTION_PAUSE_SERVICE = "com.ltrudu.serverresponsetest.PAUSE_SERVICE";
    public static final String ACTION_RESUME_SERVICE = "com.ltrudu.serverresponsetest.RESUME_SERVICE";
    
    public static final String EXTRA_SERVER_ID = "server_id";
    public static final String EXTRA_SERVER_NAME = "server_name";
    public static final String EXTRA_SUCCESS = "success";
    public static final String EXTRA_ERROR_MESSAGE = "error_message";
    public static final String EXTRA_RESPONSE_TIME = "response_time";
    
    public static final String EXTRA_TIME_BETWEEN_REQUESTS = "time_between_requests";
    public static final String EXTRA_REQUEST_DELAY_MS = "request_delay_ms";
    public static final String EXTRA_RANDOM_MIN_DELAY_MS = "random_min_delay_ms";
    public static final String EXTRA_RANDOM_MAX_DELAY_MS = "random_max_delay_ms";
    public static final String EXTRA_INFINITE_REQUESTS = "infinite_requests";
    public static final String EXTRA_NUMBER_OF_REQUESTS = "number_of_requests";
    public static final String EXTRA_CURRENT_REQUEST = "current_request";
    public static final String EXTRA_TOTAL_REQUESTS = "total_requests";
    
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicBoolean isPaused = new AtomicBoolean(false);
    private ExecutorService executorService;
    private Future<?> testTask;
    private ServerRepository serverRepository;
    private LocalBroadcastManager localBroadcastManager;
    private Random random = new Random();
    private NotificationManager notificationManager;
    
    // Notification state tracking
    private String currentServerName = "";
    private int totalServers = 0;
    private int currentServerIndex = 0;
    private int requestCount = 0;
    
    private int timeBetweenRequests = 5000;
    private int requestDelayMs = 100;
    private int randomMinDelayMs = 50;
    private int randomMaxDelayMs = 100;
    private boolean infiniteRequests = true;
    private int numberOfRequests = 10;
    
    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newFixedThreadPool(4);
        serverRepository = new ServerRepository(getApplication());
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            
            if (ACTION_STOP_SERVICE.equals(action)) {
                stopTesting();
                return START_NOT_STICKY;
            } else if (ACTION_PAUSE_SERVICE.equals(action)) {
                pauseTesting();
                return START_NOT_STICKY;
            } else if (ACTION_RESUME_SERVICE.equals(action)) {
                resumeTesting();
                return START_NOT_STICKY;
            }
            
            // Handle start command with settings
            timeBetweenRequests = intent.getIntExtra(EXTRA_TIME_BETWEEN_REQUESTS, 5000);
            requestDelayMs = intent.getIntExtra(EXTRA_REQUEST_DELAY_MS, 100);
            randomMinDelayMs = intent.getIntExtra(EXTRA_RANDOM_MIN_DELAY_MS, 50);
            randomMaxDelayMs = intent.getIntExtra(EXTRA_RANDOM_MAX_DELAY_MS, 100);
            infiniteRequests = intent.getBooleanExtra(EXTRA_INFINITE_REQUESTS, true);
            numberOfRequests = intent.getIntExtra(EXTRA_NUMBER_OF_REQUESTS, 10);
            
            startForegroundService();
            startTesting();
        }
        return START_NOT_STICKY;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Server Test Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Running server load tests");
            channel.setShowBadge(false);
            channel.setSound(null, null);
            channel.enableVibration(false);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private void startForegroundService() {
        updateNotification("Initializing...", false);
    }
    
    private void updateNotification(String status, boolean pausedState) {
        // Create main app intent
        Intent mainIntent = new Intent(this, com.ltrudu.serverresponsetest.MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Create action intents
        Intent stopIntent = new Intent(this, ServerTestService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        Intent pauseResumeIntent = new Intent(this, ServerTestService.class);
        pauseResumeIntent.setAction(pausedState ? ACTION_RESUME_SERVICE : ACTION_PAUSE_SERVICE);
        PendingIntent pauseResumePendingIntent = PendingIntent.getService(this, 1, pauseResumeIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Build notification content
        String title = pausedState ? "Server Load Test - Paused" : "Server Load Test - Running";
        String content;
        if (totalServers > 0) {
            if (infiniteRequests) {
                content = String.format("Testing %s (%d/%d) - Request #%d", 
                        currentServerName, currentServerIndex + 1, totalServers, requestCount + 1);
            } else {
                content = String.format("Testing %s (%d/%d) - %d/%d requests", 
                        currentServerName, currentServerIndex + 1, totalServers, requestCount + 1, numberOfRequests);
            }
        } else {
            content = status;
        }
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification_server_test)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(mainPendingIntent)
                .addAction(pausedState ? R.drawable.ic_play_24 : R.drawable.ic_pause_24, 
                           pausedState ? "Resume" : "Pause", pauseResumePendingIntent)
                .addAction(R.drawable.ic_stop_24, "Stop", stopPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .build();
        
        if (isRunning.get() || isPaused.get()) {
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
            if (!isPaused.get() && isRunning.get()) {
                startForeground(NOTIFICATION_ID, notification);
            }
        }
    }
    
    private void startTesting() {
        if (isRunning.compareAndSet(false, true)) {
            isPaused.set(false);
            broadcastTestStarted();
            
            testTask = executorService.submit(() -> {
                List<Server> servers = serverRepository.getAllServersSync();
                
                if (servers == null || servers.isEmpty()) {
                    Log.w(TAG, "No servers to test");
                    updateNotification("No servers configured", false);
                    stopTesting();
                    return;
                }
                
                totalServers = servers.size();
                requestCount = 0;
                
                updateNotification("Starting tests...", false);
                
                // Broadcast initial request progress for finite mode
                if (!infiniteRequests) {
                    broadcastRequestProgress();
                }
                
                while (isRunning.get() && (infiniteRequests || requestCount < numberOfRequests)) {
                    // Wait while paused
                    while (isPaused.get() && isRunning.get()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    
                    if (!isRunning.get()) break;
                    
                    for (int i = 0; i < servers.size(); i++) {
                        if (!isRunning.get()) {
                            break;
                        }
                        
                        // Wait while paused
                        while (isPaused.get() && isRunning.get()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        
                        if (!isRunning.get()) break;
                        
                        currentServerIndex = i;
                        Server server = servers.get(i);
                        currentServerName = server.getName();
                        
                        // Update notification with current server
                        updateNotification("", isPaused.get());
                        
                        testServer(server);
                        
                        // Add delay between individual server requests (only if there are multiple servers)
                        if (servers.size() > 1 && i < servers.size() - 1 && isRunning.get() && !isPaused.get()) {
                            try {
                                // Calculate total delay: base delay + random delay
                                int totalDelay = requestDelayMs;
                                if (randomMaxDelayMs > randomMinDelayMs) {
                                    int randomDelay = randomMinDelayMs + random.nextInt(randomMaxDelayMs - randomMinDelayMs + 1);
                                    totalDelay += randomDelay;
                                } else if (randomMinDelayMs > 0) {
                                    totalDelay += randomMinDelayMs;
                                }
                                
                                if (totalDelay > 0) {
                                    Thread.sleep(totalDelay);
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                    
                    if (!infiniteRequests) {
                        requestCount++;
                        broadcastRequestProgress();
                    }
                    
                    // Wait between cycles
                    if (isRunning.get() && !isPaused.get()) {
                        try {
                            Thread.sleep(timeBetweenRequests);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                
                stopTesting();
            });
        }
    }
    
    private void testServer(Server server) {
        // Broadcast that we're starting to test this server
        Intent testingIntent = new Intent(ACTION_SERVER_TESTING);
        testingIntent.putExtra(EXTRA_SERVER_ID, server.getId());
        testingIntent.putExtra(EXTRA_SERVER_NAME, server.getName());
        localBroadcastManager.sendBroadcast(testingIntent);
        
        long startTime = System.currentTimeMillis();
        boolean success = false;
        String errorMessage = null;
        
        try {
            if (server.getRequestType() == Server.RequestType.HTTPS) {
                success = testHttpServer(server);
            } else if (server.getRequestType() == Server.RequestType.PING) {
                success = testPingServer(server);
            }
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            Log.e(TAG, "Error testing server " + server.getName(), e);
        }
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        Intent resultIntent = new Intent(ACTION_TEST_RESULT);
        resultIntent.putExtra(EXTRA_SERVER_ID, server.getId());
        resultIntent.putExtra(EXTRA_SERVER_NAME, server.getName());
        resultIntent.putExtra(EXTRA_SUCCESS, success);
        resultIntent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
        resultIntent.putExtra(EXTRA_RESPONSE_TIME, responseTime);
        
        localBroadcastManager.sendBroadcast(resultIntent);
    }
    
    private boolean testHttpServer(Server server) {
        try {
            String urlString = server.getAddress();
            
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "https://" + urlString;
            }
            
            if (server.getPort() != null) {
                urlString += ":" + server.getPort();
            }
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
            return responseCode >= 200 && responseCode < 400;
            
        } catch (IOException e) {
            Log.e(TAG, "HTTP test failed for " + server.getName(), e);
            return false;
        }
    }
    
    private boolean testPingServer(Server server) {
        try {
            String address = server.getAddress();
            
            if (address.startsWith("http://")) {
                address = address.substring(7);
            } else if (address.startsWith("https://")) {
                address = address.substring(8);
            }
            
            InetAddress inetAddress = InetAddress.getByName(address);
            return inetAddress.isReachable(10000);
            
        } catch (IOException e) {
            Log.e(TAG, "Ping test failed for " + server.getName(), e);
            return false;
        }
    }
    
    private void pauseTesting() {
        if (isRunning.get() && isPaused.compareAndSet(false, true)) {
            Log.d(TAG, "Test paused");
            updateNotification("", true);
        }
    }
    
    private void resumeTesting() {
        if (isRunning.get() && isPaused.compareAndSet(true, false)) {
            Log.d(TAG, "Test resumed");
            updateNotification("", false);
        }
    }
    
    public void stopTesting() {
        if (isRunning.compareAndSet(true, false)) {
            isPaused.set(false);
            
            if (testTask != null) {
                testTask.cancel(true);
            }
            
            Log.d(TAG, "Test stopped");
            broadcastTestStopped();
            stopForeground(true);
            stopSelf();
        }
    }
    
    private void broadcastTestStarted() {
        Intent intent = new Intent(ACTION_TEST_STARTED);
        localBroadcastManager.sendBroadcast(intent);
    }
    
    private void broadcastTestStopped() {
        Intent intent = new Intent(ACTION_TEST_STOPPED);
        localBroadcastManager.sendBroadcast(intent);
    }
    
    private void broadcastRequestProgress() {
        Intent intent = new Intent(ACTION_REQUEST_PROGRESS);
        intent.putExtra(EXTRA_CURRENT_REQUEST, requestCount);
        intent.putExtra(EXTRA_TOTAL_REQUESTS, numberOfRequests);
        intent.putExtra(EXTRA_INFINITE_REQUESTS, infiniteRequests);
        localBroadcastManager.sendBroadcast(intent);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTesting();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}