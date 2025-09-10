package com.ltrudu.serverresponsetest.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Settings {
    
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id = 1; // Single row for settings
    
    @ColumnInfo(name = "time_between_requests")
    private int timeBetweenRequests = 5000;
    
    @ColumnInfo(name = "request_delay_ms")
    private int requestDelayMs = 100;
    
    @ColumnInfo(name = "random_min_delay_ms")
    private int randomMinDelayMs = 50;
    
    @ColumnInfo(name = "random_max_delay_ms")
    private int randomMaxDelayMs = 100;
    
    @ColumnInfo(name = "infinite_requests")
    private boolean infiniteRequests = true;
    
    @ColumnInfo(name = "number_of_requests")
    private int numberOfRequests = 10;
    
    // Constructor
    public Settings() {}
    
    @Ignore
    public Settings(int timeBetweenRequests, int requestDelayMs, int randomMinDelayMs, 
                   int randomMaxDelayMs, boolean infiniteRequests, int numberOfRequests) {
        this.timeBetweenRequests = timeBetweenRequests;
        this.requestDelayMs = requestDelayMs;
        this.randomMinDelayMs = randomMinDelayMs;
        this.randomMaxDelayMs = randomMaxDelayMs;
        this.infiniteRequests = infiniteRequests;
        this.numberOfRequests = numberOfRequests;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getTimeBetweenRequests() {
        return timeBetweenRequests;
    }
    
    public void setTimeBetweenRequests(int timeBetweenRequests) {
        this.timeBetweenRequests = timeBetweenRequests;
    }
    
    public int getRequestDelayMs() {
        return requestDelayMs;
    }
    
    public void setRequestDelayMs(int requestDelayMs) {
        this.requestDelayMs = requestDelayMs;
    }
    
    public int getRandomMinDelayMs() {
        return randomMinDelayMs;
    }
    
    public void setRandomMinDelayMs(int randomMinDelayMs) {
        this.randomMinDelayMs = randomMinDelayMs;
    }
    
    public int getRandomMaxDelayMs() {
        return randomMaxDelayMs;
    }
    
    public void setRandomMaxDelayMs(int randomMaxDelayMs) {
        this.randomMaxDelayMs = randomMaxDelayMs;
    }
    
    public boolean isInfiniteRequests() {
        return infiniteRequests;
    }
    
    public void setInfiniteRequests(boolean infiniteRequests) {
        this.infiniteRequests = infiniteRequests;
    }
    
    public int getNumberOfRequests() {
        return numberOfRequests;
    }
    
    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }
    
    // Create default settings instance
    public static Settings getDefault() {
        return new Settings(5000, 100, 50, 100, true, 10);
    }
}