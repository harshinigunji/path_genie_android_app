package com.SIMATS.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * StreamExamAddedSuccessPage - Success screen shown after adding stream exam to
 * roadmap.
 * Auto-redirects to ExamAddedConfirmationPage after a short delay.
 */
public class StreamExamAddedSuccessPage extends AppCompatActivity {

    private static final int REDIRECT_DELAY_MS = 2000; // 2 seconds delay

    private int streamId = -1;
    private int educationLevelId = -1;
    private String streamName = "";
    private String examName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stream_exam_added_success_page);

        // Get data from intent for navigation
        streamId = getIntent().getIntExtra("stream_id", -1);
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);
        streamName = getIntent().getStringExtra("stream_name");
        examName = getIntent().getStringExtra("exam_name");

        if (streamName == null)
            streamName = "";
        if (examName == null)
            examName = "Entrance Exam";

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start animations
        startSuccessAnimations();

        // Auto-redirect to ExamAddedConfirmationPage after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToConfirmation, REDIRECT_DELAY_MS);
    }

    /**
     * Start success animations on icon and text.
     */
    private void startSuccessAnimations() {
        View iconContainer = findViewById(R.id.successIconContainer);
        TextView titleText = findViewById(R.id.titleText);
        TextView subtitleText = findViewById(R.id.subtitleText);

        // Initial state - scale to 0
        iconContainer.setScaleX(0f);
        iconContainer.setScaleY(0f);
        titleText.setAlpha(0f);
        subtitleText.setAlpha(0f);

        // Animate icon with bounce/scale effect
        iconContainer.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    // Pulse animation after scale
                    startPulseAnimation(iconContainer);
                })
                .start();

        // Fade in title with delay
        titleText.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(300)
                .start();

        // Fade in subtitle with more delay
        subtitleText.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(500)
                .start();
    }

    /**
     * Start a continuous pulse animation.
     */
    private void startPulseAnimation(View view) {
        ScaleAnimation pulse = new ScaleAnimation(
                1.0f, 1.05f, 1.0f, 1.05f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setDuration(600);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(pulse);
    }

    /**
     * Navigate to ExamAddedConfirmationPage.
     */
    private void navigateToConfirmation() {
        Intent intent = new Intent(this, ExamAddedConfirmationPage.class);
        intent.putExtra("stream_id", streamId);
        intent.putExtra("education_level_id", educationLevelId);
        intent.putExtra("stream_name", streamName);
        intent.putExtra("exam_name", examName);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Navigate to confirmation page on back press as well
        navigateToConfirmation();
    }
}
