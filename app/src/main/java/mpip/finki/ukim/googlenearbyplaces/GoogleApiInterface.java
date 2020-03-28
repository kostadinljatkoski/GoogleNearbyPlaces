package mpip.finki.ukim.googlenearbyplaces;

import mpip.finki.ukim.googlenearbyplaces.models.GoogleResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiInterface {

    @GET("maps/api/place/nearbysearch/json?key=AIzaSyDFD95-7_-cxek8FlCSrYphfxWMVIksEik&radius=5000&types=bank")
    Call<GoogleResponse> getBanks(@Query("location") String location);
}
