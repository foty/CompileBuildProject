package com.example.apt;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.annotation.FindView;

public class MainActivity extends AppCompatActivity {

    @FindView(R.id.tvName)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BindUtil.bindView(this);
        tv.setText("APT YES");
    }
}