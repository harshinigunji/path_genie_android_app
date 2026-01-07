package com.SIMATS.myapplication;

import android.content.Intent;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.SIMATS.myapplication.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * SavedRoadmapListPage - Shows list of all saved roadmaps
 * Delete functionality is in SavedFullRoadmapPage (detail view)
 */
public class SavedRoadmapListPage extends AppCompatActivity {

    private static final String TAG = "SavedRoadmapListPage";

    private ImageView backButton;
    private LinearLayout roadmapsContainer;
    private ProgressBar loadingProgress;
    private ScrollView listScrollView;
    private LinearLayout emptyState;
    private LinearLayout navHome, navCommunity, navSaved, navProfile;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_roadmap_list_page);

        sessionManager = new SessionManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        fetchSavedRoadmaps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning from details page (in case user deleted)
        fetchSavedRoadmaps();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        roadmapsContainer = findViewById(R.id.roadmapsContainer);
        loadingProgress = findViewById(R.id.loadingProgress);
        listScrollView = findViewById(R.id.listScrollView);
        emptyState = findViewById(R.id.emptyState);

        navHome = findViewById(R.id.navHome);
        navCommunity = findViewById(R.id.navCommunity);
        navSaved = findViewById(R.id.navSaved);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePage.class));
            finish();
        });
        navCommunity.setOnClickListener(v -> {
            startActivity(new Intent(this, ForumHomePage.class));
            finish();
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfilePage.class));
            finish();
        });
    }

    private void fetchSavedRoadmaps() {
        loadingProgress.setVisibility(View.VISIBLE);
        listScrollView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url = ApiConfig.getBaseUrl() + "get_saved_roadmaps.php?user_id=" + userId;
        Log.d(TAG, "Fetching from: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingProgress.setVisibility(View.GONE);
                    Log.d(TAG, "Response: " + response.toString());

                    try {
                        boolean success = response.optBoolean("success", false);
                        if (success) {
                            JSONArray roadmaps = response.optJSONArray("data");
                            if (roadmaps != null && roadmaps.length() > 0) {
                                listScrollView.setVisibility(View.VISIBLE);
                                populateRoadmaps(roadmaps);
                            } else {
                                emptyState.setVisibility(View.VISIBLE);
                            }
                        } else {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getMessage(), e);
                        emptyState.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Network error", error);
                });

        queue.add(request);
    }

    private void populateRoadmaps(JSONArray roadmaps) {
        roadmapsContainer.removeAllViews();

        try {
            for (int i = 0; i < roadmaps.length(); i++) {
                JSONObject roadmap = roadmaps.getJSONObject(i);
                int roadmapId = roadmap.optInt("roadmap_id", 0);
                String type = roadmap.optString("roadmap_type", "SYSTEM");
                String title = roadmap.optString("title", "My Roadmap");
                String targetJob = roadmap.optString("target_job_name", "");
                String fromStream = roadmap.optString("from_stream", "");

                String description = "";
                if (!fromStream.isEmpty() && !targetJob.isEmpty()) {
                    description = "From " + fromStream + " to " + targetJob;
                } else if (!targetJob.isEmpty()) {
                    description = "Career path to " + targetJob;
                }

                View card = createRoadmapCard(roadmapId, type, title, description);
                roadmapsContainer.addView(card);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating: " + e.getMessage(), e);
        }
    }

    private View createRoadmapCard(int roadmapId, String type, String title, String description) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundResource(R.drawable.bg_card_white);
        card.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int padding = dp(16);
        card.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(16);
        card.setLayoutParams(cardParams);

        // Icon
        ImageView icon = new ImageView(this);
        int iconSize = dp(44);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.bg_icon_circle_light);
        icon.setPadding(dp(10), dp(10), dp(10), dp(10));
        icon.setImageResource(type.equals("SYSTEM") ? R.drawable.ic_roadmap : R.drawable.ic_briefcase);
        icon.setColorFilter(0xFF2563EB);
        card.addView(icon);

        // Text container
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,
                1);
        textParams.leftMargin = dp(16);
        textContainer.setLayoutParams(textParams);

        // Title
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(0xFF111827);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setMaxLines(1);
        titleText.setEllipsize(android.text.TextUtils.TruncateAt.END);
        textContainer.addView(titleText);

        // Description
        if (description != null && !description.isEmpty()) {
            TextView descText = new TextView(this);
            descText.setText(description);
            descText.setTextSize(13);
            descText.setTextColor(0xFF6B7280);
            descText.setMaxLines(1);
            descText.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            descParams.topMargin = dp(2);
            descText.setLayoutParams(descParams);
            textContainer.addView(descText);
        }

        // View Roadmap link
        TextView viewLink = new TextView(this);
        viewLink.setText("VIEW ROADMAP →");
        viewLink.setTextSize(13);
        viewLink.setTextColor(0xFF2563EB);
        viewLink.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams linkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linkParams.topMargin = dp(8);
        viewLink.setLayoutParams(linkParams);
        textContainer.addView(viewLink);

        card.addView(textContainer);

        // Click listener - go to details
        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, SavedFullRoadmapPage.class);
            intent.putExtra("roadmap_id", roadmapId);
            intent.putExtra("roadmap_type", type);
            intent.putExtra("title", title);
            startActivity(intent);
        });

        return card;
    }

    private int dp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
