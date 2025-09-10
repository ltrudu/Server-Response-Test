package com.ltrudu.serverloadtest.data;

import androidx.room.TypeConverter;

public class Converters {
    
    @TypeConverter
    public static String fromRequestType(Server.RequestType requestType) {
        return requestType == null ? null : requestType.name();
    }
    
    @TypeConverter
    public static Server.RequestType toRequestType(String requestType) {
        return requestType == null ? null : Server.RequestType.valueOf(requestType);
    }
}