
package com.example.xa.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private boolean PermissionFlag = false;
    public TextView mTvName,mTvTime;
    public Button mBtnPlay,mBtnPause,mBtnStop,mBtnRefresh;
    public ListView mListView;
    public SeekBar mSeekBar;
    private ArrayList<Song> mSongs;
    private SongAdapter mSongAdapter;
    private MediaPlayer mMediaPlayer;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private boolean seekBarChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //如果没有权限，则动态申请授权
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        initAudioList();
        initView();

    }

    private void initView() {
        mBtnPause = findViewById(R.id.main_btn_Pause);
        mBtnPlay = findViewById(R.id.main_btn_play);
        mBtnStop = findViewById(R.id.main_btn_Stop);
        mBtnRefresh = findViewById(R.id.main_btn_Refresh);
        mTvName = findViewById(R.id.main_tv_name);
        mTvTime = findViewById(R.id.main_tv_time);
        mListView = findViewById(R.id.main_lv);
        mSeekBar = findViewById(R.id.main_sb);

        initAudioList();

        mBtnRefresh.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarChange = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarChange = false;
                mMediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initMediaPlayer(mSongs.get(position));
            }
        });
    }

    private void initAudioList() {

        //刷新新媒体库
        MediaScannerConnection.scanFile(this,new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()},
                null,null);
        mSongs = new ArrayList<Song>();
        Log.d(TAG, "onRequestPermissionsResult: 开始获取歌曲数据");
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musics = getContentResolver().query(uri,new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA},null,null,null);
        String fileName, title,singer,album,size,filePath="";
        int duration;
        Song song;
        if (musics!=null) {
            Log.d(TAG, "onRequestPermissionsResult: ");
            if (musics.moveToFirst()) {
                do {
                    fileName = musics.getString(1);
                    title = musics.getString(2);
                    duration = musics.getInt(3);
                    singer = musics.getString(4);
                    album = musics.getString(5);
                    size = (musics.getString(6) == null) ? "未知" : musics.getInt(6) / 1024 / 1024 + "MB";
                    if (musics.getString(7) != null) filePath = musics.getString(7);
                    song = new Song(fileName, title, duration, singer, album, size, filePath);
                    mSongs.add(song);
                } while (musics.moveToNext());
                musics.close();
            }
        }

        mSongAdapter = new SongAdapter(MainActivity.this,R.layout.main_list_item,mSongs);
        mListView = findViewById(R.id.main_lv);
        mListView.setAdapter(mSongAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 1:
                Log.d(TAG, "onRequestPermissionsResult: 正在获取");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: 读取外部数据获取成功");
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: 读取外部数据获取失败");
                    Toast.makeText(MainActivity.this,"未获取SD卡访问的权限",Toast.LENGTH_LONG).show();
                    finish();
                }
        }


//        if (requestCode == 1){
//            Log.d(TAG, "onRequestPermissionsResult: 正在获取");
//            if (!(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
//                Log.d(TAG, "onRequestPermissionsResult: 获取失败");
//                Toast.makeText(MainActivity.this,"未获取SD卡访问的权限",Toast.LENGTH_LONG).show();
//                finish();
//            }
//            Log.d(TAG, "onRequestPermissionsResult: 获取成功");
//
//        }
    }


    /**
     * 开始播放歌曲
     * @param song
     */
    private void initMediaPlayer(Song song){
        try {
            if (mMediaPlayer==null){
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(song.getFilePath());
            mMediaPlayer.prepare();//加载音频，完成准备
            mMediaPlayer.start();

            //设置视图
            int m = song.getDuration()/60000;
            int s = (song.getDuration()/60000)/1000;
            mTvTime.setText("时长：" +m+ "分" +s+ "秒");
            mTvName.setText(song.getFileName());
            mSeekBar.setMax(song.getDuration());
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (seekBarChange)return;
                    mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                }
            };
            mTimer.schedule(mTimerTask,0,10);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_btn_Stop:
                if (mMediaPlayer!=null && mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                    try {
                        mMediaPlayer.prepare();//准备
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.main_btn_Pause:
                if (mMediaPlayer!=null && mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                }
                break;
            case R.id.main_btn_play:
                if (mMediaPlayer!=null){
                    mMediaPlayer.start();
                }
                break;
            case R.id.main_btn_Refresh:
                initAudioList();
                break;
        }
    }
}
