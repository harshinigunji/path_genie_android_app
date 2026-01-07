package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.SIMATS.myapplication.utils.SessionManager;

/**
 * OnboardingPage Activity - First screen shown when app launches.
 * Displays app introduction and navigates to LoginPage when user taps "Get
 * Started".
 */
public class OnboardingPage extends AppCompatActivity {

    private Button getStartedButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding_page);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Setup edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    /**
     * Initialize view references.
     */
    private void initViews() {
        getStartedButton = findViewById(R.id.getStartedButton);
    }

    /**
     * Setup click listeners for interactive elements.
     */
    private void setupClickListeners() {
        getStartedButton.setOnClickListener(v -> navigateToLogin());
    }

    /**
     * Navigate to LoginPage and clear back stack so user can't return to
     * onboarding.
     */
    private void navigateToLogin() {
        // Clear any existing session to ensure fresh login
        sessionManager.logout();

        Intent intent = new Intent(OnboardingPage.this, LoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
