package com.ltrudu.serverloadtest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ltrudu.serverloadtest.R;
import com.ltrudu.serverloadtest.data.Server;

import java.util.HashMap;
import java.util.Map;

public class TestServerAdapter extends ListAdapter<Server, TestServerAdapter.TestServerViewHolder> {
    
    private Map<Long, ServerTestResult> testResults = new HashMap<>();
    
    public enum ServerStatus {
        IDLE,
        PENDING,
        PROCESSING,
        SUCCESS,
        ERROR
    }
    
    public static class ServerTestResult {
        public ServerStatus status;
        public boolean success;
        public long responseTime;
        public String errorMessage;
        
        public ServerTestResult(ServerStatus status, boolean success, long responseTime, String errorMessage) {
            this.status = status;
            this.success = success;
            this.responseTime = responseTime;
            this.errorMessage = errorMessage;
        }
        
        // Convenience constructors
        public static ServerTestResult pending() {
            return new ServerTestResult(ServerStatus.PENDING, false, 0, null);
        }
        
        public static ServerTestResult processing() {
            return new ServerTestResult(ServerStatus.PROCESSING, false, 0, null);
        }
        
        public static ServerTestResult success(long responseTime) {
            return new ServerTestResult(ServerStatus.SUCCESS, true, responseTime, null);
        }
        
        public static ServerTestResult error(long responseTime, String errorMessage) {
            return new ServerTestResult(ServerStatus.ERROR, false, responseTime, errorMessage);
        }
    }
    
    public TestServerAdapter() {
        super(DIFF_CALLBACK);
    }
    
    private static final DiffUtil.ItemCallback<Server> DIFF_CALLBACK = new DiffUtil.ItemCallback<Server>() {
        @Override
        public boolean areItemsTheSame(@NonNull Server oldItem, @NonNull Server newItem) {
            return oldItem.getId() == newItem.getId();
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull Server oldItem, @NonNull Server newItem) {
            // Always return false to force refresh when server list updates
            return false;
        }
    };
    
    @NonNull
    @Override
    public TestServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_server, parent, false);
        return new TestServerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TestServerViewHolder holder, int position) {
        Server server = getItem(position);
        ServerTestResult result = testResults.get(server.getId());
        holder.bind(server, result);
    }
    
    public void updateServerResult(long serverId, boolean success, long responseTime, String errorMessage) {
        ServerStatus status = success ? ServerStatus.SUCCESS : ServerStatus.ERROR;
        testResults.put(serverId, new ServerTestResult(status, success, responseTime, errorMessage));
        notifyDataSetChanged();
    }
    
    public void updateServerStatus(long serverId, ServerStatus status) {
        ServerTestResult existingResult = testResults.get(serverId);
        if (existingResult != null) {
            testResults.put(serverId, new ServerTestResult(status, existingResult.success, existingResult.responseTime, existingResult.errorMessage));
        } else {
            testResults.put(serverId, new ServerTestResult(status, false, 0, null));
        }
        notifyDataSetChanged();
    }
    
    public void setAllServersPending() {
        for (Map.Entry<Long, ServerTestResult> entry : testResults.entrySet()) {
            entry.setValue(ServerTestResult.pending());
        }
        notifyDataSetChanged();
    }
    
    public void clearResults() {
        testResults.clear();
        notifyDataSetChanged();
    }
    
    static class TestServerViewHolder extends RecyclerView.ViewHolder {
        private ImageView statusIcon;
        private TextView serverName;
        private TextView serverAddress;
        private TextView requestType;
        
        public TestServerViewHolder(@NonNull View itemView) {
            super(itemView);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            serverName = itemView.findViewById(R.id.serverName);
            serverAddress = itemView.findViewById(R.id.serverAddress);
            requestType = itemView.findViewById(R.id.requestType);
        }
        
        public void bind(Server server, ServerTestResult result) {
            serverName.setText(server.getName());
            
            String address = server.getAddress();
            if (server.getPort() != null) {
                address += ":" + server.getPort();
            }
            
            if (result != null) {
                address += " (" + result.responseTime + "ms)";
            }
            
            serverAddress.setText(address);
            requestType.setText(server.getRequestType().name());
            
            if (result != null) {
                switch (result.status) {
                    case PENDING:
                        statusIcon.setImageResource(android.R.drawable.ic_menu_recent_history);
                        statusIcon.setContentDescription("Pending");
                        break;
                    case PROCESSING:
                        statusIcon.setImageResource(android.R.drawable.ic_popup_sync);
                        statusIcon.setContentDescription("Processing");
                        break;
                    case SUCCESS:
                        statusIcon.setImageResource(android.R.drawable.checkbox_on_background);
                        statusIcon.setContentDescription(itemView.getContext().getString(R.string.status_success));
                        break;
                    case ERROR:
                        statusIcon.setImageResource(android.R.drawable.ic_delete);
                        statusIcon.setContentDescription(itemView.getContext().getString(R.string.status_error));
                        break;
                    case IDLE:
                    default:
                        statusIcon.setImageResource(android.R.drawable.ic_dialog_info);
                        statusIcon.setContentDescription(itemView.getContext().getString(R.string.status_idle));
                        break;
                }
            } else {
                statusIcon.setImageResource(android.R.drawable.ic_dialog_info);
                statusIcon.setContentDescription(itemView.getContext().getString(R.string.status_idle));
            }
        }
    }
}