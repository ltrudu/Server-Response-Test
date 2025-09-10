package com.ltrudu.serverresponsetest.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.ltrudu.serverresponsetest.data.Server;
import com.ltrudu.serverresponsetest.repository.ServerRepository;

import java.util.List;

public class ServerViewModel extends AndroidViewModel {
    
    private ServerRepository repository;
    private LiveData<List<Server>> allServers;
    
    public ServerViewModel(@NonNull Application application) {
        super(application);
        repository = new ServerRepository(application);
        allServers = repository.getAllServers();
    }
    
    public LiveData<List<Server>> getAllServers() {
        return allServers;
    }
    
    public List<Server> getAllServersSync() {
        return repository.getAllServersSync();
    }
    
    public LiveData<Server> getServerById(long id) {
        return repository.getServerById(id);
    }
    
    public void insertServer(Server server, ServerRepository.OnServerInsertedListener listener) {
        repository.insertServer(server, listener);
    }
    
    public void updateServer(Server server) {
        repository.updateServer(server);
    }
    
    public void deleteServer(Server server) {
        repository.deleteServer(server);
    }
    
    public void deleteAllServers() {
        repository.deleteAllServers();
    }
}