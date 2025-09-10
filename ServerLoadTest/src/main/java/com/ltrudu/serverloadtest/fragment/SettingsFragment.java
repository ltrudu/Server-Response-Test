package com.ltrudu.serverloadtest.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.documentfile.provider.DocumentFile;
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
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {
    
    private static final String PREFS_NAME = "ServerLoadTestPrefs";
    private static final String PREF_TIME_BETWEEN_REQUESTS = "time_between_requests";
    private static final String PREF_REQUEST_DELAY_MS = "request_delay_ms";
    private static final String PREF_RANDOM_MIN_DELAY_MS = "random_min_delay_ms";
    private static final String PREF_RANDOM_MAX_DELAY_MS = "random_max_delay_ms";
    private static final String PREF_INFINITE_REQUESTS = "infinite_requests";
    private static final String PREF_NUMBER_OF_REQUESTS = "number_of_requests";
    
    private ServerViewModel serverViewModel;
    private SharedPreferences sharedPreferences;
    private ExecutorService executorService;
    
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
    
    private ActivityResultLauncher<String> importLauncher;
    private ActivityResultLauncher<String> createDocumentLauncher;
    private ActivityResultLauncher<String[]> openDocumentLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    
    private static final int STORAGE_PERMISSION_REQUEST = 100;
    
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
        loadSettings();
        
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
            saveSettingToPrefs(PREF_INFINITE_REQUESTS, isChecked);
        });
        
        layoutExportData.setOnClickListener(v -> exportServers());
        layoutImportData.setOnClickListener(v -> importServers());
        layoutShareData.setOnClickListener(v -> shareServers());
        
        setupAutoSave();
    }
    
    private void loadSettings() {
        int timeBetweenRequests = sharedPreferences.getInt(PREF_TIME_BETWEEN_REQUESTS, 5);
        int requestDelayMs = sharedPreferences.getInt(PREF_REQUEST_DELAY_MS, 100);
        int randomMinDelayMs = sharedPreferences.getInt(PREF_RANDOM_MIN_DELAY_MS, 50);
        int randomMaxDelayMs = sharedPreferences.getInt(PREF_RANDOM_MAX_DELAY_MS, 100);
        boolean infiniteRequests = sharedPreferences.getBoolean(PREF_INFINITE_REQUESTS, true);
        int numberOfRequests = sharedPreferences.getInt(PREF_NUMBER_OF_REQUESTS, 10);
        
        timeBetweenRequestsEditText.setText(String.valueOf(timeBetweenRequests));
        requestDelayEditText.setText(String.valueOf(requestDelayMs));
        randomMinDelayEditText.setText(String.valueOf(randomMinDelayMs));
        randomMaxDelayEditText.setText(String.valueOf(randomMaxDelayMs));
        infiniteRequestsCheckBox.setChecked(infiniteRequests);
        numberOfRequestsEditText.setText(String.valueOf(numberOfRequests));
        numberOfRequestsInputLayout.setEnabled(!infiniteRequests);
    }
    
    private void setupAutoSave() {
        timeBetweenRequestsEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                saveIntSetting(s.toString().trim(), PREF_TIME_BETWEEN_REQUESTS, 5);
            }
        });
        
        requestDelayEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                saveIntSetting(s.toString().trim(), PREF_REQUEST_DELAY_MS, 100);
            }
        });
        
        randomMinDelayEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                saveIntSetting(s.toString().trim(), PREF_RANDOM_MIN_DELAY_MS, 50);
            }
        });
        
        randomMaxDelayEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                saveIntSetting(s.toString().trim(), PREF_RANDOM_MAX_DELAY_MS, 100);
            }
        });
        
        numberOfRequestsEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                saveIntSetting(s.toString().trim(), PREF_NUMBER_OF_REQUESTS, 10);
            }
        });
    }
    
    private void saveIntSetting(String value, String key, int defaultValue) {
        if (!value.isEmpty()) {
            try {
                int intValue = Integer.parseInt(value);
                if (intValue >= 0) {
                    saveSettingToPrefs(key, intValue);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }
    }
    
    private void saveSettingToPrefs(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.apply();
    }
    
    private static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        
        @Override
        public void afterTextChanged(Editable s) {}
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
                Gson gson = new Gson();
                String json = gson.toJson(servers);
                
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
                Gson gson = new Gson();
                String json = gson.toJson(servers);
                
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
    
    public int getRequestDelayMs() {
        return sharedPreferences.getInt(PREF_REQUEST_DELAY_MS, 100);
    }
    
    public int getRandomMinDelayMs() {
        return sharedPreferences.getInt(PREF_RANDOM_MIN_DELAY_MS, 50);
    }
    
    public int getRandomMaxDelayMs() {
        return sharedPreferences.getInt(PREF_RANDOM_MAX_DELAY_MS, 100);
    }
    
    public boolean isInfiniteRequests() {
        return sharedPreferences.getBoolean(PREF_INFINITE_REQUESTS, true);
    }
    
    public int getNumberOfRequests() {
        return sharedPreferences.getInt(PREF_NUMBER_OF_REQUESTS, 10);
    }
}