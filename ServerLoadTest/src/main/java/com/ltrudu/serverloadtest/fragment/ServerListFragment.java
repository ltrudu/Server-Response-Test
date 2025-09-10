package com.ltrudu.serverloadtest.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    private LinearLayout emptyStateLayout;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_list, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
        
        return view;
    }
    
    private void initializeViews(View view) {
        serverRecyclerView = view.findViewById(R.id.serverRecyclerView);
        addServerFab = view.findViewById(R.id.addServerFab);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
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
                
                // Show/hide empty state
                if (servers.isEmpty()) {
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    serverRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateLayout.setVisibility(View.GONE);
                    serverRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    
    private void setupClickListeners() {
        addServerFab.setOnClickListener(v -> showAddEditServerDialog(null));
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
        Button deleteButton = dialogView.findViewById(R.id.deleteButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        
        boolean isEdit = existingServer != null;
        
        if (isEdit) {
            nameEditText.setText(existingServer.getName());
            addressEditText.setText(existingServer.getAddress());
            if (existingServer.getPort() != null) {
                portEditText.setText(String.valueOf(existingServer.getPort()));
            }
            
            if (existingServer.getRequestType() == Server.RequestType.HTTPS) {
                requestTypeToggleGroup.check(R.id.httpButton);
            } else {
                requestTypeToggleGroup.check(R.id.pingButton);
            }
            
            // Show delete button only when editing
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            requestTypeToggleGroup.check(R.id.httpButton);
            deleteButton.setVisibility(View.GONE);
        }
        
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? R.string.edit_server : R.string.add_server)
                .setView(dialogView)
                .create();
        
        // Setup automatic capitalization for server name
        nameEditText.setFilters(new InputFilter[] { new CapitalizeFilter() });
        
        // Setup automatic HTTPS prefix handling
        requestTypeToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String currentAddress = addressEditText.getText().toString().trim();
                
                if (checkedId == R.id.httpButton) { // HTTPS selected
                    if (!currentAddress.startsWith("https://") && !currentAddress.startsWith("http://")) {
                        if (!currentAddress.isEmpty()) {
                            // Remove any existing protocol and add https://
                            currentAddress = currentAddress.replaceFirst("^(https?://)?", "");
                        }
                        addressEditText.setText("https://" + currentAddress);
                    }
                } else if (checkedId == R.id.pingButton) { // Ping selected
                    // Remove protocol prefix for ping
                    if (currentAddress.startsWith("https://")) {
                        currentAddress = currentAddress.substring(8);
                        addressEditText.setText(currentAddress);
                    } else if (currentAddress.startsWith("http://")) {
                        currentAddress = currentAddress.substring(7);
                        addressEditText.setText(currentAddress);
                    }
                }
            }
        });
        
        // Initialize address field if creating new HTTPS server
        if (!isEdit && requestTypeToggleGroup.getCheckedButtonId() == R.id.httpButton) {
            if (addressEditText.getText().toString().isEmpty()) {
                addressEditText.setText("https://");
            }
        }
        
        // Setup custom button click listeners
        saveButton.setOnClickListener(view -> {
            if (validateInput(nameInputLayout, addressInputLayout, portInputLayout, 
                    nameEditText, addressEditText, portEditText)) {
                saveServer(existingServer, nameEditText, addressEditText, portEditText, requestTypeToggleGroup);
                dialog.dismiss();
            }
        });
        
        cancelButton.setOnClickListener(view -> dialog.dismiss());
        
        deleteButton.setOnClickListener(view -> {
            showDeleteConfirmationDialog(existingServer);
            dialog.dismiss();
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
                ? Server.RequestType.HTTPS : Server.RequestType.PING;
        
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
        AlertDialog confirmDialog = new AlertDialog.Builder(requireContext())
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
                .create();
        
        confirmDialog.setOnShowListener(dialog -> {
            Button deleteButton = confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            deleteButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        });
        
        confirmDialog.show();
    }
    
    // InputFilter to automatically capitalize first letter of each word
    private static class CapitalizeFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, 
                                 android.text.Spanned dest, int dstart, int dend) {
            if (source.length() == 0) {
                return null; // No changes needed
            }
            
            StringBuilder sb = new StringBuilder();
            boolean capitalizeNext = dstart == 0 || (dstart > 0 && dest.charAt(dstart - 1) == ' ');
            
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (capitalizeNext && Character.isLetter(c)) {
                    sb.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    sb.append(c);
                }
                
                if (c == ' ') {
                    capitalizeNext = true;
                }
            }
            
            return sb.toString();
        }
    }
}