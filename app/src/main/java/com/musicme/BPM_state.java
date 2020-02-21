package com.musicme;

import android.app.Application;

public class BPM_state extends Application {

    private int bpm;

    @Override
    public void onCreate() {
        bpm=0;
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void set_bpm(String st_bpm){
        int in_bpm = Integer.parseInt(st_bpm);
        this.bpm = in_bpm;
    }

    public int get_bpm(){
        return bpm;
    }
}

//public class BPM_state {
//
//    private int bpm;
//
//    public void set_bpm(String st_bpm){
//        this.bpm=0;
//        int in_bpm = Integer.parseInt(st_bpm);
//        this.bpm = in_bpm;
//    }
//
//    public int get_bpm(){
//        return bpm;
//    }
//}
