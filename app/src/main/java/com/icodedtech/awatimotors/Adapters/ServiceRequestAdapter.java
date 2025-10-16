package com.icodedtech.awatimotors.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icodedtech.awatimotors.Model.ServiceRequest;
import com.icodedtech.awatimotors.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ServiceViewHolder> implements Filterable {

    private Context context;
    private List<ServiceRequest> serviceRequests;
    private List<ServiceRequest> serviceRequestsOriginal;

    public ServiceRequestAdapter(Context context, List<ServiceRequest> serviceRequests) {
        this.context = context;
        this.serviceRequests = new ArrayList<>(serviceRequests);
        this.serviceRequestsOriginal = new ArrayList<>(serviceRequests);
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

    public void updateOriginalData(List<ServiceRequest> newServiceRequests) {
        this.serviceRequests = new ArrayList<>(newServiceRequests);
        this.serviceRequestsOriginal = new ArrayList<>(newServiceRequests);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                if (filterPattern.isEmpty()) {
                    serviceRequests = new ArrayList<>(serviceRequestsOriginal);
                } else {
                    List<ServiceRequest> filteredList = new ArrayList<>();
                    for (ServiceRequest service : serviceRequestsOriginal) {
                        if (searchInService(service, filterPattern)) {
                            filteredList.add(service);
                        }
                    }
                    serviceRequests = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = serviceRequests;
                results.count = serviceRequests.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                serviceRequests = (List<ServiceRequest>) results.values;
                notifyDataSetChanged();
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

        return false;
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvVehicleInfo, tvStatus, tvServiceDate,
                tvServiceTime, tvMobileNumber, tvServiceNotes, tvTimestamp;
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
            ivCall = itemView.findViewById(R.id.iv_call);
            ivMore = itemView.findViewById(R.id.iv_more);
        }

        public void bind(ServiceRequest service, int position) {
            // Set customer name with null check
            tvCustomerName.setText(service.getCustomerName() != null ? service.getCustomerName() : "Unknown");

            // Set vehicle info with null checks
            String vehicleType = service.getVehicleType() != null ? service.getVehicleType() : "Vehicle";
            String vehicleNumber = service.getVehicleNumber() != null ? service.getVehicleNumber() : "N/A";
            tvVehicleInfo.setText(vehicleType + " • " + vehicleNumber);

            // Set status with appropriate background
            String status = service.getStatus() != null ? service.getStatus() : "Pending";
            tvStatus.setText(status);
            setStatusBackground(status);

            // Set service date and time with null checks
            tvServiceDate.setText(service.getServiceDate() != null ? service.getServiceDate() : "Not Set");
            tvServiceTime.setText(service.getServiceTime() != null ? service.getServiceTime() : "Not Set");

            // Set mobile number with null check
            tvMobileNumber.setText(service.getMobileNumber() != null ? service.getMobileNumber() : "N/A");

            // Set service notes (show only if available)
            if (!TextUtils.isEmpty(service.getServiceNotes())) {
                tvServiceNotes.setText(service.getServiceNotes());
                tvServiceNotes.setVisibility(View.VISIBLE);
            } else {
                tvServiceNotes.setVisibility(View.GONE);
            }

            // Set formatted timestamp
            String formattedTimestamp = formatTimestamp(service.getTimestamp());
            tvTimestamp.setText("Requested on " + formattedTimestamp);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                Toast.makeText(context, "Service request by " + service.getCustomerName(),
                        Toast.LENGTH_SHORT).show();
            });

            ivCall.setOnClickListener(v -> makePhoneCall(service.getMobileNumber()));

            ivMore.setOnClickListener(v -> {
                Toast.makeText(context, "More options for " + service.getCustomerName(),
                        Toast.LENGTH_SHORT).show();
            });
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
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show();
                return;
            }

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
