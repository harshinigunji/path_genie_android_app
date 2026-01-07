package com.SIMATS.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import com.SIMATS.myapplication.network.ApiConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * SavedFullRoadmapPage - Displays full roadmap timeline with all steps
 * Now includes delete functionality
 */
public class SavedFullRoadmapPage extends AppCompatActivity {

    private static final String TAG = "SavedFullRoadmapPage";

    private ImageView backButton;
    private ImageView deleteButton;
    private TextView roadmapTitle;
    private LinearLayout timelineContainer;
    private ProgressBar loadingProgress;
    private Button goHomeButton;

    private int roadmapId;
    private String roadmapType;
    private String title;
    private int userId;

    // Step type configurations: [type, badge, badgeBgColor, badgeTextColor]
    private static final String[][] STEP_CONFIG = {
            { "STREAM", "FOUNDATION", "#DBEAFE", "#2563EB" },
            { "EXAM", "ENTRANCE EXAM", "#FEF3C7", "#D97706" },
            { "JOB", "FINAL GOAL", "#2563EB", "#FFFFFF" },
            { "EDUCATION", "EDUCATION", "#DCFCE7", "#16A34A" },
            { "EDUCATION_LEVEL", "FOUNDATION", "#EBF4FF", "#2563EB" },
            { "EXPERIENCE", "EXPERIENCE", "#F3E8FF", "#9333EA" },
            { "PREPARATION", "JOB PREP", "#FFE4E6", "#E11D48" }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_full_roadmap_page);

        // Get data from intent
        roadmapId = getIntent().getIntExtra("roadmap_id", 0);
        roadmapType = getIntent().getStringExtra("roadmap_type");
        title = getIntent().getStringExtra("title");

        if (roadmapType == null)
            roadmapType = "SYSTEM";
        if (title == null)
            title = "Career Roadmap";

        // Get user ID
        // Use SessionManager to get user ID consistently
        com.SIMATS.myapplication.utils.SessionManager sessionMgr = new com.SIMATS.myapplication.utils.SessionManager(
                this);
        userId = sessionMgr.getUserId();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        fetchRoadmapSteps();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);
        roadmapTitle = findViewById(R.id.roadmapTitle);
        timelineContainer = findViewById(R.id.timelineContainer);
        loadingProgress = findViewById(R.id.loadingProgress);
        goHomeButton = findViewById(R.id.goHomeButton);

        roadmapTitle.setText(title);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        deleteButton.setOnClickListener(v -> showDeleteConfirmation());

        goHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Show delete confirmation dialog
     */
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Roadmap")
                .setMessage("Are you sure you want to delete this roadmap? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteRoadmap())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Delete the roadmap via API
     */
    private void deleteRoadmap() {
        if (userId == -1) {
            Toast.makeText(this, "Please login to delete roadmaps", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingProgress.setVisibility(View.VISIBLE);

        String url = ApiConfig.getBaseUrl() + "delete_roadmap.php";

        try {
            JSONObject body = new JSONObject();
            body.put("roadmap_id", roadmapId);
            body.put("user_id", userId);
            body.put("roadmap_type", roadmapType);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        loadingProgress.setVisibility(View.GONE);
                        try {
                            boolean status = response.getBoolean("status");
                            String message = response.optString("message", "");

                            if (status) {
                                Toast.makeText(this, "Roadmap deleted successfully", Toast.LENGTH_SHORT).show();
                                // Go back to list
                                finish();
                            } else {
                                Toast.makeText(this, message.isEmpty() ? "Failed to delete" : message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage());
                            Toast.makeText(this, "Error deleting roadmap", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        loadingProgress.setVisibility(View.GONE);
                        Log.e(TAG, "Network error: " + error.toString());
                        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            loadingProgress.setVisibility(View.GONE);
            Log.e(TAG, "Error creating request: " + e.getMessage());
        }
    }

    private void fetchRoadmapSteps() {
        loadingProgress.setVisibility(View.VISIBLE);
        timelineContainer.setVisibility(View.GONE);

        // Use different API based on roadmap type
        String url;
        if ("USER".equals(roadmapType)) {
            url = ApiConfig.getBaseUrl() + "get_user_roadmap_steps.php?roadmap_id=" + roadmapId;
        } else {
            url = ApiConfig.getBaseUrl() + "get_roadmap.php?roadmap_id=" + roadmapId;
        }
        Log.d(TAG, "Fetching from: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingProgress.setVisibility(View.GONE);
                    timelineContainer.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Response: " + response.toString());

                    try {
                        boolean success = response.optBoolean("status", false);
                        if (success) {
                            JSONArray steps = response.optJSONArray("data");
                            if (steps != null && steps.length() > 0) {
                                buildTimeline(steps);
                            } else {
                                Toast.makeText(this, "No steps found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = response.optString("message", "Failed to load roadmap");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getMessage(), e);
                        Toast.makeText(this, "Error loading roadmap", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    Log.e(TAG, "Network error", error);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void buildTimeline(JSONArray steps) {
        timelineContainer.removeAllViews();

        try {
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                int stepOrder = step.optInt("step_order", i + 1);
                String stepType = step.optString("step_type", "STREAM").toUpperCase();
                String stepTitle = step.optString("title", "Step " + stepOrder);
                String description = step.optString("description", "");
                String icon = step.optString("icon", "ic_step");

                boolean isLast = (i == steps.length() - 1);
                addTimelineStep(stepOrder, stepType, stepTitle, description, icon, isLast);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error building timeline: " + e.getMessage(), e);
        }
    }

    private void addTimelineStep(int stepOrder, String stepType, String stepTitle, String description, String iconName,
            boolean isLast) {
        // Find config for this step type
        String badge = stepType;
        String badgeBgColor = "#DBEAFE";
        String badgeTextColor = "#2563EB";

        for (String[] config : STEP_CONFIG) {
            if (config[0].equals(stepType)) {
                badge = config[1];
                badgeBgColor = config[2];
                badgeTextColor = config[3];
                break;
            }
        }

        boolean isFinalGoal = stepType.equals("JOB");
        int iconRes = getIconResource(iconName, stepType);

        // Create step container
        LinearLayout stepContainer = new LinearLayout(this);
        stepContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams stepParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        stepParams.bottomMargin = dp(0);
        stepContainer.setLayoutParams(stepParams);

        // Timeline column (icon + line)
        LinearLayout timelineCol = new LinearLayout(this);
        timelineCol.setOrientation(LinearLayout.VERTICAL);
        timelineCol.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams timelineParams = new LinearLayout.LayoutParams(dp(48),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        timelineCol.setLayoutParams(timelineParams);

        // Icon with light blue bg and blue tint
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(36), dp(36));
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconRes);

        if (isFinalGoal) {
            icon.setBackgroundResource(R.drawable.bg_circle_green);
            icon.setColorFilter(0xFFFFFFFF);
        } else {
            icon.setBackgroundResource(R.drawable.bg_icon_circle_light);
            icon.setColorFilter(0xFF2563EB);
        }
        icon.setPadding(dp(8), dp(8), dp(8), dp(8));
        timelineCol.addView(icon);

        // Connecting line to final goal
        if (!isLast) {
            View line = new View(this);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(dp(2), dp(70));
            line.setLayoutParams(lineParams);
            line.setBackgroundColor(0xFFCBD5E1);
            timelineCol.addView(line);
        }

        stepContainer.addView(timelineCol);

        // Content card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);

        if (isFinalGoal) {
            card.setBackgroundResource(R.drawable.bg_card_gradient_primary);
        } else {
            card.setBackgroundResource(R.drawable.bg_card_white);
        }
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,
                1);
        cardParams.leftMargin = dp(12);
        cardParams.bottomMargin = dp(16);
        card.setLayoutParams(cardParams);

        // Badge
        TextView badgeText = new TextView(this);
        badgeText.setText(badge);
        badgeText.setTextSize(10);
        badgeText.setTypeface(null, android.graphics.Typeface.BOLD);
        badgeText.setLetterSpacing(0.05f);

        if (isFinalGoal) {
            badgeText.setTextColor(0xFFFFFFFF);
            badgeText.setBackgroundResource(R.drawable.bg_badge);
            badgeText.getBackground().setTint(0x33FFFFFF);
        } else {
            badgeText.setTextColor(android.graphics.Color.parseColor(badgeTextColor));
            badgeText.setBackgroundResource(R.drawable.bg_badge);
            badgeText.getBackground().setTint(android.graphics.Color.parseColor(badgeBgColor));
        }
        badgeText.setPadding(dp(8), dp(4), dp(8), dp(4));
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeText.setLayoutParams(badgeParams);
        card.addView(badgeText);

        // Title
        TextView titleText = new TextView(this);
        titleText.setText(stepTitle);
        titleText.setTextSize(17);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setTextColor(isFinalGoal ? 0xFFFFFFFF : 0xFF111827);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.topMargin = dp(8);
        titleText.setLayoutParams(titleParams);
        card.addView(titleText);

        // Description
        if (description != null && !description.isEmpty()) {
            TextView descText = new TextView(this);
            descText.setText(description);
            descText.setTextSize(13);
            descText.setTextColor(isFinalGoal ? 0xCCFFFFFF : 0xFF6B7280);
            descText.setLineSpacing(0, 1.3f);
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            descParams.topMargin = dp(4);
            descText.setLayoutParams(descParams);
            card.addView(descText);
        }

        stepContainer.addView(card);
        timelineContainer.addView(stepContainer);
    }

    private int getIconResource(String iconName, String stepType) {
        switch (iconName) {
            case "ic_stream":
                return R.drawable.ic_science;
            case "ic_exam":
                return R.drawable.ic_exam;
            case "ic_job":
            case "ic_briefcase":
                return R.drawable.ic_trophy;
            case "ic_education":
                return R.drawable.ic_education;
            default:
                switch (stepType) {
                    case "STREAM":
                        return R.drawable.ic_science;
                    case "EXAM":
                        return R.drawable.ic_exam;
                    case "JOB":
                        return R.drawable.ic_trophy;
                    case "EDUCATION":
                    case "EDUCATION_LEVEL":
                        return R.drawable.ic_education;
                    case "EXPERIENCE":
                        return R.drawable.ic_code;
                    default:
                        return R.drawable.ic_education;
                }
        }
    }

    private int dp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
