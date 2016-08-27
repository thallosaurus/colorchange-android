package com.jarvis.myapplication;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://socket-thallosaurus.rhcloud.com:8000");
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }
    }

    int counter;

    private Boolean color;

    public void sendSignal(View view) {
        mSocket.emit("switch");
    }

        public void setBackgroundColor(boolean color) {
            //TODO insert color specific code - Done
            //int col;
            View view = this.getWindow().getDecorView();

            if (color == true) {
                System.out.println("color is True");
                //col = 0xFFFFFF;
                view.setBackgroundColor(Color.WHITE);
            } else {
                System.out.println("color is false");
                view.setBackgroundColor(Color.DKGRAY);
                //col = 0x000000;
            }
            return;
    }

    private Emitter.Listener onSwitch = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
               @Override
                public void run() {
                   JSONObject data = (JSONObject) args[0];
                   System.out.println("JSONData " + data);
                   Boolean color;
                   try {
                       color = data.getBoolean("color");
                       System.out.println(color);
                       setBackgroundColor(color);
                       counter = data.getInt("counter");
                       System.out.println(counter);
                       TextView tv = (TextView) findViewById(R.id.counterView);
                       tv.setText("Total: " + counter);

                   } catch (JSONException e) {
                       return;
                   }
               }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvc = (TextView) findViewById(R.id.counterView);
        tvc.setTextSize(40);
        counter = 0;

        //For fullscreen access
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSocket.on("switch", onSwitch);
        mSocket.connect();
        Toast.makeText(this, "Connected, touch to change the Color!", Toast.LENGTH_SHORT).show();

        RelativeLayout rl;
        rl = (RelativeLayout) findViewById(R.id.view);
        rl.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("switch");
    }

    @Override
    public void onClick(View view) {
        sendSignal(view);
    }
}
