package com.example.nhattruong.arduinoconnect;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    private MediaPlayer player;
    TextView tv;
    TextView tvState;
    SwitchCompat switchCompat;
    boolean isTurnOn=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv_data);
        tvState = findViewById(R.id.tv_state);

        switchCompat = findViewById(R.id.sw);

        player = MediaPlayer.create(this,R.raw.alert);

        try {
            //Dia chi ip thay doi
            mSocket = IO.socket("http://192.168.43.174:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect();
        mSocket.on("serverSendData", onRetrieveData);

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTurnOn){
                    player.pause();
                }
                isTurnOn = !isTurnOn;
                tvState.setText(isTurnOn? "On" : "Off");
            }
        });

       /* findViewById(R.id.btn_turn_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.pause();
                mSocket.emit("turnoff", 1);
                isTurnOn = false;
            }
        });

        findViewById(R.id.btn_turn_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTurnOn = true;
            }
        });*/

    }

    private Emitter.Listener onRetrieveData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int number = (int) args[0];
                    if (number ==1 && isTurnOn){
                        if (!player.isPlaying()){
                            player.setLooping(true);
                            player.start();
                        }
                    }
                    if (number == 0){
                        if (player.isPlaying()){
                            player.pause();
                        }
                    }
                    tv.setText(String.valueOf(number));
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
