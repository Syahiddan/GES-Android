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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.model.PlantState;
import com.example.shidan.ges.shidan.PlantStateAdapterList;
import com.example.shidan.ges.shidan.Session;
import com.example.shidan.ges.shidan.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminPlantState extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String test = "test";
    DrawerLayout drawer;
    Toolbar toolbar;
    RelativeLayout mainLayout;
    LayoutInflater li;
    View layout;
    ListView lv;
    WebService ws;
    ArrayList<PlantState> plantStates;
    PlantStateAdapterList pal;

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

        mainLayout = (RelativeLayout) findViewById(R.id.admin_content_space);
        li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = li.inflate(R.layout.activity_admin_plant_state, null);
        mainLayout.removeAllViews();
        mainLayout.addView(layout);

        ws = new WebService(AdminPlantState.this);
        lv = (ListView) findViewById(R.id.list_plant_state);
        plantStates = new ArrayList<PlantState>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                setup_plant_state_list();
                listViewListener();
            }
        };

        thread.start();
    }

    public void setup_plant_state_list() {
        EditText input_plant_name = (EditText) findViewById(R.id.input_plant_name);
        input_plant_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {

                }
            }
        });
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("function", "fnPlantStateList");
        ws.post("", params, new WebService.JSON() {
            @Override
            public void responseData(JSONObject status, JSONObject data) {
                if (status.optString("statusRequest").equals("success")) {
                    Log.d(test, data.toString());
                    if (data != null) {

                        try {
//                                Log.d(test,data.getJSONArray("data").toString());
                            for (int i = 0; i < data.getJSONArray("data").length(); i++) {
                                JSONObject temp = (JSONObject) data.getJSONArray("data").get(i);
                                PlantState temp_plant = new PlantState();
                                temp_plant.setCode(temp.optString("code"));
                                temp_plant.setState_name(temp.optString("state_name"));
                                temp_plant.setHumidity(Float.valueOf(temp.optString("humidity")));
                                temp_plant.setTemperature(Float.valueOf(temp.optString("temperature")));
                                plantStates.add(temp_plant);
                            }


                            pal = new PlantStateAdapterList(AdminPlantState.this, R.layout.plant_state_arraylist, plantStates);
                        } catch (JSONException e) {
                            Log.d(test, e.getLocalizedMessage());
                        }

                    }
                }

                lv.setAdapter(pal);
            }
        });
    }

    public void listViewListener() {
        try {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View root = pal.getView(position, view, parent);
                    LinearLayout action_div = root.findViewById(R.id.action_div);

                    action_div.setVisibility((action_div.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);

                    Button edit_btn = (Button) root.findViewById(R.id.edit_btn);
                    Button delete_btn = (Button) root.findViewById(R.id.delete_btn);

                    edit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editPlantStateBtn(v);
                        }
                    });

                    delete_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deletePlantStateBtn(v);
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.d(test, e.getLocalizedMessage());
        }
    }

    public void deletePlantStateBtn(View view) {
        LinearLayout root = (LinearLayout) view.getParent().getParent();
        final String code_data = ((TextView) root.findViewById(R.id.code)).getText().toString();
        Toast.makeText(AdminPlantState.this, "hahahaha", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlantState.this);
        final AlertDialog deleteDialog = builder.create();
        builder.setTitle("Alert Deleting Plant State Information!");
        builder.setMessage("Are you sure to delete this information");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final HashMap<Object, Object> params = new HashMap<Object, Object>();
                params.put("function", "fnPlantStateDelete");
                params.put("code", code_data);

                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        ws.post("", params, new WebService.JSON() {
                            @Override
                            public void responseData(JSONObject status, JSONObject data) {
//                               if(status.optString(""))
                                if (status.optString("statusRequest").equals("success")) {
                                    Log.d(test, data.toString());
//                                    Toast.makeText(AdminPlantState.this, data.toString(), Toast.LENGTH_SHORT).show();
                                    if (data.optString("status").equals("success")) {
                                        PlantState plantState = new PlantState();
                                        plantState.setCode(code_data);
                                        changePlaceData(plantState, "delete");
                                        dialog.dismiss();
                                    } else {
                                        dialog.dismiss();
                                    }
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
                dialog.dismiss();
            }
        });
        builder.show();

    }

    public void addPlantStateBtn(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlantState.this);
        final AlertDialog addDialog = builder.create();

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(R.layout.plant_state_add_layout, null);


        final EditText state_name_edit = (EditText) root.findViewById(R.id.state_name_edit);
        final EditText humidity_edit = (EditText) root.findViewById(R.id.humidity_edit);

        humidity_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String full = (String) s.toString();
                String[] array = full.split("\\.");

                try {
//                    Log.d(test,array[1]);
                    String left = "";
                    String right = "";
                    try{
                        left = array[0];
                        right = array[1];
                    }catch (Exception e)
                    {

                    }
                    if (left.length() > 2 || left.length() < 0 || right.length() > 2 || right.length() < 0) {
                        humidity_edit.setHint("0.0");
                        humidity_edit.setText("");
                        Toast.makeText(AdminPlantState.this,"Range : 0.01 ~ 99.99",Toast.LENGTH_SHORT).show();
                    }
                    else if(left.length() > 2 || left.length() < 0 ){
                        Log.d(test,"sinisini");
                        humidity_edit.setHint("0.0");
                        humidity_edit.setText("");
                        Toast.makeText(AdminPlantState.this,"Range : 0.01 ~ 99.99",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d(test + "sni", e.getLocalizedMessage());
                }


            }
        });
        final EditText temperature_edit = (EditText) root.findViewById(R.id.temperature_edit);

        temperature_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String full = (String) s.toString();
                String[] array = full.split("\\.");

                try {
//                    Log.d(test,array[1]);
                    String left = "";
                    String right = "";
                    try{
                        left = array[0];
                        right = array[1];
                    }catch (Exception e)
                    {

                    }
                    if (left.length() > 2 || left.length() < 0 || right.length() > 2 || right.length() < 0) {
                        temperature_edit.setHint("0.0");
                        temperature_edit.setText("");
                        Toast.makeText(AdminPlantState.this,"Range : 0.01 ~ 99.99",Toast.LENGTH_SHORT).show();
                    }
                    else if(left.length() > 2 || left.length() < 0 ){
                        Log.d(test,"sinisini");
                        temperature_edit.setHint("0.0");
                        temperature_edit.setText("");
                        Toast.makeText(AdminPlantState.this,"Range : 0.01 ~ 99.99",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d(test + "sni", e.getLocalizedMessage());
                }


            }
        });
        final EditText code_edit = (EditText) root.findViewById(R.id.code_edit);
        Button submit_btn = (Button) root.findViewById(R.id.submit_btn);
        Button cancel_btn = (Button) root.findViewById(R.id.cancel_btn);
        code_edit.setText("PL" + String.valueOf(generate_rand_number()));

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp_name = "";
                double humidity = 0.0;
                double temperature = 0.0;
                try {
                    temp_name = state_name_edit.getText().toString().trim();
                    humidity = Double.valueOf(humidity_edit.getText().toString().trim());
                    temperature = Double.valueOf(temperature_edit.getText().toString().trim());
                } catch (Exception e) {
                    Toast.makeText(AdminPlantState.this, "Please Enter Information ", Toast.LENGTH_SHORT).show();

                }

                if (temp_name.length() > 0 && !temp_name.equals("") && !humidity_edit.getText().toString().trim().equals("") && !temperature_edit.getText().toString().trim().equals("")) {
                    final HashMap<Object, Object> params = new HashMap<Object, Object>();
                    params.put("function", "fnPlantStateAdd");
                    params.put("code", code_edit.getText().toString().trim());
                    params.put("state_name", temp_name);
                    params.put("humidity", humidity);
                    params.put("temperature", temperature);

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            ws.post("", params, new WebService.JSON() {
                                @Override
                                public void responseData(JSONObject status, JSONObject data) {
                                    Log.d(test, data.toString());
                                    try {
                                        if (status.optString("statusRequest").equals("success")) {
                                            try {
                                                JSONObject temp = data.getJSONObject("data");
                                                PlantState temp_ps = new PlantState();
                                                temp_ps.setCode(temp.optString("code"));
                                                temp_ps.setState_name(temp.optString("state_name"));
                                                temp_ps.setHumidity(Float.valueOf(temp.optString("humidity")));
                                                temp_ps.setTemperature(Float.valueOf(temp.optString("temperature")));

                                                changePlaceData(temp_ps, "add");
                                                addDialog.dismiss();

                                            } catch (JSONException e) {
                                                Log.d(test, e.getLocalizedMessage());
                                            }
                                        } else {
                                            Toast.makeText(AdminPlantState.this, "Try Again", Toast.LENGTH_SHORT).show();
                                            addDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        Log.d(test, e.getLocalizedMessage());
                                    }

                                }
                            });
                        }
                    };
                    thread.start();

                }
                else {
                    if(temp_name.length()<=0)
                    {
                        Toast.makeText(AdminPlantState.this,"Please Enter Plant State Name",Toast.LENGTH_SHORT).show();
                    }
                    if(humidity_edit.getText().toString().trim().length()<=0)
                    {
                        Toast.makeText(AdminPlantState.this,"Please Enter Humidity value",Toast.LENGTH_SHORT).show();
                    }
                    if(temperature_edit.getText().toString().trim().length()<=0)
                    {
                        Toast.makeText(AdminPlantState.this,"Please Enter Temperature value",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.dismiss();
            }
        });
        addDialog.setView(root);
        addDialog.show();
    }

    public void editPlantStateBtn(View view) {
        View root = (View) view.getParent().getParent();
        TextView code = (TextView) root.findViewById(R.id.code);
        TextView state_name = (TextView) root.findViewById(R.id.state_name);
        TextView humidity = (TextView) root.findViewById(R.id.humidity);
        TextView temperature = (TextView) root.findViewById(R.id.temperature);

        String code_val = code.getText().toString().trim();
        final String state_name_val = state_name.getText().toString().trim();
        final String humidity_val = humidity.getText().toString().trim();
        final String temperature_val = temperature.getText().toString().trim();


        AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlantState.this);
        final AlertDialog editDialog = builder.create();

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View rootDialog = layoutInflater.inflate(R.layout.plant_state_update_layout, null);
        final EditText code_edit = (EditText) rootDialog.findViewById(R.id.code_edit);
        final EditText state_name_edit = (EditText) rootDialog.findViewById(R.id.state_name_edit);
        final EditText humidity_edit = (EditText) rootDialog.findViewById(R.id.humidity_edit);
        final EditText temperature_edit = (EditText) rootDialog.findViewById(R.id.temperature_edit);
        Button submit_btn = (Button) rootDialog.findViewById(R.id.submit_btn);
        Button cancel_btn = (Button) rootDialog.findViewById(R.id.cancel_btn);

        code_edit.setHint(code_val);
        code_edit.setText(code_val);

        state_name_edit.setHint(state_name_val);
        state_name_edit.setText(state_name_val);

        humidity_edit.setHint(humidity_val);
        humidity_edit.setText(humidity_val);
        humidity_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String full = (String) s.toString();
                String[] array = full.split("\\.");

                try {
//                    Log.d(test,array[1]);
                    String left = "";
                    String right = "";
                    try{
                        left = array[0];
                        right = array[1];
                    }catch (Exception e)
                    {

                    }
                    if (left.length() > 2 || left.length() < 0 || right.length() > 2 || right.length() < 0) {
                        humidity_edit.setHint("0.0");
                        humidity_edit.setText("");
                        Toast.makeText(AdminPlantState.this,"Range : 0.01 ~ 99.99",Toast.LENGTH_SHORT).show();
                    }
                    else if(left.length() > 2 || left.length() < 0 ){
                        Log.d(test,"sinisini");
                        humidity_edit.setHint("0.0");
                        humidity_edit.setText("");
                        Toast.makeText(AdminPlantState.this,"Range : 0.01 ~ 99.99",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d(test + "sni", e.getLocalizedMessage());
                }


            }
        });

        temperature_edit.setHint(temperature_val);
        temperature_edit.setText(temperature_val);
        temperature_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String full = (String) s.toString();
                String[] array = full.split("\\.");

                try {
//                    Log.d(test,array[1]);
                    String left = "";
                    String right = "";
                    try{
                        left = array[0];
                        right = array[1];
                    }catch (Exception e)
                    {

                    }
                    if (left.length() > 2 || left.length() < 0 || right.length() > 2 || right.length() < 0) {
                        temperature_edit.setHint("0.0");
                        temperature_edit.setText("");
                        Toast.makeText(AdminPlantState.this,"Range : 0.01 ~ 99.99",Toast.LENGTH_SHORT).show();
                    }
                    else if(left.length() > 2 || left.length() < 0 ){
                        temperature_edit.setHint("0.0");
                        temperature_edit.setText("");
                        Toast.makeText(AdminPlantState.this,"Range : 0.01 ~ 99.99",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.d(test + "sni", e.getLocalizedMessage());
                }


            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        String temp_code = code_edit.getText().toString().trim();
                        String temp_name = state_name_edit.getText().toString().trim();
                        float temp_humidity = Float.valueOf(humidity_edit.getText().toString().trim());
                        float temp_temperature = Float.valueOf(temperature_edit.getText().toString().trim());

                        if (temp_code.length() > 0 && !temp_code.equals("") && temp_name.length() > 0 && !temp_name.equals("") && humidity_edit.getText().toString().trim().length() > 0 && temperature_edit.getText().toString().trim().length() > 0) {
                            HashMap<Object, Object> params = new HashMap<Object, Object>();
                            params.put("function", "fnPlantStateUpdate");
                            params.put("code", temp_code);
                            params.put("state_name", temp_name);
                            params.put("humidity", temp_humidity);
                            params.put("temperature", temp_temperature);
                            ws.post("", params, new WebService.JSON() {
                                @Override
                                public void responseData(JSONObject status, JSONObject data) {
                                    Log.d(test, data.toString());
                                    try {
                                        if (status.optString("statusRequest").equals("success")) {
                                            JSONObject temp = data.getJSONObject("data");
                                            PlantState temp_ps = new PlantState();
                                            temp_ps.setCode(temp.optString("code"));
                                            temp_ps.setState_name(temp.optString("state_name"));
                                            temp_ps.setHumidity(Float.valueOf(temp.optString("humidity")));
                                            temp_ps.setTemperature(Float.valueOf(temp.optString("temperature")));

                                            changePlaceData(temp_ps, "update");
                                            editDialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        Log.d(test, e.getLocalizedMessage());
                                    }
                                }
                            });
                        }
                    }
                };
                thread.start();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.dismiss();
            }
        });
        editDialog.setView(rootDialog);
        editDialog.show();

    }

    public void changePlaceData(PlantState data, String action) {
        if (action.equals("Add") || action.equals("add")) {
            plantStates.add(data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantStateAdapterList(AdminPlantState.this, R.layout.plant_state_arraylist, plantStates);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        } else if (action.equals("Update") || action.equals("update")) {

            for (int i = 0; i < plantStates.size(); i++) {
                if (plantStates.get(i).getCode().equals(data.getCode())) {
                    PlantState temp_plant_update = plantStates.get(i);
                    temp_plant_update.setCode(data.getCode());
                    temp_plant_update.setState_name(data.getState_name());
                    temp_plant_update.setHumidity(data.getHumidity());
                    temp_plant_update.setTemperature(data.getTemperature());
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantStateAdapterList(AdminPlantState.this, R.layout.plant_state_arraylist, plantStates);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        } else if (action.equals("Delete") || action.equals("delete")) {
            for (int i = 0; i < plantStates.size(); i++) {
                if (plantStates.get(i).getCode() == data.getCode()) {
                    plantStates.remove(i);
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantStateAdapterList(AdminPlantState.this, R.layout.plant_state_arraylist, plantStates);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        }

    }

    public void setup_drawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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
            Intent intent = new Intent(AdminPlantState.this, AdminDevice.class);
            startActivity(intent);
        } else if (id == R.id.drawer_staff_menu) {
            Intent intent = new Intent(AdminPlantState.this, AdminWorker.class);
            startActivity(intent);

        }
        else if (id == R.id.drawer_place_menu) {
            Intent intent = new Intent(AdminPlantState.this,AdminPlace.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_menu) {
            Intent intent = new Intent(AdminPlantState.this,AdminPlant.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_state_menu) {
            Intent intent = new Intent(AdminPlantState.this,AdminPlantState.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_logout_menu) {
            Session.setCurrentContext(Login.class);
            Session.setUser(null);
            Intent intent = new Intent(AdminPlantState.this, Login.class);
            startActivity(intent);
            finish();
        }
//
//

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public int generate_rand_number() {
        int max = 999999;
        int min = 0;
        int temp_num = (int) ((Math.random() * ((max - min) + 1)) + min);
        ;
        return temp_num;
    }
}
