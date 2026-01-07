package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * RecommendationP2Page - Interest Area Selection
 * Allows user to select their primary interest area for AI recommendations.
 * 
 * Interest Areas: Technology, Science, Commerce, Arts, Law
 */
public class RecommendationP2Page extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnContinue;

    // Interest area cards
    private LinearLayout cardTechnology;
    private LinearLayout cardScience;
    private LinearLayout cardCommerce;
    private LinearLayout cardArts;
    private LinearLayout cardLaw;

    private LinearLayout selectedCard = null;
    private String selectedInterest = null;
    private int educationLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommendation_p2_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get education level from previous page
        educationLevel = getIntent().getIntExtra("education_level", 1);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnContinue = findViewById(R.id.btnContinue);

        cardTechnology = findViewById(R.id.cardTechnology);
        cardScience = findViewById(R.id.cardScience);
        cardCommerce = findViewById(R.id.cardCommerce);
        cardArts = findViewById(R.id.cardArts);
        cardLaw = findViewById(R.id.cardLaw);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Interest area cards
        cardTechnology.setOnClickListener(v -> selectCard(cardTechnology, "Technology"));
        cardScience.setOnClickListener(v -> selectCard(cardScience, "Science"));
        cardCommerce.setOnClickListener(v -> selectCard(cardCommerce, "Commerce"));
        cardArts.setOnClickListener(v -> selectCard(cardArts, "Arts"));
        cardLaw.setOnClickListener(v -> selectCard(cardLaw, "Law"));

        // Continue button
        btnContinue.setOnClickListener(v -> {
            if (selectedInterest != null) {
                Intent intent = new Intent(RecommendationP2Page.this, RecommendationP3Page.class);
                intent.putExtra("education_level", educationLevel);
                intent.putExtra("interest_area", selectedInterest);
                startActivity(intent);
            }
        });
    }

    /**
     * Handle card selection with visual feedback.
     */
    private void selectCard(LinearLayout card, String interest) {
        // Deselect previous card
        if (selectedCard != null) {
            selectedCard.setBackgroundResource(R.drawable.bg_card_selectable);
        }

        // Select new card
        selectedCard = card;
        selectedInterest = interest;
        card.setBackgroundResource(R.drawable.bg_card_selected);

        // Enable continue button
        btnContinue.setEnabled(true);
        btnContinue.setBackgroundResource(R.drawable.bg_primary_button);
    }
}
