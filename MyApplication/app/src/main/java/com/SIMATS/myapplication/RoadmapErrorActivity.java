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

public class RoadmapErrorActivity extends AppCompatActivity {

    private Button editGoalButton;
    private Button closeButton;
    private TextView errorMessageText;

    private int educationLevelId;
    private String educationLevelName;
    private int streamId;
    private String streamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_roadmap_error);

        // Retrieve passed data so we can go back to P3 correctly
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);
        educationLevelName = getIntent().getStringExtra("education_level_name");
        streamId = getIntent().getIntExtra("stream_id", -1);
        streamName = getIntent().getStringExtra("stream_name");
        String serverMessage = getIntent().getStringExtra("error_message");

        initViews();

        if (serverMessage != null && !serverMessage.isEmpty()) {
            errorMessageText.setText(serverMessage);
        }

        setupClickListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        editGoalButton = findViewById(R.id.editGoalButton);
        closeButton = findViewById(R.id.closeButton);
        errorMessageText = findViewById(R.id.errorMessage);
    }

    private void setupClickListeners() {
        // "Edit Goal" -> Go back to SystemGenerateP3Page (Job Selection)
        editGoalButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SystemGenerateP3Page.class);
            intent.putExtra("education_level_id", educationLevelId);
            intent.putExtra("education_level_name", educationLevelName);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("stream_name", streamName);
            // Clear top to avoid stacking up errors
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // "Close" -> Just finish, likely returning to P4 or Home depending on stack
        closeButton.setOnClickListener(v -> finish());
    }
}
