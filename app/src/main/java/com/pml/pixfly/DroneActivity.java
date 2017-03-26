package com.pml.pixfly;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DroneActivity extends AppCompatActivity implements LocationListener {

    Socket clientSocket;
    GPSTracker gps;
    DataOutputStream os = null;
    DataInputStream is = null;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String provider;
    protected Double latitude=0.0;
    protected Double longitude=0.0;
    protected boolean gps_enabled,network_enabled;
    protected SocketUtil utilObj;
    public static boolean enableFollowMe = false;
    Handler hand = new Handler();

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude(), Toast.LENGTH_LONG).show();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }


    private void followMeTask(){
        clientSocket = utilObj.getClientSocket();
        String request = utilObj.createRequest(Constants.FOLLOW_ME, Constants.MODE.GUIDED, latitude, longitude);
        execute(request);
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
                    Intent missionsIntent = new Intent(DroneActivity.this, MyLocationActivity.class);
                    startActivity(missionsIntent);
                }
                else if(id == R.id.nav_missions) {
                    Intent missionsIntent = new Intent(DroneActivity.this, ViewMissionsActivity.class);
                    startActivity(missionsIntent);
                }
                else if (id == R.id.nav_stream) {
                    Intent streamIntent = new Intent(DroneActivity.this,
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_launch);
        utilObj = new SocketUtil();
        hand = new Handler();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Button btnFollowMe = (Button) findViewById(R.id.followme);
        btnFollowMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableFollowMe = true;

                hand.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String request = utilObj.createRequest(Constants.FOLLOW_ME, Constants.MODE.GUIDED, latitude, longitude);
                            //String mission = Constants.FOLLOW_ME+":"+new Date().toString();

                            Mission follow_me = new Mission();
                            follow_me.setMission_name(Constants.FOLLOW_ME);
                            follow_me.setLatitude(latitude);
                            follow_me.setLongitude(longitude);
                            follow_me.setLaunch_date(new Date().toString());

                            try {
                                clientSocket = utilObj.getClientSocket();
                                os = new DataOutputStream(clientSocket.getOutputStream());
                                is = new DataInputStream(clientSocket.getInputStream());

                                if (clientSocket != null && os != null && is != null) {
                                    sendRequest(request);
                                    if (clientSocket != null && is != null) {
                                        String serverResp = utilObj.getResponse(is);
                                        if (serverResp.equals("200")) {
                                            Context context = getApplicationContext();
                                            CharSequence text = "Task Complete!";

                                            //String gps_coordinates = latitude.toString()+","+longitude.toString();
                                            FileOperationsUtil.writeToFile(getBaseContext(), follow_me.toString(), Constants.MISSIONS, getBaseContext().MODE_APPEND);

                                            int duration = Toast.LENGTH_SHORT;
                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                            os.close();
                                            is.close();
                                            clientSocket.close();
                                        }
                                    }
                                    os.close();
                                    is.close();
                                    clientSocket.close();
                                }

                            } catch (UnknownHostException ue) {
                                System.err.println("Don't know about host: hostname");
                            } catch (IOException e) {
                                System.err.println("Couldn't get I/O for the connection to: hostname");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(enableFollowMe) {
                                hand.postDelayed(this, 1000);
                            } else {
                                hand.removeCallbacks(this);
                            }
                        }
                    }, 1000);

                /*Integer count = 0;

                do {
                    clientSocket = utilObj.getClientSocket();
                    String request = utilObj.createRequest(DroneCodes.FOLLOW_ME, DroneCodes.MODE.GUIDED, latitude, longitude);
                    new Thread(
                            new WorkerRunnable(
                                    clientSocket, request)
                    ).start();
                    count++;
                } while (enableFollowMe);*/

               /* new Thread(new Runnable() {
                    public void run() {
                        Integer count = 0;
                        do {
                            Log.i("Follow me execution : ", count.toString());
                            String request = utilObj.createRequest(DroneCodes.FOLLOW_ME, DroneCodes.MODE.GUIDED, latitude, longitude);
                            execute(request);
                            try {
                                Thread.sleep(5000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            count++;
                        } while (count<2);
                    }
                }).start();*/
            }
        });

        Button btnTracePath = (Button) findViewById(R.id.trace_path);
        btnTracePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableFollowMe = false;
                Double[] __coordinate = new Double[2];
                ArrayList<String> coordinatesList = FileOperationsUtil.readFromFile(getBaseContext(), Constants.TRACE_ME_FILE);
                if ((coordinatesList != null) && (coordinatesList.size() != 0)) {
                    for (String coordinate : coordinatesList) {
                        String[] _coordinate = coordinate.split(",");
                        for(int i = 0; i < _coordinate.length; i++)
                        {
                            __coordinate[i] = Double.parseDouble(_coordinate[i]);
                        }
                        String request = utilObj.createRequest(Constants.GOTO, Constants.MODE.GUIDED, __coordinate[0], __coordinate[1]);
                        execute(request);
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    //showToast("Reports are empty!");
                }

            }
        });

        Button btnComeToMe = (Button) findViewById(R.id.cometome);
        btnComeToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableFollowMe = false;
                String request = utilObj.createRequest(Constants.GOTO, Constants.MODE.GUIDED, latitude, longitude);
                execute(request);
            }
        });

        Button btnLand = (Button) findViewById(R.id.land);
        btnLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableFollowMe = false;
                String request = utilObj.createRequest(Constants.LAND, Constants.MODE.GUIDED, latitude, longitude);
                execute(request);
            }
        });
    }

    private void sendRequest(String request) {
        Log.i("Gson String : ", request);
        try {
            os.writeBytes(request.length() + "|");
            os.writeBytes(request);
            clientSocket.shutdownOutput();
        }catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
    private void execute(String request) {

        if(displayGpsStatus()) {
            gps = new GPSTracker(DroneActivity.this);

            // Check if GPS enabled
            if(gps.canGetLocation()) {

                //double latitude = gps.getLatitude();
                //double longitude = gps.getLongitude();

                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                try {
                    clientSocket = utilObj.getClientSocket();
                    os = new DataOutputStream(clientSocket.getOutputStream());
                    is = new DataInputStream(clientSocket.getInputStream());

                    if (clientSocket != null && os != null && is != null) {
                       sendRequest(request);
                       if (clientSocket != null && is != null) {
                          String serverResp = utilObj.getResponse(is);
                          if (serverResp.equals("200")) {
                             Context context = getApplicationContext();
                             CharSequence text = "Task Complete!";
                             int duration = Toast.LENGTH_SHORT;
                             Toast toast = Toast.makeText(context, text, duration);
                             toast.show();
                             os.close();
                             is.close();
                             clientSocket.close();
                          }
                       }
                       os.close();
                       is.close();
                       clientSocket.close();
                    }

                }catch (UnknownHostException ue) {
                    System.err.println("Don't know about host: hostname");
                } catch (IOException e) {
                    System.err.println("Couldn't get I/O for the connection to: hostname");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                gps.showSettingsAlert();
            }
        }
        else {
            alertbox("Gps Status!!", "Your GPS is: OFF");
        }
    }

    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drone_launch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

/*----------Listener class to get coordinates ------------- */
/*class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location loc) {

        String longitude = "Longitude: " +loc.getLongitude();
        String latitude = "Latitude: " +loc.getLatitude();
        Log.i("Latitude", latitude);
        Log.i("Longitude", longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}*/

/*class FollowMeTask extends AsyncTask<Double, Void, String> {
    protected String doInBackground(Double... params) {
        return executeTask(params[0], params[1]);
    }

    protected void onPostExecute(Socket clientSocket) {

    }

    private String executeTask(double latitude, double longitude){
        SocketUtil utilObj = new SocketUtil();
        Socket clientSocket = null;
        do {
            clientSocket = utilObj.getClientSocket();
            String request = utilObj.createRequest(DroneCodes.FOLLOW_ME, DroneCodes.MODE.GUIDED, latitude, longitude);
            new Thread(
                    new WorkerRunnable(
                            clientSocket, request)
            ).start();
        } while (DroneActivity.enableFollowMe);
        return "";
    }
}*/
