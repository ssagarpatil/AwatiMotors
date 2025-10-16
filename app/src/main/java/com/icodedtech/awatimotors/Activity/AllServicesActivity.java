////package com.icodedtech.awatimotors.Activity;
////
////import android.content.Context;
////import android.content.Intent;
////import android.net.Uri;
////import android.os.Bundle;
////import android.text.TextUtils;
////import android.util.Log;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.Filter;
////import android.widget.Filterable;
////import android.widget.ImageView;
////import android.widget.LinearLayout;
////import android.widget.ProgressBar;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.appcompat.widget.SearchView;
////import androidx.appcompat.widget.Toolbar;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.auth.FirebaseUser;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////import com.icodedtech.awatimotors.R;
////
////import java.text.ParseException;
////import java.text.SimpleDateFormat;
////import java.util.ArrayList;
////import java.util.Collections;
////import java.util.Date;
////import java.util.List;
////import java.util.Locale;
////
////public class AllServicesActivity extends AppCompatActivity {
////
////    private Toolbar toolbar;
////    private SearchView searchView;
////    private RecyclerView recyclerView;
////    private ProgressBar progressBar;
////    private LinearLayout tvNoData; // Changed from TextView to LinearLayout
////    private TextView tvResultCount;
////
////    private FirebaseAuth mAuth;
////    private DatabaseReference mDatabase;
////    private AllServicesAdapter adapter;
////    private List<ServiceRequest> allServicesList;
////    private ValueEventListener serviceListener;
////
////    private static final String TAG = "AllServicesActivity";
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_all_services);
////
////        initViews();
////        setupToolbar();
////        setupFirebase();
////        setupRecyclerView();
////        setupSearchView();
////        loadAllServices();
////    }
////
////    private void initViews() {
////        toolbar = findViewById(R.id.toolbar);
////        searchView = findViewById(R.id.search_view);
////        recyclerView = findViewById(R.id.recycler_view);
////        progressBar = findViewById(R.id.progress_bar);
////        tvNoData = findViewById(R.id.tv_no_data); // Now correctly cast as LinearLayout
////        tvResultCount = findViewById(R.id.tv_result_count);
////
////        mAuth = FirebaseAuth.getInstance();
////        allServicesList = new ArrayList<>();
////    }
////
////
////
////    private void setupToolbar() {
////        setSupportActionBar(toolbar);
////        if (getSupportActionBar() != null) {
////            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
////            getSupportActionBar().setTitle("All Service Requests");
////        }
////        toolbar.setNavigationOnClickListener(v -> onBackPressed());
////    }
////
////    private void setupFirebase() {
////        mDatabase = FirebaseDatabase.getInstance().getReference();
////    }
////
////    private void setupRecyclerView() {
////        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
////        recyclerView.setLayoutManager(layoutManager);
////
////        adapter = new AllServicesAdapter(this, allServicesList);
////        recyclerView.setAdapter(adapter);
////    }
////
////    private void setupSearchView() {
////        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////            @Override
////            public boolean onQueryTextSubmit(String query) {
////                adapter.getFilter().filter(query);
////                return false;
////            }
////
////            @Override
////            public boolean onQueryTextChange(String newText) {
////                adapter.getFilter().filter(newText);
////                return false;
////            }
////        });
////
////        searchView.setQueryHint("Search by name, mobile, or vehicle number");
////    }
////
////    private void loadAllServices() {
////        FirebaseUser currentUser = mAuth.getCurrentUser();
////        if (currentUser == null) {
////            Log.e(TAG, "User not authenticated");
////            hideProgressBar();
////            showNoDataMessage();
////            return;
////        }
////
////        showProgressBar();
////
////        String userEmail = currentUser.getEmail().replace(".", "_");
////        DatabaseReference serviceRef = mDatabase.child("Users")
////                .child(userEmail)
////                .child("ServiceInfo");
////
////        serviceListener = new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                allServicesList.clear();
////
////                if (dataSnapshot.exists()) {
////                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
////                        try {
////                            ServiceRequest serviceRequest = serviceSnapshot.getValue(ServiceRequest.class);
////                            if (serviceRequest != null) {
////                                allServicesList.add(serviceRequest);
////                            }
////                        } catch (Exception e) {
////                            Log.e(TAG, "Error parsing service request: " + e.getMessage());
////                        }
////                    }
////
////                    // Sort by server timestamp (newest first)
////                    Collections.sort(allServicesList, (s1, s2) ->
////                            Long.compare(s2.getServerTimestamp(), s1.getServerTimestamp()));
////
////                    Log.d(TAG, "Total services loaded: " + allServicesList.size());
////                } else {
////                    Log.d(TAG, "No service requests found");
////                }
////
////                updateUI();
////                hideProgressBar();
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                Log.e(TAG, "Failed to load service requests: " + databaseError.getMessage());
////                Toast.makeText(AllServicesActivity.this, "Failed to load data: " + databaseError.getMessage(),
////                        Toast.LENGTH_SHORT).show();
////                hideProgressBar();
////                showNoDataMessage();
////            }
////        };
////
////        serviceRef.addValueEventListener(serviceListener);
////    }
////
////    private void updateUI() {
////        runOnUiThread(() -> {
////            if (adapter != null) {
////                adapter.updateOriginalData(allServicesList);
////                updateResultCount(allServicesList.size());
////            }
////
////            if (allServicesList.isEmpty()) {
////                showNoDataMessage();
////            } else {
////                hideNoDataMessage();
////            }
////        });
////    }
////
////    private void updateResultCount(int count) {
////        if (tvResultCount != null) {
////            tvResultCount.setText(count + " service request" + (count != 1 ? "s" : "") + " found");
////            tvResultCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
////        }
////    }
////
////    private void showProgressBar() {
////        if (progressBar != null) {
////            progressBar.setVisibility(View.VISIBLE);
////        }
////    }
////
////    private void hideProgressBar() {
////        if (progressBar != null) {
////            progressBar.setVisibility(View.GONE);
////        }
////    }
////
////    private void showNoDataMessage() {
////        if (tvNoData != null) {
////            tvNoData.setVisibility(View.VISIBLE);
////        }
////        if (recyclerView != null) {
////            recyclerView.setVisibility(View.GONE);
////        }
////    }
////
////    private void hideNoDataMessage() {
////        if (tvNoData != null) {
////            tvNoData.setVisibility(View.GONE);
////        }
////        if (recyclerView != null) {
////            recyclerView.setVisibility(View.VISIBLE);
////        }
////    }
////
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        if (serviceListener != null && mDatabase != null) {
////            FirebaseUser currentUser = mAuth.getCurrentUser();
////            if (currentUser != null) {
////                String userEmail = currentUser.getEmail().replace(".", "_");
////                mDatabase.child("Users")
////                        .child(userEmail)
////                        .child("ServiceInfo")
////                        .removeEventListener(serviceListener);
////            }
////        }
////    }
////
////    // ================================
////    // SERVICE REQUEST MODEL CLASS
////    // ================================
////    public static class ServiceRequest {
////        private String customerName;
////        private String vehicleNumber;
////        private String mobileNumber;
////        private String vehicleType;
////        private String serviceDate;
////        private String serviceTime;
////        private String serviceNotes;
////        private String status;
////        private long serverTimestamp;
////        private String timestamp;
////        private String serviceCenter;
////        private String userEmail;
////        private List<String> imageUrls;
////
////        public ServiceRequest() {}
////
////        // Getters and Setters
////        public String getCustomerName() { return customerName; }
////        public void setCustomerName(String customerName) { this.customerName = customerName; }
////
////        public String getVehicleNumber() { return vehicleNumber; }
////        public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
////
////        public String getMobileNumber() { return mobileNumber; }
////        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
////
////        public String getVehicleType() { return vehicleType; }
////        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
////
////        public String getServiceDate() { return serviceDate; }
////        public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }
////
////        public String getServiceTime() { return serviceTime; }
////        public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }
////
////        public String getServiceNotes() { return serviceNotes; }
////        public void setServiceNotes(String serviceNotes) { this.serviceNotes = serviceNotes; }
////
////        public String getStatus() { return status; }
////        public void setStatus(String status) { this.status = status; }
////
////        public String getTimestamp() { return timestamp; }
////        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
////
////        public long getServerTimestamp() { return serverTimestamp; }
////        public void setServerTimestamp(long serverTimestamp) { this.serverTimestamp = serverTimestamp; }
////
////        public String getServiceCenter() { return serviceCenter; }
////        public void setServiceCenter(String serviceCenter) { this.serviceCenter = serviceCenter; }
////
////        public String getUserEmail() { return userEmail; }
////        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
////
////        public List<String> getImageUrls() { return imageUrls; }
////        public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
////    }
////
////    // ================================
////    // FILTERABLE ADAPTER CLASS
////    // ================================
////    public class AllServicesAdapter extends RecyclerView.Adapter<AllServicesAdapter.ServiceViewHolder> implements Filterable {
////
////        private Context context;
////        private List<ServiceRequest> serviceRequestsOriginal;
////        private List<ServiceRequest> serviceRequestsFiltered;
////
////        public AllServicesAdapter(Context context, List<ServiceRequest> serviceRequests) {
////            this.context = context;
////            this.serviceRequestsOriginal = new ArrayList<>();
////            this.serviceRequestsFiltered = new ArrayList<>();
////        }
////
////        @NonNull
////        @Override
////        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////            View view = LayoutInflater.from(context).inflate(R.layout.item_service_request, parent, false);
////            return new ServiceViewHolder(view);
////        }
////
////        @Override
////        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
////            ServiceRequest service = serviceRequestsFiltered.get(position);
////            holder.bind(service, position);
////        }
////
////        @Override
////        public int getItemCount() {
////            return serviceRequestsFiltered.size();
////        }
////
////        public void updateOriginalData(List<ServiceRequest> newServiceRequests) {
////            this.serviceRequestsOriginal = new ArrayList<>(newServiceRequests);
////            this.serviceRequestsFiltered = new ArrayList<>(newServiceRequests);
////            notifyDataSetChanged();
////            updateResultCount(serviceRequestsFiltered.size());
////        }
////
////        @Override
////        public Filter getFilter() {
////            return new Filter() {
////                @Override
////                protected FilterResults performFiltering(CharSequence constraint) {
////                    String filterPattern = constraint.toString().toLowerCase().trim();
////
////                    if (filterPattern.isEmpty()) {
////                        serviceRequestsFiltered = new ArrayList<>(serviceRequestsOriginal);
////                    } else {
////                        List<ServiceRequest> filteredList = new ArrayList<>();
////                        for (ServiceRequest service : serviceRequestsOriginal) {
////                            if (searchInService(service, filterPattern)) {
////                                filteredList.add(service);
////                            }
////                        }
////                        serviceRequestsFiltered = filteredList;
////                    }
////
////                    FilterResults results = new FilterResults();
////                    results.values = serviceRequestsFiltered;
////                    results.count = serviceRequestsFiltered.size();
////                    return results;
////                }
////
////                @Override
////                protected void publishResults(CharSequence constraint, FilterResults results) {
////                    serviceRequestsFiltered = (List<ServiceRequest>) results.values;
////                    notifyDataSetChanged();
////                    updateResultCount(serviceRequestsFiltered.size());
////
////                    if (serviceRequestsFiltered.isEmpty()) {
////                        showNoDataMessage();
////                    } else {
////                        hideNoDataMessage();
////                    }
////                }
////            };
////        }
////
////        private boolean searchInService(ServiceRequest service, String query) {
////            if (service.getCustomerName() != null &&
////                    service.getCustomerName().toLowerCase().contains(query)) {
////                return true;
////            }
////
////            if (service.getMobileNumber() != null &&
////                    service.getMobileNumber().contains(query)) {
////                return true;
////            }
////
////            if (service.getVehicleNumber() != null &&
////                    service.getVehicleNumber().toLowerCase().contains(query)) {
////                return true;
////            }
////
////            if (service.getVehicleType() != null &&
////                    service.getVehicleType().toLowerCase().contains(query)) {
////                return true;
////            }
////
////            if (service.getStatus() != null &&
////                    service.getStatus().toLowerCase().contains(query)) {
////                return true;
////            }
////
////            return false;
////        }
////
////        class ServiceViewHolder extends RecyclerView.ViewHolder {
////            TextView tvCustomerName, tvVehicleInfo, tvStatus, tvServiceDate,
////                    tvServiceTime, tvMobileNumber, tvServiceNotes, tvTimestamp;
////            ImageView ivCall, ivMore;
////
////            public ServiceViewHolder(@NonNull View itemView) {
////                super(itemView);
////
////                tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
////                tvVehicleInfo = itemView.findViewById(R.id.tv_vehicle_info);
////                tvStatus = itemView.findViewById(R.id.tv_status);
////                tvServiceDate = itemView.findViewById(R.id.tv_service_date);
////                tvServiceTime = itemView.findViewById(R.id.tv_service_time);
////                tvMobileNumber = itemView.findViewById(R.id.tv_mobile_number);
////                tvServiceNotes = itemView.findViewById(R.id.tv_service_notes);
////                tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
////                ivCall = itemView.findViewById(R.id.iv_call);
////                ivMore = itemView.findViewById(R.id.iv_more);
////            }
////
////            public void bind(ServiceRequest service, int position) {
////                tvCustomerName.setText(service.getCustomerName() != null ? service.getCustomerName() : "Unknown");
////
////                String vehicleInfo = (service.getVehicleType() != null ? service.getVehicleType() : "Vehicle") +
////                        " • " + (service.getVehicleNumber() != null ? service.getVehicleNumber() : "N/A");
////                tvVehicleInfo.setText(vehicleInfo);
////
////                String status = service.getStatus() != null ? service.getStatus() : "Pending";
////                tvStatus.setText(status);
////                setStatusBackground(status);
////
////                tvServiceDate.setText(service.getServiceDate() != null ? service.getServiceDate() : "Not Set");
////                tvServiceTime.setText(service.getServiceTime() != null ? service.getServiceTime() : "Not Set");
////                tvMobileNumber.setText(service.getMobileNumber() != null ? service.getMobileNumber() : "N/A");
////
////                if (!TextUtils.isEmpty(service.getServiceNotes())) {
////                    tvServiceNotes.setText(service.getServiceNotes());
////                    tvServiceNotes.setVisibility(View.VISIBLE);
////                } else {
////                    tvServiceNotes.setVisibility(View.GONE);
////                }
////
////                String formattedTimestamp = formatTimestamp(service.getTimestamp());
////                tvTimestamp.setText("Requested on " + formattedTimestamp);
////
////                itemView.setOnClickListener(v -> {
////                    Toast.makeText(context, "Service request by " + service.getCustomerName(),
////                            Toast.LENGTH_SHORT).show();
////                });
////
////                ivCall.setOnClickListener(v -> makePhoneCall(service.getMobileNumber()));
////
////                ivMore.setOnClickListener(v -> {
////                    Toast.makeText(context, "More options for " + service.getCustomerName(),
////                            Toast.LENGTH_SHORT).show();
////                });
////            }
////
////            private void setStatusBackground(String status) {
////                int backgroundRes;
////                switch (status.toLowerCase()) {
////                    case "completed":
////                        backgroundRes = R.drawable.status_completed_bg;
////                        break;
////                    case "cancelled":
////                        backgroundRes = R.drawable.status_cancelled_bg;
////                        break;
////                    case "in progress":
////                        backgroundRes = R.drawable.status_in_progress_bg;
////                        break;
////                    default:
////                        backgroundRes = R.drawable.status_pending_bg;
////                        break;
////                }
////                tvStatus.setBackgroundResource(backgroundRes);
////            }
////
////            private String formatTimestamp(String timestamp) {
////                if (TextUtils.isEmpty(timestamp)) return "Unknown";
////
////                try {
////                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
////                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
////                    Date date = inputFormat.parse(timestamp);
////                    return outputFormat.format(date);
////                } catch (ParseException e) {
////                    return timestamp;
////                }
////            }
////
////            private void makePhoneCall(String phoneNumber) {
////                if (TextUtils.isEmpty(phoneNumber)) return;
////
////                try {
////                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
////                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
////                    context.startActivity(callIntent);
////                } catch (Exception e) {
////                    Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
////                }
////            }
////        }
////    }
////}
//
//
//
//
//
//package com.icodedtech.awatimotors.Activity;
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
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.icodedtech.awatimotors.Model.ServiceRequest;
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
//public class AllServicesActivity extends AppCompatActivity {
//
//    private Toolbar toolbar;
//    private SearchView searchView;
//    private RecyclerView recyclerView;
//    private ProgressBar progressBar;
//    private LinearLayout tvNoData;
//    private TextView tvResultCount;
//
//    private FirebaseAuth mAuth;
//    private DatabaseReference mDatabase;
//    private AllServicesAdapter adapter;
//    private List<ServiceRequest> allServicesList;
//    private ValueEventListener serviceListener;
//
//    private static final String TAG = "AllServicesActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_all_services);
//
//        initViews();
//        setupToolbar();
//        setupFirebase();
//        setupRecyclerView();
//        setupSearchView();
//        loadAllServices();
//    }
//
//    private void initViews() {
//        toolbar = findViewById(R.id.toolbar);
//        searchView = findViewById(R.id.search_view);
//        recyclerView = findViewById(R.id.recycler_view);
//        progressBar = findViewById(R.id.progress_bar);
//        tvNoData = findViewById(R.id.tv_no_data);
//        tvResultCount = findViewById(R.id.tv_result_count);
//
//        mAuth = FirebaseAuth.getInstance();
//        allServicesList = new ArrayList<>();
//    }
//
//    private void setupToolbar() {
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("All Service Requests");
//        }
//        toolbar.setNavigationOnClickListener(v -> onBackPressed());
//    }
//
//    private void setupFirebase() {
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//    }
//
//    private void setupRecyclerView() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        adapter = new AllServicesAdapter(this, allServicesList);
//        recyclerView.setAdapter(adapter);
//    }
//
//    private void setupSearchView() {
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//
//        searchView.setQueryHint("Search by name, mobile, or vehicle number");
//    }
//
//    private void loadAllServices() {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            Log.e(TAG, "User not authenticated");
//            hideProgressBar();
//            showNoDataMessage();
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
//        serviceListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                allServicesList.clear();
//
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
//                        try {
//                            ServiceRequest serviceRequest = serviceSnapshot.getValue(ServiceRequest.class);
//                            if (serviceRequest != null) {
//                                // Set the request ID from the Firebase key
//                                serviceRequest.setRequestId(serviceSnapshot.getKey());
//                                allServicesList.add(serviceRequest);
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, "Error parsing service request: " + e.getMessage());
//                        }
//                    }
//
//                    // Sort by server timestamp (newest first)
//                    Collections.sort(allServicesList, (s1, s2) ->
//                            Long.compare(s2.getServerTimestamp(), s1.getServerTimestamp()));
//
//                    Log.d(TAG, "Total services loaded: " + allServicesList.size());
//                } else {
//                    Log.d(TAG, "No service requests found");
//                }
//
//                updateUI();
//                hideProgressBar();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to load service requests: " + databaseError.getMessage());
//                Toast.makeText(AllServicesActivity.this, "Failed to load data: " + databaseError.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//                hideProgressBar();
//                showNoDataMessage();
//            }
//        };
//
//        serviceRef.addValueEventListener(serviceListener);
//    }
//
//    private void updateUI() {
//        runOnUiThread(() -> {
//            if (adapter != null) {
//                adapter.updateOriginalData(allServicesList);
//                updateResultCount(allServicesList.size());
//            }
//
//            if (allServicesList.isEmpty()) {
//                showNoDataMessage();
//            } else {
//                hideNoDataMessage();
//            }
//        });
//    }
//
//    private void updateResultCount(int count) {
//        if (tvResultCount != null) {
//            tvResultCount.setText(count + " service request" + (count != 1 ? "s" : "") + " found");
//            tvResultCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
//        }
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
//        if (recyclerView != null) {
//            recyclerView.setVisibility(View.GONE);
//        }
//    }
//
//    private void hideNoDataMessage() {
//        if (tvNoData != null) {
//            tvNoData.setVisibility(View.GONE);
//        }
//        if (recyclerView != null) {
//            recyclerView.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
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
//    // FILTERABLE ADAPTER CLASS
//    // ================================
//    public class AllServicesAdapter extends RecyclerView.Adapter<AllServicesAdapter.ServiceViewHolder> implements Filterable {
//
//        private Context context;
//        private List<ServiceRequest> serviceRequestsOriginal;
//        private List<ServiceRequest> serviceRequestsFiltered;
//
//        public AllServicesAdapter(Context context, List<ServiceRequest> serviceRequests) {
//            this.context = context;
//            this.serviceRequestsOriginal = new ArrayList<>();
//            this.serviceRequestsFiltered = new ArrayList<>();
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
//            ServiceRequest service = serviceRequestsFiltered.get(position);
//            holder.bind(service, position);
//        }
//
//        @Override
//        public int getItemCount() {
//            return serviceRequestsFiltered.size();
//        }
//
//        public void updateOriginalData(List<ServiceRequest> newServiceRequests) {
//            this.serviceRequestsOriginal = new ArrayList<>(newServiceRequests);
//            this.serviceRequestsFiltered = new ArrayList<>(newServiceRequests);
//            notifyDataSetChanged();
//            updateResultCount(serviceRequestsFiltered.size());
//        }
//
//        @Override
//        public Filter getFilter() {
//            return new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence constraint) {
//                    String filterPattern = constraint.toString().toLowerCase().trim();
//
//                    if (filterPattern.isEmpty()) {
//                        serviceRequestsFiltered = new ArrayList<>(serviceRequestsOriginal);
//                    } else {
//                        List<ServiceRequest> filteredList = new ArrayList<>();
//                        for (ServiceRequest service : serviceRequestsOriginal) {
//                            if (searchInService(service, filterPattern)) {
//                                filteredList.add(service);
//                            }
//                        }
//                        serviceRequestsFiltered = filteredList;
//                    }
//
//                    FilterResults results = new FilterResults();
//                    results.values = serviceRequestsFiltered;
//                    results.count = serviceRequestsFiltered.size();
//                    return results;
//                }
//
//                @Override
//                protected void publishResults(CharSequence constraint, FilterResults results) {
//                    serviceRequestsFiltered = (List<ServiceRequest>) results.values;
//                    notifyDataSetChanged();
//                    updateResultCount(serviceRequestsFiltered.size());
//
//                    if (serviceRequestsFiltered.isEmpty()) {
//                        showNoDataMessage();
//                    } else {
//                        hideNoDataMessage();
//                    }
//                }
//            };
//        }
//
//        private boolean searchInService(ServiceRequest service, String query) {
//            if (service.getCustomerName() != null &&
//                    service.getCustomerName().toLowerCase().contains(query)) {
//                return true;
//            }
//
//            if (service.getMobileNumber() != null &&
//                    service.getMobileNumber().contains(query)) {
//                return true;
//            }
//
//            if (service.getVehicleNumber() != null &&
//                    service.getVehicleNumber().toLowerCase().contains(query)) {
//                return true;
//            }
//
//            if (service.getVehicleType() != null &&
//                    service.getVehicleType().toLowerCase().contains(query)) {
//                return true;
//            }
//
//            if (service.getStatus() != null &&
//                    service.getStatus().toLowerCase().contains(query)) {
//                return true;
//            }
//
//            // NEW: Search in work items
//            if (service.getWorkList() != null) {
//                for (ServiceRequest.WorkItem workItem : service.getWorkList()) {
//                    if (workItem.getWorkName() != null &&
//                            workItem.getWorkName().toLowerCase().contains(query)) {
//                        return true;
//                    }
//                }
//            }
//
//            return false;
//        }
//
//        class ServiceViewHolder extends RecyclerView.ViewHolder {
//            TextView tvCustomerName, tvVehicleInfo, tvStatus, tvServiceDate,
//                    tvServiceTime, tvMobileNumber, tvServiceNotes, tvTimestamp;
//            TextView tvEstimatedCost, tvWorkCount; // NEW VIEWS
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
//                tvEstimatedCost = itemView.findViewById(R.id.tv_estimated_cost);
//                tvWorkCount = itemView.findViewById(R.id.tv_work_count);
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
//                // NEW: Display estimated cost
//                if (tvEstimatedCost != null) {
//                    tvEstimatedCost.setText(service.getFormattedEstimatedCost());
//                    tvEstimatedCost.setVisibility(service.getEstimatedCost() > 0 ? View.VISIBLE : View.GONE);
//                }
//
//                // NEW: Display work count
//                if (tvWorkCount != null) {
//                    int workCount = service.getWorkItemCount();
//                    if (workCount > 0) {
//                        tvWorkCount.setText(workCount + " work item" + (workCount != 1 ? "s" : ""));
//                        tvWorkCount.setVisibility(View.VISIBLE);
//                    } else {
//                        tvWorkCount.setVisibility(View.GONE);
//                    }
//                }
//
//                // Handle service notes or show work summary
//                if (!TextUtils.isEmpty(service.getServiceNotes())) {
//                    tvServiceNotes.setText(service.getServiceNotes());
//                    tvServiceNotes.setVisibility(View.VISIBLE);
//                } else if (service.hasWorkItems()) {
//                    // Show first work item as preview
//                    String workPreview = service.getWorkList().get(0).getWorkName();
//                    if (service.getWorkItemCount() > 1) {
//                        workPreview += " + " + (service.getWorkItemCount() - 1) + " more";
//                    }
//                    tvServiceNotes.setText(workPreview);
//                    tvServiceNotes.setVisibility(View.VISIBLE);
//                } else {
//                    tvServiceNotes.setVisibility(View.GONE);
//                }
//
//                String formattedTimestamp = formatTimestamp(service.getTimestamp());
//                tvTimestamp.setText("Requested on " + formattedTimestamp);
//
//                // UPDATED: Click listener to open service details
//                itemView.setOnClickListener(v -> {
//                    Intent intent = new Intent(context, ServiceDetailsActivity.class);
//
//                    // Pass all service data
//                    intent.putExtra("requestId", service.getRequestId());
//                    intent.putExtra("customerName", service.getCustomerName());
//                    intent.putExtra("vehicleNumber", service.getVehicleNumber());
//                    intent.putExtra("mobileNumber", service.getMobileNumber());
//                    intent.putExtra("vehicleType", service.getVehicleType());
//                    intent.putExtra("serviceDate", service.getServiceDate());
//                    intent.putExtra("serviceTime", service.getServiceTime());
//                    intent.putExtra("serviceNotes", service.getServiceNotes());
//                    intent.putExtra("status", service.getStatus());
//                    intent.putExtra("timestamp", service.getTimestamp());
//                    intent.putExtra("serverTimestamp", service.getServerTimestamp());
//                    intent.putExtra("serviceCenter", service.getServiceCenter());
//                    intent.putExtra("userEmail", service.getUserEmail());
//
//                    // Pass cost information
//                    intent.putExtra("estimatedCost", service.getEstimatedCost());
//                    intent.putExtra("actualCost", service.getActualCost());
//                    intent.putExtra("priority", service.getPriority());
//                    intent.putExtra("paymentStatus", service.getPaymentStatus());
//                    intent.putExtra("assignedTechnician", service.getAssignedTechnician());
//                    intent.putExtra("completionDate", service.getCompletionDate());
//                    intent.putExtra("customerRating", service.getCustomerRating());
//                    intent.putExtra("customerFeedback", service.getCustomerFeedback());
//
//                    // Pass work list
//                    if (service.getWorkList() != null) {
//                        intent.putParcelableArrayListExtra("workList", new ArrayList<>(service.getWorkList()));
//                    }
//
//                    // Pass image URLs
//                    if (service.getImageUrls() != null) {
//                        intent.putStringArrayListExtra("imageUrls", new ArrayList<>(service.getImageUrls()));
//                    }
//
//                    context.startActivity(intent);
//                });
//
//                ivCall.setOnClickListener(v -> makePhoneCall(service.getMobileNumber()));
//
//                ivMore.setOnClickListener(v -> {
//                    // TODO: Show popup menu with options like Edit, Delete, Update Status, etc.
//                    showMoreOptions(service);
//                });
//            }
//
//            private void showMoreOptions(ServiceRequest service) {
//                // Create popup menu or dialog with more options
//                android.widget.PopupMenu popup = new android.widget.PopupMenu(context, ivMore);
//                popup.getMenuInflater().inflate(R.menu.service_item_menu, popup.getMenu());
//
//                popup.setOnMenuItemClickListener(item -> {
//                    int itemId = item.getItemId();
//                    if (itemId == R.id.menu_edit) {
//                        // TODO: Open edit activity
//                        Toast.makeText(context, "Edit " + service.getCustomerName(), Toast.LENGTH_SHORT).show();
//                        return true;
//                    } else if (itemId == R.id.menu_update_status) {
//                        // TODO: Show status update dialog
//                        showStatusUpdateDialog(service);
//                        return true;
//                    } else if (itemId == R.id.menu_delete) {
//                        // TODO: Show delete confirmation
//                        showDeleteConfirmation(service);
//                        return true;
//                    } else if (itemId == R.id.menu_share) {
//                        // TODO: Share service details
//                        shareServiceDetails(service);
//                        return true;
//                    }
//                    return false;
//                });
//
//                popup.show();
//            }
//
//            private void showStatusUpdateDialog(ServiceRequest service) {
//                String[] statuses = {"Pending", "In Progress", "Completed", "Cancelled"};
//
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//                builder.setTitle("Update Status for " + service.getCustomerName())
//                        .setItems(statuses, (dialog, which) -> {
//                            String newStatus = statuses[which];
//                            // TODO: Update status in Firebase
//                            Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
//                        })
//                        .show();
//            }
//
//            private void showDeleteConfirmation(ServiceRequest service) {
//                new android.app.AlertDialog.Builder(context)
//                        .setTitle("Delete Service Request")
//                        .setMessage("Are you sure you want to delete the service request for " + service.getCustomerName() + "?")
//                        .setPositiveButton("Delete", (dialog, which) -> {
//                            // TODO: Delete from Firebase
//                            Toast.makeText(context, "Service request deleted", Toast.LENGTH_SHORT).show();
//                        })
//                        .setNegativeButton("Cancel", null)
//                        .show();
//            }
//
//            private void shareServiceDetails(ServiceRequest service) {
//                StringBuilder details = new StringBuilder();
//                details.append("Service Request Details\n\n");
//                details.append("Customer: ").append(service.getCustomerName()).append("\n");
//                details.append("Vehicle: ").append(service.getVehicleType()).append(" - ").append(service.getVehicleNumber()).append("\n");
//                details.append("Mobile: ").append(service.getMobileNumber()).append("\n");
//                details.append("Date: ").append(service.getServiceDate()).append("\n");
//                details.append("Time: ").append(service.getServiceTime()).append("\n");
//                details.append("Status: ").append(service.getStatus()).append("\n");
//                details.append("Estimated Cost: ").append(service.getFormattedEstimatedCost()).append("\n");
//
//                if (service.hasWorkItems()) {
//                    details.append("\nWork Items:\n");
//                    for (ServiceRequest.WorkItem item : service.getWorkList()) {
//                        details.append("• ").append(item.getDisplayText()).append("\n");
//                    }
//                }
//
//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, details.toString());
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Service Request - " + service.getCustomerName());
//                context.startActivity(Intent.createChooser(shareIntent, "Share Service Details"));
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

package com.icodedtech.awatimotors.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.icodedtech.awatimotors.Model.ServiceRequest;
import com.icodedtech.awatimotors.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllServicesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout tvNoData;
    private TextView tvResultCount;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private AllServicesAdapter adapter;
    private List<ServiceRequest> allServicesList;
    private ValueEventListener serviceListener;

    private static final String TAG = "AllServicesActivity";
    private static final int REQUEST_CODE_SERVICE_DETAILS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);

        initViews();
        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        setupSearchView();
        loadAllServices();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        tvResultCount = findViewById(R.id.tv_result_count);

        mAuth = FirebaseAuth.getInstance();
        allServicesList = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Service Requests");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AllServicesAdapter(this, allServicesList);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setQueryHint("Search by name, mobile, or vehicle number");
    }

    private void loadAllServices() {
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

                if (dataSnapshot.exists()) {
                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                        try {
                            ServiceRequest serviceRequest = serviceSnapshot.getValue(ServiceRequest.class);
                            if (serviceRequest != null) {
                                // Set the request ID from the Firebase key
                                serviceRequest.setRequestId(serviceSnapshot.getKey());
                                allServicesList.add(serviceRequest);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing service request: " + e.getMessage());
                        }
                    }

                    // Sort by server timestamp (newest first)
                    Collections.sort(allServicesList, (s1, s2) ->
                            Long.compare(s2.getServerTimestamp(), s1.getServerTimestamp()));

                    Log.d(TAG, "Total services loaded: " + allServicesList.size());
                } else {
                    Log.d(TAG, "No service requests found");
                }

                updateUI();
                hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load service requests: " + databaseError.getMessage());
                Toast.makeText(AllServicesActivity.this, "Failed to load data: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                hideProgressBar();
                showNoDataMessage();
            }
        };

        serviceRef.addValueEventListener(serviceListener);
    }

    private void updateUI() {
        runOnUiThread(() -> {
            if (adapter != null) {
                adapter.updateOriginalData(allServicesList);
                updateResultCount(allServicesList.size());
            }

            if (allServicesList.isEmpty()) {
                showNoDataMessage();
            } else {
                hideNoDataMessage();
            }
        });
    }

    private void updateResultCount(int count) {
        if (tvResultCount != null) {
            tvResultCount.setText(count + " service request" + (count != 1 ? "s" : "") + " found");
            tvResultCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SERVICE_DETAILS && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("updated", false)) {
                String newStatus = data.getStringExtra("newStatus");
                String serviceKey = data.getStringExtra("serviceKey");

                Toast.makeText(this, "Service status updated successfully", Toast.LENGTH_SHORT).show();
                // The Firebase listener will automatically update the UI
            }
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

    // ================================
    // FILTERABLE ADAPTER CLASS
    // ================================
    public class AllServicesAdapter extends RecyclerView.Adapter<AllServicesAdapter.ServiceViewHolder> implements Filterable {

        private Context context;
        private List<ServiceRequest> serviceRequestsOriginal;
        private List<ServiceRequest> serviceRequestsFiltered;

        public AllServicesAdapter(Context context, List<ServiceRequest> serviceRequests) {
            this.context = context;
            this.serviceRequestsOriginal = new ArrayList<>();
            this.serviceRequestsFiltered = new ArrayList<>();
        }

        @NonNull
        @Override
        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_service_request, parent, false);
            return new ServiceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
            ServiceRequest service = serviceRequestsFiltered.get(position);
            holder.bind(service, position);
        }

        @Override
        public int getItemCount() {
            return serviceRequestsFiltered.size();
        }

        public void updateOriginalData(List<ServiceRequest> newServiceRequests) {
            this.serviceRequestsOriginal = new ArrayList<>(newServiceRequests);
            this.serviceRequestsFiltered = new ArrayList<>(newServiceRequests);
            notifyDataSetChanged();
            updateResultCount(serviceRequestsFiltered.size());
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    if (filterPattern.isEmpty()) {
                        serviceRequestsFiltered = new ArrayList<>(serviceRequestsOriginal);
                    } else {
                        List<ServiceRequest> filteredList = new ArrayList<>();
                        for (ServiceRequest service : serviceRequestsOriginal) {
                            if (searchInService(service, filterPattern)) {
                                filteredList.add(service);
                            }
                        }
                        serviceRequestsFiltered = filteredList;
                    }

                    FilterResults results = new FilterResults();
                    results.values = serviceRequestsFiltered;
                    results.count = serviceRequestsFiltered.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    serviceRequestsFiltered = (List<ServiceRequest>) results.values;
                    notifyDataSetChanged();
                    updateResultCount(serviceRequestsFiltered.size());

                    if (serviceRequestsFiltered.isEmpty()) {
                        showNoDataMessage();
                    } else {
                        hideNoDataMessage();
                    }
                }
            };
        }

        private boolean searchInService(ServiceRequest service, String query) {
            if (service.getCustomerName() != null &&
                    service.getCustomerName().toLowerCase().contains(query)) {
                return true;
            }

            if (service.getMobileNumber() != null &&
                    service.getMobileNumber().contains(query)) {
                return true;
            }

            if (service.getVehicleNumber() != null &&
                    service.getVehicleNumber().toLowerCase().contains(query)) {
                return true;
            }

            if (service.getVehicleType() != null &&
                    service.getVehicleType().toLowerCase().contains(query)) {
                return true;
            }

            if (service.getStatus() != null &&
                    service.getStatus().toLowerCase().contains(query)) {
                return true;
            }

            // Search in work items
            if (service.getWorkList() != null) {
                for (ServiceRequest.WorkItem workItem : service.getWorkList()) {
                    if (workItem.getWorkName() != null &&
                            workItem.getWorkName().toLowerCase().contains(query)) {
                        return true;
                    }
                }
            }

            return false;
        }

        class ServiceViewHolder extends RecyclerView.ViewHolder {
            TextView tvCustomerName, tvVehicleInfo, tvStatus, tvServiceDate,
                    tvServiceTime, tvMobileNumber, tvServiceNotes, tvTimestamp;
            TextView tvEstimatedCost, tvWorkCount;
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

                    // Use startActivityForResult to handle status updates
                    if (context instanceof AllServicesActivity) {
                        ((AllServicesActivity) context).startActivityForResult(intent, REQUEST_CODE_SERVICE_DETAILS);
                    } else {
                        context.startActivity(intent);
                    }
                });

                ivCall.setOnClickListener(v -> makePhoneCall(service.getMobileNumber()));

                ivMore.setOnClickListener(v -> {
                    showMoreOptions(service);
                });
            }

            private void showMoreOptions(ServiceRequest service) {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(context, ivMore);
                popup.getMenuInflater().inflate(R.menu.service_item_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_edit) {
                        Toast.makeText(context, "Edit " + service.getCustomerName(), Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (itemId == R.id.menu_update_status) {
                        showStatusUpdateDialog(service);
                        return true;
                    } else if (itemId == R.id.menu_delete) {
                        showDeleteConfirmation(service);
                        return true;
                    } else if (itemId == R.id.menu_share) {
                        shareServiceDetails(service);
                        return true;
                    }
                    return false;
                });

                popup.show();
            }

            private void showStatusUpdateDialog(ServiceRequest service) {
                String[] statuses = {"Pending", "In Progress", "Completed", "Cancelled"};

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle("Update Status for " + service.getCustomerName())
                        .setItems(statuses, (dialog, which) -> {
                            String newStatus = statuses[which];
                            Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                        })
                        .show();
            }

            private void showDeleteConfirmation(ServiceRequest service) {
                new android.app.AlertDialog.Builder(context)
                        .setTitle("Delete Service Request")
                        .setMessage("Are you sure you want to delete the service request for " + service.getCustomerName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Toast.makeText(context, "Service request deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            private void shareServiceDetails(ServiceRequest service) {
                StringBuilder details = new StringBuilder();
                details.append("Service Request Details\n\n");
                details.append("Customer: ").append(service.getCustomerName()).append("\n");
                details.append("Vehicle: ").append(service.getVehicleType()).append(" - ").append(service.getVehicleNumber()).append("\n");
                details.append("Mobile: ").append(service.getMobileNumber()).append("\n");
                details.append("Date: ").append(service.getServiceDate()).append("\n");
                details.append("Time: ").append(service.getServiceTime()).append("\n");
                details.append("Status: ").append(service.getStatus()).append("\n");
                details.append("Estimated Cost: ").append(service.getFormattedEstimatedCost()).append("\n");

                if (service.hasWorkItems()) {
                    details.append("\nWork Items:\n");
                    for (ServiceRequest.WorkItem item : service.getWorkList()) {
                        details.append("• ").append(item.getDisplayText()).append("\n");
                    }
                }

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, details.toString());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Service Request - " + service.getCustomerName());
                context.startActivity(Intent.createChooser(shareIntent, "Share Service Details"));
            }

            private void setStatusBackground(String status) {
                int backgroundRes;
                switch (status.toLowerCase()) {
                    case "completed":
                        backgroundRes = R.drawable.status_completed_bg;
                        break;
                    case "cancelled":
                        backgroundRes = R.drawable.status_cancelled_bg;
                        break;
                    case "in progress":
                        backgroundRes = R.drawable.status_in_progress_bg;
                        break;
                    default:
                        backgroundRes = R.drawable.status_pending_bg;
                        break;
                }
                tvStatus.setBackgroundResource(backgroundRes);
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
