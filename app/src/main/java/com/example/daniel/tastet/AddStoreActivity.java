package com.example.daniel.tastet;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class AddStoreActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater.from(this).inflate(R.layout.add_store_activity,null);
    }

    public void cancel(View v) {


    }

    public void submit(View v) {


    }
}
