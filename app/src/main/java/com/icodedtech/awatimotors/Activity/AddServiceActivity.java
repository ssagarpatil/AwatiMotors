package com.icodedtech.awatimotors.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.icodedtech.awatimotors.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class AddServiceActivity extends AppCompatActivity {

    private TextInputLayout tilCustomerName, tilVehicleNumber, tilMobileNumber,
            tilServiceDate, tilServiceTime, tilWorkName, tilWorkCost;
    private TextInputEditText etCustomerName, etVehicleNumber, etMobileNumber,
            etServiceDate, etServiceTime, etWorkName, etWorkCost;
    private RadioGroup rgVehicleType;
    private RadioButton rbBike, rbCar;
    private Button btnSubmit, btnCamera, btnGallery, btnAddWork, btnClearAll;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    // Work List Section
    private LinearLayout llWorkList;
    private TextView tvTotalCost;
    private CardView cardWorkInput, cardWorkList;

    // Image Views and Cards
    private ImageView ivImage1, ivImage2, ivImage3, ivImage4;
    private ImageView ivRemove1, ivRemove2, ivRemove3, ivRemove4;
    private CardView cardImage1, cardImage2, cardImage3, cardImage4;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    // Image handling
    private List<Uri> selectedImages;
    private int currentImagePosition = 0;
    private Uri cameraImageUri;

    // Date and Time handling
    private Calendar selectedDate;
    private Calendar selectedTime;

    // Work List
    private List<WorkItem> workList;
    private double totalCost = 0.0;

    // Constants
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int GALLERY_REQUEST_CODE = 1002;
    private static final int CAMERA_REQUEST_CODE = 1003;
    private static final int MAX_IMAGES = 4;

    // Validation Patterns
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern VEHICLE_NUMBER_PATTERN = Pattern.compile("^[A-Z]{2}[ -]?[0-9]{2}[ -]?[A-Z]{1,2}[ -]?[0-9]{4}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,30}$");
    private static final Pattern COST_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");

    // Work Item Class
    public static class WorkItem {
        private String workName;
        private double cost;

        public WorkItem(String workName, double cost) {
            this.workName = workName;
            this.cost = cost;
        }

        public String getWorkName() { return workName; }
        public void setWorkName(String workName) { this.workName = workName; }
        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        initViews();
        setupToolbar();
        setupFirebase();
        setupClickListeners();
        initializeImageList();
        initializeDateAndTime();
        initializeWorkList();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilCustomerName = findViewById(R.id.til_customer_name);
        tilVehicleNumber = findViewById(R.id.til_vehicle_number);
        tilMobileNumber = findViewById(R.id.til_mobile_number);
        tilServiceDate = findViewById(R.id.til_service_date);
        tilServiceTime = findViewById(R.id.til_service_time);
        tilWorkName = findViewById(R.id.til_work_name);
        tilWorkCost = findViewById(R.id.til_work_cost);

        etCustomerName = findViewById(R.id.et_customer_name);
        etVehicleNumber = findViewById(R.id.et_vehicle_number);
        etMobileNumber = findViewById(R.id.et_mobile_number);
        etServiceDate = findViewById(R.id.et_service_date);
        etServiceTime = findViewById(R.id.et_service_time);
        etWorkName = findViewById(R.id.et_work_name);
        etWorkCost = findViewById(R.id.et_work_cost);

        rgVehicleType = findViewById(R.id.rg_vehicle_type);
        rbBike = findViewById(R.id.rb_bike);
        rbCar = findViewById(R.id.rb_car);

        btnSubmit = findViewById(R.id.btn_submit);
        btnCamera = findViewById(R.id.btn_camera);
        btnGallery = findViewById(R.id.btn_gallery);
        btnAddWork = findViewById(R.id.btn_add_work);
        btnClearAll = findViewById(R.id.btn_clear_all);

        progressBar = findViewById(R.id.progress_bar);

        // Work Section Views
        llWorkList = findViewById(R.id.ll_work_list);
        tvTotalCost = findViewById(R.id.tv_total_cost);
        cardWorkInput = findViewById(R.id.card_work_input);
        cardWorkList = findViewById(R.id.card_work_list);

        // Image Views
        ivImage1 = findViewById(R.id.iv_image_1);
        ivImage2 = findViewById(R.id.iv_image_2);
        ivImage3 = findViewById(R.id.iv_image_3);
        ivImage4 = findViewById(R.id.iv_image_4);

        // Remove buttons
        ivRemove1 = findViewById(R.id.iv_remove_1);
        ivRemove2 = findViewById(R.id.iv_remove_2);
        ivRemove3 = findViewById(R.id.iv_remove_3);
        ivRemove4 = findViewById(R.id.iv_remove_4);

        // Cards
        cardImage1 = findViewById(R.id.card_image_1);
        cardImage2 = findViewById(R.id.card_image_2);
        cardImage3 = findViewById(R.id.card_image_3);
        cardImage4 = findViewById(R.id.card_image_4);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Service Request");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    private void initializeWorkList() {
        workList = new ArrayList<>();
        updateTotalCostDisplay();
        updateWorkListVisibility();
    }

    private void initializeImageList() {
        selectedImages = new ArrayList<>();
        for (int i = 0; i < MAX_IMAGES; i++) {
            selectedImages.add(null);
        }
    }

    private void initializeDateAndTime() {
        selectedDate = Calendar.getInstance();
        selectedTime = Calendar.getInstance();
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> validateAndSubmitForm());
        btnCamera.setOnClickListener(v -> showImagePickerDialog());
        btnGallery.setOnClickListener(v -> openMultipleGallery());
        btnAddWork.setOnClickListener(v -> addWorkToList());
        btnClearAll.setOnClickListener(v -> clearAllWork());

        // Date and Time picker clicks
        etServiceDate.setOnClickListener(v -> showDatePicker());
        etServiceTime.setOnClickListener(v -> showTimePicker());

        // Image card clicks
        cardImage1.setOnClickListener(v -> selectImagePosition(0));
        cardImage2.setOnClickListener(v -> selectImagePosition(1));
        cardImage3.setOnClickListener(v -> selectImagePosition(2));
        cardImage4.setOnClickListener(v -> selectImagePosition(3));

        // Remove button clicks
        ivRemove1.setOnClickListener(v -> removeImage(0));
        ivRemove2.setOnClickListener(v -> removeImage(1));
        ivRemove3.setOnClickListener(v -> removeImage(2));
        ivRemove4.setOnClickListener(v -> removeImage(3));
    }

    private void addWorkToList() {
        String workName = etWorkName.getText().toString().trim();
        String workCostStr = etWorkCost.getText().toString().trim();

        // Clear previous errors
        tilWorkName.setError(null);
        tilWorkCost.setError(null);
        tilWorkName.setErrorEnabled(false);
        tilWorkCost.setErrorEnabled(false);

        boolean isValid = true;

        // Validate work name
        if (TextUtils.isEmpty(workName)) {
            tilWorkName.setError("Work name is required");
            tilWorkName.setErrorEnabled(true);
            isValid = false;
        } else if (workName.length() < 3) {
            tilWorkName.setError("Work name must be at least 3 characters");
            tilWorkName.setErrorEnabled(true);
            isValid = false;
        }

        // Validate work cost
        if (TextUtils.isEmpty(workCostStr)) {
            tilWorkCost.setError("Work cost is required");
            tilWorkCost.setErrorEnabled(true);
            isValid = false;
        } else if (!COST_PATTERN.matcher(workCostStr).matches()) {
            tilWorkCost.setError("Invalid cost format (e.g., 100 or 100.50)");
            tilWorkCost.setErrorEnabled(true);
            isValid = false;
        } else {
            try {
                double cost = Double.parseDouble(workCostStr);
                if (cost <= 0) {
                    tilWorkCost.setError("Cost must be greater than 0");
                    tilWorkCost.setErrorEnabled(true);
                    isValid = false;
                } else if (cost > 50000) {
                    tilWorkCost.setError("Cost cannot exceed ₹50,000");
                    tilWorkCost.setErrorEnabled(true);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilWorkCost.setError("Invalid cost format");
                tilWorkCost.setErrorEnabled(true);
                isValid = false;
            }
        }

        if (isValid) {
            double cost = Double.parseDouble(workCostStr);
            WorkItem workItem = new WorkItem(workName, cost);
            workList.add(workItem);

            // Clear input fields
            etWorkName.setText("");
            etWorkCost.setText("");

            // Update displays
            updateWorkListDisplay();
            updateTotalCostDisplay();
            updateWorkListVisibility();

            Toast.makeText(this, "Work added: " + workName, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWorkListDisplay() {
        llWorkList.removeAllViews();

        for (int i = 0; i < workList.size(); i++) {
            WorkItem workItem = workList.get(i);
            View workItemView = createWorkItemView(workItem, i);
            llWorkList.addView(workItemView);
        }
    }

    private View createWorkItemView(WorkItem workItem, int position) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_work_list, null);

        TextView tvWorkName = itemView.findViewById(R.id.tv_work_name);
        TextView tvWorkCost = itemView.findViewById(R.id.tv_work_cost);
        ImageView ivRemoveWork = itemView.findViewById(R.id.iv_remove_work);

        tvWorkName.setText(workItem.getWorkName());
        tvWorkCost.setText("₹" + String.format("%.0f", workItem.getCost()));

        ivRemoveWork.setOnClickListener(v -> {
            workList.remove(position);
            updateWorkListDisplay();
            updateTotalCostDisplay();
            updateWorkListVisibility();
            Toast.makeText(this, "Work removed", Toast.LENGTH_SHORT).show();
        });

        return itemView;
    }

    private void updateTotalCostDisplay() {
        totalCost = 0.0;
        for (WorkItem work : workList) {
            totalCost += work.getCost();
        }
        tvTotalCost.setText("Total: ₹" + String.format("%.0f", totalCost));
    }

    private void updateWorkListVisibility() {
        if (workList.isEmpty()) {
            cardWorkList.setVisibility(View.GONE);
        } else {
            cardWorkList.setVisibility(View.VISIBLE);
        }
    }

    private void clearAllWork() {
        if (!workList.isEmpty()) {
            workList.clear();
            updateWorkListDisplay();
            updateTotalCostDisplay();
            updateWorkListVisibility();
            Toast.makeText(this, "All work cleared", Toast.LENGTH_SHORT).show();
        }
    }

    // Date Picker Implementation
    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Service Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate.setTimeInMillis(selection);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate.getTime());
            etServiceDate.setText(formattedDate);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    // Time Picker Implementation
    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(selectedTime.get(Calendar.HOUR_OF_DAY))
                .setMinute(selectedTime.get(Calendar.MINUTE))
                .setTitleText("Select Service Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            selectedTime.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            selectedTime.set(Calendar.MINUTE, timePicker.getMinute());

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTime = timeFormat.format(selectedTime.getTime());
            etServiceTime.setText(formattedTime);
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void selectImagePosition(int position) {
        currentImagePosition = position;
        if (selectedImages.get(position) == null) {
            showImagePickerDialog();
        }
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(new String[]{"Camera", "Gallery (Multiple)"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openMultipleGallery();
                    }
                })
                .show();
    }

    private void openMultipleGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures (Max 4)"), GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        if (checkCameraPermission()) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    cameraImageUri = FileProvider.getUriForFile(this,
                            getPackageName() + ".fileprovider",
                            photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            }
        } else {
            requestCameraPermission();
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures");
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                handleMultipleImageSelection(data);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                setImageAtPosition(currentImagePosition, cameraImageUri);
            }
        }
    }

    private void handleMultipleImageSelection(Intent data) {
        List<Uri> imagesList = new ArrayList<>();

        if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            int count = Math.min(clipData.getItemCount(), MAX_IMAGES);

            for (int i = 0; i < count; i++) {
                Uri imageUri = clipData.getItemAt(i).getUri();
                imagesList.add(imageUri);
            }

            if (clipData.getItemCount() > MAX_IMAGES) {
                Toast.makeText(this, "Maximum " + MAX_IMAGES + " images allowed. First " + MAX_IMAGES + " selected.", Toast.LENGTH_LONG).show();
            }
        } else if (data.getData() != null) {
            imagesList.add(data.getData());
        }

        setMultipleImages(imagesList);
    }

    private void setMultipleImages(List<Uri> imagesList) {
        clearAllImages();
        for (int i = 0; i < imagesList.size() && i < MAX_IMAGES; i++) {
            setImageAtPosition(i, imagesList.get(i));
        }
        Toast.makeText(this, imagesList.size() + " image(s) selected", Toast.LENGTH_SHORT).show();
    }

    private void clearAllImages() {
        for (int i = 0; i < MAX_IMAGES; i++) {
            removeImage(i);
        }
    }

    private void setImageAtPosition(int position, Uri imageUri) {
        if (imageUri != null) {
            selectedImages.set(position, imageUri);
            updateImageView(position, imageUri);
            showRemoveButton(position, true);
        }
    }

    private void updateImageView(int position, Uri imageUri) {
        ImageView imageView = getImageViewByPosition(position);
        if (imageView != null && imageUri != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    imageView.setImageURI(imageUri);
                } catch (Exception e) {
                    Log.e("ImageLoad", "Error loading image: " + e.getMessage());
                }
            });
        }
    }

    private ImageView getImageViewByPosition(int position) {
        switch (position) {
            case 0: return ivImage1;
            case 1: return ivImage2;
            case 2: return ivImage3;
            case 3: return ivImage4;
            default: return null;
        }
    }

    private void showRemoveButton(int position, boolean show) {
        ImageView removeButton = getRemoveButtonByPosition(position);
        if (removeButton != null) {
            removeButton.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private ImageView getRemoveButtonByPosition(int position) {
        switch (position) {
            case 0: return ivRemove1;
            case 1: return ivRemove2;
            case 2: return ivRemove3;
            case 3: return ivRemove4;
            default: return null;
        }
    }

    private void removeImage(int position) {
        selectedImages.set(position, null);
        ImageView imageView = getImageViewByPosition(position);
        if (imageView != null) {
            imageView.setImageResource(android.R.color.transparent);
            imageView.setBackgroundResource(R.color.light_gray);
        }
        showRemoveButton(position, false);
    }

    private void validateAndSubmitForm() {
        String customerName = etCustomerName.getText().toString().trim();
        String vehicleNumber = etVehicleNumber.getText().toString().trim().toUpperCase();
        String mobileNumber = etMobileNumber.getText().toString().trim();
        String serviceDate = etServiceDate.getText().toString().trim();
        String serviceTime = etServiceTime.getText().toString().trim();

        clearErrors();
        boolean isValid = true;

        if (!validateCustomerName(customerName)) isValid = false;
        if (!validateVehicleNumber(vehicleNumber)) isValid = false;
        if (!validateMobileNumber(mobileNumber)) isValid = false;
        if (!validateServiceDate(serviceDate)) isValid = false;
        if (!validateServiceTime(serviceTime)) isValid = false;

        // Validate work list
        if (workList.isEmpty()) {
            Toast.makeText(this, "Please add at least one work item", Toast.LENGTH_SHORT).show();
            cardWorkInput.requestFocus();
            isValid = false;
        }

        int selectedVehicleTypeId = rgVehicleType.getCheckedRadioButtonId();
        if (selectedVehicleTypeId == -1) {
            Toast.makeText(this, "Please select vehicle type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            RadioButton selectedRadioButton = findViewById(selectedVehicleTypeId);
            String vehicleType = selectedRadioButton.getText().toString();
            submitServiceRequest(customerName, vehicleNumber, mobileNumber, vehicleType,
                    serviceDate, serviceTime);
        }
    }

    private void clearErrors() {
        tilCustomerName.setError(null);
        tilVehicleNumber.setError(null);
        tilMobileNumber.setError(null);
        tilServiceDate.setError(null);
        tilServiceTime.setError(null);

        tilCustomerName.setErrorEnabled(false);
        tilVehicleNumber.setErrorEnabled(false);
        tilMobileNumber.setErrorEnabled(false);
        tilServiceDate.setErrorEnabled(false);
        tilServiceTime.setErrorEnabled(false);
    }

    private boolean validateCustomerName(String name) {
        if (TextUtils.isEmpty(name)) {
            tilCustomerName.setError("Customer name is required");
            tilCustomerName.setErrorEnabled(true);
            return false;
        }

        if (name.length() < 2) {
            tilCustomerName.setError("Name must be at least 2 characters");
            tilCustomerName.setErrorEnabled(true);
            return false;
        }

        if (name.length() > 30) {
            tilCustomerName.setError("Name must be less than 30 characters");
            tilCustomerName.setErrorEnabled(true);
            return false;
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            tilCustomerName.setError("Name should contain only letters and spaces");
            tilCustomerName.setErrorEnabled(true);
            return false;
        }

        return true;
    }

    private boolean validateVehicleNumber(String vehicleNumber) {
        if (TextUtils.isEmpty(vehicleNumber)) {
            tilVehicleNumber.setError("Vehicle number is required");
            tilVehicleNumber.setErrorEnabled(true);
            return false;
        }

        String cleanVehicleNumber = vehicleNumber.replaceAll("[ -]", "");

        if (cleanVehicleNumber.length() < 9 || cleanVehicleNumber.length() > 10) {
            tilVehicleNumber.setError("Vehicle number should be 9-10 characters");
            tilVehicleNumber.setErrorEnabled(true);
            return false;
        }

        if (!VEHICLE_NUMBER_PATTERN.matcher(vehicleNumber).matches()) {
            tilVehicleNumber.setError("Invalid vehicle number format (e.g., MH 12 AB 1234)");
            tilVehicleNumber.setErrorEnabled(true);
            return false;
        }

        return true;
    }

    private boolean validateMobileNumber(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            tilMobileNumber.setError("Mobile number is required");
            tilMobileNumber.setErrorEnabled(true);
            return false;
        }

        if (mobile.length() != 10) {
            tilMobileNumber.setError("Mobile number must be exactly 10 digits");
            tilMobileNumber.setErrorEnabled(true);
            return false;
        }

        if (!mobile.matches("[0-9]+")) {
            tilMobileNumber.setError("Mobile number should contain only digits");
            tilMobileNumber.setErrorEnabled(true);
            return false;
        }

        if (!MOBILE_PATTERN.matcher(mobile).matches()) {
            tilMobileNumber.setError("Invalid Indian mobile number (must start with 6-9)");
            tilMobileNumber.setErrorEnabled(true);
            return false;
        }

        return true;
    }

    private boolean validateServiceDate(String serviceDate) {
        if (TextUtils.isEmpty(serviceDate)) {
            tilServiceDate.setError("Service date is required");
            tilServiceDate.setErrorEnabled(true);
            return false;
        }

        // Check if selected date is not in the past
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (selectedDate.before(today)) {
            tilServiceDate.setError("Service date cannot be in the past");
            tilServiceDate.setErrorEnabled(true);
            return false;
        }

        return true;
    }

    private boolean validateServiceTime(String serviceTime) {
        if (TextUtils.isEmpty(serviceTime)) {
            tilServiceTime.setError("Service time is required");
            tilServiceTime.setErrorEnabled(true);
            return false;
        }

        // Additional time validation can be added here
        int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
        if (hour < 9 || hour > 18) {
            tilServiceTime.setError("Service time must be between 9:00 AM and 6:00 PM");
            tilServiceTime.setErrorEnabled(true);
            return false;
        }

        return true;
    }

    private void submitServiceRequest(String customerName, String vehicleNumber,
                                      String mobileNumber, String vehicleType,
                                      String serviceDate, String serviceTime) {

        showProgressBar(true);
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            resetSubmitButton();
            return;
        }

        String userEmail = currentUser.getEmail().replace(".", "_");
        String serviceKey = mobileNumber + "_" + System.currentTimeMillis();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        long serverTimestamp = System.currentTimeMillis();

        // Prepare work list data
        List<Map<String, Object>> workListData = new ArrayList<>();
        for (WorkItem work : workList) {
            Map<String, Object> workMap = new HashMap<>();
            workMap.put("workName", work.getWorkName());
            workMap.put("cost", work.getCost());
            workListData.add(workMap);
        }

        Map<String, Object> serviceData = new HashMap<>();
        serviceData.put("customerName", customerName);
        serviceData.put("vehicleNumber", vehicleNumber);
        serviceData.put("mobileNumber", mobileNumber);
        serviceData.put("vehicleType", vehicleType);
        serviceData.put("serviceDate", serviceDate);
        serviceData.put("serviceTime", serviceTime);
        serviceData.put("workList", workListData);
        serviceData.put("estimatedCost", totalCost);
        serviceData.put("actualCost", 0.0);
        serviceData.put("timestamp", timestamp);
        serviceData.put("serverTimestamp", serverTimestamp);
        serviceData.put("status", "Pending");
        serviceData.put("priority", "Medium");
        serviceData.put("paymentStatus", "Pending");
        serviceData.put("serviceCenter", "Main Service Center");
        serviceData.put("userEmail", currentUser.getEmail());
        serviceData.put("assignedTechnician", "");
        serviceData.put("completionDate", "");
        serviceData.put("customerRating", "");
        serviceData.put("customerFeedback", "");

        Log.d("AddService", "Saving data with key: " + serviceKey);
        Log.d("AddService", "Data: " + serviceData.toString());

        uploadImagesAndSaveData(serviceData, serviceKey, userEmail);
    }

    private void uploadImagesAndSaveData(Map<String, Object> serviceData, String serviceKey, String userEmail) {
        List<Uri> imagesToUpload = new ArrayList<>();
        for (Uri uri : selectedImages) {
            if (uri != null) {
                imagesToUpload.add(uri);
            }
        }

        if (imagesToUpload.isEmpty()) {
            saveServiceData(serviceData, serviceKey, userEmail);
            return;
        }

        List<String> imageUrls = new ArrayList<>();
        AtomicInteger uploadCount = new AtomicInteger(0);
        int totalImages = imagesToUpload.size();

        for (int i = 0; i < imagesToUpload.size(); i++) {
            Uri imageUri = imagesToUpload.get(i);
            String imageName = serviceKey + "_image_" + i + "_" + System.currentTimeMillis() + ".jpg";
            StorageReference imageRef = mStorage.child("service_images").child(imageName);

            int finalI = i;
            imageRef.putFile(imageUri)
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d("Upload", "Upload " + (finalI + 1) + " progress: " + progress + "%");
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            synchronized (imageUrls) {
                                imageUrls.add(downloadUri.toString());
                                int completed = uploadCount.incrementAndGet();

                                Log.d("Upload", "Completed: " + completed + "/" + totalImages);

                                if (completed == totalImages) {
                                    serviceData.put("imageUrls", imageUrls);
                                    saveServiceData(serviceData, serviceKey, userEmail);
                                }
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("Upload", "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            resetSubmitButton();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Upload", "Failed to upload image: " + e.getMessage());
                        Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        resetSubmitButton();
                    });
        }
    }

    private void saveServiceData(Map<String, Object> serviceData, String serviceKey, String userEmail) {
        Log.d("AddService", "Saving to path: Users/" + userEmail + "/ServiceInfo/" + serviceKey);

        mDatabase.child("Users")
                .child(userEmail)
                .child("ServiceInfo")
                .child(serviceKey)
                .setValue(serviceData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AddService", "Data saved successfully!");
                    showProgressBar(false);
                    Toast.makeText(this, "Service request submitted successfully!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("AddService", "Failed to save data: " + e.getMessage());
                    Toast.makeText(this, "Failed to submit request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    resetSubmitButton();
                });
    }

    private void showProgressBar(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void resetSubmitButton() {
        showProgressBar(false);
        btnSubmit.setEnabled(true);
        btnSubmit.setText("Submit Service Request");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (selectedImages != null) {
            selectedImages.clear();
        }
        if (workList != null) {
            workList.clear();
        }
    }
}
