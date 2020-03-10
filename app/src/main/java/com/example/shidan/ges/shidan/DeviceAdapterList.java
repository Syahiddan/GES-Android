package com.example.shidan.ges.shidan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shidan.ges.AdminDevice;
import com.example.shidan.ges.R;
import com.example.shidan.ges.model.Device;
import com.example.shidan.ges.model.DeviceComp;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceAdapterList extends ArrayAdapter<Device> {
    private Context mContext;
    private int mResource;
    WebService ws;


    private ArrayList<Device> devices;
    private ViewHolder holder;
    private int lastPosition = -1;

    static class ViewHolder {
        TextView btn_device_name;
//        Button btn_device_control;
//        Button btn_device_status;
//        Button btn_device_reading;

        TextView menu_title;
        TextView device_status;
        LinearLayout table_device_control;


        LinearLayout device_content ;
        ArrayList<DeviceComponentControl> deviceComponentTriggers;

    }

    public DeviceAdapterList(Context context, int resource, ArrayList<Device> objects) {
        super(context, resource, objects);
        ws = new WebService(context);
        mContext = context;
        mResource = resource;
        devices = objects;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.btn_device_name = (TextView) convertView.findViewById(R.id.btn_device_name);
//            holder.btn_device_control = (Button) convertView.findViewById(R.id.btn_device_control);
//            holder.btn_device_status = (Button) convertView.findViewById(R.id.btn_device_status);
//            holder.btn_device_reading = (Button) convertView.findViewById(R.id.btn_device_reading);
            holder.device_status = (TextView) convertView.findViewById(R.id.device_status);

            holder.menu_title = (TextView) convertView.findViewById(R.id.menu_title);
            holder.table_device_control = (LinearLayout) convertView.findViewById(R.id.table_device_control);

            holder.device_content = (LinearLayout)convertView.findViewById(R.id.device_content);

            result = convertView;
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.scroll_down_anim : R.anim.scroll_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.btn_device_name.setText(getItem(position).getDevice_name());
        holder.device_status.setText((getItem(position).getIsOnline()== 1)? "ONLINE" : "OFFLINE");
        poolState(getItem(position).getDevice_id(),holder.device_status);
        return convertView;
    }


    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    private void poolState(final int id, final TextView curTv) {
        Timer pool = new Timer();
        pool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                HashMap<Object,Object> params = new HashMap<Object, Object>();
                params.put("function","fnGetStatusMessage");
                params.put("id",id);

                ws.post("", params, new WebService.JSON() {
                    @Override
                    public void responseData(JSONObject status, JSONObject data) {
//                        Log.d("test",status.toString());
                        Log.d("test1",data.toString());
                        try{
                            if(status.getString("statusRequest").equals("success"))
                            {
                                String msg = data.optString("string");
                                if(!curTv.getText().toString().trim().equals(msg))
                                {
                                    curTv.setText(msg);
                                }
                                else
                                {
                                    Log.d("test1","no change string");
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
