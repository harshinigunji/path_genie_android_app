package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * SystemGenerateP3Page - Step 3: Select Target Job
 */
public class SystemGenerateP3Page extends AppCompatActivity {

    private static final String TAG = "SystemGenerateP3";
    // Using centralized ApiConfig.getBaseUrl()

    private ImageView backButton;
    private Button nextButton;
    private ProgressBar loadingProgress;
    private ScrollView contentScroll;
    private LinearLayout jobsContainer;
    private TextView subtitleText;

    // Data from previous pages
    private int educationLevelId = -1;
    private String educationLevelName = "";
    private int streamId = -1;
    private String streamName = "";

    // Selected job
    private int selectedJobId = -1;
    private String selectedJobName = "";
    private String selectedJobSalary = "";
    private View currentSelectedCard = null;

    private TextView filterAll, filterGovt, filterPrivate;
    private String currentFilter = "ALL"; // ALL, GOVT, PRIVATE
    private JSONArray allJobs = new JSONArray(); // Store all fetched jobs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_generate_p3_page);

        // Get data from previous pages
        educationLevelId = getIntent().getIntExtra("education_level_id", -1);
        educationLevelName = getIntent().getStringExtra("education_level_name");
        streamId = getIntent().getIntExtra("stream_id", -1);
        streamName = getIntent().getStringExtra("stream_name");

        if (educationLevelName == null)
            educationLevelName = "";
        if (streamName == null)
            streamName = "";

        Log.d(TAG, "Received stream_id: " + streamId);
        Log.d(TAG, "Received stream_name: " + streamName);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        fetchJobs();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        contentScroll = findViewById(R.id.contentScroll);
        jobsContainer = findViewById(R.id.jobsContainer);
        subtitleText = findViewById(R.id.subtitleText);

        filterAll = findViewById(R.id.filterAll);
        filterGovt = findViewById(R.id.filterGovt);
        filterPrivate = findViewById(R.id.filterPrivate);

        subtitleText.setText("Based on your interest in " + streamName + ", here are the top recommended roles.");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        nextButton.setOnClickListener(v -> {
            if (selectedJobId != -1) {
                Intent intent = new Intent(this, SystemGenerateP4Page.class);
                intent.putExtra("education_level_id", educationLevelId);
                intent.putExtra("education_level_name", educationLevelName);
                intent.putExtra("stream_id", streamId);
                intent.putExtra("stream_name", streamName);
                intent.putExtra("job_id", selectedJobId);
                intent.putExtra("job_name", selectedJobName);
                intent.putExtra("job_salary", selectedJobSalary);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a target job", Toast.LENGTH_SHORT).show();
            }
        });

        // Filter Click Listeners
        filterAll.setOnClickListener(v -> updateFilter("ALL"));
        filterGovt.setOnClickListener(v -> updateFilter("GOVERNMENT"));
        filterPrivate.setOnClickListener(v -> updateFilter("PRIVATE"));
    }

    private void updateFilter(String filter) {
        currentFilter = filter;

        // Visual Update
        resetFilters();
        if (filter.equals("ALL")) {
            setSelected(filterAll);
        } else if (filter.equals("GOVERNMENT")) {
            setSelected(filterGovt);
        } else if (filter.equals("PRIVATE")) {
            setSelected(filterPrivate);
        }

        // Data Update
        populateJobs(allJobs);
    }

    private void resetFilters() {
        setUnselected(filterAll);
        setUnselected(filterGovt);
        setUnselected(filterPrivate);
    }

    private void setSelected(TextView view) {
        view.setTextColor(0xFFFFFFFF);
        view.setBackgroundResource(R.drawable.bg_toggle_selected);
    }

    private void setUnselected(TextView view) {
        view.setTextColor(0xFF6B7280);
        view.setBackground(null);
    }

    private void fetchJobs() {
        loadingProgress.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);

        // Use get_all_reachable_jobs.php to show ALL possible jobs from this stream
        String url = ApiConfig.getBaseUrl() + "get_all_reachable_jobs.php?stream_id=" + streamId;
        Log.d(TAG, "Fetching all reachable jobs from: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingProgress.setVisibility(View.GONE);
                    contentScroll.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Response: " + response.toString());

                    try {
                        boolean success = response.optBoolean("success", false);
                        if (success) {
                            allJobs = response.optJSONArray("data"); // Store globally
                            if (allJobs != null && allJobs.length() > 0) {
                                populateJobs(allJobs);
                            } else {
                                Toast.makeText(this, "No jobs found for this stream", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = response.optString("message", "Unknown error");
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "API error: " + message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Parse error: " + e.getMessage(), e);
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    contentScroll.setVisibility(View.VISIBLE);
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Network error";
                    Log.e(TAG, "Network error: " + errorMsg, error);
                    Toast.makeText(this, "Network error: " + errorMsg, Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void populateJobs(JSONArray jobs) {
        jobsContainer.removeAllViews();

        int[] icons = { R.drawable.ic_code, R.drawable.ic_building, R.drawable.ic_smart,
                R.drawable.ic_rocket, R.drawable.ic_microscope };

        try {
            boolean foundAny = false;
            for (int i = 0; i < jobs.length(); i++) {
                JSONObject job = jobs.getJSONObject(i);
                int jobId = job.optInt("job_id", 0);
                String jobName = job.optString("job_name", "Unknown");
                String description = job.optString("description", "");
                String salary = job.optString("average_salary", "");
                String jobType = job.optString("job_type", "PRIVATE").toUpperCase(); // Default to PRIVATE if missing

                // Filter Logic
                if (!currentFilter.equals("ALL")) {
                    if (!jobType.equals(currentFilter)) {
                        continue; // Skip if types don't match
                    }
                }

                foundAny = true;
                Log.d(TAG, "Adding job: " + jobId + " - " + jobName + " (" + jobType + ")");

                View card = createJobCard(jobId, jobName, description, salary, icons[i % icons.length]);
                jobsContainer.addView(card);
            }

            if (!foundAny) {
                TextView emptyText = new TextView(this);
                emptyText.setText(
                        "No " + (currentFilter.equals("ALL") ? "" : currentFilter.toLowerCase() + " ") + "jobs found.");
                emptyText.setTextSize(14);
                emptyText.setTextColor(0xFF6B7280);
                emptyText.setGravity(android.view.Gravity.CENTER);
                emptyText.setPadding(0, 32, 0, 0);
                jobsContainer.addView(emptyText);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error populating jobs: " + e.getMessage(), e);
            Toast.makeText(this, "Error displaying jobs", Toast.LENGTH_SHORT).show();
        }
    }

    private View createJobCard(int jobId, String name, String description, String salary, int iconRes) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundResource(R.drawable.bg_card_selectable);

        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        card.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = (int) (12 * getResources().getDisplayMetrics().density);
        card.setLayoutParams(params);

        // Icon
        ImageView icon = new ImageView(this);
        int iconSize = (int) (44 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconRes);
        icon.setBackgroundResource(R.drawable.bg_icon_circle_light);
        icon.setColorFilter(0xFF2563EB);
        int iconPadding = (int) (10 * getResources().getDisplayMetrics().density);
        icon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        card.addView(icon);

        // Text container
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,
                1);
        textParams.leftMargin = (int) (16 * getResources().getDisplayMetrics().density);
        textContainer.setLayoutParams(textParams);

        TextView nameText = new TextView(this);
        nameText.setText(name);
        nameText.setTextSize(16);
        nameText.setTextColor(0xFF111827);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);
        textContainer.addView(nameText);

        if (description != null && !description.isEmpty()) {
            TextView descText = new TextView(this);
            descText.setText(description);
            descText.setTextSize(13);
            descText.setTextColor(0xFF6B7280);
            descText.setMaxLines(3);
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            descParams.topMargin = (int) (4 * getResources().getDisplayMetrics().density);
            descText.setLayoutParams(descParams);
            textContainer.addView(descText);
        }

        card.addView(textContainer);

        // Checkmark for selected state
        ImageView checkmark = new ImageView(this);
        int checkSize = (int) (24 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(checkSize, checkSize);
        checkmark.setLayoutParams(checkParams);
        checkmark.setImageResource(R.drawable.ic_check);
        checkmark.setColorFilter(0xFF2563EB);
        checkmark.setVisibility(View.GONE);
        card.addView(checkmark);

        // Re-select if this card matches currently selected job (after filter refresh)
        if (jobId == selectedJobId) {
            card.setSelected(true);
            checkmark.setVisibility(View.VISIBLE);
            currentSelectedCard = card;
        }

        final ImageView finalCheckmark = checkmark;
        final String finalSalary = salary;
        card.setOnClickListener(v -> {
            // Deselect previous
            if (currentSelectedCard != null) {
                currentSelectedCard.setSelected(false);
                View prevCheck = ((LinearLayout) currentSelectedCard).getChildAt(2);
                if (prevCheck != null)
                    prevCheck.setVisibility(View.GONE);
            }

            // Select new
            card.setSelected(true);
            finalCheckmark.setVisibility(View.VISIBLE);
            currentSelectedCard = card;
            selectedJobId = jobId;
            selectedJobName = name;
            selectedJobSalary = finalSalary;

            Log.d(TAG, "Selected job: " + jobId + " - " + name);
        });

        return card;
    }
}
