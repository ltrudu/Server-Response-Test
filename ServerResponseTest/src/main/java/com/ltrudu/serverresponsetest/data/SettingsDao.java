package com.ltrudu.serverresponsetest.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SettingsDao {
    
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    LiveData<Settings> getSettings();
    
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    Settings getSettingsSync();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSettings(Settings settings);
    
    @Update
    void updateSettings(Settings settings);
    
    @Query("DELETE FROM settings")
    void deleteAllSettings();
    
    // Individual field updates for efficiency
    @Query("UPDATE settings SET time_between_requests = :value WHERE id = 1")
    void updateTimeBetweenRequests(int value);
    
    @Query("UPDATE settings SET request_delay_ms = :value WHERE id = 1")
    void updateRequestDelayMs(int value);
    
    @Query("UPDATE settings SET random_min_delay_ms = :value WHERE id = 1")
    void updateRandomMinDelayMs(int value);
    
    @Query("UPDATE settings SET random_max_delay_ms = :value WHERE id = 1")
    void updateRandomMaxDelayMs(int value);
    
    @Query("UPDATE settings SET infinite_requests = :value WHERE id = 1")
    void updateInfiniteRequests(boolean value);
    
    @Query("UPDATE settings SET number_of_requests = :value WHERE id = 1")
    void updateNumberOfRequests(int value);
}