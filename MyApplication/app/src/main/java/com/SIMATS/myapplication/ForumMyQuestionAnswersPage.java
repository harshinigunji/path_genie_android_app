package com.SIMATS.myapplication;

import com.SIMATS.myapplication.network.ApiConfig;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
 * ForumMyQuestionAnswersPage - Shows answers to user's own question with Reply
 * functionality.
 */
public class ForumMyQuestionAnswersPage extends AppCompatActivity {

    private static final String TAG = "ForumMyAnswers";

    private ImageView backIcon, btnDelete;
    private TextView txtQuestionTitle, txtQuestionDescription, txtQuestionDate, txtAnswersHeader;
    private LinearLayout answersContainer;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    private int questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_my_question_answers_page);

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
        loadQuestionAndAnswers();
    }

    private void initViews() {
        backIcon = findViewById(R.id.backIcon);
        btnDelete = findViewById(R.id.btnDelete);
        txtQuestionTitle = findViewById(R.id.txtQuestionTitle);
        txtQuestionDescription = findViewById(R.id.txtQuestionDescription);
        txtQuestionDate = findViewById(R.id.txtQuestionDate);
        txtAnswersHeader = findViewById(R.id.txtAnswersHeader);
        answersContainer = findViewById(R.id.answersContainer);
    }

    private void setupClickListeners() {
        backIcon.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage(
                        "Are you sure you want to delete this question? All answers and replies will also be permanently deleted.")
                .setPositiveButton("Delete", (dialog, which) -> deleteQuestion())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteQuestion() {
        String url = ApiConfig.getBaseUrl() + "delete_question.php";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("question_id", questionId);
            requestBody.put("user_id", sessionManager.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    if (response.optBoolean("status", false)) {
                        Toast.makeText(this, "Question deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close page
                    } else {
                        Toast.makeText(this, response.optString("message", "Failed to delete"), Toast.LENGTH_SHORT)
                                .show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void loadQuestionAndAnswers() {
        loadQuestionDetails();
        loadAnswers();
    }

    private void loadQuestionDetails() {
        String url = ApiConfig.getBaseUrl() + "get_question_detail.php?question_id=" + questionId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optBoolean("status", false)) {
                            JSONObject question = response.getJSONObject("question");
                            txtQuestionTitle.setText(question.getString("title"));
                            txtQuestionDescription.setText(question.optString("description", ""));
                            txtQuestionDate.setText("📅 Posted on " + formatDate(question.optString("created_at", "")));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing question details", e);
                    }
                },
                error -> Log.e(TAG, "Error loading question details", error));

        requestQueue.add(request);
    }

    private void loadAnswers() {
        int userId = sessionManager.getUserId();
        String url = ApiConfig.getBaseUrl() + "my_question_answers.php?question_id=" + questionId + "&user_id="
                + userId;

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
                        } else {
                            String msg = response.optString("message", "Failed to load");
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing answers", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error loading answers", error);
                    Toast.makeText(this, "Failed to load answers", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void addAnswerCard(JSONObject answer) {
        try {
            int answerId = answer.getInt("answer_id");
            String fullName = answer.optString("full_name", "Anonymous");
            String answerText = answer.getString("answer_text");
            String createdAt = answer.optString("created_at", "");
            JSONArray replies = answer.optJSONArray("replies");

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

            // Name
            LinearLayout nameColumn = new LinearLayout(this);
            nameColumn.setOrientation(LinearLayout.VERTICAL);
            nameColumn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
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

            // ------------------ REPLIES SECTION ------------------ //

            // Container for replies
            LinearLayout repliesLayout = new LinearLayout(this);
            repliesLayout.setOrientation(LinearLayout.VERTICAL);
            repliesLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            repliesLayout.setPadding(dpToPx(8), dpToPx(8), 0, 0); // Indent

            if (replies != null && replies.length() > 0) {
                View divider = new View(this);
                divider.setLayoutParams(
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1)));
                divider.setBackgroundColor(Color.parseColor("#E2E8F0"));
                ((LinearLayout.LayoutParams) divider.getLayoutParams()).topMargin = dpToPx(12);
                ((LinearLayout.LayoutParams) divider.getLayoutParams()).bottomMargin = dpToPx(8);
                card.addView(divider);

                for (int i = 0; i < replies.length(); i++) {
                    JSONObject reply = replies.getJSONObject(i);
                    addReplyItem(repliesLayout, reply);
                }
                card.addView(repliesLayout);
            }

            // Reply Button
            TextView btnReply = new TextView(this);
            btnReply.setText("Reply");
            btnReply.setTextSize(12);
            btnReply.setTextColor(Color.parseColor("#2563EB"));
            btnReply.setTypeface(null, Typeface.BOLD);
            btnReply.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));
            btnReply.setGravity(Gravity.END);

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            btnParams.gravity = Gravity.END;
            btnParams.topMargin = dpToPx(8);
            btnReply.setLayoutParams(btnParams);

            btnReply.setOnClickListener(v -> showReplyDialog(answerId));
            card.addView(btnReply);

            answersContainer.addView(card);

        } catch (Exception e) {
            Log.e(TAG, "Error creating answer card", e);
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

    private void showReplyDialog(int answerId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reply to Answer");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Type your reply here...");
        input.setMinLines(3);
        input.setGravity(Gravity.TOP | Gravity.START);

        // Add margins
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(24), dpToPx(12), dpToPx(24), dpToPx(12));
        input.setLayoutParams(params);
        input.setBackgroundResource(android.R.drawable.edit_text); // Standard styling
        container.addView(input);

        builder.setView(container);

        builder.setPositiveButton("Post", (dialog, which) -> {
            String replyText = input.getText().toString().trim();
            if (!replyText.isEmpty()) {
                postReply(answerId, replyText);
            } else {
                Toast.makeText(this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void postReply(int answerId, String text) {
        String url = ApiConfig.getBaseUrl() + "post_reply.php";
        int userId = sessionManager.getUserId();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("answer_id", answerId);
            requestBody.put("user_id", userId);
            requestBody.put("reply_text", text);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    if (response.optBoolean("status", false)) {
                        Toast.makeText(this, "Reply posted!", Toast.LENGTH_SHORT).show();
                        loadAnswers(); // Refresh
                    } else {
                        Toast.makeText(this, response.optString("message", "Failed"), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error returning reply", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void addNoAnswersMessage() {
        TextView txt = new TextView(this);
        txt.setText("No answers yet. Check back later!");
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
        if (dateStr == null || dateStr.isEmpty())
            return "recently";
        // Simple implementation - in real app use SimpleDateFormat
        return dateStr;
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return "";
        return dateStr.split(" ")[0];
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
