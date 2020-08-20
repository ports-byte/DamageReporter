package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.snackbar.Snackbar;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.myapplication.Constants.noPictures;

public class DisplayMessageActivity extends AppCompatActivity {
    File root;
    AssetManager assetManager;
    TextView tv;
    ArrayList<String> items = new ArrayList<String>(5);
    String finalItemsString = null;
    String pdfPass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        //get intent that started this activity and extra the string (extra message)
        Intent intent = getIntent();
        finalItemsString = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        /**arr1 = intent.getByteArrayExtra("imageView");
         map = BitmapFactory.decodeByteArray(arr1,0, arr1.length);
         imageView.setImageBitmap(map);*/

        //quickly generate a password to be used
        pdfPass = getSaltString();

        //capture the layout textview and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText("You are about to submit the following: " + finalItemsString);
        View v = null;
        setup(v);
    }

    public void onClick(View v) {
        Date date = new Date();
        SimpleDateFormat sdfFile = new SimpleDateFormat("ddMMyy");
        try {
            // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
            if (ContextCompat.checkSelfPermission(DisplayMessageActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(DisplayMessageActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

        Snackbar.make(v, "Attempting to open PDF", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/VMGeneratedDoc" + sdfFile.format(date) + ".pdf");  // -> filename = maven.pdf
        Uri path = FileProvider.getUriForFile(DisplayMessageActivity.this, "com.example.myapplication.provider", pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(pdfIntent);
        }catch(Exception e){
            Toast.makeText(this, "No application available to view PDF - error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void send(View v) {
        try {
            // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
            if (ContextCompat.checkSelfPermission(DisplayMessageActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(DisplayMessageActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } catch (Exception d) {
            Toast.makeText(this, "Couldn't access local files. Error: " + d, Toast.LENGTH_LONG).show();
        }
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
            String filename = "VMGeneratedDoc" + sdf.format(date) + ".pdf";
            //String filename = "VMGeneratedDoc.pdf";
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            assetManager = getAssets();
            SimpleDateFormat sdfFile = new SimpleDateFormat("ddMMyy");
            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/VMGeneratedDoc" + sdfFile.format(date) + ".pdf");  // -> filename = maven.pdf
            Uri path = FileProvider.getUriForFile(DisplayMessageActivity.this, "com.example.myapplication.provider", pdfFile);


            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String[] recipients = {"xyz@virginmedia.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Report on fibre damage");
            intent.putExtra(Intent.EXTRA_TEXT, "Please see the attached PDF for a fibre damage report. \n\n Filename: " + filename + "\n\n  Password: " + pdfPass);
            intent.putExtra(Intent.EXTRA_CC, "ccAddress@virginmedia.co.uk"); // doesnt work
            intent.putExtra(Intent.EXTRA_STREAM, path);
            intent.setType("text/html");
            startActivity(Intent.createChooser(intent, "Choose an app to send the report from (i.e. Boxer)"));
        } catch (Exception f) {
            Toast.makeText(this, "Couldn't compose a mail. Error: " + f, Toast.LENGTH_SHORT).show();
        }
    }

    // create pdf starts here
    //setup
    private void setup(View v) {
        //enable android style asset loading
        PDFBoxResourceLoader.init(getApplicationContext());
        // Find the root of the external storage.
        root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        assetManager = getAssets();
        tv = (TextView) findViewById(R.id.tv);

        // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
        if (ContextCompat.checkSelfPermission(DisplayMessageActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(DisplayMessageActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        try {
            createPDF(v);
        } catch (Exception g) {
            System.out.println("Couldn't create a pdf");
        }
    }

    // creates pdf from scratch and save to a file
    public void createPDF(View v) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        try {
            items = new ArrayList<>(Arrays.asList(finalItemsString.split(";")));
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            //wrap text for pdf formatting - todo - read until whitespace until you newline
            ArrayList<String> itemDescArray = new ArrayList<>();
            int index=0;
            if (items.get(3).length()>60) {
                while (index<items.get(3).length()) {
                    itemDescArray.add(items.get(3).substring(index, Math.min(index + 80, items.get(3).length())));
                    index +=80;
                }
            }

            PDFont font = PDType1Font.HELVETICA;
            PDPageContentStream contentStream;

            // Define a content stream for adding to the PDF
            contentStream = new PDPageContentStream(document, page);
            /* Load in the taken photos */
            InputStream in = assetManager.open("vmlogo.jpg");
            //draw vm logo
            PDImageXObject ximage = JPEGFactory.createFromStream(document, in);
            contentStream.drawImage(ximage, 240, 550);
            /*File vmLogoImg = new File("/assets/vmTransEdit.png");
            if (vmLogoImg.exists()) {
                Bitmap vmLogoBitmap = BitmapFactory.decodeFile(vmLogoImg.getAbsolutePath());
                PDImageXObject ximage = LosslessFactory.createFromImage(document, vmLogoBitmap);
                contentStream.drawImage(ximage, 240, 550);
            }*/
            //todo add watermark
           /* HashMap<Integer, String> overlayGuide = new HashMap<Integer, String>();
            in = assetManager.open("vmtrans.jpg");
            for(int i=0; i<document.getNumberOfPages(); i++){
                overlayGuide.put(i+1, "/assets/vmtrans.jpg");
            }
            Overlay overlay = new Overlay();
            overlay.setInputPDF(document);
            overlay.setOverlayPosition(Overlay.Position.BACKGROUND);
            overlay.overlay(overlayGuide);*/


            // red rectangle
            contentStream.setNonStrokingColor(204,0,0);
            contentStream.addRect(0, 700, 1000, 5);
            contentStream.fill();
            contentStream.beginText();
            contentStream.setNonStrokingColor(0, 0, 0);
            contentStream.setFont(font, 32);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(0, 735);
            contentStream.showText("Virgin Media | Damage report | " + sdf.format(date));

            //here we'll need to have the subject (i.e. malicious/rat damage)
            contentStream.newLineAtOffset(10, -170);
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLine();
            contentStream.setFont(font, 15);
            contentStream.setNonStrokingColor(0, 0, 0);
            contentStream.showText("Engineer name: " + items.get(0) + " (" + items.get(1) + ")"); // name & email
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Incident address: " + items.get(4)); // Address
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Incident type: " + items.get(2));
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLine();
            if (items.get(3).length()<60) {
                contentStream.showText("Incident description: " + items.get(3));
            } else {
                contentStream.showText("Incident description: ");
                contentStream.newLine();
                for (int i=0; i<itemDescArray.size(); i++) {
                    contentStream.showText(itemDescArray.get(i));
                    contentStream.newLine();
                }
            }
            contentStream.endText();
            contentStream.setNonStrokingColor(204,0,0);
            contentStream.addRect(0, 250, 1000, 5);
            contentStream.fill();

            //
            if (noPictures != true) {
                Bitmap image = Constants.photoFinishBitmap;
                Bitmap image2 = Constants.photoFinishBitmap2;
                Bitmap image3 = Constants.photoFinishBitmap3;
                Bitmap image4 = Constants.photoFinishBitmap4;

                PDImageXObject ximage1 = JPEGFactory.createFromImage(document, image);
                contentStream.drawImage(ximage1, 20, 50, ximage1.getWidth(), ximage1.getHeight());
                if (image2 != null) {
                    PDImageXObject ximage2 = JPEGFactory.createFromImage(document, image2);
                    contentStream.drawImage(ximage2, ximage1.getWidth() + 40, 50, ximage2.getWidth(), ximage2.getHeight());
                }
                if (image3 != null) {
                    PDImageXObject ximage3 = JPEGFactory.createFromImage(document, image3);
                    contentStream.drawImage(ximage3, ximage1.getWidth() * 2 + 60, 50, ximage3.getWidth(), ximage3.getHeight());
                }
                if (image4 != null) {
                    PDImageXObject ximage4 = JPEGFactory.createFromImage(document, image4);
                    contentStream.drawImage(ximage4, ximage1.getWidth() * 3 + 80, 50, ximage4.getWidth(), ximage4.getHeight());
                }
            }

            contentStream.close();

            /**Settings: enable encryption
            int keyLength = 128;
            AccessPermission ap = new AccessPermission();

            StandardProtectionPolicy spp = new  StandardProtectionPolicy(pdfPass, pdfPass, ap);
            spp.setEncryptionKeyLength(keyLength);
            spp.setPermissions(ap);
            document.protect(spp);*/

            //Save file with todays date
            SimpleDateFormat sdfFile = new SimpleDateFormat("ddMMyy");
            String path = root.getAbsolutePath() + "/VMGeneratedDoc" + sdfFile.format(date) + ".pdf";
            document.save(path);
            document.close();
            tv.setText("Successfully generated PDF");
        } catch (Exception e) {
            Toast.makeText(this, "Couldn't generate a pdf. Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public ArrayList<String> getItems() {
        this.getItems();
        return items;
    }
}
