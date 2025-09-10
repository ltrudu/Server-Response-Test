package com.ltrudu.serverresponsetest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private ExecutorService executorService;
    private Future<?> testTask;
    private ServerRepository serverRepository;
    private LocalBroadcastManager localBroadcastManager;
    private Random random = new Random();
    
    private int timeBetweenRequests = 5;
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
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            timeBetweenRequests = intent.getIntExtra(EXTRA_TIME_BETWEEN_REQUESTS, 5);
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
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Server Load Test")
                .setContentText("Testing servers...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        
        startForeground(NOTIFICATION_ID, notification);
    }
    
    private void startTesting() {
        if (isRunning.compareAndSet(false, true)) {
            broadcastTestStarted();
            
            testTask = executorService.submit(() -> {
                List<Server> servers = serverRepository.getAllServersSync();
                
                if (servers == null || servers.isEmpty()) {
                    Log.w(TAG, "No servers to test");
                    stopTesting();
                    return;
                }
                
                int requestCount = 0;
                
                while (isRunning.get() && (infiniteRequests || requestCount < numberOfRequests)) {
                    for (int i = 0; i < servers.size(); i++) {
                        if (!isRunning.get()) {
                            break;
                        }
                        
                        Server server = servers.get(i);
                        testServer(server);
                        
                        // Add delay between individual server requests (only if there are multiple servers)
                        if (servers.size() > 1 && i < servers.size() - 1 && isRunning.get()) {
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
                    }
                    
                    if (isRunning.get()) {
                        try {
                            Thread.sleep(timeBetweenRequests * 1000L);
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
    
    public void stopTesting() {
        if (isRunning.compareAndSet(true, false)) {
            if (testTask != null) {
                testTask.cancel(true);
            }
            
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
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTesting();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}