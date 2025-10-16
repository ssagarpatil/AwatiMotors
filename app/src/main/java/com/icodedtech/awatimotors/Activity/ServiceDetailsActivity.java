package com.icodedtech.awatimotors.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.icodedtech.awatimotors.Adapters.WorkListAdapter;
import com.icodedtech.awatimotors.Model.ServiceRequest;
import com.icodedtech.awatimotors.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServiceDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    // Customer Info
    private TextView tvCustomerName, tvMobileNumber, tvVehicleInfo;
    private TextView tvServiceDate, tvServiceTime, tvStatus, tvTimestamp;
    private TextView tvServiceCenter, tvPriority, tvAssignedTechnician;

    // Cost Info
    private TextView tvEstimatedCost, tvActualCost, tvPaymentStatus;
    private CardView cardCostInfo;

    // Work List
    private RecyclerView rvWorkList;
    private TextView tvWorkListTitle, tvNoWork;
    private CardView cardWorkList;
    private WorkListAdapter workListAdapter;

    // Notes
    private TextView tvServiceNotes;
    private CardView cardNotes;

    // Images
    private RecyclerView rvImages;
    private TextView tvImagesTitle, tvNoImages;
    private CardView cardImages;

    // Action Buttons
    private MaterialButton btnCall, btnEdit, btnUpdateStatus;

    // Firebase
    private DatabaseReference mDatabase;
    private String serviceKey;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        initViews();
        setupToolbar();
        setupFirebase();
        loadServiceDetails();
        debugServiceKey();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);

        // Customer Info
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvVehicleInfo = findViewById(R.id.tv_vehicle_info);
        tvServiceDate = findViewById(R.id.tv_service_date);
        tvServiceTime = findViewById(R.id.tv_service_time);
        tvStatus = findViewById(R.id.tv_status);
        tvTimestamp = findViewById(R.id.tv_timestamp);


        // Cost Info
        tvEstimatedCost = findViewById(R.id.tv_estimated_cost);
        tvActualCost = findViewById(R.id.tv_actual_cost);
        cardCostInfo = findViewById(R.id.card_cost_info);

        // Work List
        rvWorkList = findViewById(R.id.rv_work_list);
        tvWorkListTitle = findViewById(R.id.tv_work_list_title);
        tvNoWork = findViewById(R.id.tv_no_work);
        cardWorkList = findViewById(R.id.card_work_list);

        // Notes
        tvServiceNotes = findViewById(R.id.tv_service_notes);
        cardNotes = findViewById(R.id.card_notes);

        // Images
        rvImages = findViewById(R.id.rv_images);
        tvImagesTitle = findViewById(R.id.tv_images_title);
        tvNoImages = findViewById(R.id.tv_no_images);
        cardImages = findViewById(R.id.card_images);

        // Action Buttons
        btnCall = findViewById(R.id.btn_call);
        btnEdit = findViewById(R.id.btn_edit);
        btnUpdateStatus = findViewById(R.id.btn_update_status);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Service Details");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void debugServiceKey() {
        String mobileNumber = getIntent().getStringExtra("mobileNumber");
        long serverTimestamp = getIntent().getLongExtra("serverTimestamp", 0);
        String generatedKey = mobileNumber + "_" + serverTimestamp;

        android.util.Log.d("ServiceDetails", "Debug Info:");
        android.util.Log.d("ServiceDetails", "Mobile: " + mobileNumber);
        android.util.Log.d("ServiceDetails", "Server Timestamp: " + serverTimestamp);
        android.util.Log.d("ServiceDetails", "Generated Key: " + generatedKey);
        android.util.Log.d("ServiceDetails", "User Email: " + userEmail);
        android.util.Log.d("ServiceDetails", "Clean Email: " + (userEmail != null ? userEmail.replace(".", "_") : "null"));
    }

    private void loadServiceDetails() {
        Intent intent = getIntent();

        // Load customer information
        String customerName = intent.getStringExtra("customerName");
        String mobileNumber = intent.getStringExtra("mobileNumber");
        String vehicleType = intent.getStringExtra("vehicleType");
        String vehicleNumber = intent.getStringExtra("vehicleNumber");
        String serviceDate = intent.getStringExtra("serviceDate");
        String serviceTime = intent.getStringExtra("serviceTime");
        String status = intent.getStringExtra("status");
        String timestamp = intent.getStringExtra("timestamp");
        String serviceCenter = intent.getStringExtra("serviceCenter");
        String serviceNotes = intent.getStringExtra("serviceNotes");
        String priority = intent.getStringExtra("priority");
        String assignedTechnician = intent.getStringExtra("assignedTechnician");
        String paymentStatus = intent.getStringExtra("paymentStatus");

        // Store user email and create service key for Firebase operations
        userEmail = intent.getStringExtra("userEmail");
        long serverTimestamp = intent.getLongExtra("serverTimestamp", 0);

        if (userEmail == null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                userEmail = currentUser.getEmail();
            }
        }

        // Create service key (same format as when saving)
        if (mobileNumber != null && serverTimestamp != 0) {
            serviceKey = mobileNumber + "_" + serverTimestamp;
        }

        // Load cost information
        double estimatedCost = intent.getDoubleExtra("estimatedCost", 0.0);
        double actualCost = intent.getDoubleExtra("actualCost", 0.0);

        // Load work list with proper casting
        ArrayList<Parcelable> parcelableWorkList = intent.getParcelableArrayListExtra("workList");
        List<ServiceRequest.WorkItem> workList = new ArrayList<>();

        if (parcelableWorkList != null) {
            for (Parcelable parcelable : parcelableWorkList) {
                if (parcelable instanceof ServiceRequest.WorkItem) {
                    workList.add((ServiceRequest.WorkItem) parcelable);
                }
            }
        }

        // Load image URLs
        ArrayList<String> imageUrls = intent.getStringArrayListExtra("imageUrls");

        // Set customer information
        tvCustomerName.setText(customerName != null ? customerName : "Unknown");
        tvMobileNumber.setText(mobileNumber != null ? mobileNumber : "N/A");

        String vehicleInfo = (vehicleType != null ? vehicleType : "Vehicle") + " • " +
                (vehicleNumber != null ? vehicleNumber : "N/A");
        tvVehicleInfo.setText(vehicleInfo);

        tvServiceDate.setText(serviceDate != null ? serviceDate : "Not Set");
        tvServiceTime.setText(serviceTime != null ? serviceTime : "Not Set");

        // Set status with background
        tvStatus.setText(status != null ? status : "Pending");
        setStatusBackground(status != null ? status : "Pending");

        tvTimestamp.setText("Requested on " + formatTimestamp(timestamp));

        // Set additional info
        if (tvServiceCenter != null) {
            tvServiceCenter.setText(serviceCenter != null ? serviceCenter : "Main Service Center");
        }
        if (tvPriority != null) {
            tvPriority.setText(priority != null ? priority : "Medium");
            setPriorityColor(priority != null ? priority : "Medium");
        }
        if (tvAssignedTechnician != null) {
            String technician = assignedTechnician;
            if (technician == null || technician.trim().isEmpty()) {
                technician = "Not Assigned";
            }
            tvAssignedTechnician.setText(technician);
        }

        // Set cost information
        tvEstimatedCost.setText("₹" + String.format("%.0f", estimatedCost));
        if (actualCost > 0) {
            tvActualCost.setText("₹" + String.format("%.0f", actualCost));
            tvActualCost.setVisibility(View.VISIBLE);
        } else {
            tvActualCost.setVisibility(View.GONE);
        }

        // Set payment status
        if (tvPaymentStatus != null) {
            tvPaymentStatus.setText(paymentStatus != null ? paymentStatus : "Pending");
            setPaymentStatusColor(paymentStatus != null ? paymentStatus : "Pending");
        }

        // Load work list
        setupWorkList(workList);

        // Load service notes
        setupServiceNotes(serviceNotes);

        // Load images
        setupImages(imageUrls);

        // Setup action buttons
        setupActionButtons(mobileNumber, customerName);
    }

    private void setupWorkList(List<ServiceRequest.WorkItem> workList) {
        if (workList != null && !workList.isEmpty()) {
            rvWorkList.setLayoutManager(new LinearLayoutManager(this));
            workListAdapter = new WorkListAdapter(this, workList);
            rvWorkList.setAdapter(workListAdapter);

            // Calculate total cost from work list
            double totalCost = 0.0;
            for (ServiceRequest.WorkItem item : workList) {
                totalCost += item.getCost();
            }

            tvWorkListTitle.setText("Work Details (" + workList.size() + " items) - Total: ₹" + String.format("%.0f", totalCost));
            rvWorkList.setVisibility(View.VISIBLE);
            tvNoWork.setVisibility(View.GONE);
        } else {
            rvWorkList.setVisibility(View.GONE);
            tvNoWork.setVisibility(View.VISIBLE);
        }
        cardWorkList.setVisibility(View.VISIBLE);
    }

    private void setupServiceNotes(String serviceNotes) {
        if (serviceNotes != null && !serviceNotes.trim().isEmpty()) {
            tvServiceNotes.setText(serviceNotes);
            cardNotes.setVisibility(View.VISIBLE);
        } else {
            cardNotes.setVisibility(View.GONE);
        }
    }

    private void setupImages(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Setup horizontal RecyclerView for images
            LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false);
            rvImages.setLayoutManager(layoutManager);

            // Create simple image adapter
            ServiceImageAdapter imageAdapter = new ServiceImageAdapter(this, imageUrls);
            rvImages.setAdapter(imageAdapter);

            tvImagesTitle.setText("Images (" + imageUrls.size() + ")");
            rvImages.setVisibility(View.VISIBLE);
            tvNoImages.setVisibility(View.GONE);
        } else {
            rvImages.setVisibility(View.GONE);
            tvNoImages.setVisibility(View.VISIBLE);
        }
        cardImages.setVisibility(View.VISIBLE);
    }

    private void setupActionButtons(String mobileNumber, String customerName) {
        btnCall.setOnClickListener(v -> {
            if (mobileNumber != null && !mobileNumber.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + mobileNumber));
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Edit functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        btnUpdateStatus.setOnClickListener(v -> {
            showStatusUpdateDialog();
        });
    }

    private void showStatusUpdateDialog() {
        String[] statuses = {"Pending", "In Progress", "Completed", "Cancelled"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Update Service Status")
                .setItems(statuses, (dialog, which) -> {
                    String newStatus = statuses[which];
                    updateServiceStatus(newStatus);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateServiceStatus(String newStatus) {
        if (serviceKey == null || userEmail == null) {
            Toast.makeText(this, "Error: Cannot update status", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Updating status...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Update status in Firebase
        String cleanEmail = userEmail.replace(".", "_");

        DatabaseReference serviceRef = mDatabase.child("Users")
                .child(cleanEmail)
                .child("ServiceInfo")
                .child(serviceKey);

        // Create update map
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);

        // Additional updates based on status
        if ("Completed".equalsIgnoreCase(newStatus)) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            updates.put("completionDate", currentDate);

            // If actual cost is not set, use estimated cost
            double actualCost = getIntent().getDoubleExtra("actualCost", 0.0);
            double estimatedCost = getIntent().getDoubleExtra("estimatedCost", 0.0);
            if (actualCost == 0.0 && estimatedCost > 0.0) {
                updates.put("actualCost", estimatedCost);
            }

            // Update payment status to pending if completed
            String currentPaymentStatus = getIntent().getStringExtra("paymentStatus");
            if (currentPaymentStatus == null || "".equals(currentPaymentStatus)) {
                updates.put("paymentStatus", "Pending");
            }
        } else if ("Cancelled".equalsIgnoreCase(newStatus)) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            updates.put("completionDate", currentDate);
        }

        // Log the path for debugging
        android.util.Log.d("StatusUpdate", "Updating path: Users/" + cleanEmail + "/ServiceInfo/" + serviceKey);
        android.util.Log.d("StatusUpdate", "Updates: " + updates.toString());

        // Perform the update
        serviceRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();

                    android.util.Log.d("StatusUpdate", "Status updated successfully to: " + newStatus);

                    // Update local UI immediately
                    tvStatus.setText(newStatus);
                    setStatusBackground(newStatus);

                    // Update actual cost display if completed
                    if ("Completed".equalsIgnoreCase(newStatus)) {
                        double actualCost = getIntent().getDoubleExtra("actualCost", 0.0);
                        double estimatedCost = getIntent().getDoubleExtra("estimatedCost", 0.0);
                        if (actualCost == 0.0 && estimatedCost > 0.0) {
                            tvActualCost.setText("₹" + String.format("%.0f", estimatedCost));
                            tvActualCost.setVisibility(View.VISIBLE);
                        }
                    }

                    // Show success message
                    Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();

                    // Set result to notify calling activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated", true);
                    resultIntent.putExtra("newStatus", newStatus);
                    resultIntent.putExtra("serviceKey", serviceKey);
                    setResult(RESULT_OK, resultIntent);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    android.util.Log.e("StatusUpdate", "Failed to update status", e);
                    Toast.makeText(this, "Failed to update status: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setStatusBackground(String status) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setCornerRadius(24f); // 12dp converted to pixels

        switch (status.toLowerCase()) {
            case "completed":
                drawable.setColor(getResources().getColor(R.color.green, getTheme()));
                tvStatus.setTextColor(getResources().getColor(R.color.black, getTheme()));
                break;
            case "cancelled":
                drawable.setColor(getResources().getColor(R.color.red, getTheme()));
                tvStatus.setTextColor(getResources().getColor(R.color.white, getTheme()));
                break;
            case "in progress":
                drawable.setColor(getResources().getColor(R.color.blue, getTheme()));
                tvStatus.setTextColor(getResources().getColor(R.color.white, getTheme()));
                break;
            default: // Pending
                drawable.setColor(getResources().getColor(R.color.orange, getTheme()));
                tvStatus.setTextColor(getResources().getColor(R.color.white, getTheme()));
                break;
        }
        tvStatus.setBackground(drawable);
    }

    private void setPriorityColor(String priority) {
        if (tvPriority != null) {
            int colorRes;
            switch (priority.toLowerCase()) {
                case "high":
                    colorRes = R.color.red;
                    break;
                case "low":
                    colorRes = R.color.green;
                    break;
                default: // Medium
                    colorRes = R.color.orange;
                    break;
            }
            tvPriority.setTextColor(getResources().getColor(colorRes, getTheme()));
        }
    }

    private void setPaymentStatusColor(String paymentStatus) {
        if (tvPaymentStatus != null) {
            int colorRes;
            switch (paymentStatus.toLowerCase()) {
                case "paid":
                    colorRes = R.color.green;
                    break;
                case "partial":
                    colorRes = R.color.orange;
                    break;
                default: // Pending
                    colorRes = R.color.red;
                    break;
            }
            tvPaymentStatus.setTextColor(getResources().getColor(colorRes, getTheme()));
        }
    }

    private String formatTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) return "Unknown";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return timestamp;
        }
    }

    // Simple image adapter
    private class ServiceImageAdapter extends RecyclerView.Adapter<ServiceImageAdapter.ImageViewHolder> {
        private List<String> imageUrls;
        private AppCompatActivity context;

        public ServiceImageAdapter(AppCompatActivity context, List<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            return new ImageViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            String imageUrl = imageUrls.get(position);

            // Use Glide to load image
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_person_24dp)
                    .error(R.drawable.ic_cancelled)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                Toast.makeText(context, "Image " + (position + 1), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return imageUrls != null ? imageUrls.size() : 0;
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ImageViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }
}
