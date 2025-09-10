package com.ltrudu.serverloadtest.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ltrudu.serverloadtest.R;
import com.ltrudu.serverloadtest.adapter.ServerAdapter;
import com.ltrudu.serverloadtest.data.Server;
import com.ltrudu.serverloadtest.viewmodel.ServerViewModel;

public class ServerListFragment extends Fragment {
    
    private ServerViewModel serverViewModel;
    private RecyclerView serverRecyclerView;
    private ServerAdapter serverAdapter;
    private FloatingActionButton addServerFab;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_list, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
        setupSwipeToDelete();
        
        return view;
    }
    
    private void initializeViews(View view) {
        serverRecyclerView = view.findViewById(R.id.serverRecyclerView);
        addServerFab = view.findViewById(R.id.addServerFab);
    }
    
    private void setupRecyclerView() {
        serverAdapter = new ServerAdapter();
        serverRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        serverRecyclerView.setAdapter(serverAdapter);
        
        serverAdapter.setOnItemClickListener(server -> showAddEditServerDialog(server));
    }
    
    private void setupViewModel() {
        serverViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        serverViewModel.getAllServers().observe(getViewLifecycleOwner(), servers -> {
            if (servers != null) {
                serverAdapter.submitList(servers);
            }
        });
    }
    
    private void setupClickListeners() {
        addServerFab.setOnClickListener(v -> showAddEditServerDialog(null));
    }
    
    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Server server = serverAdapter.getServerAt(position);
                showDeleteConfirmationDialog(server);
            }
        };
        
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(serverRecyclerView);
    }
    
    private void showAddEditServerDialog(Server existingServer) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_server, null);
        
        TextInputLayout nameInputLayout = dialogView.findViewById(R.id.nameInputLayout);
        TextInputLayout addressInputLayout = dialogView.findViewById(R.id.addressInputLayout);
        TextInputLayout portInputLayout = dialogView.findViewById(R.id.portInputLayout);
        TextInputEditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        TextInputEditText addressEditText = dialogView.findViewById(R.id.addressEditText);
        TextInputEditText portEditText = dialogView.findViewById(R.id.portEditText);
        MaterialButtonToggleGroup requestTypeToggleGroup = dialogView.findViewById(R.id.requestTypeToggleGroup);
        Button httpButton = dialogView.findViewById(R.id.httpButton);
        Button pingButton = dialogView.findViewById(R.id.pingButton);
        
        boolean isEdit = existingServer != null;
        
        if (isEdit) {
            nameEditText.setText(existingServer.getName());
            addressEditText.setText(existingServer.getAddress());
            if (existingServer.getPort() != null) {
                portEditText.setText(String.valueOf(existingServer.getPort()));
            }
            
            if (existingServer.getRequestType() == Server.RequestType.HTTP) {
                requestTypeToggleGroup.check(R.id.httpButton);
            } else {
                requestTypeToggleGroup.check(R.id.pingButton);
            }
        } else {
            requestTypeToggleGroup.check(R.id.httpButton);
        }
        
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? R.string.edit_server : R.string.add_server)
                .setView(dialogView)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                if (validateInput(nameInputLayout, addressInputLayout, portInputLayout, 
                        nameEditText, addressEditText, portEditText)) {
                    saveServer(existingServer, nameEditText, addressEditText, portEditText, requestTypeToggleGroup);
                    dialog.dismiss();
                }
            });
        });
        
        dialog.show();
    }
    
    private boolean validateInput(TextInputLayout nameInputLayout, TextInputLayout addressInputLayout, 
                                  TextInputLayout portInputLayout, TextInputEditText nameEditText, 
                                  TextInputEditText addressEditText, TextInputEditText portEditText) {
        boolean isValid = true;
        
        nameInputLayout.setError(null);
        addressInputLayout.setError(null);
        portInputLayout.setError(null);
        
        String name = nameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError(getString(R.string.error_empty_name));
            isValid = false;
        }
        
        String address = addressEditText.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            addressInputLayout.setError(getString(R.string.error_empty_address));
            isValid = false;
        }
        
        String portText = portEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(portText)) {
            try {
                int port = Integer.parseInt(portText);
                if (port < 1 || port > 65535) {
                    portInputLayout.setError(getString(R.string.error_invalid_port));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                portInputLayout.setError(getString(R.string.error_invalid_port));
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    private void saveServer(Server existingServer, TextInputEditText nameEditText, 
                           TextInputEditText addressEditText, TextInputEditText portEditText, 
                           MaterialButtonToggleGroup requestTypeToggleGroup) {
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String portText = portEditText.getText().toString().trim();
        Integer port = TextUtils.isEmpty(portText) ? null : Integer.parseInt(portText);
        
        Server.RequestType requestType = requestTypeToggleGroup.getCheckedButtonId() == R.id.httpButton 
                ? Server.RequestType.HTTP : Server.RequestType.PING;
        
        if (existingServer == null) {
            Server newServer = new Server(name, address, port, requestType);
            serverViewModel.insertServer(newServer, null);
        } else {
            existingServer.setName(name);
            existingServer.setAddress(address);
            existingServer.setPort(port);
            existingServer.setRequestType(requestType);
            serverViewModel.updateServer(existingServer);
        }
    }
    
    private void showDeleteConfirmationDialog(Server server) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_server)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    serverViewModel.deleteServer(server);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    serverAdapter.notifyDataSetChanged();
                })
                .setOnCancelListener(dialog -> {
                    serverAdapter.notifyDataSetChanged();
                })
                .show();
    }
}