package com.pml.pixfly;

import android.app.Activity;
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
import android.support.v7.app.AlertDialog;
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

public class DroneLaunch extends Activity implements LocationListener {

    Socket clientSocket;
    GPSTracker gps;
    DataOutputStream os = null;
    DataInputStream is = null;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String provider;
    protected double latitude,longitude;
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

   /* Runnable run = new Runnable() {
        @Override
        public void run() {
            followMeTask();
        }
    };*/


    private void followMeTask(){
        clientSocket = utilObj.getClientSocket();
        String request = utilObj.createRequest(DroneCodes.FOLLOW_ME, DroneCodes.MODE.GUIDED, latitude, longitude);
        execute(request);
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
                            String request = utilObj.createRequest(DroneCodes.FOLLOW_ME, DroneCodes.MODE.GUIDED, latitude, longitude);

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

                            } catch (UnknownHostException ue) {
                                System.err.println("Don't know about host: hostname");
                            } catch (IOException e) {
                                System.err.println("Couldn't get I/O for the connection to: hostname");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(enableFollowMe) {
                                hand.postDelayed(this, 5000);
                            } else {
                                hand.removeCallbacks(this);
                            }
                        }
                    }, 5000);

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

        Button btnComeToMe = (Button) findViewById(R.id.cometome);
        btnComeToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableFollowMe = false;
                String request = utilObj.createRequest(DroneCodes.GOTO, DroneCodes.MODE.GUIDED, latitude, longitude);
                execute(request);
            }
        });

        Button btnLand = (Button) findViewById(R.id.land);
        btnLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableFollowMe = false;
                String request = utilObj.createRequest(DroneCodes.LAND, DroneCodes.MODE.GUIDED, latitude, longitude);
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
            gps = new GPSTracker(DroneLaunch.this);

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
    /*----------Method to create an AlertBox ------------- */
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*----Method to Check GPS is enable or disable ----- */
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
        } while (DroneLaunch.enableFollowMe);
        return "";
    }
}*/
