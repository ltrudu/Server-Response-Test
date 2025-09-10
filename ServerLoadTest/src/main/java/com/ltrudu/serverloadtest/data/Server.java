package com.ltrudu.serverloadtest.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "servers")
public class Server {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private String address;
    private Integer port;
    private RequestType requestType;
    
    public enum RequestType {
        HTTP, PING
    }
    
    public Server() {}
    
    public Server(String name, String address, Integer port, RequestType requestType) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.requestType = requestType;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public RequestType getRequestType() {
        return requestType;
    }
    
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}