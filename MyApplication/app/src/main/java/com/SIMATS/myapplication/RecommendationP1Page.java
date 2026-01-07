package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * RecommendationP1Page - Education Level Selection
 * Allows user to select their current education level for AI recommendations.
 * 
 * Education Level Mapping:
 * - 10th Pass = 1
 * - 12th Science = 2
 * - 12th Commerce = 3
 * - 12th Arts = 4
 * - Diploma = 5
 * - Undergraduate = 6
 * - Postgraduate = 7
 */
public class RecommendationP1Page extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnContinue;

    // Education level cards
    private LinearLayout card10th;
    private LinearLayout card12Science;
    private LinearLayout card12Commerce;
    private LinearLayout card12Arts;
    private LinearLayout cardDiploma;
    private LinearLayout cardUG;
    private LinearLayout cardPG;

    private LinearLayout selectedCard = null;
    private int selectedEducationLevel = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommendation_p1_page);

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
        btnContinue = findViewById(R.id.btnContinue);

        card10th = findViewById(R.id.card10th);
        card12Science = findViewById(R.id.card12Science);
        card12Commerce = findViewById(R.id.card12Commerce);
        card12Arts = findViewById(R.id.card12Arts);
        cardDiploma = findViewById(R.id.cardDiploma);
        cardUG = findViewById(R.id.cardUG);
        cardPG = findViewById(R.id.cardPG);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Education level cards
        card10th.setOnClickListener(v -> selectCard(card10th, 1));
        card12Science.setOnClickListener(v -> selectCard(card12Science, 2));
        card12Commerce.setOnClickListener(v -> selectCard(card12Commerce, 3));
        card12Arts.setOnClickListener(v -> selectCard(card12Arts, 4));
        cardDiploma.setOnClickListener(v -> selectCard(cardDiploma, 5));
        cardUG.setOnClickListener(v -> selectCard(cardUG, 6));
        cardPG.setOnClickListener(v -> selectCard(cardPG, 7));

        // Continue button
        btnContinue.setOnClickListener(v -> {
            if (selectedEducationLevel != -1) {
                Intent intent = new Intent(RecommendationP1Page.this, RecommendationP2Page.class);
                intent.putExtra("education_level", selectedEducationLevel);
                startActivity(intent);
            }
        });
    }

    /**
     * Handle card selection with visual feedback.
     */
    private void selectCard(LinearLayout card, int educationLevel) {
        // Deselect previous card
        if (selectedCard != null) {
            selectedCard.setBackgroundResource(R.drawable.bg_card_selectable);
        }

        // Select new card
        selectedCard = card;
        selectedEducationLevel = educationLevel;
        card.setBackgroundResource(R.drawable.bg_card_selected);

        // Enable continue button
        btnContinue.setEnabled(true);
        btnContinue.setBackgroundResource(R.drawable.bg_primary_button);
    }
}
