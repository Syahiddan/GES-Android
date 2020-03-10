package com.example.shidan.ges.shidan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.shidan.ges.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceComponentReading {

    private Context context;
    private int comp_id;
    private String comp_name;
    private WebService ws;
    private View root;
    TextView tv_comp_name,tv_comp_reading,tv_comp_reading_unit;
    private double cur_reading;
    HashMap<Object,Object> params=  new HashMap<Object, Object>();;


    public DeviceComponentReading(Context context, int comp_id, String comp_name) {
        this.context = context;
        this.comp_id = comp_id;
        this.comp_name = comp_name;
        ws = new WebService(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = inflater.inflate(R.layout.device_reading, null);
        this.tv_comp_name = root.findViewById(R.id.comp_name);
        this.tv_comp_reading = root.findViewById(R.id.comp_reading);
        this.tv_comp_reading_unit = root.findViewById(R.id.comp_unit_reading);
    }

    public View createComponentReading(){
        return root;
    }
    public View createComponentReading(double cur_reading,String unit_reading){
        this.tv_comp_name.setText(this.comp_name);
        this.tv_comp_reading.setText(String.valueOf(cur_reading));
        this.tv_comp_reading_unit.setText(unit_reading);
        this.cur_reading = cur_reading;
        poolState();
        return root;
    }
    private void poolState()
    {
        params.clear();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                params.put("function","fnCompCurrentReading");
                params.put("comp_id",comp_id);
                Log.d("test","component id : " + String.valueOf(comp_id));
                ws.post("", params, new WebService.JSON() {
                    @Override
                    public void responseData(JSONObject status, JSONObject data) {
                        Log.d("test",data.toString());
                        if(cur_reading != data.optDouble("reading"))
                        {
                           cur_reading = data.optDouble("reading");
                            tv_comp_reading.setText(String.valueOf(cur_reading));
                        }
                    }
                });
            }
        },0,1000);
    }
}

