package com.handsome.ffmpegdouyin;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.handsome.ffmpegdouyin.FFmpeg.FFmpegRun;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar;
    public String VIDEO_PATH = SD_PATH + "kugoumusic/input.mp4";
    public String AUDIO_PATH = SD_PATH + "kugoumusic/input.mp3";
    public String OUTPUT_VIDEO_PATH = SD_PATH + "kugoumusic/output.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mergeVideo(View view) {
        int result = FFmpegRun.mergeVideo(AUDIO_PATH, VIDEO_PATH, OUTPUT_VIDEO_PATH);

        if (result == 0) {
            Toast.makeText(this, "finish", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }
}
