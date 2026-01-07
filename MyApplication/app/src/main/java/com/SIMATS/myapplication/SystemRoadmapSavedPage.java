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
 * SystemRoadmapSavedPage - Success confirmation matching UserRoadmapSuccessPage
 * UI/Logic.
 */
public class SystemRoadmapSavedPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_roadmap_saved_page);

        // Setup edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start animations
        startSuccessAnimations();

        // Auto-navigate to HomePage after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 2500);
    }

    /**
     * Start success animations on icon and text.
     */
    private void startSuccessAnimations() {
        ImageView iconContainer = findViewById(R.id.iconContainer);
        TextView titleText = findViewById(R.id.titleText);
        TextView subtitleText = findViewById(R.id.subtitleText);

        // Initial state - scale to 0
        if (iconContainer != null) {
            iconContainer.setScaleX(0f);
            iconContainer.setScaleY(0f);
        }
        if (titleText != null)
            titleText.setAlpha(0f);
        if (subtitleText != null)
            subtitleText.setAlpha(0f);

        // Animate icon with bounce/scale effect
        if (iconContainer != null) {
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
        }

        // Fade in title with delay
        if (titleText != null) {
            titleText.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay(300)
                    .start();
        }

        // Fade in subtitle with more delay
        if (subtitleText != null) {
            subtitleText.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay(500)
                    .start();
        }
    }

    /**
     * Start a continuous pulse animation.
     */
    private void startPulseAnimation(View view) {
        if (view == null)
            return;
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

    @Override
    public void onBackPressed() {
        // Override to prevent going back during success animation
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
