package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.SIMATS.myapplication.network.VolleySingleton;
import com.SIMATS.myapplication.utils.RoadmapSessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import com.SIMATS.myapplication.utils.SessionManager;

/**
 * UserGeneratePage Activity - Shows loading animation while creating roadmap.
 * Creates roadmap via API then navigates to UserRoadmapPage.
 */
public class UserGeneratePage extends AppCompatActivity {

    private static final String TAG = "UserGeneratePage";
    // Using centralized ApiConfig.getBaseUrl()

    private RoadmapSessionManager sessionManager;
    private SessionManager userSessionManager;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_generate_page);

        // Get user ID from SessionManager
        userSessionManager = new SessionManager(this);
        userId = userSessionManager.getUserId();

        if (userId == -1 || !userSessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new RoadmapSessionManager(this);

        // Delay to show loading animation, then create roadmap
        new Handler(Looper.getMainLooper()).postDelayed(this::createRoadmap, 2500);
    }

    private void createRoadmap() {
        JSONArray steps = sessionManager.getStepsArray();
        JSONObject targetJob = sessionManager.getTargetJob();

        if (steps.length() == 0) {
            Toast.makeText(this, "No steps recorded. Please start from education level.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            // Build request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("user_id", userId);
            requestBody.put("title", buildRoadmapTitle(steps));

            if (targetJob != null) {
                requestBody.put("target_job_name", targetJob.optString("job_name", ""));
                requestBody.put("target_salary", targetJob.optString("salary", ""));
            }

            requestBody.put("steps", steps);

            String url = ApiConfig.getBaseUrl() + "save_user_roadmap.php";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    requestBody,
                    response -> {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                int roadmapId = response.getJSONObject("data").getInt("roadmap_id");

                                // Navigate to roadmap page
                                Intent intent = new Intent(this, UserRoadmapPage.class);
                                intent.putExtra("roadmap_id", roadmapId);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = response.optString("message", "Failed to create roadmap");
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage());
                            Toast.makeText(this, "Error creating roadmap", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Error creating roadmap: " + error.toString());
                        Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                        finish();
                    });

            VolleySingleton.getInstance(this).addToRequestQueue(request);

        } catch (Exception e) {
            Log.e(TAG, "Error building request: " + e.getMessage());
            Toast.makeText(this, "Error creating roadmap", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String buildRoadmapTitle(JSONArray steps) {
        try {
            String firstStream = "";
            String lastItem = "";

            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                String type = step.getString("step_type");
                String title = step.getString("title");

                if ("STREAM".equals(type) && firstStream.isEmpty()) {
                    firstStream = title;
                }
                if ("JOB".equals(type)) {
                    lastItem = title;
                }
            }

            if (!firstStream.isEmpty() && !lastItem.isEmpty()) {
                return firstStream + " to " + lastItem;
            } else if (!firstStream.isEmpty()) {
                return firstStream + " Journey";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error building title: " + e.getMessage());
        }
        return "My Career Roadmap";
    }
}

