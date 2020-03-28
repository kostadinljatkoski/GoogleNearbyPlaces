package mpip.finki.ukim.googlenearbyplaces.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import mpip.finki.ukim.googlenearbyplaces.GoogleApiInterface;
import mpip.finki.ukim.googlenearbyplaces.MapsActivity;
import mpip.finki.ukim.googlenearbyplaces.clients.GoogleApiClient;
import mpip.finki.ukim.googlenearbyplaces.models.GoogleResponse;
import mpip.finki.ukim.googlenearbyplaces.models.GoogleResultsItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.io.Serializable;

public class LocationService extends IntentService {

    public static int RESULT_CODE = 1234567;


    public LocationService() {
        super("LocationService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("======================================HANDLE INTENT==========================================");
        ResultReceiver receiver = intent.getParcelableExtra("locationResultReceiver");
        Location location = intent.getParcelableExtra("locationData");
        Bundle b = new Bundle();
        b.putSerializable(MapsActivity.GOOGLE_RESPONSE, getNearbyPlaces(location));
        if(receiver != null)
            receiver.send(RESULT_CODE, b);
    }

    private GoogleResponse getNearbyPlaces(Location location) {
        GoogleApiInterface googleApiInterface = GoogleApiClient.getRetrofit().create(GoogleApiInterface.class);
        Call<GoogleResponse> response = googleApiInterface.getBanks(location.getLatitude() + "," + location.getLongitude());
        try {
            GoogleResponse googleResponse = response.execute().body();
            return googleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
