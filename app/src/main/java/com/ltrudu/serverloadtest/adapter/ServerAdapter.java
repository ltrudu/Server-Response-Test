package com.ltrudu.serverloadtest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ltrudu.serverloadtest.R;
import com.ltrudu.serverloadtest.data.Server;

public class ServerAdapter extends ListAdapter<Server, ServerAdapter.ServerViewHolder> {
    
    private OnItemClickListener onItemClickListener;
    private OnItemSwipeListener onItemSwipeListener;
    
    public interface OnItemClickListener {
        void onItemClick(Server server);
    }
    
    public interface OnItemSwipeListener {
        void onItemSwiped(Server server);
    }
    
    public ServerAdapter() {
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
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_server, parent, false);
        return new ServerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    public void setOnItemSwipeListener(OnItemSwipeListener listener) {
        this.onItemSwipeListener = listener;
    }
    
    class ServerViewHolder extends RecyclerView.ViewHolder {
        private TextView serverName;
        private TextView serverAddress;
        private TextView requestType;
        
        public ServerViewHolder(@NonNull View itemView) {
            super(itemView);
            serverName = itemView.findViewById(R.id.serverName);
            serverAddress = itemView.findViewById(R.id.serverAddress);
            requestType = itemView.findViewById(R.id.requestType);
            
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(getItem(getAdapterPosition()));
                }
            });
        }
        
        public void bind(Server server) {
            serverName.setText(server.getName());
            
            String address = server.getAddress();
            if (server.getPort() != null) {
                address += ":" + server.getPort();
            }
            serverAddress.setText(address);
            
            requestType.setText(server.getRequestType().name());
        }
    }
    
    public Server getServerAt(int position) {
        return getItem(position);
    }
}