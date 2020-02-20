package com.musicme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class ShowDetectedMoodActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mainLayout;
    private LineChart mChart;

    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_detected_mood_layout);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setScrollable(true);

//        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
//        // create line chart
//        mChart = new LineChart(this);
//        // add to main layout
//        mainLayout.addView(mChart);
//
//        // customize line chart
//        //mChart.setDescription("");
//        mChart.setNoDataText("No data for the moment");
//
//        // enable value highlighting
//        //mChart.setDefaultFocusHighlightEnabled();
//
//        // we want also enable scaling and dragging
//        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
//        mChart.setDrawGridBackground(false);
//
//        // enable pinch zoom to avoid scaling x and y axis separately
//        mChart.setPinchZoom(true);
//
//        // alternative background color
//        mChart.setBackgroundColor(Color.LTGRAY);
//
//        // now, we work on data
//        LineData data = new LineData();
//        data.setValueTextColor(Color.WHITE);
//
//        // add data to line chart
//        mChart.setData(data);
//
//        // get legend object
//        Legend l = mChart.getLegend();
//
//        // customize Legend
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTextColor(Color.WHITE);
//
//        XAxis xl = mChart.getXAxis();
//        xl.setTextColor(Color.WHITE);
//        xl.setDrawGridLines(false);
//        xl.setAvoidFirstLastClipping(true);
//
//        YAxis yl = mChart.getAxisLeft();
//        yl.setTextColor(Color.WHITE);
//        yl.setAxisMaxValue(120f);
//        yl.setDrawGridLines(true);
//
//        YAxis yl2 =  mChart.getAxisRight();
//        yl2.setEnabled(false);


        Button save_mood= findViewById(R.id.button_Save_Mood);
        Button detect_mood= findViewById(R.id.button_remeasure_mood);
        //Button detect_mood= findViewById(R.id.detect_my_mood_button);


        // <global menu buttons
        Button bMood = findViewById(R.id.buttonMood);
        Button bMusic = findViewById(R.id.buttonMusic);
        Button bSettings = findViewById(R.id.buttonSettings);

        bMood.setEnabled(false);
        bMusic.setOnClickListener(this);
        bSettings.setOnClickListener(this);
        // global menu buttons>

        save_mood.setOnClickListener(this);
        detect_mood.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 10);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id==R.id.action_settings){
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // now, we're going to simullate real time data addition
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // add 100 entries
//                for (int i = 0; i < 100; i++) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            addEntry(); // chart is notified of update in addEntry method
//                        }
//                    });
//
//                    // pause bwt adds
//                    try {
//                        Thread.sleep(600);
//                    } catch (InterruptedException e) {
//                        // manage error ...
//                    }
//                }
//            }
//        });
//    }
//
//    private void addEntry(){
//        LineData data = mChart.getData();
//
//        if (data != null){
//            LineDataSet set = data.getDataSetByIndex(0);
//
//            if(set == null){
//                // creation if null
//                set = createSet();
//                data.addDataSet(set);
//
//            }
//
//            // add a new random value
//            data.addXValue("");
//            data.addEntry(new Entry((float)Math.random() * 75) +40f,
//                    set.getEntryCount(), 0);
//
//            // notify chart data have changed
//            mChart.notifyDataSetChanged();
//
//            // limit number of visible entries
//            mChart.setVisibleXRange(6);
//
//            // scroll to the last entry
//            mChart.moveViewToX(data.getXValCount()-7);
//
//        }
//    }
//
//    // method to create set
//    private LineDataSet createSet(){
//        LineDataSet set = new LineDataSet(null,"SPL Db");
//        set.setCubicIntensity(0.2f);
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setColor(ColorTemplate.getHoloBlue());
//        set.setCircleColor(ColorTemplate.getHoloBlue());
//        set.setLineWidth(2f);
//        set.setCircleSize(4f);
//        set.setFillAlpha(65);
//        set.setFillColor(ColorTemplate.getHoloBlue());
//        set.setHighLightColor(Color.rgb(244,117,177));
//        set.setValueTextColor(Color.WHITE);
//        set.setValueTextSize(10f);
//
//        return set;
//    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.buttonMood:
                intent = new Intent(this, EnterMoodActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonMusic:
                intent = new Intent(this, MusicPlayerActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
//            case R.id.button_Save_Mood:
//                intent = new Intent(this, .class);
//                startActivity(intent);
//                finish();
//                break;
            case R.id.button_remeasure_mood:
                intent = new Intent(this, EnterMoodActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }
}
