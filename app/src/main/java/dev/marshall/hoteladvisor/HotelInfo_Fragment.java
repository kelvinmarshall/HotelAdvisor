package dev.marshall.hoteladvisor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.marshall.hoteladvisor.Remote.IGeoCoordinates;
import dev.marshall.hoteladvisor.common.Common;
import dev.marshall.hoteladvisor.common.DirectionJSONParser;
import dev.marshall.hoteladvisor.model.Hotels;
import dev.marshall.hoteladvisor.model.MoreInfo;
import dev.marshall.hoteladvisor.model.MoreInfoCategory;
import dev.marshall.hoteladvisor.model.Rating;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelInfo_Fragment extends Fragment implements OnMapReadyCallback  {
    int color;

    public HotelInfo_Fragment() {
    }

    @SuppressLint("ValidFragment")
    public HotelInfo_Fragment(int color) {
        this.color = color;
    }

    private GoogleMap mMap;

    private final static int LOCATION_PERMISSION_REQUEST = 1001;

    private Location mLastLocation;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    Marker mCurrentmarker;
    Polyline polyline;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;

    private IGeoCoordinates mService;

    ProgressBar security, amenities, accessbility, value, comfort, staff, cleanliness;
    TextView sec, amen, access, val, comf, staf, clean;
    TextView HotelName, RealLocation, description, checkin, checkout, extras;
    TextView pa, fo, la, se, po, in, ba, be, rser, ch, sp, ca, airc, airport, gol, gy;
    String HotelId = "";

    String dining, recreation, near, additional;
    int valsec, valamen, valaccess, valmoney, valcomf, valstaff, valueclean;
    float averagesec, averageamen, averageacces, averageval, averagecomf, averagestaff, averageclean;
    TextView Averagerating,remark,no_reviewers,star_class;

    FirebaseDatabase database;
    DatabaseReference hotels, moreinfomation,ratings, rate;

    Hotels currentHotel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_hotel_info__fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //firebase
        database = FirebaseDatabase.getInstance();
        hotels = database.getReference("Hotels");
        ratings=database.getReference("Ratings");
        rate = database.getReference("Ratings").child(HotelId);
        moreinfomation = database.getReference("Hotels").child(HotelId).child("moreInfo");

        dining = Common.currentHotel.getMoreInfo().getDining();
        recreation = Common.currentHotel.getMoreInfo().getRecreation();
        near = Common.currentHotel.getMoreInfo().getNear();
        additional = Common.currentHotel.getMoreInfo().getAdditional();

        security = (ProgressBar) view.findViewById(R.id.sec);
        staff = (ProgressBar) view.findViewById(R.id.staf);
        amenities = (ProgressBar) view.findViewById(R.id.amen);
        accessbility = (ProgressBar) view.findViewById(R.id.access);
        value = (ProgressBar) view.findViewById(R.id.val);
        comfort = (ProgressBar) view.findViewById(R.id.comf);
        cleanliness = (ProgressBar) view.findViewById(R.id.clean);

        sec = (TextView) view.findViewById(R.id.valuesec);
        access = (TextView) view.findViewById(R.id.valaccess);
        amen=(TextView)view.findViewById(R.id.valamen);
        comf = (TextView) view.findViewById(R.id.valcomf);
        val = (TextView) view.findViewById(R.id.valmoney);
        clean = (TextView) view.findViewById(R.id.valclean);
        staf = (TextView) view.findViewById(R.id.valstaff);

        remark=(TextView)view.findViewById(R.id.Remark);
        Averagerating=(TextView)view.findViewById(R.id.avrating);
        no_reviewers=(TextView)view.findViewById(R.id.no_reviewers);
        star_class=(TextView)view.findViewById(R.id.star_class);

        getRateSecurity(HotelId);
        getRateStaff(HotelId);
        getRateAmenities(HotelId);
        getRateAccessbility(HotelId);
        getRateComfort(HotelId);
        getRateCleanliness(HotelId);
        getRateValue(HotelId);

        float avaragevaluerate=(averagesec+ averageamen+ averageacces+averageval+averagecomf+averagestaff+averageclean)/7;
        Averagerating.setText(String.valueOf(avaragevaluerate));
        final float avpercent=(valamen+valaccess+valsec+valstaff+valmoney+valueclean+valcomf)/7;
        //update value in algolia

        Client client=new Client(Common.APPLICATION_ID,Common.ADMIN_API_KEY);
        Index index = client.getIndex(Common.ALGOLIA_INDEX_NAME);
        JSONObject j = new JSONObject();
        try {
            j.put("Rating",avaragevaluerate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        index.partialUpdateObjectAsync(j,HotelId,null);

        ratings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(HotelId).exists()) {
                    remark.setText("Not Rated");

                } else {
                    if (avpercent >= 0 && avpercent < 20) {
                        remark.setText("Poor");
                    } else if (avpercent >= 21 && avpercent < 40) {
                        remark.setText("Fair");
                    } else if (avpercent >= 41 && avpercent < 60) {
                        remark.setText("Average");
                    } else if (avpercent >= 61 && avpercent < 80) {
                        remark.setText("Very Good");
                    } else {
                        remark.setText("Excellent");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        com.google.firebase.database.Query securityrating = rate.orderByChild("hotelID").equalTo(HotelId);
        securityrating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String no_reviewer_count= String.valueOf(postSnapshot.getChildrenCount());
                    no_reviewers.setText(String.format("Rating from %s reviewres",no_reviewer_count));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



        CircleDisplay cd = (CircleDisplay) view.findViewById(R.id.circleDisplay);
        cd.setColor(Color.parseColor("#00a680"));
        cd.setAnimDuration(3000);
        cd.setValueWidthPercent(55f);
        cd.setTextSize(14f);
        cd.setColor(Color.GREEN);
        cd.setDrawText(true);
        cd.setDrawInnerCircle(true);
        cd.setFormatDigits(1);
        cd.setTouchEnabled(false);
        cd.setUnit("%");
        cd.setStepSize(0.5f);
        // cd.setCustomText(...); // sets a custom array of text
        cd.showValue(avpercent, 100f, true);

        ExpandableLayout layout = (ExpandableLayout) view.findViewById(R.id.expandablelayout);

        layout.setRenderer(new ExpandableLayout.Renderer<MoreInfoCategory, MoreInfo>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void renderParent(View view, MoreInfoCategory moreInfoCategory, boolean isExpanded, int parentPosition) {
                ((TextView) view.findViewById(R.id.parent_name)).setText(moreInfoCategory.name);
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);

            }

            @Override
            public void renderChild(View view, MoreInfo moreInfo, int parentPosition, int childPosition) {
                ((TextView) view.findViewById(R.id.childtext)).setText(moreInfo.name);

            }
        });

        layout.addSection(getDining());
        layout.addSection(getRecreation());
        layout.addSection(getNear());
        layout.addSection(getAdditional());

        mService = Common.getGeoCodeService();

        //check permission
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_PERMISSION_REQUEST);
        } else {
            builLocationRequest();
            buildLocationCallBack();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }


        pa = (TextView) view.findViewById(R.id.pa);
        fo = (TextView) view.findViewById(R.id.fo);
        se = (TextView) view.findViewById(R.id.se);
        in = (TextView) view.findViewById(R.id.in);
        la = (TextView) view.findViewById(R.id.la);
        po = (TextView) view.findViewById(R.id.po);
        ba = (TextView) view.findViewById(R.id.txtbar);
        be = (TextView) view.findViewById(R.id.txtbeach);
        rser = (TextView) view.findViewById(R.id.txtrservice);
        ch = (TextView) view.findViewById(R.id.txtchild);
        sp = (TextView) view.findViewById(R.id.txtsap);
        ca = (TextView) view.findViewById(R.id.txtcasino);
        airc = (TextView) view.findViewById(R.id.txtair);
        airport = (TextView) view.findViewById(R.id.textshuttle);
        gol = (TextView) view.findViewById(R.id.txtgolf);
        gy = (TextView) view.findViewById(R.id.txtgym);

        HotelId = HotelDetails.HotelId;

        hotels.child(HotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentHotel = dataSnapshot.getValue(Hotels.class);
                if (currentHotel.getAmenities().getFood().equals("true")) {
                    fo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_cafe_black_24dp, 0, 0, 0);
                    fo.setTextColor(Color.BLACK);
                } else {
                    fo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.food_false, 0, 0, 0);
                    fo.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getInternet().equals("true")) {
                    in.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_black_24dp, 0, 0, 0);
                    in.setTextColor(Color.BLACK);
                } else {
                    in.setCompoundDrawablesWithIntrinsicBounds(R.drawable.internet_false, 0, 0, 0);
                    in.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getLaundry().equals("true")) {
                    la.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_laundry_service_black_24dp, 0, 0, 0);
                    la.setTextColor(Color.BLACK);
                } else {
                    la.setCompoundDrawablesWithIntrinsicBounds(R.drawable.laundry_false, 0, 0, 0);
                    la.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getSecurity().equals("true")) {
                    se.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_security_black_24dp, 0, 0, 0);
                    se.setTextColor(Color.BLACK);
                } else {
                    se.setCompoundDrawablesWithIntrinsicBounds(R.drawable.security_false, 0, 0, 0);
                    se.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getPool().equals("true")) {
                    po.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pool_black_24dp, 0, 0, 0);
                    po.setTextColor(Color.BLACK);
                } else {
                    po.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pool_false, 0, 0, 0);
                    po.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getParking().equals("true")) {
                    pa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_parking_black_24dp, 0, 0, 0);
                    pa.setTextColor(Color.BLACK);
                } else {
                    pa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.parking_false, 0, 0, 0);
                    pa.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getAirconditioning().equals("true")) {
                    airc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_airconditioning, 0, 0, 0);
                    airc.setTextColor(Color.BLACK);
                } else {
                    airc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_airconditioning, 0, 0, 0);
                    airc.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getAirportshuttle().equals("true")) {
                    airport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_airport_shuttle_black_24dp, 0, 0, 0);
                    airport.setTextColor(Color.BLACK);
                } else {
                    airport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_shuttle, 0, 0, 0);
                    airport.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getBar().equals("true")) {
                    ba.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_bar_black_24dp, 0, 0, 0);
                    ba.setTextColor(Color.BLACK);
                } else {
                    ba.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_bar, 0, 0, 0);
                    ba.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getBeach().equals("true")) {
                    be.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_beach_access_black_24dp, 0, 0, 0);
                    be.setTextColor(Color.BLACK);
                } else {
                    be.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_beach, 0, 0, 0);
                    be.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getCasino().equals("true")) {
                    ca.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_casino_black_24dp, 0, 0, 0);
                    ca.setTextColor(Color.BLACK);
                } else {
                    ca.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_calsino, 0, 0, 0);
                    ca.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getChild().equals("true")) {
                    ch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_child_friendly_black_24dp, 0, 0, 0);
                    ch.setTextColor(Color.BLACK);
                } else {
                    ch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_child, 0, 0, 0);
                    ch.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getGolf().equals("true")) {
                    gol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_golf_course_black_24dp, 0, 0, 0);
                    gol.setTextColor(Color.BLACK);
                } else {
                    gol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_golf, 0, 0, 0);
                    gol.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getGym().equals("true")) {
                    gy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fitness_center_black_24dp, 0, 0, 0);
                    gy.setTextColor(Color.BLACK);
                } else {
                    gy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_fitness, 0, 0, 0);
                    gy.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getRservice().equals("true")) {
                    rser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_room_service_black_24dp, 0, 0, 0);
                    rser.setTextColor(Color.BLACK);
                } else {
                    rser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_rservice, 0, 0, 0);
                    rser.setTextColor(Color.GRAY);
                }
                if (currentHotel.getAmenities().getSpa().equals("true")) {
                    sp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_spa_black_24dp, 0, 0, 0);
                    sp.setTextColor(Color.BLACK);
                } else {
                    sp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_spa, 0, 0, 0);
                    sp.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        HotelName = (TextView) view.findViewById(R.id.hotel_name);
        RealLocation = (TextView) view.findViewById(R.id.real_location);
        description = (TextView) view.findViewById(R.id.hotel_description);

        checkin = (TextView) view.findViewById(R.id.chkin);
        checkout = (TextView) view.findViewById(R.id.chkout);
        extras = (TextView) view.findViewById(R.id.Extras);


        if (!HotelId.isEmpty()) {
            getDetailHotel(HotelId);
        }
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
                if (mCurrentmarker != null)
                    mCurrentmarker.setPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));

                drawRoute(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Common.currentHotel);


            }
        };
    }
    private void builLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
    }
    private void drawRoute(final LatLng yourlocation, final Hotels currentHotel) {
        if (polyline!=null)
            polyline.remove();

        if (currentHotel.getLocationdetail() !=null && !currentHotel.getLocationdetail().isEmpty())
        {
            String address=currentHotel.getLocationdetail();
            mService.getGeoCode(address).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String>call, Response<String>response) {
                    try{
                        JSONObject jsonObject = new JSONObject(response.body().toString());

                        String lat = ((JSONArray)jsonObject.get("results"))
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location")
                                .get("lat").toString();

                        String lng = ((JSONArray)jsonObject.get("results"))
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location")
                                .get("lng").toString();

                        LatLng orderLocation = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                        float results[]=new float[1];
                        Location.distanceBetween(yourlocation.latitude,yourlocation.longitude,orderLocation.latitude
                                ,orderLocation.longitude,results);
                        Common.currentlocation = results[0]/1000.0f+" Km";
                        // update to distance to firebase
                        Map<String,Object> updatelatlng = new HashMap<>();
                        updatelatlng.put("lat",Double.parseDouble(lat));
                        updatelatlng.put("lng",Double.parseDouble(lng));
                        hotels.child(HotelId).updateChildren(updatelatlng);

                        //update algolia
                        Client client = new Client(Common.APPLICATION_ID, Common.ADMIN_API_KEY);
                        final Index index = client.getIndex("hotel_LOCATION");
                        JSONObject j = new JSONObject();
                        j.put("_geoloc", new JSONObject().put("lat", Double.parseDouble(lat)).put("lng",Double.parseDouble(lng)));
                        index.partialUpdateObjectAsync(j,HotelId,null);



                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.hotel_icon);

                        bitmap = Common.scaleBitmap(bitmap,200,200);

                        MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .title("Order of"+Common.currentHotel.getPhone())
                                .position(orderLocation)
                                .snippet("Distance = "+results[0]);
                        mMap.addMarker(marker);

                        //draw route
                        mService.getDirections(yourlocation.latitude+","+yourlocation.longitude
                                ,orderLocation.latitude+","+orderLocation.longitude)
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        new ParserTask().execute(response.body().toString());
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                    }
                                });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }
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

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "You should assign permission", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    private void getRateSecurity(String hotelId) {
        com.google.firebase.database.Query securityrating = rate.orderByChild("hotelID").equalTo(hotelId);
        securityrating.addValueEventListener(new ValueEventListener() {
            float count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Float.parseFloat(item.getValSecurity());
                    count++;
                }
                if (count != 0) {
                    averagesec = sum / count;
                }
                sec.setText(String.format("%s/5",averagesec));
                valsec = (int) (averagesec / 5 * 100);
                security.setMax(100);
                security.setProgress(0);
                security.setProgress(valsec);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    private void getRateAmenities(String hotelId) {
        com.google.firebase.database.Query productRating = rate.orderByChild("hotelID").equalTo(hotelId);

        productRating.addValueEventListener(new ValueEventListener() {
            float count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Float.parseFloat(item.getValAmenities());
                    count++;
                }
                if (count != 0) {
                    averageamen = sum / count;
                }
                amen.setText(String.format("%s/5",averageamen));
                valamen = (int) (averageamen / 5 * 100);
                amenities.setMax(100);
                amenities.setProgress(0);
                amenities.setProgress(valamen);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getRateAccessbility(String hotelId) {
        com.google.firebase.database.Query productRating = rate.orderByChild("hotelID").equalTo(hotelId);

        productRating.addValueEventListener(new ValueEventListener() {
            float count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Float.parseFloat(item.getValAccessbility());
                    count++;
                }
                if (count != 0) {
                    averageacces = sum / count;

                }
                valaccess = (int) (averageacces / 5 * 100);
                accessbility.setMax(100);
                accessbility.setProgress(0);
                accessbility.setProgress(valaccess);
                access.setText(String.format("%s/5",averageacces));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getRateComfort(String hotelId) {
        com.google.firebase.database.Query productRating = rate.orderByChild("hotelID").equalTo(hotelId);

        productRating.addValueEventListener(new ValueEventListener() {
            float count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Float.parseFloat(item.getValConfort());
                    count++;
                }
                if (count != 0) {
                    averagecomf = sum / count;
                }
                valcomf = (int) (averagecomf / 5 * 100);
                comfort.setMax(100);
                comfort.setProgress(0);
                comfort.setProgress(valcomf);
                comf.setText(String.format("%s/5",averagecomf));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getRateCleanliness(String hotelId) {
        com.google.firebase.database.Query productRating = rate.orderByChild("hotelID").equalTo(hotelId);

        productRating.addValueEventListener(new ValueEventListener() {
           float count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Float.parseFloat(item.getValCleanliness());
                    count++;
                }
                if (count != 0) {
                    averageclean = sum / count;
                }
                clean.setText(String.format("%s/5",averageclean));
                valueclean = (int) (averageclean / 5 * 100);
                cleanliness.setMax(100);
                cleanliness.setProgress(0);
                cleanliness.setProgress(valueclean);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getRateStaff(String hotelId) {
        com.google.firebase.database.Query productRating = rate.orderByChild("hotelID").equalTo(hotelId);

        productRating.addValueEventListener(new ValueEventListener() {
            float count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Float.parseFloat(item.getValStaff());
                    count++;
                }
                if (count != 0) {
                    averagestaff = sum / count;

                }
                staf.setText(String.format("%s/5",averagestaff));
                valstaff = (int) (averagestaff / 5 * 100);
                staff.setMax(100);
                staff.setProgress(0);
                staff.setProgress(valstaff);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getRateValue(String hotelId) {
        com.google.firebase.database.Query productRating = rate.orderByChild("hotelID").equalTo(hotelId);

        productRating.addValueEventListener(new ValueEventListener() {
            float count = 0, sum = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Float.parseFloat(item.getValMoney());
                    count++;
                }
                if (count != 0) {
                    averageval = sum / count;

                }
                val.setText(String.format("%s/5",averageval));
                valmoney = (int) (averageval / 5 * 100);
                value.setMax(100);
                value.setProgress(0);
                value.setProgress(valmoney);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private Section<MoreInfoCategory, MoreInfo> getDining() {
        Section<MoreInfoCategory, MoreInfo> section = new Section<>();
        MoreInfo moreInfo = new MoreInfo(dining);

        MoreInfoCategory moreInfoCategory = new MoreInfoCategory("Dining");
        section.parent = moreInfoCategory;
        section.children.add(moreInfo);
        return section;
    }

    private Section<MoreInfoCategory, MoreInfo> getRecreation() {
        Section<MoreInfoCategory, MoreInfo> section = new Section<>();
        MoreInfo moreInfo = new MoreInfo(recreation);
        MoreInfoCategory moreInfoCategory = new MoreInfoCategory("Recreation");

        section.parent = moreInfoCategory;
        section.children.add(moreInfo);
        return section;
    }

    private Section<MoreInfoCategory, MoreInfo> getNear() {
        Section<MoreInfoCategory, MoreInfo> section = new Section<>();
        MoreInfo moreInfo = new MoreInfo(near);
        MoreInfoCategory moreInfoCategory = new MoreInfoCategory("What's Near");

        section.parent = moreInfoCategory;
        section.children.add(moreInfo);
        return section;
    }

    private Section<MoreInfoCategory, MoreInfo> getAdditional() {
        Section<MoreInfoCategory, MoreInfo> section = new Section<>();
        MoreInfo moreInfo = new MoreInfo(additional);
        MoreInfoCategory moreInfoCategory = new MoreInfoCategory("Additional Information");

        section.parent = moreInfoCategory;
        section.children.add(moreInfo);
        return section;
    }

    private void getDetailHotel(String hotelId) {
        hotels.child(HotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentHotel = dataSnapshot.getValue(Hotels.class);

                HotelName.setText(currentHotel.getName());

                RealLocation.setText(currentHotel.getLocationdetail());

                description.setText(currentHotel.getDescription());

                checkin.setText(currentHotel.getCheckin());

                checkout.setText(currentHotel.getCheckout());

                extras.setText(currentHotel.getExtras());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLastLocation=location;
                LatLng yourlocation=new LatLng(location.getLatitude(),location.getLatitude());
                mCurrentmarker= mMap.addMarker(new MarkerOptions().position(yourlocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourlocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
            }
        });

    }



    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
        ProgressDialog mDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please wait...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String,String>>> routes=null;
            try{
                jObject=new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                routes = parser.parse(jObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList points = null;
            PolylineOptions lineOptions=null;

            for (int i=0;i<lists.size();i++)
            {
                points=new ArrayList();
                lineOptions=new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);

                for (int j=0;j<path.size();j++)
                {
                    HashMap<String,String> point = path.get(j);

                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat,lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }
            if(points!=null)
            {
                mMap.addPolyline(lineOptions);
            }

        }
    }
}

