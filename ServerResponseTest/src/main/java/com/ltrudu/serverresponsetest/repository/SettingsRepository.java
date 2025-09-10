package com.ltrudu.serverresponsetest.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.ltrudu.serverresponsetest.data.AppDatabase;
import com.ltrudu.serverresponsetest.data.Settings;
import com.ltrudu.serverresponsetest.data.SettingsDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsRepository {
    
    private SettingsDao settingsDao;
    private LiveData<Settings> settings;
    private ExecutorService executorService;
    
    public SettingsRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        settingsDao = database.settingsDao();
        settings = settingsDao.getSettings();
        executorService = Executors.newSingleThreadExecutor();
        
        // Ensure default settings exist
        executorService.execute(() -> {
            Settings currentSettings = settingsDao.getSettingsSync();
            if (currentSettings == null) {
                settingsDao.insertSettings(Settings.getDefault());
            }
        });
    }
    
    public LiveData<Settings> getSettings() {
        return settings;
    }
    
    public Settings getSettingsSync() {
        return settingsDao.getSettingsSync();
    }
    
    public void insertSettings(Settings settings) {
        executorService.execute(() -> settingsDao.insertSettings(settings));
    }
    
    public void updateSettings(Settings settings) {
        executorService.execute(() -> settingsDao.updateSettings(settings));
    }
    
    public void deleteAllSettings() {
        executorService.execute(() -> settingsDao.deleteAllSettings());
    }
    
    // Individual field update methods for efficiency
    public void updateTimeBetweenRequests(int value) {
        executorService.execute(() -> settingsDao.updateTimeBetweenRequests(value));
    }
    
    public void updateRequestDelayMs(int value) {
        executorService.execute(() -> settingsDao.updateRequestDelayMs(value));
    }
    
    public void updateRandomMinDelayMs(int value) {
        executorService.execute(() -> settingsDao.updateRandomMinDelayMs(value));
    }
    
    public void updateRandomMaxDelayMs(int value) {
        executorService.execute(() -> settingsDao.updateRandomMaxDelayMs(value));
    }
    
    public void updateInfiniteRequests(boolean value) {
        executorService.execute(() -> settingsDao.updateInfiniteRequests(value));
    }
    
    public void updateNumberOfRequests(int value) {
        executorService.execute(() -> settingsDao.updateNumberOfRequests(value));
    }
    
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}