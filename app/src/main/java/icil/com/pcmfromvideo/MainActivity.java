package icil.com.pcmfromvideo;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    AudioFromVideo mAudioFromVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.run);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video = Environment.getExternalStorageDirectory() + "/input.mp4";
                String audio = Environment.getExternalStorageDirectory() + "/output.pcm";

                mAudioFromVideo = new AudioFromVideo(video,audio);
                mAudioFromVideo.start();
            }
        });

        Button play = (Button)findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    thread.run();
                }

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String audio = Environment.getExternalStorageDirectory() + "/output.pcm";
            try {
                PlayShortAudioFileViaAudioTrack(audio);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Thread thread = new Thread(runnable);

    private void PlayShortAudioFileViaAudioTrack(String filePath) throws IOException
    {
// We keep temporarily filePath globally as we have only two sample sounds now..
        if (filePath==null)
            return;

//Reading the file..
        byte[] byteData = null;
        File file = null;
        file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
        byteData = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream( file );
            in.read( byteData );
            in.close();

        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        int sampleRate = 44100;
        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
        if (at!=null) {
            at.play();
// Write the byte array to the track
            at.write(byteData, 0, byteData.length);
            at.stop();
            at.release();
        }
        else
            Log.d("TCAudio", "audio track is not initialised ");

    }
}


