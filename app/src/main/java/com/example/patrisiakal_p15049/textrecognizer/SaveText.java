package com.example.patrisiakal_p15049.textrecognizer;

import java.util.Date;

public class SaveText {

    private String Text;
    private String Lang_id;
    private long TimeStamp;
    private int Sentiment;
    private String SentDescr;
    private int Mps,Mns; //most positive score, most negative score
    private String Mpw, Mnw; //most positive word, most negative word

    //Constructor which is called in MainActivity
    public SaveText(String Text, String Lang_id, Long timeStamp, Integer sentiment, String sentimentDescription, Integer mns, String mnw, Integer mps, String mpw) {
        this.Text = Text;
        this.Lang_id = Lang_id;
        this.TimeStamp = timeStamp;
        this.Sentiment = sentiment;
        this.SentDescr = sentimentDescription;
        this.Mns = mns;
        this.Mnw = mnw;
        this.Mps = mps;
        this.Mpw = mpw;
    }

    // Default constructor
    public SaveText() {
    }


    //Gets the values from the Database

    public String getText() { return Text; }

    public String getLang_id() { return Lang_id; }

    public long getTimeStamp() { return TimeStamp; }

    public int getSentiment() { return Sentiment; }

    public String getSentDescr() { return SentDescr; }

    public int getMns() { return Mns; }

    public String getMnw() { return Mnw; }

    public int getMps() { return Mps; }

    public String getMpw() { return Mpw; }

    //Sets the values

    public void setText(String Text) { this.Text = Text; }

    public void setLang_id(String Lang_id) { this.Lang_id = Lang_id; }

    public void setTimeStamp(long TimeStamp) { this.TimeStamp = TimeStamp; }

    public void setSentiment(int Sentiment) { this.Sentiment = Sentiment; }

    public void setSentDescr(String SentDescr) { this.SentDescr = SentDescr; }

    public void setMns(int Mns) { this.Mns = Mns; }

    public void setMnw(String Mnw) { this.Mnw = Mnw; }

    public void setMps(int Mps) { this.Mps = Mps; }

    public void setMpw(String Mpw) { this.Mpw = Mpw; }
}
