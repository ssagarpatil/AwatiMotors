//package com.icodedtech.awatimotors.Fragments;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.icodedtech.awatimotors.Activity.AddServiceActivity;
//import com.icodedtech.awatimotors.Activity.AllServicesActivity;
//import com.icodedtech.awatimotors.R;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class HomeFragment extends Fragment {
//
//    private TextView tvWelcome, tvCarCount, tvBikeCount;
//    private LinearLayout tvNoData;
//    private RecyclerView rvRecentServices;
//    private FloatingActionButton fabAddService;
//    private CardView cardBookService, cardFindCenter;
//    private ProgressBar progressBar;
//    private Button btnViewAll;
//
//    private FirebaseAuth mAuth;
//    private DatabaseReference mDatabase;
//    private ServiceRequestAdapter adapter;
//    private List<ServiceRequest> serviceRequestList;
//    private List<ServiceRequest> allServicesList;
//    private ValueEventListener serviceListener;
//
//    private static final String TAG = "HomeFragment";
//    private static final int RECENT_LIMIT = 5;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        initViews(view);
//        setupFirebase();
//        setupWelcomeMessage();
//        setupRecyclerView();
//        setupClickListeners();
//        loadServiceRequests();
//
//        return view;
//    }
//
//    private void initViews(View view) {
//        tvWelcome = view.findViewById(R.id.tv_welcome);
//        tvCarCount = view.findViewById(R.id.tv_car_count);
//        tvBikeCount = view.findViewById(R.id.tv_bike_count);
//        tvNoData = view.findViewById(R.id.tv_no_data);
//        rvRecentServices = view.findViewById(R.id.rv_recent_services);
//        fabAddService = view.findViewById(R.id.fab_add_service);
//        cardBookService = view.findViewById(R.id.card_book_service);
//        cardFindCenter = view.findViewById(R.id.card_find_center);
//        progressBar = view.findViewById(R.id.progress_bar);
//        btnViewAll = view.findViewById(R.id.btn_view_all);
//
//        mAuth = FirebaseAuth.getInstance();
//        serviceRequestList = new ArrayList<>();
//        allServicesList = new ArrayList<>();
//    }
//
//    private void setupFirebase() {
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//    }
//
//    private void setupWelcomeMessage() {
//        if (tvWelcome != null) {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser != null) {
//                String displayName = currentUser.getDisplayName();
//                String welcomeMessage = "Welcome, " + (displayName != null ? displayName : "User") + "!";
//                tvWelcome.setText(welcomeMessage);
//            } else {
//                tvWelcome.setText("Welcome to Awati Motors!");
//            }
//        }
//    }
//
//    private void setupRecyclerView() {
//        if (rvRecentServices != null) {
//            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
//                @Override
//                public boolean canScrollVertically() {
//                    return false;
//                }
//            };
//            rvRecentServices.setLayoutManager(layoutManager);
//            rvRecentServices.setHasFixedSize(false);
//            rvRecentServices.setNestedScrollingEnabled(false);
//
//            adapter = new ServiceRequestAdapter(getContext(), serviceRequestList);
//            rvRecentServices.setAdapter(adapter);
//        }
//    }
//
//    private void setupClickListeners() {
//        fabAddService.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), AddServiceActivity.class);
//            startActivity(intent);
//        });
//
//        cardBookService.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), AddServiceActivity.class);
//            startActivity(intent);
//        });
//
//        cardFindCenter.setOnClickListener(v -> {
//            requireActivity().getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, new com.icodedtech.awatimotors.Fragments.ServiceCenterFragment())
//                    .addToBackStack(null)
//                    .commit();
//        });
//
//        btnViewAll.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), AllServicesActivity.class);
//            startActivity(intent);
//        });
//
//        btnViewAll.setVisibility(View.VISIBLE);
//    }
//
//    private void loadServiceRequests() {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            Log.e(TAG, "User not authenticated");
//            hideProgressBar();
//            showNoDataMessage();
//            updateVehicleCounts(0, 0);
//            return;
//        }
//
//        showProgressBar();
//
//        String userEmail = currentUser.getEmail().replace(".", "_");
//        DatabaseReference serviceRef = mDatabase.child("Users")
//                .child(userEmail)
//                .child("ServiceInfo");
//
//        if (serviceListener != null) {
//            serviceRef.removeEventListener(serviceListener);
//        }
//
//        serviceListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                allServicesList.clear();
//                serviceRequestList.clear();
//
//                int carCount = 0;
//                int bikeCount = 0;
//
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
//                        try {
//                            ServiceRequest serviceRequest = serviceSnapshot.getValue(ServiceRequest.class);
//                            if (serviceRequest != null) {
//                                allServicesList.add(serviceRequest);
//
//                                // Count vehicles by type
//                                String vehicleType = serviceRequest.getVehicleType();
//                                if (vehicleType != null) {
//                                    if (vehicleType.toLowerCase().contains("car")) {
//                                        carCount++;
//                                    } else if (vehicleType.toLowerCase().contains("bike")) {
//                                        bikeCount++;
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, "Error parsing service request: " + e.getMessage());
//                        }
//                    }
//
//                    Collections.sort(allServicesList, (s1, s2) ->
//                            Long.compare(s2.getServerTimestamp(), s1.getServerTimestamp()));
//
//                    // Get latest 5 for home display
//                    int limit = Math.min(allServicesList.size(), RECENT_LIMIT);
//                    for (int i = 0; i < limit; i++) {
//                        serviceRequestList.add(allServicesList.get(i));
//                    }
//
//                    Log.d(TAG, "Total services: " + allServicesList.size() +
//                            ", Cars: " + carCount + ", Bikes: " + bikeCount +
//                            ", Showing: " + serviceRequestList.size());
//                } else {
//                    Log.d(TAG, "No service requests found");
//                }
//
//                updateVehicleCounts(carCount, bikeCount);
//                updateUI();
//                hideProgressBar();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to load service requests: " + databaseError.getMessage());
//                if (getContext() != null) {
//                    Toast.makeText(getContext(), "Failed to load data: " + databaseError.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                }
//                hideProgressBar();
//                showNoDataMessage();
//                updateVehicleCounts(0, 0);
//            }
//        };
//
//        serviceRef.addValueEventListener(serviceListener);
//    }
//
//    private void updateVehicleCounts(int carCount, int bikeCount) {
//        if (getActivity() == null) return;
//
//        getActivity().runOnUiThread(() -> {
//            if (tvCarCount != null) {
//                tvCarCount.setText(String.valueOf(carCount));
//            }
//            if (tvBikeCount != null) {
//                tvBikeCount.setText(String.valueOf(bikeCount));
//            }
//        });
//    }
//
//    private void updateUI() {
//        if (getActivity() == null) return;
//
//        getActivity().runOnUiThread(() -> {
//            if (adapter != null) {
//                adapter.updateData(serviceRequestList);
//                adapter.notifyDataSetChanged();
//                Log.d(TAG, "Adapter updated with " + serviceRequestList.size() + " items");
//            }
//
//            if (serviceRequestList.isEmpty()) {
//                showNoDataMessage();
//                btnViewAll.setVisibility(View.VISIBLE);
//            } else {
//                hideNoDataMessage();
//                btnViewAll.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    private void showProgressBar() {
//        if (progressBar != null) {
//            progressBar.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void hideProgressBar() {
//        if (progressBar != null) {
//            progressBar.setVisibility(View.GONE);
//        }
//    }
//
//    private void showNoDataMessage() {
//        if (tvNoData != null) {
//            tvNoData.setVisibility(View.VISIBLE);
//        }
//        if (rvRecentServices != null) {
//            rvRecentServices.setVisibility(View.GONE);
//        }
//    }
//
//    private void hideNoDataMessage() {
//        if (tvNoData != null) {
//            tvNoData.setVisibility(View.GONE);
//        }
//        if (rvRecentServices != null) {
//            rvRecentServices.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (adapter != null) {
//            loadServiceRequests();
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (serviceListener != null && mDatabase != null) {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser != null) {
//                String userEmail = currentUser.getEmail().replace(".", "_");
//                mDatabase.child("Users")
//                        .child(userEmail)
//                        .child("ServiceInfo")
//                        .removeEventListener(serviceListener);
//            }
//        }
//    }
//
//    // ================================
//    // SERVICE REQUEST MODEL CLASS
//    // ================================
//    public static class ServiceRequest {
//        private String customerName;
//        private String vehicleNumber;
//        private String mobileNumber;
//        private String vehicleType;
//        private String serviceDate;
//        private String serviceTime;
//        private String serviceNotes;
//        private String status;
//        private String timestamp;
//        private long serverTimestamp;
//        private String serviceCenter;
//        private String userEmail;
//        private List<String> imageUrls;
//
//        public ServiceRequest() {}
//
//        public String getCustomerName() { return customerName; }
//        public void setCustomerName(String customerName) { this.customerName = customerName; }
//
//        public String getVehicleNumber() { return vehicleNumber; }
//        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
//
//        public String getMobileNumber() { return mobileNumber; }
//        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
//
//        public String getVehicleType() { return vehicleType; }
//        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
//
//        public String getServiceDate() { return serviceDate; }
//        public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }
//
//        public String getServiceTime() { return serviceTime; }
//        public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }
//
//        public String getServiceNotes() { return serviceNotes; }
//        public void setServiceNotes(String serviceNotes) { this.serviceNotes = serviceNotes; }
//
//        public String getStatus() { return status; }
//        public void setStatus(String status) { this.status = status; }
//
//        public String getTimestamp() { return timestamp; }
//        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
//
//        public long getServerTimestamp() { return serverTimestamp; }
//        public void setServerTimestamp(long serverTimestamp) { this.serverTimestamp = serverTimestamp; }
//
//        public String getServiceCenter() { return serviceCenter; }
//        public void setServiceCenter(String serviceCenter) { this.serviceCenter = serviceCenter; }
//
//        public String getUserEmail() { return userEmail; }
//        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
//
//        public List<String> getImageUrls() { return imageUrls; }
//        public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
//    }
//
//    // ================================
//    // RECYCLERVIEW ADAPTER CLASS (same as before)
//    // ================================
//    public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ServiceViewHolder> {
//
//        private Context context;
//        private List<ServiceRequest> serviceRequests;
//
//        public ServiceRequestAdapter(Context context, List<ServiceRequest> serviceRequests) {
//            this.context = context;
//            this.serviceRequests = serviceRequests;
//        }
//
//        @NonNull
//        @Override
//        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(context).inflate(R.layout.item_service_request, parent, false);
//            return new ServiceViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
//            ServiceRequest service = serviceRequests.get(position);
//            holder.bind(service, position);
//        }
//
//        @Override
//        public int getItemCount() {
//            return serviceRequests != null ? serviceRequests.size() : 0;
//        }
//
//        public void updateData(List<ServiceRequest> newServiceRequests) {
//            this.serviceRequests = newServiceRequests;
//            notifyDataSetChanged();
//        }
//
//        class ServiceViewHolder extends RecyclerView.ViewHolder {
//            TextView tvCustomerName, tvVehicleInfo, tvStatus, tvServiceDate,
//                    tvServiceTime, tvMobileNumber, tvServiceNotes, tvTimestamp;
//            ImageView ivCall, ivMore;
//
//            public ServiceViewHolder(@NonNull View itemView) {
//                super(itemView);
//
//                tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
//                tvVehicleInfo = itemView.findViewById(R.id.tv_vehicle_info);
//                tvStatus = itemView.findViewById(R.id.tv_status);
//                tvServiceDate = itemView.findViewById(R.id.tv_service_date);
//                tvServiceTime = itemView.findViewById(R.id.tv_service_time);
//                tvMobileNumber = itemView.findViewById(R.id.tv_mobile_number);
//                tvServiceNotes = itemView.findViewById(R.id.tv_service_notes);
//                tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
//                ivCall = itemView.findViewById(R.id.iv_call);
//                ivMore = itemView.findViewById(R.id.iv_more);
//            }
//
//            public void bind(ServiceRequest service, int position) {
//                tvCustomerName.setText(service.getCustomerName() != null ? service.getCustomerName() : "Unknown");
//
//                String vehicleInfo = (service.getVehicleType() != null ? service.getVehicleType() : "Vehicle") +
//                        " • " + (service.getVehicleNumber() != null ? service.getVehicleNumber() : "N/A");
//                tvVehicleInfo.setText(vehicleInfo);
//
//                String status = service.getStatus() != null ? service.getStatus() : "Pending";
//                tvStatus.setText(status);
//                setStatusBackground(status);
//
//                tvServiceDate.setText(service.getServiceDate() != null ? service.getServiceDate() : "Not Set");
//                tvServiceTime.setText(service.getServiceTime() != null ? service.getServiceTime() : "Not Set");
//                tvMobileNumber.setText(service.getMobileNumber() != null ? service.getMobileNumber() : "N/A");
//
//                if (!TextUtils.isEmpty(service.getServiceNotes())) {
//                    tvServiceNotes.setText(service.getServiceNotes());
//                    tvServiceNotes.setVisibility(View.VISIBLE);
//                } else {
//                    tvServiceNotes.setVisibility(View.GONE);
//                }
//
//                String formattedTimestamp = formatTimestamp(service.getTimestamp());
//                tvTimestamp.setText("Requested on " + formattedTimestamp);
//
//                itemView.setOnClickListener(v -> {
//                    Toast.makeText(context, "Service request by " + service.getCustomerName(),
//                            Toast.LENGTH_SHORT).show();
//                });
//
//                ivCall.setOnClickListener(v -> makePhoneCall(service.getMobileNumber()));
//
//                ivMore.setOnClickListener(v -> {
//                    Toast.makeText(context, "More options for " + service.getCustomerName(),
//                            Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            private void setStatusBackground(String status) {
//                int backgroundRes;
//                switch (status.toLowerCase()) {
//                    case "completed":
//                        backgroundRes = R.drawable.status_completed_bg;
//                        break;
//                    case "cancelled":
//                        backgroundRes = R.drawable.status_cancelled_bg;
//                        break;
//                    case "in progress":
//                        backgroundRes = R.drawable.status_in_progress_bg;
//                        break;
//                    default:
//                        backgroundRes = R.drawable.status_pending_bg;
//                        break;
//                }
//                tvStatus.setBackgroundResource(backgroundRes);
//            }
//
//            private String formatTimestamp(String timestamp) {
//                if (TextUtils.isEmpty(timestamp)) return "Unknown";
//
//                try {
//                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                    Date date = inputFormat.parse(timestamp);
//                    return outputFormat.format(date);
//                } catch (ParseException e) {
//                    return timestamp;
//                }
//            }
//
//            private void makePhoneCall(String phoneNumber) {
//                if (TextUtils.isEmpty(phoneNumber)) return;
//
//                try {
//                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
//                    context.startActivity(callIntent);
//                } catch (Exception e) {
//                    Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//}



//
//package com.icodedtech.awatimotors.Fragments;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.icodedtech.awatimotors.Activity.AddServiceActivity;
//import com.icodedtech.awatimotors.Activity.AllServicesActivity;
//import com.icodedtech.awatimotors.FilteredServicesActivity;
//import com.icodedtech.awatimotors.R;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class HomeFragment extends Fragment {
//
//    private TextView tvWelcome, tvCarCount, tvBikeCount, tvPendingCount, tvCompletedCount, tvTotalCount;
//    private LinearLayout tvNoData;
//    private RecyclerView rvRecentServices;
//    private FloatingActionButton fabAddService;
//    private CardView cardPendingServices, cardCompletedServices, cardTotalServices;
//    private ProgressBar progressBar;
//    private Button btnViewAll;
//
//    private FirebaseAuth mAuth;
//    private DatabaseReference mDatabase;
//    private ServiceRequestAdapter adapter;
//    private List<ServiceRequest> serviceRequestList;
//    private List<ServiceRequest> allServicesList;
//    private ValueEventListener serviceListener;
//
//    // Statistics counters
//    private int totalServices = 0;
//    private int pendingServices = 0;
//    private int completedServices = 0;
//    private int inProgressServices = 0;
//    private int cancelledServices = 0;
//    private int carCount = 0;
//    private int bikeCount = 0;
//
//    private static final String TAG = "HomeFragment";
//    private static final int RECENT_LIMIT = 5;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        initViews(view);
//        setupFirebase();
//        setupWelcomeMessage();
//        setupRecyclerView();
//        setupClickListeners();
//        loadServiceRequests();
//
//        return view;
//    }
//
//    private void initViews(View view) {
//        tvWelcome = view.findViewById(R.id.tv_welcome);
//        tvCarCount = view.findViewById(R.id.tv_car_count);
//        tvBikeCount = view.findViewById(R.id.tv_bike_count);
//        tvPendingCount = view.findViewById(R.id.tv_pending_count);
//        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
//        tvTotalCount = view.findViewById(R.id.tv_total_count);
//        tvNoData = view.findViewById(R.id.tv_no_data);
//        rvRecentServices = view.findViewById(R.id.rv_recent_services);
//        fabAddService = view.findViewById(R.id.fab_add_service);
//        cardPendingServices = view.findViewById(R.id.card_pending_services);
//        cardCompletedServices = view.findViewById(R.id.card_completed_services);
//        cardTotalServices = view.findViewById(R.id.card_total_services);
//        progressBar = view.findViewById(R.id.progress_bar);
//        btnViewAll = view.findViewById(R.id.btn_view_all);
//
//        mAuth = FirebaseAuth.getInstance();
//        serviceRequestList = new ArrayList<>();
//        allServicesList = new ArrayList<>();
//    }
//
//    private void setupFirebase() {
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//    }
//
//    private void setupWelcomeMessage() {
//        if (tvWelcome != null) {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser != null) {
//                String displayName = currentUser.getDisplayName();
//                String welcomeMessage = "Welcome, " + (displayName != null ? displayName : "User") + "!";
//                tvWelcome.setText(welcomeMessage);
//            } else {
//                tvWelcome.setText("Welcome to Awati Motors!");
//            }
//        }
//    }
//
//    private void setupRecyclerView() {
//        if (rvRecentServices != null) {
//            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
//                @Override
//                public boolean canScrollVertically() {
//                    return false;
//                }
//            };
//            rvRecentServices.setLayoutManager(layoutManager);
//            rvRecentServices.setHasFixedSize(false);
//            rvRecentServices.setNestedScrollingEnabled(false);
//
//            adapter = new ServiceRequestAdapter(getContext(), serviceRequestList);
//            rvRecentServices.setAdapter(adapter);
//        }
//    }
//
//    private void setupClickListeners() {
//        fabAddService.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), AddServiceActivity.class);
//            startActivity(intent);
//        });
//
//        btnViewAll.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), AllServicesActivity.class);
//            startActivity(intent);
//        });
//
//        // Statistics card click listeners
//        cardPendingServices.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), FilteredServicesActivity.class);
//            intent.putExtra("filter_type", "Pending");
//            startActivity(intent);
//        });
//
//        cardCompletedServices.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), FilteredServicesActivity.class);
//            intent.putExtra("filter_type", "Completed");
//            startActivity(intent);
//        });
//
//        cardTotalServices.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), AllServicesActivity.class);
//            startActivity(intent);
//        });
//
//        btnViewAll.setVisibility(View.VISIBLE);
//    }
//
//    private void loadServiceRequests() {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            Log.e(TAG, "User not authenticated");
//            hideProgressBar();
//            showNoDataMessage();
//            updateAllStatistics();
//            return;
//        }
//
//        showProgressBar();
//
//        String userEmail = currentUser.getEmail().replace(".", "_");
//        DatabaseReference serviceRef = mDatabase.child("Users")
//                .child(userEmail)
//                .child("ServiceInfo");
//
//        if (serviceListener != null) {
//            serviceRef.removeEventListener(serviceListener);
//        }
//
//        serviceListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                allServicesList.clear();
//                serviceRequestList.clear();
//                resetCounters();
//
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
//                        try {
//                            ServiceRequest serviceRequest = serviceSnapshot.getValue(ServiceRequest.class);
//                            if (serviceRequest != null) {
//                                allServicesList.add(serviceRequest);
//                                updateCounters(serviceRequest);
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, "Error parsing service request: " + e.getMessage());
//                        }
//                    }
//
//                    Collections.sort(allServicesList, (s1, s2) ->
//                            Long.compare(s2.getServerTimestamp(), s1.getServerTimestamp()));
//
//                    // Get latest 5 for home display
//                    int limit = Math.min(allServicesList.size(), RECENT_LIMIT);
//                    for (int i = 0; i < limit; i++) {
//                        serviceRequestList.add(allServicesList.get(i));
//                    }
//
//                    Log.d(TAG, "Total services: " + totalServices +
//                            ", Pending: " + pendingServices + ", Completed: " + completedServices +
//                            ", Cars: " + carCount + ", Bikes: " + bikeCount +
//                            ", Showing: " + serviceRequestList.size());
//                } else {
//                    Log.d(TAG, "No service requests found");
//                }
//
//                updateAllStatistics();
//                updateUI();
//                hideProgressBar();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to load service requests: " + databaseError.getMessage());
//                if (getContext() != null) {
//                    Toast.makeText(getContext(), "Failed to load data: " + databaseError.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                }
//                hideProgressBar();
//                showNoDataMessage();
//                updateAllStatistics();
//            }
//        };
//
//        serviceRef.addValueEventListener(serviceListener);
//    }
//
//    private void resetCounters() {
//        totalServices = 0;
//        pendingServices = 0;
//        completedServices = 0;
//        inProgressServices = 0;
//        cancelledServices = 0;
//        carCount = 0;
//        bikeCount = 0;
//    }
//
//    private void updateCounters(ServiceRequest serviceRequest) {
//        totalServices++;
//
//        // Count by status
//        String status = serviceRequest.getStatus();
//        if (status != null) {
//            switch (status.toLowerCase()) {
//                case "pending":
//                    pendingServices++;
//                    break;
//                case "completed":
//                    completedServices++;
//                    break;
//                case "in progress":
//                    inProgressServices++;
//                    break;
//                case "cancelled":
//                    cancelledServices++;
//                    break;
//            }
//        } else {
//            pendingServices++; // Default to pending if status is null
//        }
//
//        // Count by vehicle type
//        String vehicleType = serviceRequest.getVehicleType();
//        if (vehicleType != null) {
//            if (vehicleType.toLowerCase().contains("car")) {
//                carCount++;
//            } else if (vehicleType.toLowerCase().contains("bike") ||
//                    vehicleType.toLowerCase().contains("motorcycle")) {
//                bikeCount++;
//            }
//        }
//    }
//
//    private void updateAllStatistics() {
//        if (getActivity() == null) return;
//
//        getActivity().runOnUiThread(() -> {
//            // Update service statistics
//            if (tvPendingCount != null) {
//                tvPendingCount.setText(String.valueOf(pendingServices));
//            }
//            if (tvCompletedCount != null) {
//                tvCompletedCount.setText(String.valueOf(completedServices));
//            }
//            if (tvTotalCount != null) {
//                tvTotalCount.setText(String.valueOf(totalServices));
//            }
//
//            // Update vehicle statistics
//            if (tvCarCount != null) {
//                tvCarCount.setText(String.valueOf(carCount));
//            }
//            if (tvBikeCount != null) {
//                tvBikeCount.setText(String.valueOf(bikeCount));
//            }
//        });
//    }
//
//    private void updateUI() {
//        if (getActivity() == null) return;
//
//        getActivity().runOnUiThread(() -> {
//            if (adapter != null) {
//                adapter.updateData(serviceRequestList);
//                adapter.notifyDataSetChanged();
//                Log.d(TAG, "Adapter updated with " + serviceRequestList.size() + " items");
//            }
//
//            if (serviceRequestList.isEmpty()) {
//                showNoDataMessage();
//                btnViewAll.setVisibility(View.VISIBLE);
//            } else {
//                hideNoDataMessage();
//                btnViewAll.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    private void showProgressBar() {
//        if (progressBar != null) {
//            progressBar.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void hideProgressBar() {
//        if (progressBar != null) {
//            progressBar.setVisibility(View.GONE);
//        }
//    }
//
//    private void showNoDataMessage() {
//        if (tvNoData != null) {
//            tvNoData.setVisibility(View.VISIBLE);
//        }
//        if (rvRecentServices != null) {
//            rvRecentServices.setVisibility(View.GONE);
//        }
//    }
//
//    private void hideNoDataMessage() {
//        if (tvNoData != null) {
//            tvNoData.setVisibility(View.GONE);
//        }
//        if (rvRecentServices != null) {
//            rvRecentServices.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (adapter != null) {
//            loadServiceRequests();
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (serviceListener != null && mDatabase != null) {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser != null) {
//                String userEmail = currentUser.getEmail().replace(".", "_");
//                mDatabase.child("Users")
//                        .child(userEmail)
//                        .child("ServiceInfo")
//                        .removeEventListener(serviceListener);
//            }
//        }
//    }
//
//    // ================================
//    // SERVICE REQUEST MODEL CLASS
//    // ================================
//    public static class ServiceRequest {
//        private String customerName;
//        private String vehicleNumber;
//        private String mobileNumber;
//        private String vehicleType;
//        private String serviceDate;
//        private String serviceTime;
//        private String serviceNotes;
//        private String status;
//        private String timestamp;
//        private long serverTimestamp;
//        private String serviceCenter;
//        private String userEmail;
//        private List<String> imageUrls;
//
//        public ServiceRequest() {}
//
//        public String getCustomerName() { return customerName; }
//        public void setCustomerName(String customerName) { this.customerName = customerName; }
//
//        public String getVehicleNumber() { return vehicleNumber; }
//        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
//
//        public String getMobileNumber() { return mobileNumber; }
//        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
//
//        public String getVehicleType() { return vehicleType; }
//        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
//
//        public String getServiceDate() { return serviceDate; }
//        public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }
//
//        public String getServiceTime() { return serviceTime; }
//        public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }
//
//        public String getServiceNotes() { return serviceNotes; }
//        public void setServiceNotes(String serviceNotes) { this.serviceNotes = serviceNotes; }
//
//        public String getStatus() { return status; }
//        public void setStatus(String status) { this.status = status; }
//
//        public String getTimestamp() { return timestamp; }
//        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
//
//        public long getServerTimestamp() { return serverTimestamp; }
//        public void setServerTimestamp(long serverTimestamp) { this.serverTimestamp = serverTimestamp; }
//
//        public String getServiceCenter() { return serviceCenter; }
//        public void setServiceCenter(String serviceCenter) { this.serviceCenter = serviceCenter; }
//
//        public String getUserEmail() { return userEmail; }
//        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
//
//        public List<String> getImageUrls() { return imageUrls; }
//        public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
//    }
//
//    // ================================
//    // RECYCLERVIEW ADAPTER CLASS
//    // ================================
//    public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ServiceViewHolder> {
//
//        private Context context;
//        private List<ServiceRequest> serviceRequests;
//
//        public ServiceRequestAdapter(Context context, List<ServiceRequest> serviceRequests) {
//            this.context = context;
//            this.serviceRequests = serviceRequests;
//        }
//
//        @NonNull
//        @Override
//        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(context).inflate(R.layout.item_service_request, parent, false);
//            return new ServiceViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
//            ServiceRequest service = serviceRequests.get(position);
//            holder.bind(service, position);
//        }
//
//        @Override
//        public int getItemCount() {
//            return serviceRequests != null ? serviceRequests.size() : 0;
//        }
//
//        public void updateData(List<ServiceRequest> newServiceRequests) {
//            this.serviceRequests = newServiceRequests;
//            notifyDataSetChanged();
//        }
//
//        class ServiceViewHolder extends RecyclerView.ViewHolder {
//            TextView tvCustomerName, tvVehicleInfo, tvStatus, tvServiceDate,
//                    tvServiceTime, tvMobileNumber, tvServiceNotes, tvTimestamp;
//            ImageView ivCall, ivMore;
//
//            public ServiceViewHolder(@NonNull View itemView) {
//                super(itemView);
//
//                tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
//                tvVehicleInfo = itemView.findViewById(R.id.tv_vehicle_info);
//                tvStatus = itemView.findViewById(R.id.tv_status);
//                tvServiceDate = itemView.findViewById(R.id.tv_service_date);
//                tvServiceTime = itemView.findViewById(R.id.tv_service_time);
//                tvMobileNumber = itemView.findViewById(R.id.tv_mobile_number);
//                tvServiceNotes = itemView.findViewById(R.id.tv_service_notes);
//                tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
//                ivCall = itemView.findViewById(R.id.iv_call);
//                ivMore = itemView.findViewById(R.id.iv_more);
//            }
//
//            public void bind(ServiceRequest service, int position) {
//                tvCustomerName.setText(service.getCustomerName() != null ? service.getCustomerName() : "Unknown");
//
//                String vehicleInfo = (service.getVehicleType() != null ? service.getVehicleType() : "Vehicle") +
//                        " • " + (service.getVehicleNumber() != null ? service.getVehicleNumber() : "N/A");
//                tvVehicleInfo.setText(vehicleInfo);
//
//                String status = service.getStatus() != null ? service.getStatus() : "Pending";
//                tvStatus.setText(status);
//                setStatusBackground(status);
//
//                tvServiceDate.setText(service.getServiceDate() != null ? service.getServiceDate() : "Not Set");
//                tvServiceTime.setText(service.getServiceTime() != null ? service.getServiceTime() : "Not Set");
//                tvMobileNumber.setText(service.getMobileNumber() != null ? service.getMobileNumber() : "N/A");
//
//                if (!TextUtils.isEmpty(service.getServiceNotes())) {
//                    tvServiceNotes.setText(service.getServiceNotes());
//                    tvServiceNotes.setVisibility(View.VISIBLE);
//                } else {
//                    tvServiceNotes.setVisibility(View.GONE);
//                }
//
//                String formattedTimestamp = formatTimestamp(service.getTimestamp());
//                tvTimestamp.setText("Requested on " + formattedTimestamp);
//
//                itemView.setOnClickListener(v -> {
//                    Toast.makeText(context, "Service request by " + service.getCustomerName(),
//                            Toast.LENGTH_SHORT).show();
//                });
//
//                ivCall.setOnClickListener(v -> makePhoneCall(service.getMobileNumber()));
//
//                ivMore.setOnClickListener(v -> {
//                    Toast.makeText(context, "More options for " + service.getCustomerName(),
//                            Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            private void setStatusBackground(String status) {
//                int backgroundRes;
//                switch (status.toLowerCase()) {
//                    case "completed":
//                        backgroundRes = R.drawable.status_completed_bg;
//                        break;
//                    case "cancelled":
//                        backgroundRes = R.drawable.status_cancelled_bg;
//                        break;
//                    case "in progress":
//                        backgroundRes = R.drawable.status_in_progress_bg;
//                        break;
//                    default:
//                        backgroundRes = R.drawable.status_pending_bg;
//                        break;
//                }
//                tvStatus.setBackgroundResource(backgroundRes);
//            }
//
//            private String formatTimestamp(String timestamp) {
//                if (TextUtils.isEmpty(timestamp)) return "Unknown";
//
//                try {
//                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                    Date date = inputFormat.parse(timestamp);
//                    return outputFormat.format(date);
//                } catch (ParseException e) {
//                    return timestamp;
//                }
//            }
//
//            private void makePhoneCall(String phoneNumber) {
//                if (TextUtils.isEmpty(phoneNumber)) return;
//
//                try {
//                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
//                    context.startActivity(callIntent);
//                } catch (Exception e) {
//                    Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//}



package com.icodedtech.awatimotors.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icodedtech.awatimotors.Activity.AddServiceActivity;
import com.icodedtech.awatimotors.Activity.AllServicesActivity;
import com.icodedtech.awatimotors.Activity.ServiceDetailsActivity;
import com.icodedtech.awatimotors.FilteredServicesActivity;
import com.icodedtech.awatimotors.Model.ServiceRequest; // Import the model
import com.icodedtech.awatimotors.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvCarCount, tvBikeCount, tvPendingCount, tvCompletedCount, tvTotalCount;
    private LinearLayout tvNoData;
    private RecyclerView rvRecentServices;
    private FloatingActionButton fabAddService;
    private CardView cardPendingServices, cardCompletedServices, cardTotalServices;
    private CardView cardCarServices, cardBikeServices;
    private ProgressBar progressBar;
    private Button btnViewAll;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ServiceRequestAdapter adapter;
    private List<ServiceRequest> serviceRequestList;
    private List<ServiceRequest> allServicesList;
    private ValueEventListener serviceListener;

    // Statistics counters
    private int totalServices = 0;
    private int pendingServices = 0;
    private int completedServices = 0;
    private int inProgressServices = 0;
    private int cancelledServices = 0;
    private int carCount = 0;
    private int bikeCount = 0;

    private static final String TAG = "HomeFragment";
    private static final int RECENT_LIMIT = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupFirebase();
        setupWelcomeMessage();
        setupRecyclerView();
        setupClickListeners();
        loadServiceRequests();

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvCarCount = view.findViewById(R.id.tv_car_count);
        tvBikeCount = view.findViewById(R.id.tv_bike_count);
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvTotalCount = view.findViewById(R.id.tv_total_count);
        tvNoData = view.findViewById(R.id.tv_no_data);
        rvRecentServices = view.findViewById(R.id.rv_recent_services);
        fabAddService = view.findViewById(R.id.fab_add_service);
        cardPendingServices = view.findViewById(R.id.card_pending_services);
        cardCompletedServices = view.findViewById(R.id.card_completed_services);
        cardTotalServices = view.findViewById(R.id.card_total_services);
        progressBar = view.findViewById(R.id.progress_bar);
        btnViewAll = view.findViewById(R.id.btn_view_all);

        // Find bike and car LinearLayout directly by their IDs
        LinearLayout carLinearLayout = view.findViewById(R.id.carCard);
        LinearLayout bikeLinearLayout = view.findViewById(R.id.bikeCard);

        // Get the parent CardViews of these LinearLayouts
        if (carLinearLayout != null && carLinearLayout.getParent() instanceof CardView) {
            cardCarServices = (CardView) carLinearLayout.getParent();
        }
        if (bikeLinearLayout != null && bikeLinearLayout.getParent() instanceof CardView) {
            cardBikeServices = (CardView) bikeLinearLayout.getParent();
        }

        mAuth = FirebaseAuth.getInstance();
        serviceRequestList = new ArrayList<>();
        allServicesList = new ArrayList<>();
    }

    private void setupFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void setupWelcomeMessage() {
        if (tvWelcome != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String displayName = currentUser.getDisplayName();
                String welcomeMessage = "Welcome, " + (displayName != null ? displayName : "User") + "!";
                tvWelcome.setText(welcomeMessage);
            } else {
                tvWelcome.setText("Welcome to Awati Motors!");
            }
        }
    }

    private void setupRecyclerView() {
        if (rvRecentServices != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            rvRecentServices.setLayoutManager(layoutManager);
            rvRecentServices.setHasFixedSize(false);
            rvRecentServices.setNestedScrollingEnabled(false);

            adapter = new ServiceRequestAdapter(getContext(), serviceRequestList);
            rvRecentServices.setAdapter(adapter);
        }
    }

    private void setupClickListeners() {
        fabAddService.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddServiceActivity.class);
            startActivity(intent);
        });

        btnViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllServicesActivity.class);
            startActivity(intent);
        });

        // Service status filter click listeners
        cardPendingServices.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FilteredServicesActivity.class);
            intent.putExtra("filter_type", "Pending");
            startActivity(intent);
        });

        cardCompletedServices.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FilteredServicesActivity.class);
            intent.putExtra("filter_type", "Completed");
            startActivity(intent);
        });

        cardTotalServices.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllServicesActivity.class);
            startActivity(intent);
        });

        // Vehicle type filter click listeners
        if (cardCarServices != null) {
            cardCarServices.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), FilteredServicesActivity.class);
                intent.putExtra("filter_type", "All");
                intent.putExtra("vehicle_filter", "Car");
                startActivity(intent);
            });
        }

        if (cardBikeServices != null) {
            cardBikeServices.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), FilteredServicesActivity.class);
                intent.putExtra("filter_type", "All");
                intent.putExtra("vehicle_filter", "Bike");
                startActivity(intent);
            });
        }

        btnViewAll.setVisibility(View.VISIBLE);
    }

    private void loadServiceRequests() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated");
            hideProgressBar();
            showNoDataMessage();
            updateAllStatistics();
            return;
        }

        showProgressBar();

        String userEmail = currentUser.getEmail().replace(".", "_");
        DatabaseReference serviceRef = mDatabase.child("Users")
                .child(userEmail)
                .child("ServiceInfo");

        if (serviceListener != null) {
            serviceRef.removeEventListener(serviceListener);
        }

        serviceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allServicesList.clear();
                serviceRequestList.clear();
                resetCounters();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                        try {
                            ServiceRequest serviceRequest = serviceSnapshot.getValue(ServiceRequest.class);
                            if (serviceRequest != null) {
                                // Set the request ID from the Firebase key
                                serviceRequest.setRequestId(serviceSnapshot.getKey());
                                allServicesList.add(serviceRequest);
                                updateCounters(serviceRequest);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing service request: " + e.getMessage());
                        }
                    }

                    Collections.sort(allServicesList, (s1, s2) ->
                            Long.compare(s2.getServerTimestamp(), s1.getServerTimestamp()));

                    // Get latest 5 for home display
                    int limit = Math.min(allServicesList.size(), RECENT_LIMIT);
                    for (int i = 0; i < limit; i++) {
                        serviceRequestList.add(allServicesList.get(i));
                    }

                    Log.d(TAG, "Total services: " + totalServices +
                            ", Pending: " + pendingServices + ", Completed: " + completedServices +
                            ", Cars: " + carCount + ", Bikes: " + bikeCount +
                            ", Showing: " + serviceRequestList.size());
                } else {
                    Log.d(TAG, "No service requests found");
                }

                updateAllStatistics();
                updateUI();
                hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load service requests: " + databaseError.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load data: " + databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                hideProgressBar();
                showNoDataMessage();
                updateAllStatistics();
            }
        };

        serviceRef.addValueEventListener(serviceListener);
    }

    private void resetCounters() {
        totalServices = 0;
        pendingServices = 0;
        completedServices = 0;
        inProgressServices = 0;
        cancelledServices = 0;
        carCount = 0;
        bikeCount = 0;
    }

    private void updateCounters(ServiceRequest serviceRequest) {
        totalServices++;

        // Count by status
        String status = serviceRequest.getStatus();
        if (status != null) {
            switch (status.toLowerCase()) {
                case "pending":
                    pendingServices++;
                    break;
                case "completed":
                    completedServices++;
                    break;
                case "in progress":
                    inProgressServices++;
                    break;
                case "cancelled":
                    cancelledServices++;
                    break;
            }
        } else {
            pendingServices++; // Default to pending if status is null
        }

        // Count by vehicle type - ENHANCED LOGIC
        String vehicleType = serviceRequest.getVehicleType();
        if (vehicleType != null) {
            String lowerVehicleType = vehicleType.toLowerCase();
            if (lowerVehicleType.contains("car") ||
                    lowerVehicleType.contains("sedan") ||
                    lowerVehicleType.contains("suv") ||
                    lowerVehicleType.contains("hatchback") ||
                    lowerVehicleType.contains("truck") ||
                    lowerVehicleType.contains("bus")) {
                carCount++;
            } else if (lowerVehicleType.contains("bike") ||
                    lowerVehicleType.contains("motorcycle") ||
                    lowerVehicleType.contains("scooter") ||
                    lowerVehicleType.contains("auto")) {
                bikeCount++;
            }
        }
    }

    private void updateAllStatistics() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            // Update service statistics
            if (tvPendingCount != null) {
                tvPendingCount.setText(String.valueOf(pendingServices));
            }
            if (tvCompletedCount != null) {
                tvCompletedCount.setText(String.valueOf(completedServices));
            }
            if (tvTotalCount != null) {
                tvTotalCount.setText(String.valueOf(totalServices));
            }

            // Update vehicle statistics
            if (tvCarCount != null) {
                tvCarCount.setText(String.valueOf(carCount));
            }
            if (tvBikeCount != null) {
                tvBikeCount.setText(String.valueOf(bikeCount));
            }
        });
    }

    private void updateUI() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (adapter != null) {
                adapter.updateData(serviceRequestList);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Adapter updated with " + serviceRequestList.size() + " items");
            }

            if (serviceRequestList.isEmpty()) {
                showNoDataMessage();
                btnViewAll.setVisibility(View.VISIBLE);
            } else {
                hideNoDataMessage();
                btnViewAll.setVisibility(View.VISIBLE);
            }
        });
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
        if (rvRecentServices != null) {
            rvRecentServices.setVisibility(View.GONE);
        }
    }

    private void hideNoDataMessage() {
        if (tvNoData != null) {
            tvNoData.setVisibility(View.GONE);
        }
        if (rvRecentServices != null) {
            rvRecentServices.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            loadServiceRequests();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    // ================================
    // RECYCLERVIEW ADAPTER CLASS
    // ================================
    public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ServiceViewHolder> {

        private Context context;
        private List<ServiceRequest> serviceRequests;

        public ServiceRequestAdapter(Context context, List<ServiceRequest> serviceRequests) {
            this.context = context;
            this.serviceRequests = serviceRequests;
        }

        @NonNull
        @Override
        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_service_request, parent, false);
            return new ServiceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
            ServiceRequest service = serviceRequests.get(position);
            holder.bind(service, position);
        }

        @Override
        public int getItemCount() {
            return serviceRequests != null ? serviceRequests.size() : 0;
        }

        public void updateData(List<ServiceRequest> newServiceRequests) {
            this.serviceRequests = newServiceRequests;
            notifyDataSetChanged();
        }

        class ServiceViewHolder extends RecyclerView.ViewHolder {
            TextView tvCustomerName, tvVehicleInfo, tvStatus, tvServiceDate,
                    tvServiceTime, tvMobileNumber, tvServiceNotes, tvTimestamp;
            TextView tvEstimatedCost, tvWorkCount; // Add these for cost display
            ImageView ivCall, ivMore;

            public ServiceViewHolder(@NonNull View itemView) {
                super(itemView);

                tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
                tvVehicleInfo = itemView.findViewById(R.id.tv_vehicle_info);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvServiceDate = itemView.findViewById(R.id.tv_service_date);
                tvServiceTime = itemView.findViewById(R.id.tv_service_time);
                tvMobileNumber = itemView.findViewById(R.id.tv_mobile_number);
                tvServiceNotes = itemView.findViewById(R.id.tv_service_notes);
                tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
                tvEstimatedCost = itemView.findViewById(R.id.tv_estimated_cost);
                tvWorkCount = itemView.findViewById(R.id.tv_work_count);
                ivCall = itemView.findViewById(R.id.iv_call);
                ivMore = itemView.findViewById(R.id.iv_more);
            }

            public void bind(ServiceRequest service, int position) {
                tvCustomerName.setText(service.getCustomerName() != null ? service.getCustomerName() : "Unknown");

                String vehicleInfo = (service.getVehicleType() != null ? service.getVehicleType() : "Vehicle") +
                        " • " + (service.getVehicleNumber() != null ? service.getVehicleNumber() : "N/A");
                tvVehicleInfo.setText(vehicleInfo);

                String status = service.getStatus() != null ? service.getStatus() : "Pending";
                tvStatus.setText(status);
                setStatusBackground(status);

                tvServiceDate.setText(service.getServiceDate() != null ? service.getServiceDate() : "Not Set");
                tvServiceTime.setText(service.getServiceTime() != null ? service.getServiceTime() : "Not Set");
                tvMobileNumber.setText(service.getMobileNumber() != null ? service.getMobileNumber() : "N/A");

                // Display estimated cost
                if (tvEstimatedCost != null) {
                    tvEstimatedCost.setText(service.getFormattedEstimatedCost());
                    tvEstimatedCost.setVisibility(service.getEstimatedCost() > 0 ? View.VISIBLE : View.GONE);
                }

                // Display work count
                if (tvWorkCount != null) {
                    int workCount = service.getWorkItemCount();
                    if (workCount > 0) {
                        tvWorkCount.setText(workCount + " work item" + (workCount != 1 ? "s" : ""));
                        tvWorkCount.setVisibility(View.VISIBLE);
                    } else {
                        tvWorkCount.setVisibility(View.GONE);
                    }
                }

                // Handle service notes or show work summary
                if (!TextUtils.isEmpty(service.getServiceNotes())) {
                    tvServiceNotes.setText(service.getServiceNotes());
                    tvServiceNotes.setVisibility(View.VISIBLE);
                } else if (service.hasWorkItems()) {
                    // Show first work item as preview
                    String workPreview = service.getWorkList().get(0).getWorkName();
                    if (service.getWorkItemCount() > 1) {
                        workPreview += " + " + (service.getWorkItemCount() - 1) + " more";
                    }
                    tvServiceNotes.setText(workPreview);
                    tvServiceNotes.setVisibility(View.VISIBLE);
                } else {
                    tvServiceNotes.setVisibility(View.GONE);
                }

                String formattedTimestamp = formatTimestamp(service.getTimestamp());
                tvTimestamp.setText("Requested on " + formattedTimestamp);

                // Click listener to open service details
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ServiceDetailsActivity.class);

                    // Pass all service data
                    intent.putExtra("requestId", service.getRequestId());
                    intent.putExtra("customerName", service.getCustomerName());
                    intent.putExtra("vehicleNumber", service.getVehicleNumber());
                    intent.putExtra("mobileNumber", service.getMobileNumber());
                    intent.putExtra("vehicleType", service.getVehicleType());
                    intent.putExtra("serviceDate", service.getServiceDate());
                    intent.putExtra("serviceTime", service.getServiceTime());
                    intent.putExtra("serviceNotes", service.getServiceNotes());
                    intent.putExtra("status", service.getStatus());
                    intent.putExtra("timestamp", service.getTimestamp());
                    intent.putExtra("serverTimestamp", service.getServerTimestamp());
                    intent.putExtra("serviceCenter", service.getServiceCenter());
                    intent.putExtra("userEmail", service.getUserEmail());

                    // Pass cost information
                    intent.putExtra("estimatedCost", service.getEstimatedCost());
                    intent.putExtra("actualCost", service.getActualCost());
                    intent.putExtra("priority", service.getPriority());
                    intent.putExtra("paymentStatus", service.getPaymentStatus());
                    intent.putExtra("assignedTechnician", service.getAssignedTechnician());
                    intent.putExtra("completionDate", service.getCompletionDate());
                    intent.putExtra("customerRating", service.getCustomerRating());
                    intent.putExtra("customerFeedback", service.getCustomerFeedback());

                    // Pass work list
                    if (service.getWorkList() != null) {
                        intent.putParcelableArrayListExtra("workList", new ArrayList<>(service.getWorkList()));
                    }

                    // Pass image URLs
                    if (service.getImageUrls() != null) {
                        intent.putStringArrayListExtra("imageUrls", new ArrayList<>(service.getImageUrls()));
                    }

                    context.startActivity(intent);
                });

                ivCall.setOnClickListener(v -> makePhoneCall(service.getMobileNumber()));

                ivMore.setOnClickListener(v -> {
                    Toast.makeText(context, "More options for " + service.getCustomerName(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            private void setStatusBackground(String status) {
                // UPDATED: Use programmatic drawable with proper colors
                android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
                drawable.setCornerRadius(24f); // 12dp converted to pixels

                switch (status.toLowerCase()) {
                    case "completed":
                        drawable.setColor(context.getResources().getColor(R.color.green, null));
                        tvStatus.setTextColor(context.getResources().getColor(R.color.black, null));
                        break;
                    case "cancelled":
                        drawable.setColor(context.getResources().getColor(R.color.red, null));
                        tvStatus.setTextColor(context.getResources().getColor(R.color.white, null));
                        break;
                    case "in progress":
                        drawable.setColor(context.getResources().getColor(R.color.blue, null));
                        tvStatus.setTextColor(context.getResources().getColor(R.color.white, null));
                        break;
                    default: // Pending
                        drawable.setColor(context.getResources().getColor(R.color.orange, null));
                        tvStatus.setTextColor(context.getResources().getColor(R.color.white, null));
                        break;
                }
                tvStatus.setBackground(drawable);
            }

            private String formatTimestamp(String timestamp) {
                if (TextUtils.isEmpty(timestamp)) return "Unknown";

                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date date = inputFormat.parse(timestamp);
                    return outputFormat.format(date);
                } catch (ParseException e) {
                    return timestamp;
                }
            }

            private void makePhoneCall(String phoneNumber) {
                if (TextUtils.isEmpty(phoneNumber)) return;

                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    context.startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

