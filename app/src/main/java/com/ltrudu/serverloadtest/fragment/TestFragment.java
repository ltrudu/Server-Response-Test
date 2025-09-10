package com.ltrudu.serverloadtest.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ltrudu.serverloadtest.R;
import com.ltrudu.serverloadtest.adapter.TestServerAdapter;
import com.ltrudu.serverloadtest.service.ServerTestService;
import com.ltrudu.serverloadtest.viewmodel.ServerViewModel;

public class TestFragment extends Fragment {
    
    private static final String PREFS_NAME = "ServerLoadTestPrefs";
    
    private ServerViewModel serverViewModel;
    private TextView serverCountText;
    private TextView statusText;
    private FloatingActionButton playStopButton;
    private RecyclerView serverListRecyclerView;
    private TestServerAdapter testServerAdapter;
    private boolean isTestRunning = false;
    
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver testResultReceiver;
    
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
    }
    
    private void initializeViews(View view) {
        serverCountText = view.findViewById(R.id.serverCountText);
        statusText = view.findViewById(R.id.statusText);
        playStopButton = view.findViewById(R.id.playStopButton);
        serverListRecyclerView = view.findViewById(R.id.serverListRecyclerView);
    }
    
    private void setupRecyclerView() {
        testServerAdapter = new TestServerAdapter();
        serverListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        serverListRecyclerView.setAdapter(testServerAdapter);
    }
    
    private void setupViewModel() {
        serverViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        serverViewModel.getAllServers().observe(getViewLifecycleOwner(), servers -> {
            if (servers != null) {
                serverCountText.setText(getString(R.string.number_of_servers, servers.size()));
                testServerAdapter.submitList(servers);
            }
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
                    updateUI();
                } else if (ServerTestService.ACTION_TEST_STOPPED.equals(action)) {
                    isTestRunning = false;
                    statusText.setText(R.string.test_stopped);
                    updateUI();
                } else if (ServerTestService.ACTION_TEST_RESULT.equals(action)) {
                    // Handle individual server test results
                    handleTestResult(intent);
                }
            }
        };
    }
    
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServerTestService.ACTION_TEST_STARTED);
        filter.addAction(ServerTestService.ACTION_TEST_STOPPED);
        filter.addAction(ServerTestService.ACTION_TEST_RESULT);
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
    }
    
    private void toggleTest() {
        if (isTestRunning) {
            stopTest();
        } else {
            startTest();
        }
    }
    
    private void startTest() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int timeBetweenRequests = prefs.getInt("time_between_requests", 5);
        boolean infiniteRequests = prefs.getBoolean("infinite_requests", true);
        int numberOfRequests = prefs.getInt("number_of_requests", 10);
        
        Intent serviceIntent = new Intent(requireContext(), ServerTestService.class);
        serviceIntent.putExtra(ServerTestService.EXTRA_TIME_BETWEEN_REQUESTS, timeBetweenRequests);
        serviceIntent.putExtra(ServerTestService.EXTRA_INFINITE_REQUESTS, infiniteRequests);
        serviceIntent.putExtra(ServerTestService.EXTRA_NUMBER_OF_REQUESTS, numberOfRequests);
        
        requireContext().startForegroundService(serviceIntent);
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