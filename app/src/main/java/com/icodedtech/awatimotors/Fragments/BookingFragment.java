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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.icodedtech.awatimotors.R;

public class BookingFragment extends Fragment {

    private RecyclerView rvBookings;
    private FloatingActionButton fabAddBooking;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupFab();
        
        return view;
    }

    private void initViews(View view) {
        rvBookings = view.findViewById(R.id.rv_bookings);
        fabAddBooking = view.findViewById(R.id.fab_add_booking);
    }

    private void setupRecyclerView() {
        rvBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        // Add your booking adapter here
    }

    private void setupFab() {
        fabAddBooking.setOnClickListener(v -> {
            // Navigate to add booking activity
        });
    }
}
