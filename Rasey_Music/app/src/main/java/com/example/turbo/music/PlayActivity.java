package com.example.turbo.music;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import javax.net.ssl.HandshakeCompletedListener;


public class PlayActivity extends Activity{
    /*

    变量

     */
    int count = 0;
    static int index;
    static int title_shake;
    static int title_handle;
    static int musicNumber;
    static boolean should_play;
    static boolean pauseAble = false;
    static boolean if_JustStart = true;
    static boolean if_first_used = true;
    Intent playMusic;
    TextView music_display;
    static List<HashMap<String, Object>> mp3list = new ArrayList<>();

    static boolean if_shaking = false;
    long mLastProximityEventTime = SystemClock.elapsedRealtime();
    private static final int FORCE_THRESHOLD = 1000; //摇晃速度
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;
    private long mLastTime;
    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    static MySimpleAdapter mAdapter;
    private ImageSwitcher imageSwitcher;
    private ImageSwitcher imageSwitcher2;
    static List<Mp3Info> mp3Infos;
    static ListView musicList;
    static Random random = new Random();
    private static Integer[] imageList = {R.drawable.random, R.drawable.orderly, R.drawable.looping,
            R.drawable.pausedown, R.drawable.pausedown1, R.drawable.pausedown2};
    private static Integer[] titleList = {R.drawable.titlebarnone, R.drawable.titlebarshakenohandle,
            R.drawable.titlebarhandlenoshake, R.drawable.titlebarboth};
    static int[] imageIds = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f,
            R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j, R.drawable.k, R.drawable.l, R.drawable.m, R.drawable.n,
            R.drawable.o, R.drawable.p, R.drawable.q, R.drawable.r, R.drawable.s, R.drawable.t, R.drawable.u, R.drawable.v,
            R.drawable.w, R.drawable.x, R.drawable.y, R.drawable.z, R.drawable.musicimage, R.drawable.m0, R.drawable.m1,
            R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5, R.drawable.m6, R.drawable.m7, R.drawable.m8, R.drawable.m9};
    /*


    初始化


     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        playMusic = new Intent(PlayActivity.this,MusicService.class);
        /*

        初始化用户数据

         */
        getInit();
        /*


         */
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorManager distance = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor proximityListener = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor distancesensor = distance.getDefaultSensor(Sensor.TYPE_LIGHT);

        musicList = (ListView) findViewById(R.id.MusicList);
        music_display = (TextView) findViewById(R.id.music_display);
        setListAdpter(getMp3Infos());
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Mp3Info mp3Info = new Mp3Info();
                mp3Info = mp3Infos.get(position);
                if_JustStart = false;
                BlackToYellow();
                musicNumber = position;
                startService(playMusic);
                if_JustStart = true;
                putInit();
                mAdapter.notifyDataSetChanged();
            }
        });


        imageSwitcher = (ImageSwitcher) findViewById(R.id.onTouch);
        this.imageSwitcher.setFactory(new ViewFactoryImp());
        imageSwitcher.setImageResource(imageList[index]);
        imageSwitcher2 = (ImageSwitcher) findViewById(R.id.titleBar);
        this.imageSwitcher2.setFactory(new ViewFactoryImp());
        if(title_handle == 0 && title_shake == 0) {
            imageSwitcher2.setImageResource(titleList[0]);
        }
        else if(title_handle == 1 && title_shake == 0){
            imageSwitcher2.setImageResource(titleList[2]);
        }
        else if(title_handle == 0 && title_shake == 1){
            imageSwitcher2.setImageResource(titleList[1]);
        }
        else{
            imageSwitcher2.setImageResource(titleList[3]);
        }
        /*

        检测歌曲是否放完

         */
        MusicService.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (!if_JustStart) {
                    startService(playMusic);
                    putInit();
                }
            }
        });
        /*

        电话监听

         */
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); // 获取系统服务
        telManager.listen(new MobliePhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        /*

        设置textview

         */
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1 && mp3Infos.get(musicNumber).getTitle().contains("-") ){
                    music_display.setText(mp3Infos.get(musicNumber).getTitle().toString());
                }
                else if(msg.what == 1){
                    music_display.setText( mp3Infos.get(musicNumber).getArtist().toString() + " - " + mp3Infos.get(musicNumber).getTitle().toString() );
            }
            }
        };
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 500, 500);
        //刷新记录的用户数据
        TimerTask flushInit = new TimerTask() {
            @Override
            public void run() {
                putInit();
            }
        };
        Timer timerFlushInit = new Timer();
        timerFlushInit.schedule(flushInit, 1000);


        /*

        摇晃手机

         */
        SensorEventListener mProximityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                long milliseconds = SystemClock.elapsedRealtime();//当前时间
                float distance = event.values[0];
                long timeSinceLastEvent = milliseconds - mLastProximityEventTime;
                if (timeSinceLastEvent > 700 && title_handle == 1) {
                    startService(playMusic);
                    BlackToYellow();
                }
                mLastProximityEventTime = milliseconds;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // ignore
            }
        };
        sensorManager.registerListener(mProximityListener, proximityListener, SensorManager.SENSOR_DELAY_GAME);


        SensorEventListener shakeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //获取加速度传感器的三个参数
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long now = System.currentTimeMillis();

                if ((now - mLastForce) > SHAKE_TIMEOUT) {
                    mShakeCount = 0;
                }

                if ((now - mLastTime) > TIME_THRESHOLD) {
                    long diff = now - mLastTime;
                    float speed = Math.abs(x + y + z - mLastX - mLastY - mLastZ) / diff * 10000;
                    if (speed > FORCE_THRESHOLD) {
                        if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                            mLastShake = now;
                            mShakeCount = 0;
                            if (true) {
                                if_shaking = true;
                            }
                        }
                        mLastForce = now;
                    }
                    mLastTime = now;
                    mLastX = x;
                    mLastY = y;
                    mLastZ = z;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };
        sm.registerListener(shakeListener, acceleromererSensor, SensorManager.SENSOR_DELAY_GAME);

        SensorEventListener distanceSensor = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                if (if_shaking && x > 10 && title_shake == 1) {
                    startService(playMusic);
                    BlackToYellow();
                }
                if_shaking = false;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        distance.registerListener(distanceSensor, distancesensor, SensorManager.SENSOR_DELAY_GAME);
        /*


        换titlebar图片


         */
        Button shakeSwitcher = (Button) findViewById(R.id.shake);
        shakeSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title_handle == 0) {
                    if (title_shake == 0) {
                        title_shake = 1;
                        imageSwitcher2.setImageResource(titleList[1]);
                    } else {
                        title_shake = 0;
                        imageSwitcher2.setImageResource(titleList[0]);
                    }
                } else {
                    if (title_shake == 0) {
                        title_shake = 1;
                        imageSwitcher2.setImageResource(titleList[3]);
                    } else {
                        title_shake = 0;
                        imageSwitcher2.setImageResource(titleList[2]);
                    }
                }
                putInit();
            }
        });
        Button handleSwicher = (Button) findViewById(R.id.handle);
        handleSwicher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title_shake == 0) {
                    if (title_handle == 0) {
                        title_handle = 1;
                        imageSwitcher2.setImageResource(titleList[2]);
                    } else {
                        title_handle = 0;
                        imageSwitcher2.setImageResource(titleList[0]);
                    }
                } else {
                    if (title_handle == 0) {
                        title_handle = 1;
                        imageSwitcher2.setImageResource(titleList[3]);
                    } else {
                        title_handle = 0;
                        imageSwitcher2.setImageResource(titleList[1]);
                    }
                }
                putInit();
            }
        });
        /*

        双击button

         */
        Button doubleTouch = (Button) findViewById(R.id.doubleTouch);
        doubleTouch.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count == 2){
                    musicList.setSelectionFromTop(musicNumber, 150);
                    count = 0;
                }
            }
        });

        final TimerTask doubleClick = new TimerTask() {
            @Override
            public void run() {
                count = 0;
            }
        };
        Timer runDoubleClick = new Timer();
        runDoubleClick.schedule(doubleClick, 500);
        /*


        定义暂停、下一首、上一首


         */
        final Button pauseMusic = (Button) findViewById(R.id.pauseMusic);
        pauseMusic.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pauseAble) {
                    YellowToBlack();
                    MusicService.mediaPlayer.pause();
                    pauseAble = false;
                } else if (if_JustStart) {
                    BlackToYellow();
                    startService(playMusic);
                    //if_JustStart = false;
                } else {
                    BlackToYellow();
                    MusicService.mediaPlayer.start();
                    pauseAble = true;
                }
                putInit();
            }
        });
        //长按button
        pauseMusic.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PlayActivity.this, WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                stopService(playMusic);
                PlayActivity.this.finish();
                return true;
            }
        });
        Button nextMusic = (Button) findViewById(R.id.nextMusic);
        nextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlackToYellow();
                startService(playMusic);
            }
        });
        Button switcher = (Button) findViewById(R.id.switcher);
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    index = 1;
                    imageSwitcher.setImageResource(imageList[index]);
                } else if (index == 1) {
                    index = 2;
                    imageSwitcher.setImageResource(imageList[index]);
                } else if (index == 2) {
                    index = 0;
                    imageSwitcher.setImageResource(imageList[index]);
                } else if (index == 3) {
                    index = 4;
                    imageSwitcher.setImageResource(imageList[index]);
                } else if (index == 4) {
                    index = 5;
                    imageSwitcher.setImageResource(imageList[index]);
                } else if (index == 5) {
                    index = 3;
                    imageSwitcher.setImageResource(imageList[index]);
                }
                putInit();
            }
        });


    }

    /*

    监听耳机动作

     */


    /*


    将手机内的音乐文件加入listview里


     */
    public List<Mp3Info> getMp3Infos() {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        mp3Infos = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Mp3Info mp3Info = new Mp3Info();
            cursor.moveToNext();
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            int imageid;

            if (title.contains("-")) {
                artist = "<" + title.subSequence(0, title.indexOf("-")) + ">";
            } else {
                artist = "<" + artist + ">";
            }

            artist = artist.substring(1, artist.length() - 1);

            if (title.toLowerCase().charAt(0) >= 'a' && title.toLowerCase().charAt(0) <= 'z') {
                imageid = getImageId(title.toLowerCase().charAt(0));
            } else if (title.charAt(0) >= '0' && title.charAt(0) <= '9') {
                imageid = getMathId(title.charAt(0));
            } else {
                imageid = R.drawable.musicimage;
            }

            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != 0 && size > 524288) {
                mp3Info.setTitle(title);
                mp3Info.setUrl(url);
                mp3Info.setArtist(artist);
                mp3Info.setImageIds(imageid);
                mp3Info.setDuration(duration);
                mp3Infos.add(mp3Info);
            }
        }
        cursor.close();
        return mp3Infos;
    }

    public void setListAdpter(List<Mp3Info> mp3Infos) {

        Iterator iterator = mp3Infos.iterator();
        while (iterator.hasNext()) {
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String, Object> map = new HashMap<>();

            map.put("title", mp3Info.getTitle());
            map.put("url", mp3Info.getUrl());
            map.put("artist", mp3Info.getArtist());
            map.put("image", mp3Info.getIds());
            map.put("duration", mp3Info.getDuration());
            mp3list.add(map);
        }
        LayoutInflater layoutInflater=getLayoutInflater();
        mAdapter = new MySimpleAdapter(this, mp3list, R.layout.simple_item, new String[]{"image", "title", "artist"}, new int[]{R.id.musicImage, R.id.title, R.id.artist}, layoutInflater);
        musicList.setAdapter(mAdapter);

    }

    /*


    定义系统按钮功能


     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
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
    /*



    自定义方法



     */
    public int getImageId(char first) {
        return imageIds[first - 'a'];
    }

    public int getMathId(char first) {
        return imageIds[first - '0' + 27];
    }

    public void BlackToYellow() {
        if (index == 0) {
            index = 3;
            imageSwitcher.setImageResource(imageList[index]);
        } else if (index == 1) {
            index = 4;
            imageSwitcher.setImageResource(imageList[index]);
        } else if (index == 2) {
            index = 5;
            imageSwitcher.setImageResource(imageList[index]);
        }
    }

    public void YellowToBlack() {
        if (index == 3) {
            index = 0;
            imageSwitcher.setImageResource(imageList[index]);
        } else if (index == 4) {
            index = 1;
            imageSwitcher.setImageResource(imageList[index]);
        } else if (index == 5) {
            index = 2;
            imageSwitcher.setImageResource(imageList[index]);
        }
    }

    /*


    获得随机数


     */
    public static void getMusicNumber() {
        if (index == 0 || index == 3) {
            musicNumber = random.nextInt(mp3Infos.size());
        } else if (index == 1 || index == 4) {
            if(musicNumber == mp3Infos.size() - 1){
                musicNumber = 0;
            }
            else{
                musicNumber++;
            }
        } else if (index == 2 || index == 5) {
            musicNumber = musicNumber;
        }
    }

    private class ViewFactoryImp implements ViewSwitcher.ViewFactory {

        public View makeView() {
            ImageView img = new ImageView(PlayActivity.this);
            return img;
        }
    }

    /*


    电话监听


     */
    private class MobliePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    MusicService.mediaPlayer.start();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    MusicService.mediaPlayer.pause();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    MusicService.mediaPlayer.pause();
                    break;
                default:
                    break;
            }
        }
    }
    /*

    输出读取数据

     */
    public void putInit(){
        SharedPreferences.Editor init = getSharedPreferences("data", MODE_PRIVATE).edit();
        if(index == 3){
            init.putInt("index", 0);
        }
        else if(index == 4){
            init.putInt("index", 1);
        }
        else if(index == 5){
            init.putInt("index", 2);
        }
        else{
            init.putInt("index", index);
        }
        init.putInt("title_shake", title_shake);
        init.putInt("title_handle", title_handle);
        init.putInt("musicNumber", musicNumber);
        init.putBoolean("should_play", should_play);
        //init.putBoolean("if_JustStart", if_JustStart);
        init.commit();
    }
    public void getInit(){
        SharedPreferences getInit = getSharedPreferences("data", MODE_PRIVATE);
        index = getInit.getInt("index", 0);
        title_shake = getInit.getInt("title_shake", 0);
        title_handle = getInit.getInt("title_handle", 0);
        musicNumber = getInit.getInt("musicNumber", 0);
        should_play = getInit.getBoolean("should_play", false);
        //if_JustStart = getInit.getBoolean("if_JustStart", false);
    }

}
/*


自定义类


 */
class Mp3Info{
    private String title;
    private String url;
    private String artist;
    private int ids;
    private int duration;

    public String getTitle(){
        return title;
    }

    public String getUrl(){
        return url;
    }

    public String getArtist(){
        return artist;
    }

    public int getIds(){
        return ids;
    }

    public int getDuration() { return duration; }

    public void setTitle(String title){
        this.title = title;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public void setImageIds(int ids){
        this.ids = ids;
    }

    public void setDuration(int duration) { this.duration = duration; };
}