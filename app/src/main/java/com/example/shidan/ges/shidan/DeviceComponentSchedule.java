package com.example.shidan.ges.shidan;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.AdminWorker;
import com.example.shidan.ges.R;

public class DeviceComponentSchedule  {
    private Context context;
    private int schedule_id;
    private String day;
    private String time;
    private WebService ws;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;

    private LinearLayout display_schedule,status_schedule,edit_schedule;
    private TextView schedule_day,schedule_hour,schedule_edit_hour;
    private Button schedule_btn_edit,schedule_btn_delete,schedule_btn_submit;
    private Spinner schedule_edit_day;


    private String log = "test";
    public DeviceComponentSchedule(Context context,int schedule_id, String day,String time) {
        this.context = context;
        this.schedule_id = schedule_id;
        this.day = day;
        this.time = time;
        ws = new WebService(context);
        builder = new AlertDialog.Builder(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    public View createComponentSchedule()
    {
        View view = inflater.inflate(R.layout.device_schedule,null);

        display_schedule = view.findViewById(R.id.display_schedule);
        status_schedule = view.findViewById(R.id.status_schedule);
        edit_schedule = view.findViewById(R.id.edit_schedule);

//        schedule_day = view.findViewById(R.id.schedule_day);
        schedule_hour = view.findViewById(R.id.schedule_hour);
        schedule_edit_hour = view.findViewById(R.id.schedule_edit_hour);

        schedule_btn_edit = view.findViewById(R.id.schedule_btn_edit);
        schedule_btn_delete = view.findViewById(R.id.schedule_btn_delete);
        schedule_btn_submit = view.findViewById(R.id.schedule_btn_submit);

        schedule_edit_day = view.findViewById(R.id.schedule_edit_day);

//        schedule_day.setText(this.day);
        schedule_hour.setText(this.time);

        listener();
        return view;
    }
    private void listener()
    {
        schedule_btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_schedule.setVisibility(View.GONE);
                edit_schedule.setVisibility(View.VISIBLE);

                schedule_edit_day.setSelection((day == "Monday")?0:(day == "Tuesday")?1:(day == "Wednesday")?2:(day == "Thursday")?3:(day == "Friday")?4:(day == "Saturday")?5:(day == "Sunday")?6:6);
                schedule_edit_hour.setText(time);
                schedule_edit_hour.requestFocus();
            }
        });
        schedule_btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Deleting Worker Information");
//                LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View temp = li.inflate(R.layout.loading,null);
//                builder.setView(temp);
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Log.d(log,"yes Click");
                            }
                        })
                        .setNegativeButton("uina", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(log,"uina click");
                            }
                        }).show();
            }
        });
        schedule_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"submit clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
