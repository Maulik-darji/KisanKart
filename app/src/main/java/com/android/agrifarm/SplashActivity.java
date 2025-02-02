package com.android.agrifarm;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.agrifarm.MainActivity;
import com.android.agrifarm.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove default splash screen (window background)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);

        // Initialize ImageView and TextView
        ImageView logo = findViewById(R.id.logo);
        TextView appName = findViewById(R.id.textView);

        // Get screen width for dynamic animation
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Set the logo's initial position off-screen to the right
        logo.setTranslationX(screenWidth);

        // Animate logo from right to center
        ObjectAnimator logoAnimator = ObjectAnimator.ofFloat(
                logo,
                "translationX",
                screenWidth,  // Start position (off-screen to the right)
                0f           // End position (center of the screen)
        );
        logoAnimator.setDuration(1200);  // Set animation duration
        logoAnimator.setInterpolator(new android.view.animation.DecelerateInterpolator());  // Smooth transition
        logoAnimator.start();

        // Fade-in the app name (TextView)
        appName.setAlpha(0f);  // Initially invisible
        appName.animate()
                .alpha(1f)  // Fade-in to full opacity
                .setDuration(800)  // Duration of fade-in
                .setStartDelay(400)  // Delay the fade-in
                .start();

        // Listener to start MainActivity once the logo animation ends
        logoAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {}

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Navigate to MainActivity after animation ends
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();  // Close SplashActivity
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {}

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });
    }
}
