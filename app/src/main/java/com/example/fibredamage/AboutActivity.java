package com.example.fibredamage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class AboutActivity extends AppCompatActivity {
    //todo save instance state of previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar myActionBar = getSupportActionBar();
        if ( myActionBar != null ) { // add back button
            myActionBar.setDisplayHomeAsUpEnabled(true);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
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
