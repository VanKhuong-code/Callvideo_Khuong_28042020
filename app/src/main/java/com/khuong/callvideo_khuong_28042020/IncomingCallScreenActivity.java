package com.khuong.callvideo_khuong_28042020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class IncomingCallScreenActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "id";
    public static int MESSAGE_ID = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call_screen);
    }
}
