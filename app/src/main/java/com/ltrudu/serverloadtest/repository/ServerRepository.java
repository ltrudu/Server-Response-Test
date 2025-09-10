package com.ltrudu.serverloadtest.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.ltrudu.serverloadtest.data.AppDatabase;
import com.ltrudu.serverloadtest.data.Server;
import com.ltrudu.serverloadtest.data.ServerDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerRepository {
    
    private ServerDao serverDao;
    private LiveData<List<Server>> allServers;
    private ExecutorService executorService;
    
    public ServerRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        serverDao = database.serverDao();
        allServers = serverDao.getAllServers();
        executorService = Executors.newFixedThreadPool(2);
    }
    
    public LiveData<List<Server>> getAllServers() {
        return allServers;
    }
    
    public List<Server> getAllServersSync() {
        return serverDao.getAllServersSync();
    }
    
    public LiveData<Server> getServerById(long id) {
        return serverDao.getServerById(id);
    }
    
    public void insertServer(Server server, OnServerInsertedListener listener) {
        executorService.execute(() -> {
            long id = serverDao.insertServer(server);
            if (listener != null) {
                listener.onServerInserted(id);
            }
        });
    }
    
    public void updateServer(Server server) {
        executorService.execute(() -> serverDao.updateServer(server));
    }
    
    public void deleteServer(Server server) {
        executorService.execute(() -> serverDao.deleteServer(server));
    }
    
    public void deleteAllServers() {
        executorService.execute(() -> serverDao.deleteAllServers());
    }
    
    public interface OnServerInsertedListener {
        void onServerInserted(long id);
    }
}