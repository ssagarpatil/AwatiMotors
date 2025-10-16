package com.icodedtech.awatimotors;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icodedtech.awatimotors.Adapters.ServiceRequestAdapter;
import com.icodedtech.awatimotors.Model.ServiceRequest;
import com.icodedtech.awatimotors.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilteredServicesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout tvNoData;
    private TextView tvResultCount;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ServiceRequestAdapter adapter;
    private List<ServiceRequest> filteredServicesList;
    private List<ServiceRequest> allServicesList;

    private String filterType;
    private String vehicleFilter; // Vehicle filter parameter
    private ValueEventListener serviceListener;
    private static final String TAG = "FilteredServicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_services);

        filterType = getIntent().getStringExtra("filter_type");
        vehicleFilter = getIntent().getStringExtra("vehicle_filter"); // Get vehicle filter

        if (filterType == null) {
            filterType = "All";
        }

        initViews();
        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        setupSearchView();
        loadFilteredServices();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        tvResultCount = findViewById(R.id.tv_result_count);

        mAuth = FirebaseAuth.getInstance();
        filteredServicesList = new ArrayList<>();
        allServicesList = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            String title;
            if (vehicleFilter != null && !vehicleFilter.isEmpty()) {
                title = vehicleFilter + " Services";
                if (!filterType.equalsIgnoreCase("All")) {
                    title = filterType + " " + vehicleFilter + " Services";
                }
            } else {
                title = filterType + " Services";
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ServiceRequestAdapter(this, filteredServicesList);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (adapter != null) {
                        adapter.getFilter().filter(query);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (adapter != null) {
                        adapter.getFilter().filter(newText);
                    }
                    return false;
                }
            });

            String hint = "Search " + filterType.toLowerCase() + " services...";
            if (vehicleFilter != null && !vehicleFilter.isEmpty()) {
                hint = "Search " + vehicleFilter.toLowerCase() + " services...";
            }
            searchView.setQueryHint(hint);
        }
    }

    private void loadFilteredServices() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated");
            hideProgressBar();
            showNoDataMessage();
            return;
        }

        showProgressBar();

        String userEmail = currentUser.getEmail().replace(".", "_");
        DatabaseReference serviceRef = mDatabase.child("Users")
                .child(userEmail)
                .child("ServiceInfo");

        serviceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allServicesList.clear();
                filteredServicesList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                        try {
                            ServiceRequest serviceRequest = serviceSnapshot.getValue(ServiceRequest.class);
                            if (serviceRequest != null) {
                                serviceRequest.setRequestId(serviceSnapshot.getKey());
                                allServicesList.add(serviceRequest);

                                // Apply filtering based on status and/or vehicle type
                                if (shouldIncludeService(serviceRequest)) {
                                    filteredServicesList.add(serviceRequest);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing service request: " + e.getMessage());
                        }
                    }

                    // Sort by server timestamp (newest first)
                    Collections.sort(filteredServicesList, (s1, s2) ->
                            Long.compare(s2.getServerTimestamp(), s1.getServerTimestamp()));

                    String filterInfo = filterType;
                    if (vehicleFilter != null && !vehicleFilter.isEmpty()) {
                        filterInfo += " " + vehicleFilter;
                    }
                    Log.d(TAG, "Total services: " + allServicesList.size() +
                            ", Filtered (" + filterInfo + "): " + filteredServicesList.size());
                } else {
                    Log.d(TAG, "No service requests found");
                }

                updateUI();
                hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load service requests: " + databaseError.getMessage());
                hideProgressBar();
                showNoDataMessage();
            }
        };

        serviceRef.addValueEventListener(serviceListener);
    }

    private boolean shouldIncludeService(ServiceRequest serviceRequest) {
        // First check vehicle filter if specified
        if (vehicleFilter != null && !vehicleFilter.isEmpty()) {
            String vehicleType = serviceRequest.getVehicleType();
            if (vehicleType != null) {
                String lowerVehicleType = vehicleType.toLowerCase();
                String lowerVehicleFilter = vehicleFilter.toLowerCase();

                boolean matchesVehicleFilter = false;

                if (lowerVehicleFilter.equals("car")) {
                    matchesVehicleFilter = lowerVehicleType.contains("car") ||
                            lowerVehicleType.contains("sedan") ||
                            lowerVehicleType.contains("suv") ||
                            lowerVehicleType.contains("hatchback") ||
                            lowerVehicleType.contains("truck") ||
                            lowerVehicleType.contains("bus") ||
                            lowerVehicleType.contains("van") ||
                            lowerVehicleType.contains("jeep");
                } else if (lowerVehicleFilter.equals("bike")) {
                    matchesVehicleFilter = lowerVehicleType.contains("bike") ||
                            lowerVehicleType.contains("motorcycle") ||
                            lowerVehicleType.contains("scooter") ||
                            lowerVehicleType.contains("auto") ||
                            lowerVehicleType.contains("rickshaw") ||
                            lowerVehicleType.contains("moped");
                } else {
                    // Direct match for other vehicle types
                    matchesVehicleFilter = lowerVehicleType.contains(lowerVehicleFilter);
                }

                if (!matchesVehicleFilter) {
                    return false; // Skip if vehicle type doesn't match
                }
            } else {
                return false; // Skip if no vehicle type and we're filtering by vehicle
            }
        }

        // Then check status filter
        String status = serviceRequest.getStatus();

        // Handle null status - default to "Pending"
        if (status == null) {
            status = "Pending";
        }

        // Filter based on the requested status type
        switch (filterType.toLowerCase()) {
            case "all":
                return true;
            case "pending":
                return "Pending".equalsIgnoreCase(status);
            case "completed":
                return "Completed".equalsIgnoreCase(status);
            case "in progress":
                return "In Progress".equalsIgnoreCase(status);
            case "cancelled":
                return "Cancelled".equalsIgnoreCase(status);
            default:
                // For any other filter type, try exact match
                return filterType.equalsIgnoreCase(status);
        }
    }

    private void updateUI() {
        runOnUiThread(() -> {
            if (adapter != null) {
                adapter.updateOriginalData(filteredServicesList);
                updateResultCount(filteredServicesList.size());
            }

            if (filteredServicesList.isEmpty()) {
                showNoDataMessage();
            } else {
                hideNoDataMessage();
            }
        });
    }

    private void updateResultCount(int count) {
        if (tvResultCount != null) {
            String countText;
            String filterDescription = filterType.toLowerCase();

            if (vehicleFilter != null && !vehicleFilter.isEmpty()) {
                filterDescription = vehicleFilter.toLowerCase();
                if (!filterType.equalsIgnoreCase("All")) {
                    filterDescription = filterType.toLowerCase() + " " + vehicleFilter.toLowerCase();
                }
            }

            if (count == 0) {
                countText = "No " + filterDescription + " services found";
            } else {
                countText = count + " " + filterDescription + " service" + (count != 1 ? "s" : "") + " found";
            }

            tvResultCount.setText(countText);
            tvResultCount.setVisibility(View.VISIBLE);
        }
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showNoDataMessage() {
        if (tvNoData != null) {
            tvNoData.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void hideNoDataMessage() {
        if (tvNoData != null) {
            tvNoData.setVisibility(View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceListener != null && mDatabase != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail().replace(".", "_");
                mDatabase.child("Users")
                        .child(userEmail)
                        .child("ServiceInfo")
                        .removeEventListener(serviceListener);
            }
        }
    }
}
