package com.example.shidan.ges;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.model.Device;
import com.example.shidan.ges.model.DeviceComp;
import com.example.shidan.ges.model.DeviceReading;
import com.example.shidan.ges.model.Place;
import com.example.shidan.ges.model.Schedule;
import com.example.shidan.ges.model.ScheduleGroup;
import com.example.shidan.ges.shidan.DeviceAdapterList;
import com.example.shidan.ges.shidan.DeviceComponentControl;
import com.example.shidan.ges.shidan.DeviceComponentReading;
import com.example.shidan.ges.shidan.DeviceComponentStatus;
import com.example.shidan.ges.shidan.LoadingDialog;
import com.example.shidan.ges.shidan.ScheduleAdapterList;
import com.example.shidan.ges.shidan.Session;
import com.example.shidan.ges.shidan.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminDevice extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Toolbar toolbar;
    LoadingDialog ld;
    RelativeLayout mainLayout;
    LayoutInflater li;
    View layout;
    ListView lv_device;
    WebService ws;
    DeviceAdapterList dal;
    ArrayList<Device> devices;
    ArrayList<Device> temp_for_search = new ArrayList<Device>();
    ArrayList<Device> temp_for_filter = new ArrayList<Device>();
    TextView filter_all_btn, filter_online_btn, filter_offline_btn;
    EditText input_device_name;
    String log = "test";
    LinearLayout pb_lyt;
    ArrayList<String> places_Spinner = new ArrayList<String>();
    ArrayList<Place> places_spinner_array = new ArrayList<Place>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(Color.GREEN);
        Thread thr1 = new Thread() {
            @Override
            public void run() {
                setup_drawer();
            }
        };
        thr1.start();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);


        ImageView curUserImage = (ImageView) header.findViewById(R.id.imageView_curUSer);
        TextView curUserName = (TextView) header.findViewById(R.id.user_name);
        TextView curUSerEmail = (TextView) header.findViewById(R.id.user_email);

//
//        Picasso.get().load("http://i.imgur.com/DvpvklR.png").resize(curUserImage.getDrawable().getIntrinsicWidth(),curUserImage.getDrawable().getIntrinsicHeight()).centerCrop().into(curUserImage);
//        curUserName.setText(Session.getUser().getFname()+" "+ Session.getUser().getMidname()+" "+Session.getUser().getLname());
//        curUSerEmail.setText(Session.getUser().getEmail());

        ws = new WebService(AdminDevice.this);
        mainLayout = (RelativeLayout) findViewById(R.id.admin_content_space);
        li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = li.inflate(R.layout.activity_admin_device, null);
        mainLayout.removeAllViews();
        mainLayout.addView(layout);
        lv_device = (ListView) findViewById(R.id.list_device);
        pb_lyt = (LinearLayout) findViewById(R.id.pb_lyt);
        input_device_name = findViewById(R.id.input_device_name);
        Thread thread = new Thread() {
            @Override
            public void run() {
                setup_spinner_item();
                setup_device_list();
                itemClickListener();
                setup_filter_btn();

            }
        };
        thread.start();


    }

    public void setup_spinner_item() {
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("function", "fnPlaceList");
        ws.post("", params, new WebService.JSON() {
            @Override
            public void responseData(JSONObject status, JSONObject data) {
//                Log.d("test3",status.toString());
                if (status.optString("statusRequest").equals("success")) {
                    Log.d("test3", data.toString());
                    try {
                        JSONArray temp_data = data.getJSONArray("place");

                        for (int i = 0; i < temp_data.length(); i++) {
                            JSONObject tempjson = (JSONObject) temp_data.get(i);
                            Place temp = new Place(tempjson.optString("id"));
                            temp.setPlace_name(tempjson.optString("name"));
                            places_spinner_array.add(temp);
                            places_Spinner.add(tempjson.optString("name"));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        });
    }

    public void setup_device_list() {

        devices = new ArrayList<Device>();
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("function", "fnDeviceList");

        ws.post("", params, new WebService.JSON() {
            @Override
            public void responseData(JSONObject status, JSONObject data) {
                Log.d(log, data.toString());
                try {
                    if (status.getString("statusRequest").equals("success")) {


                        JSONArray dev_list_array = data.getJSONArray("data");

                        for (int i = 0; i < dev_list_array.length(); i++) {
                            Device device_temp = new Device();
                            device_temp.setDevice_id(dev_list_array.getJSONObject(i).getInt("id"));
                            device_temp.setDevice_name(dev_list_array.getJSONObject(i).getString("name"));
                            device_temp.setPlace_id(dev_list_array.getJSONObject(i).getString("place_id"));
                            device_temp.setIsOnline(dev_list_array.getJSONObject(i).optInt("online"));
                            JSONArray comp_array = dev_list_array.getJSONObject(i).optJSONArray("comp");
                            JSONArray scheduleGroup_array = dev_list_array.getJSONObject(i).optJSONArray("schedule");
                            JSONArray reading_array = dev_list_array.getJSONObject(i).optJSONArray("reading");
                            if (comp_array != null) {
                                ArrayList<DeviceComp> deviceComps = new ArrayList<DeviceComp>();
                                for (int j = 0; j < comp_array.length(); j++) {
                                    DeviceComp comp_temp = new DeviceComp();
                                    comp_temp.setComp_id(comp_array.getJSONObject(j).getInt("part_id"));
                                    comp_temp.setComp_name(comp_array.getJSONObject(j).optString("part_name"));
                                    comp_temp.setComp_status(comp_array.getJSONObject(j).optInt("status"));
                                    comp_temp.setComp_online(comp_array.getJSONObject(j).optInt("online"));
                                    Log.d("test", String.valueOf(comp_array.getJSONObject(j).optInt("online")));
                                    deviceComps.add(comp_temp);
                                }
                                device_temp.setDeviceComps(deviceComps);

                            }
                            if (scheduleGroup_array != null) {
                                ArrayList<ScheduleGroup> scheduleGroups = new ArrayList<ScheduleGroup>();
                                for (int j = 0; j < scheduleGroup_array.length(); j++) {
                                    ScheduleGroup scheduleGroup_temp = new ScheduleGroup();
                                    scheduleGroup_temp.setDay_id(scheduleGroup_array.getJSONObject(j).optInt("day_id"));
                                    scheduleGroup_temp.setDay_name(scheduleGroup_array.getJSONObject(j).optString("name"));

                                    JSONArray temp = scheduleGroup_array.getJSONObject(j).getJSONArray("schedule_list");
//                                    Log.d("dandan",String.valueOf(temp.length()));
                                    ArrayList<Schedule> temp_schedules = new ArrayList<Schedule>();
                                    for (int k = 0; k < temp.length(); k++) {
                                        Schedule temp_sch = new Schedule();
                                        temp_sch.setSchedule_id(((JSONObject) temp.get(k)).optInt("id"));
                                        temp_sch.setDay_id(((JSONObject) temp.get(k)).optInt("day_id"));
                                        temp_sch.setTime(((JSONObject) temp.get(k)).optString("hour"));
                                        temp_sch.setStatus(((JSONObject) temp.get(k)).optString("status"));
                                        temp_sch.setArd_id(((JSONObject) temp.get(k)).optInt("ard_id"));
                                        temp_schedules.add(temp_sch);
                                    }
                                    scheduleGroup_temp.setSchedules(temp_schedules);
                                    scheduleGroups.add(scheduleGroup_temp);
                                }

//                                device_temp.setSchedules(deviceSchedules);

                                device_temp.setScheduleGroups(scheduleGroups);
                            }
                            if (reading_array != null) {
                                Log.d("dandan", reading_array.getJSONObject(0).toString());
                                ArrayList<DeviceReading> deviceReadings = new ArrayList<DeviceReading>();
                                for (int j = 0; j < reading_array.length(); j++) {
                                    DeviceReading reading_temp = new DeviceReading();
                                    reading_temp.setPart_id(reading_array.getJSONObject(j).optInt("part_id"));
                                    reading_temp.setPart_name(reading_array.getJSONObject(j).optString("part_name"));
                                    reading_temp.setReading(reading_array.getJSONObject(j).optDouble("reading"));
                                    reading_temp.setReading_unit(reading_array.getJSONObject(j).optString("reading_unit"));
                                    deviceReadings.add(reading_temp);
                                }
                                device_temp.setDeviceReadings(deviceReadings);
                            }

                            devices.add(device_temp);
                            dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, devices);

                            lv_device.setAdapter(dal);
                            pb_lyt.setVisibility(View.GONE);
                            lv_device.setVisibility(View.VISIBLE);
                        }


                    } else {
                        pb_lyt.setVisibility(View.VISIBLE);
                        lv_device.setVisibility(View.GONE);
//                        Toast.makeText(AdminDevice.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        setup_device_list();
                    }
                } catch (JSONException e) {
                    Log.d(log, e.getLocalizedMessage());
                }
            }
        });

    }

    public void itemClickListener() {
        lv_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final RelativeLayout root = (RelativeLayout) dal.getView(position, view, parent);
                final LinearLayout device_content = (LinearLayout) root.findViewById(R.id.device_content);
                final LinearLayout row_place = (LinearLayout) root.findViewById(R.id.row_place);
                final LinearLayout table_device_control = (LinearLayout) root.findViewById(R.id.table_device_control);
                final LinearLayout table_device_status = (LinearLayout) root.findViewById(R.id.table_device_status);
                final LinearLayout table_device_reading = (LinearLayout) root.findViewById(R.id.table_device_reading);
//                final LinearLayout for_schedule = (LinearLayout) root.findViewById(R.id.for_schedule);

                row_place.setVisibility((row_place.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                final Spinner place_spinner = (Spinner) root.findViewById(R.id.place_spinner);


                final ArrayAdapter<String> adapter_place = new ArrayAdapter<String>(AdminDevice.this, android.R.layout.simple_spinner_dropdown_item, places_Spinner);
                place_spinner.setAdapter(adapter_place);
                final TextView menu_title = (TextView) root.findViewById(R.id.menu_title);

                root.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    Animation animation2;

                    @Override
                    public void onAnimationStart(Animation animation) {
                        animation2 = AnimationUtils.loadAnimation(AdminDevice.this, (device_content.getVisibility() != View.VISIBLE) ? R.anim.scroll_down_anim : R.anim.scroll_up_anim);
                        device_content.setVisibility((device_content.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                        device_content.getChildAt(0).setAnimation(animation2);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                final Button btn_device_control = (Button) root.findViewById(R.id.btn_device_control);
                final Button btn_device_status = (Button) root.findViewById(R.id.btn_device_status);
                final Button btn_device_reading = (Button) root.findViewById(R.id.btn_device_reading);
                final Button btn_device_schedule = (Button) root.findViewById(R.id.btn_device_schedule);

                table_device_control.setVisibility(View.VISIBLE);
//                for_schedule.setVisibility(View.GONE);
                table_device_status.setVisibility(View.GONE);
                table_device_reading.setVisibility(View.GONE);

                menu_title.setText("Control");
                if (table_device_control.getChildCount() == 0) {

                    ArrayList<DeviceComp> deviceComps = dal.getItem(position).getDeviceComps();
                    if (deviceComps != null) {
                        for (int i = 0; i < deviceComps.size(); i++) {
                            Log.d(log, String.valueOf(deviceComps.get(i).getComp_status()));
                            DeviceComponentControl temp = new DeviceComponentControl(AdminDevice.this, deviceComps.get(i).getComp_id(), deviceComps.get(i).getComp_name());
                            table_device_control.addView(temp.createComponentControl(deviceComps.get(i).getComp_status()));

                        }
                    } else {
                        TextView textView = new TextView(AdminDevice.this);
                        textView.setText("Not Available");
                        textView.setGravity(Gravity.CENTER);
                        table_device_control.addView(textView);
                    }
                }
                place_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                String curID = places_spinner_array.get(position).getPlace_id();
                                int curDevID = 0;
                                for(int i = 0; i < devices.size();i++)
                                {
                                    if(devices.get(i).getDevice_name() == ((TextView)root.findViewById(R.id.btn_device_name)).getText().toString().trim())
                                    {
                                        curDevID = devices.get(i).getDevice_id();
                                        break;
                                    }
                                }
                                HashMap<Object, Object> params = new HashMap<Object, Object>();
                                params.put("function", "fnChangeDevicePlace");
                                params.put("device_id", curDevID);
                                params.put("place_id", curID);
                                Log.d("test3",String.valueOf(curDevID));
                                Log.d("test3",curID);
                                ws.post("", params, new WebService.JSON() {
                                    @Override
                                    public void responseData(JSONObject status, JSONObject data) {
                                        if (status.optString("statusRequest").equals("success")) {
                                            Log.d("test3",data.toString());
                                            if (data.optString("status").equals("success")) {
                                                Toast.makeText(AdminDevice.this, "Place for this device change to place id:" + adapter_place.getItem(position), Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(AdminDevice.this, "failed", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }
                                });
                            }
                        };
                        thread.start();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                btn_device_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(log, "button clicked control:" + ((TextView) root.findViewById(R.id.btn_device_name)).getText().toString());
                        table_device_control.setVisibility(View.VISIBLE);
//                        for_schedule.setVisibility(View.GONE);
                        table_device_status.setVisibility(View.GONE);
                        table_device_reading.setVisibility(View.GONE);
                        menu_title.setText("Control");
                        if (table_device_control.getChildCount() == 0) {
//                                    table_device_control.removeAllViewsInLayout();

                            ArrayList<DeviceComp> deviceComps = dal.getItem(position).getDeviceComps();
                            if (deviceComps != null) {
                                for (int i = 0; i < deviceComps.size(); i++) {
                                    Log.d(log, String.valueOf(deviceComps.get(i).getComp_status()));
                                    DeviceComponentControl temp = new DeviceComponentControl(AdminDevice.this, deviceComps.get(i).getComp_id(), deviceComps.get(i).getComp_name());

                                    table_device_control.addView(temp.createComponentControl(deviceComps.get(i).getComp_status()));

                                }
                            } else {
                                TextView textView = new TextView(AdminDevice.this);
                                textView.setText("Not Available");
                                textView.setGravity(Gravity.CENTER);
                                table_device_control.addView(textView);
                            }
                        }

                    }
                });
                btn_device_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(log, "button clicked status:" + ((TextView) root.findViewById(R.id.btn_device_name)).getText().toString());
                        table_device_status.setVisibility(View.VISIBLE);
//                        for_schedule.setVisibility(View.GONE);
                        table_device_control.setVisibility(View.GONE);
                        table_device_reading.setVisibility(View.GONE);

                        menu_title.setText("Status");
                        if (table_device_status.getChildCount() == 0) {

                            ArrayList<DeviceComp> deviceComps = dal.getItem(position).getDeviceComps();
                            if (deviceComps != null) {
                                for (int i = 0; i < deviceComps.size(); i++) {
                                    Log.d(log, String.valueOf(deviceComps.get(i).getComp_status()));
                                    DeviceComponentStatus temp = new DeviceComponentStatus(AdminDevice.this, deviceComps.get(i).getComp_id(), deviceComps.get(i).getComp_name());
                                    table_device_status.addView(temp.createComponentStatus(deviceComps.get(i).getComp_status()));

                                }
                            } else {
                                Log.d(log, "sini test2x");
                                table_device_status.removeAllViews();
                                TextView textView = new TextView(AdminDevice.this);
                                textView.setText("Not Available");
                                textView.setGravity(Gravity.CENTER);
                                table_device_status.addView(textView);
                            }
                        }

                    }
                });
                btn_device_reading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(log, "button clicked reading:" + ((TextView) root.findViewById(R.id.btn_device_name)).getText().toString());
                        table_device_reading.setVisibility(View.VISIBLE);
//                        for_schedule.setVisibility(View.GONE);
                        table_device_control.setVisibility(View.GONE);
                        table_device_status.setVisibility(View.GONE);
                        menu_title.setText("Reading");

                        if (table_device_reading.getChildCount() == 0) {
                            ArrayList<DeviceReading> deviceReadings = dal.getItem(position).getDeviceReadings();
                            if (deviceReadings != null) {
                                for (int i = 0; i < deviceReadings.size(); i++) {
                                    Log.d("dandan", String.valueOf(deviceReadings.get(i).getPart_id()));
                                    DeviceComponentReading temp = new DeviceComponentReading(AdminDevice.this, deviceReadings.get(i).getPart_id(), deviceReadings.get(i).getPart_name());
                                    table_device_reading.addView(temp.createComponentReading(deviceReadings.get(i).getReading(), deviceReadings.get(i).getReading_unit()));
                                    Log.d("test", "create");
                                }
                            } else {
                                table_device_reading.removeAllViews();
                                TextView textView = new TextView(AdminDevice.this);
                                textView.setText("Not Available");
                                textView.setGravity(Gravity.CENTER);
                                table_device_reading.addView(textView);
                            }
                        }

                    }
                });
                btn_device_schedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(log, "button clicked schedule:" + ((TextView) root.findViewById(R.id.btn_device_name)).getText().toString());
                        table_device_reading.setVisibility(View.GONE);
                        table_device_control.setVisibility(View.GONE);
                        table_device_status.setVisibility(View.GONE);
//                        for_schedule.setVisibility(View.VISIBLE);
                        menu_title.setText("Schedule");

                        try {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(AdminDevice.this);
                            final AlertDialog windowSchedule = builder.create();

                            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            View root = layoutInflater.inflate(R.layout.device_schedule_activity, null);
                            final Spinner schedule_day_spinner = (Spinner) root.findViewById(R.id.schedule_edit_day);
                            Button btn_add_schedule = (Button) root.findViewById(R.id.btn_add_schedule);
                            final ListView lv_schedule = (ListView) root.findViewById(R.id.lv_schedule);
                            Button btn_modal_close = (Button) root.findViewById(R.id.btn_modal_close);
                            btn_modal_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    windowSchedule.dismiss();
                                }
                            });
                            final int cur_group = position;
                            final ArrayList<Schedule> temp_schedules = ((ScheduleGroup) ((ArrayList<ScheduleGroup>) dal.getItem(position).getScheduleGroups()).get(0)).getSchedules();
                            btn_add_schedule.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder_add = new AlertDialog.Builder(AdminDevice.this);
                                    final AlertDialog addSchedule = builder_add.create();
                                    View root_add = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.device_schedule_add, null);
                                    final Spinner schedule_day_add = (Spinner) root_add.findViewById(R.id.schedule_day_add);
                                    final EditText schedule_hour_add = (EditText) root_add.findViewById(R.id.schedule_hour_add);
                                    final Button schedule_btn_submit = (Button) root_add.findViewById(R.id.schedule_btn_submit);

                                    schedule_btn_submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Thread thread = new Thread() {
                                                @Override
                                                public void run() {
                                                    HashMap<Object, Object> params = new HashMap<Object, Object>();
                                                    params.put("function", "fnScheduleAdd");
                                                    params.put("day_id", schedule_day_add.getSelectedItemPosition() + 1);
                                                    params.put("hour", schedule_hour_add.getText().toString().trim());
                                                    params.put("ard_id", devices.get(position).getDevice_id());
                                                    ws.post("", params, new WebService.JSON() {
                                                        @Override
                                                        public void responseData(JSONObject status, JSONObject data) {
//                                                            Log.d("shidan",status.toString());
                                                            Log.d("shidan", data.toString());
                                                            try {
                                                                if (status.getString("statusRequest").equals("success")) {
                                                                    if (data.optString("status").equals("success")) {
                                                                        JSONObject tempJSON = data.getJSONObject("data");
                                                                        Schedule schedule = new Schedule();
                                                                        schedule.setArd_id(tempJSON.optInt("ard_id"));
                                                                        schedule.setSchedule_id(tempJSON.optInt("id"));
                                                                        schedule.setDay_id(tempJSON.optInt("day_id"));
                                                                        schedule.setTime(tempJSON.optString("hour"));
                                                                        schedule.setStatus(tempJSON.optString("status"));
                                                                        changeScheduleData(temp_schedules, schedule, "add", addSchedule, windowSchedule);
                                                                    }

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
                                    schedule_day_add.setSelection(schedule_day_spinner.getSelectedItemPosition());

                                    addSchedule.setView(root_add);
                                    addSchedule.show();


                                }
                            });
                            Log.d("dandan", String.valueOf(dal.getItem(position).getScheduleGroups().size()));
                            final ScheduleAdapterList temp_adapter = new ScheduleAdapterList(AdminDevice.this, R.layout.device_schedule, temp_schedules);
                            schedule_day_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                                    try {
                                        Log.d("test2", String.valueOf(dal.getItem(cur_group).getScheduleGroups().size()));
                                        ArrayList<Schedule> temp_schedules = ((ScheduleGroup) ((ArrayList<ScheduleGroup>) dal.getItem(cur_group).getScheduleGroups()).get(position)).getSchedules();
                                        ScheduleAdapterList temp_adapter = new ScheduleAdapterList(AdminDevice.this, R.layout.device_schedule, temp_schedules);
                                        lv_schedule.setAdapter(temp_adapter);
                                    } catch (Exception e) {
                                        Toast.makeText(AdminDevice.this, "Schedule for this day not available", Toast.LENGTH_SHORT);
                                        ArrayList<Schedule> temp_null = new ArrayList<Schedule>();
                                        ScheduleAdapterList temp_adapter = new ScheduleAdapterList(AdminDevice.this, R.layout.device_schedule, temp_null);
                                        lv_schedule.setAdapter(temp_adapter);

                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            lv_schedule.setAdapter(temp_adapter);
                            windowSchedule.setView(root);
                            windowSchedule.show();
                            lv_schedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    LinearLayout curScheduleViewRow = (LinearLayout) temp_adapter.getView(position, view, parent);
                                    Button schedule_btn_edit = (Button) curScheduleViewRow.findViewById(R.id.schedule_btn_edit);
                                    schedule_btn_edit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            LinearLayout rowRoot = (LinearLayout) v.getParent().getParent();
//                                                Log.d("test3",((TextView)rowRoot.findViewById(R.id.schedule_hour)).getText().toString());
                                        }
                                    });
                                    Button schedule_btn_delete = (Button) curScheduleViewRow.findViewById(R.id.schedule_btn_delete);
                                    schedule_btn_delete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(AdminDevice.this);
                                            builder2.setTitle("Are you sure to delete this schedule data?");
                                            builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Thread thread = new Thread() {
                                                        @Override
                                                        public void run() {
                                                            HashMap<Object, Object> params = new HashMap<Object, Object>();
                                                            params.put("function", "fnScheduleDelete");
                                                            params.put("schedule_id", temp_schedules.get(position).getSchedule_id());

                                                            ws.post("", params, new WebService.JSON() {
                                                                @Override
                                                                public void responseData(JSONObject status, JSONObject data) {
//                                                                        Log.d("test3",status.toString());
//                                                                        Log.d("test3",data.toString());
                                                                    if (status.optString("statusRequest").equals("success")) {
                                                                        if (data.optString("status").equals("success")) {
                                                                            Toast.makeText(AdminDevice.this, "Schedule delete Succesfully", Toast.LENGTH_SHORT).show();
                                                                            AlertDialog.Builder tempDum = new AlertDialog.Builder(AdminDevice.this);
                                                                            AlertDialog dummy = tempDum.create();
                                                                            Schedule temp = new Schedule();
                                                                            temp.setSchedule_id(temp_schedules.get(position).getSchedule_id());
                                                                            changeScheduleData(temp_schedules, temp, "delete", dummy, windowSchedule);
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    };
                                                    thread.start();
                                                }
                                            });
                                            builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder2.show();
                                        }
                                    });
                                }

                            });


                        } catch (Exception e) {
                            Toast.makeText(AdminDevice.this, "Schedule List not available for this device", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public void changeScheduleData(ArrayList<Schedule> group, Schedule data, String action, final AlertDialog child, final AlertDialog parent) {
        if (action.equals("Add") || action.equals("add")) {
            group.add(data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, devices);
                    lv_device.setAdapter(dal);
                    child.dismiss();
                    parent.dismiss();
                    lv_device.invalidate();
                    parent.show();
                }
            });

        } else if (action.equals("Update") || action.equals("update")) {

            for (int i = 0; i < group.size(); i++) {
                if (group.get(i).getSchedule_id() == data.getSchedule_id()) {
                    Schedule temp_plant_update = group.get(i);
                    temp_plant_update.setSchedule_id(data.getSchedule_id());
                    temp_plant_update.setArd_id(data.getArd_id());
                    temp_plant_update.setTime(data.getTime());
                    temp_plant_update.setStatus(data.getStatus());
                    temp_plant_update.setDay_id((data.getDay_id().trim().equals("Monday")) ? 1 : (data.getDay_id().trim().equals("Tuesday")) ? 2 : (data.getDay_id().trim().equals("Wednesday")) ? 3 : (data.getDay_id().trim().equals("Thursday")) ? 4 : (data.getDay_id().trim().equals("Friday")) ? 5 : (data.getDay_id().trim().equals("Saturday")) ? 6 : (data.getDay_id().trim().equals("Sunday")) ? 7 : 1);
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, devices);
                    lv_device.setAdapter(dal);
                    child.dismiss();
                    parent.dismiss();
                    lv_device.invalidate();
                    parent.show();
                }
            });
        } else if (action.equals("Delete") || action.equals("delete")) {
            for (int i = 0; i < group.size(); i++) {
                if (group.get(i).getSchedule_id() == data.getSchedule_id()) {
                    group.remove(i);
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, devices);
                    lv_device.setAdapter(dal);
                    child.dismiss();
                    parent.dismiss();
                    lv_device.invalidate();
                    parent.show();
                }
            });
        }

    }

    public void filterDevice(View v) {
        LinearLayout root = (LinearLayout) v.getParent();
        RelativeLayout overRoot = (RelativeLayout) root.getParent();
        TextView filter_online_btn_temp = overRoot.findViewById(R.id.filter_online_btn);
        TextView filter_offline_btn_temp = overRoot.findViewById(R.id.filter_offline_btn);
        TextView filter_all_btn_temp = overRoot.findViewById(R.id.filter_all_btn);

        int count_child = lv_device.getAdapter().getCount();
        int checkFilter = -1;
        if (!filter_online_btn_temp.isEnabled()) {
            checkFilter = 1;
        } else if (!filter_offline_btn_temp.isEnabled()) {
            checkFilter = 0;
        } else if (!filter_all_btn_temp.isEnabled()) {
            checkFilter = -1;
        }

        if (!input_device_name.getText().toString().toLowerCase().trim().isEmpty() || input_device_name.getText().toString().toLowerCase().trim().length() > 0) {
            temp_for_search.clear();

            if (checkFilter == 1) {
                for (int i = 0; i < devices.size(); i++) {
                    if (devices.get(i).getDevice_name().toLowerCase().contains(input_device_name.getText().toString().toLowerCase().trim()) && devices.get(i).getIsOnline() == 1) {
                        temp_for_search.add(devices.get(i));
                    }
                }
                Log.d("along", "online + ade search string");
            } else if (checkFilter == 0) {
                for (int i = 0; i < devices.size(); i++) {
                    if (devices.get(i).getDevice_name().toLowerCase().contains(input_device_name.getText().toString().toLowerCase().trim()) && devices.get(i).getIsOnline() == 0) {
                        temp_for_search.add(devices.get(i));
                    }
                }
                Log.d("along", "offline + ade search string");
            } else if (checkFilter == -1) {
                for (int i = 0; i < devices.size(); i++) {
                    if (devices.get(i).getDevice_name().toLowerCase().contains(input_device_name.getText().toString().toLowerCase().trim())) {
                        temp_for_search.add(devices.get(i));
                    }
                }
                Log.d("along", "all + ade search string");
            }
            dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, temp_for_search);
            lv_device.setAdapter(dal);
            lv_device.invalidate();
        } else {
            temp_for_filter.clear();
            if (checkFilter == 1) {
                for (int i = 0; i < devices.size(); i++) {
                    if (devices.get(i).getIsOnline() == 1) {
                        temp_for_filter.add(devices.get(i));
                    }
                }
                Log.d("along", "online + xde search string");
            } else if (checkFilter == 0) {
                for (int i = 0; i < devices.size(); i++) {
                    if (devices.get(i).getIsOnline() == 0) {
                        temp_for_filter.add(devices.get(i));
                    }
                }
                Log.d("along", "offline + xde search string");
            } else if (checkFilter == -1) {
                temp_for_filter = devices;
                Log.d("along", "all + xde search string");
            }
            dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, temp_for_filter);
            lv_device.setAdapter(dal);
            lv_device.invalidate();
        }


    }

    public void setup_drawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void setup_filter_btn() {
        filter_online_btn = (TextView) findViewById(R.id.filter_online_btn);
        filter_offline_btn = (TextView) findViewById(R.id.filter_offline_btn);
        filter_all_btn = (TextView) findViewById(R.id.filter_all_btn);
        filter_all_btn.setBackgroundResource(R.drawable.custom_btn_success);
        filter_all_btn.setEnabled(false);
        filter_all_btn.setTextColor(Color.BLACK);
        filter_online_btn.setTextColor(Color.BLACK);
        filter_offline_btn.setTextColor(Color.BLACK);
        filter_online_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_for_filter.clear();
                filter_online_btn.setEnabled(false);
                filter_online_btn.setBackgroundResource(R.drawable.custom_btn_success);
                filter_offline_btn.setBackgroundResource(R.drawable.simple_bubble_btn);
                filter_all_btn.setBackgroundResource(R.drawable.simple_bubble_btn);
                filter_offline_btn.setEnabled(true);
                filter_all_btn.setEnabled(true);

                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        if (input_device_name.getText().toString().trim().length() > 0 && temp_for_search.size() != 0) {
                            for (int i = 0; i < temp_for_search.size(); i++) {
                                if (temp_for_search.get(i).getIsOnline() == 1) {
                                    temp_for_filter.add(temp_for_search.get(i));
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, temp_for_filter);
                                    lv_device.setAdapter(dal);
                                    lv_device.invalidate();
                                }
                            });
                        } else {


                            for (int i = 0; i < devices.size(); i++) {
                                if (devices.get(i).getIsOnline() == 1) {
                                    temp_for_filter.add(devices.get(i));
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, temp_for_filter);
                                    lv_device.setAdapter(dal);
                                    lv_device.invalidate();
                                }
                            });

                        }

                    }
                };
                thread.start();
            }
        });
        filter_offline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_for_filter.clear();
                filter_online_btn.setEnabled(true);
                filter_offline_btn.setEnabled(false);
                filter_all_btn.setEnabled(true);
                filter_offline_btn.setBackgroundResource(R.drawable.custom_btn_success);
                filter_online_btn.setBackgroundResource(R.drawable.simple_bubble_btn);
                filter_all_btn.setBackgroundResource(R.drawable.simple_bubble_btn);

                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        if (input_device_name.getText().toString().trim().length() > 0 && temp_for_search.size() != 0) {
                            for (int i = 0; i < temp_for_search.size(); i++) {
                                if (temp_for_search.get(i).getIsOnline() == 0) {
                                    temp_for_filter.add(temp_for_search.get(i));
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, temp_for_filter);
                                    lv_device.setAdapter(dal);
                                    lv_device.invalidate();
                                }
                            });
                            Log.d("along", "search ade dan search dah click");
                        } else {


                            for (int i = 0; i < devices.size(); i++) {
                                if (devices.get(i).getIsOnline() == 0) {
                                    temp_for_filter.add(devices.get(i));
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, temp_for_filter);
                                    lv_device.setAdapter(dal);
                                    lv_device.invalidate();
                                }
                            });

                        }

                    }
                };
                thread.start();
            }
        });
        filter_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_for_filter.clear();
                filter_online_btn.setEnabled(true);
                filter_offline_btn.setEnabled(true);
                filter_all_btn.setEnabled(false);
                filter_offline_btn.setBackgroundResource(R.drawable.simple_bubble_btn);
                filter_online_btn.setBackgroundResource(R.drawable.simple_bubble_btn);
                filter_all_btn.setBackgroundResource(R.drawable.custom_btn_success);
                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        if (input_device_name.getText().toString().trim().length() > 0 && temp_for_search.size() != 0) {
                            for (int i = 0; i < temp_for_search.size(); i++) {
                                if (temp_for_search.get(i).getIsOnline() == 0) {
                                    temp_for_filter.add(temp_for_search.get(i));
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, temp_for_search);
                                    lv_device.setAdapter(dal);
                                    lv_device.invalidate();
                                }
                            });
                        } else {


                            for (int i = 0; i < devices.size(); i++) {
                                if (devices.get(i).getIsOnline() == 0) {
                                    temp_for_filter.add(devices.get(i));
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dal = new DeviceAdapterList(AdminDevice.this, R.layout.device_arraylist, devices);
                                    lv_device.setAdapter(dal);
                                    lv_device.invalidate();
                                }
                            });

                        }

                    }
                };
                thread.start();
            }
        });

    }

    public void open_filter_layout(View view) {
        RelativeLayout root = (RelativeLayout) view.getParent().getParent();
        LinearLayout tool_footer = root.findViewById(R.id.tool_footer);
        tool_footer.setVisibility((tool_footer.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.drawer_device_menu) {
//            ld = new LoadingDialog();
//            ld.show(getSupportFragmentManager(),"uinaaaaaa");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ld.dismiss();
//                }
//            },2000);
            Intent intent = new Intent(AdminDevice.this, AdminDevice.class);
            startActivity(intent);
        } else if (id == R.id.drawer_staff_menu) {
            Intent intent = new Intent(AdminDevice.this, AdminWorker.class);
            startActivity(intent);

        }
        else if (id == R.id.drawer_place_menu) {
            Intent intent = new Intent(AdminDevice.this,AdminPlace.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_menu) {
            Intent intent = new Intent(AdminDevice.this,AdminPlant.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_state_menu) {
            Intent intent = new Intent(AdminDevice.this,AdminPlantState.class);
            startActivity(intent);
        }else if (id == R.id.drawer_logout_menu) {
            Session.setCurrentContext(Login.class);
            Session.setUser(null);
            Intent intent = new Intent(AdminDevice.this, Login.class);
            startActivity(intent);
            finish();
        }
//
//

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
