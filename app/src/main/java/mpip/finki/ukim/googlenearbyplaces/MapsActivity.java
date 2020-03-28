package mpip.finki.ukim.googlenearbyplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import mpip.finki.ukim.googlenearbyplaces.models.GoogleResponse;
import mpip.finki.ukim.googlenearbyplaces.models.GoogleResultsItem;
import mpip.finki.ukim.googlenearbyplaces.services.LocationService;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker marker;
    private LocationResultReceiver receiver;
    public static final String GOOGLE_RESPONSE = "googleResponse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        receiver = new LocationResultReceiver(new Handler());
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenLocationUpdates();
    }


    private void listenLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  60 * 1000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,  60 * 1000, 0, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currentLocation(location);
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("locationData", location);
        intent.putExtra("locationResultReceiver", receiver);
        startService(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        currentLocation(location);
        marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("locationData", location);
        intent.putExtra("locationResultReceiver", receiver);
        startService(intent);
    }


    private void currentLocation(Location location) {
        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
        final MarkerOptions markerOptions = new MarkerOptions()
                .position(current)
                .draggable(false)
                .title("Current Location");
        marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),13f));
    }

    public void showNearbyPlaces(GoogleResponse googleResponse) {
        for(GoogleResultsItem item : googleResponse.results) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(item.geometry.location.lat, item.geometry.location.lng))
                    .draggable(false)
                    .title(item.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class LocationResultReceiver extends ResultReceiver {

        public LocationResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultCode == LocationService.RESULT_CODE) {
                GoogleResponse googleResponse = (GoogleResponse) resultData.getSerializable(GOOGLE_RESPONSE);
                showNearbyPlaces(googleResponse);
            }
            
        }
    }
}
