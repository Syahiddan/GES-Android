package com.example.shidan.ges;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shidan.ges.model.User;
import com.example.shidan.ges.shidan.CustomProgressBarShidan;
import com.example.shidan.ges.shidan.Session;
import com.example.shidan.ges.shidan.WebService;
import com.example.shidan.ges.shidan.WorkerAdapterList;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AdminWorker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Toolbar toolbar;

    RelativeLayout mainLayout;
    LayoutInflater li;
    View layout;
    AlertDialog ad;
    AlertDialog.Builder builder;
    ListView lv_worker;
    ArrayList<User> workers;
    WorkerAdapterList wal;

    View child;
    ExpandableLinearLayout lv_add_worker;
    ScrollView sv;
    WebService ws;

    //counter...
    ImageView temp_replace_img; // for replace upload image temp
    int view_edit_count = 0;
    private static int edit_place = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle("Admin");
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
        layout = li.inflate(R.layout.activity_admin_worker, null);
        mainLayout.removeAllViews();
        mainLayout.addView(layout);

        lv_worker = (ListView) findViewById(R.id.worker_table_list);
        lv_add_worker = (ExpandableLinearLayout) findViewById(R.id.worker_table_list_add_container);
        sv = (ScrollView) findViewById(R.id.big_container);

        builder = new AlertDialog.Builder(AdminWorker.this);
        ws = new WebService(getApplicationContext());
        ad = new AlertDialog.Builder(this).setView(R.layout.loading).show();
        Thread th1 = new Thread() {
            @Override
            public void run() {
                setup_worker_list(null);
                ad.dismiss();
            }
        };
        th1.start();
    }

    public void setup_worker_list(@Nullable String filter) {
        final WebService ws1 = new WebService(getApplicationContext());
        HashMap<Object, Object> dataSend = new HashMap<Object, Object>();
        dataSend.put("function", "fnWorkerList");
        dataSend.put("filter", "w");

        workers = new ArrayList<User>();

        ws1.post("", dataSend, new WebService.JSON() {
            @Override
            public void responseData(JSONObject Status, JSONObject data) {
                try {

                    int countWorker = data.getJSONArray("data").length();
                    Log.d("countarray", String.valueOf(countWorker));

                    JSONArray temp_data = data.getJSONArray("data");
                    for (int i = 0; i < countWorker; i++) {
                        JSONObject temp_worker = (JSONObject) temp_data.get(i);
//                        Log.d("dataOI",temp_worker.optString("worker_id"));
                        User temp = new User();
                        temp.setWorker_id(temp_worker.optString("worker_id"));
                        temp.setWorker_type(temp_worker.optString("worker_type"));
                        temp.setFname(temp_worker.optString("fname"));
                        temp.setMidname(temp_worker.optString("midname"));
                        temp.setLname(temp_worker.optString("lname"));
                        temp.setEmail(temp_worker.optString("email"));
                        temp.setPhone(temp_worker.optString("phone"));
                        temp.setAddress(temp_worker.optString("address"));
                        temp.setPostcode(temp_worker.optInt("postcode"));
                        temp.setState(temp_worker.optString("state"));
                        temp.setCountry(temp_worker.optString("country"));
                        temp.setImage(temp_worker.optString("image"));
                        workers.add(temp);


                    }

                    wal = new WorkerAdapterList(getApplicationContext(), R.layout.worker_arraylist, workers);
                    lv_worker.setAdapter(wal);


                    lv_worker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            final View temp = wal.getView(position, view, parent);

                            LinearLayout ll_1 = (LinearLayout) temp.findViewById(R.id.ll_address);
                            LinearLayout ll_2 = (LinearLayout) temp.findViewById(R.id.ll_postcode_state);
                            LinearLayout ll_3 = (LinearLayout) temp.findViewById(R.id.ll_country);

                            TextView edit_btn = (TextView) temp.findViewById(R.id.edit_btn);
                            TextView delete_btn = (TextView) temp.findViewById(R.id.delete_btn);
                            edit_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    open_update_fragment(v);
                                }
                            });
                            delete_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    delete_data_worker(v);
                                }
                            });
                            if (ll_1.getVisibility() == View.GONE) {
                                wal.setReset();
                                ll_1.setVisibility(View.VISIBLE);
                                ll_2.setVisibility(View.VISIBLE);
                                ll_3.setVisibility(View.VISIBLE);
                                edit_btn.setVisibility(View.VISIBLE);
                                delete_btn.setVisibility(View.VISIBLE);
                            } else {
                                wal.setReset();
                            }
                        }

                    });

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void delete_data_worker(final View v) {
        builder.setTitle("Deleting Worker Information");
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        LinearLayout root = (LinearLayout) v.getParent().getParent();
                        final TextView id = root.findViewById(R.id.worker_id_space);
                        Log.d("test", id.getText().toString());
                        HashMap<Object, Object> params = new HashMap<Object, Object>();
                        params.put("function", "fnWorkerRemove");
                        params.put("worker_id", id.getText().toString());

                        ws.post("", params, new WebService.JSON() {
                            @Override
                            public void responseData(JSONObject status, JSONObject data) {
                                try {
                                    if (status.getString("statusRequest").equals("success")) {

//                                        Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
                                        if (data.getString("status").equals("success")) {

                                            int index = findUserPointer(id.getText().toString());
                                            wal.remove(wal.getItem(index));
                                            wal.notifyDataSetChanged();
                                            wal.setReset();

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("test", "no click");
                    }
                }).show();
    }

    public void open_update_fragment(View v) {
        final View child = getLayoutInflater().inflate(R.layout.worker_arraylist_input_text, null);
        child.findViewById(R.id.worker_id_edit).setEnabled(false);
        Button upload_img_btn = (Button) child.findViewById(R.id.upload_img_btn);
        Button btn_update = (Button) child.findViewById(R.id.add_btn);
        Button cancel_btn = (Button) child.findViewById(R.id.cancel_btn);

        btn_update.setText("Update");

        lv_add_worker.addView(child);

        LinearLayout root = (LinearLayout) v.getParent().getParent();

        ImageView worker_img = (ImageView) root.findViewById(R.id.worker_img);
        TextView worker_id_space = (TextView) root.findViewById(R.id.worker_id_space);
        TextView worker_phone_space = (TextView) root.findViewById(R.id.worker_phone_space);
        TextView worker_name_space = (TextView) root.findViewById(R.id.worker_name_space);

        String[] name_splitter = worker_name_space.getText().toString().split(" ");

        TextView worker_email_space = (TextView) root.findViewById(R.id.worker_email_space);
        TextView worker_address_space = (TextView) root.findViewById(R.id.worker_address_space);
        TextView worker_postcode_space = (TextView) root.findViewById(R.id.worker_postcode_space);
        TextView worker_state_space = (TextView) root.findViewById(R.id.worker_state_space);
        TextView worker_country_space = (TextView) root.findViewById(R.id.worker_country_space);

        final ImageView worker_img_temp = (ImageView) child.findViewById(R.id.worker_img);
        TextView worker_id_edit = (TextView) child.findViewById(R.id.worker_id_edit);
        TextView worker_phone_edit = (TextView) child.findViewById(R.id.worker_phone_edit);
        TextView worker_fname_edit = (TextView) child.findViewById(R.id.worker_fname_edit);
        TextView worker_mname_edit = (TextView) child.findViewById(R.id.worker_mname_edit);
        TextView worker_lname_edit = (TextView) child.findViewById(R.id.worker_lname_edit);
        TextView worker_email_edit = (TextView) child.findViewById(R.id.worker_email_edit);
        TextView worker_address_edit = (TextView) child.findViewById(R.id.worker_address_edit);
        TextView worker_postcode_edit = (TextView) child.findViewById(R.id.worker_postcode_edit);
        TextView worker_state_edit = (TextView) child.findViewById(R.id.worker_state_edit);
        TextView worker_country_edit = (TextView) child.findViewById(R.id.worker_country_edit);

        worker_img_temp.setImageDrawable(worker_img.getDrawable());
        worker_id_edit.setText(worker_id_space.getText().toString());
        worker_phone_edit.setText(worker_phone_space.getText().toString());
        worker_fname_edit.setText(name_splitter[0]);
        worker_mname_edit.setText(name_splitter[1]);
        worker_lname_edit.setText(name_splitter[2]);
        worker_email_edit.setText(worker_email_space.getText().toString());
        worker_address_edit.setText(worker_address_space.getText().toString());
        worker_postcode_edit.setText(worker_postcode_space.getText().toString());
        worker_state_edit.setText(worker_state_space.getText().toString());
        worker_country_edit.setText(worker_country_space.getText().toString());

//        Log.d("test", worker_id_space.getText().toString());

        upload_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_replace_img = (ImageView) child.findViewById(R.id.worker_img);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                RelativeLayout root = (RelativeLayout) v.getParent().getParent().getParent();
                final LinearLayout loading_space = (LinearLayout) root.findViewById(R.id.loading_space);
                loading_space.setVisibility(View.VISIBLE);

                final CustomProgressBarShidan customProgressBarShidan = new CustomProgressBarShidan(getApplicationContext(), AdminWorker.this);
                loading_space.addView(customProgressBarShidan.getProgressBarLayout());

                customProgressBarShidan.setProgressBarText("Updating... Please Wait");
                customProgressBarShidan.animateTo(10);
                Thread thread3 = new Thread() {
                    @Override
                    public void run() {
                        update_data_submit(v, child, worker_img_temp, customProgressBarShidan);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loading_space.setVisibility(View.GONE);
                            }
                        });

                    }
                };
                thread3.start();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandableLinearLayout lv_main = (ExpandableLinearLayout) v.getParent().getParent().getParent().getParent();
                lv_main.removeView(child);
            }
        });
    }

    public void update_data_submit(final View v, final View child, final ImageView curr_img, final CustomProgressBarShidan customProgressBarShidan) {
        View parent = (View) v.getParent().getParent();

        final EditText worker_id = (EditText) parent.findViewById(R.id.worker_id_edit);
        final EditText worker_phone = (EditText) parent.findViewById(R.id.worker_phone_edit);
        final EditText worker_fname = (EditText) parent.findViewById(R.id.worker_fname_edit);
        final EditText worker_mname = (EditText) parent.findViewById(R.id.worker_mname_edit);
        final EditText worker_lname = (EditText) parent.findViewById(R.id.worker_lname_edit);
        final EditText worker_email = (EditText) parent.findViewById(R.id.worker_email_edit);
        final EditText worker_address = (EditText) parent.findViewById(R.id.worker_address_edit);
        final EditText worker_postcode = (EditText) parent.findViewById(R.id.worker_postcode_edit);
        final EditText worker_state = (EditText) parent.findViewById(R.id.worker_state_edit);
        final EditText worker_country = (EditText) parent.findViewById(R.id.worker_country_edit);

        customProgressBarShidan.animateTo(100);


        View parent_2 = (View) v.getParent();
        ImageView imageView = (ImageView) parent_2.findViewById(R.id.worker_img);

        String imageRes = convertImageViewToString(imageView);
        Log.d("click", "updating");
        Log.d("click", imageRes);
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("function", "fnWorkerUpdate");
        params.put("worker_id", worker_id.getText().toString());
        params.put("phone", worker_phone.getText().toString());
        params.put("fname", worker_fname.getText().toString());
        params.put("midname", worker_mname.getText().toString());
        params.put("lname", worker_lname.getText().toString());
        params.put("email", worker_email.getText().toString());
        params.put("address", worker_address.getText().toString());
        params.put("postcode", worker_postcode.getText().toString());
        params.put("state", worker_state.getText().toString());
        params.put("country", worker_country.getText().toString());
        params.put("image", worker_id.getText().toString());
        params.put("imageRes", imageRes);
        Log.d("test",imageRes);

        ws.post("", params, new WebService.JSON() {
            @Override
            public void responseData(JSONObject Status, JSONObject data) {
                Log.d("test",Status.toString());
                boolean is_updated = false;
                try {
                    Log.d("test", data.getString("status"));
                    String status_update = data.getString("status");
                    String status_image = data.getString("status_image");
                    customProgressBarShidan.setProgressLoading(100);

                    if (status_update.equals("success") && status_image.equals("success")) {
                        ExpandableLinearLayout lv_main = (ExpandableLinearLayout) v.getParent().getParent().getParent().getParent();
                        lv_main.removeView(child);
                        Toast.makeText(getApplicationContext(), "Update Completed", Toast.LENGTH_SHORT).show();
                        is_updated = true;

                    } else if (status_update.equals("success") && !status_image.equals("success")) {
                        ExpandableLinearLayout lv_main = (ExpandableLinearLayout) v.getParent().getParent().getParent().getParent();
                        lv_main.removeView(child);
                        Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        is_updated = true;
                    } else if (status_image.equals("success") && !status_update.equals("success")) {
                        ExpandableLinearLayout lv_main = (ExpandableLinearLayout) v.getParent().getParent().getParent().getParent();
                        lv_main.removeView(child);
                        Toast.makeText(getApplicationContext(), data.getString("status_image"), Toast.LENGTH_SHORT).show();
                        is_updated = true;
                    } else if (!status_update.equals("success") && !status_image.equals("success")) {
                        ExpandableLinearLayout lv_main = (ExpandableLinearLayout) v.getParent().getParent().getParent().getParent();
                        lv_main.removeView(child);


                        Toast.makeText(getApplicationContext(), data.getString("status"), Toast.LENGTH_SHORT).show();
                    }

                    if (is_updated) {

                        int index = findUserPointer(worker_id.getText().toString());
                        User currWorker = workers.get(index);
                        if (!worker_phone.getText().toString().trim().isEmpty() || !worker_phone.getText().toString().matches("")) {
                            currWorker.setPhone(worker_phone.getText().toString());
                        }
                        if (!worker_fname.getText().toString().trim().isEmpty() || !worker_fname.getText().toString().matches("")) {
                            currWorker.setFname(worker_fname.getText().toString());
                        }
                        if (!worker_mname.getText().toString().trim().isEmpty() || !worker_mname.getText().toString().matches("")) {
                            currWorker.setMidname(worker_mname.getText().toString());
                        }
                        if (!worker_lname.getText().toString().trim().isEmpty() || !worker_lname.getText().toString().matches("")) {
                            currWorker.setLname(worker_lname.getText().toString());
                        }
                        if (!worker_email.getText().toString().trim().isEmpty() || !worker_email.getText().toString().matches("")) {
                            currWorker.setEmail(worker_email.getText().toString());
                        }
                        if (!worker_address.getText().toString().trim().isEmpty() || !worker_address.getText().toString().matches("")) {
                            currWorker.setAddress(worker_address.getText().toString());
                        }
                        if (!worker_postcode.getText().toString().trim().isEmpty() || !worker_postcode.getText().toString().matches("")) {
                            currWorker.setPostcode(Integer.valueOf(worker_postcode.getText().toString()));
                        }
                        if (!worker_state.getText().toString().trim().isEmpty() || !worker_state.getText().toString().matches("")) {
                            currWorker.setState(worker_state.getText().toString());
                        }
                        if (!worker_country.getText().toString().trim().isEmpty() || !worker_country.getText().toString().matches("")) {
                            currWorker.setCountry(worker_country.getText().toString());
                        }
                        currWorker.setImage(worker_id.getText().toString());

                        Picasso.get().invalidate("http://192.168.137.1/ges/UserImage/" + worker_id.getText().toString() + ".png");
                        Picasso.get().load("http://192.168.137.1/ges/UserImage/" + worker_id.getText().toString() + ".png").memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).resize(100, 100).centerCrop().into(curr_img);
                        wal.notifyDataSetChanged();
                        wal.setReset();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String convertImageViewToString(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] img_data = stream.toByteArray();

        String imageRes = Base64.encodeToString(img_data, Base64.DEFAULT);
        return imageRes;
    }

    private int findUserPointer(String id) {

        Comparator<User> c = new Comparator<User>() {
            public int compare(User u1, User u2) {
                return u1.getWorker_id().compareToIgnoreCase(u2.getWorker_id());
            }
        };
        User temp = new User();
        temp.setWorker_id(id);
        int currSearchIndex = Collections.binarySearch(workers, temp, c);
        return currSearchIndex;

    }

    public void add_new_worker(View view) {
        if (view_edit_count < 1) {

            HashMap<Object, Object> params = new HashMap<Object, Object>();
            params.put("function", "fnGetNewID");
            params.put("worker_type", "w");

            final View child = getLayoutInflater().inflate(R.layout.worker_arraylist_input_text, null);
            child.findViewById(R.id.worker_id_edit).setEnabled(false);
            ws.post("", params, new WebService.JSON() {
                @Override
                public void responseData(JSONObject status, JSONObject data) {

                    try {
                        if (status.getString("statusRequest").equals("success")) {
                            if (data.getString("status").equals("success")) {
                                String new_id = data.getJSONObject("data").getString("new_id");
                                Log.d("test", new_id);
                                EditText id = (EditText) child.findViewById(R.id.worker_id_edit);
                                id.setText(new_id);
                            }
                        } else {
                            Log.d("test", status.getString("statusRequest"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            });


            lv_add_worker.addView(child, view_edit_count + 1);
            lv_add_worker.setPadding(5, 5, 5, 5);
            lv_add_worker.initLayout();
            lv_add_worker.expand();
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scroll_down_anim);
            child.startAnimation(animation);
            sv.requestChildFocus(child, child);
            add_new_worker_listener(child, view_edit_count);
            view_edit_count++;


        } else if (view_edit_count == 1) {
            Toast.makeText(getApplicationContext(), "Registration Limit to 1 people at same time!!!", Toast.LENGTH_SHORT).show();
        }

    }


    public void add_new_worker_listener(final View child, final int count) {
        Button upload_img_btn = (Button) child.findViewById(R.id.upload_img_btn);
        Button add_btn = (Button) child.findViewById(R.id.add_btn);
        Button cancel_btn = (Button) child.findViewById(R.id.cancel_btn);

        final ImageView img_for_upload = (ImageView) child.findViewById(R.id.worker_img);
        final TextView error_message = (TextView) child.findViewById(R.id.error_message);
        final TextView worker_id_edit = (TextView) child.findViewById(R.id.worker_id_edit);
        final EditText worker_phone_edit = (EditText) child.findViewById(R.id.worker_phone_edit);
        final EditText worker_fname_edit = (EditText) child.findViewById(R.id.worker_fname_edit);
        final EditText worker_mname_edit = (EditText) child.findViewById(R.id.worker_mname_edit);
        final EditText worker_lname_edit = (EditText) child.findViewById(R.id.worker_lname_edit);
        final EditText worker_email_edit = (EditText) child.findViewById(R.id.worker_email_edit);
        final EditText worker_address_edit = (EditText) child.findViewById(R.id.worker_address_edit);
        final EditText worker_postcode_edit = (EditText) child.findViewById(R.id.worker_postcode_edit);
        final EditText worker_state_edit = (EditText) child.findViewById(R.id.worker_state_edit);
        final EditText worker_country_edit = (EditText) child.findViewById(R.id.worker_country_edit);

        upload_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 2);
            }
        });
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                error_message.setText("");
                            }
                        });


                        final String id = worker_id_edit.getText().toString();
                        String phone = worker_phone_edit.getText().toString().trim();
                        String fname = worker_fname_edit.getText().toString().trim();
                        String midname = worker_mname_edit.getText().toString().trim();
                        String lname = worker_lname_edit.getText().toString().trim();
                        String email = worker_email_edit.getText().toString().trim();
                        String address = worker_address_edit.getText().toString().trim();
                        String postcode = worker_postcode_edit.getText().toString().trim();
                        String state = worker_state_edit.getText().toString().trim();
                        String country = worker_country_edit.getText().toString().trim();

                        HashMap<Object, Object> params = new HashMap<Object, Object>();
                        params.put("function", "fnWorkerAdd");
                        params.put("worker_id", id);
                        params.put("fname", fname);
                        params.put("midname", midname);
                        params.put("lname", lname);
                        params.put("email", email);
                        params.put("phone", phone);
                        params.put("address", address);
                        params.put("postcode", postcode);
                        params.put("state", state);
                        params.put("country", country);
                        params.put("image", id);
                        params.put("imageRes", convertImageViewToString(img_for_upload));

                        if (!fname.isEmpty() && !midname.isEmpty() && !lname.isEmpty() && !email.isEmpty() && !address.isEmpty() && !postcode.isEmpty() && !state.isEmpty() && !country.isEmpty()) {

                            ws.post("", params, new WebService.JSON() {
                                @Override
                                public void responseData(JSONObject status, JSONObject data) {
                                    boolean is_updated = false;
                                    try {
                                        if (status.getString("statusRequest").equals("success") && (data.getString("status_image").equals("success") || data.getString("status").equals("success"))) {
                                            Log.d("test", "here");
                                            Log.d("test", data.toString());
                                            view_edit_count--;
                                            ExpandableLinearLayout lv_main = (ExpandableLinearLayout) v.getParent().getParent().getParent().getParent();
                                            lv_main.removeView(child);
                                            if (lv_add_worker.getChildCount() <= 1) {
                                                lv_add_worker.setPadding(0, 0, 0, 0);
                                            }
                                            is_updated = true;

                                        } else if (!data.getString("status_submit").equals("success")) {
                                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d("test", "failed");
                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                            ExpandableLinearLayout lv_main = (ExpandableLinearLayout) v.getParent().getParent().getParent().getParent();
                                            lv_main.removeView(child);
                                            view_edit_count--;
                                            if (lv_add_worker.getChildCount() <= 1) {
                                                lv_add_worker.setPadding(0, 0, 0, 0);
                                            }
                                        }

                                        if (is_updated) {
                                            if (is_updated) {

                                                User currWorker = new User();

                                                if (!worker_id_edit.getText().toString().trim().isEmpty() || !worker_id_edit.getText().toString().matches("")) {
                                                    currWorker.setWorker_id(worker_id_edit.getText().toString());
                                                }
                                                if (!worker_phone_edit.getText().toString().trim().isEmpty() || !worker_phone_edit.getText().toString().matches("")) {
                                                    currWorker.setWorker_id(worker_phone_edit.getText().toString());
                                                }
                                                if (!worker_fname_edit.getText().toString().trim().isEmpty() || !worker_fname_edit.getText().toString().matches("")) {
                                                    currWorker.setFname(worker_fname_edit.getText().toString());
                                                }
                                                if (!worker_mname_edit.getText().toString().trim().isEmpty() || !worker_mname_edit.getText().toString().matches("")) {
                                                    currWorker.setMidname(worker_mname_edit.getText().toString());
                                                }
                                                if (!worker_lname_edit.getText().toString().trim().isEmpty() || !worker_lname_edit.getText().toString().matches("")) {
                                                    currWorker.setLname(worker_lname_edit.getText().toString());
                                                }
                                                if (!worker_email_edit.getText().toString().trim().isEmpty() || !worker_email_edit.getText().toString().matches("")) {
                                                    currWorker.setEmail(worker_email_edit.getText().toString());
                                                }
                                                if (!worker_address_edit.getText().toString().trim().isEmpty() || !worker_address_edit.getText().toString().matches("")) {
                                                    currWorker.setAddress(worker_address_edit.getText().toString());
                                                }
                                                if (!worker_postcode_edit.getText().toString().trim().isEmpty() || !worker_postcode_edit.getText().toString().matches("")) {
                                                    currWorker.setPostcode(Integer.valueOf(worker_postcode_edit.getText().toString()));
                                                }
                                                if (!worker_state_edit.getText().toString().trim().isEmpty() || !worker_state_edit.getText().toString().matches("")) {
                                                    currWorker.setState(worker_state_edit.getText().toString());
                                                }
                                                if (!worker_country_edit.getText().toString().trim().isEmpty() || !worker_country_edit.getText().toString().matches("")) {
                                                    currWorker.setCountry(worker_country_edit.getText().toString());
                                                }
                                                currWorker.setImage(id);
                                                wal.add(currWorker);
                                                Picasso.get().invalidate("http://192.168.137.1/ges/UserImage/" + id + ".png");
                                                wal.notifyDataSetChanged();
                                                wal.setReset();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            String temp = "ALERT\nEmpty Input\n\n";
                            if (fname.isEmpty()) {
                                temp += "First Name\n";
                            }
                            if (midname.isEmpty()) {
                                temp += "Middle Name\n";
                            }
                            if (lname.isEmpty()) {
                                temp += "Last Name\n";
                            }
                            if (email.isEmpty()) {
                                temp += "Email\n";
                            }
                            if (address.isEmpty()) {
                                temp += "Address\n";
                            }
                            if (postcode.isEmpty()) {
                                temp += "Postcode\n";
                            }
                            if (state.isEmpty()) {
                                temp += "State\n";
                            }
                            if (country.isEmpty()) {
                                temp += "Country\n";
                            }
                            final String uina = temp;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    error_message.setText(uina);
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
//                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scroll_down_anim);
//                    temp.startAnimation(animation);
                RelativeLayout tempRL = (RelativeLayout) v.getParent().getParent().getParent();
                final Button add_btn = (Button) tempRL.findViewById(R.id.add_btn);

                lv_add_worker.removeView(tempRL);

                view_edit_count--;
                if (lv_add_worker.getChildCount() <= 1) {
                    lv_add_worker.setPadding(0, 0, 0, 0);
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        View view = lv_add_worker.getChildAt(1);
                        ImageView imageView = (ImageView) view.findViewById(R.id.worker_img);
                        imageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));

//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        byte[] img_data = stream.toByteArray();
//
//                        String encodedImage = Base64.encodeToString(img_data, Base64.DEFAULT);
//
//                        WebService ws = new WebService(getApplicationContext());
//                        HashMap<Object, Object> param = new HashMap<Object, Object>();
//                        param.put("function", "image");
//                        param.put("image", encodedImage);
//                        ws.post("", param, new WebService.JSON() {
//                            @Override
//                            public void responseData(JSONObject data) {
//                                Toast.makeText(getApplicationContext(), "berjaya", Toast.LENGTH_SHORT).show();
//                            }
//                        });

                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
                case 2: {
                    Uri selectedImage2 = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage2);
                        View view = lv_add_worker.getChildAt(1);
                        ImageView imageView = (ImageView) view.findViewById(R.id.worker_img);
                        imageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

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
            Intent intent = new Intent(AdminWorker.this,AdminDevice.class);
            startActivity(intent);
        } else if (id == R.id.drawer_staff_menu) {
            Intent intent = new Intent(AdminWorker.this,AdminWorker.class);
            startActivity(intent);

        }
        else if (id == R.id.drawer_place_menu) {
            Intent intent = new Intent(AdminWorker.this,AdminPlace.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_menu) {
            Intent intent = new Intent(AdminWorker.this,AdminPlant.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_state_menu) {
            Intent intent = new Intent(AdminWorker.this,AdminPlantState.class);
            startActivity(intent);
        }
        else if(id == R.id.drawer_logout_menu)
        {
            Session.setCurrentContext(Login.class);
            Session.setUser(null);
            Intent intent = new Intent(AdminWorker.this,Login.class);
            startActivity(intent);
            finish();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
