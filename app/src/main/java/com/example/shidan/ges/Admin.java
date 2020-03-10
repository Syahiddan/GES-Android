package com.example.shidan.ges;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shidan.ges.shidan.LoadingDialog;
import com.example.shidan.ges.shidan.Session;
import com.squareup.picasso.Picasso;

public class Admin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Toolbar toolbar;
    LoadingDialog ld;

    RelativeLayout mainLayout;
    LayoutInflater li;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Window w = getWindow();
//        toolbar.setTitleTextColor(Color.BLACK);

        toolbar.setBackgroundColor(Color.GREEN);
        Thread thr1 = new Thread(){
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

        Button leaf_btn_device = (Button) findViewById(R.id.leaf_btn_device);
        Button leaf_btn_place = (Button) findViewById(R.id.leaf_btn_place);
        Button leaf_btn_plant = (Button) findViewById(R.id.leaf_btn_plant);
        Button leaf_btn_plant_state = (Button) findViewById(R.id.leaf_btn_plant_state);

        leaf_btn_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(Admin.this,AdminDevice.class);
                startActivity(intent);
            }
        });
        leaf_btn_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(Admin.this,AdminPlace.class);
                startActivity(intent);
            }
        });
        leaf_btn_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(Admin.this,AdminPlant.class);
                startActivity(intent);
            }
        });
        leaf_btn_plant_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(Admin.this,AdminPlantState.class);
                startActivity(intent);
            }
        });
//        Picasso.get().load("http://i.imgur.com/DvpvklR.png").resize(curUserImage.getDrawable().getIntrinsicWidth(),curUserImage.getDrawable().getIntrinsicHeight()).centerCrop().into(curUserImage);
//        curUserName.setText(Session.getUser().getFname()+" "+ Session.getUser().getMidname()+" "+Session.getUser().getLname());
//        curUSerEmail.setText(Session.getUser().getEmail());

        mainLayout = (RelativeLayout) findViewById(R.id.admin_content_space);
        li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = li.inflate(R.layout.loading,null);
//        mainLayout.removeAllViews();
        mainLayout.addView(layout);

    }

    public void setup_drawer()
    {
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
            Intent intent = new Intent(Admin.this,AdminDevice.class);
            startActivity(intent);
        } else if (id == R.id.drawer_staff_menu) {
            Intent intent = new Intent(Admin.this,AdminWorker.class);
            startActivity(intent);

        }
        else if (id == R.id.drawer_place_menu) {
            Intent intent = new Intent(Admin.this,AdminPlace.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_menu) {
            Intent intent = new Intent(Admin.this,AdminPlant.class);
            startActivity(intent);
        }
        else if (id == R.id.drawer_plant_state_menu) {
            Intent intent = new Intent(Admin.this,AdminPlantState.class);
            startActivity(intent);
        }
        else if(id == R.id.drawer_logout_menu)
        {
            Session.setCurrentContext(Login.class);
            Session.setUser(null);
            Intent intent = new Intent(Admin.this,Login.class);
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
