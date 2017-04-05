package com.pml.pixfly.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.pml.pixfly.common.Constants;
import com.pml.pixfly.util.FileOperationsUtil;
import com.pml.pixfly.bean.Payload;
import com.pml.pixfly.R;
import com.pml.pixfly.util.SocketUtil;

import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayNav();

        //seedFile();

        if(displayGpsStatus()) {

            final ImageView imgbtn = (ImageView) findViewById(R.id.drone);
            imgbtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    imgbtn.setImageResource(R.drawable.drone_launching);
                    imgbtn.setEnabled(false);

                    Socket clientSocket = null;
                    DataOutputStream os = null;
                    DataInputStream is = null;
                    SocketUtil utilObj = new SocketUtil();
                    /*Context context = getApplicationContext();
                    Intent intent = new Intent(context,DroneActivity.class);
                    startActivity(intent);*/
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        clientSocket = utilObj.getClientSocket();
                        os = new DataOutputStream(clientSocket.getOutputStream());
                        is = new DataInputStream(clientSocket.getInputStream());

                        Payload payload = new Payload();
                        payload.setCommand(Constants.LAUNCH);
                        payload.setMode(Constants.MODE.GUIDED);
                        payload.setAlt(5.0);
                        Gson gson = new Gson();
                        String str = gson.toJson(payload);
                        Log.i("Gson String : ", str);
                        os.writeBytes(str.length() + "|");
                        os.writeBytes(str);

                        clientSocket.shutdownOutput();
                        if (clientSocket != null && is != null) {

                            String serverResp = utilObj.getResponse(is);
                            if (serverResp.equals("200")) {

                                Context context = getApplicationContext();
                                CharSequence text = "Launch Complete!";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                os.close();
                                is.close();
                                clientSocket.close();

                                //imgbtn.setImageResource(R.drawable.drone);
                                Intent intent = new Intent(context, DroneActivity.class);
                                startActivity(intent);
                            }
                        }


                    } catch (UnknownHostException ue) {
                        System.err.println("Don't know about host: hostname");
                    } catch (IOException e) {
                        System.err.println("Couldn't get I/O for the connection to: hostname");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }
            });
        }
        else {
            alertbox("Gps Status!!", "Your GPS is: OFF");
        }
    }

    private void seedFile() {
        Date date = new Date();
        String mission0 = Constants.FOLLOW_ME+";"+date.toString()+";"+"12.93530426;77.69615321";
        FileOperationsUtil.writeToFile(getApplicationContext(), mission0, Constants.DUMMY, getApplicationContext().MODE_APPEND);

        String mission1 = Constants.FOLLOW_ME+";"+date.toString()+";"+"12.93532449;77.69617006";
        FileOperationsUtil.writeToFile(getApplicationContext(), mission1, Constants.DUMMY, getApplicationContext().MODE_APPEND);

        String mission2 = Constants.FOLLOW_ME+";"+date.toString()+";"+"12.93531365;77.6962003";
        FileOperationsUtil.writeToFile(getApplicationContext(), mission2, Constants.DUMMY, getApplicationContext().MODE_APPEND);

        String mission3 = Constants.FOLLOW_ME+";"+date.toString()+";"+"12.9353287;77.69617836";
        FileOperationsUtil.writeToFile(getApplicationContext(), mission3, Constants.DUMMY, getApplicationContext().MODE_APPEND);
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
                    Intent missionsIntent = new Intent(MainActivity.this, MyLocationActivity.class);
                    startActivity(missionsIntent);
                }
                else if(id == R.id.nav_missions) {
                    Intent missionsIntent = new Intent(MainActivity.this, ViewMissionsActivity.class);
                    startActivity(missionsIntent);
                }
                else if (id == R.id.nav_stream) {
                    Intent streamIntent = new Intent(MainActivity.this,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
