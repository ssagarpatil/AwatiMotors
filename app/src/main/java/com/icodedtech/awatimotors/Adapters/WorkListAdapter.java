package com.icodedtech.awatimotors.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icodedtech.awatimotors.Model.ServiceRequest; // CORRECTED IMPORT
import com.icodedtech.awatimotors.R;

import java.util.List;

public class WorkListAdapter extends RecyclerView.Adapter<WorkListAdapter.WorkViewHolder> {

    private Context context;
    private List<ServiceRequest.WorkItem> workList; // CORRECTED TYPE

    public WorkListAdapter(Context context, List<ServiceRequest.WorkItem> workList) {
        this.context = context;
        this.workList = workList;
    }

    @NonNull
    @Override
    public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_work_detail, parent, false);
        return new WorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
        ServiceRequest.WorkItem workItem = workList.get(position);
        holder.bind(workItem, position);
    }

    @Override
    public int getItemCount() {
        return workList != null ? workList.size() : 0;
    }

    class WorkViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkName, tvWorkCost, tvWorkNumber;

        public WorkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkName = itemView.findViewById(R.id.tv_work_name);
            tvWorkCost = itemView.findViewById(R.id.tv_work_cost);

        }

        public void bind(ServiceRequest.WorkItem workItem, int position) {
            if (tvWorkNumber != null) {
                tvWorkNumber.setText(String.valueOf(position + 1));
            }
            tvWorkName.setText(workItem.getWorkName());
            tvWorkCost.setText(workItem.getFormattedCost());
        }
    }
}
