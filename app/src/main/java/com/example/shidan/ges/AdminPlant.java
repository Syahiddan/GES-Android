package com.example.shidan.ges;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.model.Place;
import com.example.shidan.ges.model.Plant;
import com.example.shidan.ges.model.PlantState;
import com.example.shidan.ges.model.User;
import com.example.shidan.ges.shidan.PlantAdapterList;
import com.example.shidan.ges.shidan.Session;
import com.example.shidan.ges.shidan.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminPlant extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //use for sorting
    private static final int ASCENDING = 1;
    private static final int DESCENDING = -1;
    private static final int NORMAL = 0;
    String test = "test";
    String cur_search_string = "";
    /////
    DrawerLayout drawer;
    Toolbar toolbar;
    RelativeLayout mainLayout;
    LayoutInflater li;
    View layout;
    WebService ws;
    ListView lv;
    PlantAdapterList pal;
    ArrayList<Plant> plants;
    //use for search and filter

    ArrayList<Place> result = new ArrayList<Place>();


    ArrayList<String> plant_list_name = new ArrayList<String>();
    ArrayList<String> plantState_list_name = new ArrayList<String>();
    ArrayList<String> guardian_list_name = new ArrayList<String>();
    ImageView cur_edit_image;


    AlertDialog alertModal_edit;

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
        layout = li.inflate(R.layout.activity_admin_plant, null);
        mainLayout.removeAllViews();
        mainLayout.addView(layout);

        ws = new WebService(AdminPlant.this);
        lv = (ListView) findViewById(R.id.list_device);
        plants = new ArrayList<Plant>();
        Thread thread = new Thread() {
            @Override
            public void run() {
//                setupSpinner();
                setup_plant_list();
                listViewListener();

            }
        };

        thread.start();


    }

    public void listViewListener() {
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
                    search_filter(plants, "");
                    cur_search_string = "";

                }
            }
        });

        try {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final RelativeLayout root = (RelativeLayout) pal.getView(position, view, parent);
                    final LinearLayout action_btn_lyt = (LinearLayout) root.findViewById(R.id.action_btn);
                    final LinearLayout layout_desc_title = (LinearLayout) root.findViewById(R.id.layout_desc_title);
                    final LinearLayout layout_desc = (LinearLayout) root.findViewById(R.id.layout_desc);

                    final Button btn_more_information = (Button) root.findViewById(R.id.btn_more_information);
//                    Log.d(test,"uina 1 click");


                }
            });
        } catch (Exception e) {
            Log.d(test, e.getLocalizedMessage());
        }

    }

    public void setup_plant_list() {

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
//                    search_filter(plants, "", filter_curr_btn);
//                    cur_search_string = "";

                }
            }
        });


        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("function", "fnPlantList");
        ws.post("", params, new WebService.JSON() {
            @Override
            public void responseData(JSONObject status, JSONObject data) {
                Log.d(test,status.toString());
                if (status.optString("statusRequest").equals("success")) {
                    Log.d(test,data.toString());
                    if (data != null) {

                        try {
//                                Log.d(test,data.getJSONArray("data").toString());
                            for (int i = 0; i < data.getJSONArray("data").length(); i++) {
                                JSONObject temp = (JSONObject) data.getJSONArray("data").get(i);
                                Plant temp_plant = new Plant(temp.optInt("id"));
                                temp_plant.setName(temp.optString("name"));
                                temp_plant.setScientific_name(temp.optString("scientific_name"));
                                temp_plant.setDescription(temp.optString("description"));
                                temp_plant.setImage(temp.optString("image"));
                                plants.add(temp_plant);
                            }


                            pal = new PlantAdapterList(AdminPlant.this, R.layout.plant_arraylist, plants);
                        } catch (JSONException e) {
                            Log.d(test, e.getLocalizedMessage());
                        }

                    }
                }

                lv.setAdapter(pal);
            }
        });
    }

    public void more_information_lv_child(View v) {

    }

    public void more_information(View v) {
        RelativeLayout root = (RelativeLayout) v.getParent().getParent().getParent();
        final LinearLayout action_btn_lyt = (LinearLayout) root.findViewById(R.id.action_btn);
        final LinearLayout layout_desc_title = (LinearLayout) root.findViewById(R.id.layout_desc_title);
        final LinearLayout layout_desc = (LinearLayout) root.findViewById(R.id.layout_desc);
        Button btn_more_information = (Button) v;

        btn_more_information.setText((btn_more_information.getText().equals("more information")) ? "less information" : "more information");
        action_btn_lyt.setVisibility((action_btn_lyt.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
        layout_desc_title.setVisibility((layout_desc_title.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
        layout_desc.setVisibility((layout_desc.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

    public void edit_information(View v) {
        RelativeLayout root = (RelativeLayout) v.getParent().getParent().getParent();
        final TextView plant_name = (TextView) root.findViewById(R.id.plant_name);
        final TextView plant_scientific_name = (TextView) root.findViewById(R.id.plant_scientific_name);
        final TextView plant_description = (TextView) root.findViewById(R.id.plant_description);
        final TextView plant_id = (TextView) root.findViewById(R.id.id_set);
        final ImageView plant_image = (ImageView) root.findViewById(R.id.plant_image);

        cur_edit_image = (ImageView) root.findViewById(R.id.edit_image);

        final AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlant.this);
        alertModal_edit = builder.create();

        LayoutInflater layoutInflater = LayoutInflater.from(AdminPlant.this);
        final View rootView = layoutInflater.inflate(R.layout.plant_update_layout, null);

        final TextView edit_set_id = (TextView) rootView.findViewById(R.id.edit_id_set);
        final EditText edit_name = (EditText) rootView.findViewById(R.id.edit_name);
        final EditText edit_scientific_name = (EditText) rootView.findViewById(R.id.edit_scientific_name);
        final EditText edit_description = (EditText) rootView.findViewById(R.id.edit_description);
        final ImageView edit_image = (ImageView) rootView.findViewById(R.id.edit_image);

        edit_set_id.setText(plant_id.getText().toString());
        edit_name.setText(plant_name.getText().toString());
        edit_scientific_name.setText(plant_scientific_name.getText().toString());
        edit_description.setText(plant_description.getText().toString());
        edit_image.setBackground(plant_image.getBackground());

        ((Button) rootView.findViewById(R.id.upload_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 1);
            }
        });
        ((Button) rootView.findViewById(R.id.submit_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Log.d(test, params.toString());

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        HashMap<Object, Object> params = new HashMap<Object, Object>();
//                    String tempImageBm = convertImageViewToString((ImageView) rootView.findViewById(R.id.edit_image));
//                    params.put("imageRes",tempImageBm);
                        params.put("function", "fnPlantUpdate");
                        params.put("id", edit_set_id.getText().toString());
                        params.put("name", (plant_name.getText().toString().equals(edit_name.getText().toString().trim()) || edit_name.getText().toString().trim().length() == 0) ? plant_name.getText().toString().trim() : edit_name.getText().toString().trim());
                        params.put("scientific_name", edit_scientific_name.getText().toString());
                        params.put("description", edit_description.getText().toString());
                        ws.post("", params, new WebService.JSON() {
                            @Override
                            public void responseData(JSONObject status, JSONObject data) {
                                try {
                                    if (status.getString("statusRequest").equals("success")) {
//                                        Log.d(test, data.toString());
                                        JSONObject temp_obj = data.getJSONObject("data");
                                        Plant temp = new Plant(temp_obj.optInt("id"));
                                        temp.setImage(temp_obj.optString("image"));
                                        temp.setName(temp_obj.optString("name"));
                                        temp.setScientific_name(temp_obj.optString("scientific_name"));
                                        temp.setDescription(temp_obj.optString("description"));
                                        Log.d(test, temp_obj.toString());
                                        changePlaceData(temp, "update");
                                        alertModal_edit.dismiss();
                                    }
                                } catch (JSONException e) {
                                    Log.d(test, e.getLocalizedMessage());
                                }


                            }
                        });

                    }
                };
                thread.start();


            }
        });
        ((Button) rootView.findViewById(R.id.cancel_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertModal_edit.dismiss();
//                plant_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                plant_image.setImageDrawable(edit_image.getBackground());
            }
        });
        alertModal_edit.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//               Log.d(test,"sini sini");
                plant_image.setImageDrawable(edit_image.getBackground());
            }
        });
        alertModal_edit.setView(rootView);
        alertModal_edit.show();
    }

    public void addPlantBtn(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlant.this);
        final AlertDialog alertModal = builder.create();
        LayoutInflater layoutInflater = LayoutInflater.from(AdminPlant.this);
        View rootView = layoutInflater.inflate(R.layout.plant_add_layout, null);
        LinearLayout root = (LinearLayout) rootView;

        final EditText add_name  = (EditText) rootView.findViewById(R.id.add_name);
        final EditText add_scientific_name = (EditText)rootView.findViewById(R.id.add_scientific_name);
        final EditText add_description = (EditText) rootView.findViewById(R.id.add_description);

        Button btn_add = (Button) rootView.findViewById(R.id.submit_btn);
        Button btn_cancel = (Button) rootView.findViewById(R.id.cancel_btn);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(add_name.getText().toString().trim().length()>0 && add_scientific_name.getText().toString().trim().length()>0 && add_description.getText().toString().length()>0){
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            HashMap<Object,Object> params = new HashMap<Object, Object>();
                            params.put("function","fnPlantAdd");
                            params.put("name",add_name.getText().toString());
                            params.put("scientific_name",add_scientific_name.getText().toString());
                            params.put("description",add_description.getText().toString());

                            ws.post("", params, new WebService.JSON() {
                                @Override
                                public void responseData(JSONObject status, JSONObject data) {
                                    try{
                                        if (status.getString("statusRequest").equals("success")){
                                            Log.d(test,data.toString());
                                            JSONObject temp_obj = data.getJSONObject("data");
                                            Plant temp = new Plant(temp_obj.optInt("id"));
                                            temp.setName(temp_obj.optString("name"));
                                            temp.setScientific_name(temp_obj.optString("scientific_name"));
                                            temp.setDescription(temp_obj.optString("description"));
                                            temp.setImage(temp_obj.optString("image"));

                                            changePlaceData(temp,"add");
                                            alertModal.dismiss();

                                        }
                                        else
                                        {
                                            Log.d(test,"unsuccess");
                                        }
                                    }catch (JSONException e)
                                    {
                                        Log.d(test,e.getLocalizedMessage());
                                    }

                                }
                            });

                        }
                    };
                    thread.start();
                    int count = 0;
                    while (thread.isAlive())
                    {
                        Log.d(test,String.valueOf(count++));
                    }

                }
                else
                {
                    if(add_name.getText().toString().trim().length()<=0)
                    {
                        Toast.makeText(AdminPlant.this,"Name empty",Toast.LENGTH_SHORT).show();
                    }
                    if(add_scientific_name.getText().toString().trim().length() <=0)
                    {
                        Toast.makeText(AdminPlant.this,"Scientific name empty",Toast.LENGTH_SHORT).show();
                    }
                    if(add_description.getText().toString().trim().length()<=0)
                    {
                        Toast.makeText(AdminPlant.this,"Description empty",Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertModal.dismiss();
            }
        });
        alertModal.setView(rootView);
        alertModal.show();



//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        Plant plant_data = getPlantData(plants, plant_spinner.getSelectedItem().toString());
//                        PlantState plantState_data = getPlantStateData(plant_states, plant_state_spinner.getSelectedItem().toString());
//                        User guardian_data = getGuardianData(guardians, guardian_spinner.getSelectedItem().toString());
//                        final String new_placeName = edit_placeName.getText().toString().trim();
//                        try {
//                            Log.d(test, edit_placeName.getText().toString());
//                            if (new_placeName.length() > 0) {
//                                HashMap<Object, Object> params = new HashMap<Object, Object>();
//                                params.put("function", "fnPlaceAdd");
//                                params.put("place_name", new_placeName);
//                                params.put("plant_id", plant_data.getId());
//                                params.put("monitor_id", guardian_data.getWorker_id());
//                                params.put("plant_state", plantState_data.getCode());
//                                ws.post("", params, new WebService.JSON() {
//                                    @Override
//                                    public void responseData(JSONObject status, JSONObject data) {
//                                        try {
//                                            JSONObject temp_object = data.getJSONObject("data");
//                                            Place temp_place_update = new Place(temp_object.optString("place_id"));
//                                            temp_place_update.setPlace_name(temp_object.optString("place_name"));
//
//                                            Plant temp_plant_update = new Plant(temp_object.optInt("plant_id"));
//                                            temp_plant_update.setName(temp_object.optString("plant_name"));
//                                            temp_plant_update.setDescription(temp_object.optString("plant_description"));
//                                            temp_plant_update.setScientific_name(temp_object.optString("plant_sci_name"));
//                                            temp_plant_update.setImage(temp_object.optString("plant_image"));
//
//                                            PlantState temp_ps_update = new PlantState();
//                                            temp_ps_update.setCode(temp_object.optString("ps_code"));
//                                            temp_ps_update.setState_name(temp_object.optString("ps_name"));
//                                            temp_ps_update.setTemperature(Float.valueOf(temp_object.optString("ps_temperature")));
//                                            temp_ps_update.setHumidity(Float.valueOf(temp_object.optString("ps_humidity")));
//
//                                            User temp_guardian_update = new User();
//                                            temp_guardian_update.setWorker_id(temp_object.optString("u_id"));
//                                            temp_guardian_update.setFname(temp_object.optString("u_fname"));
//                                            temp_guardian_update.setMidname(temp_object.optString("u_midname"));
//                                            temp_guardian_update.setLname(temp_object.optString("u_lname"));
//                                            temp_guardian_update.setEmail(temp_object.optString("u_email"));
//                                            temp_guardian_update.setPhone(temp_object.optString("u_phone"));
//                                            temp_guardian_update.setImage(temp_object.optString("u_image"));
//
//                                            temp_place_update.setPlant(temp_plant_update);
//                                            temp_place_update.setPlantState(temp_ps_update);
//                                            temp_place_update.setMonitor(temp_guardian_update);
//
//                                            changePlaceData(temp_place_update, "add");
//                                        } catch (JSONException e) {
//                                            Log.d(test, e.getLocalizedMessage());
//                                        }
//
//                                    }
//                                });
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(AdminPlant.this, "Data Add Submitted", Toast.LENGTH_SHORT).show();
//                                        alertModal.dismiss();
//                                    }
//                                });
//                            } else {
//                                if (new_placeName.length() <= 0) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(AdminPlant.this, "Place Name Empty!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.d(test, e.getLocalizedMessage());
//                        }
//                    }
//                };
//                thread.start();
//            }
//        });
//
//        close_modal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertModal.dismiss();
//            }
//        });
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertModal.dismiss();
//            }
//        });
//
//        ArrayAdapter<String> adapter_plant = new ArrayAdapter<String>(AdminPlant.this, android.R.layout.simple_spinner_dropdown_item, plant_list_name);
//        plant_spinner.setAdapter(adapter_plant);
//
//        ArrayAdapter<String> adapter_plant_state = new ArrayAdapter<String>(AdminPlant.this, android.R.layout.simple_spinner_dropdown_item, plantState_list_name);
//        plant_state_spinner.setAdapter(adapter_plant_state);
//
//        ArrayAdapter<String> adapter_guardian = new ArrayAdapter<String>(AdminPlant.this, android.R.layout.simple_spinner_dropdown_item, guardian_list_name);
//        guardian_spinner.setAdapter(adapter_guardian);
    }

    public void deletePlantBtn(View v)
    {
        RelativeLayout root = (RelativeLayout) v.getParent().getParent().getParent();
        final TextView plant_id = (TextView) root.findViewById(R.id.id_set);

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlant.this);
        final AlertDialog alert_delete = builder.create();

        builder.setMessage("Are you sure to delete this plant data");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        HashMap<Object,Object> params = new HashMap<Object, Object>();
                        params.put("function","fnPlantDelete");
                        params.put("plant_id",plant_id.getText().toString().trim());
                        ws.post("", params, new WebService.JSON() {
                            @Override
                            public void responseData(JSONObject status, JSONObject data) {
                                try{
                                    if (status.getString("statusRequest").equals("success")){
                                        if(data.getString("status").equals("success"))
                                        {
                                            changePlaceData(new Plant(Integer.valueOf(plant_id.getText().toString().trim())),"delete");
                                            alert_delete.dismiss();
                                        }
                                    }
                                    else {
                                        Log.d(test,"unsuccess");
                                    }
                                }catch (JSONException e)
                                {
                                    Log.d(test,e.getLocalizedMessage());
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
                alert_delete.dismiss();
            }
        });
        builder.show();


    }
    public void changePlaceData(Plant data, String action) {
        if (action.equals("Add") || action.equals("add")) {
            plants.add(data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantAdapterList(AdminPlant.this, R.layout.plant_arraylist, plants);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        } else if (action.equals("Update") || action.equals("update")) {

            for (int i = 0; i < plants.size(); i++) {
                if (plants.get(i).getId() == data.getId()) {
                    Plant temp_plant_update = plants.get(i);
                    temp_plant_update.setId(data.getId());
                    temp_plant_update.setName(data.getName());
                    temp_plant_update.setScientific_name(data.getScientific_name());
                    temp_plant_update.setDescription(data.getDescription());
                    temp_plant_update.setImage(data.getImage());
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantAdapterList(AdminPlant.this, R.layout.plant_arraylist, plants);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        } else if (action.equals("Delete") || action.equals("delete")) {
            for (int i = 0; i < plants.size(); i++) {
                if (plants.get(i).getId() == data.getId()) {
                    plants.remove(i);
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantAdapterList(AdminPlant.this, R.layout.plant_arraylist, plants);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        }

    }

    public Plant getPlantData(ArrayList<Plant> temp, int search) {
        Plant temp_data = new Plant(-1);
//        Log.d(test,String.valueOf(temp.size()));
        try {
            for (int i = 0; i < temp.size(); i++) {

                Plant temp_plant = (Plant) temp.get(i);
//                Log.d(test,temp_plant.getName());
                if (temp_plant.getId() == search) {
                    return temp_plant;
                }
            }
        } catch (Exception e) {
            Log.d(test, e.getLocalizedMessage());

        }
        return temp_data;
    }

    public PlantState getPlantStateData(ArrayList<PlantState> temp, String search) {
        PlantState temp_data = new PlantState();
        temp_data.setState_name("-1");
//        Log.d(test,String.valueOf(temp.size()));
        try {
            for (int i = 0; i < temp.size(); i++) {

                PlantState temp_plantState = (PlantState) temp.get(i);
//                Log.d(test,temp_plantState.getState_name());
                if (temp_plantState.getState_name().equals(search)) {
                    return temp_plantState;
                }
            }
        } catch (Exception e) {
            Log.d(test, e.getLocalizedMessage());

        }
        return temp_data;
    }

    public User getGuardianData(ArrayList<User> temp, String search) {
        User temp_data = new User();
        temp_data.setFname("-1");
        temp_data.setMidname("-1");

//        Log.d(test,String.valueOf(temp.size()));
        try {
            for (int i = 0; i < temp.size(); i++) {

                User temp_guardian = (User) temp.get(i);
//                Log.d(test,temp_guardian.getFname());
                String append_name = temp_guardian.getFname() + " " + temp_guardian.getMidname() + " " + temp_guardian.getLname();
                if (append_name.equals(search)) {
                    return temp_guardian;
                }
            }
        } catch (Exception e) {
            Log.d(test, e.getLocalizedMessage());

        }
        return temp_data;
    }

    private void search_filter(final ArrayList<Plant> places, String search) {
        final ArrayList<Plant> temp = new ArrayList<Plant>();
        if (search.equals("") ) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantAdapterList(AdminPlant.this, R.layout.plant_arraylist, plants);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
            Log.d(test, "p");
        }
        else if (!search.equals("")) {
            //s

            for (int i = 0; i < places.size(); i++) {
                if (plants.get(i).getName().toLowerCase().contains(search.toLowerCase())) {
                    temp.add(places.get(i));
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantAdapterList(AdminPlant.this, R.layout.plant_arraylist, temp);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
            Log.d(test, "s");


        }
        Log.d(test, String.valueOf(temp.size()));
    }

    private void sorting(final ArrayList<Plant> plant, String category, int sort) {
        //ascending 1
        //descending -1
        //normal 0
//        Log.d(test,category);
        final ArrayList<Plant> temp = plants;
        if (sort == NORMAL) {
            Log.d(test, "p");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlantAdapterList(AdminPlant.this, R.layout.place_arraylist, plants);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });

        } else {
            if (sort == ASCENDING) {

//                if (category.equals("NAME") || category.equals("Name")) {
//                    Comparator<Plant> compareByPlaceNameAsc = new Comparator<Plant>(){
//
//                    };
//                    Collections.sort(temp, compareByPlaceNameAsc);
//                    Log.d(test, "sa name");
//                } else if (category.equals("GUARDIAN") || category.equals("Guardian")) {
//                    Comparator<Place> compareByPlaceGuardianAsc = new Comparator<Place>() {
//                        @Override
//                        public int compare(Place o1, Place o2) {
//                            return o1.getMonitor().getFname().compareTo(o2.getMonitor().getFname());
//                        }
//                    };
//                    Collections.sort(temp, compareByPlaceGuardianAsc);
//                    Log.d(test, "sa guardian");
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        pal = new PlaceAdapterList(AdminPlant.this, R.layout.place_arraylist, temp);
//                        lv.setAdapter(pal);
//                        lv.invalidate();
//                    }
//                });
//
//            } else if (sort == DESCENDING) {
//                if (category.equals("NAME") || category.equals("Name")) {
//                    Comparator<Place> compareByPlaceNamedesc = new Comparator<Place>() {
//                        @Override
//                        public int compare(Place o1, Place o2) {
//                            return o2.getPlace_name().compareTo(o1.getPlace_name());
//                        }
//                    };
//                    Collections.sort(temp, compareByPlaceNamedesc);
//                    Log.d(test, "sd name");
//                } else if (category.equals("GUARDIAN") || category.equals("Guardian")) {
//                    Comparator<Place> compareByPlaceGuardiandesc = new Comparator<Place>() {
//                        @Override
//                        public int compare(Place o1, Place o2) {
//                            return o2.getMonitor().getFname().compareTo(o1.getMonitor().getFname());
//                        }
//                    };
//                    Collections.sort(temp, compareByPlaceGuardiandesc);
//                    Log.d(test, "sd guardian");
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        pal = new PlaceAdapterList(AdminPlant.this, R.layout.place_arraylist, temp);
//                        lv.setAdapter(pal);
//                        lv.invalidate();
//                    }
//                });
            }
        }
    }

//    public void submitEditData(View view) {
//        final LinearLayout root = (LinearLayout) view.getParent().getParent();
//        final LinearLayout layout_edit = (LinearLayout) root.findViewById(R.id.layout_edit);
//
//        final EditText edit_placeName = (EditText) root.findViewById(R.id.edit_placeName);
//        final Spinner plant_spinner = (Spinner) root.findViewById(R.id.plant_spinner);
//        final Spinner plant_state_spinner = (Spinner) root.findViewById(R.id.plant_state_spinner);
//        final Spinner guardian_spinner = (Spinner) root.findViewById(R.id.guardian_spinner);
//
//        final String curr_place_id = ((TextView) root.findViewById(R.id.edit_id)).getText().toString().trim();
//        final String new_placeName = edit_placeName.getText().toString().trim();
//
//
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                Plant plant_data = getPlantData(plants, plant_spinner.getSelectedItem().toString());
//                PlantState plantState_data = getPlantStateData(plant_states, plant_state_spinner.getSelectedItem().toString());
//                User guardian_data = getGuardianData(guardians, guardian_spinner.getSelectedItem().toString());
//
//                try {
//
//
//                    if (curr_place_id.length() > 0 && new_placeName.length() > 0) {
//                        HashMap<Object, Object> params = new HashMap<Object, Object>();
//                        params.put("function", "fnPlaceUpdate");
//                        params.put("place_id", curr_place_id);
//                        params.put("place_name", new_placeName);
//                        params.put("plant_id", plant_data.getId());
//                        params.put("monitor_id", guardian_data.getWorker_id());
//                        params.put("plant_state", plantState_data.getCode());
//                        Log.d(test, guardian_data.getWorker_id());
//                        ws.post("", params, new WebService.JSON() {
//                            @Override
//                            public void responseData(JSONObject status, JSONObject data) {
//                                Log.d(test, data.toString());
//                                try {
//                                    JSONObject temp_object = data.getJSONObject("data");
//                                    Place temp_place_update = new Place(temp_object.optString("place_id"));
//                                    temp_place_update.setPlace_name(temp_object.optString("place_name"));
//
//                                    Plant temp_plant_update = new Plant(temp_object.optInt("plant_id"));
//                                    temp_plant_update.setName(temp_object.optString("plant_name"));
//                                    temp_plant_update.setDescription(temp_object.optString("plant_description"));
//                                    temp_plant_update.setScientific_name(temp_object.optString("plant_sci_name"));
//                                    temp_plant_update.setImage(temp_object.optString("plant_image"));
//
//                                    PlantState temp_ps_update = new PlantState();
//                                    temp_ps_update.setCode(temp_object.optString("ps_code"));
//                                    temp_ps_update.setState_name(temp_object.optString("ps_name"));
//                                    temp_ps_update.setTemperature(Float.valueOf(temp_object.optString("ps_temperature")));
//                                    temp_ps_update.setHumidity(Float.valueOf(temp_object.optString("ps_humidity")));
//
//                                    User temp_guardian_update = new User();
//                                    temp_guardian_update.setWorker_id(temp_object.optString("u_id"));
//                                    temp_guardian_update.setFname(temp_object.optString("u_fname"));
//                                    temp_guardian_update.setMidname(temp_object.optString("u_midname"));
//                                    temp_guardian_update.setLname(temp_object.optString("u_lname"));
//                                    temp_guardian_update.setEmail(temp_object.optString("u_email"));
//                                    temp_guardian_update.setPhone(temp_object.optString("u_phone"));
//                                    temp_guardian_update.setImage(temp_object.optString("u_image"));
//
//                                    temp_place_update.setPlant(temp_plant_update);
//                                    temp_place_update.setPlantState(temp_ps_update);
//                                    temp_place_update.setMonitor(temp_guardian_update);
//
//                                    changePlaceData(temp_place_update, "Update");
//
//                                } catch (JSONException e) {
//                                    Log.d(test, e.getLocalizedMessage());
//                                }
//
//                            }
//                        });
//
//
////
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(AdminPlant.this, "Data Submitted", Toast.LENGTH_SHORT).show();
//                                layout_edit.setVisibility(View.GONE);
//                            }
//                        });
//                    } else {
//                        if (new_placeName.length() <= 0) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(AdminPlant.this, "Place Name Empty!", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.d(test, e.getLocalizedMessage());
//                }
//            }
//        };
//        thread.start();
//
//
//    }



    public void sortingPlace(View view) {
        LinearLayout root = (LinearLayout) view.getParent();
        for (int i = 0; i < root.getChildCount() - 1; i++) {
            Button tempBtn = (Button) root.getChildAt(i);
            tempBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        final Button cur_btn = (Button) view;
        if (cur_btn.getTag() == null) {
            cur_btn.setTag(R.drawable.icon_sorting_down);
            cur_btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_sorting_down, 0);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    sorting(pal.getAllData(), (cur_btn).getText().toString(), DESCENDING);
                }
            };
            thread.start();
            (cur_btn.getCompoundDrawables()[2]).setColorFilter(getResources().getColor(R.color.light_slate_blue), PorterDuff.Mode.SRC_ATOP);
        } else {
            cur_btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, ((Integer) cur_btn.getTag()).equals(R.drawable.icon_sorting_down) ? R.drawable.icon_sorting_up : R.drawable.icon_sorting_down, 0);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    sorting(pal.getAllData(), (cur_btn).getText().toString(), ((Integer) cur_btn.getTag()).equals(R.drawable.icon_sorting_down) ? DESCENDING : ASCENDING);
                }
            };
            thread.start();
            cur_btn.setTag(((Integer) cur_btn.getTag()).equals(R.drawable.icon_sorting_down) ? R.drawable.icon_sorting_up : R.drawable.icon_sorting_down);
            (cur_btn.getCompoundDrawables()[2]).setColorFilter(getResources().getColor(R.color.light_slate_blue), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void filterPlantSearch(View view) {
        final View v = view;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    EditText temp_search = ((View) v.getParent()).findViewById(R.id.input_plant_name);
                    final String temp = temp_search.getText().toString().trim();

                    if (temp.length() > 0) {
                        Log.d(test,"masuk mne");
                        search_filter(plants, temp);
                        cur_search_string = temp;
                    }

                } catch (Exception e) {
                    Log.d(test, e.getLocalizedMessage());
                }
            }
        };
        thread.start();

    }

//    public void filterPlacesBtn(View view) {
//        try {
//            TextView cur_btn = (TextView) view;
//            LinearLayout root = (LinearLayout) view.getParent();
//            for (int i = 1; i < root.getChildCount(); i++) {
//                TextView temp = (TextView) root.getChildAt(i);
//                temp.setEnabled(true);
//                temp.setBackgroundResource(R.drawable.simple_bubble_btn);
//            }
//            cur_btn.setEnabled(false);
//            cur_btn.setBackgroundResource(R.drawable.custom_btn_success);
//
//            filter_curr_btn = (cur_btn.getText().toString().equals("ALL")) ? 0 : (cur_btn.getText().toString().equals("EXIST")) ? 1 : (cur_btn.getText().toString().equals("NOT EXIST")) ? 2 : -1;
//            search_filter(places, cur_search_string, filter_curr_btn);
//
//        } catch (Exception e) {
//            Log.d(test, e.getLocalizedMessage());
//        }
//    }

    public void open_filter_layout(View view) {
        RelativeLayout root = (RelativeLayout) view.getParent().getParent();
        LinearLayout tool_footer = root.findViewById(R.id.tool_footer);
        tool_footer.setVisibility((tool_footer.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);

    }

    public void setup_drawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private String convertImageViewToString(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] img_data = stream.toByteArray();

        String imageRes = Base64.encodeToString(img_data, Base64.DEFAULT);
        return imageRes;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                try {
                    Uri selectedImage = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ((ImageView) alertModal_edit.findViewById(R.id.edit_image)).setBackground(new BitmapDrawable(getResources(), bitmap));
//                    cur_edit_image.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                } catch (Exception e) {
                    Log.d(test, e.getLocalizedMessage());
                }
            }

        }
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
            Intent intent = new Intent(AdminPlant.this, AdminDevice.class);
            startActivity(intent);
        } else if (id == R.id.drawer_staff_menu) {
            Intent intent = new Intent(AdminPlant.this, AdminWorker.class);
            startActivity(intent);

        }
        else if (id == R.id.drawer_place_menu) {
            Intent intent = new Intent(AdminPlant.this,AdminPlace.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_menu) {
            Intent intent = new Intent(AdminPlant.this,AdminPlant.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_state_menu) {
            Intent intent = new Intent(AdminPlant.this,AdminPlantState.class);
            startActivity(intent);
        }else if (id == R.id.drawer_logout_menu) {
            Session.setCurrentContext(Login.class);
            Session.setUser(null);
            Intent intent = new Intent(AdminPlant.this, Login.class);
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
