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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JobsPage Activity - Displays job opportunities for a stream.
 * Separates jobs into Government and Private categories with toggle buttons.
 */
public class JobsPage extends AppCompatActivity {

    private static final String TAG = "JobsPage";
    // Using centralized ApiConfig.getBaseUrl()

    // UI Components
    private ImageView backButton;
    private TextView titleText;
    private TextView qualificationChip;
    private TextView subtitleText;
    private ProgressBar progressBar;
    private TextView noJobsText;
    private ScrollView contentScroll;
    private LinearLayout jobsContainer;
    private TextView btnGovernment;
    private TextView btnPrivate;

    // Data
    private int streamId = -1;
    private String streamName = "";
    private List<JSONObject> allJobs = new ArrayList<>();
    private String currentFilter = "Government"; // Default filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jobs_page);

        // Get data from intent
        streamId = getIntent().getIntExtra("stream_id", -1);
        streamName = getIntent().getStringExtra("stream_name");

        if (streamId == -1) {
            Toast.makeText(this, "Invalid stream", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (streamName == null)
            streamName = "Stream";

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

        // Update title with stream name
        titleText.setText("Jobs in " + getShortStreamName(streamName));
        qualificationChip.setText("🎓 STREAM: " + getShortStreamName(streamName).toUpperCase());

        // Fetch jobs from API
        fetchJobs();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        qualificationChip = findViewById(R.id.qualificationChip);
        subtitleText = findViewById(R.id.subtitleText);
        progressBar = findViewById(R.id.progressBar);
        noJobsText = findViewById(R.id.noJobsText);
        contentScroll = findViewById(R.id.contentScroll);
        jobsContainer = findViewById(R.id.jobsContainer);
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
     * Fetch jobs from API.
     */
    private void fetchJobs() {
        progressBar.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);
        noJobsText.setVisibility(View.GONE);

        String url = ApiConfig.getBaseUrl() + "jobs.php?stream_id=" + streamId;

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
                                contentScroll.setVisibility(View.VISIBLE);
                                filterAndDisplayJobs();
                            }
                        } else {
                            String message = response.optString("message", "Failed to fetch jobs");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            noJobsText.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        noJobsText.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching jobs: " + error.toString());
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                    noJobsText.setVisibility(View.VISIBLE);
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
                // Normalize job type
                boolean isGovernment = jobType.toLowerCase().contains("government")
                        || jobType.toLowerCase().contains("govt")
                        || jobType.toLowerCase().contains("public");

                if ((currentFilter.equals("Government") && isGovernment) ||
                        (currentFilter.equals("Private") && !isGovernment)) {
                    hasJobs = true;
                    displayJobCard(inflater, job, isGovernment);
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
    private void displayJobCard(LayoutInflater inflater, JSONObject job, boolean isGovernment) {
        try {
            int jobId = job.getInt("job_id");
            String jobName = job.getString("job_name");
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
            descView.setText(truncateText(description, 100));

            // Set category based on job type
            if (isGovernment) {
                categoryView.setText(getCategoryLabel(jobName));
                tagView.setText("🏛 " + getJobTag(jobName, "government"));
            } else {
                categoryView.setText(getCategoryLabel(jobName));
                tagView.setText("💼 " + (salary.isEmpty() ? "Private Sector" : salary));
            }

            // Click listener - navigate to job details
            jobView.setOnClickListener(v -> {
                Intent intent = new Intent(this, JobDetailsPage.class);
                intent.putExtra("job_id", jobId);
                intent.putExtra("stream_id", streamId);
                intent.putExtra("stream_name", streamName);
                startActivity(intent);
            });

            jobsContainer.addView(jobView);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying job: " + e.getMessage());
        }
    }

    /**
     * Get category label for job based on name.
     */
    private String getCategoryLabel(String jobName) {
        String lower = jobName.toLowerCase();
        if (lower.contains("ies") || lower.contains("upsc")) {
            return "UNION PUBLIC SERVICE COMMISSION";
        } else if (lower.contains("gate") || lower.contains("psu")) {
            return "PUBLIC SECTOR UNDERTAKING";
        } else if (lower.contains("rrb") || lower.contains("railway")) {
            return "INDIAN RAILWAYS";
        } else if (lower.contains("software") || lower.contains("developer")) {
            return "IT & SOFTWARE";
        } else if (lower.contains("design")) {
            return "CORE ENGINEERING";
        } else if (lower.contains("trainee") || lower.contains("management")) {
            return "MANAGEMENT";
        }
        return "CAREER OPPORTUNITY";
    }

    /**
     * Get tag for job based on type.
     */
    private String getJobTag(String jobName, String jobType) {
        String lower = jobName.toLowerCase();
        if (lower.contains("central") || lower.contains("ies")) {
            return "Central Govt";
        } else if (lower.contains("psu") || lower.contains("gate")) {
            return "PSU Jobs";
        } else if (lower.contains("railway")) {
            return "Railways";
        } else if (lower.contains("software") || lower.contains("tech")) {
            return "Tech Sector";
        } else if (lower.contains("manufacturing") || lower.contains("design")) {
            return "Manufacturing";
        } else if (lower.contains("corporate") || lower.contains("trainee")) {
            return "Corporate";
        }
        return jobType.contains("govt") ? "Government" : "Private";
    }

    /**
     * Get short name from stream name.
     */
    private String getShortStreamName(String name) {
        if (name.contains("(") && name.contains(")")) {
            int start = name.indexOf("(");
            int end = name.indexOf(")");
            return name.substring(start + 1, end);
        }
        String[] words = name.split(" ");
        if (words.length >= 2) {
            return words[0] + " " + words[1];
        }
        return name;
    }

    /**
     * Truncate text to specified length.
     */
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength) + "...";
    }
}

