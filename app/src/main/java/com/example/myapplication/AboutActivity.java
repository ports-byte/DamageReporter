package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity that provides an interface for the user to report a bug if encountered.
 * Has a floating action button that will take them to an email app (boxer) where they can report their problem
 */
public class AboutActivity extends AppCompatActivity {
    //todo save instance state of previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar myActionBar = getSupportActionBar();
        if ( myActionBar != null ) { // add back button
            myActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (ContextCompat.checkSelfPermission(AboutActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AboutActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String[] recipients = {"ben.fortune@virginmedia.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Damage Reporter");
                    intent.putExtra(Intent.EXTRA_TEXT, "Describe your problem in detail:  ");
                    intent.setType("text/html");
                    startActivity(Intent.createChooser(intent, "Choose an app to send the feedback from (i.e. Boxer)"));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }
}
