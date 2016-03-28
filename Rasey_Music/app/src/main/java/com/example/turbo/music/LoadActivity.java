package com.example.turbo.music;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.DownloadListener;

import java.util.Timer;
import java.util.TimerTask;


public class LoadActivity extends Activity {
    /*

    变量

     */


    /*

    初始化

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.hide();*/
        setContentView(R.layout.activity_load);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(LoadActivity.this, PlayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                LoadActivity.this.finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 1000);
    }
    /*


    自定义函数


     */


    /*


    系统行为


     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
