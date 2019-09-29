package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameET, passwordET;
    private LinearLayout form_menu;
    private Animation zoom_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.usernameET = findViewById(R.id.usernameEditText);
        this.passwordET = findViewById(R.id.passwordEditText);
        this.form_menu = findViewById(R.id.formMenu);

        this.zoom_in = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        this.form_menu.setAnimation(this.zoom_in);

    }


    /**
     * Si on clique sur le bouton de connexion
     * On implémente la classe 'Signin'
     * Qui va permettre d'aller vérifier dans la base de donnée
     * Si l'utilisateur peur se connecter
     * @param view
     */

    public void onLogin(View view) {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        new Signin(this).execute(username, password);

    }

}
