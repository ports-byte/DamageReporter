package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.myapplication.Constants.*;


/**
 * DONE: Correct permissions added for URI access for download directory (removed strictmode)
 *       Added encryption, option to delete files after use and include an additional email to send to (line manager)
 *       Fixed email not being added
 *       Address doesn't add commas on a space
 *       Regenerates PDF if attempting to send again (i.e. sent to recipient but needs to send another or to additional people after PDF has been purged). Done by if(pdffile.exists()) createPDF
 *
 * todo: Correct the layout of the pdf so that words arent cut off at the end of the line
 * /

/** @author Ben Fortune (@ports) @v0.8
 * Main activity for the app (initial screen). Provides texts fields for users to input to as well as request use of the camera to take pictures of damaged cables
 */
public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String EXTRA_MESSAGE = "";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    ImageView imageView, imageView2, imageView3, imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Spinner spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.itemsArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        setupSharedPreferences();
    }

    /**
     * Setup preferences for use in the wider app, current settings only accessible from the main activity so makes sense to have here
     */
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        lineManagerEmail = sharedPreferences.getString("lineEmailText_key", "");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("encryptPDF_key")) {
            Constants.encryptPDF = sharedPreferences.getBoolean("encryptPDF_key", true);
        }
        if (key.equals("purgePDF_key")) {
            purgePDF = sharedPreferences.getBoolean("purgePDF_key", true);
            Toast.makeText(this, "purge: " + purgePDF, Toast.LENGTH_SHORT).show();
        }
        //todo if selecting/deselecting and changing the line manager email field, it saves the previous value. Need to update upon back button, ok button on dialog or next on main activity
        if (key.equals("lineEmailCheck_key")) {
            lineManagerEmailBool = sharedPreferences.getBoolean("lineEmailCheck_key", true);
            lineManagerEmail = sharedPreferences.getString("lineEmailText_key", "");
        }
    }

    /**
     * After clicking the floating action button, the capture is set to an image view based on how many pictures have been taken so far.
     * todo: allow user to select photo to remove and replace. Can only replace image4 as of currently
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (imageView != null && imageView2 != null && imageView3 != null) { //set photo 4
                    imageBitmap = (Bitmap) extras.get("data");
                    imageView4 = findViewById(R.id.imageView4);
                    imageView4.setImageBitmap(imageBitmap);
                    Constants.photoFinishBitmap4 = imageBitmap;
                } else if (imageView != null && imageView2 != null) { //set to photo 3
                    imageBitmap = (Bitmap) extras.get("data");
                    imageView3 = findViewById(R.id.imageView3);
                    imageView3.setImageBitmap(imageBitmap);
                    Constants.photoFinishBitmap3 = imageBitmap;
                } else if (imageView != null && imageView3 == null) { //set to photo 2
                    imageBitmap = (Bitmap) extras.get("data");
                    imageView2 = findViewById(R.id.imageView2);
                    imageView2.setImageBitmap(imageBitmap);
                    Constants.photoFinishBitmap2 = imageBitmap;
                } else if (imageView2 == null && imageView3 == null && imageView == null) { //set to photo 1
                    imageBitmap = (Bitmap) extras.get("data");
                    imageView = findViewById(R.id.imageView);
                    imageView.setImageBitmap(imageBitmap);
                    Constants.photoFinishBitmap = imageBitmap;
                } else {
                    Toast.makeText(this, "Photo capacity reached!", Toast.LENGTH_LONG).show();
                }
                // try to pass it to next activity - if it fails just save to a directory and grab it again
                //arr = imageBitmap.getNinePatchChunk();

            }
        } catch (Exception f) {
            Toast.makeText(this, "Error " + f, Toast.LENGTH_LONG).show();
        }
    }

    /** Verification
     * Ensures all fields have been filled out to prevent nullpointerexceptions and ensure adequate data is provided to the 3rd party (i.e. no missing address)
     * @param view
     */
    public void checkFields(View view) {
        EditText engineerEmail = findViewById(R.id.engineerEmail);
        EditText engineerName = findViewById(R.id.engineerName);
        EditText engineerDescription = findViewById(R.id.engineerDescription);
        EditText address = findViewById(R.id.address);

        //convert to string to check if the fields are empty
        String engEmail = engineerEmail.getText().toString();
        String engName = engineerName.getText().toString();
        String engDesc = engineerDescription.getText().toString();
        String engAddr = address.getText().toString();

        if (engEmail.matches("") || engName.matches("") || engDesc.matches("") || engAddr.matches("")) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_LONG).show();
        } else {
            if (imageView == null) {
                new AlertDialog.Builder(this)
                        .setTitle("No pictures attached!")
                        .setMessage("There aren't any pictures attached to this report. Are you sure you want to proceed?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendMessage();
                                noPictures = true;
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                noPictures = false;
                sendMessage();
            }
        }
    }

    /**
     * Sets up the handover to the next activity (DisplayMessageActivity), combining all text fields into an EXTRA_MESSAGE which can then be processed in the next one.
     */
    public void sendMessage() {
        // stress the importance of adding pictures
        //todo add spaces for readability on submit
        try {
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            EditText engineerEmail =findViewById(R.id.engineerEmail);
            EditText engineerName = findViewById(R.id.engineerName);
            EditText engineerDescription = findViewById(R.id.engineerDescription);
            EditText address = findViewById(R.id.address);
            Spinner incidentType = findViewById(R.id.spinner);
            String strEmail = engineerEmail.getText().toString();
            String strName = engineerName.getText().toString();
            String strAddrTemp = address.getText().toString(); // remove return characters (errors with font glyphs)
            String strAddr = strAddrTemp.replaceAll("(\\r|\\n|\\r\\n)+", ", "); // Backslash s indicates a space - backslash r and n indicate break characters
            String strType = incidentType.getSelectedItem().toString();
            String strDescTemp = engineerDescription.getText().toString();
            String strDesc = strDescTemp.replaceAll("(\\r|\\n|\\r\\n)+", ". "); // 60 characters till line break needed.
            //todo needs to be put into a string array (split every 60 characters)
            String message = strName + ";" + strEmail + ";" + strType + ";" + strDesc + ";" + strAddr;
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } catch (Exception f) {
            Toast.makeText(this, "Error: " + f, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * todo
     * @param view
     */
    public void selectPhoto(View view) {

    }

    /**
     * Handles menu options
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Toast.makeText(this, "Attempting open of id: " + id, Toast.LENGTH_LONG);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Attempting open of id: " + id, Toast.LENGTH_LONG);
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        if (id == R.id.aboutMenuIttem) {
            Toast.makeText(this, "Attempting open of id: " + id, Toast.LENGTH_LONG);
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
