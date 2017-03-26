package com.pml.pixfly;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.pml.pixfly.R;

public class ViewMissionsActivity extends AppCompatActivity {

    private ListView missionsListView;
    private ListAdapter missionsListAdapter;
    private ArrayList<String> missionsArrayList;
    private Map<String, ArrayList<Mission>> missionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_missions);
        displayNav();
        missionsListView = (ListView) findViewById(R.id.missionsListView);
        missionsArrayList = new ArrayList<String>();

        missionsListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, missionsArrayList);
        missionsListView.setAdapter(missionsListAdapter);

        //Read missions from file
        FileOperationsUtil util = new FileOperationsUtil();
        ArrayList<String> missionList = util.readFromFile(getApplicationContext(), Constants.MISSIONS);

        if(missionList!=null) {
            missionMap = new ConcurrentHashMap<String, ArrayList<Mission>>();

            for(String missionRow : missionList) {
                String missionName = getMissionName(missionRow);
                if(missionMap.containsKey(missionName)) {
                    missionMap.get(missionName).add(getMission(missionRow));
                } else {
                    ArrayList<Mission> newMissionList = new ArrayList<Mission>();
                    missionMap.put(missionName, newMissionList);
                }
            }

            for(Map.Entry<String, ArrayList<Mission>> entry : missionMap.entrySet()) {
                missionsArrayList.add(entry.getKey());
            }

            missionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    String mission = ((TextView) view).getText().toString();
                    Toast.makeText(getBaseContext(), mission, Toast.LENGTH_LONG).show();

                    Intent plotIntent = new Intent(ViewMissionsActivity.this, PlotMissionActivity.class);

                    //plotIntent.putExtra("Key", missionMap.get(mission));
                    plotIntent.putExtra("mission", (ArrayList<Mission>) missionMap.get(mission));
                    startActivity(plotIntent);
                }
            });
        }
    }

    private String getMissionName(String missionRow) {
        //FOLLOW_ME;25/03/2017;12.93532884;77.69618476
        String[] missionData = missionRow.split(";");
        return missionData[0]+":"+missionData[1];
    }

    private Mission getMission(String missionRow) {
        String[] missionData = missionRow.split(";");
        Mission mission = new Mission();
        mission.setMission_name(missionData[0]);
        mission.setLaunch_date(missionData[1]);
        mission.setLatitude(Double.parseDouble(missionData[2]));
        mission.setLongitude(Double.parseDouble(missionData[3]));
        return mission;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_missions, menu);
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

    private void displayNav(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.nav_location) {
                    Intent missionsIntent = new Intent(ViewMissionsActivity.this, MyLocationActivity.class);
                    startActivity(missionsIntent);
                }
                else if(id == R.id.nav_missions) {
                    Intent missionsIntent = new Intent(ViewMissionsActivity.this, ViewMissionsActivity.class);
                    startActivity(missionsIntent);
                }
                else if (id == R.id.nav_stream) {
                    Intent streamIntent = new Intent(ViewMissionsActivity.this,
                            StreamingActivity.class);
                    startActivity(streamIntent);
                }
                else if (id == R.id.nav_preferences) {
                    // Handle the preference  action
                } else if (id == R.id.nav_about) {
                    // Handle the About action
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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
}
