package com.pml.pixfly.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pml.pixfly.common.Constants;
import com.pml.pixfly.bean.Mission;
import com.pml.pixfly.R;
import com.pml.pixfly.util.SocketUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlotMissionActivity extends FragmentActivity {

    private static final LatLng P1 = new LatLng(12.93662118,77.69591218);
    private static final LatLng P2 = new LatLng(12.93528495,77.69618531);
    private static final LatLng P3 = new LatLng(12.93525451,77.69622239);

    protected SocketUtil utilObj;
    private GoogleMap googleMap;
    Socket clientSocket = null;
    DataOutputStream os = null;
    DataInputStream is = null;
    Handler hand = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_mission);

        ArrayList<Mission> mission = (ArrayList<Mission>) getIntent().getExtras().getSerializable("mission");
        utilObj = new SocketUtil();
        //hand = new Handler();

        // check if we have got the googleMap already
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.plot_map)).getMap();
            if (googleMap != null) {
                addMarkers(mission);
                //addLines();
            }
        }

        //hand = new Handler();
        final ArrayList<Mission> traceMission = mission;

        Button btnTracePath = (Button) findViewById(R.id.trace_path_btn);
        btnTracePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = getBaseContext();
                CharSequence text = "Trace Mission Initiated!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                hand.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if ((traceMission != null) && (traceMission.size() != 0)) {
                            for (Mission mission : traceMission) {

                                String request = utilObj.createRequest(Constants.GOTO, Constants.MODE.GUIDED, mission.getLatitude(), mission.getLongitude());
                                //execute(request);

                                try {
                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(policy);
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


                                try {
                                    TimeUnit.MILLISECONDS.sleep(1000);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, 1000);

            }
        });
    }

    private void execute(String request) {

        try {
            clientSocket = utilObj.getClientSocket();
            os = new DataOutputStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());

            if (clientSocket != null && os != null && is != null) {
                //sendRequest(request);
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

    private void addMarkers(ArrayList<Mission> missionList) {
        for(Mission mission : missionList) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(mission.getLatitude(), mission.getLongitude())));
        }
        LatLng p1 = new LatLng(missionList.get(0).getLatitude(), missionList.get(0).getLongitude());
        // move camera to zoom on map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p1,13));
    }

    private void addLines() {
        googleMap.addPolyline((new PolylineOptions()).add(P1, P2, P3, P1).width(5).color(Color.BLUE).geodesic(true));
        // move camera to zoom on map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(P1,
                13));
    }
}