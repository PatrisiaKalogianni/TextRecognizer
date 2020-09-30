package com.example.patrisiakal_p15049.textrecognizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.IdentifiedLanguage;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap bitmap;
    FirebaseVisionImage fimage;
    long timestamp;
    String id, tvStrings, lowerCaseWord ;
    int wordScore;
    int method = 0;
    Uri uriData;
    EditText editText, multiLineText;
    TextView textView;

    String monthScans, weekScans;
    List <String> wordSet;

    public static List<String> words;
    public static List<Integer> scores;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference textRef, showData;

    int most_positive_score;
    String most_pos_word;

    int most_negative_score;
    String most_neg_word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to the image view.
        imageView = findViewById(R.id.imageView);

        multiLineText = findViewById(R.id.editText2);
        textView = findViewById(R.id.textView);


        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        textRef = firebaseDatabase.getReference("Text");
        showData = firebaseDatabase.getReference("Text").child(mAuth.getCurrentUser().getUid());

        monthScans = "";
        weekScans = "";

        words = new ArrayList<>();
        scores = new ArrayList<>();

        // Fill 2 lists with the words and their scores, extracted from the afinn-111 library.
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("afinn-111.txt"), "UTF-8"));

            // Read file. Loop through the file until the end of the reading.
            String mLine;
            int nLine = 0;
            while ((mLine = reader.readLine()) != null) {
                String[] line = mLine.split(",");
                words.add(line[0]);
                scores.add(Integer.parseInt(line[1]));
                Log.v("Line " + nLine ,"Word: " + words.get(nLine) + "Score: " +scores.get(nLine));
                nLine++;
            }
        } catch (IOException e) {
            Log.e("MainActivity","Error Message " + e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("MainActivity","Error Message " + e);
                }
            }
        }

    }



    /// ---------- A c t i v a t e   M e n u   A c c o r d i n g  t o   U s e r ----------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if(mAuth.getCurrentUser().getEmail().equals("admin@admin.com") ){
            inflater.inflate(R.menu.adminn_menu, menu);
        }
        else{
            inflater.inflate(R.menu.main_menu, menu);
        }

        return true;
    }



    /// ---------- D r o p - d o w n   M e n u   O p t i o n s ----------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                AlertDialog alertDialog4 = new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Statistics")
                        .setMessage("Do you wish to be transferred to the Statistics Page? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Open new activity.
                                Intent statInetent = new Intent(MainActivity.this, StatisticsActivity.class);
                                startActivity(statInetent);
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
                return true;
            case R.id.History:
                return true;
            case R.id.help:
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setTitle("Help")
                        .setMessage("1. Click on the camera or gallery button to take a photo or import one from your device.\n\n2. Then click the scanner button to scan for text.\n\n(!) If no text is found you will be informed.\n\n(!) Make sure the picture is the right way up.\n\n - To view the history of your past scans select History from the menu and then select the desired time frame.\n\n - To view the online help select Help from the menu.\n\n - To sign out from your account select Sign Out from the menu.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Set what would happen when positive button is clicked.
                                dialogInterface.cancel();
                            }
                        })
                        .show();
                return true;
            case R.id.subitem1: // past week
                // Gets the timestamp of the date oe week before the current date.
                long msWeek = Calendar.getInstance().getTimeInMillis() - 604800000;     // 1 week = 604800000 ms


                // Attach a listener to read the data at reference.
                showData.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        SaveText savedText = dataSnapshot.getValue(SaveText.class);

                        if( savedText.getTimeStamp() >= msWeek){
                            // Retrieve the data from the database and form the format for the listView.
                            weekScans += "---------- "+DateFormat.format("dd-MM-yyyy (HH:mm:ss)", savedText.getTimeStamp()) +" ----------\n" + savedText.getText()+"---------------------------------------------------------\n\n";
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                showAlertWindow(weekScans,"Past week's scans :");

                return true;
            case R.id.subitem2: //past month

                // 1 month = 2628000000 ms, the L at the end of the number symbolises it is type long and not an integer.
                long msMonth = Calendar.getInstance().getTimeInMillis() - 26280000000L;



                // Attach a listener to read the data at reference.
                showData.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        SaveText savedText = dataSnapshot.getValue(SaveText.class);

                        if( savedText.getTimeStamp() >= msMonth){
                            //Retrieve the data from the database and form the format for the listView
                            monthScans += "---------- " + DateFormat.format("dd-MM-yyyy (HH:mm:ss)", savedText.getTimeStamp()) + " ----------\n" + savedText.getText() + "---------------------------------------------------------\n\n";
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                showAlertWindow(monthScans,"Past month's scans :");

                return true;
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, LogInActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /// ---------- T o   I m p o r t   P i c    f r o m   C a m e r a --------- (Take Photo Button)
    public void takePic(View view){
        //open camera to take photo
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);

        method = 1 ;
    }


    /// ---------- T o   I m p o r t   P i c    f r o m   G a l l e r y ---------- (Import Photo Button)
    public void importPic(View view) {
        // Invoke the image gallery using implicit intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // Where do we vent to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
        String pictureDirectoryPath = pictureDirectory.getPath();

        // Get a URI () representation.
        uriData = Uri.parse(pictureDirectoryPath);

        // Set data and type. Get all image types.
        photoPickerIntent.setDataAndType(uriData, "image/*");

        // Invoke activity and get something back from it.
        startActivityForResult(photoPickerIntent, 1);

        method = 2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Determine which activity the result comes from.
        if (resultCode == RESULT_OK){
            // Everything processed successfully.

            // Photo from camera.
            if(requestCode == 0){

                // Get photo from camera and turn into bitmap 'form'.
                bitmap = (Bitmap)data.getExtras().get("data");

                // Show photo in imageView.
                imageView.setImageBitmap(bitmap);

                // Make background clear, A = 0.
                imageView.setBackgroundColor(Color.argb(0, 255,255,255));


            }
            // Photo from the image gallery.
            else if(requestCode == 1){

                // The address or the image on the SD card.
                Uri imageUri = data.getData();

                // Declares a stream to read the image data from the SD card.
                InputStream inputStream;

                // Gets an input stream, based on the Uri of the image.
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // Get a bitmap from the stream.
                    bitmap = BitmapFactory.decodeStream(inputStream);

                    // Show photo in imageView.
                    imageView.setImageBitmap(bitmap);
                    // Make background clear.
                    imageView.setBackgroundColor(Color.argb(0, 255,255,255));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /// ---------- C r e a t e   T o a s t   M e s s a g e ----------
    public void showMessage(String s){
        Toast.makeText(this," " + s , Toast.LENGTH_SHORT).show();
    }


    /// ---------- C r e a t e   A l e r t   M e s s a g e   W i n d o w ----------
    public void showAlertWindow(String message, String title){

        AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_menu_recent_history)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    /// ---------- D e t e c t   T e x t   A r e a s ---------- ( Scanner Button )
    public void detectText(View v){
        if(method ==1 || method==2){

            // Get FirebaseVisonImage version of image.
            if (method ==1){                 // Image from camera.
                // Image represented by the Bitmap object must be upright.
                // Pass the bitmap into the constructor with the  VisionImage object.
                // This image can be used for on divice and cloud api detectors.
                fimage = FirebaseVisionImage.fromBitmap(bitmap);
            }
            else if(method ==2){             // Image from file.

                fimage = FirebaseVisionImage.fromBitmap(bitmap);
            }

            // Get an instance of FirebaseVisionTextRecognizer for the on-device model.
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            detector.processImage(fimage)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    processTextRecognitionResult(firebaseVisionText);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });


        }
        else{                             // No image has been selected.
            Toast.makeText(this,"Please select or take a picture!", Toast.LENGTH_SHORT).show();
        }

    }

    /// ---------- T e x t   R e c o g n i t i o n ----------
    private void processTextRecognitionResult(FirebaseVisionText ftext){
        int sentimentScore = 0;

        // Get logged in user's id.
        if(mAuth.getCurrentUser() != null){
            id = mAuth.getCurrentUser().getUid();
        }else{
            showMessage("Unable to detect user ID");
            // Get device id.
            id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        // Get timestamp.
        timestamp = new Date().getTime();

        // Get text blocks.
        List<FirebaseVisionText.TextBlock> blocks = ftext.getTextBlocks();

        if (blocks.size() == 0){
            Toast.makeText(this,"No text found", Toast.LENGTH_SHORT).show();
            multiLineText.setText("");
            return;
        }

        multiLineText.setText("");

        for (int i = 0; i< blocks.size(); i++){

            // Recognize the language of each block.
            String blockLanguages = getLang(blocks.get(i).getText());

            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();

            for(int j = 0; j < lines.size(); j++){
                String lineLanguages = getLang(lines.get(j).getText());

                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();


                for(int k = 0; k < elements.size(); k++){

                    String elementLanguages = getLang(elements.get(k).getText());
                    multiLineText.append(elements.get(k).getText()+" ");

                }
            }

            // Add 2 new lines at the end of each block of text.
            multiLineText.append(" \n\n");

        }

        tvStrings = multiLineText.getText().toString();

        // Split a string in java on any non-alphanumeric characters.
        wordSet = Arrays.asList((tvStrings.split("[^\\p{L}0-9]+")));

        sentimentScore = sentimentAnalysis(wordSet);

        String sentDescription;
        if (sentimentScore > 1){
            sentDescription = "Positive";
        }
        else if(sentimentScore <= 1 && sentimentScore >=0){
            sentDescription = "Neutral";
        }
        else{
            sentDescription = "Negative";
        }

        String key = textRef.push().getKey();

        // Get SaveText class object and use the values to update the database.
        SaveText saveText = new SaveText(tvStrings, "en", timestamp, sentimentScore, sentDescription, most_negative_score, most_neg_word, most_positive_score, most_pos_word);
        textRef.child(id).child(key).setValue(saveText);

    }

    /// ---------- S e n t i m e n t   A n a l y s i s   u s i n g   afinn-111.txt ----------
    public int sentimentAnalysis (List<String> wordList){

        int totalScore = 0;
        most_positive_score = 0;
        most_negative_score = 0;

        for (int w = 0; w < wordList.size(); w++){

            // Converts to lower case.
            lowerCaseWord = wordList.get(w).toLowerCase();

            // Finds the score of the word.
            for (int i=0; i< words.size(); i++){
                if (lowerCaseWord.equals(words.get(i))){

                    wordScore = scores.get(i);

                    if(wordScore > most_positive_score){
                        most_positive_score = wordScore;
                        most_pos_word = lowerCaseWord;
                    }
                    else if(wordScore == most_positive_score){
                        most_pos_word += ","+lowerCaseWord;
                    }

                    if(wordScore < most_negative_score){
                        most_negative_score = wordScore;
                        most_neg_word = lowerCaseWord;
                    }
                    else if(wordScore == most_negative_score){
                        most_neg_word += ","+lowerCaseWord;
                    }

                    break;
                }
                else if (!lowerCaseWord.equals(words.get(i))){
                    wordScore = 0;
                }
            }

            totalScore += wordScore;
        }

        return totalScore;
    }


    /// ---------- G e t   L a n g u a g e   o f   T e x t ----------
    public String getLang(String text){

        final String[] langCd = {""};
        FirebaseLanguageIdentification languageIdentifier =
                FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode != "und") {
                                    Log.i("info tag", "Language: " + languageCode);
                                    langCd[0] = languageCode;

                                } else {
                                    Log.i("info tag", "Can't identify language.");
                                    showMessage("Can't identify language.");
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                            }
                        });
        return langCd[0];

    }

}
