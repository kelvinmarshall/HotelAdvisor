package dev.marshall.hoteladvisor;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.ListView;
import android.widget.Toast;

import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.search.saas.AbstractQuery;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import dev.marshall.hoteladvisor.common.Common;
import dev.marshall.hoteladvisor.io_nearby.NearbyAdapter;
import dev.marshall.hoteladvisor.io_nearby.SearchResultsJsonParser;
import dev.marshall.hoteladvisor.model.HotelSearch;

import static dev.marshall.hoteladvisor.common.Common.ALGOLIA_INDEX_NAME;

public class NearBy extends AppCompatActivity {

    private Client client;
    private Index index;
    private Query query;
    private SearchResultsJsonParser resultsParser = new SearchResultsJsonParser();
    private int lastSearchedSeqNo;
    private int lastDisplayedSeqNo;
    private int lastRequestedPage;
    private int lastDisplayedPage;
    private boolean endReached;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private NearbyAdapter hotelListAdapter;

    private Location mLastLocation;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    // Constants
    private final static int LOCATION_PERMISSION_REQUEST = 1001;
    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_INTERVAL = 5000;

    private static final int HITS_PER_PAGE = 20;

    /** Number of items before the end of the list past which we start loading more content. */
    private static final int LOAD_MORE_THRESHOLD = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_nearby);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        client=new Client(Common.APPLICATION_ID,Common.ADMIN_API_KEY);
        index=client.getIndex(ALGOLIA_INDEX_NAME);

        // Pre-build query.
        query = new Query();
        query.setAttributesToRetrieve("Name", "Image", "Price", "Rating");
        query.setHitsPerPage(HITS_PER_PAGE);

        displayresult();

    }
    private void builLocationRequest() {
        locationRequest= LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

    }

    private void buildLocationCallBack() {
        locationCallback =new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation=locationResult.getLastLocation();

            }
        };
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        builLocationRequest();
                        buildLocationCallBack();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(NearBy.this);
                        if (ActivityCompat.checkSelfPermission(NearBy.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    else
                    {
                        Toast.makeText(NearBy.this, "You should assign permission", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
            default:
                break;
        }
    }


    public void onStop() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }
    private void displayresult() {
        AbstractQuery.LatLng latLng=new AbstractQuery.LatLng(Common.CURRENT_LOCATION.latitude,Common.CURRENT_LOCATION.longitude);

        query.setAroundLatLng(latLng).setAroundRadius(50000);
        endReached = false;
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {

                List<HotelSearch> results=resultsParser.parseResults(jsonObject);

                if (results.isEmpty()){
                    endReached=true;
                }
                else
                {
                    hotelListAdapter=new NearbyAdapter(NearBy.this,results);
                    recyclerView.setAdapter(hotelListAdapter);
                    hotelListAdapter.notifyDataSetChanged();
                }

            }
        });

    }
}
