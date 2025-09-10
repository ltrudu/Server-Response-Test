package com.ltrudu.serverresponsetest.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ltrudu.serverresponsetest.R;
import com.ltrudu.serverresponsetest.adapter.TestServerAdapter;
import com.ltrudu.serverresponsetest.repository.SettingsRepository;
import com.ltrudu.serverresponsetest.service.ServerTestService;
import com.ltrudu.serverresponsetest.viewmodel.ServerViewModel;

public class TestFragment extends Fragment {
    
    private ServerViewModel serverViewModel;
    private SettingsRepository settingsRepository;
    private com.ltrudu.serverresponsetest.data.Settings currentSettings;
    private TextView serverCountText;
    private TextView statusText;
    private TextView countdownText;
    private FloatingActionButton playStopButton;
    private RecyclerView serverListRecyclerView;
    private TestServerAdapter testServerAdapter;
    private LinearLayout emptyTestStateLayout;
    private LinearLayout testControlsLayout;
    private boolean isTestRunning = false;
    private boolean areServersProcessing = false;
    private int processedServerCount = 0;
    private int totalServerCount = 0;
    
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver testResultReceiver;
    private CountDownTimer countDownTimer;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
        setupBroadcastReceiver();
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcastReceiver();
        stopCountdown();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCountdown();
        
        if (settingsRepository != null) {
            settingsRepository.shutdown();
        }
    }
    
    private void initializeViews(View view) {
        serverCountText = view.findViewById(R.id.serverCountText);
        statusText = view.findViewById(R.id.statusText);
        countdownText = view.findViewById(R.id.countdownText);
        playStopButton = view.findViewById(R.id.playStopButton);
        serverListRecyclerView = view.findViewById(R.id.serverListRecyclerView);
        emptyTestStateLayout = view.findViewById(R.id.emptyTestStateLayout);
        testControlsLayout = view.findViewById(R.id.testControlsLayout);
    }
    
    private void setupRecyclerView() {
        testServerAdapter = new TestServerAdapter();
        serverListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        serverListRecyclerView.setAdapter(testServerAdapter);
    }
    
    private void setupViewModel() {
        serverViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        settingsRepository = new SettingsRepository(requireActivity().getApplication());
        serverViewModel.getAllServers().observe(getViewLifecycleOwner(), servers -> {
            if (servers != null) {
                totalServerCount = servers.size();
                serverCountText.setText(getString(R.string.number_of_servers, servers.size()));
                testServerAdapter.submitList(servers);
                
                // Show/hide empty state and controls
                if (servers.isEmpty()) {
                    // Hide all controls and show empty state
                    testControlsLayout.setVisibility(View.GONE);
                    serverListRecyclerView.setVisibility(View.GONE);
                    emptyTestStateLayout.setVisibility(View.VISIBLE);
                } else {
                    // Show controls and server list, hide empty state
                    testControlsLayout.setVisibility(View.VISIBLE);
                    serverListRecyclerView.setVisibility(View.VISIBLE);
                    emptyTestStateLayout.setVisibility(View.GONE);
                    playStopButton.setEnabled(true);
                }
            }
        });
        
        // Observe settings changes
        settingsRepository.getSettings().observe(getViewLifecycleOwner(), settings -> {
            currentSettings = settings;
        });
    }
    
    private void setupClickListeners() {
        playStopButton.setOnClickListener(v -> toggleTest());
    }
    
    private void setupBroadcastReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext());
        
        testResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ServerTestService.ACTION_TEST_STARTED.equals(action)) {
                    isTestRunning = true;
                    statusText.setText(R.string.test_running);
                    // Set all servers to pending status when test starts
                    setAllServersPending();
                    processedServerCount = 0;
                    areServersProcessing = true;
                    updateUI();
                    updateCountdownText();
                } else if (ServerTestService.ACTION_TEST_STOPPED.equals(action)) {
                    isTestRunning = false;
                    areServersProcessing = false;
                    statusText.setText(R.string.test_stopped);
                    stopCountdown();
                    updateUI();
                } else if (ServerTestService.ACTION_TEST_RESULT.equals(action)) {
                    // Handle individual server test results
                    handleTestResult(intent);
                } else if (ServerTestService.ACTION_SERVER_TESTING.equals(action)) {
                    // Handle server testing started
                    handleServerTesting(intent);
                }
            }
        };
    }
    
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServerTestService.ACTION_TEST_STARTED);
        filter.addAction(ServerTestService.ACTION_TEST_STOPPED);
        filter.addAction(ServerTestService.ACTION_TEST_RESULT);
        filter.addAction(ServerTestService.ACTION_SERVER_TESTING);
        localBroadcastManager.registerReceiver(testResultReceiver, filter);
    }
    
    private void unregisterBroadcastReceiver() {
        if (testResultReceiver != null) {
            localBroadcastManager.unregisterReceiver(testResultReceiver);
        }
    }
    
    private void handleTestResult(Intent intent) {
        long serverId = intent.getLongExtra(ServerTestService.EXTRA_SERVER_ID, -1);
        String serverName = intent.getStringExtra(ServerTestService.EXTRA_SERVER_NAME);
        boolean success = intent.getBooleanExtra(ServerTestService.EXTRA_SUCCESS, false);
        String errorMessage = intent.getStringExtra(ServerTestService.EXTRA_ERROR_MESSAGE);
        long responseTime = intent.getLongExtra(ServerTestService.EXTRA_RESPONSE_TIME, 0);
        
        // Update the adapter to show test results
        testServerAdapter.updateServerResult(serverId, success, responseTime, errorMessage);
        
        // Track processed servers
        processedServerCount++;
        
        // Check if all servers are processed
        if (processedServerCount >= totalServerCount) {
            areServersProcessing = false;
            if (isTestRunning) {
                // All servers processed, start countdown for next cycle
                startCountdown();
            }
        }
    }
    
    private void handleServerTesting(Intent intent) {
        long serverId = intent.getLongExtra(ServerTestService.EXTRA_SERVER_ID, -1);
        
        // Update the server status to processing
        testServerAdapter.updateServerStatus(serverId, TestServerAdapter.ServerStatus.PROCESSING);
    }
    
    private void setAllServersPending() {
        // Use the adapter's method to set all servers to pending status
        testServerAdapter.setAllServersPending();
        processedServerCount = 0;
        areServersProcessing = true;
    }
    
    private void updateCountdownText() {
        if (areServersProcessing) {
            countdownText.setText(R.string.processing_servers);
            countdownText.setVisibility(View.VISIBLE);
        } else if (isTestRunning) {
            // Countdown will be handled by the timer
            countdownText.setVisibility(View.VISIBLE);
        } else {
            countdownText.setVisibility(View.GONE);
        }
    }
    
    private void startCountdown() {
        // Don't start countdown if servers are still processing
        if (areServersProcessing) {
            updateCountdownText();
            return;
        }
        
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        // Get current settings from cache
        int timeBetweenRequests = 5; // Default value
        if (currentSettings != null) {
            timeBetweenRequests = currentSettings.getTimeBetweenRequests();
        }
        
        long countdownTime = timeBetweenRequests * 1000L; // Convert to milliseconds
        
        countDownTimer = new CountDownTimer(countdownTime, 100L) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!areServersProcessing) {
                    long seconds = millisUntilFinished / 1000;
                    long tenths = (millisUntilFinished % 1000) / 100;
                    String countdownFormat = getString(R.string.next_cycle_in, String.format("%d.%d", seconds, tenths));
                    countdownText.setText(countdownFormat);
                    countdownText.setVisibility(View.VISIBLE);
                } else {
                    updateCountdownText();
                }
            }
            
            @Override
            public void onFinish() {
                // Reset countdown for next cycle
                if (isTestRunning) {
                    setAllServersPending();
                    updateCountdownText();
                }
            }
        };
        
        countDownTimer.start();
    }
    
    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countdownText.setVisibility(View.GONE);
    }
    
    private void toggleTest() {
        if (isTestRunning) {
            stopTest();
        } else {
            startTest();
        }
    }
    
    private void startTest() {
        // Get current settings from cache
        int timeBetweenRequests = 5; // Default values
        int requestDelayMs = 100;
        int randomMinDelayMs = 50;
        int randomMaxDelayMs = 100;
        boolean infiniteRequests = true;
        int numberOfRequests = 10;
        
        if (currentSettings != null) {
            timeBetweenRequests = currentSettings.getTimeBetweenRequests();
            requestDelayMs = currentSettings.getRequestDelayMs();
            randomMinDelayMs = currentSettings.getRandomMinDelayMs();
            randomMaxDelayMs = currentSettings.getRandomMaxDelayMs();
            infiniteRequests = currentSettings.isInfiniteRequests();
            numberOfRequests = currentSettings.getNumberOfRequests();
        }
        
        Intent serviceIntent = new Intent(requireContext(), ServerTestService.class);
        serviceIntent.putExtra(ServerTestService.EXTRA_TIME_BETWEEN_REQUESTS, timeBetweenRequests);
        serviceIntent.putExtra(ServerTestService.EXTRA_REQUEST_DELAY_MS, requestDelayMs);
        serviceIntent.putExtra(ServerTestService.EXTRA_RANDOM_MIN_DELAY_MS, randomMinDelayMs);
        serviceIntent.putExtra(ServerTestService.EXTRA_RANDOM_MAX_DELAY_MS, randomMaxDelayMs);
        serviceIntent.putExtra(ServerTestService.EXTRA_INFINITE_REQUESTS, infiniteRequests);
        serviceIntent.putExtra(ServerTestService.EXTRA_NUMBER_OF_REQUESTS, numberOfRequests);
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent);
        } else {
            requireContext().startService(serviceIntent);
        }
    }
    
    private void stopTest() {
        Intent serviceIntent = new Intent(requireContext(), ServerTestService.class);
        requireContext().stopService(serviceIntent);
        
        isTestRunning = false;
        statusText.setText(R.string.test_stopped);
        updateUI();
    }
    
    private void updateUI() {
        if (isTestRunning) {
            playStopButton.setImageResource(android.R.drawable.ic_media_pause);
            playStopButton.setContentDescription(getString(R.string.stop_test));
        } else {
            playStopButton.setImageResource(android.R.drawable.ic_media_play);
            playStopButton.setContentDescription(getString(R.string.start_test));
        }
    }
}