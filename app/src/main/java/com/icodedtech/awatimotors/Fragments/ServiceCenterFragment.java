package com.icodedtech.awatimotors.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.icodedtech.awatimotors.R;

public class ServiceCenterFragment extends Fragment {

    private RecyclerView rvServiceCenters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_center, container, false);
        
        initViews(view);
        setupRecyclerView();
        
        return view;
    }

    private void initViews(View view) {
        rvServiceCenters = view.findViewById(R.id.rv_service_centers);
    }

    private void setupRecyclerView() {
        rvServiceCenters.setLayoutManager(new LinearLayoutManager(getContext()));
        // Add your service center adapter here
    }
}
