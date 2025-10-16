package com.icodedtech.awatimotors;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.icodedtech.awatimotors.Fragments.AboutFragment;
import com.icodedtech.awatimotors.Fragments.BookingFragment;
import com.icodedtech.awatimotors.Fragments.HelpFragment;
import com.icodedtech.awatimotors.Fragments.ServiceCenterFragment;
import com.icodedtech.awatimotors.Fragments.SettingsFragment;
import com.icodedtech.awatimotors.Register.LoginActivity;
import com.icodedtech.awatimotors.Fragments.HomeFragment;
import com.icodedtech.awatimotors.Fragments.ProfileFragment;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth mAuth;
    private TextView navUserName, navUserEmail;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        initViews();

        // Setup toolbar
        setupToolbar();

        // Setup navigation drawer
        setupNavigationDrawer();

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup navigation header
        setupNavigationHeader();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            navigationView.setCheckedItem(R.id.nav_home);
            setTitle("Home");
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return handleBottomNavigation(item);
            }
        });
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        navUserName = headerView.findViewById(R.id.nav_user_name);
        navUserEmail = headerView.findViewById(R.id.nav_user_email);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            navUserName.setText(displayName != null ? displayName : "User");
            navUserEmail.setText(email != null ? email : "user@example.com");
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add smooth transition animation
            fragmentTransaction.setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

            activeFragment = fragment;
        }
    }

    // Handle Navigation Drawer Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "";

        int itemId = item.getItemId();

        // Navigation Drawer Items
        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
            title = "Home";
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (itemId == R.id.nav_service_centers) {
            selectedFragment = new ServiceCenterFragment();
            title = "Service Centers";
            bottomNavigationView.setSelectedItemId(R.id.nav_service_centers);
        } else if (itemId == R.id.nav_bookings) {
            selectedFragment = new BookingFragment();
            title = "My Bookings";
            bottomNavigationView.setSelectedItemId(R.id.nav_bookings);
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
            title = "Profile";
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else if (itemId == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
            title = "Settings";
            clearBottomNavigationSelection();
        } else if (itemId == R.id.nav_about) {
            selectedFragment = new AboutFragment();
            title = "About";
            clearBottomNavigationSelection();
        } else if (itemId == R.id.nav_help) {
            selectedFragment = new HelpFragment();
            title = "Help & Support";
            clearBottomNavigationSelection();
        } else if (itemId == R.id.nav_logout) {
            showLogoutConfirmation();
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            setTitle(title);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Handle Bottom Navigation Menu
    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "";

        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
            title = "Home";
            navigationView.setCheckedItem(R.id.nav_home);
        } else if (itemId == R.id.nav_service_centers) {
            selectedFragment = new ServiceCenterFragment();
            title = "Service Centers";
            navigationView.setCheckedItem(R.id.nav_service_centers);
        } else if (itemId == R.id.nav_bookings) {
            selectedFragment = new BookingFragment();
            title = "My Bookings";
            navigationView.setCheckedItem(R.id.nav_bookings);
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
            title = "Profile";
            navigationView.setCheckedItem(R.id.nav_profile);
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            setTitle(title);
            return true;
        }

        return false;
    }

    private void clearBottomNavigationSelection() {
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", null)
                .show();
    }

    public void logoutUser() {
        mAuth.signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (bottomNavigationView.getSelectedItemId() != R.id.nav_home) {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
                navigationView.setCheckedItem(R.id.nav_home);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Exit App")
                        .setMessage("Do you want to exit the app?")
                        .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed())
                        .setNegativeButton("No", null)
                        .show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void selectNavigationItem(int drawerItemId, int bottomItemId) {
        if (drawerItemId != -1) {
            navigationView.setCheckedItem(drawerItemId);
        }
        if (bottomItemId != -1) {
            bottomNavigationView.setSelectedItemId(bottomItemId);
        }
    }
}

