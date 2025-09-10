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
    
    public static class ServerTestResult {
        public boolean success;
        public long responseTime;
        public String errorMessage;
        
        public ServerTestResult(boolean success, long responseTime, String errorMessage) {
            this.success = success;
            this.responseTime = responseTime;
            this.errorMessage = errorMessage;
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
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getAddress().equals(newItem.getAddress()) &&
                   oldItem.getRequestType() == newItem.getRequestType();
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
        testResults.put(serverId, new ServerTestResult(success, responseTime, errorMessage));
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
                if (result.success) {
                    statusIcon.setImageResource(android.R.drawable.checkbox_on_background);
                    statusIcon.setContentDescription(itemView.getContext().getString(R.string.status_success));
                } else {
                    statusIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                    statusIcon.setContentDescription(itemView.getContext().getString(R.string.status_error));
                }
            } else {
                statusIcon.setImageResource(android.R.drawable.ic_dialog_info);
                statusIcon.setContentDescription(itemView.getContext().getString(R.string.status_idle));
            }
        }
    }
}