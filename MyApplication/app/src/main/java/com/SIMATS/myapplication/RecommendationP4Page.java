package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * RecommendationP4Page - AI Recommendation Results
 * Displays recommended streams, exams, and jobs based on user preferences.
 */
public class RecommendationP4Page extends AppCompatActivity {

    private static final String TAG = "RecommendationResults";
    // Using centralized ApiConfig.getBaseUrl()

    private ImageView btnBack;
    private Button btnHome;

    private LinearLayout streamsContainer;
    private LinearLayout examsContainer;
    private LinearLayout jobsContainer;

    private RequestQueue requestQueue;
    private int educationLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommendation_p4_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);

        initViews();
        setupClickListeners();

        // Get recommendations from intent
        educationLevel = getIntent().getIntExtra("education_level", 1);
        String streamsJson = getIntent().getStringExtra("recommended_streams");
        String examsJson = getIntent().getStringExtra("recommended_exams");
        String jobsJson = getIntent().getStringExtra("recommended_jobs");

        // Display recommendations
        displayStreams(streamsJson);
        displayExams(examsJson);
        displayJobs(jobsJson);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        streamsContainer = findViewById(R.id.streamsContainer);
        examsContainer = findViewById(R.id.examsContainer);
        jobsContainer = findViewById(R.id.jobsContainer);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnHome.setOnClickListener(v -> {
            // Navigate back to home, clearing the recommendation flow
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayStreams(String jsonStr) {
        try {
            JSONArray array = new JSONArray(jsonStr);
            if (array.length() == 0) {
                addNoDataCard(streamsContainer, "No streams available");
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int streamId = obj.getInt("stream_id");
                int score = obj.getInt("score");
                fetchStreamDetails(streamId, score);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing streams", e);
            addNoDataCard(streamsContainer, "No streams available");
        }
    }

    private void displayExams(String jsonStr) {
        try {
            JSONArray array = new JSONArray(jsonStr);
            if (array.length() == 0) {
                addNoDataCard(examsContainer, "No exams available");
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int examId = obj.getInt("exam_id");
                int score = obj.getInt("score");
                fetchExamDetails(examId, score);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing exams", e);
            addNoDataCard(examsContainer, "No exams available");
        }
    }

    private void displayJobs(String jsonStr) {
        try {
            JSONArray array = new JSONArray(jsonStr);
            if (array.length() == 0) {
                addNoDataCard(jobsContainer, "No jobs available");
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int jobId = obj.getInt("job_id");
                int score = obj.getInt("score");
                fetchJobDetails(jobId, score);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing jobs", e);
            addNoDataCard(jobsContainer, "No jobs available");
        }
    }

    private void fetchStreamDetails(int streamId, int score) {
        String url = ApiConfig.getBaseUrl() + "stream_details.php?stream_id=" + streamId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONObject data = response.getJSONObject("data");
                            String name = data.getString("stream_name");
                            String subjects = data.optString("subjects", "");
                            String description = data.optString("description", "");
                            addStreamCard(name, subjects, score, description);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing stream details", e);
                    }
                },
                error -> Log.e(TAG, "Failed to fetch stream " + streamId, error));

        requestQueue.add(request);
    }

    private void fetchExamDetails(int examId, int score) {
        String url = ApiConfig.getBaseUrl() + "exam_details.php?exam_id=" + examId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONObject data = response.getJSONObject("data");
                            String name = data.getString("exam_name");
                            String description = data.optString("description", "");
                            addExamCard(name, score, description);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing exam details", e);
                    }
                },
                error -> Log.e(TAG, "Failed to fetch exam " + examId, error));

        requestQueue.add(request);
    }

    private void fetchJobDetails(int jobId, int score) {
        String url = ApiConfig.getBaseUrl() + "job_details.php?job_id=" + jobId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONObject data = response.getJSONObject("data");
                            String name = data.getString("job_title");
                            String type = data.optString("job_type", "Private");
                            String description = data.optString("description", "");
                            addJobCard(name, type, score, description);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing job details", e);
                    }
                },
                error -> Log.e(TAG, "Failed to fetch job " + jobId, error));

        requestQueue.add(request);
    }

    private void addStreamCard(String name, String subjects, int score, String description) {
        LinearLayout card = createRecommendationCard();

        // Title and Score row
        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView txtName = new TextView(this);
        txtName.setText(name);
        txtName.setTextSize(16);
        txtName.setTypeface(null, Typeface.BOLD);
        txtName.setTextColor(getResources().getColor(android.R.color.black));
        txtName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView txtScore = new TextView(this);
        txtScore.setText(score + "%");
        txtScore.setTextSize(16);
        txtScore.setTypeface(null, Typeface.BOLD);
        txtScore.setTextColor(score >= 90 ? 0xFF2563EB : 0xFF64748B);

        titleRow.addView(txtName);
        titleRow.addView(txtScore);
        card.addView(titleRow);

        // Subjects
        if (subjects != null && !subjects.isEmpty()) {
            TextView txtSubjects = new TextView(this);
            txtSubjects.setText(subjects);
            txtSubjects.setTextSize(12);
            txtSubjects.setTextColor(0xFF64748B);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = 8;
            txtSubjects.setLayoutParams(params);
            card.addView(txtSubjects);
        }

        // Description
        if (description != null && !description.isEmpty()) {
            TextView txtDesc = new TextView(this);
            txtDesc.setText(description);
            txtDesc.setTextSize(13);
            txtDesc.setTextColor(0xFF334155);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = 16;
            txtDesc.setLayoutParams(params);
            card.addView(txtDesc);
        }

        streamsContainer.addView(card);
    }

    private void addExamCard(String name, int score, String description) {
        LinearLayout card = createRecommendationCard();

        TextView txtName = new TextView(this);
        txtName.setText(name);
        txtName.setTextSize(16);
        txtName.setTypeface(null, Typeface.BOLD);
        txtName.setTextColor(getResources().getColor(android.R.color.black));
        card.addView(txtName);

        TextView txtScore = new TextView(this);
        txtScore.setText(score + "% MATCH");
        txtScore.setTextSize(12);
        txtScore.setTextColor(score >= 90 ? 0xFF16A34A : 0xFF2563EB);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8;
        txtScore.setLayoutParams(params);
        card.addView(txtScore);

        if (description != null && !description.isEmpty()) {
            TextView txtDesc = new TextView(this);
            txtDesc.setText(description);
            txtDesc.setTextSize(13);
            txtDesc.setTextColor(0xFF334155);
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            descParams.topMargin = 16;
            txtDesc.setLayoutParams(descParams);
            card.addView(txtDesc);
        }

        examsContainer.addView(card);
    }

    private void addJobCard(String name, String type, int score, String description) {
        LinearLayout card = createRecommendationCard();

        TextView txtName = new TextView(this);
        txtName.setText(name);
        txtName.setTextSize(16);
        txtName.setTypeface(null, Typeface.BOLD);
        txtName.setTextColor(getResources().getColor(android.R.color.black));
        card.addView(txtName);

        TextView txtType = new TextView(this);
        txtType.setText(type.toUpperCase());
        txtType.setTextSize(11);
        txtType.setTextColor("private".equalsIgnoreCase(type) ? 0xFF7C3AED : 0xFFF59E0B);
        LinearLayout.LayoutParams typeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        typeParams.topMargin = 8;
        txtType.setLayoutParams(typeParams);
        card.addView(txtType);

        TextView txtScore = new TextView(this);
        txtScore.setText(score + "%");
        txtScore.setTextSize(14);
        txtScore.setTypeface(null, Typeface.BOLD);
        txtScore.setTextColor(0xFF2563EB);
        LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        scoreParams.topMargin = 8;
        txtScore.setLayoutParams(scoreParams);
        card.addView(txtScore);

        if (description != null && !description.isEmpty()) {
            TextView txtDesc = new TextView(this);
            txtDesc.setText(description);
            txtDesc.setTextSize(13);
            txtDesc.setTextColor(0xFF334155);
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            descParams.topMargin = 16;
            txtDesc.setLayoutParams(descParams);
            card.addView(txtDesc);
        }

        jobsContainer.addView(card);
    }

    private LinearLayout createRecommendationCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(32, 32, 32, 32);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 24;
        card.setLayoutParams(params);

        return card;
    }

    private void addNoDataCard(LinearLayout container, String message) {
        TextView txt = new TextView(this);
        txt.setText(message);
        txt.setTextSize(14);
        txt.setTextColor(0xFF64748B);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(32, 32, 32, 32);
        container.addView(txt);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}

