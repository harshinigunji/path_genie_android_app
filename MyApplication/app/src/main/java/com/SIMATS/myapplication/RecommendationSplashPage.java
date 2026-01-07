package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * RecommendationSplashPage - Entry point for AI Career Recommendation flow.
 * Shows AI Career Recommendation Engine intro and Start button.
 */
public class RecommendationSplashPage extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnStartAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommendation_splash_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnStartAI = findViewById(R.id.btnStartAI);
    }

    private void setupClickListeners() {
        // Back button - return to HomePage
        btnBack.setOnClickListener(v -> finish());

        // Start AI Recommendation - navigate to P1 (Education Level)
        btnStartAI.setOnClickListener(v -> {
            Intent intent = new Intent(RecommendationSplashPage.this, RecommendationP1Page.class);
            startActivity(intent);
        });
    }
}
