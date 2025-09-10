package com.ltrudu.serverloadtest.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ltrudu.serverloadtest.R;
import com.ltrudu.serverloadtest.data.Server;
import com.ltrudu.serverloadtest.viewmodel.ServerViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {
    
    private static final String PREFS_NAME = "ServerLoadTestPrefs";
    private static final String PREF_TIME_BETWEEN_REQUESTS = "time_between_requests";
    private static final String PREF_INFINITE_REQUESTS = "infinite_requests";
    private static final String PREF_NUMBER_OF_REQUESTS = "number_of_requests";
    
    private ServerViewModel serverViewModel;
    private SharedPreferences sharedPreferences;
    private ExecutorService executorService;
    
    private TextInputEditText timeBetweenRequestsEditText;
    private MaterialCheckBox infiniteRequestsCheckBox;
    private TextInputLayout numberOfRequestsInputLayout;
    private TextInputEditText numberOfRequestsEditText;
    private Button exportButton;
    private Button importButton;
    private Button shareButton;
    private Button saveButton;
    
    private ActivityResultLauncher<String> importLauncher;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        importLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        importServersFromUri(uri);
                    }
                });
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        initializeViews(view);
        setupViewModel();
        setupPreferences();
        setupClickListeners();
        loadSettings();
        
        return view;
    }
    
    private void initializeViews(View view) {
        timeBetweenRequestsEditText = view.findViewById(R.id.timeBetweenRequestsEditText);
        infiniteRequestsCheckBox = view.findViewById(R.id.infiniteRequestsCheckBox);
        numberOfRequestsInputLayout = view.findViewById(R.id.numberOfRequestsInputLayout);
        numberOfRequestsEditText = view.findViewById(R.id.numberOfRequestsEditText);
        exportButton = view.findViewById(R.id.exportButton);
        importButton = view.findViewById(R.id.importButton);
        shareButton = view.findViewById(R.id.shareButton);
        saveButton = view.findViewById(R.id.saveButton);
    }
    
    private void setupViewModel() {
        serverViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
    }
    
    private void setupPreferences() {
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        executorService = Executors.newSingleThreadExecutor();
    }
    
    private void setupClickListeners() {
        infiniteRequestsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            numberOfRequestsInputLayout.setEnabled(!isChecked);
        });
        
        saveButton.setOnClickListener(v -> saveSettings());
        exportButton.setOnClickListener(v -> exportServers());
        importButton.setOnClickListener(v -> importServers());
        shareButton.setOnClickListener(v -> shareServers());
    }
    
    private void loadSettings() {
        int timeBetweenRequests = sharedPreferences.getInt(PREF_TIME_BETWEEN_REQUESTS, 5);
        boolean infiniteRequests = sharedPreferences.getBoolean(PREF_INFINITE_REQUESTS, true);
        int numberOfRequests = sharedPreferences.getInt(PREF_NUMBER_OF_REQUESTS, 10);
        
        timeBetweenRequestsEditText.setText(String.valueOf(timeBetweenRequests));
        infiniteRequestsCheckBox.setChecked(infiniteRequests);
        numberOfRequestsEditText.setText(String.valueOf(numberOfRequests));
        numberOfRequestsInputLayout.setEnabled(!infiniteRequests);
    }
    
    private void saveSettings() {
        String timeText = timeBetweenRequestsEditText.getText().toString().trim();
        String numberOfRequestsText = numberOfRequestsEditText.getText().toString().trim();
        
        if (TextUtils.isEmpty(timeText)) {
            Toast.makeText(getContext(), "Please enter time between requests", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!infiniteRequestsCheckBox.isChecked() && TextUtils.isEmpty(numberOfRequestsText)) {
            Toast.makeText(getContext(), "Please enter number of requests", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            int timeBetweenRequests = Integer.parseInt(timeText);
            boolean infiniteRequests = infiniteRequestsCheckBox.isChecked();
            int numberOfRequests = infiniteRequests ? 0 : Integer.parseInt(numberOfRequestsText);
            
            if (timeBetweenRequests <= 0) {
                Toast.makeText(getContext(), "Time between requests must be positive", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!infiniteRequests && numberOfRequests <= 0) {
                Toast.makeText(getContext(), "Number of requests must be positive", Toast.LENGTH_SHORT).show();
                return;
            }
            
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PREF_TIME_BETWEEN_REQUESTS, timeBetweenRequests);
            editor.putBoolean(PREF_INFINITE_REQUESTS, infiniteRequests);
            editor.putInt(PREF_NUMBER_OF_REQUESTS, numberOfRequests);
            editor.apply();
            
            Toast.makeText(getContext(), "Settings saved", Toast.LENGTH_SHORT).show();
            
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void exportServers() {
        executorService.execute(() -> {
            try {
                List<Server> servers = serverViewModel.getAllServersSync();
                Gson gson = new Gson();
                String json = gson.toJson(servers);
                
                File exportDir = new File(requireContext().getExternalFilesDir(null), "exports");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                
                File exportFile = new File(exportDir, "servers_export.json");
                
                try (FileWriter writer = new FileWriter(exportFile)) {
                    writer.write(json);
                }
                
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), getString(R.string.export_success), Toast.LENGTH_SHORT).show());
                
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), getString(R.string.export_error), Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    private void importServers() {
        importLauncher.launch("application/json");
    }
    
    private void importServersFromUri(Uri uri) {
        executorService.execute(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(requireContext().getContentResolver().openInputStream(uri)))) {
                
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Server>>(){}.getType();
                List<Server> servers = gson.fromJson(jsonBuilder.toString(), listType);
                
                serverViewModel.deleteAllServers();
                
                for (Server server : servers) {
                    serverViewModel.insertServer(server, null);
                }
                
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), getString(R.string.import_success), Toast.LENGTH_SHORT).show());
                
            } catch (IOException | JsonSyntaxException e) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), getString(R.string.import_error), Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    private void shareServers() {
        executorService.execute(() -> {
            try {
                List<Server> servers = serverViewModel.getAllServersSync();
                Gson gson = new Gson();
                String json = gson.toJson(servers);
                
                File tempDir = new File(requireContext().getCacheDir(), "temp");
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                
                File tempFile = new File(tempDir, "servers_share.json");
                
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(json);
                }
                
                Uri uri = FileProvider.getUriForFile(requireContext(), 
                        requireContext().getPackageName() + ".fileprovider", tempFile);
                
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/json");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Server Configuration");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                
                requireActivity().runOnUiThread(() -> 
                    startActivity(Intent.createChooser(shareIntent, "Share Server Configuration")));
                
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "Error sharing servers", Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    
    public int getTimeBetweenRequests() {
        return sharedPreferences.getInt(PREF_TIME_BETWEEN_REQUESTS, 5);
    }
    
    public boolean isInfiniteRequests() {
        return sharedPreferences.getBoolean(PREF_INFINITE_REQUESTS, true);
    }
    
    public int getNumberOfRequests() {
        return sharedPreferences.getInt(PREF_NUMBER_OF_REQUESTS, 10);
    }
}