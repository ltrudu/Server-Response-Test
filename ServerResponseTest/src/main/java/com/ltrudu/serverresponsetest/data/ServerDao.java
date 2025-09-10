package com.ltrudu.serverresponsetest.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServerDao {
    
    @Query("SELECT * FROM servers ORDER BY name ASC")
    LiveData<List<Server>> getAllServers();
    
    @Query("SELECT * FROM servers ORDER BY name ASC")
    List<Server> getAllServersSync();
    
    @Query("SELECT * FROM servers WHERE id = :id")
    LiveData<Server> getServerById(long id);
    
    @Insert
    long insertServer(Server server);
    
    @Update
    void updateServer(Server server);
    
    @Delete
    void deleteServer(Server server);
    
    @Query("DELETE FROM servers")
    void deleteAllServers();
}