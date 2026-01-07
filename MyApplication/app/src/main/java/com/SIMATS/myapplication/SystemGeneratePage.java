package com.SIMATS.myapplication;

import android.animation.ObjectAnimator;
import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
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

import org.json.JSONObject;

/**
 * SystemGeneratePage - Loading animation while generating the roadmap
 * Calls generate_system_roadmap.php with start_stream_id and target_job_id
 */
public class SystemGeneratePage extends AppCompatActivity {

    private static final String TAG = "SystemGeneratePage";
    // Using centralized ApiConfig.getBaseUrl()

    private ProgressBar progressBar;
    private TextView statusText;
    private Handler handler;

    // Data from P4
    private int educationLevelId;
    private String educationLevelName;
    private int streamId; // This becomes start_stream_id
    private String streamName;
    private int jobId; // This becomes target_job_id
    private String jobName;
    private String jobSalary;

    private SessionManager sessionManager;
    private String[] statusMessages = { "ANALYZING", "STRUCTURING", "FINALIZING" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_generate_page);

        // Get data from P4
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

        Log.d(TAG, "Received: stream_id=" + streamId + ", job_id=" + jobId);

        sessionManager = new SessionManager(this);
        handler = new Handler(Looper.getMainLooper());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        startAnimation();
        generateRoadmap();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);
    }

    private void startAnimation() {
        // Animate progress bar
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        animator.setDuration(4000);
        animator.start();

        // Cycle through status messages
        handler.postDelayed(() -> updateStatus(1), 1500);
        handler.postDelayed(() -> updateStatus(2), 3000);
    }

    private void updateStatus(int index) {
        if (index < statusMessages.length) {
            statusText.setText(statusMessages[index]);
        }
    }

    private void generateRoadmap() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            // Use generate_system_roadmap.php with the correct parameters
            JSONObject requestBody = new JSONObject();
            requestBody.put("user_id", userId);
            requestBody.put("start_stream_id", streamId); // From P2
            requestBody.put("target_job_id", jobId); // From P3

            Log.d(TAG, "Request to generate_system_roadmap.php: " + requestBody.toString());

            String url = ApiConfig.getBaseUrl() + "generate_system_roadmap.php";

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    response -> {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            // generate_system_roadmap.php uses "status" not "success"
                            boolean success = response.optBoolean("status", false);
                            if (success) {
                                // Get roadmap_id from data object
                                JSONObject data = response.optJSONObject("data");
                                int roadmapId = 0;
                                if (data != null) {
                                    roadmapId = data.optInt("roadmap_id", 0);
                                }
                                Log.d(TAG, "Roadmap created with ID: " + roadmapId);

                                // Navigate to SystemRoadmapPage after delay
                                final int finalRoadmapId = roadmapId;
                                handler.postDelayed(() -> navigateToRoadmap(finalRoadmapId), 1500);
                            } else {
                                String message = response.optString("message", "Failed to generate roadmap");
                                Log.e(TAG, "API Error: " + message);

                                // Launch Error Activity instead of Toast
                                Intent errorIntent = new Intent(SystemGeneratePage.this, RoadmapErrorActivity.class);
                                errorIntent.putExtra("error_message", message);
                                // Pass context data back
                                errorIntent.putExtra("education_level_id", educationLevelId);
                                errorIntent.putExtra("education_level_name", educationLevelName);
                                errorIntent.putExtra("stream_id", streamId);
                                errorIntent.putExtra("stream_name", streamName);
                                startActivity(errorIntent);
                                finish();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Parse Error: " + e.getMessage(), e);
                            Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    },
                    error -> {
                        String errorMsg = error.getMessage() != null ? error.getMessage() : "Network error";
                        Log.e(TAG, "Network error: " + errorMsg, error);
                        Toast.makeText(this, "Network error: " + errorMsg, Toast.LENGTH_LONG).show();
                        finish();
                    });

            queue.add(request);

        } catch (Exception e) {
            Log.e(TAG, "Error creating request: " + e.getMessage(), e);
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void navigateToRoadmap(int roadmapId) {
        Intent intent = new Intent(this, SystemRoadmapPage.class);
        intent.putExtra("roadmap_id", roadmapId);
        intent.putExtra("job_name", jobName);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
