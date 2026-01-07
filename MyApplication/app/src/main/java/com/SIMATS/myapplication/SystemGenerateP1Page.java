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
 * SystemGenerateP1Page - Step 1: Select Education Level
 * Education Level IDs should match database:
 * 1 = 10th Pass
 * 2 = 12th Science
 * 3 = 12th Commerce
 * 4 = 12th Arts
 * 5 = Diploma
 * 6 = Undergraduate
 * 7 = Postgraduate
 */
public class SystemGenerateP1Page extends AppCompatActivity {

    private ImageView backButton;
    private Button nextButton;

    // Option cards (7 education levels)
    private LinearLayout option10th, option12thScience, option12thCommerce, option12thArts;
    private LinearLayout optionDiploma, optionUndergraduate, optionPostgraduate;

    // Selected data
    private int selectedEducationLevelId = -1;
    private String selectedEducationLevelName = "";
    private LinearLayout currentSelectedCard = null;

    private static final String[] EDUCATION_LEVEL_NAMES = {
            "10th Pass",
            "12th Science",
            "12th Commerce",
            "12th Arts",
            "Diploma",
            "Undergraduate",
            "Postgraduate"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_generate_p1_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);

        option10th = findViewById(R.id.option10th);
        option12thScience = findViewById(R.id.option12thScience);
        option12thCommerce = findViewById(R.id.option12thCommerce);
        option12thArts = findViewById(R.id.option12thArts);
        optionDiploma = findViewById(R.id.optionDiploma);
        optionUndergraduate = findViewById(R.id.optionUndergraduate);
        optionPostgraduate = findViewById(R.id.optionPostgraduate);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Set up option click listeners with correct database IDs
        option10th.setOnClickListener(v -> selectOption(option10th, 1, EDUCATION_LEVEL_NAMES[0]));
        option12thScience.setOnClickListener(v -> selectOption(option12thScience, 2, EDUCATION_LEVEL_NAMES[1]));
        option12thCommerce.setOnClickListener(v -> selectOption(option12thCommerce, 3, EDUCATION_LEVEL_NAMES[2]));
        option12thArts.setOnClickListener(v -> selectOption(option12thArts, 4, EDUCATION_LEVEL_NAMES[3]));
        optionDiploma.setOnClickListener(v -> selectOption(optionDiploma, 5, EDUCATION_LEVEL_NAMES[4]));
        optionUndergraduate.setOnClickListener(v -> selectOption(optionUndergraduate, 6, EDUCATION_LEVEL_NAMES[5]));
        optionPostgraduate.setOnClickListener(v -> selectOption(optionPostgraduate, 7, EDUCATION_LEVEL_NAMES[6]));

        nextButton.setOnClickListener(v -> {
            if (selectedEducationLevelId != -1) {
                Intent intent = new Intent(this, SystemGenerateP2Page.class);
                intent.putExtra("education_level_id", selectedEducationLevelId);
                intent.putExtra("education_level_name", selectedEducationLevelName);
                startActivity(intent);
            }
        });
    }

    private void selectOption(LinearLayout selectedCard, int levelId, String levelName) {
        // Reset previous selection
        if (currentSelectedCard != null) {
            currentSelectedCard.setBackgroundResource(R.drawable.bg_card_selectable);
        }

        // Set new selection
        selectedCard.setBackgroundResource(R.drawable.bg_card_selected);
        currentSelectedCard = selectedCard;
        selectedEducationLevelId = levelId;
        selectedEducationLevelName = levelName;
    }
}
