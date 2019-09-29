package com.example.parking;


import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

public class MainActivity extends AppCompatActivity {

    // Variable permettant de gérer la longueur
    // De l'écran de bienvenue

    private static int WELCOME_TIME = 2000;

    // Variable pour terminer l'activité une fois finie
    // Sans impact sur la transition entre les
    // 2 activités

    private boolean mShouldFinish = false;

    private TextView welcome_text;
    private Animation zoom_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Animation sur le texte de bienvenue
        // Animation zoom_out

        this.welcome_text = findViewById(R.id.welcomeText);
        this.zoom_out = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        this.welcome_text.setAnimation(this.zoom_out);


        // On implémente un thread pour attribuer une durée à l'activité de bienvenue
        // Une fois le temps écoulé l'activité login se lance

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);

                // On applique la transition sur Texte 'PARKING'
                // Entre les 2 activités

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(welcome_text, "welcomeTextTransition");
                ActivityOptions options = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(loginIntent, options.toBundle());
                    mShouldFinish = true;

                }
            }
        }, WELCOME_TIME);
    }

    /**
     * On modifie la méthode pour pemettre de terminer
     * Correctement l'activité
     */

    @Override
    public void onStop() {
        super.onStop();
        if(mShouldFinish)
            finish();
    }



}
