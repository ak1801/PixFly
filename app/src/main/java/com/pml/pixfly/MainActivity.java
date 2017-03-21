package com.pml.pixfly;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    Intent intent = new Intent(context, DroneLaunch.class);
                    startActivity(intent);*/
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        clientSocket = utilObj.getClientSocket();
                        os = new DataOutputStream(clientSocket.getOutputStream());
                        is = new DataInputStream(clientSocket.getInputStream());

                        Payload payload = new Payload();
                        payload.setCommand(DroneCodes.LAUNCH);
                        payload.setMode(DroneCodes.MODE.GUIDED);
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
                                Intent intent = new Intent(context, DroneLaunch.class);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
