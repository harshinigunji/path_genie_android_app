package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
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
 * ForumHomePage - Community Forum main screen with tabs and likes.
 */
public class ForumHomePage extends AppCompatActivity {

    private static final String TAG = "ForumHomePage";
    // Using centralized ApiConfig.getBaseUrl()

    private ImageView backIcon;
    private TextView tabAll, tabMyQuestions, tabAfter10th, tabAfter12th, tabDiploma, tabUG, tabPG;
    private LinearLayout questionsContainer;
    private LinearLayout fabAskQuestion;
    private FrameLayout notificationBell;
    private TextView notificationBadge;
    private LinearLayout navHome, navCommunity, navSaved, navProfile;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    private int currentTab = 0;
    private TextView[] allTabs;

    // Education level IDs
    private static final int LEVEL_ALL = 0;
    private static final int LEVEL_AFTER_10TH = 1;
    private static final int LEVEL_AFTER_12TH = 2;
    private static final int LEVEL_DIPLOMA = 3;
    private static final int LEVEL_UG = 4;
    private static final int LEVEL_PG = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_home_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupClickListeners();
        loadQuestions(LEVEL_ALL);
        loadUnreadCount();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        tabAll = findViewById(R.id.tabAll);
        tabMyQuestions = findViewById(R.id.tabMyQuestions);
        tabAfter10th = findViewById(R.id.tabAfter10th);
        tabAfter12th = findViewById(R.id.tabAfter12th);
        tabDiploma = findViewById(R.id.tabDiploma);
        tabUG = findViewById(R.id.tabUG);
        tabPG = findViewById(R.id.tabPG);
        questionsContainer = findViewById(R.id.questionsContainer);
        fabAskQuestion = findViewById(R.id.fabAskQuestion);
        notificationBell = findViewById(R.id.notificationBell);
        notificationBadge = findViewById(R.id.notificationBadge);

        navHome = findViewById(R.id.navHome);
        navCommunity = findViewById(R.id.navCommunity);
        navSaved = findViewById(R.id.navSaved);
        navProfile = findViewById(R.id.navProfile);

        allTabs = new TextView[] { tabAll, tabMyQuestions, tabAfter10th, tabAfter12th, tabDiploma, tabUG, tabPG };
    }

    private void setupClickListeners() {
        backIcon.setOnClickListener(v -> finish());

        tabAll.setOnClickListener(v -> {
            setActiveTab(0);
            loadQuestions(LEVEL_ALL);
        });
        tabMyQuestions.setOnClickListener(v -> {
            setActiveTab(1);
            startActivity(new Intent(this, ForumMyQuestionsPage.class));
        });
        tabAfter10th.setOnClickListener(v -> {
            setActiveTab(2);
            loadQuestions(LEVEL_AFTER_10TH);
        });
        tabAfter12th.setOnClickListener(v -> {
            setActiveTab(3);
            loadQuestions(LEVEL_AFTER_12TH);
        });
        tabDiploma.setOnClickListener(v -> {
            setActiveTab(4);
            loadQuestions(LEVEL_DIPLOMA);
        });
        tabUG.setOnClickListener(v -> {
            setActiveTab(5);
            loadQuestions(LEVEL_UG);
        });
        tabPG.setOnClickListener(v -> {
            setActiveTab(6);
            loadQuestions(LEVEL_PG);
        });

        fabAskQuestion.setOnClickListener(v -> {
            startActivity(new Intent(this, ForumAskQuestionPage.class));
        });

        notificationBell.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsPage.class));
        });

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePage.class));
            finish();
        });
        navSaved.setOnClickListener(v -> {
            startActivity(new Intent(this, SavedRoadmapListPage.class));
            finish();
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfilePage.class));
            finish();
        });
    }

    private void setActiveTab(int index) {
        currentTab = index;
        for (int i = 0; i < allTabs.length; i++) {
            if (i == index) {
                allTabs[i].setBackgroundResource(R.drawable.bg_primary_button);
                allTabs[i].setTextColor(Color.WHITE);
            } else {
                allTabs[i].setBackgroundResource(R.drawable.bg_tab_unselected);
                allTabs[i].setTextColor(Color.parseColor("#374151"));
            }
        }
    }

    private void loadQuestions(int educationLevelId) {
        questionsContainer.removeAllViews();

        int userId = sessionManager.getUserId();
        String url = ApiConfig.getBaseUrl() + "get_questions.php?user_id=" + userId;
        if (educationLevelId > 0) {
            url += "&education_level_id=" + educationLevelId;
        }

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
                    Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void loadUnreadCount() {
        int userId = sessionManager.getUserId();
        String url = ApiConfig.getBaseUrl() + "get_unread_count.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    int count = response.optInt("unread_count", 0);
                    if (count > 0) {
                        notificationBadge.setText(count > 9 ? "9+" : String.valueOf(count));
                        notificationBadge.setVisibility(View.VISIBLE);
                    } else {
                        notificationBadge.setVisibility(View.GONE);
                    }
                },
                error -> Log.e(TAG, "Error loading unread count", error));

        requestQueue.add(request);
    }

    private void addQuestionCard(JSONObject question) {
        try {
            int questionId = question.getInt("question_id");
            String title = question.getString("title");
            String description = question.optString("description", "");
            String authorName = question.optString("author_name", "Anonymous");
            int replies = question.optInt("replies", 0);
            int likesCount = question.optInt("likes_count", 0);
            boolean userLiked = question.optBoolean("user_liked", false);
            int educationLevelId = question.optInt("education_level_id", 0);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card);
            card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = dpToPx(12);
            card.setLayoutParams(cardParams);

            // Top row: Badge + Author
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            // Education level badge
            TextView badge = new TextView(this);
            badge.setText(getEducationLevelLabel(educationLevelId));
            badge.setTextSize(10);
            badge.setTextColor(Color.WHITE);
            badge.setTypeface(null, Typeface.BOLD);
            badge.setBackgroundResource(R.drawable.bg_category_badge);
            badge.setPadding(dpToPx(8), dpToPx(3), dpToPx(8), dpToPx(3));
            topRow.addView(badge);

            // Spacer
            View spacer = new View(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1));
            topRow.addView(spacer);

            // Author
            TextView txtAuthor = new TextView(this);
            txtAuthor.setText("by " + authorName);
            txtAuthor.setTextSize(11);
            txtAuthor.setTextColor(Color.parseColor("#64748B"));
            topRow.addView(txtAuthor);

            card.addView(topRow);

            // Title
            TextView txtTitle = new TextView(this);
            txtTitle.setText(title);
            txtTitle.setTextSize(16);
            txtTitle.setTypeface(null, Typeface.BOLD);
            txtTitle.setTextColor(Color.parseColor("#0F172A"));
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleParams.topMargin = dpToPx(10);
            txtTitle.setLayoutParams(titleParams);
            card.addView(txtTitle);

            // Description preview
            if (description != null && !description.isEmpty()) {
                TextView txtDesc = new TextView(this);
                String shortDesc = description.length() > 80 ? description.substring(0, 80) + "..." : description;
                txtDesc.setText(shortDesc);
                txtDesc.setTextSize(13);
                txtDesc.setTextColor(Color.parseColor("#64748B"));
                LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                descParams.topMargin = dpToPx(6);
                txtDesc.setLayoutParams(descParams);
                card.addView(txtDesc);
            }

            // Bottom row: Like + Replies
            LinearLayout bottomRow = new LinearLayout(this);
            bottomRow.setOrientation(LinearLayout.HORIZONTAL);
            bottomRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bottomParams.topMargin = dpToPx(12);
            bottomRow.setLayoutParams(bottomParams);

            // Like button
            LinearLayout likeBtn = new LinearLayout(this);
            likeBtn.setOrientation(LinearLayout.HORIZONTAL);
            likeBtn.setGravity(Gravity.CENTER_VERTICAL);
            likeBtn.setPadding(dpToPx(8), dpToPx(6), dpToPx(8), dpToPx(6));
            likeBtn.setBackgroundResource(R.drawable.bg_tab_unselected);

            ImageView heartIcon = new ImageView(this);
            heartIcon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(16), dpToPx(16)));
            heartIcon.setImageResource(userLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            likeBtn.addView(heartIcon);

            TextView likeCount = new TextView(this);
            likeCount.setText(" " + likesCount);
            likeCount.setTextSize(12);
            likeCount.setTextColor(userLiked ? Color.parseColor("#EF4444") : Color.parseColor("#64748B"));
            likeBtn.addView(likeCount);

            likeBtn.setOnClickListener(
                    v -> toggleLikeQuestion(questionId, heartIcon, likeCount, userLiked, likesCount));

            bottomRow.addView(likeBtn);

            // Replies
            TextView txtReplies = new TextView(this);
            txtReplies.setText("💬 " + replies + " replies");
            txtReplies.setTextSize(12);
            txtReplies.setTextColor(Color.parseColor("#64748B"));
            LinearLayout.LayoutParams repliesParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            repliesParams.setMarginStart(dpToPx(16));
            txtReplies.setLayoutParams(repliesParams);
            bottomRow.addView(txtReplies);

            card.addView(bottomRow);

            // Card click - navigate to details
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, ForumQuestionDetailsPage.class);
                intent.putExtra("question_id", questionId);
                startActivity(intent);
            });

            questionsContainer.addView(card);

        } catch (Exception e) {
            Log.e(TAG, "Error creating question card", e);
        }
    }

    private void toggleLikeQuestion(int questionId, ImageView heartIcon, TextView likeCount, boolean wasLiked,
            int currentCount) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("user_id", sessionManager.getUserId());
            payload.put("question_id", questionId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.getBaseUrl() + "like_question.php",
                    payload,
                    response -> {
                        boolean nowLiked = response.optBoolean("liked", false);
                        int newCount = nowLiked ? currentCount + 1 : currentCount - 1;
                        heartIcon.setImageResource(nowLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                        likeCount.setText(" " + Math.max(0, newCount));
                        likeCount.setTextColor(nowLiked ? Color.parseColor("#EF4444") : Color.parseColor("#64748B"));
                    },
                    error -> Log.e(TAG, "Error toggling like", error));

            requestQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error creating like request", e);
        }
    }

    private String getEducationLevelLabel(int levelId) {
        switch (levelId) {
            case 1:
                return "AFTER 10TH";
            case 2:
                return "AFTER 12TH";
            case 3:
                return "DIPLOMA";
            case 4:
                return "UG";
            case 5:
                return "PG";
            default:
                return "GENERAL";
        }
    }

    private void addNoDataMessage() {
        TextView txt = new TextView(this);
        txt.setText("No questions found. Be the first to ask!");
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
    protected void onResume() {
        super.onResume();
        loadUnreadCount();
        if (currentTab == 0) {
            loadQuestions(LEVEL_ALL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
