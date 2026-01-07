package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.SIMATS.myapplication.utils.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ForumMyQuestionsPage - Shows questions posted by current user.
 */
public class ForumMyQuestionsPage extends AppCompatActivity {

    private static final String TAG = "ForumMyQuestions";
    // Using centralized ApiConfig.getBaseUrl()

    private ImageView backIcon;
    private LinearLayout questionsContainer;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_my_questions_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupClickListeners();
        loadMyQuestions();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        questionsContainer = findViewById(R.id.questionsContainer);
    }

    private void setupClickListeners() {
        backIcon.setOnClickListener(v -> finish());
    }

    private void loadMyQuestions() {
        questionsContainer.removeAllViews();

        int userId = sessionManager.getUserId();
        String url = ApiConfig.getBaseUrl() + "my_questions.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONArray questions = response.getJSONArray("questions");
                            for (int i = 0; i < questions.length(); i++) {
                                addQuestionCard(questions.getJSONObject(i));
                            }
                            if (questions.length() == 0) {
                                addNoDataMessage();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing questions", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error loading questions", error);
                    Toast.makeText(this, "Failed to load your questions", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void addQuestionCard(JSONObject question) {
        try {
            int questionId = question.getInt("question_id");
            String title = question.getString("title");
            String status = question.optString("status", "PENDING");
            int replies = question.optInt("replies", 0);
            String createdAt = question.optString("created_at", "");

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card);
            card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = dpToPx(12);
            card.setLayoutParams(cardParams);

            // Top row: Status + Time
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            // Status badge
            TextView badge = new TextView(this);
            boolean isAnswered = "ANSWERED".equalsIgnoreCase(status);
            badge.setText(isAnswered ? "● Answered" : "● Waiting for replies");
            badge.setTextSize(11);
            badge.setTextColor(isAnswered ? Color.parseColor("#16A34A") : Color.parseColor("#F59E0B"));
            badge.setBackgroundResource(isAnswered ? R.drawable.bg_status_answered : R.drawable.bg_status_waiting);
            badge.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));
            topRow.addView(badge);

            // Spacer
            TextView spacer = new TextView(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1));
            topRow.addView(spacer);

            // Time
            TextView txtTime = new TextView(this);
            txtTime.setText(getTimeAgo(createdAt));
            txtTime.setTextSize(12);
            txtTime.setTextColor(Color.parseColor("#64748B"));
            topRow.addView(txtTime);

            card.addView(topRow);

            // Title
            TextView txtTitle = new TextView(this);
            txtTitle.setText(title);
            txtTitle.setTextSize(16);
            txtTitle.setTypeface(null, Typeface.BOLD);
            txtTitle.setTextColor(Color.parseColor("#0F172A"));
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleParams.topMargin = dpToPx(12);
            txtTitle.setLayoutParams(titleParams);
            card.addView(txtTitle);

            // Bottom row: Replies + View button
            LinearLayout bottomRow = new LinearLayout(this);
            bottomRow.setOrientation(LinearLayout.HORIZONTAL);
            bottomRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bottomParams.topMargin = dpToPx(12);
            bottomRow.setLayoutParams(bottomParams);

            TextView txtReplies = new TextView(this);
            txtReplies.setText("💬 " + replies + " Replies");
            txtReplies.setTextSize(12);
            txtReplies.setTextColor(Color.parseColor("#64748B"));
            txtReplies.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            bottomRow.addView(txtReplies);

            TextView btnView = new TextView(this);
            btnView.setText("View →");
            btnView.setTextSize(14);
            btnView.setTextColor(Color.parseColor("#2563EB"));
            btnView.setTypeface(null, Typeface.BOLD);
            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(this, ForumMyQuestionAnswersPage.class);
                intent.putExtra("question_id", questionId);
                startActivity(intent);
            });
            bottomRow.addView(btnView);

            card.addView(bottomRow);

            questionsContainer.addView(card);

        } catch (Exception e) {
            Log.e(TAG, "Error creating question card", e);
        }
    }

    private String getTimeAgo(String dateStr) {
        // Simplified time ago - in production use proper date parsing
        if (dateStr == null || dateStr.isEmpty())
            return "";
        return "recently";
    }

    private void addNoDataMessage() {
        TextView txt = new TextView(this);
        txt.setText("You haven't asked any questions yet.");
        txt.setTextSize(14);
        txt.setTextColor(Color.parseColor("#94A3B8"));
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(dpToPx(16), dpToPx(48), dpToPx(16), dpToPx(48));
        questionsContainer.addView(txt);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}

