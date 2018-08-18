package barman.utpal.voicerecorder;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.STORAGE;


public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private long startTime, elapsedTime;
    private final int REFRESH_RATE = 100;

    private String hours, minutes, seconds;
    private long secs, mins, hrs;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static File dir = null;
    private MediaRecorder mediaRecorder = null;
    private TextView tv_timer, tv_status;
    private MediaPlayer mediaPlayer;
    String myFilePath;




    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                            + "/Recordings/");
                    intent.setDataAndType(uri, "text/csv");
                    startActivity(Intent.createChooser(intent, "Open folder"));

            }
        });
        








        //assign id to variables
        ImageButton btn_startRecording = (ImageButton) findViewById(R.id.imageButton_startRecording);
        ImageButton btn_stopRecording = (ImageButton) findViewById(R.id.imageButton_stopRecording);
        tv_status = (TextView) findViewById(R.id.textView_status);
        tv_timer = (TextView) findViewById(R.id.textView_stopWatch);



        //runnable handler
        final Runnable startTimer = new Runnable() {
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                updateTimer(elapsedTime);
                mHandler.postDelayed(this, REFRESH_RATE);
            }
        };




        //OnClickListener for startRecording
        btn_startRecording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                tv_status.setText("Recording...");
                (findViewById(R.id.imageButton_startRecording)).setVisibility(View.GONE);
                (findViewById(R.id.imageButton_stopRecording)).setVisibility(View.VISIBLE);

                startTime = System.currentTimeMillis();

                mHandler.removeCallbacks(startTimer);
                mHandler.postDelayed(startTimer, 0);






                startAudioRecording();





            }
        });



        //OnClickListener for stopRecording
        btn_stopRecording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mHandler.removeCallbacks(startTimer);
                tv_status.setText("Stopped");
                stopMediaRecorder();

            }
        });


    }



    private void startAudioRecording() {

        // Click sound
        mediaPlayer = MediaPlayer.create(this, R.raw.raw_start);
        mediaPlayer.start();

        // Prepare the file name
        String dateInString = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(
                new Date());
        String fileName = "VOICE_" + dateInString + ".3gpp";

        // Save the file : android/data/<packageName>/cache/<fileName>
        //myFilePath = getExternalCacheDir().getAbsolutePath() + "/" +fileName; //returns a String

        // Save the file : android/data/files/Recordings/<fileName>
        //myFilePath = getExternalFilesDir("Recordings").getAbsolutePath() + "/" + fileName; //returns a String




        //Save the file : Recordings/<fileName>
        myFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Recordings/";

        dir = new File(myFilePath);
        if(!dir.exists()) {
            try{
                dir.mkdirs();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        myFilePath = myFilePath + "/" + fileName;


        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(myFilePath);

       

        try {
            mediaRecorder.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaRecorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void stopMediaRecorder() {

        // Click Sound
        mediaPlayer = MediaPlayer.create(this, R.raw.raw_stop);
        mediaPlayer.start();

        if(mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                Toast.makeText(this, "Record saved successfully. Record another audio!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        else
            Toast.makeText(this, "bug detected: recorder instance is null", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.imageButton_startRecording).setVisibility(View.VISIBLE);
                    findViewById(R.id.imageButton_stopRecording).setVisibility(View.GONE);
                    tv_timer.setText("00:00:00");
                    tv_status.setText("Record");
                }
            }, 1500);


    }


    private void updateTimer(float time) {
        secs = (long) (time / 1000);
        mins = (long) ((time / 1000) / 60);
        hrs = (long) (((time / 1000) / 60) / 60);

		/* Convert the seconds to String
		 * and format to ensure it has
		 * a leading zero when required
		 */

        secs = secs % 60;
        seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        }

		/* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        }

    	/* Convert the hours to String and format the String */

        hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

		/* Setting the timer text to the elapsed time */
        ((TextView) findViewById(R.id.textView_stopWatch)).setText(hours + ":" + minutes + ":" + seconds);
    }





    // Options

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
