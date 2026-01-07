package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.SIMATS.myapplication.network.VolleySingleton;

import org.json.JSONObject;

/**
 * EducationLevelJobDetailsPage Activity - Displays job details.
 * 
 * Navigation Flow:
 * HomePage → JobsEducationLevelsPage → EducationLevelJobsPage →
 * EducationLevelJobDetailsPage → HomePage
 * 
 * Fetches job details from job_details.php API based on job_id.
 */
public class EducationLevelJobDetailsPage extends AppCompatActivity {

    private static final String TAG = "JobDetailsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private ProgressBar progressBar;
    private ScrollView contentScroll;
    private TextView jobTypeChip;
    private TextView jobName;
    private TextView jobSubtitle;
    private TextView averageSalary;
    private TextView jobDescription;
    private TextView requiredEducation;
    private TextView requiredExams;
    private TextView careerGrowth;
    private Button btnHome;

    // Data
    private int jobId = -1;
    private int educationLevelId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_education_level_job_details_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from intent
        jobId = getIntent().getIntExtra("job_id", -1);
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);

        if (jobId == -1) {
            Toast.makeText(this, "Invalid job", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        fetchJobDetails();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        contentScroll = findViewById(R.id.contentScroll);
        jobTypeChip = findViewById(R.id.jobTypeChip);
        jobName = findViewById(R.id.jobName);
        jobSubtitle = findViewById(R.id.jobSubtitle);
        averageSalary = findViewById(R.id.averageSalary);
        jobDescription = findViewById(R.id.jobDescription);
        requiredEducation = findViewById(R.id.requiredEducation);
        requiredExams = findViewById(R.id.requiredExams);
        careerGrowth = findViewById(R.id.careerGrowth);
        btnHome = findViewById(R.id.btnHome);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> navigateToHome());
    }

    private void fetchJobDetails() {
        progressBar.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "job_details.php?job_id=" + jobId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    contentScroll.setVisibility(View.VISIBLE);
                    try {
                        boolean status = response.getBoolean("status");
                        if (status) {
                            JSONObject job = response.getJSONObject("data");
                            populateUI(job);
                        } else {
                            String message = response.optString("message", "Failed to fetch job details");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error loading job details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    contentScroll.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Error fetching job: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void populateUI(JSONObject job) {
        try {
            // Job name
            String name = job.optString("job_name", "Job Details");
            jobName.setText(name);

            // Job type chip
            String type = job.optString("job_type", "Private");
            jobTypeChip.setText(type.toUpperCase());

            // Subtitle (short description)
            String desc = job.optString("description", "");
            if (desc.length() > 50) {
                jobSubtitle.setText(desc.substring(0, 50) + "...");
            } else {
                jobSubtitle.setText(desc);
            }

            // Full description
            jobDescription.setText(desc.isEmpty() ? "No description available." : desc);

            // Average salary
            String salary = job.optString("average_salary", "");
            if (!salary.isEmpty()) {
                averageSalary.setText(salary);
            } else {
                averageSalary.setText("Varies based on experience");
            }

            // Required education
            String education = job.optString("required_education", "");
            if (!education.isEmpty()) {
                requiredEducation.setText(education);
            } else {
                requiredEducation.setText("Check job requirements");
            }

            // Required exams
            String exams = job.optString("required_exams", "");
            if (!exams.isEmpty()) {
                requiredExams.setText(exams);
            } else {
                requiredExams.setText("No specific exams required");
            }

            // Career growth
            String growth = job.optString("career_growth", "");
            if (!growth.isEmpty()) {
                careerGrowth.setText(growth);
            } else {
                careerGrowth.setText("Multiple career advancement opportunities available");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error populating UI: " + e.getMessage());
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

