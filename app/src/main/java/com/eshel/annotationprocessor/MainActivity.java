package com.eshel.annotationprocessor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import annotation.Bind;
import annotation.ViewBinder;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.tv)
    TextView tv2;
    @Bind(R.id.tv)
    TextView tv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
        tv.setText(" ViewBinder bindView success!!!");
    }
}
