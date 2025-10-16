//package com.icodedtech.awatimotors.Model;
//
//import java.util.List;
//
//public class ServiceRequest {
//    private String requestId; // MISSING FIELD - ADDED
//    private String customerName;
//    private String vehicleNumber;
//    private String mobileNumber;
//    private String vehicleType;
//    private String serviceDate;
//    private String serviceTime;
//    private String serviceNotes;
//    private String status;
//    private String timestamp;
//    private long serverTimestamp;
//    private String serviceCenter;
//    private String userEmail;
//    private List<String> imageUrls;
//
//    // Default constructor required for Firebase
//    public ServiceRequest() {}
//
//    public ServiceRequest(String customerName, String vehicleNumber, String mobileNumber,
//                          String vehicleType, String serviceDate, String serviceTime,
//                          String serviceNotes, String status, String timestamp) {
//        this.customerName = customerName;
//        this.vehicleNumber = vehicleNumber;
//        this.mobileNumber = mobileNumber;
//        this.vehicleType = vehicleType;
//        this.serviceDate = serviceDate;
//        this.serviceTime = serviceTime;
//        this.serviceNotes = serviceNotes;
//        this.status = status;
//        this.timestamp = timestamp;
//    }
//
//    // Getters and Setters
//    public String getRequestId() { return requestId; } // MISSING GETTER - ADDED
//    public void setRequestId(String requestId) { this.requestId = requestId; } // MISSING SETTER - ADDED
//
//    public String getCustomerName() { return customerName; }
//    public void setCustomerName(String customerName) { this.customerName = customerName; }
//
//    public String getVehicleNumber() { return vehicleNumber; }
//    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
//
//    public String getMobileNumber() { return mobileNumber; }
//    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
//
//    public String getVehicleType() { return vehicleType; }
//    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
//
//    public String getServiceDate() { return serviceDate; }
//    public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }
//
//    public String getServiceTime() { return serviceTime; }
//    public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }
//
//    public String getServiceNotes() { return serviceNotes; }
//    public void setServiceNotes(String serviceNotes) { this.serviceNotes = serviceNotes; }
//
//    public String getStatus() { return status; }
//    public void setStatus(String status) { this.status = status; }
//
//    public String getTimestamp() { return timestamp; }
//    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
//
//    public long getServerTimestamp() { return serverTimestamp; }
//    public void setServerTimestamp(long serverTimestamp) { this.serverTimestamp = serverTimestamp; }
//
//    public String getServiceCenter() { return serviceCenter; }
//    public void setServiceCenter(String serviceCenter) { this.serviceCenter = serviceCenter; }
//
//    public String getUserEmail() { return userEmail; }
//    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
//
//    public List<String> getImageUrls() { return imageUrls; }
//    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
//}



package com.icodedtech.awatimotors.Model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;
import java.util.ArrayList;

public class ServiceRequest {
    private String requestId;
    private String customerName;
    private String vehicleNumber;
    private String mobileNumber;
    private String vehicleType;
    private String serviceDate;
    private String serviceTime;
    private String serviceNotes;
    private String status;
    private String timestamp;
    private long serverTimestamp;
    private String serviceCenter;
    private String userEmail;
    private List<String> imageUrls;

    // NEW FIELDS FOR WORK AND COST MANAGEMENT
    private List<WorkItem> workList;
    private double estimatedCost;
    private double actualCost;
    private String priority;
    private String paymentStatus;
    private String assignedTechnician;
    private String completionDate;
    private String customerRating;
    private String customerFeedback;

    // Default constructor required for Firebase
    public ServiceRequest() {}

    public ServiceRequest(String customerName, String vehicleNumber, String mobileNumber,
                          String vehicleType, String serviceDate, String serviceTime,
                          String serviceNotes, String status, String timestamp) {
        this.customerName = customerName;
        this.vehicleNumber = vehicleNumber;
        this.mobileNumber = mobileNumber;
        this.vehicleType = vehicleType;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.serviceNotes = serviceNotes;
        this.status = status;
        this.timestamp = timestamp;
        this.workList = new ArrayList<>();
        this.estimatedCost = 0.0;
        this.actualCost = 0.0;
        this.priority = "Medium";
        this.paymentStatus = "Pending";
    }

    // EXISTING GETTERS AND SETTERS
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getServiceDate() { return serviceDate; }
    public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }

    public String getServiceTime() { return serviceTime; }
    public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }

    public String getServiceNotes() { return serviceNotes; }
    public void setServiceNotes(String serviceNotes) { this.serviceNotes = serviceNotes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public long getServerTimestamp() { return serverTimestamp; }
    public void setServerTimestamp(long serverTimestamp) { this.serverTimestamp = serverTimestamp; }

    public String getServiceCenter() { return serviceCenter; }
    public void setServiceCenter(String serviceCenter) { this.serviceCenter = serviceCenter; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    // NEW GETTERS AND SETTERS FOR WORK AND COST MANAGEMENT
    public List<WorkItem> getWorkList() { return workList; }
    public void setWorkList(List<WorkItem> workList) { this.workList = workList; }

    public double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }

    public double getActualCost() { return actualCost; }
    public void setActualCost(double actualCost) { this.actualCost = actualCost; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getAssignedTechnician() { return assignedTechnician; }
    public void setAssignedTechnician(String assignedTechnician) { this.assignedTechnician = assignedTechnician; }

    public String getCompletionDate() { return completionDate; }
    public void setCompletionDate(String completionDate) { this.completionDate = completionDate; }

    public String getCustomerRating() { return customerRating; }
    public void setCustomerRating(String customerRating) { this.customerRating = customerRating; }

    public String getCustomerFeedback() { return customerFeedback; }
    public void setCustomerFeedback(String customerFeedback) { this.customerFeedback = customerFeedback; }

    // HELPER METHODS
    public void addWorkItem(String workName, double cost) {
        if (workList == null) {
            workList = new ArrayList<>();
        }
        workList.add(new WorkItem(workName, cost));
        calculateEstimatedCost();
    }

    public void removeWorkItem(int index) {
        if (workList != null && index >= 0 && index < workList.size()) {
            workList.remove(index);
            calculateEstimatedCost();
        }
    }

    public void calculateEstimatedCost() {
        if (workList != null) {
            estimatedCost = 0.0;
            for (WorkItem item : workList) {
                estimatedCost += item.getCost();
            }
        }
    }

    public int getWorkItemCount() {
        return workList != null ? workList.size() : 0;
    }

    public boolean hasWorkItems() {
        return workList != null && !workList.isEmpty();
    }

    // INNER CLASS: WORKITEM
    public static class WorkItem implements Parcelable {
        private String workName;
        private double cost;
        private String category;
        private String description;
        private boolean isCompleted;

        // Default constructor for Firebase
        public WorkItem() {}

        public WorkItem(String workName, double cost) {
            this.workName = workName;
            this.cost = cost;
            this.isCompleted = false;
        }

        public WorkItem(String workName, double cost, String category) {
            this.workName = workName;
            this.cost = cost;
            this.category = category;
            this.isCompleted = false;
        }

        public WorkItem(String workName, double cost, String category, String description) {
            this.workName = workName;
            this.cost = cost;
            this.category = category;
            this.description = description;
            this.isCompleted = false;
        }

        // Parcelable constructor
        protected WorkItem(Parcel in) {
            workName = in.readString();
            cost = in.readDouble();
            category = in.readString();
            description = in.readString();
            isCompleted = in.readByte() != 0;
        }

        public static final Creator<WorkItem> CREATOR = new Creator<WorkItem>() {
            @Override
            public WorkItem createFromParcel(Parcel in) {
                return new WorkItem(in);
            }

            @Override
            public WorkItem[] newArray(int size) {
                return new WorkItem[size];
            }
        };

        // Getters and Setters
        public String getWorkName() { return workName; }
        public void setWorkName(String workName) { this.workName = workName; }

        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { isCompleted = completed; }

        // Utility methods
        public String getFormattedCost() {
            return "₹" + String.format("%.0f", cost);
        }

        public String getDisplayText() {
            return workName + " - " + getFormattedCost();
        }

        // Parcelable implementation
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(workName);
            dest.writeDouble(cost);
            dest.writeString(category);
            dest.writeString(description);
            dest.writeByte((byte) (isCompleted ? 1 : 0));
        }

        @Override
        public String toString() {
            return getDisplayText();
        }
    }

    // UTILITY METHODS FOR SERVICE REQUEST
    public String getFormattedEstimatedCost() {
        return "₹" + String.format("%.0f", estimatedCost);
    }

    public String getFormattedActualCost() {
        return "₹" + String.format("%.0f", actualCost);
    }

    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }

    public boolean isInProgress() {
        return "In Progress".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "Cancelled".equalsIgnoreCase(status);
    }

    public String getStatusColor() {
        if (status == null) return "#FFA500"; // Orange for Pending

        switch (status.toLowerCase()) {
            case "completed":
                return "#4CAF50"; // Green
            case "cancelled":
                return "#F44336"; // Red
            case "in progress":
                return "#2196F3"; // Blue
            default:
                return "#FFA500"; // Orange for Pending
        }
    }

    public String getPriorityColor() {
        if (priority == null) return "#FFA500"; // Orange for Medium

        switch (priority.toLowerCase()) {
            case "high":
                return "#F44336"; // Red
            case "low":
                return "#4CAF50"; // Green
            default:
                return "#FFA500"; // Orange for Medium
        }
    }

    public boolean hasImages() {
        return imageUrls != null && !imageUrls.isEmpty();
    }

    public int getImageCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    public boolean isPaid() {
        return "Paid".equalsIgnoreCase(paymentStatus);
    }

    public boolean isPaymentPending() {
        return "Pending".equalsIgnoreCase(paymentStatus);
    }

    public double getCostDifference() {
        return actualCost - estimatedCost;
    }

    public boolean isOverBudget() {
        return actualCost > estimatedCost;
    }

    public String getCostDifferenceText() {
        double difference = getCostDifference();
        if (difference == 0) {
            return "On Budget";
        } else if (difference > 0) {
            return "₹" + String.format("%.0f", difference) + " Over";
        } else {
            return "₹" + String.format("%.0f", Math.abs(difference)) + " Under";
        }
    }

    // Service completion percentage based on work items
    public double getCompletionPercentage() {
        if (workList == null || workList.isEmpty()) {
            return isCompleted() ? 100.0 : 0.0;
        }

        int completedItems = 0;
        for (WorkItem item : workList) {
            if (item.isCompleted()) {
                completedItems++;
            }
        }

        return (completedItems * 100.0) / workList.size();
    }

    public String getCompletionStatus() {
        double percentage = getCompletionPercentage();
        if (percentage == 100.0) {
            return "Completed";
        } else if (percentage > 0) {
            return String.format("%.0f%% Complete", percentage);
        } else {
            return "Not Started";
        }
    }

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "customerName='" + customerName + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", status='" + status + '\'' +
                ", estimatedCost=" + estimatedCost +
                ", workItemCount=" + getWorkItemCount() +
                '}';
    }
}
