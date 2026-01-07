package com.SIMATS.myapplication;

import com.SIMATS.myapplication.network.ApiConfig;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageButton;
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
 * ForumQuestionDetailsPage - Shows question details with answers and like
 * functionality.
 */
public class ForumQuestionDetailsPage extends AppCompatActivity {

    private static final String TAG = "ForumQuestionDetails";
    // Using centralized ApiConfig.getBaseUrl()

    private ImageView backIcon;
    private TextView txtQuestionTitle, txtQuestionDescription, txtAnswersHeader;
    private TextView txtAuthorName, txtAuthorRole, txtAuthorInitials, txtTimeAgo;
    private LinearLayout answersContainer, questionCard;
    private EditText inputAnswer;
    private ImageButton btnSendAnswer;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    private int questionId;
    private int questionLikesCount = 0;
    private boolean questionUserLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_question_details_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        questionId = getIntent().getIntExtra("question_id", 0);

        initViews();
        setupClickListeners();
        loadQuestionDetails();
        loadAnswers();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        txtQuestionTitle = findViewById(R.id.txtQuestionTitle);
        txtQuestionDescription = findViewById(R.id.txtQuestionDescription);
        txtAnswersHeader = findViewById(R.id.txtAnswersHeader);
        txtAuthorName = findViewById(R.id.txtAuthorName);
        txtAuthorRole = findViewById(R.id.txtAuthorRole);
        txtAuthorInitials = findViewById(R.id.txtAuthorInitials);
        txtTimeAgo = findViewById(R.id.txtTimeAgo);
        answersContainer = findViewById(R.id.answersContainer);
        questionCard = findViewById(R.id.questionCard);
        inputAnswer = findViewById(R.id.inputAnswer);
        btnSendAnswer = findViewById(R.id.btnSendAnswer);
    }

    private void setupClickListeners() {
        backIcon.setOnClickListener(v -> finish());
        btnSendAnswer.setOnClickListener(v -> postAnswer());
    }

    private void loadQuestionDetails() {
        String url = ApiConfig.getBaseUrl() + "get_question_detail.php?question_id=" + questionId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONObject question = response.getJSONObject("question");

                            String title = question.getString("title");
                            String description = question.optString("description", "");
                            String fullName = question.optString("full_name", "Anonymous");
                            String createdAt = question.optString("created_at", "");

                            txtQuestionTitle.setText(title);
                            txtQuestionDescription.setText(description);
                            txtAuthorName.setText(fullName);
                            txtAuthorRole.setText("Student");
                            txtAuthorInitials.setText(getInitials(fullName));
                            txtTimeAgo.setText(getTimeAgo(createdAt));

                            // Add like button to question card
                            addQuestionLikeButton();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing question", e);
                    }
                },
                error -> Log.e(TAG, "Error loading question", error));

        requestQueue.add(request);
    }

    private void addQuestionLikeButton() {
        // First get the current like status
        int userId = sessionManager.getUserId();
        String url = ApiConfig.getBaseUrl() + "get_questions.php?user_id=" + userId + "&question_id=" + questionId;

        // For simplicity, we'll add a static like button; in production, fetch actual
        // like status
        LinearLayout likeRow = new LinearLayout(this);
        likeRow.setOrientation(LinearLayout.HORIZONTAL);
        likeRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams likeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        likeParams.topMargin = dpToPx(12);
        likeRow.setLayoutParams(likeParams);

        LinearLayout likeBtn = new LinearLayout(this);
        likeBtn.setOrientation(LinearLayout.HORIZONTAL);
        likeBtn.setGravity(Gravity.CENTER_VERTICAL);
        likeBtn.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
        likeBtn.setBackgroundResource(R.drawable.bg_tab_unselected);

        ImageView heartIcon = new ImageView(this);
        heartIcon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(18), dpToPx(18)));
        heartIcon.setImageResource(R.drawable.ic_heart_outline);
        likeBtn.addView(heartIcon);

        TextView likeTxt = new TextView(this);
        likeTxt.setText(" Like this question");
        likeTxt.setTextSize(13);
        likeTxt.setTextColor(Color.parseColor("#64748B"));
        likeBtn.addView(likeTxt);

        likeBtn.setOnClickListener(v -> {
            toggleQuestionLike(heartIcon, likeTxt);
        });

        likeRow.addView(likeBtn);
        questionCard.addView(likeRow);
    }

    private void toggleQuestionLike(ImageView heartIcon, TextView likeTxt) {
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
                        heartIcon.setImageResource(nowLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                        likeTxt.setText(nowLiked ? " Liked!" : " Like this question");
                        likeTxt.setTextColor(nowLiked ? Color.parseColor("#EF4444") : Color.parseColor("#64748B"));
                        Toast.makeText(this, nowLiked ? "Liked!" : "Like removed", Toast.LENGTH_SHORT).show();
                    },
                    error -> Log.e(TAG, "Error toggling like", error));

            requestQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error creating like request", e);
        }
    }

    private void loadAnswers() {
        int userId = sessionManager.getUserId();
        String url = ApiConfig.getBaseUrl() + "get_answers.php?question_id=" + questionId + "&user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONArray answers = response.getJSONArray("answers");
                            txtAnswersHeader.setText("Answers (" + answers.length() + ")");

                            answersContainer.removeAllViews();
                            for (int i = 0; i < answers.length(); i++) {
                                addAnswerCard(answers.getJSONObject(i));
                            }

                            if (answers.length() == 0) {
                                addNoAnswersMessage();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing answers", e);
                    }
                },
                error -> Log.e(TAG, "Error loading answers", error));

        requestQueue.add(request);
    }

    private void postAnswer() {
        String answerText = inputAnswer.getText().toString().trim();

        if (answerText.isEmpty()) {
            inputAnswer.setError("Please enter an answer");
            return;
        }

        int userId = sessionManager.getUserId();

        try {
            JSONObject payload = new JSONObject();
            payload.put("question_id", questionId);
            payload.put("user_id", userId);
            payload.put("answer_text", answerText);

            btnSendAnswer.setEnabled(false);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.getBaseUrl() + "post_answer.php",
                    payload,
                    response -> {
                        if (response.optBoolean("status", false)) {
                            Toast.makeText(this, "Answer posted!", Toast.LENGTH_SHORT).show();
                            inputAnswer.setText("");
                            loadAnswers();
                        } else {
                            Toast.makeText(this, "Failed to post answer", Toast.LENGTH_SHORT).show();
                        }
                        btnSendAnswer.setEnabled(true);
                    },
                    error -> {
                        Log.e(TAG, "Error posting answer", error);
                        Toast.makeText(this, "Failed to post answer", Toast.LENGTH_SHORT).show();
                        btnSendAnswer.setEnabled(true);
                    });

            requestQueue.add(request);

        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
            btnSendAnswer.setEnabled(true);
        }
    }

    private void addAnswerCard(JSONObject answer) {
        try {
            int answerId = answer.getInt("answer_id");
            String fullName = answer.optString("full_name", "Anonymous");
            String answerText = answer.getString("answer_text");
            String createdAt = answer.optString("created_at", "");
            int likesCount = answer.optInt("likes_count", 0);
            boolean userLiked = answer.optBoolean("user_liked", false);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card);
            card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = dpToPx(12);
            card.setLayoutParams(cardParams);

            // Author row
            LinearLayout authorRow = new LinearLayout(this);
            authorRow.setOrientation(LinearLayout.HORIZONTAL);
            authorRow.setGravity(Gravity.CENTER_VERTICAL);

            // Avatar
            TextView avatar = new TextView(this);
            avatar.setText(getInitials(fullName));
            avatar.setTextSize(12);
            avatar.setTextColor(Color.WHITE);
            avatar.setTypeface(null, Typeface.BOLD);
            avatar.setGravity(Gravity.CENTER);
            avatar.setBackgroundResource(R.drawable.bg_primary_button);
            LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
            avatar.setLayoutParams(avatarParams);
            authorRow.addView(avatar);

            // Name column
            LinearLayout nameColumn = new LinearLayout(this);
            nameColumn.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams nameColParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            nameColParams.setMarginStart(dpToPx(12));
            nameColumn.setLayoutParams(nameColParams);

            TextView txtName = new TextView(this);
            txtName.setText(fullName);
            txtName.setTextSize(14);
            txtName.setTypeface(null, Typeface.BOLD);
            txtName.setTextColor(Color.parseColor("#0F172A"));
            nameColumn.addView(txtName);

            authorRow.addView(nameColumn);

            // Time
            TextView txtTime = new TextView(this);
            txtTime.setText(getTimeAgo(createdAt));
            txtTime.setTextSize(11);
            txtTime.setTextColor(Color.parseColor("#64748B"));
            authorRow.addView(txtTime);

            card.addView(authorRow);

            // Answer text
            TextView txtAnswer = new TextView(this);
            txtAnswer.setText(answerText);
            txtAnswer.setTextSize(14);
            txtAnswer.setTextColor(Color.parseColor("#374151"));
            txtAnswer.setLineSpacing(dpToPx(2), 1);
            LinearLayout.LayoutParams answerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            answerParams.topMargin = dpToPx(12);
            txtAnswer.setLayoutParams(answerParams);
            card.addView(txtAnswer);

            // Actions row: Like
            LinearLayout actionsRow = new LinearLayout(this);
            actionsRow.setOrientation(LinearLayout.HORIZONTAL);
            actionsRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams actionsParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            actionsParams.topMargin = dpToPx(12);
            actionsRow.setLayoutParams(actionsParams);

            // Like button for answer
            LinearLayout likeBtn = new LinearLayout(this);
            likeBtn.setOrientation(LinearLayout.HORIZONTAL);
            likeBtn.setGravity(Gravity.CENTER_VERTICAL);
            likeBtn.setPadding(dpToPx(8), dpToPx(6), dpToPx(8), dpToPx(6));
            likeBtn.setBackgroundResource(R.drawable.bg_tab_unselected);

            ImageView heartIcon = new ImageView(this);
            heartIcon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(16), dpToPx(16)));
            heartIcon.setImageResource(userLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            likeBtn.addView(heartIcon);

            TextView likeCountTxt = new TextView(this);
            likeCountTxt.setText(" " + likesCount);
            likeCountTxt.setTextSize(12);
            likeCountTxt.setTextColor(userLiked ? Color.parseColor("#EF4444") : Color.parseColor("#64748B"));
            likeBtn.addView(likeCountTxt);

            final int currentLikes = likesCount;
            final boolean currentlyLiked = userLiked;

            likeBtn.setOnClickListener(
                    v -> toggleAnswerLike(answerId, heartIcon, likeCountTxt, currentlyLiked, currentLikes));

            actionsRow.addView(likeBtn);

            card.addView(actionsRow);

            // ------------------ REPLIES SECTION ------------------ //
            JSONArray replies = answer.optJSONArray("replies");
            if (replies != null && replies.length() > 0) {
                // Divider
                android.view.View divider = new android.view.View(this);
                divider.setLayoutParams(
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1)));
                divider.setBackgroundColor(Color.parseColor("#E2E8F0"));
                ((LinearLayout.LayoutParams) divider.getLayoutParams()).topMargin = dpToPx(12);
                ((LinearLayout.LayoutParams) divider.getLayoutParams()).bottomMargin = dpToPx(8);
                card.addView(divider);

                // Container for replies
                LinearLayout repliesLayout = new LinearLayout(this);
                repliesLayout.setOrientation(LinearLayout.VERTICAL);
                repliesLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                repliesLayout.setPadding(dpToPx(16), 0, 0, 0); // Indent

                for (int i = 0; i < replies.length(); i++) {
                    addReplyItem(repliesLayout, replies.getJSONObject(i));
                }
                card.addView(repliesLayout);
            }

            answersContainer.addView(card);
        } catch (Exception e) {
            Log.e(TAG, "Error creating answer card", e);
        }
    }

    private void toggleAnswerLike(int answerId, ImageView heartIcon, TextView likeCountTxt, boolean wasLiked,
            int currentCount) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("user_id", sessionManager.getUserId());
            payload.put("answer_id", answerId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiConfig.getBaseUrl() + "like_answer.php",
                    payload,
                    response -> {
                        boolean nowLiked = response.optBoolean("liked", false);
                        int newCount = nowLiked ? currentCount + 1 : currentCount - 1;
                        heartIcon.setImageResource(nowLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                        likeCountTxt.setText(" " + Math.max(0, newCount));
                        likeCountTxt.setTextColor(nowLiked ? Color.parseColor("#EF4444") : Color.parseColor("#64748B"));
                    },
                    error -> Log.e(TAG, "Error toggling answer like", error));

            requestQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error creating like request", e);
        }
    }

    private void addReplyItem(LinearLayout container, JSONObject reply) {
        try {
            String author = reply.optString("full_name", "User");
            String text = reply.getString("reply_text");

            LinearLayout replyRow = new LinearLayout(this);
            replyRow.setOrientation(LinearLayout.VERTICAL);
            replyRow.setBackgroundColor(Color.parseColor("#F8FAFC")); // Light gray
            replyRow.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = dpToPx(6);
            replyRow.setLayoutParams(params);

            TextView authorTxt = new TextView(this);
            authorTxt.setText(author);
            authorTxt.setTextSize(11);
            authorTxt.setTypeface(null, Typeface.BOLD);
            authorTxt.setTextColor(Color.parseColor("#334155"));
            replyRow.addView(authorTxt);

            TextView contentTxt = new TextView(this);
            contentTxt.setText(text);
            contentTxt.setTextSize(13);
            contentTxt.setTextColor(Color.parseColor("#475569"));
            replyRow.addView(contentTxt);

            container.addView(replyRow);

        } catch (Exception e) {
            Log.e(TAG, "Error adding reply item", e);
        }
    }

    private void addNoAnswersMessage() {
        TextView txt = new TextView(this);
        txt.setText("No answers yet. Be the first to help!");
        txt.setTextSize(14);
        txt.setTextColor(Color.parseColor("#94A3B8"));
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(dpToPx(16), dpToPx(32), dpToPx(16), dpToPx(32));
        answersContainer.addView(txt);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty())
            return "??";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return "" + parts[0].charAt(0) + parts[1].charAt(0);
        }
        return name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.toUpperCase();
    }

    private String getTimeAgo(String dateStr) {
        return "recently";
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
