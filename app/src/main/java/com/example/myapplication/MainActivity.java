package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import static com.example.myapplication.Constants.noPictures;

/**
 * DONE: Correct permissions added for URI access for download folder (removed strictmode)
 *       Added encryption option
 *       Removed redundant stuff
 *
 * todo: Correct the layout of the pdf so that words arent cut off at the end of the line
 */

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String dmgDescription = "Description of the damage";
    Bitmap imageBitmap, imageBitmap2, imageBitmap3;
    ImageView imageView, imageView2, imageView3, imageView4;
    byte[] arr;
    int c = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.itemsArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // This is where we should replace with the camera action
        // make sure the send button is not opaque before pictures are taken
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                   /*  Snackbar.make(view, "Launching camera", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();*/
                }
            }
        });
    }

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
                } else if (imageView2 == null && imageView3 == null && imageView == null) {
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
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMenuIttem:
                AboutActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/
    public void checkFields(View view) {
        EditText engineerEmail = (EditText) findViewById(R.id.engineerEmail);
        EditText engineerName = (EditText) findViewById(R.id.engineerName);
        EditText engineerDescription = (EditText) findViewById(R.id.engineerDescription);
        EditText address = (EditText) findViewById(R.id.address);
        Spinner incidentType = (Spinner) findViewById(R.id.spinner);

        //convert to string to check if the fields are empty
        String engEmail = engineerEmail.getText().toString();
        String engName = engineerName.getText().toString();
        String engDesc = engineerDescription.getText().toString();
        String engAddr = address.getText().toString();

        if (engEmail.matches("") || engName.matches("") || engDesc.matches("") || engAddr.matches("")) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_LONG).show();
        } else {
            if (imageView == null) {
                //Toast.makeText(this, "No pictures attached! Are you sure you want to continue", Toast.LENGTH_LONG).show();
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

    public void sendMessage() {
        // stress the importance of adding pictures
        //todo add spaces for readability on submit
        try {
            Intent intent = new Intent(this, DisplayMessageActivity.class);
            EditText engineerEmail = (EditText) findViewById(R.id.engineerEmail);
            EditText engineerName = (EditText) findViewById(R.id.engineerName);
            EditText engineerDescription = (EditText) findViewById(R.id.engineerDescription);
            EditText address = (EditText) findViewById(R.id.address);
            Spinner incidentType = (Spinner) findViewById(R.id.spinner);
            String strEmail = engineerEmail.getText().toString();
            String strName = engineerName.getText().toString();
            String strAddrTemp = address.getText().toString(); // remove return characters (errors with font glyphs)
            String strAddr = strAddrTemp.replaceAll("(\\r|\\n|\\r\\n|\\s)+", ", ");
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

    public void selectPhoto(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        if (id == R.id.aboutMenuIttem) {
            Toast.makeText(this, "Attempting open of id: " + id, Toast.LENGTH_LONG);
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
