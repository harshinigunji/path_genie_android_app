package com.SIMATS.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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

import com.SIMATS.myapplication.network.ApiConfig;
import com.SIMATS.myapplication.utils.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * HomePage Activity - Main dashboard.
 * Updated: Fetches profile image from server.
 */
public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePage";

    // UI Components - Feature Cards
    private LinearLayout exploreStreamsCard;
    private LinearLayout entranceExamsCard;
    private LinearLayout jobsCard;
    private LinearLayout pathGuidanceCard;

    // UI Components - AI Tools
    private LinearLayout aiRoadmapCard;
    private LinearLayout careerRecommendationCard;

    // UI Components - Other
    private TextView greetingText;
    private ImageButton fabChat;
    private View avatarContainer; // The RelativeLayout or FrameLayout
    private ImageView userAvatar; // The actual ImageView inside it

    // Notification Bell
    private FrameLayout notificationBellContainer;
    private TextView notificationBadge;

    // Bottom Navigation
    private LinearLayout navHome;
    private LinearLayout navCommunity;
    private LinearLayout navSaved;
    private LinearLayout navProfile;

    // Session & Network
    private SessionManager sessionManager;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        sessionManager = new SessionManager(this);
        requestQueue = Volley.newRequestQueue(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        // Initial load
        loadDashboardData();
    }

    private void initViews() {
        greetingText = findViewById(R.id.greetingText);

        exploreStreamsCard = findViewById(R.id.exploreStreamsCard);
        entranceExamsCard = findViewById(R.id.entranceExamsCard);
        jobsCard = findViewById(R.id.jobsCard);
        pathGuidanceCard = findViewById(R.id.pathGuidanceCard);

        aiRoadmapCard = findViewById(R.id.aiRoadmapCard);
        careerRecommendationCard = findViewById(R.id.careerRecommendationCard);

        fabChat = findViewById(R.id.btnAI);

        notificationBellContainer = findViewById(R.id.notificationBellContainer);
        notificationBadge = findViewById(R.id.notificationBadge);

        avatarContainer = findViewById(R.id.avatarContainer);
        // Find ImageView inside avatarContainer if possible, or use ID if known from
        // XML
        // Ideally XML should have an ID for the ImageView. Assuming it might be
        // 'userAvatar'.
        // If not, we iterate or rely on 'avatarContainer' being the structure.
        // Let's assume there is an ImageView with id 'userProfileImage' or similar if
        // looking at XML.
        // Since I don't see XML, I will assume consistent naming or try to find by ID
        // if I added it.
        // Re-checking XML structure: typically it's an ImageView inside.
        // I will try to findById(R.id.userAvatar) assuming I added it or it exists.
        // User's previous XMLs had consistent IDs. I'll check if R.id.userAvatar exists
        // safely.
        userAvatar = findViewById(R.id.userAvatar);
        if (userAvatar == null) {
            // Fallback: try to find first ImageView child of avatarContainer if it's a
            // ViewGroup
            if (avatarContainer instanceof android.view.ViewGroup) {
                android.view.ViewGroup vg = (android.view.ViewGroup) avatarContainer;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    if (vg.getChildAt(i) instanceof ImageView) {
                        userAvatar = (ImageView) vg.getChildAt(i);
                        break;
                    }
                }
            }
        }

        navHome = findViewById(R.id.navHome);
        navCommunity = findViewById(R.id.navCommunity);
        navSaved = findViewById(R.id.navSaved);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupClickListeners() {
        if (exploreStreamsCard != null)
            exploreStreamsCard.setOnClickListener(
                    v -> startActivity(new Intent(HomePage.this, StreamsEducationLevelsPage.class)));
        if (entranceExamsCard != null)
            entranceExamsCard
                    .setOnClickListener(v -> startActivity(new Intent(HomePage.this, ExamsEducationLevelsPage.class)));
        if (jobsCard != null)
            jobsCard.setOnClickListener(v -> startActivity(new Intent(HomePage.this, JobsEducationLevelsPage.class)));
        if (pathGuidanceCard != null)
            pathGuidanceCard
                    .setOnClickListener(v -> startActivity(new Intent(HomePage.this, EducationLevelsPage.class)));

        if (aiRoadmapCard != null)
            aiRoadmapCard.setOnClickListener(v -> startActivity(new Intent(HomePage.this, SystemGenerateP1Page.class)));
        if (careerRecommendationCard != null)
            careerRecommendationCard
                    .setOnClickListener(v -> startActivity(new Intent(HomePage.this, RecommendationSplashPage.class)));

        if (fabChat != null)
            fabChat.setOnClickListener(v -> startActivity(new Intent(HomePage.this, AiAssistantPage.class)));
        if (notificationBellContainer != null)
            notificationBellContainer
                    .setOnClickListener(v -> startActivity(new Intent(HomePage.this, NotificationsPage.class)));

        if (avatarContainer != null) {
            avatarContainer.setOnClickListener(v -> startActivity(new Intent(HomePage.this, ProfilePage.class)));
        }

        if (navCommunity != null)
            navCommunity.setOnClickListener(v -> startActivity(new Intent(HomePage.this, ForumHomePage.class)));
        if (navSaved != null)
            navSaved.setOnClickListener(v -> startActivity(new Intent(HomePage.this, SavedRoadmapListPage.class)));
        if (navProfile != null)
            navProfile.setOnClickListener(v -> startActivity(new Intent(HomePage.this, ProfilePage.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        int userId = sessionManager.getUserId();
        if (userId <= 0)
            return;

        // 1. Unread Count
        loadUnreadCount(userId);

        // 2. Profile Data (Name + Image)
        // We can use get_profile.php to refresh name and image on Home Page
        loadProfileData(userId);
    }

    private void loadUnreadCount(int userId) {
        String url = ApiConfig.getBaseUrl() + "get_unread_count.php?user_id=" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    int count = response.optInt("unread_count", 0);
                    if (notificationBadge != null) {
                        if (count > 0) {
                            notificationBadge.setText(count > 9 ? "9+" : String.valueOf(count));
                            notificationBadge.setVisibility(View.VISIBLE);
                        } else {
                            notificationBadge.setVisibility(View.GONE);
                        }
                    }
                },
                error -> Log.e(TAG, "Error loading unread count", error));
        requestQueue.add(request);
    }

    private void loadProfileData(int userId) {
        String url = ApiConfig.getBaseUrl() + "get_profile.php?user_id=" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.optBoolean("status", false)) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            // Update Greeting
                            String fullName = data.optString("full_name", "");
                            if (fullName.isEmpty())
                                fullName = sessionManager.getUserName();
                            updateGreeting(fullName);

                            // Update Avatar
                            String imagePath = data.optString("profile_image", "");
                            if (userAvatar != null) {
                                if (!imagePath.isEmpty()) {
                                    String fullUrl = imagePath.startsWith("http") ? imagePath
                                            : ApiConfig.getBaseUrl() + imagePath;
                                    loadAvatar(fullUrl);
                                } else {
                                    userAvatar.setImageResource(R.drawable.ic_avatar);
                                }
                            }
                        }
                    }
                },
                error -> Log.e(TAG, "Error refreshing profile data", error));
        requestQueue.add(request);
    }

    private void updateGreeting(String fullName) {
        if (greetingText == null)
            return;
        String name = "User";
        if (fullName != null && !fullName.isEmpty()) {
            name = fullName.split(" ")[0];
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        greetingText.setText("Hi " + name + "!");
    }

    private void loadAvatar(String url) {
        ImageRequest imageRequest = new ImageRequest(url,
                bitmap -> {
                    if (userAvatar != null)
                        userAvatar.setImageBitmap(bitmap);
                },
                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                error -> Log.e(TAG, "Error loading avatar: " + error.getMessage()));
        requestQueue.add(imageRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
