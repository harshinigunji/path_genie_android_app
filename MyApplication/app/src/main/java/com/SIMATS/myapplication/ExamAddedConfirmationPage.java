package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * ExamAddedConfirmationPage - Shows confirmation that exam was added to
 * roadmap.
 * Displays the added exam info and provides navigation options.
 */
public class ExamAddedConfirmationPage extends AppCompatActivity {

    // UI Components
    private ImageView backButton;
    private TextView examNameText;
    private TextView examTypeText;
    private Button exploreNextButton;
    private Button exploreJobsButton;

    // Data
    private int streamId = -1;
    private int educationLevelId = -1;
    private String streamName = "";
    private String examName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam_added_confirmation_page);

        // Get data from intent
        streamId = getIntent().getIntExtra("stream_id", -1);
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);
        streamName = getIntent().getStringExtra("stream_name");
        examName = getIntent().getStringExtra("exam_name");

        if (streamName == null)
            streamName = "";
        if (examName == null)
            examName = "Entrance Exam";

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initViews();

        // Set exam info
        examNameText.setText(examName);
        examTypeText.setText("Entrance Exam for " + streamName);

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        examNameText = findViewById(R.id.examNameText);
        examTypeText = findViewById(R.id.examTypeText);
        exploreNextButton = findViewById(R.id.exploreNextButton);
        exploreJobsButton = findViewById(R.id.exploreJobsButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Explore Next button - Navigate to NextStreamSuggestionsPage
        exploreNextButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NextStreamSuggestionsPage.class);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("education_level_id", educationLevelId);
            startActivity(intent);
            finish();
        });

        // Job Opportunities button - Navigate to JobsPage
        exploreJobsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, JobsPage.class);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("stream_name", streamName);
            startActivity(intent);
            finish();
        });
    }
}
