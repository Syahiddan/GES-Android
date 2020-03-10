package com.example.shidan.ges;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.shidan.ges.shidan.PageError;
import com.example.shidan.ges.shidan.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Splash extends AppCompatActivity {
//    private static int SPLASH_TIME_OUT = 4000;

    private static int SPLASH_TIME_OUT = 400;
    private Boolean isConnected = false;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent jump_temp = new Intent(Splash.this, Login.class);
        startActivity(jump_temp);

        final RelativeLayout main_splash = (RelativeLayout) findViewById(R.id.main_splash);
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("function", "checkConnection");

        final WebService ws = new WebService(Splash.this);
        ws.post("", params, new WebService.JSON() {
            @Override
            public void responseData(JSONObject status, JSONObject data) {
                try {
                    Log.d("shidan", data.toString());
                    if (status.getString("statusRequest").equals("success")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(Splash.this, Login.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        }, SPLASH_TIME_OUT);
                    } else {
                        final PageError pageError = new PageError(Splash.this, Splash.class);
                        setContentView(pageError.openWindow());
                        pageError.openWindow().findViewById(R.id.refresh_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pageError.openLoading();
                                final HashMap<Object, Object> params = new HashMap<Object, Object>();
                                params.put("function", "checkConnection");
                                thread= new Thread() {
                                    @Override
                                    public void run() {
                                        ws.post("", params, new WebService.JSON() {
                                                @Override
                                                public void responseData(JSONObject status, JSONObject data) {
                                                    try {
                                                        Log.d("shidan", status.toString());
                                                        if (status.getString("statusRequest").equals("success")) {
                                                            if (data.getString("status").equals("connected")) {
                                                                Toast.makeText(Splash.this, "connected", Toast.LENGTH_SHORT).show();
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        pageError.closeLoading();
                                                                        isConnected = true;
                                                                        Log.d("shidan", "coonnected");
                                                                        Intent loginIntent = new Intent(Splash.this, Login.class);
                                                                        startActivity(loginIntent);
                                                                        finish();
                                                                    }
                                                                }, 500);
                                                            }
                                                        } else {
                                                            Toast.makeText(Splash.this, "No Network connected to server", Toast.LENGTH_SHORT).show();
                                                            pageError.closeLoading();
                                                        }

                                                    } catch (JSONException e) {
                                                    }
                                                }
                                            });
                                    }
                                };
                                thread.start();
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.d("shidan", e.getLocalizedMessage());
                }
            }
        });
    }
}