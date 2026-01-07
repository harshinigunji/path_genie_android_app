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
 * NotificationsPage - Shows user notifications for forum activity.
 * Click navigates to ForumMyQuestionAnswersPage.
 */
public class NotificationsPage extends AppCompatActivity {

    private static final String TAG = "NotificationsPage";
    // Using centralized ApiConfig.getBaseUrl()

    private ImageView backIcon;
    private TextView btnMarkAllRead, btnClearAll;
    private LinearLayout notificationsContainer;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupClickListeners();
        loadNotifications();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        btnClearAll = findViewById(R.id.btnClearAll);
        notificationsContainer = findViewById(R.id.notificationsContainer);
    }

    private void setupClickListeners() {
        backIcon.setOnClickListener(v -> finish());
        btnMarkAllRead.setOnClickListener(v -> markAllRead());
        if (btnClearAll != null) {
            btnClearAll.setOnClickListener(v -> clearAllNotifications());
        }
    }

    private void loadNotifications() {
        notificationsContainer.removeAllViews();

        int userId = sessionManager.getUserId();
        String url = ApiConfig.getBaseUrl() + "get_notifications.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONArray notifications = response.getJSONArray("notifications");
                            for (int i = 0; i < notifications.length(); i++) {
                                addNotificationCard(notifications.getJSONObject(i));
                            }
                            if (notifications.length() == 0) {
                                addNoDataMessage();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing notifications", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error loading notifications", error);
                    Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void addNotificationCard(JSONObject notification) {
        try {
            int notificationId = notification.getInt("notification_id");
            String type = notification.getString("type");
            String fromUserName = notification.optString("from_user_name", "Someone");
            String questionTitle = notification.optString("question_title", "");
            int questionId = notification.optInt("question_id", 0);
            boolean isRead = notification.optInt("is_read", 0) == 1;
            String createdAt = notification.optString("created_at", "");

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(isRead ? R.drawable.bg_card : R.drawable.bg_tab_selected);
            card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = dpToPx(12);
            card.setLayoutParams(cardParams);

            // Icon + Message Row
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);

            // Icon based on type
            TextView icon = new TextView(this);
            icon.setTextSize(24);
            switch (type) {
                case "answer":
                case "reply":
                    icon.setText("💬");
                    break;
                case "like_question":
                case "like_answer":
                    icon.setText("❤️");
                    break;
                default:
                    icon.setText("🔔");
            }
            row.addView(icon);

            // Text content
            LinearLayout textCol = new LinearLayout(this);
            textCol.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            textParams.setMarginStart(dpToPx(12));
            textCol.setLayoutParams(textParams);

            // From user
            TextView txtFrom = new TextView(this);
            txtFrom.setText(fromUserName);
            txtFrom.setTextSize(15);
            txtFrom.setTypeface(null, Typeface.BOLD);
            txtFrom.setTextColor(Color.parseColor("#0F172A"));
            textCol.addView(txtFrom);

            // Message
            TextView txtMessage = new TextView(this);
            txtMessage.setText(getNotificationMessage(type, questionTitle));
            txtMessage.setTextSize(13);
            txtMessage.setTextColor(Color.parseColor("#64748B"));
            txtMessage.setMaxLines(2);
            LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            msgParams.topMargin = dpToPx(4);
            txtMessage.setLayoutParams(msgParams);
            textCol.addView(txtMessage);

            row.addView(textCol);

            // Time
            TextView txtTime = new TextView(this);
            txtTime.setText(formatTime(createdAt));
            txtTime.setTextSize(11);
            txtTime.setTextColor(Color.parseColor("#94A3B8"));
            row.addView(txtTime);

            card.addView(row);

            // Click to open ForumMyQuestionAnswersPage
            if (questionId > 0) {
                card.setOnClickListener(v -> {
                    markAsRead(notificationId);
                    Intent intent = new Intent(this, ForumMyQuestionAnswersPage.class);
                    intent.putExtra("question_id", questionId);
                    startActivity(intent);
                });
            }

            notificationsContainer.addView(card);

        } catch (Exception e) {
            Log.e(TAG, "Error creating notification card", e);
        }
    }

    private String getNotificationMessage(String type, String questionTitle) {
        String shortTitle = questionTitle.length() > 50 ? questionTitle.substring(0, 50) + "..." : questionTitle;
        switch (type) {
            case "answer":
                return "answered your question: \"" + shortTitle + "\"";
            case "reply":
                return "replied to your answer on: \"" + shortTitle + "\"";
            case "like_question":
                return "liked your question: \"" + shortTitle + "\"";
            case "like_answer":
                return "liked your answer";
            default:
                return "sent you a notification";
        }
    }

    private String formatTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return "";
        // Simple format - just show date part
        if (dateStr.contains(" ")) {
            return dateStr.split(" ")[0];
        }
        return dateStr;
    }

    private void markAsRead(int notificationId) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("user_id", sessionManager.getUserId());
            payload.put("notification_id", notificationId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.getBaseUrl() + "mark_notification_read.php",
                    payload,
                    response -> {
                    },
                    error -> Log.e(TAG, "Error marking notification read", error));

            requestQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
        }
    }

    private void markAllRead() {
        try {
            JSONObject payload = new JSONObject();
            payload.put("user_id", sessionManager.getUserId());
            payload.put("mark_all", true);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.getBaseUrl() + "mark_notification_read.php",
                    payload,
                    response -> {
                        Toast.makeText(this, "All marked as read", Toast.LENGTH_SHORT).show();
                        loadNotifications();
                    },
                    error -> Log.e(TAG, "Error marking all read", error));

            requestQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
        }
    }

    private void clearAllNotifications() {
        try {
            JSONObject payload = new JSONObject();
            payload.put("user_id", sessionManager.getUserId());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.getBaseUrl() + "clear_notifications.php",
                    payload,
                    response -> {
                        if (response.optBoolean("status", false)) {
                            Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
                            loadNotifications();
                        }
                    },
                    error -> Log.e(TAG, "Error clearing notifications", error));

            requestQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
        }
    }

    private void addNoDataMessage() {
        TextView txt = new TextView(this);
        txt.setText(
                "🔔 No notifications yet\n\nYou'll see notifications here when someone answers your questions or likes your content.");
        txt.setTextSize(14);
        txt.setTextColor(Color.parseColor("#64748B"));
        txt.setGravity(Gravity.CENTER);
        txt.setLineSpacing(dpToPx(4), 1);
        txt.setPadding(dpToPx(32), dpToPx(64), dpToPx(32), dpToPx(64));
        notificationsContainer.addView(txt);
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
