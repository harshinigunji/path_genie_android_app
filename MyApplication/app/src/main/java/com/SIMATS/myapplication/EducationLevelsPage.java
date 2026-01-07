package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.SIMATS.myapplication.utils.SessionManager;
import com.SIMATS.myapplication.utils.RoadmapSessionManager;

/**
 * EducationLevelsPage Activity - User selects their current education level.
 * This is a key step in the roadmap personalization flow.
 * 
 * Maps to database education_levels table:
 * 1 = 10th Pass
 * 2 = 12th Science
 * 3 = 12th Commerce
 * 4 = 12th Arts
 * 5 = Diploma
 * 6 = Undergraduate
 * 7 = Postgraduate
 */
public class EducationLevelsPage extends AppCompatActivity {

    // UI Components
    private ImageView backButton;
    private Button continueButton;
    private LinearLayout card10thPass, card12thScience, card12thCommerce, card12thArts;
    private LinearLayout cardDiploma, cardUndergraduate, cardPostgraduate;
    private LinearLayout[] allCards;

    // State
    private int selectedEducationLevelId = -1;
    private String selectedLevelName = "";
    private LinearLayout selectedCard = null;
    private SessionManager sessionManager;
    private RoadmapSessionManager roadmapSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_education_levels_page);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        roadmapSessionManager = new RoadmapSessionManager(this);

        // Clear session when starting fresh
        roadmapSessionManager.clearSession();

        // Setup edge-to-edge
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
     * Initialize all view references.
     */
    private void initViews() {
        backButton = findViewById(R.id.backButton);
        continueButton = findViewById(R.id.continueButton);

        // Cards (7 education levels)
        card10thPass = findViewById(R.id.card10thPass);
        card12thScience = findViewById(R.id.card12thScience);
        card12thCommerce = findViewById(R.id.card12thCommerce);
        card12thArts = findViewById(R.id.card12thArts);
        cardDiploma = findViewById(R.id.cardDiploma);
        cardUndergraduate = findViewById(R.id.cardUndergraduate);
        cardPostgraduate = findViewById(R.id.cardPostgraduate);

        allCards = new LinearLayout[] {
                card10thPass, card12thScience, card12thCommerce, card12thArts,
                cardDiploma, cardUndergraduate, cardPostgraduate
        };
    }

    /**
     * Setup click listeners for all interactive elements.
     */
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Continue button
        continueButton.setOnClickListener(v -> navigateToNextScreen());

        // Card click listeners with education_level_id mapping (matching DB IDs)
        card10thPass.setOnClickListener(v -> selectCard(card10thPass, 1, "10th Pass"));
        card12thScience.setOnClickListener(v -> selectCard(card12thScience, 2, "12th Science"));
        card12thCommerce.setOnClickListener(v -> selectCard(card12thCommerce, 3, "12th Commerce"));
        card12thArts.setOnClickListener(v -> selectCard(card12thArts, 4, "12th Arts"));
        cardDiploma.setOnClickListener(v -> selectCard(cardDiploma, 5, "Diploma"));
        cardUndergraduate.setOnClickListener(v -> selectCard(cardUndergraduate, 6, "Undergraduate"));
        cardPostgraduate.setOnClickListener(v -> selectCard(cardPostgraduate, 7, "Postgraduate"));
    }

    /**
     * Handle card selection - updates visual state and stores selected ID.
     *
     * @param card             The clicked card view
     * @param educationLevelId The corresponding education_level_id from database
     * @param levelName        The name of the education level
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
     * Navigate to the next screen (Stream Selection) with the selected education
     * level.
     */
    private void navigateToNextScreen() {
        if (selectedEducationLevelId == -1) {
            Toast.makeText(this, "Please select your education level", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear any previous session and add new education level step
        roadmapSessionManager.clearSession();
        roadmapSessionManager.addEducationLevel(selectedEducationLevelId, selectedLevelName);

        // Navigate to StreamsPage with education_level_id
        Intent intent = new Intent(EducationLevelsPage.this, StreamsPage.class);
        intent.putExtra("education_level_id", selectedEducationLevelId);
        intent.putExtra("education_level_name", selectedLevelName);
        startActivity(intent);
    }
}
