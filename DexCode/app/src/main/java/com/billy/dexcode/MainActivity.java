package com.billy.dexcode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test() {
        int a = 0;
        for(int i = 0;i<10;i++){
            a++;
        }
        int b = 10;
        for(int i = 0;i<10;i++){
            a++;
        }
        int j;

        double i = 2.1;
    }

}
