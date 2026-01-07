package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * RecommendationLoadingPage - Loading screen while AI processes
 * recommendations.
 * Makes API call to ai_recommendations.php and navigates to results page.
 */
public class RecommendationLoadingPage extends AppCompatActivity {

    private static final String TAG = "RecommendationLoading";
    private static final String API_URL = "http://10.0.2.2/education_stream_advisor_api/ai_recommendations.php";

    private RequestQueue requestQueue;

    // Data from previous pages
    private int educationLevel;
    private String interestArea;
    private int careerPreferencePrivate;
    private int difficultyTolerance;
    private int riskTolerance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommendation_loading_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Volley
        requestQueue = Volley.newRequestQueue(this);

        // Get data from previous pages
        educationLevel = getIntent().getIntExtra("education_level", 1);
        interestArea = getIntent().getStringExtra("interest_area");
        careerPreferencePrivate = getIntent().getIntExtra("career_preference_private", 0);
        difficultyTolerance = getIntent().getIntExtra("difficulty_tolerance", 2);
        riskTolerance = getIntent().getIntExtra("risk_tolerance", 2);

        // Start API call
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        try {
            // Build JSON payload
            JSONObject payload = new JSONObject();
            payload.put("education_level", educationLevel);
            payload.put("interest_area", interestArea != null ? interestArea : "Technology");
            payload.put("career_preference_private", careerPreferencePrivate);
            payload.put("difficulty_tolerance", difficultyTolerance);
            payload.put("risk_tolerance", riskTolerance);

            Log.d(TAG, "Request payload: " + payload.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    API_URL,
                    payload,
                    response -> {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            if (response.optBoolean("status", false)) {
                                // Parse recommendations
                                JSONArray streams = response.optJSONArray("recommended_streams");
                                JSONArray exams = response.optJSONArray("recommended_exams");
                                JSONArray jobs = response.optJSONArray("recommended_jobs");

                                navigateToResults(
                                        streams != null ? streams.toString() : "[]",
                                        exams != null ? exams.toString() : "[]",
                                        jobs != null ? jobs.toString() : "[]");
                            } else {
                                String error = response.optString("error", "Unknown error");
                                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response", e);
                            Toast.makeText(this, "Error processing recommendations.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    },
                    error -> {
                        Log.e(TAG, "API call failed", error);
                        Toast.makeText(this, "Failed to get recommendations. Please try again.", Toast.LENGTH_LONG)
                                .show();
                        finish();
                    });

            // Set retry policy with longer timeout
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, // 60 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void navigateToResults(String streams, String exams, String jobs) {
        Intent intent = new Intent(this, CareerRecommendationPage.class);
        intent.putExtra("recommended_streams", streams);
        intent.putExtra("recommended_exams", exams);
        intent.putExtra("recommended_jobs", jobs);
        intent.putExtra("education_level", educationLevel);
        startActivity(intent);
        finish(); // Don't allow back to loading page
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
