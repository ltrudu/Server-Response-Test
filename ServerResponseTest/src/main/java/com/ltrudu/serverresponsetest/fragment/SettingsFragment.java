package com.ltrudu.serverresponsetest.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ltrudu.serverresponsetest.R;
import com.ltrudu.serverresponsetest.data.ExportData;
import com.ltrudu.serverresponsetest.data.Server;
import com.ltrudu.serverresponsetest.data.Settings;
import com.ltrudu.serverresponsetest.repository.SettingsRepository;
import com.ltrudu.serverresponsetest.viewmodel.ServerViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {
    
    private ServerViewModel serverViewModel;
    private SettingsRepository settingsRepository;
    private ExecutorService executorService;
    private Settings currentSettings;
    
    private TextInputEditText timeBetweenRequestsEditText;
    private TextInputEditText requestDelayEditText;
    private TextInputEditText randomMinDelayEditText;
    private TextInputEditText randomMaxDelayEditText;
    private MaterialCheckBox infiniteRequestsCheckBox;
    private TextInputLayout numberOfRequestsInputLayout;
    private TextInputEditText numberOfRequestsEditText;
    private LinearLayout layoutExportData;
    private LinearLayout layoutImportData;
    private LinearLayout layoutShareData;
    private LinearLayout layoutResetDatabase;
    
    private ActivityResultLauncher<String> importLauncher;
    private ActivityResultLauncher<String> createDocumentLauncher;
    private ActivityResultLauncher<String[]> openDocumentLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    
    private static final int STORAGE_PERMISSION_REQUEST = 100;
    private static final int DEBOUNCE_DELAY_MS = 500; // 500ms delay for database saves
    
    private Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingSaveRunnable;
    private boolean isUpdatingFromSettings = false;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Legacy import launcher (for Android < 10)
        importLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        importServersFromUri(uri);
                    }
                });
        
        // Scoped storage launchers (for Android >= 10)
        createDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/json"),
                uri -> {
                    if (uri != null) {
                        exportServersToUri(uri);
                    }
                });
        
        openDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        importServersFromUri(uri);
                    }
                });
        
        // Permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, proceed with export
                        exportServersLegacy();
                    } else {
                        Toast.makeText(getContext(), "Storage permission denied", Toast.LENGTH_SHORT).show();
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
        
        return view;
    }
    
    private void initializeViews(View view) {
        timeBetweenRequestsEditText = view.findViewById(R.id.timeBetweenRequestsEditText);
        requestDelayEditText = view.findViewById(R.id.requestDelayEditText);
        randomMinDelayEditText = view.findViewById(R.id.randomMinDelayEditText);
        randomMaxDelayEditText = view.findViewById(R.id.randomMaxDelayEditText);
        infiniteRequestsCheckBox = view.findViewById(R.id.infiniteRequestsCheckBox);
        numberOfRequestsInputLayout = view.findViewById(R.id.numberOfRequestsInputLayout);
        numberOfRequestsEditText = view.findViewById(R.id.numberOfRequestsEditText);
        layoutExportData = view.findViewById(R.id.layoutExportData);
        layoutImportData = view.findViewById(R.id.layoutImportData);
        layoutShareData = view.findViewById(R.id.layoutShareData);
        layoutResetDatabase = view.findViewById(R.id.layoutResetDatabase);
    }
    
    private void setupViewModel() {
        serverViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        settingsRepository = new SettingsRepository(requireActivity().getApplication());
        executorService = Executors.newSingleThreadExecutor();
        
        // Observe settings changes
        settingsRepository.getSettings().observe(getViewLifecycleOwner(), settings -> {
            if (settings != null) {
                currentSettings = settings;
                updateUIFromSettings(settings);
            }
        });
    }
    
    private void setupPreferences() {
        // Method kept for compatibility but functionality moved to setupViewModel
    }
    
    private void updateUIFromSettings(Settings settings) {
        if (settings != null) {
            // Prevent text watchers from firing during programmatic updates
            isUpdatingFromSettings = true;
            
            timeBetweenRequestsEditText.setText(String.valueOf(settings.getTimeBetweenRequests()));
            requestDelayEditText.setText(String.valueOf(settings.getRequestDelayMs()));
            randomMinDelayEditText.setText(String.valueOf(settings.getRandomMinDelayMs()));
            randomMaxDelayEditText.setText(String.valueOf(settings.getRandomMaxDelayMs()));
            infiniteRequestsCheckBox.setChecked(settings.isInfiniteRequests());
            numberOfRequestsEditText.setText(String.valueOf(settings.getNumberOfRequests()));
            numberOfRequestsInputLayout.setEnabled(!settings.isInfiniteRequests());
            
            isUpdatingFromSettings = false;
        }
    }
    
    private void setupClickListeners() {
        infiniteRequestsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            numberOfRequestsInputLayout.setEnabled(!isChecked);
            settingsRepository.updateInfiniteRequests(isChecked);
        });
        
        layoutExportData.setOnClickListener(v -> exportServers());
        layoutImportData.setOnClickListener(v -> importServers());
        layoutShareData.setOnClickListener(v -> shareServers());
        layoutResetDatabase.setOnClickListener(v -> showResetDatabaseDialog());
        
        setupAutoSave();
    }
    
    
    private void setupAutoSave() {
        // Use debounced text watchers to reduce database calls and InputMethodManager spam
        timeBetweenRequestsEditText.addTextChangedListener(
                new DebouncedTextWatcher("time_between_requests", 5));
        
        requestDelayEditText.addTextChangedListener(
                new DebouncedTextWatcher("request_delay_ms", 100));
        
        randomMinDelayEditText.addTextChangedListener(
                new DebouncedTextWatcher("random_min_delay_ms", 50));
        
        randomMaxDelayEditText.addTextChangedListener(
                new DebouncedTextWatcher("random_max_delay_ms", 100));
        
        numberOfRequestsEditText.addTextChangedListener(
                new DebouncedTextWatcher("number_of_requests", 10));
    }
    
    private void saveIntSetting(String value, String key, int defaultValue) {
        if (!value.isEmpty()) {
            try {
                int intValue = Integer.parseInt(value);
                if (intValue >= 0) {
                    switch (key) {
                        case "time_between_requests":
                            settingsRepository.updateTimeBetweenRequests(intValue);
                            break;
                        case "request_delay_ms":
                            settingsRepository.updateRequestDelayMs(intValue);
                            break;
                        case "random_min_delay_ms":
                            settingsRepository.updateRandomMinDelayMs(intValue);
                            break;
                        case "random_max_delay_ms":
                            settingsRepository.updateRandomMaxDelayMs(intValue);
                            break;
                        case "number_of_requests":
                            settingsRepository.updateNumberOfRequests(intValue);
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }
    }
    
    private static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        
        @Override
        public void afterTextChanged(Editable s) {}
    }
    
    private class DebouncedTextWatcher implements TextWatcher {
        private final String settingKey;
        private final int defaultValue;
        
        public DebouncedTextWatcher(String settingKey, int defaultValue) {
            this.settingKey = settingKey;
            this.defaultValue = defaultValue;
        }
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        
        @Override
        public void afterTextChanged(Editable s) {
            // Skip processing during programmatic updates to prevent unnecessary database calls
            if (isUpdatingFromSettings) {
                return;
            }
            
            // Cancel any pending save operation
            if (pendingSaveRunnable != null) {
                debounceHandler.removeCallbacks(pendingSaveRunnable);
            }
            
            // Schedule a new save operation with debounce
            pendingSaveRunnable = () -> {
                saveIntSetting(s.toString().trim(), settingKey, defaultValue);
                pendingSaveRunnable = null;
            };
            
            debounceHandler.postDelayed(pendingSaveRunnable, DEBOUNCE_DELAY_MS);
        }
    }
    
    
    private void exportServers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - Use scoped storage
            exportServersScoped();
        } else {
            // Android 7-9 - Use legacy external storage with permission
            if (hasStoragePermission()) {
                exportServersLegacy();
            } else {
                requestStoragePermission();
            }
        }
    }
    
    private void exportServersScoped() {
        // Use SAF (Storage Access Framework) for Android 10+
        createDocumentLauncher.launch("servers_export.json");
    }
    
    private void exportServersLegacy() {
        executorService.execute(() -> {
            try {
                List<Server> servers = serverViewModel.getAllServersSync();
                Settings settings = settingsRepository.getSettingsSync();
                
                ExportData exportData = new ExportData(servers, settings);
                Gson gson = new Gson();
                String json = gson.toJson(exportData);
                
                // Use public Downloads directory for Android 7-9
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File exportFile = new File(downloadsDir, "servers_export.json");
                
                try (FileWriter writer = new FileWriter(exportFile)) {
                    writer.write(json);
                }
                
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), getString(R.string.export_success) + "\n" + exportFile.getAbsolutePath(), Toast.LENGTH_LONG).show());
                
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), getString(R.string.export_error), Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    private void exportServersToUri(Uri uri) {
        executorService.execute(() -> {
            try {
                List<Server> servers = serverViewModel.getAllServersSync();
                Settings settings = settingsRepository.getSettingsSync();
                
                ExportData exportData = new ExportData(servers, settings);
                Gson gson = new Gson();
                String json = gson.toJson(exportData);
                
                try (OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri)) {
                    if (outputStream != null) {
                        outputStream.write(json.getBytes());
                    }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - Use scoped storage
            String[] mimeTypes = {"application/json", "text/json"};
            openDocumentLauncher.launch(mimeTypes);
        } else {
            // Android 7-9 - Use legacy method
            importLauncher.launch("application/json");
        }
    }
    
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true; // No permission needed for scoped storage
        }
        return ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
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
                
                String jsonString = jsonBuilder.toString();
                Gson gson = new Gson();
                
                try {
                    // Try to parse as new format (ExportData)
                    ExportData exportData = gson.fromJson(jsonString, ExportData.class);
                    
                    if (exportData != null && exportData.getServers() != null) {
                        // Clear existing data
                        serverViewModel.deleteAllServers();
                        
                        // Import servers
                        for (Server server : exportData.getServers()) {
                            serverViewModel.insertServer(server, null);
                        }
                        
                        // Replace settings (this will automatically trigger UI update)
                        Settings settingsToImport;
                        if (exportData.getSettings() != null) {
                            settingsToImport = exportData.getSettings();
                        } else {
                            settingsToImport = Settings.getDefault();
                        }
                        settingsToImport.setId(1); // Ensure it replaces the existing row
                        settingsRepository.insertSettings(settingsToImport);
                        
                        requireActivity().runOnUiThread(() -> 
                            Toast.makeText(getContext(), getString(R.string.import_success), Toast.LENGTH_SHORT).show());
                    } else {
                        throw new JsonSyntaxException("Invalid export data format");
                    }
                } catch (JsonSyntaxException e) {
                    try {
                        // Fallback: try to parse as old format (List<Server>)
                        Type listType = new TypeToken<List<Server>>(){}.getType();
                        List<Server> servers = gson.fromJson(jsonString, listType);
                        
                        if (servers != null) {
                            serverViewModel.deleteAllServers();
                            
                            for (Server server : servers) {
                                serverViewModel.insertServer(server, null);
                            }
                            
                            // Keep existing settings when importing old format
                            requireActivity().runOnUiThread(() -> 
                                Toast.makeText(getContext(), getString(R.string.import_success), Toast.LENGTH_SHORT).show());
                        } else {
                            throw new JsonSyntaxException("Invalid server list format");
                        }
                    } catch (JsonSyntaxException e2) {
                        requireActivity().runOnUiThread(() -> 
                            Toast.makeText(getContext(), getString(R.string.import_error), Toast.LENGTH_SHORT).show());
                    }
                }
                
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), getString(R.string.import_error), Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    private void shareServers() {
        executorService.execute(() -> {
            try {
                List<Server> servers = serverViewModel.getAllServersSync();
                Settings settings = settingsRepository.getSettingsSync();
                
                ExportData exportData = new ExportData(servers, settings);
                Gson gson = new Gson();
                String json = gson.toJson(exportData);
                
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
        
        // Clean up debounce handler to prevent memory leaks
        if (debounceHandler != null && pendingSaveRunnable != null) {
            debounceHandler.removeCallbacks(pendingSaveRunnable);
            pendingSaveRunnable = null;
        }
        
        if (executorService != null) {
            executorService.shutdown();
        }
        if (settingsRepository != null) {
            settingsRepository.shutdown();
        }
    }
    
    private void showResetDatabaseDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.reset_database_title)
                .setMessage(R.string.reset_database_message)
                .setIcon(R.drawable.ic_warning_24)
                .setPositiveButton(R.string.reset, (dialog, which) -> resetDatabase())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void resetDatabase() {
        executorService.execute(() -> {
            try {
                // Clear all servers
                serverViewModel.deleteAllServers();
                
                // Replace settings with defaults (this will automatically trigger UI update)
                Settings defaultSettings = Settings.getDefault();
                defaultSettings.setId(1); // Ensure it replaces the existing row
                settingsRepository.insertSettings(defaultSettings);
                
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "Database reset successfully", Toast.LENGTH_SHORT).show());
                
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "Error resetting database", Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    public int getTimeBetweenRequests() {
        return currentSettings != null ? currentSettings.getTimeBetweenRequests() : 5;
    }
    
    public int getRequestDelayMs() {
        return currentSettings != null ? currentSettings.getRequestDelayMs() : 100;
    }
    
    public int getRandomMinDelayMs() {
        return currentSettings != null ? currentSettings.getRandomMinDelayMs() : 50;
    }
    
    public int getRandomMaxDelayMs() {
        return currentSettings != null ? currentSettings.getRandomMaxDelayMs() : 100;
    }
    
    public boolean isInfiniteRequests() {
        return currentSettings != null ? currentSettings.isInfiniteRequests() : true;
    }
    
    public int getNumberOfRequests() {
        return currentSettings != null ? currentSettings.getNumberOfRequests() : 10;
    }
}