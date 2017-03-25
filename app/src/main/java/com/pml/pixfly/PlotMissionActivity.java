package com.pml.pixfly;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class PlotMissionActivity extends FragmentActivity {

    private static final LatLng P1 = new LatLng(12.93662118,77.69591218);
    private static final LatLng P2 = new LatLng(12.93528495,77.69618531);
    private static final LatLng P3 = new LatLng(12.93525451,77.69622239);

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_mission);
        setUpMap();
    }

    private void setUpMap() {
        // check if we have got the googleMap already
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.plot_map)).getMap();
            if (googleMap != null) {
                addLines();
            }
        }
    }

    private void addLines() {

        googleMap
                .addPolyline((new PolylineOptions())
                        .add(P1, P2, P3,
                                P1).width(5).color(Color.BLUE)
                        .geodesic(true));
        // move camera to zoom on map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(P1,
                13));
    }
}

