package com.icodedtech.awatimotors.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.icodedtech.awatimotors.MainActivity;
import com.icodedtech.awatimotors.R;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail;
    private Button btnLogout;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        loadUserData();
        setupLogout();

        return view;
    }

    private void initViews(View view) {
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        mAuth = FirebaseAuth.getInstance();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (tvProfileName != null) {
                tvProfileName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
            }
            if (tvProfileEmail != null) {
                tvProfileEmail.setText(currentUser.getEmail());
            }
        }
    }

    private void setupLogout() {
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).logoutUser();
                }
            });
        }
    }
}
