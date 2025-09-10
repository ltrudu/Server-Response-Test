package com.ltrudu.serverloadtest.data;

import java.util.List;

public class ExportData {
    private List<Server> servers;
    private Settings settings;
    private String exportVersion = "2.0"; // Version for future compatibility
    private long exportTimestamp;
    
    public ExportData() {
        this.exportTimestamp = System.currentTimeMillis();
    }
    
    public ExportData(List<Server> servers, Settings settings) {
        this.servers = servers;
        this.settings = settings;
        this.exportTimestamp = System.currentTimeMillis();
    }
    
    public List<Server> getServers() {
        return servers;
    }
    
    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
    
    public Settings getSettings() {
        return settings;
    }
    
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
    
    public String getExportVersion() {
        return exportVersion;
    }
    
    public void setExportVersion(String exportVersion) {
        this.exportVersion = exportVersion;
    }
    
    public long getExportTimestamp() {
        return exportTimestamp;
    }
    
    public void setExportTimestamp(long exportTimestamp) {
        this.exportTimestamp = exportTimestamp;
    }
}