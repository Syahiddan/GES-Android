package com.example.shidan.ges.shidan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class DeviceComponentControl {
    public static final int ON = 1;
    public static final int OFF = 0;
    public static final int BROKE = -2;
    public static final int DISABLE = -1;
    private int btn_pick = -100;
    private View triggerView;
    private Context context;
    final private int comp_id;
    private String comp_name;
    private int status;
    WebService ws;
    RadioRealButtonGroup radioRealButtonGroup;
    Button comp_state;
    Timer pool;

    public DeviceComponentControl(Context context, int comp_id, String comp_name) {
        this.context = context;
        this.comp_id = comp_id;
        this.comp_name = comp_name;
//        this.status = status;
        ws = new WebService(context);

    }

    public View createComponentControl()    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        triggerView = inflater.inflate(R.layout.device_control, null);
        TextView component_id = (TextView) triggerView.findViewById(R.id.comp_id);
        component_id.setText("uina");
        TextView component_name = (TextView) triggerView.findViewById(R.id.comp_name);
        component_name.setText(comp_name);
        radioRealButtonGroup = (RadioRealButtonGroup) triggerView.findViewById(R.id.group_button);
        comp_state = (Button) triggerView.findViewById(R.id.comp_state);


        return triggerView;
    }
    public View createComponentControl(int status)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        triggerView = inflater.inflate(R.layout.device_control, null);
        TextView component_id = (TextView) triggerView.findViewById(R.id.comp_id);
        component_id.setText("uina");
        TextView component_name = (TextView) triggerView.findViewById(R.id.comp_name);
        component_name.setText(comp_name);
        radioRealButtonGroup = (RadioRealButtonGroup) triggerView.findViewById(R.id.group_button);
        comp_state = (Button) triggerView.findViewById(R.id.comp_state);


        if(status == 0 )
        {
            radioRealButtonGroup.setPosition(1);
            btn_pick = OFF;

        }else if(status == 1)
        {
            radioRealButtonGroup.setPosition(0);
            btn_pick = ON;
        }
        setListener();
        poolState();
        return triggerView;
    }
    private void setListener() {
        radioRealButtonGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {

//                pool.cancel();
                if (position == 0) {
                    btn_pick = ON;
                } else {
                    btn_pick = OFF;
                }
                HashMap<Object,Object> params = new HashMap<Object, Object>();
                params.put("function","fnCompTrigger");
                params.put("comp_id",comp_id);
                params.put("status",btn_pick);

                ws.post("", params, new WebService.JSON() {
                    @Override
                    public void responseData(JSONObject status, JSONObject data) {
                        Log.d("test",data.toString());
                        if(status.optString("statusRequest").equals("success")){
                            if(data.optString("status").equals("triggered"))
                            {
                                String temp = comp_name+" :";
                                temp += (btn_pick == ON)? "ON":"OFF";
                                Toast.makeText(context,temp,Toast.LENGTH_SHORT).show();
                            }
                            else if (data.optString("status").equals("unchanged"))
                            {
                                String temp = comp_name+" already :";
                                temp += (btn_pick == ON)? "ON":"OFF";
                                Toast.makeText(context,temp,Toast.LENGTH_SHORT).show();
                            }
                            else if(data.optString("status").equals("error"))
                            {
                                String temp = comp_name + "trigger error";
                                Toast.makeText(context,temp,Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                            String temp = comp_name + "no connection";
                            Toast.makeText(context,temp,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        comp_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn_name = ((Button) v).getText().toString();
                Toast.makeText(context, "checking for availability   " + btn_name, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void poolState() {
        pool = new Timer();
        pool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("test","pooling ni  "+String.valueOf(comp_id));
                HashMap<Object,Object> params = new HashMap<Object, Object>();
                params.put("function","fnCompCurrentStatus");
                params.put("comp_id",comp_id);

                ws.post("", params, new WebService.JSON() {
                    @Override
                    public void responseData(JSONObject status, JSONObject data) {
//                        Log.d("test",status.toString());
//                        Log.d("test",data.toString());
                        try{
                            if(status.getString("statusRequest").equals("success"))
                            {
                                int tempNew = data.getInt("status");
                                Log.d("test",String.valueOf(tempNew));
                                if( btn_pick!= tempNew)
                                {

                                    Log.d("test", "changed!");
                                    btn_pick = tempNew;
                                    radioRealButtonGroup.setPosition((radioRealButtonGroup.getPosition()== ON)? OFF:ON);
                                }
                                else
                                {
                                    Log.d("test","nothing change");
                                }
                            }
                        }catch (JSONException e){

                        }
                    }
                });
            }
        },0,3000);
    }
}
