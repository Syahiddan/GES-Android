package com.example.shidan.ges.shidan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.shidan.ges.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DeviceComponentStatus {

    private Context context;
    private int comp_id;
    private String comp_name;
    private WebService ws;
    private TextView device_name;
    private Button btn_device_status;
    private View root;
    private int current_state;

    public DeviceComponentStatus(Context context, int comp_id, String comp_name) {
        this.context = context;
        this.comp_id = comp_id;
        this.comp_name = comp_name;
        ws = new WebService(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = inflater.inflate(R.layout.device_status, null);
        device_name = (TextView) root.findViewById(R.id.device_name);
        btn_device_status = (Button) root.findViewById(R.id.btn_device_status);

    }

    public View createComponentStatus() {

        return root;

    }

    public View createComponentStatus(int status) {
        this.current_state = status;
        device_name.setText(this.comp_name);
        setButtonListener();
        btn_device_status.setText((status == -1) ? "OFFLINE" : "ONLINE");
        btn_device_status.setClickable((status == -1)? true:false);
        btn_device_status.setEnabled((status == -1)? true:false);
        poolState();
        return root;
    }

    private void poolState() {
       new Timer().scheduleAtFixedRate(new TimerTask() {
           @Override
           public void run() {
               Log.d("along","pooling ni");
               HashMap<Object,Object> params = new HashMap<Object, Object>();
               params.put("function","fnCompCurrentOnline");
               params.put("comp_id",comp_id);

               ws.post("", params, new WebService.JSON() {
                   @Override
                   public void responseData(JSONObject status, JSONObject data) {
                       Log.d("test",status.toString());
                       Log.d("test",data.toString());
                       try{
                           if(status.getString("statusRequest").equals("success"))
                           {
                               int tempNew = data.getInt("status");
                               if( current_state!= tempNew)
                               {
                                   btn_device_status.setText((tempNew == -1) ? "OFFLINE" : "ONLINE");
                                   btn_device_status.setClickable((tempNew == -1)? true:false);
                                   btn_device_status.setEnabled((tempNew == -1)? true:false);
                                   current_state = tempNew;
                               }
                               else
                               {
                                   Log.d("along","nothing change");
                               }
                           }
                       }catch (JSONException e){

                       }
                   }
               });
           }
       },0,1000);
    }
    private void setButtonListener()
    {
        btn_device_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Request to Check Component Availability?");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("test","yes click");
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                HashMap<Object,Object> params = new HashMap<Object, Object>();
                                params.put("function","fnCompCurrentStatus");
                                params.put("comp_id",comp_id);

                                ws.post("", params, new WebService.JSON() {
                                    @Override
                                    public void responseData(JSONObject status, JSONObject data) {
                                        Log.d("along",status.toString());
                                        Log.d("along",data.toString());
                                        try{
                                            if(status.getString("statusRequest").equals("success"))
                                            {
                                                int tempNew = data.getInt("status");
                                                if( current_state!= tempNew)
                                                {
                                                    btn_device_status.setText((tempNew == -1) ? "OFFLINE" : "ONLINE");
                                                    btn_device_status.setClickable((tempNew == -1)? true:false);
                                                    btn_device_status.setEnabled((tempNew == -1)? true:false);
                                                    current_state = tempNew;
                                                }
                                                else
                                                {
                                                    Log.d("along","nothing change");
                                                }
                                            }
                                        }catch (JSONException e){

                                        }
                                    }
                                });
                            }
                        };
                        thread.start();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("test","no click");
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

}
