package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar myActionBar = getSupportActionBar();
        if ( myActionBar != null ) { // add back button
            myActionBar.setDisplayHomeAsUpEnabled(true);
        }
       /* RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
        EditText lineManagerEmail = findViewById(R.id.lineManagerEmail);
        lineManagerEmail.setEnabled(false);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.switch5:
                        lineManagerEmail.setEnabled(true);
                        lineManagerEmail.setInputType(InputType.TYPE_NULL);
                        lineManagerEmail.setFocusableInTouchMode(false);
                        Toast.makeText(null, "Switch status changed!", Toast.LENGTH_LONG).show();
                        break;
                    /*case R.id.switch5:
                        lineManagerEmail.setEnabled(false);
                        lineManagerEmail.setFocusableInTouchMode(false);
                    break;
                }
            }
        }); */
    }
}
