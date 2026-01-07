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
 * ELEAddedSucessfullyPage - Success screen shown after adding exam to roadmap.
 * Auto-redirects to StreamsPage after a short delay with animations.
 */
public class ELEAddedSucessfullyPage extends AppCompatActivity {

    private static final int REDIRECT_DELAY_MS = 2000; // 2 seconds delay

    private int educationLevelId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eleadded_sucessfully_page);

        // Get education_level_id from intent for navigation back to streams
        educationLevelId = getIntent().getIntExtra("education_level_id", 1);

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start animations
        startSuccessAnimations();

        // Auto-redirect to StreamsPage after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToStreamsPage, REDIRECT_DELAY_MS);
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
     * Navigate back to StreamsPage.
     */
    private void navigateToStreamsPage() {
        Intent intent = new Intent(this, StreamsPage.class);
        intent.putExtra("education_level_id", educationLevelId);
        // Clear the back stack so user goes directly to streams
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Navigate to streams page on back press as well
        navigateToStreamsPage();
    }
}
