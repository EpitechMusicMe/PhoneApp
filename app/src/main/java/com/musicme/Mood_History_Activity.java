package com.musicme;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Mood_History_Activity extends AppCompatActivity {

    PieChart pieChart;
    static int hap_cnt=0,exc_cnt=0,ang_cnt=0,ene_cnt=0,neu_cnt=0,tir_cnt=0,ner_cnt=0,bor_cnt=0,sad_cnt=0;

    public static void set_count(String emotion, int emotion_cnt){
        if(emotion == "hap"){ hap_cnt = emotion_cnt; }
        else if(emotion == "exc"){ exc_cnt = emotion_cnt; }
        else if(emotion == "ang"){ ang_cnt = emotion_cnt; }
        else if(emotion == "ene"){ ene_cnt = emotion_cnt; }
        else if(emotion == "neu"){ neu_cnt = emotion_cnt; }
        else if(emotion == "tir"){ tir_cnt = emotion_cnt; }
        else if(emotion == "ner"){ ner_cnt = emotion_cnt; }
        else if(emotion == "bor"){ bor_cnt = emotion_cnt; }
        else { sad_cnt = emotion_cnt; }
    }

    public static int get_count(int emotion_count){
        return emotion_count;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_history);

        pieChart = (PieChart) findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(hap_cnt,"Happy"));
        yValues.add(new PieEntry(exc_cnt,"Excited"));
        yValues.add(new PieEntry(ang_cnt,"Angry"));
        yValues.add(new PieEntry(ene_cnt,"Energetic"));
        yValues.add(new PieEntry(neu_cnt,"Neutral"));
        yValues.add(new PieEntry(tir_cnt,"Tired"));
        yValues.add(new PieEntry(ner_cnt,"Nervous"));
        yValues.add(new PieEntry(bor_cnt,"Bored"));
        yValues.add(new PieEntry(sad_cnt,"Sad"));

        PieDataSet dataSet = new PieDataSet(yValues, "emotions");
//        dataSet.setColors(new int[]{Color.parseColor(#FF81E3),R.color.colorExcited,R.color.colorAngry,
//                R.color.colorEnergetic, R.color.colorNeutral,R.color.colorTired,
//                R.color.colorNervous,R.color.colorBored,R.color.colorSad});
        dataSet.setColors(
                getResources().getColor(R.color.colorHappy),
                getResources().getColor(R.color.colorExcited),
                getResources().getColor(R.color.colorAngry),
                getResources().getColor(R.color.colorEnergetic),
                getResources().getColor(R.color.colorNeutral),
                getResources().getColor(R.color.colorTired),
                getResources().getColor(R.color.colorNervous),
                getResources().getColor(R.color.colorBored),
                getResources().getColor(R.color.colorSad));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        //dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
    }



    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.buttonMood:
                intent = new Intent(this, EnterMoodActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonMusic:
                intent = new Intent(this, MusicPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
