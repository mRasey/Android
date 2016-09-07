package com.example.billy.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test() {
        for(int i = 0; i < 10; i++) {
            System.out.println(i);
        }
        for(int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }

}