package com.musicme;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class ShowDetectedMoodActivity extends AppCompatActivity implements View.OnClickListener {

    // bluetooth
    //static int BPM=0;

    //BPM_state bpm_tool = (BPM_state) getApplicationContext();
    TextView TvBPM;

    TextView mTvBluetoothStatus;
    TextView mTvReceiveData;
//    TextView mTvSendData;
    Button mBtnBluetoothOn;
//    Button mBtnBluetoothOff;
    Button mBtnConnect;
//    Button mBtnSendData;

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;

    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    // graph

    private RelativeLayout mainLayout;
    private LineChart mChart;

    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_detected_mood_layout);

        // graph activity

        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(10);
        viewport.setScrollable(true);


        // bluetooth activity

//        TvBPM = (TextView)findViewById(R.id.bpm) ;


        mTvBluetoothStatus = (TextView)findViewById(R.id.tvBluetoothStatus);
        mTvReceiveData = (TextView)findViewById(R.id.tvReceiveData);
//        mTvSendData =  (EditText) findViewById(R.id.tvSendData);
        mBtnBluetoothOn = (Button)findViewById(R.id.btnBluetoothOn);
//        mBtnBluetoothOff = (Button)findViewById(R.id.btnBluetoothOff);
        mBtnConnect = (Button)findViewById(R.id.btnConnect);
//        mBtnSendData = (Button)findViewById(R.id.btnSendData);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        mBtnBluetoothOn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOn();
            }
        });
//        mBtnBluetoothOff.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bluetoothOff();
//            }
//        });
        mBtnConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPairedDevices();
            }
        });
//        mBtnSendData.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mThreadConnectedBluetooth != null) {
//                    mThreadConnectedBluetooth.write(mTvSendData.getText().toString());
//                    mTvSendData.setText("");
//                }
//            }
//        });
        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mTvReceiveData.setText(readMessage);
                    //mTvReceiveData.setText(Integer.parseInt(readMessage));

                    //int BPM = Integer.parseInt(readMessage);
                    BPM_state bpm_tool = (BPM_state) getApplicationContext();

//                    BPM_state bpm_tool = (BPM_state) getApplication();
                    //bpm_tool.set_bpm(readMessage);
//                    try {
//                        bpm_tool.set_bpm(readMessage);
//                    } catch(NumberFormatException nfe) {
//                        //Toast.makeText(getApplicationContext(), "bpm error", Toast.LENGTH_LONG).show();
//                    }

                    //addEntry(bpm_tool.get_bpm());
                }
            }
        };

        // <global menu buttons
        Button bMood = findViewById(R.id.buttonMood);
        Button bMusic = findViewById(R.id.buttonMusic);
        Button bSettings = findViewById(R.id.buttonSettings);

        bMood.setOnClickListener(this);
        bMusic.setOnClickListener(this);
        bSettings.setOnClickListener(this);
        // global menu buttons>
    }

    // graph activity

    // add random data to graph
//    private void addEntry(int BPM) {
//        String i = "10";
//        // here, we choose to display max 10 points on the viewport and we scroll to end
//        series.appendData(new DataPoint(lastX++, BPM++), true, 100);
//    }

    private void addEntry1() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        TvBPM = (TextView)findViewById(R.id.bpm_Number) ;
        int ran = RANDOM.nextInt(120 -50 +1)+50;
        series.appendData(new DataPoint(lastX++, ran ), true, 100);
        String str_ran = String.valueOf(ran);
        TvBPM.setText(str_ran);
    }

    private void addEntry2() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        TvBPM = (TextView)findViewById(R.id.bpm_Number) ;
        int fix = 71;
        series.appendData(new DataPoint(lastX++, fix), true, 100);
        String str_fix = String.valueOf(fix);
        TvBPM.setText(str_fix);
    }


    void bluetoothOn() {
        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "This device does not support Bluetooth.", Toast.LENGTH_LONG).show();
        }
        else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Bluetooth is already active.", Toast.LENGTH_LONG).show();
                mTvBluetoothStatus.setText("Activation");
            }
            else {
                Toast.makeText(getApplicationContext(), "Bluetooth is not activated.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }
//    void bluetoothOff() {
//        if (mBluetoothAdapter.isEnabled()) {
//            mBluetoothAdapter.disable();
//            Toast.makeText(getApplicationContext(), "Bluetooth is not activated.", Toast.LENGTH_SHORT).show();
//            mTvBluetoothStatus.setText("Deactivation");
//        }
//        else {
//            Toast.makeText(getApplicationContext(), "Bluetooth is already deactivated.", Toast.LENGTH_SHORT).show();
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "Bluetooth Activation", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("Activation");
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "Cancellation", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("Deactivation");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Device");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "No paired device found.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already deactivated.", Toast.LENGTH_SHORT).show();
        }
    }
    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ShowDetectedMoodActivity.ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error connecting to Bluetooth.", Toast.LENGTH_LONG).show();
        }
    }

    public class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error connecting socket.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "An error occurred while transferring data.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "An error occurred while releasing the socket.", Toast.LENGTH_LONG).show();
            }
        }

    }

//    public void set_bpm(int bpm){
//        BPM=bpm;
//    }

//    public int get_bpm(){
//        return bpm;
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        TvBPM = (TextView)findViewById(R.id.bpm) ;
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 20; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry1();
                            //addEntry(BPM);

//                            BPM_state bpm_tool = (BPM_state) getApplicationContext();
////                                    addEntry(bpm_tool.get_bpm());
//                            try {
//                                addEntry(bpm_tool.get_bpm());
//                            } catch(NumberFormatException nfe) {
//                                //Toast.makeText(getApplicationContext(), "bpm error", Toast.LENGTH_LONG).show();
//                            }
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
                while(true){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry2();
                            //addEntry(BPM);

//                            BPM_state bpm_tool = (BPM_state) getApplicationContext();
////                                    addEntry(bpm_tool.get_bpm());
//                            try {
//                                addEntry(bpm_tool.get_bpm());
//                            } catch(NumberFormatException nfe) {
//                                //Toast.makeText(getApplicationContext(), "bpm error", Toast.LENGTH_LONG).show();
//                            }
                        }
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }






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

        }
    }
}
