package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.SIMATS.myapplication.utils.SessionManager;

/**
 * DashboardActivity - Test activity to verify login success.
 * Shows user info and provides logout functionality.
 */
public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView userIdText;
    private TextView userEmailText;
    private Button logoutButton;
    private Button roadmapGenButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Check if logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initViews();

        // Display user info
        displayUserInfo();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        welcomeText = findViewById(R.id.welcomeText);
        userIdText = findViewById(R.id.userIdText);
        userEmailText = findViewById(R.id.userEmailText);
        logoutButton = findViewById(R.id.logoutButton);
        roadmapGenButton = findViewById(R.id.roadmapGenButton);
    }

    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> logout());

        // Navigate to System Generate P1 Page
        roadmapGenButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SystemGenerateP1Page.class);
            startActivity(intent);
        });
    }

    private void displayUserInfo() {
        int userId = sessionManager.getUserId();
        String email = sessionManager.getUserEmail();

        welcomeText.setText("Welcome to Education Stream Advisor!");
        userIdText.setText("User ID: " + userId);
        userEmailText.setText("Email: " + (email != null ? email : "N/A"));
    }

    private void logout() {
        sessionManager.logout();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(DashboardActivity.this, LoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
