package com.example.patrisiakal_p15049.textrecognizer;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Calendar calendar;
    DatePickerDialog dpd;
    TextView fromTv, untilTv, statsTv;
    EditText wordResult, wordSearch, dateResult, mostPos, mostNeg, mostFreqPosED;
    Button hideWordBtn, hideDateBtn;
    int day, month, year;
    int pos, neg, neut, countTotalScans, max, min;
    boolean hiddenWord, hiddenDate;

    Long cal1, cal2;

    FirebaseAuth mAuth;
    DatabaseReference userRef;

    String datePicker;

    public static List<String>  posFound, negFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        fromTv = findViewById(R.id.textView9);
        untilTv = findViewById(R.id.textView5);

        statsTv = findViewById(R.id.textView6);

        wordResult = findViewById(R.id.editText4);
        wordSearch = findViewById(R.id.editText3);
        dateResult = findViewById(R.id.editText5);

        mostPos = findViewById(R.id.editText6);
        mostNeg = findViewById(R.id.editText7);

        hideWordBtn = findViewById(R.id.button7);
        hideDateBtn = findViewById(R.id.button8);

        hiddenWord = false;
        hiddenDate = false;

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH); // Note: zero based!
        year = calendar.get(Calendar.YEAR);

        mAuth = FirebaseAuth.getInstance();

        posFound = new ArrayList<>();
        posFound.clear();
        negFound = new ArrayList<>();
        negFound.clear();

        userRef = FirebaseDatabase.getInstance().getReference("Text");

        pos = 0;
        neg = 0;
        neut = 0;
        countTotalScans = 0;

        min = 0;
        max = 1;

        // Sentiment Analysis Statistics AND most positive(+) and negative(-) words
        userRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    SaveText savedText = ds.getValue(SaveText.class);

                    if( savedText.getSentDescr().equals("Positive") ){
                        pos += 1;
                        countTotalScans += 1;
                    }
                    else if (savedText.getSentDescr().equals("Neutral")){
                        neut += 1;
                        countTotalScans += 1;
                    }
                    else if (savedText.getSentDescr().equals("Negative")){
                        neg += 1;
                        countTotalScans += 1;
                    }

                    // Most Positive Words.
                    if (savedText.getMps() > 1){

                        // If there are more than 1 words with the same score.
                        if(savedText.getMpw().contains(",")){

                            String[] posWords = savedText.getMpw().split(",");

                            // Check for dublicates.
                            for(int w = 0; w < posWords.length; w++){

                                if( !(posFound.contains(posWords[w]))){

                                    if( savedText.getMps() > max ){
                                        max = savedText.getMps();
                                        posFound.clear();
                                        posFound.add(posWords[w]);
                                    }
                                    else if(savedText.getMps() == max){
                                        posFound.add(posWords[w]);
                                    }

                                }

                            }

                        }
                        else {
                            // Check for dublicates.
                            if ( !(posFound.contains(savedText.getMpw()))) {
                                if( savedText.getMps() > max ){
                                    max = savedText.getMps();
                                    posFound.clear();
                                    posFound.add(savedText.getMpw());
                                }
                                else if(savedText.getMps() == max){
                                    posFound.add(savedText.getMpw());
                                }
                            }
                        }

                    }
                    // Most Negative Words.
                    if (savedText.getMns() < 0){
                        // If there are more than 1 words with the same score.
                        if(savedText.getMnw().contains(",")){

                            String[] negWords = savedText.getMpw().split(",");

                            // Check for dublicates.
                            for(int w = 0; w < negWords.length; w++){

                                if(!negFound.contains(negWords[w])){
                                    if( savedText.getMns() < min ){
                                        min = savedText.getMns();
                                        negFound.clear();
                                        negFound.add(negWords[w]);
                                    }
                                    else if(savedText.getMps() == min){
                                        negFound.add(negWords[w]);
                                    }
                                }

                            }

                        }
                        else{
                            // Check for dublicates.
                            if (! negFound.contains( savedText.getMnw() ) ){
                                if( savedText.getMns() < min ){
                                    min = savedText.getMns();
                                    negFound.clear();
                                    negFound.add( savedText.getMnw());
                                }
                                else if(savedText.getMps() == min){
                                    negFound.add( savedText.getMnw());
                                }
                            }
                        }
                    }


                }

                // Show results of most negative words.
                for(int w = 0; w < negFound.size(); w++){
                    if (!mostNeg.getText().toString().contains(negFound.get(w))){
                        mostNeg.append( negFound.get(w) + "\n");
                    }
                }

                // Show results of most positive words.
                for(int w = 0; w < posFound.size(); w++){
                    if (!mostPos.getText().toString().contains(posFound.get(w))){
                        mostPos.append( posFound.get(w) + "\n");
                    }
                }

                statsTv.setText( "Out of " + countTotalScans + " scans:\n\n" + pos*100/countTotalScans +"%   scanned text is Positive\n" + neut*100/countTotalScans +"%   scanned text is Neutral\n" + neg*100/countTotalScans +"%   scanned text is Negative\n" );

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


    }


    /// ---------- C r e a t e   T o a s t   M e s s a g e ----------
    public void showMessage(String s){
        Toast.makeText(this,"" + s , Toast.LENGTH_SHORT).show();
    }


    /// ---------- S e a r c h   f o r   W o r d   I n   D a t a b a s e ----------
    public void searchWord(View v){
        String word = wordSearch.getText().toString();
        // Clear previous search.
        wordResult.setText("");

        if( !word.equals("") ){
            // Attach a listener to read the data at reference
            userRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {

                        SaveText savedText = ds.getValue(SaveText.class);

                        //Retrieve the data from the database and add to EditText
                        if( savedText.getText().contains(word) ){

                            // Change color of searched word in text.
                            SpannableString yellowWord=  new SpannableString(word);
                            yellowWord.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8C08")), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            wordResult.append("------------------- "+ DateFormat.format("dd-MM-yyyy", savedText.getTimeStamp()) +" ------------------\n\n");

                            String[] splittedSentence = savedText.getText().split(word);
                            for ( int i = 0; i < splittedSentence.length; i ++)
                            {
                                //arrayList3.add
                                if ( i == 0){
                                    wordResult.append( splittedSentence[i] );
                                }else{
                                    wordResult.append( yellowWord );
                                    wordResult.append( splittedSentence[i] );
                                }
                            }
                            wordResult.append("---------------------------------------------------------\n\n");
                        }
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

            // Show 'Hide' button.
            hideWordBtn.setVisibility(View.VISIBLE);
            hideWordBtn.setText("Hide");
            wordResult.setVisibility(View.VISIBLE);
            hiddenWord = false;

        }
        else if(word.equals("")){
            showMessage("Please type a word!");
        }

    }


    /// ---------- H i d e / S h o w   E d i t T e x t   B u t t o n ----------
    public void hideWordSearch(View v){

        // After click hides results.
        if( !hiddenWord ){
            hideWordBtn.setText("Show");
            wordResult.setVisibility(View.GONE);
            hiddenWord = true;
        }

        // After click shows results.
        else {
            hideWordBtn.setText("Hide");
            wordResult.setVisibility(View.VISIBLE);
            hiddenWord = false;
        }

    }


    /// ---------- S e a r c h   i n   D a t a b a s e   b y   D a t e ----------
    public void searchDate(View v){

        if (fromTv.getText().toString().equals("From: -") || untilTv.getText().toString().equals("Until: -")){

            showMessage("Please select dates! ");

        }
        else{

            if (cal1 < cal2){

                // Clear previous search.
                dateResult.setText("");

                userRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {

                            SaveText savedText = ds.getValue(SaveText.class);

                            // Retrieve the data from the database and add to EditText.
                            if( savedText.getTimeStamp()>= cal1 && savedText.getTimeStamp() <= cal2){

                                dateResult.append("------------------- "+ DateFormat.format("dd-MM-yyyy", savedText.getTimeStamp()) +" ------------------\n\n" + savedText.getText()+"---------------------------------------------------------\n\n");

                            }
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

                wordSearch.requestFocus();

                //Show 'Hide' button.
                hideDateBtn.setVisibility(View.VISIBLE);
                hideDateBtn.setText("Hide");
                dateResult.setVisibility(View.VISIBLE);
                hiddenDate = false;

            }else{
                showMessage("Please make sure the date you are searching from is not more recent than the date you are searching until!");
            }
        }

    }


    /// ---------- H i d e / S h o w   E d i t T e x t   B u t t o n ----------
    public void hideDateSearch(View v){

        // After click hides results.
        if( !hiddenDate ){
            hideDateBtn.setText("Show");
            dateResult.setVisibility(View.GONE);
            hiddenDate = true;
        }

        // After click shows results.
        else {
            hideDateBtn.setText("Hide");
            dateResult.setVisibility(View.VISIBLE);
            hiddenDate = false;
        }

    }


    /// ---------- G e t   D a t e   f r o m   1 s t   C a l e n d a r ----------
    public  void calendar1(View v){


        dpd = new DatePickerDialog(this, this, year, month, day);
        dpd.show();

        datePicker = "from";
    }

    /// ---------- O p e n   D a t e   P i c k e r ----------
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){

        if (datePicker.equals("from")){

            fromTv.setText("From : " + dayOfMonth + " / " + (month+1) + " / " + year);

            String date_1 = dayOfMonth + "/" + (month+1) + "/" + year;
            Date date1;

            try{
                // String to date format.
                date1=new SimpleDateFormat("dd/MM/yyyy").parse(date_1);

                // Divide getTime() by 1000 to get a timestamp from Date() ------- NOT
                cal1 = date1.getTime();

            }catch (ParseException e){
                showMessage("An error has occured");
            }
        }
        else if (datePicker.equals("until")){

            untilTv.setText("Until : " + dayOfMonth + " / " + (month+1) + " / " + year);

            String date_2 = dayOfMonth + "/" + (month+1) + "/" + year;
            Date date2;
            try {
                date2 = new SimpleDateFormat("dd/MM/yyyy").parse(date_2);//new Date( year, month+1, dayOfMonth);
                cal2 = date2.getTime();
            } catch (ParseException e) {
                showMessage("An error has occured");
            }

        }
    }


    /// ---------- G e t   D a t e   f r o m   2 n d   C a l e n d a r ----------
    public  void calendar2(View v){
        dpd = new DatePickerDialog(this, this, year, month, day);
        dpd.show();
        datePicker = "until";
    }

}
