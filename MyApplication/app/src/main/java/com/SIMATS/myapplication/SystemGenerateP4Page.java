package com.SIMATS.myapplication;

import com.SIMATS.myapplication.network.ApiConfig;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.SIMATS.myapplication.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * SystemGenerateP4Page - Step 4: Review & Confirm
 */
public class SystemGenerateP4Page extends AppCompatActivity {

    // Using centralized ApiConfig.getBaseUrl()

    private ImageView backButton;
    private Button confirmButton;
    private TextView educationLevelText, streamText, jobText;
    private ImageView editEducationButton, editStreamButton, editJobButton;

    // Data from previous pages
    private int educationLevelId = -1;
    private String educationLevelName = "";
    private int streamId = -1;
    private String streamName = "";
    private int jobId = -1;
    private String jobName = "";
    private String jobSalary = "";

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_generate_p4_page);

        // Get data from previous pages
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);
        educationLevelName = getIntent().getStringExtra("education_level_name");
        streamId = getIntent().getIntExtra("stream_id", -1);
        streamName = getIntent().getStringExtra("stream_name");
        jobId = getIntent().getIntExtra("job_id", -1);
        jobName = getIntent().getStringExtra("job_name");
        jobSalary = getIntent().getStringExtra("job_salary");

        if (educationLevelName == null)
            educationLevelName = "";
        if (streamName == null)
            streamName = "";
        if (jobName == null)
            jobName = "";
        if (jobSalary == null)
            jobSalary = "";

        sessionManager = new SessionManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        displayData();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        confirmButton = findViewById(R.id.confirmButton);
        educationLevelText = findViewById(R.id.educationLevelText);
        streamText = findViewById(R.id.streamText);
        jobText = findViewById(R.id.jobText);
        editEducationButton = findViewById(R.id.editEducationButton);
        editStreamButton = findViewById(R.id.editStreamButton);
        editJobButton = findViewById(R.id.editJobButton);
    }

    private void displayData() {
        educationLevelText.setText(educationLevelName);
        streamText.setText(streamName);
        jobText.setText(jobName);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Edit buttons - navigate back to respective pages
        editEducationButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SystemGenerateP1Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        editStreamButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SystemGenerateP2Page.class);
            intent.putExtra("education_level_id", educationLevelId);
            intent.putExtra("education_level_name", educationLevelName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        editJobButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SystemGenerateP3Page.class);
            intent.putExtra("education_level_id", educationLevelId);
            intent.putExtra("education_level_name", educationLevelName);
            intent.putExtra("stream_id", streamId);
            intent.putExtra("stream_name", streamName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Confirm button - generate roadmap
        confirmButton.setOnClickListener(v -> generateRoadmap());
    }

    private void generateRoadmap() {
        // Navigate to SystemGeneratePage with all data
        Intent intent = new Intent(this, SystemGeneratePage.class);
        intent.putExtra("education_level_id", educationLevelId);
        intent.putExtra("education_level_name", educationLevelName);
        intent.putExtra("stream_id", streamId);
        intent.putExtra("stream_name", streamName);
        intent.putExtra("job_id", jobId);
        intent.putExtra("job_name", jobName);
        intent.putExtra("job_salary", jobSalary);
        startActivity(intent);
        finish();
    }
}

