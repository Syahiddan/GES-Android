package com.example.shidan.ges;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.model.Place;
import com.example.shidan.ges.model.Plant;
import com.example.shidan.ges.model.PlantState;
import com.example.shidan.ges.model.User;
import com.example.shidan.ges.shidan.PlaceAdapterList;
import com.example.shidan.ges.shidan.Session;
import com.example.shidan.ges.shidan.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AdminPlace extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //use for sorting
    private static final int ASCENDING = 1;
    private static final int DESCENDING = -1;
    private static final int NORMAL = 0;
    String test = "test";
    /////
    DrawerLayout drawer;
    Toolbar toolbar;
    RelativeLayout mainLayout;
    LayoutInflater li;
    View layout;
    WebService ws;
    ListView lv;
    PlaceAdapterList pal;
    ArrayList<Place> places;
    //use for search and filter
    ArrayList<Place> result = new ArrayList<Place>();
    String cur_search_string = "";
    int filter_curr_btn = -1;

    ArrayList<Plant> plants = new ArrayList<Plant>();
    ArrayList<PlantState> plant_states = new ArrayList<PlantState>();
    ArrayList<User> guardians = new ArrayList<User>();

    ArrayList<String> plant_list_name = new ArrayList<String>();
    ArrayList<String> plantState_list_name = new ArrayList<String>();
    ArrayList<String> guardian_list_name = new ArrayList<String>();

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
        layout = li.inflate(R.layout.activity_admin_place, null);
        mainLayout.removeAllViews();
        mainLayout.addView(layout);

        ws = new WebService(AdminPlace.this);
        lv = (ListView) findViewById(R.id.list_device);
        places = new ArrayList<Place>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                setupSpinner();
                setup_place_list();
                listViewListener();

            }
        };

        thread.start();


    }

    public void setupSpinner() {
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("function", "fnGetPlaceEditOption");
        ws.post("", params, new WebService.JSON() {
            @Override
            public void responseData(JSONObject status, JSONObject data) {
                try {
                    plant_list_name = new ArrayList<String>();
                    plantState_list_name = new ArrayList<String>();
                    guardian_list_name = new ArrayList<String>();

                    JSONArray plant_list = data.getJSONArray("plant_list");
                    JSONArray plantState_list = data.getJSONArray("plant_state_list");
                    JSONArray guardian_list = data.getJSONArray("guardian_list");

                    for (int i = 0; i < plant_list.length(); i++) {
                        JSONObject temp = (JSONObject) plant_list.get(i);
                        Plant plant_list_temp = new Plant(temp.optInt("id"));
                        plant_list_temp.setName(temp.optString("name"));
                        plants.add(plant_list_temp);
                        plant_list_name.add(temp.optString("name"));

                    }
                    for (int i = 0; i < plantState_list.length(); i++) {
                        JSONObject temp = (JSONObject) plantState_list.get(i);
                        PlantState plantState_temp = new PlantState();
                        plantState_temp.setCode(temp.optString("code"));
                        plantState_temp.setState_name(temp.optString("state_name"));
                        plant_states.add(plantState_temp);
                        plantState_list_name.add(temp.optString("state_name"));
                    }

                    for (int i = 0; i < guardian_list.length(); i++) {
                        JSONObject temp = (JSONObject) guardian_list.get(i);
                        User user_temp = new User();
                        user_temp.setWorker_id(temp.optString("worker_id"));
                        user_temp.setFname(temp.optString("fname"));
                        user_temp.setMidname(temp.optString("midname"));
                        user_temp.setLname(temp.optString("lname"));
                        guardians.add(user_temp);
                        guardian_list_name.add(temp.optString("fname") + " " + temp.optString("midname") + " " + temp.optString("lname"));
                    }
                } catch (JSONException e) {
                    Log.d(test, e.getLocalizedMessage());
                }

            }
        });

    }

    public void listViewListener() {
        try {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final RelativeLayout root = (RelativeLayout) pal.getView(position, view, parent);
                    final LinearLayout layout_info = (LinearLayout) root.findViewById(R.id.layout_info);
                    final LinearLayout action_layout = (LinearLayout) root.findViewById(R.id.action_layout);
                    final TextView more_detail_btn = (TextView) root.findViewById(R.id.more_detail_btn);

                    TextView btn_edit = (TextView) action_layout.findViewById(R.id.btn_edit);
                    TextView btn_delete = (TextView) action_layout.findViewById(R.id.btn_delete);
                    TextView btn_collapse = (TextView) action_layout.findViewById(R.id.btn_collapse);

                    final String place_id = ((TextView)layout_info.findViewById(R.id.place_id)).getText().toString();
                    layout_info.setVisibility((layout_info.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
                    action_layout.setVisibility((layout_info.getVisibility() == View.VISIBLE) ? View.VISIBLE : View.GONE);
                    more_detail_btn.setVisibility((layout_info.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);

                    more_detail_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(AdminPlace.this, "ui", Toast.LENGTH_SHORT).show();
                        }
                    });

                    btn_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(test, "edit btn");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlace.this);
                            final AlertDialog alertModal = builder.create();
                            LayoutInflater layoutInflater = LayoutInflater.from(AdminPlace.this);
                            View rootView = layoutInflater.inflate(R.layout.place_update_layout, null);

                            final TextView btn_modal_close = (TextView) rootView.findViewById(R.id.btn_modal_close);
                            final Button btn_submit = (Button) rootView.findViewById(R.id.btn_submit);
                            final TextView edit_id = (TextView) rootView.findViewById(R.id.edit_id);
                            final EditText placeName = (EditText) rootView.findViewById(R.id.edit_placeName);
                            final Spinner plant_spinner = (Spinner) rootView.findViewById(R.id.plant_spinner);
                            final Spinner plant_state_spinner = (Spinner) rootView.findViewById(R.id.plant_state_spinner);
                            final Spinner guardian_spinner = (Spinner) rootView.findViewById(R.id.guardian_spinner);

                            edit_id.setText(((TextView) layout_info.findViewById(R.id.place_id)).getText().toString());
                            placeName.setText(((TextView) layout_info.findViewById(R.id.place_sec_name)).getText().toString());

                            ArrayAdapter<String> adapter_plant = new ArrayAdapter<String>(AdminPlace.this, android.R.layout.simple_spinner_dropdown_item, plant_list_name);
                            plant_spinner.setAdapter(adapter_plant);
                            int pos_plant = adapter_plant.getPosition(((TextView) layout_info.findViewById(R.id.plant_name)).getText().toString());
                            plant_spinner.setSelection(pos_plant);

                            ArrayAdapter<String> adapter_plant_state = new ArrayAdapter<String>(AdminPlace.this, android.R.layout.simple_spinner_dropdown_item, plantState_list_name);
                            plant_state_spinner.setAdapter(adapter_plant_state);
                            int pos_plant_state = adapter_plant_state.getPosition(((TextView) layout_info.findViewById(R.id.plant_state)).getText().toString());
                            plant_state_spinner.setSelection(pos_plant_state);


                            ArrayAdapter<String> adapter_guardian = new ArrayAdapter<String>(AdminPlace.this, android.R.layout.simple_spinner_dropdown_item, guardian_list_name);
                            guardian_spinner.setAdapter(adapter_guardian);
                            int pos_guardian = adapter_guardian.getPosition(((TextView) layout_info.findViewById(R.id.place_sec_guardian)).getText().toString());
                            guardian_spinner.setSelection(pos_guardian);

                            btn_modal_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertModal.dismiss();
                                }
                            });
                            btn_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    submitEditData(v);
                                    alertModal.dismiss();
                                }
                            });
                            alertModal.setView(rootView);
                            alertModal.show();
                        }
                    });
                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(test, "delete btn");
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlace.this);
                            builder.setMessage("Are you sure?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            HashMap<Object, Object> params = new HashMap<Object, Object>();
                                            params.put("function","fnPlaceDelete");
                                            params.put("place_id",place_id);
                                            ws.post("", params, new WebService.JSON() {
                                                @Override
                                                public void responseData(JSONObject status, JSONObject data) {
                                                    Log.d(test,status.toString() +" "+ data.toString());
                                                    try{
                                                        if(status.getString("statusRequest").equals("success"))
                                                        {


                                                            Place temp = new Place(place_id);
                                                            if(((JSONObject)data.getJSONObject("data")).getString("status").equals("success"))
                                                            {
                                                                Toast.makeText(AdminPlace.this,"Place Information Deleted Succesfully",Toast.LENGTH_SHORT).show();
                                                                changePlaceData(temp, "delete");
                                                                dialog.dismiss();

                                                            }else if(((JSONObject)data.getJSONObject("data")).getString("status").equals("deleted"))
                                                            {
                                                                Toast.makeText(AdminPlace.this,"Place Information Already Deleted ",Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                            else if(((JSONObject)data.getJSONObject("data")).getString("status").equals("failed"))
                                                            {
                                                                Toast.makeText(AdminPlace.this,"Something went wrong.. Try Again",Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }

                                                        }
                                                    }catch (JSONException e)
                                                    {

                                                    }
                                                }
                                            });
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }
                            ).show();
                        }
                    });
                    btn_collapse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(test, "collapse btn");
//                            root.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                        }

                    });


                }
            });
        } catch (Exception e) {
            Log.d(test, e.getLocalizedMessage());
        }

    }

    public void setup_place_list() {
        EditText input_place_name = (EditText) findViewById(R.id.input_place_name);
        input_place_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    search_filter(places, "", filter_curr_btn);
                    cur_search_string = "";

                }
            }
        });


        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("function", "fnPlaceList");
        ws.post("", params, new WebService.JSON() {
            @Override
            public void responseData(JSONObject status, JSONObject data) {
                Log.d(test,data.toString());
                if (status.optString("statusRequest").equals("success")) {

                    if (data != null) {

                        try {
//                            Log.d(test, data.getJSONArray("place").get(0).toString());
                            for (int i = 0; i < data.getJSONArray("place").length(); i++) {
                                JSONObject temp = (JSONObject) data.getJSONArray("place").get(i);
//                                Log.d(test,temp.toString());
                                Place place_temp = new Place(temp.getString("name"));
                                place_temp.setPlace_id(temp.optString("id"));
                                place_temp.setPlace_name(temp.getString("name"));

                                Plant plant_temp = new Plant(temp.optInt("plant_id"));
                                JSONObject temp2 = (JSONObject) temp.getJSONObject("plant_detail");
                                plant_temp.setId(temp.optInt("plant_id"));
                                plant_temp.setName(temp2.optString("name"));
                                plant_temp.setScientific_name(temp2.optString("scientific_name"));
                                plant_temp.setDescription(temp2.optString("description"));
                                plant_temp.setImage(temp2.optString("image"));
                                place_temp.setPlant(plant_temp);

                                PlantState plantState_temp = new PlantState();
                                JSONObject temp3 = (JSONObject) temp.getJSONObject("plant_state_info");
                                plantState_temp.setCode(temp3.optString("code"));
                                plantState_temp.setState_name(temp3.optString("state_name"));
                                plantState_temp.setHumidity(Float.valueOf(temp3.optString("humidity")));
                                plantState_temp.setTemperature(Float.valueOf(temp3.optString("temperature")));
                                place_temp.setPlantState(plantState_temp);

                                User user_temp = new User();
                                JSONObject temp4 = (JSONObject) temp.getJSONObject("monitor_detail");
                                user_temp.setWorker_id(temp4.optString("worker_id"));
                                user_temp.setFname(temp4.optString("fname"));
                                user_temp.setMidname(temp4.optString("midname"));
                                user_temp.setLname(temp4.optString("lname"));
                                user_temp.setEmail(temp4.optString("email"));
                                user_temp.setPhone(temp4.optString("phone"));
                                user_temp.setImage(temp4.optString("image"));
                                place_temp.setMonitor(user_temp);

                                places.add(place_temp);

                            }
                            pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, places);
                        } catch (JSONException e) {
                            Log.d(test,e.getLocalizedMessage());
                        }

                    }
                }

                lv.setAdapter(pal);

            }
        });
    }

    public void changePlaceData(Place data, String action) {
        if (action.equals("Add") || action.equals("add")) {
            places.add(data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, places);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        } else if (action.equals("Update") || action.equals("update")) {

            for (int i = 0; i < places.size(); i++) {
                if (places.get(i).getPlace_id().equals(data.getPlace_id())) {
//                    Log.d(test,"masuk sni");

                    Place temp_place_update = places.get(i);
                    temp_place_update.setPlace_name(data.getPlace_name());

                    Plant temp_plant_update = temp_place_update.getPlant();
                    temp_plant_update.setId(data.getPlant().getId());
                    temp_plant_update.setName(data.getPlant().getName());
                    temp_plant_update.setDescription(data.getPlant().getDescription());
                    temp_plant_update.setScientific_name(data.getPlant().getScientific_name());
                    temp_plant_update.setImage(data.getPlant().getImage());

                    PlantState temp_ps_update = temp_place_update.getPlantState();
                    temp_ps_update.setCode(data.getPlantState().getCode());
                    temp_ps_update.setState_name(data.getPlantState().getState_name());
                    temp_ps_update.setTemperature(data.getPlantState().getTemperature());
                    temp_ps_update.setHumidity(Float.valueOf(data.getPlantState().getHumidity()));

                    User temp_guardian_update = temp_place_update.getMonitor();
                    temp_guardian_update.setWorker_id(data.getMonitor().getWorker_id());
                    temp_guardian_update.setFname(data.getMonitor().getFname());
                    temp_guardian_update.setMidname(data.getMonitor().getMidname());
                    temp_guardian_update.setLname(data.getMonitor().getLname());
                    temp_guardian_update.setEmail(data.getMonitor().getEmail());
                    temp_guardian_update.setPhone(data.getMonitor().getPhone());
                    temp_guardian_update.setImage(data.getMonitor().getImage());


//                    Log.d(test,"place id: "+places.get(i).getPlace_id()+" place name: " + places.get(i).getPlace_name()+" plant id: " +places.get(i).getPlant().getId()+ " guardian id: "+places.get(i).getMonitor().getWorker_id() );

                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, places);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        } else if (action.equals("Delete") || action.equals("delete")) {
            for (int i = 0; i < places.size(); i++) {
                if (places.get(i).getPlace_id().equals(data.getPlace_id())) {
                    places.remove(i);
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, places);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
        }

    }

    public Plant getPlantData(ArrayList<Plant> temp, String search) {
        Plant temp_data = new Plant(-1);
//        Log.d(test,String.valueOf(temp.size()));
        try {
            for (int i = 0; i < temp.size(); i++) {

                Plant temp_plant = (Plant) temp.get(i);
//                Log.d(test,temp_plant.getName());
                if (temp_plant.getName().equals(search)) {
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

    private void search_filter(final ArrayList<Place> places, String search, int cur_btn_filter) {
        final ArrayList<Place> temp = new ArrayList<Place>();
        if (search.equals("") && cur_btn_filter <= 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, places);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
            Log.d(test, "p");
        } else if (search.equals("") && !(cur_btn_filter <= 0)) {
            if (cur_btn_filter == 1) {
                //fe
                for (int i = 0; i < places.size(); i++) {
                    if (!places.get(i).getMonitor().getFname().equals("") || places.get(i).getMonitor() != null) {
                        temp.add(places.get(i));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, temp);
                        lv.setAdapter(pal);
                        lv.invalidate();
                    }
                });
                Log.d(test, "fe");
            } else if (cur_btn_filter == 2) {
                //fn
                for (int i = 0; i < places.size(); i++) {
                    if (places.get(i).getMonitor().getFname().equals("") || places.get(i).getMonitor() == null) {
                        temp.add(places.get(i));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, temp);
                        lv.setAdapter(pal);
                        lv.invalidate();
                    }
                });
                Log.d(test, "fn");
            }
        } else if (!search.equals("") && cur_btn_filter <= 0) {
            //s

            for (int i = 0; i < places.size(); i++) {
                if (places.get(i).getPlace_name().toLowerCase().contains(search.toLowerCase())) {
                    temp.add(places.get(i));
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, temp);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });
            Log.d(test, "s");

        } else if (!search.equals("") && !(cur_btn_filter <= 0)) {
            if (cur_btn_filter == 1) {
                //sfe
                //search
                ArrayList<Place> temp2 = new ArrayList<Place>();
                for (int i = 0; i < places.size(); i++) {
                    if (places.get(i).getPlace_name().toLowerCase().contains(search.toLowerCase())) {
                        temp2.add(places.get(i));
                    }
                }
                //filter
                for (int j = 0; j < temp2.size(); j++) {
                    if (temp2.get(j).getMonitor().getFname().length() > 0 || temp2.get(j).getMonitor() != null) {
                        temp.add(temp2.get(j));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, temp);
                        lv.setAdapter(pal);
                        lv.invalidate();
                    }
                });
                Log.d(test, "sfe");

            } else if (cur_btn_filter == 2) {
                //sfn
                ArrayList<Place> temp2 = new ArrayList<Place>();
                for (int i = 0; i < places.size(); i++) {
                    if (places.get(i).getPlace_name().toLowerCase().equals(search.toLowerCase())) {
                        temp2.add(places.get(i));
                    }
                }
                //filter
                for (int j = 0; j < temp2.size(); j++) {
                    if (temp2.get(j).getMonitor().getFname().length() <= 0 || temp2.get(j).getMonitor() == null) {
                        temp.add(temp2.get(j));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, temp);
                        lv.setAdapter(pal);
                        lv.invalidate();
                    }
                });
                Log.d(test, "sfn");
            }
        }
        Log.d(test, String.valueOf(temp.size()));
    }

    private void sorting(final ArrayList<Place> places, String category, int sort) {
        //ascending 1
        //descending -1
        //normal 0
//        Log.d(test,category);
        final ArrayList<Place> temp = places;
        if (sort == NORMAL) {
            Log.d(test, "p");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, places);
                    lv.setAdapter(pal);
                    lv.invalidate();
                }
            });

        } else {
            if (sort == ASCENDING) {

                if (category.equals("NAME") || category.equals("Name")) {
                    Comparator<Place> compareByPlaceNameAsc = new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return o1.getPlace_name().compareTo(o2.getPlace_name());
                        }
                    };
                    Collections.sort(temp, compareByPlaceNameAsc);
                    Log.d(test, "sa name");
                } else if (category.equals("GUARDIAN") || category.equals("Guardian")) {
                    Comparator<Place> compareByPlaceGuardianAsc = new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return o1.getMonitor().getFname().compareTo(o2.getMonitor().getFname());
                        }
                    };
                    Collections.sort(temp, compareByPlaceGuardianAsc);
                    Log.d(test, "sa guardian");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, temp);
                        lv.setAdapter(pal);
                        lv.invalidate();
                    }
                });

            } else if (sort == DESCENDING) {
                if (category.equals("NAME") || category.equals("Name")) {
                    Comparator<Place> compareByPlaceNamedesc = new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return o2.getPlace_name().compareTo(o1.getPlace_name());
                        }
                    };
                    Collections.sort(temp, compareByPlaceNamedesc);
                    Log.d(test, "sd name");
                } else if (category.equals("GUARDIAN") || category.equals("Guardian")) {
                    Comparator<Place> compareByPlaceGuardiandesc = new Comparator<Place>() {
                        @Override
                        public int compare(Place o1, Place o2) {
                            return o2.getMonitor().getFname().compareTo(o1.getMonitor().getFname());
                        }
                    };
                    Collections.sort(temp, compareByPlaceGuardiandesc);
                    Log.d(test, "sd guardian");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pal = new PlaceAdapterList(AdminPlace.this, R.layout.place_arraylist, temp);
                        lv.setAdapter(pal);
                        lv.invalidate();
                    }
                });
            }
        }
    }

    public void submitEditData(View view) {
        final LinearLayout root = (LinearLayout) view.getParent().getParent();
        final LinearLayout layout_edit = (LinearLayout) root.findViewById(R.id.layout_edit);

        final EditText edit_placeName = (EditText) root.findViewById(R.id.edit_placeName);
        final Spinner plant_spinner = (Spinner) root.findViewById(R.id.plant_spinner);
        final Spinner plant_state_spinner = (Spinner) root.findViewById(R.id.plant_state_spinner);
        final Spinner guardian_spinner = (Spinner) root.findViewById(R.id.guardian_spinner);

        final String curr_place_id = ((TextView) root.findViewById(R.id.edit_id)).getText().toString().trim();
        final String new_placeName = edit_placeName.getText().toString().trim();


        Thread thread = new Thread() {
            @Override
            public void run() {
                Plant plant_data = getPlantData(plants, plant_spinner.getSelectedItem().toString());
                PlantState plantState_data = getPlantStateData(plant_states, plant_state_spinner.getSelectedItem().toString());
                User guardian_data = getGuardianData(guardians, guardian_spinner.getSelectedItem().toString());

                try {


                    if (curr_place_id.length() > 0 && new_placeName.length() > 0) {
                        HashMap<Object, Object> params = new HashMap<Object, Object>();
                        params.put("function", "fnPlaceUpdate");
                        params.put("place_id", curr_place_id);
                        params.put("place_name", new_placeName);
                        params.put("plant_id", plant_data.getId());
                        params.put("monitor_id", guardian_data.getWorker_id());
                        params.put("plant_state", plantState_data.getCode());
                        Log.d(test, guardian_data.getWorker_id());
                        ws.post("", params, new WebService.JSON() {
                            @Override
                            public void responseData(JSONObject status, JSONObject data) {
                                Log.d(test, data.toString());
                                try {
                                    JSONObject temp_object = data.getJSONObject("data");
                                    Place temp_place_update = new Place(temp_object.optString("place_id"));
                                    temp_place_update.setPlace_name(temp_object.optString("place_name"));

                                    Plant temp_plant_update = new Plant(temp_object.optInt("plant_id"));
                                    temp_plant_update.setName(temp_object.optString("plant_name"));
                                    temp_plant_update.setDescription(temp_object.optString("plant_description"));
                                    temp_plant_update.setScientific_name(temp_object.optString("plant_sci_name"));
                                    temp_plant_update.setImage(temp_object.optString("plant_image"));

                                    PlantState temp_ps_update = new PlantState();
                                    temp_ps_update.setCode(temp_object.optString("ps_code"));
                                    temp_ps_update.setState_name(temp_object.optString("ps_name"));
                                    temp_ps_update.setTemperature(Float.valueOf(temp_object.optString("ps_temperature")));
                                    temp_ps_update.setHumidity(Float.valueOf(temp_object.optString("ps_humidity")));

                                    User temp_guardian_update = new User();
                                    temp_guardian_update.setWorker_id(temp_object.optString("u_id"));
                                    temp_guardian_update.setFname(temp_object.optString("u_fname"));
                                    temp_guardian_update.setMidname(temp_object.optString("u_midname"));
                                    temp_guardian_update.setLname(temp_object.optString("u_lname"));
                                    temp_guardian_update.setEmail(temp_object.optString("u_email"));
                                    temp_guardian_update.setPhone(temp_object.optString("u_phone"));
                                    temp_guardian_update.setImage(temp_object.optString("u_image"));

                                    temp_place_update.setPlant(temp_plant_update);
                                    temp_place_update.setPlantState(temp_ps_update);
                                    temp_place_update.setMonitor(temp_guardian_update);

                                    changePlaceData(temp_place_update, "Update");

                                } catch (JSONException e) {
                                    Log.d(test, e.getLocalizedMessage());
                                }

                            }
                        });


//
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AdminPlace.this, "Data Submitted", Toast.LENGTH_SHORT).show();
                                layout_edit.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        if (new_placeName.length() <= 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AdminPlace.this, "Place Name Empty!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.d(test, e.getLocalizedMessage());
                }
            }
        };
        thread.start();


    }

    public void addPlaceBtn(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AdminPlace.this);
        final AlertDialog alertModal = builder.create();
        LayoutInflater layoutInflater = LayoutInflater.from(AdminPlace.this);
        View rootView = layoutInflater.inflate(R.layout.place_add_layout, null);
        LinearLayout root = (LinearLayout) rootView;
        TextView close_modal = (TextView) rootView.findViewById(R.id.close_modal);
        final EditText edit_placeName = (EditText) rootView.findViewById(R.id.edit_placeName);
        Button btn_add = (Button) rootView.findViewById(R.id.btn_add);
        Button btn_cancel = (Button) rootView.findViewById(R.id.btn_cancel);

        alertModal.setView(rootView);
        alertModal.show();

        final Spinner plant_spinner = (Spinner) rootView.findViewById(R.id.plant_spinner);
        final Spinner plant_state_spinner = (Spinner) rootView.findViewById(R.id.plant_state_spinner);
        final Spinner guardian_spinner = (Spinner) rootView.findViewById(R.id.guardian_spinner);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Plant plant_data = getPlantData(plants, plant_spinner.getSelectedItem().toString());
                        PlantState plantState_data = getPlantStateData(plant_states, plant_state_spinner.getSelectedItem().toString());
                        User guardian_data = getGuardianData(guardians, guardian_spinner.getSelectedItem().toString());
                        final String new_placeName = edit_placeName.getText().toString().trim();
                        try {
                            Log.d(test, edit_placeName.getText().toString());
                            if (new_placeName.length() > 0) {
                                HashMap<Object, Object> params = new HashMap<Object, Object>();
                                params.put("function", "fnPlaceAdd");
                                params.put("place_name", new_placeName);
                                params.put("plant_id", plant_data.getId());
                                params.put("monitor_id", guardian_data.getWorker_id());
                                params.put("plant_state", plantState_data.getCode());
                                ws.post("", params, new WebService.JSON() {
                                    @Override
                                    public void responseData(JSONObject status, JSONObject data) {
                                        try {
                                            JSONObject temp_object = data.getJSONObject("data");
                                            Place temp_place_update = new Place(temp_object.optString("place_id"));
                                            temp_place_update.setPlace_name(temp_object.optString("place_name"));

                                            Plant temp_plant_update = new Plant(temp_object.optInt("plant_id"));
                                            temp_plant_update.setName(temp_object.optString("plant_name"));
                                            temp_plant_update.setDescription(temp_object.optString("plant_description"));
                                            temp_plant_update.setScientific_name(temp_object.optString("plant_sci_name"));
                                            temp_plant_update.setImage(temp_object.optString("plant_image"));

                                            PlantState temp_ps_update = new PlantState();
                                            temp_ps_update.setCode(temp_object.optString("ps_code"));
                                            temp_ps_update.setState_name(temp_object.optString("ps_name"));
                                            temp_ps_update.setTemperature(Float.valueOf(temp_object.optString("ps_temperature")));
                                            temp_ps_update.setHumidity(Float.valueOf(temp_object.optString("ps_humidity")));

                                            User temp_guardian_update = new User();
                                            temp_guardian_update.setWorker_id(temp_object.optString("u_id"));
                                            temp_guardian_update.setFname(temp_object.optString("u_fname"));
                                            temp_guardian_update.setMidname(temp_object.optString("u_midname"));
                                            temp_guardian_update.setLname(temp_object.optString("u_lname"));
                                            temp_guardian_update.setEmail(temp_object.optString("u_email"));
                                            temp_guardian_update.setPhone(temp_object.optString("u_phone"));
                                            temp_guardian_update.setImage(temp_object.optString("u_image"));

                                            temp_place_update.setPlant(temp_plant_update);
                                            temp_place_update.setPlantState(temp_ps_update);
                                            temp_place_update.setMonitor(temp_guardian_update);

                                            changePlaceData(temp_place_update, "add");
                                        } catch (JSONException e) {
                                            Log.d(test, e.getLocalizedMessage());
                                        }

                                    }
                                });
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AdminPlace.this, "Data Add Submitted", Toast.LENGTH_SHORT).show();
                                        alertModal.dismiss();
                                    }
                                });
                            } else {
                                if (new_placeName.length() <= 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AdminPlace.this, "Place Name Empty!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            Log.d(test, e.getLocalizedMessage());
                        }
                    }
                };
                thread.start();
            }
        });

        close_modal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertModal.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertModal.dismiss();
            }
        });

        ArrayAdapter<String> adapter_plant = new ArrayAdapter<String>(AdminPlace.this, android.R.layout.simple_spinner_dropdown_item, plant_list_name);
        plant_spinner.setAdapter(adapter_plant);

        ArrayAdapter<String> adapter_plant_state = new ArrayAdapter<String>(AdminPlace.this, android.R.layout.simple_spinner_dropdown_item, plantState_list_name);
        plant_state_spinner.setAdapter(adapter_plant_state);

        ArrayAdapter<String> adapter_guardian = new ArrayAdapter<String>(AdminPlace.this, android.R.layout.simple_spinner_dropdown_item, guardian_list_name);
        guardian_spinner.setAdapter(adapter_guardian);
    }

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

    public void filterPlaceSearch(View view) {
        final View v = view;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    EditText temp_search = ((View) v.getParent()).findViewById(R.id.input_place_name);
                    final String temp = temp_search.getText().toString().trim();

                    if (temp.length() > 0) {

                        search_filter(places, temp, filter_curr_btn);
                        cur_search_string = temp;
                    }

                } catch (Exception e) {
                    Log.d(test, e.getLocalizedMessage());
                }
            }
        };
        thread.start();

    }

    public void filterPlacesBtn(View view) {
        try {
            TextView cur_btn = (TextView) view;
            LinearLayout root = (LinearLayout) view.getParent();
            for (int i = 1; i < root.getChildCount(); i++) {
                TextView temp = (TextView) root.getChildAt(i);
                temp.setEnabled(true);
                temp.setBackgroundResource(R.drawable.simple_bubble_btn);
            }
            cur_btn.setEnabled(false);
            cur_btn.setBackgroundResource(R.drawable.custom_btn_success);

            filter_curr_btn = (cur_btn.getText().toString().equals("ALL")) ? 0 : (cur_btn.getText().toString().equals("EXIST")) ? 1 : (cur_btn.getText().toString().equals("NOT EXIST")) ? 2 : -1;
            search_filter(places, cur_search_string, filter_curr_btn);

        } catch (Exception e) {
            Log.d(test, e.getLocalizedMessage());
        }
    }

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
            Intent intent = new Intent(AdminPlace.this, AdminDevice.class);
            startActivity(intent);
        } else if (id == R.id.drawer_staff_menu) {
            Intent intent = new Intent(AdminPlace.this, AdminWorker.class);
            startActivity(intent);

        }
        else if (id == R.id.drawer_place_menu) {
            Intent intent = new Intent(AdminPlace.this,AdminPlace.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_menu) {
            Intent intent = new Intent(AdminPlace.this,AdminPlant.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_state_menu) {
            Intent intent = new Intent(AdminPlace.this,AdminPlantState.class);
            startActivity(intent);
        }else if (id == R.id.drawer_logout_menu) {
            Session.setCurrentContext(Login.class);
            Session.setUser(null);
            Intent intent = new Intent(AdminPlace.this, Login.class);
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
