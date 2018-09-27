package com.eshel.annotationprocessor;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import annotation.Bind;
import annotation.ViewBinder;

public class SecondActivity extends Activity{

    @Bind(R.id.tv)
    TextView tv_;
    @Bind(R.id.tv)
    TextView tv_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewBinder.bind(this);
    }
}
