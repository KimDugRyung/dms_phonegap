package com.luxoft.dms;

import org.apache.cordova.DroidGap;

import android.annotation.SuppressLint;
import android.os.Bundle;

@SuppressLint("NewApi")
public class App extends DroidGap {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
    }
}
