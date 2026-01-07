package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CareerRecommendationPage - Premium AI Recommendation Results
 * Refactored for cleaner UI using item_recommendation_card.xml
 */
public class CareerRecommendationPage extends AppCompatActivity {

    private static final String TAG = "CareerRecommendation";

    // Colors
    private static final int COLOR_BLUE = 0xFF2563EB;
    private static final int COLOR_GREEN = 0xFF10B981;
    private static final int COLOR_PURPLE = 0xFF8B5CF6;
    private static final int COLOR_ORANGE = 0xFFF59E0B;

    private ImageView backIcon;
    private Button btnBackHome;
    private LinearLayout streamsContainer;
    private LinearLayout examsContainer;
    private LinearLayout jobsContainer;

    private RequestQueue requestQueue;

    private List<RecommendationItem> streamItems = new ArrayList<>();
    private List<RecommendationItem> examItems = new ArrayList<>();
    private List<RecommendationItem> jobItems = new ArrayList<>();

    private int pendingStreams = 0;
    private int pendingExams = 0;
    private int pendingJobs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_career_recommendation_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);

        initViews();
        setupClickListeners();

        String streamsJson = getIntent().getStringExtra("recommended_streams");
        String examsJson = getIntent().getStringExtra("recommended_exams");
        String jobsJson = getIntent().getStringExtra("recommended_jobs");

        loadStreams(streamsJson);
        loadExams(examsJson);
        loadJobs(jobsJson);
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        btnBackHome = findViewById(R.id.btnBackHome);
        streamsContainer = findViewById(R.id.streamsContainer);
        examsContainer = findViewById(R.id.examsContainer);
        jobsContainer = findViewById(R.id.jobsContainer);
    }

    private void setupClickListeners() {
        backIcon.setOnClickListener(v -> finish());
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // ==================== STREAMS ====================
    private void loadStreams(String jsonStr) {
        try {
            if (jsonStr == null || jsonStr.isEmpty()) {
                addNoDataMessage(streamsContainer, "No streams recommended");
                return;
            }
            JSONArray array = new JSONArray(jsonStr);
            if (array.length() == 0) {
                addNoDataMessage(streamsContainer, "No streams recommended");
                return;
            }

            List<JSONObject> sortedList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                sortedList.add(array.getJSONObject(i));
            }
            Collections.sort(sortedList, (a, b) -> b.optInt("score", 0) - a.optInt("score", 0));

            pendingStreams = sortedList.size();
            for (JSONObject obj : sortedList) {
                int streamId = obj.getInt("stream_id");
                int score = obj.getInt("score");
                fetchStreamDetails(streamId, score);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing streams", e);
            addNoDataMessage(streamsContainer, "No streams recommended");
        }
    }

    private void fetchStreamDetails(int streamId, int score) {
        String url = ApiConfig.getBaseUrl() + "stream_details.php?stream_id=" + streamId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONObject data = response.getJSONObject("data");
                            String name = data.optString("stream_name", "Unknown");
                            String subjects = data.optString("subjects", "");
                            streamItems.add(new RecommendationItem(name, subjects, null, score));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing stream", e);
                    } finally {
                        pendingStreams--;
                        if (pendingStreams <= 0)
                            displayStreams();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching stream", error);
                    pendingStreams--;
                    if (pendingStreams <= 0)
                        displayStreams();
                });
        requestQueue.add(request);
    }

    private void displayStreams() {
        Collections.sort(streamItems, (a, b) -> b.score - a.score);
        for (int i = 0; i < streamItems.size(); i++) {
            RecommendationItem item = streamItems.get(i);
            addCard(streamsContainer, item.name, item.subtitle, null, item.score, R.drawable.ic_graduation, COLOR_BLUE,
                    i == 0);
        }
        if (streamItems.isEmpty()) {
            addNoDataMessage(streamsContainer, "No streams recommended");
        }
    }

    // ==================== EXAMS ====================
    private void loadExams(String jsonStr) {
        try {
            if (jsonStr == null || jsonStr.isEmpty()) {
                addNoDataMessage(examsContainer, "No exams recommended");
                return;
            }
            JSONArray array = new JSONArray(jsonStr);
            if (array.length() == 0) {
                addNoDataMessage(examsContainer, "No exams recommended");
                return;
            }

            List<JSONObject> sortedList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                sortedList.add(array.getJSONObject(i));
            }
            Collections.sort(sortedList, (a, b) -> b.optInt("score", 0) - a.optInt("score", 0));

            pendingExams = sortedList.size();
            for (JSONObject obj : sortedList) {
                int examId = obj.getInt("exam_id");
                int score = obj.getInt("score");
                fetchExamDetails(examId, score);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing exams", e);
            addNoDataMessage(examsContainer, "No exams recommended");
        }
    }

    private void fetchExamDetails(int examId, int score) {
        String url = ApiConfig.getBaseUrl() + "exam_details.php?exam_id=" + examId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONObject data = response.getJSONObject("data");
                            String name = data.optString("exam_name", "Unknown");
                            examItems.add(new RecommendationItem(name, null, null, score));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing exam", e);
                    } finally {
                        pendingExams--;
                        if (pendingExams <= 0)
                            displayExams();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching exam", error);
                    pendingExams--;
                    if (pendingExams <= 0)
                        displayExams();
                });
        requestQueue.add(request);
    }

    private void displayExams() {
        Collections.sort(examItems, (a, b) -> b.score - a.score);
        for (int i = 0; i < examItems.size(); i++) {
            RecommendationItem item = examItems.get(i);
            addCard(examsContainer, item.name, null, null, item.score, R.drawable.ic_diploma, COLOR_GREEN, i == 0);
        }
        if (examItems.isEmpty()) {
            addNoDataMessage(examsContainer, "No exams recommended");
        }
    }

    // ==================== JOBS ====================
    private void loadJobs(String jsonStr) {
        try {
            if (jsonStr == null || jsonStr.isEmpty()) {
                addNoDataMessage(jobsContainer, "No jobs recommended");
                return;
            }
            JSONArray array = new JSONArray(jsonStr);
            if (array.length() == 0) {
                addNoDataMessage(jobsContainer, "No jobs recommended");
                return;
            }

            List<JSONObject> sortedList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                sortedList.add(array.getJSONObject(i));
            }
            Collections.sort(sortedList, (a, b) -> b.optInt("score", 0) - a.optInt("score", 0));

            pendingJobs = sortedList.size();
            for (JSONObject obj : sortedList) {
                int jobId = obj.getInt("job_id");
                int score = obj.getInt("score");
                fetchJobDetails(jobId, score);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing jobs", e);
            addNoDataMessage(jobsContainer, "No jobs recommended");
        }
    }

    private void fetchJobDetails(int jobId, int score) {
        String url = ApiConfig.getBaseUrl() + "job_details.php?job_id=" + jobId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONObject data = response.getJSONObject("data");
                            String name = data.optString("job_name", "Unknown");
                            String type = data.optString("job_type", "Private");
                            jobItems.add(new RecommendationItem(name, null, type, score));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing job", e);
                    } finally {
                        pendingJobs--;
                        if (pendingJobs <= 0)
                            displayJobs();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching job", error);
                    pendingJobs--;
                    if (pendingJobs <= 0)
                        displayJobs();
                });
        requestQueue.add(request);
    }

    private void displayJobs() {
        Collections.sort(jobItems, (a, b) -> b.score - a.score);
        for (int i = 0; i < jobItems.size(); i++) {
            RecommendationItem item = jobItems.get(i);
            addCard(jobsContainer, item.name, null, item.type, item.score, R.drawable.ic_briefcase, COLOR_PURPLE,
                    i == 0);
        }
        if (jobItems.isEmpty()) {
            addNoDataMessage(jobsContainer, "No jobs recommended");
        }
    }

    // ==================== UNIFIED CARD UI ====================
    private void addCard(LinearLayout container, String name, String subtitle, String chipText, int score,
            int iconRes, int accentColor, boolean isTop) {

        View cardView = LayoutInflater.from(this).inflate(R.layout.item_recommendation_card, container, false);

        // References
        View colorStrip = cardView.findViewById(R.id.colorStrip);
        ImageView itemIcon = cardView.findViewById(R.id.itemIcon);
        TextView topMatchBadge = cardView.findViewById(R.id.topMatchBadge);
        TextView itemTitle = cardView.findViewById(R.id.itemTitle);
        TextView itemSubtitle = cardView.findViewById(R.id.itemSubtitle);
        TextView itemChip = cardView.findViewById(R.id.itemChip);
        TextView scoreText = cardView.findViewById(R.id.scoreText);

        // Apply Data
        itemTitle.setText(name);

        // Subtitle
        if (subtitle != null && !subtitle.isEmpty()) {
            itemSubtitle.setText(subtitle);
            itemSubtitle.setVisibility(View.VISIBLE);
        } else {
            itemSubtitle.setVisibility(View.GONE);
        }

        // Chip (for Jobs)
        if (chipText != null && !chipText.isEmpty()) {
            boolean isGovt = chipText.equalsIgnoreCase("govt") || chipText.equalsIgnoreCase("government");
            itemChip.setText(isGovt ? "GOVT" : "PRIVATE");
            itemChip.setTextColor(isGovt ? COLOR_ORANGE : COLOR_PURPLE);
            itemChip.setBackgroundResource(isGovt ? R.drawable.bg_chip_govt : R.drawable.bg_chip_private);
            itemChip.setVisibility(View.VISIBLE);
        } else {
            itemChip.setVisibility(View.GONE); // Explicitly hide if null
        }

        // Icon & Colors
        itemIcon.setImageResource(iconRes);
        itemIcon.setColorFilter(accentColor);
        colorStrip.setBackgroundColor(accentColor);
        scoreText.setText(score + "%");
        scoreText.setTextColor(accentColor);
        topMatchBadge.setTextColor(accentColor);

        // Badge Visibility
        topMatchBadge.setVisibility(isTop ? View.VISIBLE : View.GONE);

        container.addView(cardView);
    }

    private void addNoDataMessage(LinearLayout container, String message) {
        TextView txt = new TextView(this);
        txt.setText(message);
        txt.setTextSize(13);
        txt.setTextColor(Color.GRAY);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(0, 40, 0, 40);
        container.addView(txt);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    private static class RecommendationItem {
        String name, subtitle, type;
        int score;

        RecommendationItem(String name, String subtitle, String type, int score) {
            this.name = name;
            this.subtitle = subtitle;
            this.type = type;
            this.score = score;
        }
    }
}
