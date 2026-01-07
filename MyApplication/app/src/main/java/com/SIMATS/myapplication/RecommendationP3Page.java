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
 * RecommendationP3Page - Career Preferences Selection
 * Allows user to select career preferences, difficulty tolerance, and risk
 * tolerance.
 * 
 * Parameters collected:
 * - Career Preference: Government (0) / Private (1)
 * - Difficulty Tolerance: Easy (1) / Medium (2) / Hard (3)
 * - Risk Tolerance: Low (1) / Medium (2) / High (3)
 */
public class RecommendationP3Page extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnContinue;

    // Career Preference cards
    private LinearLayout cardGovt;
    private LinearLayout cardPrivate;

    // Difficulty options
    private LinearLayout optionEasy;
    private LinearLayout optionMedium;
    private LinearLayout optionHard;

    // Risk Tolerance cards
    private LinearLayout cardLowRisk;
    private LinearLayout cardMediumRisk;
    private LinearLayout cardHighRisk;

    // Selected values
    private int educationLevel;
    private String interestArea;
    private int careerPreference = -1; // 0 = Govt, 1 = Private
    private int difficultyTolerance = -1; // 1 = Easy, 2 = Medium, 3 = Hard
    private int riskTolerance = -1; // 1 = Low, 2 = Medium, 3 = High

    // Currently selected views
    private LinearLayout selectedCareerCard = null;
    private LinearLayout selectedDifficultyOption = null;
    private LinearLayout selectedRiskCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommendation_p3_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from previous pages
        educationLevel = getIntent().getIntExtra("education_level", 1);
        interestArea = getIntent().getStringExtra("interest_area");

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnContinue = findViewById(R.id.btnContinue);

        // Career Preference
        cardGovt = findViewById(R.id.cardGovt);
        cardPrivate = findViewById(R.id.cardPrivate);

        // Difficulty Tolerance
        optionEasy = findViewById(R.id.optionEasy);
        optionMedium = findViewById(R.id.optionMedium);
        optionHard = findViewById(R.id.optionHard);

        // Risk Tolerance
        cardLowRisk = findViewById(R.id.cardLowRisk);
        cardMediumRisk = findViewById(R.id.cardMediumRisk);
        cardHighRisk = findViewById(R.id.cardHighRisk);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Career Preference cards
        cardGovt.setOnClickListener(v -> selectCareerPreference(cardGovt, 0));
        cardPrivate.setOnClickListener(v -> selectCareerPreference(cardPrivate, 1));

        // Difficulty Tolerance options
        optionEasy.setOnClickListener(v -> selectDifficulty(optionEasy, 1));
        optionMedium.setOnClickListener(v -> selectDifficulty(optionMedium, 2));
        optionHard.setOnClickListener(v -> selectDifficulty(optionHard, 3));

        // Risk Tolerance cards
        cardLowRisk.setOnClickListener(v -> selectRisk(cardLowRisk, 1));
        cardMediumRisk.setOnClickListener(v -> selectRisk(cardMediumRisk, 2));
        cardHighRisk.setOnClickListener(v -> selectRisk(cardHighRisk, 3));

        // Continue button
        btnContinue.setOnClickListener(v -> {
            if (isAllSelected()) {
                Intent intent = new Intent(RecommendationP3Page.this, RecommendationLoadingPage.class);
                intent.putExtra("education_level", educationLevel);
                intent.putExtra("interest_area", interestArea);
                intent.putExtra("career_preference_private", careerPreference);
                intent.putExtra("difficulty_tolerance", difficultyTolerance);
                intent.putExtra("risk_tolerance", riskTolerance);
                startActivity(intent);
            }
        });
    }

    private void selectCareerPreference(LinearLayout card, int value) {
        if (selectedCareerCard != null) {
            selectedCareerCard.setBackgroundResource(R.drawable.bg_card_selectable);
        }
        selectedCareerCard = card;
        careerPreference = value;
        card.setBackgroundResource(R.drawable.bg_card_selected);
        updateContinueButton();
    }

    private void selectDifficulty(LinearLayout option, int value) {
        if (selectedDifficultyOption != null) {
            selectedDifficultyOption.setBackgroundResource(R.drawable.bg_card_selectable);
        }
        selectedDifficultyOption = option;
        difficultyTolerance = value;
        option.setBackgroundResource(R.drawable.bg_card_selected);
        updateContinueButton();
    }

    private void selectRisk(LinearLayout card, int value) {
        if (selectedRiskCard != null) {
            selectedRiskCard.setBackgroundResource(R.drawable.bg_card_selectable);
        }
        selectedRiskCard = card;
        riskTolerance = value;
        card.setBackgroundResource(R.drawable.bg_card_selected);
        updateContinueButton();
    }

    private boolean isAllSelected() {
        return careerPreference != -1 && difficultyTolerance != -1 && riskTolerance != -1;
    }

    private void updateContinueButton() {
        if (isAllSelected()) {
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundResource(R.drawable.bg_primary_button);
        }
    }
}
