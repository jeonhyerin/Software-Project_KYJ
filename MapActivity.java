package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final String SERVICE_URL = "http://192.168.0.133/connect1.php";
    GoogleMap map;

    private String TAG = "MapDemo";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;


    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this, permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Unable to show location-permission required", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        new MarkerTask().execute();
    }



    class MarkerTask extends AsyncTask<Void, Void, String> {
        private static final String LOG_TAG = "ExampleApp";
        private static final String SERVICE_URL = "http://192.168.0.133/connect1.php";
        String sb = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(Void... args) {

            HttpURLConnection conn = null;
            final StringBuilder json = new StringBuilder();
            try {
                // Connect to the web service
                URL url = new URL(SERVICE_URL);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Read the JSON data into the StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    json.append(buff, 0, read);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error connecting to service", e);
                //throw new IOException("Error connecting to service", e); //uncaught
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            String sb = json.toString();
            return json.toString();
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String sb) {

            try {
                // De-serialize the JSON string into an array of city objects
                JSONArray jsonArray = new JSONObject(sb).getJSONArray("result");
                int cnt = jsonArray.length();
                String[] getName = new String[cnt];
                Double[] getLat = new Double[cnt];
                Double[] getLng = new Double[cnt];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    getName[i] = jsonObj.getString("user_id");
                    getLat[i] = jsonObj.getDouble("user_latitude");
                    getLng[i] = jsonObj.getDouble("user_longitude");
                    Log.i("JsonParsing", getLat[i] + "," + getLng[i] + "," + getName[i]);
                    LatLng latLng = new LatLng(getLat[i], getLng[i]);

                    //move CameraPosition on first result
                    if (i == 0) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng).zoom(13).build();

                        map.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                    }

                    // Create a marker for each city in the JSON data.
                    map.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .title(jsonObj.getString("user_id"))
                            .position(latLng));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error processing JSON", e);
            }

        }
    }

}









