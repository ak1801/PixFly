package com.pml.pixfly.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;

import com.pml.pixfly.R;

public class StreamingActivity extends AppCompatActivity {

    WebView webView;
    //String StreamURL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
    String StreamURL = "http://169.254.114.33:9090/stream";
    //String StreamURL = "http://trackfield.webcam.oregonstate.edu/axis-cgi/mjpg/video.cgi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        displayNav();
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setPadding(0, 0, 0, 0);
        webView.setInitialScale(getScale());
        webView.loadUrl(StreamURL);
    }

    private int getScale(){
        Display display = ((WindowManager) getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width)/new Double(640);
        val = val * 100d;
        return val.intValue();
    }

    private void displayNav(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_stream);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.nav_location) {
                    Intent locationIntent = new Intent(StreamingActivity.this, MyLocationActivity.class);
                    startActivity(locationIntent);
                }
                else if(id == R.id.nav_missions) {
                    Intent missionsIntent = new Intent(StreamingActivity.this, ViewMissionsActivity.class);
                    startActivity(missionsIntent);
                }
                else if (id == R.id.nav_missions) {
                    Intent streamIntent = new Intent(StreamingActivity.this,
                            StreamingActivity.class);
                    startActivity(streamIntent);
                }
                else if (id == R.id.nav_stream) {
                    Intent myIntent = new Intent(StreamingActivity.this,
                            StreamingActivity.class);
                    startActivity(myIntent);
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
        getMenuInflater().inflate(R.menu.menu_streaming, menu);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
