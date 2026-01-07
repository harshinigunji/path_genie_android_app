package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * JobsEducationLevelsPage Activity - User selects their education level to see
 * relevant jobs.
 * 
 * Maps to database education_levels table:
 * 1 = 10th Pass
 * 2 = 12th Science
 * 3 = 12th Commerce
 * 4 = 12th Arts
 * 5 = Diploma
 * 6 = Undergraduate
 * 7 = Postgraduate
 * 
 * Navigation Flow:
 * HomePage → JobsEducationLevelsPage → EducationLevelJobsPage →
 * EducationLevelJobDetailsPage → HomePage
 */
public class JobsEducationLevelsPage extends AppCompatActivity {

    // UI Components
    private ImageView backButton;
    private LinearLayout card10thPass;
    private LinearLayout card12thScience;
    private LinearLayout card12thCommerce;
    private LinearLayout card12thArts;
    private LinearLayout cardDiploma;
    private LinearLayout cardUndergraduate;
    private LinearLayout cardPostgraduate;
    private Button continueButton;

    // State
    private LinearLayout selectedCard = null;
    private int selectedEducationLevelId = -1;
    private String selectedLevelName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jobs_education_levels_page);

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

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        card10thPass = findViewById(R.id.card10thPass);
        card12thScience = findViewById(R.id.card12thScience);
        card12thCommerce = findViewById(R.id.card12thCommerce);
        card12thArts = findViewById(R.id.card12thArts);
        cardDiploma = findViewById(R.id.cardDiploma);
        cardUndergraduate = findViewById(R.id.cardUndergraduate);
        cardPostgraduate = findViewById(R.id.cardPostgraduate);
        continueButton = findViewById(R.id.continueButton);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Education level cards with DB IDs
        card10thPass.setOnClickListener(v -> selectCard(card10thPass, 1, "10th Pass"));
        card12thScience.setOnClickListener(v -> selectCard(card12thScience, 2, "12th Science"));
        card12thCommerce.setOnClickListener(v -> selectCard(card12thCommerce, 3, "12th Commerce"));
        card12thArts.setOnClickListener(v -> selectCard(card12thArts, 4, "12th Arts"));
        cardDiploma.setOnClickListener(v -> selectCard(cardDiploma, 5, "Diploma"));
        cardUndergraduate.setOnClickListener(v -> selectCard(cardUndergraduate, 6, "Undergraduate"));
        cardPostgraduate.setOnClickListener(v -> selectCard(cardPostgraduate, 7, "Postgraduate"));

        // Continue button
        continueButton.setOnClickListener(v -> navigateToJobsPage());
    }

    /**
     * Handle card selection.
     */
    private void selectCard(LinearLayout card, int educationLevelId, String levelName) {
        // Deselect previous card
        if (selectedCard != null) {
            selectedCard.setSelected(false);
        }

        // Select new card
        card.setSelected(true);
        selectedCard = card;
        selectedEducationLevelId = educationLevelId;
        selectedLevelName = levelName;

        // Enable continue button
        enableContinueButton();
    }

    /**
     * Enable the continue button after a selection is made.
     */
    private void enableContinueButton() {
        continueButton.setEnabled(true);
        continueButton.setBackgroundResource(R.drawable.bg_button_primary);
    }

    /**
     * Navigate to the EducationLevelJobsPage with the selected education level.
     */
    private void navigateToJobsPage() {
        if (selectedEducationLevelId == -1) {
            Toast.makeText(this, "Please select your education level", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to EducationLevelJobsPage with education_level_id
        Intent intent = new Intent(JobsEducationLevelsPage.this, EducationLevelJobsPage.class);
        intent.putExtra("education_level_id", selectedEducationLevelId);
        intent.putExtra("education_level_name", selectedLevelName);
        startActivity(intent);
    }
}
