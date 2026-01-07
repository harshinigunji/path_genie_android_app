package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * EducationLevelJobsPage Activity - Displays jobs for a specific education
 * level.
 * Fetches data from jobs_by_level.php API based on education_level_id.
 * 
 * Navigation Flow:
 * HomePage → JobsEducationLevelsPage → EducationLevelJobsPage →
 * EducationLevelJobDetailsPage → HomePage
 */
public class EducationLevelJobsPage extends AppCompatActivity {

    private static final String TAG = "EducationLevelJobsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView titleText;
    private TextView qualificationChip;
    private LinearLayout jobsContainer;
    private ProgressBar progressBar;
    private TextView noJobsText;
    private TextView btnGovernment;
    private TextView btnPrivate;

    // Data
    private int educationLevelId = -1;
    private String educationLevelName = "";
    private List<JSONObject> allJobs = new ArrayList<>();
    private String currentFilter = "Government"; // Default filter

    // Education level names mapping
    private static final String[] LEVEL_NAMES = {
            "", "10th Pass", "12th Science", "12th Commerce", "12th Arts", "Diploma", "Undergraduate", "Postgraduate"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_education_level_jobs_page);

        // Get education_level_id from intent
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);
        educationLevelName = getIntent().getStringExtra("education_level_name");

        if (educationLevelId == -1) {
            Toast.makeText(this, "Invalid education level", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get level name if not passed
        if (educationLevelName == null && educationLevelId > 0 && educationLevelId < LEVEL_NAMES.length) {
            educationLevelName = LEVEL_NAMES[educationLevelId];
        }

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

        // Set dynamic title
        titleText.setText("Jobs for " + educationLevelName);
        qualificationChip.setText("🎓 QUALIFICATION: " + educationLevelName.toUpperCase());

        // Fetch jobs from API
        fetchJobs();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        qualificationChip = findViewById(R.id.qualificationChip);
        jobsContainer = findViewById(R.id.jobsContainer);
        progressBar = findViewById(R.id.progressBar);
        noJobsText = findViewById(R.id.noJobsText);
        btnGovernment = findViewById(R.id.btnGovernment);
        btnPrivate = findViewById(R.id.btnPrivate);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Toggle buttons
        btnGovernment.setOnClickListener(v -> {
            currentFilter = "Government";
            updateToggleUI();
            filterAndDisplayJobs();
        });

        btnPrivate.setOnClickListener(v -> {
            currentFilter = "Private";
            updateToggleUI();
            filterAndDisplayJobs();
        });
    }

    /**
     * Update toggle button UI based on current filter.
     */
    private void updateToggleUI() {
        int primaryColor = android.graphics.Color.parseColor("#2563EB");
        int secondaryColor = android.graphics.Color.parseColor("#6B7280");

        if (currentFilter.equals("Government")) {
            btnGovernment.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnGovernment.setTextColor(primaryColor);
            btnPrivate.setBackground(null);
            btnPrivate.setTextColor(secondaryColor);
        } else {
            btnPrivate.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnPrivate.setTextColor(primaryColor);
            btnGovernment.setBackground(null);
            btnGovernment.setTextColor(secondaryColor);
        }
    }

    /**
     * Fetch jobs from API based on education_level_id.
     */
    private void fetchJobs() {
        progressBar.setVisibility(View.VISIBLE);
        jobsContainer.removeAllViews();
        noJobsText.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "jobs_by_level.php?education_level_id=" + educationLevelId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        boolean status = response.getBoolean("status");
                        if (status) {
                            JSONArray jobs = response.getJSONArray("data");
                            allJobs.clear();
                            for (int i = 0; i < jobs.length(); i++) {
                                allJobs.add(jobs.getJSONObject(i));
                            }

                            if (allJobs.isEmpty()) {
                                noJobsText.setVisibility(View.VISIBLE);
                            } else {
                                filterAndDisplayJobs();
                            }
                        } else {
                            String message = response.optString("message", "Failed to fetch jobs");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error loading jobs", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching jobs: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Filter and display jobs based on current filter.
     */
    private void filterAndDisplayJobs() {
        jobsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        boolean hasJobs = false;
        for (JSONObject job : allJobs) {
            try {
                String jobType = job.optString("job_type", "Private");
                if (jobType.equals(currentFilter)) {
                    hasJobs = true;
                    displayJobCard(inflater, job);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error filtering job: " + e.getMessage());
            }
        }

        if (!hasJobs) {
            noJobsText.setText("No " + currentFilter.toLowerCase() + " jobs found");
            noJobsText.setVisibility(View.VISIBLE);
        } else {
            noJobsText.setVisibility(View.GONE);
        }
    }

    /**
     * Display a job card.
     */
    private void displayJobCard(LayoutInflater inflater, JSONObject job) {
        try {
            int jobId = job.getInt("job_id");
            String jobName = job.getString("job_name");
            String jobType = job.optString("job_type", "Private");
            String description = job.optString("description", "");
            String salary = job.optString("average_salary", "");

            // Inflate job item
            View jobView = inflater.inflate(R.layout.item_job, jobsContainer, false);

            // Set data
            TextView categoryView = jobView.findViewById(R.id.jobCategory);
            TextView nameView = jobView.findViewById(R.id.jobName);
            TextView descView = jobView.findViewById(R.id.jobDescription);
            TextView tagView = jobView.findViewById(R.id.jobTag);

            nameView.setText(jobName);
            descView.setText(description);

            // Set category based on job type
            if (jobType.equals("Government")) {
                categoryView.setText("GOVERNMENT");
                tagView.setText("🏛 Govt Sector");
            } else {
                categoryView.setText("PRIVATE");
                tagView.setText("💼 " + salary);
            }

            // Click listener - navigate to job details
            jobView.setOnClickListener(v -> {
                Intent intent = new Intent(this, EducationLevelJobDetailsPage.class);
                intent.putExtra("job_id", jobId);
                intent.putExtra("education_level_id", educationLevelId);
                startActivity(intent);
            });

            jobsContainer.addView(jobView);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying job: " + e.getMessage());
        }
    }
}

