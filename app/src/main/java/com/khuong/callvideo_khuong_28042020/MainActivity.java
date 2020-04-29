package com.khuong.callvideo_khuong_28042020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnDangNhap;
    EditText edtUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
    }

    private void addControls() {
        btnDangNhap = findViewById(R.id.btnDangNhap);
        edtUserId = findViewById(R.id.txtName);
    }

    @Override
    public void onClick(View v) {

    }
}
