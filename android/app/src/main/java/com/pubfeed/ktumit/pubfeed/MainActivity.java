package com.pubfeed.ktumit.pubfeed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private LinearLayout mapRootLL;
    private EditText msgET;
private String color ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        msgET = (EditText) findViewById(R.id.messageET);
        mapRootLL = (LinearLayout) findViewById(R.id.mapRootLL);
        Random rnd = new Random();
        int intColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        color = String.format("#%06X", 0xFFFFFF & intColor);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initMessageList();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocation();
        getMyName();
    }

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();


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
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    Menu menu;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (mapRootLL.getVisibility() == View.VISIBLE) {
                mapRootLL.setVisibility(View.GONE);
                menu.findItem(R.id.action_settings).setIcon(R.drawable.map_menu);
            } else {
                mapRootLL.setVisibility(View.VISIBLE);
                menu.findItem(R.id.action_settings).setIcon(R.drawable.buble);

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Trabzon and move the camera
    }

    ArrayList<Message> messages = new ArrayList<>();
    MessageAdapter messageAdapter;
    ListView messageList;

    private void initMessageList() {

        messageList = (ListView) findViewById(R.id.message_list);
        messageAdapter = new MessageAdapter(this, R.layout.message_item, messages);
        messageList.setAdapter(messageAdapter);
    }


    private static final String TAG = "MAINACTIVITY";

    private final WebSocketConnection mConnection = new WebSocketConnection();


    private void start() {
        if (!mConnection.isConnected()) {
            final String wsuri = "ws://188.226.238.213:8888/msg";

            try {
                mConnection.connect(wsuri, new WebSocketHandler() {

                    @Override
                    public void onOpen() {
                        Log.d(TAG, "Status: Connected to " + wsuri);
                        JSONObject jObj = new JSONObject();
                        try {
                            jObj.put("_coordinates", getLatLng());
                            jObj.put("token", "4yw6opzmt4k7zjuh7o86ywrk9qj1wfj5pb0h39xtia9kltbj4i");

                        } catch (Exception e) {
                        }
                        mConnection.sendTextMessage(jObj.toString());
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.d(TAG, "Got echo: " + payload);
                        try {
                            JSONObject jObj = new JSONObject(payload);
                            String name = jObj.getString("name");
                            String msg = jObj.getString("message");
                            String color = jObj.getString("color");
                            messages.add(new Message(name, msg, color));
                            messageAdapter.notifyDataSetChanged();
                            scrollMyListViewToBottom();
                            if (!markers.contains(name)) {
                                JSONObject location = jObj.getJSONObject("location");
                                LatLng loc = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                                mMap.addMarker(new MarkerOptions().position(loc));
                                markers.add(name);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d(TAG, "Connection lost.");
                    }
                });
            } catch (WebSocketException e) {

                Log.d(TAG, e.toString());
            }
        }
    }

    private ArrayList<String> markers = new ArrayList<>();

    public void sendMessage(View v) {
        if (mConnection != null) {
            JSONObject jObj = new JSONObject();
            try {
                jObj.put("message", msgET.getText().toString());
                jObj.put("color", color);
                jObj.put("name", getMyName());
                jObj.put("location", getLatLng());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mConnection.sendTextMessage(jObj.toString());
            msgET.setText("");
        }
    }

    Marker markerMe;

    private void getLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    Location mLastLocation;

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            LatLng markerMeLL = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (markerMe == null) {
                markerMe = mMap.addMarker(new MarkerOptions().position(markerMeLL));
                markers.add(getMyName());
            } else
                markerMe.setPosition(markerMeLL);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerMeLL, 15));
            start();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private JSONObject getLatLng() {
        JSONObject jCoor = new JSONObject();
        try {
            if (mLastLocation != null) {
                jCoor.put("lat", mLastLocation.getLatitude());
                jCoor.put("lng", mLastLocation.getLongitude());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jCoor;
    }


    private String getMyName() {
        SharedPreferences sharedPref = getSharedPreferences("PUBFEED", Context.MODE_PRIVATE);
        return sharedPref.getString("pubfeed_name", "");
    }

    private void scrollMyListViewToBottom() {
        messageList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                messageList.setSelection(messageAdapter.getCount() - 1);
            }
        });
    }
}
