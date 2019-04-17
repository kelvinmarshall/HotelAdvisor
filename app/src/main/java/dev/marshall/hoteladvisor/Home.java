package dev.marshall.hoteladvisor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisor.Database.Database;
import dev.marshall.hoteladvisor.Interface.ItemClickListener;
import dev.marshall.hoteladvisor.Remote.IGeoCoordinates;
import dev.marshall.hoteladvisor.common.Common;
import dev.marshall.hoteladvisor.io_nearby.SearchResultsJsonParser;
import dev.marshall.hoteladvisor.model.Favourites;
import dev.marshall.hoteladvisor.model.Hotels;
import dev.marshall.hoteladvisor.viewHolder.MenuViewHolder;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dev.marshall.hoteladvisor.common.Common.currentHotel;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener{

    String APPLICATION_ID="QZNK9LXV19";
    String API_KEY="4ac09b7d0adce89157de480429a3e747";
    public static final String ALGOLIA_INDEX_NAME = "hotel_LOCATION";
    String ADMIN_API_KEY="a3a6ffd0e0da9f2ef03789d9ef016384";

   // private FilterResultsFragment filterResultsFragment;
    int total_pages;
    int nbPage;

    private GoogleMap mMap;

    private final static int LOCATION_PERMISSION_REQUEST = 1001;

    private Location mLastLocation;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;

    private IGeoCoordinates mService;

    private  Client client;
    String HotelId;


    FirebaseDatabase database;
    DatabaseReference menu;
    TextView txtfullname;
    ImageView imageView;
    TextView sortby;

    ProgressBar circular_progress_dialog;
    SwipeRefreshLayout swipeRefreshLayout;

    private SearchResultsJsonParser resultsParser = new SearchResultsJsonParser();

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    //sortview
    MaterialSpinner spinnername,spinnerloction,spinnerprice;
    RadioButton Rname,Rlocation,Rprice;

    //FavouritesActicity
    Database localDB;


    FirebaseRecyclerAdapter<Hotels, MenuViewHolder> adapter;

    //search bar
    FirebaseRecyclerAdapter<Hotels,MenuViewHolder> searchadapter,nameadapter,locationadapter,priceadapter;
    List<String> suggestList=new ArrayList<>();

    MaterialSearchBar materialSearchBar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (FirebaseApp.getApps(getApplicationContext()).isEmpty()) {
            FirebaseApp.initializeApp(getApplicationContext());
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        client = new Client(APPLICATION_ID, ADMIN_API_KEY);
        final Index index = client.getIndex(ALGOLIA_INDEX_NAME);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // toolbar.setTitle("HotelAdvisor");
       // setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
          //      this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
       // drawer.addDrawerListener(toggle);
       // toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Init Firebase
        database=FirebaseDatabase.getInstance();
        menu = database.getReference("Hotels");

        //LocalDB
        localDB= new Database(this);

        Paper.init(this);

        //Set name for user
        View headerView=navigationView.getHeaderView(0);
        imageView=(ImageView)headerView.findViewById(R.id.imageView);

        GlideApp.with(Home.this).load(Common.currentUser.getImage())
                .placeholder(R.drawable.ic_person_outline_black_24dp)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile=new Intent(Home.this,Profile.class);
                startActivity(profile);
            }
        });
        txtfullname=(TextView)headerView.findViewById(R.id.txtfullName);
        txtfullname.setText(Common.currentUser.getName());
        //Load menu
        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        circular_progress_dialog=(ProgressBar)findViewById(R.id.circular_progress_dialog);

        sortby=(TextView)findViewById(R.id.sort);
        sortby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorthotels();
            }
        });

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.SwiperefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.black,
                android.R.color.holo_red_dark);
                if (Common.isConnectedToInternet(getBaseContext())){
                    loadMenu();
                }
                else
                {
                   new  SweetAlertDialog(Home.this,SweetAlertDialog.ERROR_TYPE)
                           .setContentText("Please check your internet connection")
                           .show();
                }
                //Search
                materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.inflateMenu(R.menu.home);
                loadSuggest();//Write function to load suggestion from firebase
                materialSearchBar.setLastSuggestions(suggestList);
                materialSearchBar.getMenu().setOnMenuItemClickListener(Home.this);


                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        List<String> suggest = new ArrayList<String>();
                        for (String search:suggestList)
                        {
                            if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        //When search bar is close
                        //Restore original suggest adapter
                        if (!enabled)
                            recycler_menu.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        //When search finish
                        //show result of search adapter
                        startSearch(text);

                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {
                        switch (buttonCode){
                            case MaterialSearchBar.BUTTON_NAVIGATION:
                                drawer.openDrawer(Gravity.START);
                                break;
                            case MaterialSearchBar.BUTTON_SPEECH:

                        }
                    }
                });


        mService = Common.getGeoCodeService();

        //check permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_PERMISSION_REQUEST);
        } else {
            builLocationRequest();
            buildLocationCallBack();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
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

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Home.this);
                        if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    else
                    {
                        Toast.makeText(Home.this, "You should assign permission", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
            default:
                break;
        }
    }

    private void builLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
                Common.CURRENT_LOCATION= new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());

                getdistance(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),Common.currentHotel);
            }
        };
    }
    private void loadSuggest() {
        menu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Hotels item = postSnapshot.getValue(Hotels.class);
                    suggestList.add(item.getLocation());//add name of product to suggest list
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchadapter= new  FirebaseRecyclerAdapter<Hotels, MenuViewHolder>(Hotels.class,
                R.layout.hotel,
                MenuViewHolder.class,
                menu.child(HotelId).orderByChild("location").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Hotels model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                viewHolder.txtLocation.setText(model.getLocation());
                viewHolder.txtDistance.setText(Common.currentlocation);
                viewHolder.txtPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Hotels clickItem = model;
                currentHotel=model;
                HotelId=adapter.getRef(position).getKey();

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent hoteldetail =new Intent(Home.this,HotelDetails.class);
                        currentHotel=model;
                        hoteldetail.putExtra("hotelId",adapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(hoteldetail);

                    }
                });
                viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent booksite=new Intent(Home.this,BookingSite.class);
                        booksite.putExtra("hotelId",adapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(booksite);
                    }
                });
                //Add favourites
                if (localDB.isFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change state of fav
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favourites favourites = new Favourites();
                        favourites.setHotelId(adapter.getRef(position).getKey());
                        favourites.setHotelName(model.getName());
                        favourites.setHotelImage(model.getImage());
                        favourites.setHotelLocation(model.getLocation());
                        favourites.setHotelPrice(model.getPrice());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        if (!localDB.isFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setAdapter(searchadapter);

    }

    private void getdistance(final LatLng latLng, Hotels currentHotel) {
        if (currentHotel.getLocationdetail() !=null && !currentHotel.getLocationdetail().isEmpty())
        {
            String address= currentHotel.getLocationdetail();
            mService.getGeoCode(address).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
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
                        Common.HOTEL_LOCATION=orderLocation;

                        float results[]=new float[1];
                        Location.distanceBetween(latLng.latitude,latLng.longitude,orderLocation.latitude
                                ,orderLocation.longitude,results);
                        Common.currentlocation = results[0]/1000f+" Km";
                        // update to distance to firebase
                        Map<String,Object> updatelatlng = new HashMap<>();
                        updatelatlng.put("lat",Double.parseDouble(lat));
                        updatelatlng.put("lng",Double.parseDouble(lng));
                        menu.child(HotelId).updateChildren(updatelatlng);


                        //update algolia
                        Client client = new Client(Common.APPLICATION_ID, Common.ADMIN_API_KEY);
                        final Index index = client.getIndex("hotel_LOCATION");
                        JSONObject j = new JSONObject();
                        j.put("_geoloc", new JSONObject().put("lat", Double.parseDouble(lat)).put("lng",Double.parseDouble(lng)));
                        index.partialUpdateObjectAsync(j,HotelId,null);



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

    private void sorthotels() {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(this);
        alertdialog.setTitle("Sort Hotels");
        alertdialog.setIcon(R.drawable.ic_sort_black_24dp);
        LayoutInflater inflater=this.getLayoutInflater();
        final View sort= inflater.inflate(R.layout.sortby,null);

       // Init view
        Rname=sort.findViewById(R.id.sort_name);
        Rlocation=sort.findViewById(R.id.sort_location);
        Rprice=sort.findViewById(R.id.sort_price);

        spinnername=sort.findViewById(R.id.spinner1);
        spinnername.setItems("A-Z","Z-A");
        spinnerloction=sort.findViewById(R.id.spinner2);
        spinnerloction.setItems("A-Z","Z-A");
        spinnerprice=sort.findViewById(R.id.spinner3);
        spinnerprice.setItems("Ascending","Descending");

        alertdialog.setView(sort);

        selectItem(sort);
        alertdialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertdialog.show();
    }

    private void priceDescending() {
        com.google.firebase.database.Query query = menu.orderByChild("price");
        priceadapter= new  FirebaseRecyclerAdapter<Hotels, MenuViewHolder>(Hotels.class,
                R.layout.hotel,
                MenuViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Hotels model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                viewHolder.txtLocation.setText(model.getLocation());
                viewHolder.txtDistance.setText(Common.currentlocation);
                viewHolder.txtPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Hotels clickItem = model;
                currentHotel=model;
                if (Common.currentHotel !=null) {
                    getdistance(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Common.currentHotel);
                }
                HotelId=adapter.getRef(position).getKey();
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent hoteldetail =new Intent(Home.this,HotelDetails.class);
                        hoteldetail.putExtra("hotelId",priceadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(hoteldetail);

                    }
                });
                viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent booksite=new Intent(Home.this,BookingSite.class);
                        booksite.putExtra("hotelId",priceadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(booksite);
                    }
                });
                //Add favourites
                if (localDB.isFavourites(priceadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change state of fav
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favourites favourites = new Favourites();
                        favourites.setHotelId(adapter.getRef(position).getKey());
                        favourites.setHotelName(model.getName());
                        favourites.setHotelImage(model.getImage());
                        favourites.setHotelLocation(model.getLocation());
                        favourites.setHotelPrice(model.getPrice());
                        favourites.setUserPhone(Common.currentUser.getPhone());

                        if (!localDB.isFavourites(priceadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(priceadapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setAdapter(priceadapter);
    }

    private void priceAscending() {
        com.google.firebase.database.Query query = menu.orderByChild("price");
        priceadapter= new  FirebaseRecyclerAdapter<Hotels, MenuViewHolder>(Hotels.class,
                R.layout.hotel,
                MenuViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Hotels model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                viewHolder.txtLocation.setText(model.getLocation());
                viewHolder.txtDistance.setText(Common.currentlocation);
                viewHolder.txtPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Hotels clickItem = model;
                currentHotel=model;
                if (Common.currentHotel !=null) {
                    getdistance(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Common.currentHotel);
                }
                HotelId=adapter.getRef(position).getKey();
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent hoteldetail =new Intent(Home.this,HotelDetails.class);
                        hoteldetail.putExtra("hotelId",priceadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(hoteldetail);

                    }
                });
                viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent booksite=new Intent(Home.this,BookingSite.class);
                        booksite.putExtra("hotelId",priceadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(booksite);
                    }
                });
                //Add favourites
                if (localDB.isFavourites(priceadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change state of fav
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favourites favourites = new Favourites();
                        favourites.setHotelId(adapter.getRef(position).getKey());
                        favourites.setHotelName(model.getName());
                        favourites.setHotelImage(model.getImage());
                        favourites.setHotelLocation(model.getLocation());
                        favourites.setHotelPrice(model.getPrice());
                        favourites.setUserPhone(Common.currentUser.getPhone());

                        if (!localDB.isFavourites(priceadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(priceadapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setAdapter(priceadapter);

    }

    private void sortlocationfromZ_A() {
        com.google.firebase.database.Query query = menu.orderByChild("location");
        locationadapter= new  FirebaseRecyclerAdapter<Hotels, MenuViewHolder>(Hotels.class,
                R.layout.hotel,
                MenuViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Hotels model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                viewHolder.txtLocation.setText(model.getLocation());
                viewHolder.txtDistance.setText(Common.currentlocation);
                viewHolder.txtPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Hotels clickItem = model;
                currentHotel=model;
                if (Common.currentHotel !=null) {
                    getdistance(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Common.currentHotel);
                }
                HotelId=adapter.getRef(position).getKey();
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent hoteldetail =new Intent(Home.this,HotelDetails.class);
                        hoteldetail.putExtra("hotelId",locationadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(hoteldetail);

                    }
                });
                viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent booksite=new Intent(Home.this,BookingSite.class);
                        booksite.putExtra("hotelId",locationadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(booksite);
                    }
                });
                //Add favourites
                if (localDB.isFavourites(locationadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change state of fav
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favourites favourites = new Favourites();
                        favourites.setHotelId(adapter.getRef(position).getKey());
                        favourites.setHotelName(model.getName());
                        favourites.setHotelImage(model.getImage());
                        favourites.setHotelLocation(model.getLocation());
                        favourites.setHotelPrice(model.getPrice());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        if (!localDB.isFavourites(locationadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(locationadapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setAdapter(locationadapter);
    }

    private void sortlocationfromA_Z() {
        com.google.firebase.database.Query query = menu.orderByChild("location");
        locationadapter= new  FirebaseRecyclerAdapter<Hotels, MenuViewHolder>(Hotels.class,
                R.layout.hotel,
                MenuViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Hotels model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                viewHolder.txtLocation.setText(model.getLocation());
                viewHolder.txtDistance.setText(Common.currentlocation);
                viewHolder.txtPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Hotels clickItem = model;
                currentHotel=model;
                if (Common.currentHotel !=null) {
                    getdistance(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Common.currentHotel);
                }
                HotelId=adapter.getRef(position).getKey();
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent hoteldetail =new Intent(Home.this,HotelDetails.class);
                        hoteldetail.putExtra("hotelId",locationadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(hoteldetail);

                    }
                });
                viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent booksite=new Intent(Home.this,BookingSite.class);
                        booksite.putExtra("hotelId",locationadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(booksite);
                    }
                });
                //Add favourites
                if (localDB.isFavourites(locationadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change state of fav
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favourites favourites = new Favourites();
                        favourites.setHotelId(adapter.getRef(position).getKey());
                        favourites.setHotelName(model.getName());
                        favourites.setHotelImage(model.getImage());
                        favourites.setHotelLocation(model.getLocation());
                        favourites.setHotelPrice(model.getPrice());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        if (!localDB.isFavourites(locationadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(locationadapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setAdapter(locationadapter);

    }

    private void sortnamefromZ_A() {
        com.google.firebase.database.Query query = menu.orderByChild("name");
        nameadapter= new  FirebaseRecyclerAdapter<Hotels, MenuViewHolder>(Hotels.class,
                R.layout.hotel,
                MenuViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Hotels model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                viewHolder.txtLocation.setText(model.getLocation());
                viewHolder.txtDistance.setText(Common.currentlocation);
                viewHolder.txtPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Hotels clickItem = model;
                currentHotel=model;
                if (Common.currentHotel !=null) {
                    getdistance(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Common.currentHotel);
                }
                HotelId=adapter.getRef(position).getKey();
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent hoteldetail =new Intent(Home.this,HotelDetails.class);
                        hoteldetail.putExtra("hotelId",nameadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(hoteldetail);

                    }
                });
                viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent booksite=new Intent(Home.this,BookingSite.class);
                        booksite.putExtra("hotelId",nameadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(booksite);
                    }
                });
                //Add favourites
                if (localDB.isFavourites(nameadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change state of fav
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favourites favourites = new Favourites();
                        favourites.setHotelId(adapter.getRef(position).getKey());
                        favourites.setHotelName(model.getName());
                        favourites.setHotelImage(model.getImage());
                        favourites.setHotelLocation(model.getLocation());
                        favourites.setHotelPrice(model.getPrice());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        if (!localDB.isFavourites(nameadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(nameadapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setAdapter(nameadapter);

    }

    private void sortnamefromA_Z() {
        com.google.firebase.database.Query query = menu.orderByChild("name");
        nameadapter= new  FirebaseRecyclerAdapter<Hotels, MenuViewHolder>(Hotels.class,
                R.layout.hotel,
                MenuViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Hotels model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                viewHolder.txtLocation.setText(model.getLocation());
                viewHolder.txtDistance.setText(Common.currentlocation);
                viewHolder.txtPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Hotels clickItem = model;
                currentHotel=model;
                if (Common.currentHotel !=null) {
                    getdistance(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Common.currentHotel);
                }
                HotelId=adapter.getRef(position).getKey();

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent hoteldetail =new Intent(Home.this,HotelDetails.class);
                        hoteldetail.putExtra("hotelId",nameadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(hoteldetail);

                    }
                });
                viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent booksite=new Intent(Home.this,BookingSite.class);
                        booksite.putExtra("hotelId",nameadapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(booksite);
                    }
                });
                //Add favourites
                if (localDB.isFavourites(nameadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change state of fav
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favourites favourites = new Favourites();
                        favourites.setHotelId(adapter.getRef(position).getKey());
                        favourites.setHotelName(model.getName());
                        favourites.setHotelImage(model.getImage());
                        favourites.setHotelLocation(model.getLocation());
                        favourites.setHotelPrice(model.getPrice());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        if (!localDB.isFavourites(nameadapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(nameadapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setAdapter(nameadapter);

    }

    public void selectItem(View sort) {
        switch (sort.getId()) {
            case R.id.sort_name:
                Rlocation.setChecked(false);
                Rprice.setChecked(false);
                spinnername.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                        if (position == 0) {
                            sortnamefromA_Z();
                        } else {
                            sortnamefromZ_A();
                        }
                    }
                });
                break;

            case R.id.sort_location:
                Rname.setChecked(false);
                Rprice.setChecked(false);
                spinnerloction.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                        if (position == 0) {
                            sortlocationfromA_Z();
                        } else {
                            sortlocationfromZ_A();
                        }
                    }
                });

                break;

            case R.id.sort_price:
                Rname.setChecked(false);
                Rlocation.setChecked(false);
                spinnerprice.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                        if (position == 0){
                            priceAscending();
                        }else {
                            priceDescending();
                        }
                    }
                });

                break;

        }
    }

    private void loadMenu() {
        circular_progress_dialog.setVisibility(View.INVISIBLE);
        adapter= new  FirebaseRecyclerAdapter<Hotels, MenuViewHolder>(Hotels.class,
                R.layout.hotel,
                MenuViewHolder.class,
                menu) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Hotels model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                viewHolder.txtLocation.setText(model.getLocation());
                viewHolder.txtDistance.setText(Common.currentlocation);
                viewHolder.txtPrice.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Hotels clickItem = model;
                currentHotel=model;

                HotelId=adapter.getRef(position).getKey();

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new activity
                        Intent hoteldetail =new Intent(Home.this,HotelDetails.class);
                        currentHotel=model;
                        hoteldetail.putExtra("hotelId",adapter.getRef(position).getKey()); //send hotel Id to new activity
                        startActivity(hoteldetail);

                    }
                });
               viewHolder.booknow.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent booksite=new Intent(Home.this,BookingSite.class);
                       booksite.putExtra("hotelId",adapter.getRef(position).getKey()); //send hotel Id to new activity
                       startActivity(booksite);
                   }
               });
                //Add favourites
                if (localDB.isFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click to change state of fav
                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favourites favourites = new Favourites();
                        favourites.setHotelId(adapter.getRef(position).getKey());
                        favourites.setHotelName(model.getName());
                        favourites.setHotelImage(model.getImage());
                        favourites.setHotelLocation(model.getLocation());
                        favourites.setHotelPrice(model.getPrice());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        if (!localDB.isFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(Home.this, " "+model.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };

        recycler_menu.setAdapter(adapter);


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
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_find) {
            startActivity(new Intent(this,SearchActicity.class));

        }else if (id == R.id.nav_favourite) {
            startActivity(new Intent(Home.this,FavouritesActicity.class));

        }else if (id == R.id.nav_nearby) {
            Intent nearby=new Intent(Home.this,NearBy.class);
            startActivity(nearby);
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_log_out) {
            //Logout
            Intent signIn=new Intent(Home.this,SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        } else if (id == R.id.nav_help) {
            Intent contact=new Intent(Home.this,Contact.class);
            startActivity(contact);

        }else if (id == R.id.nav_about) {

        }
        else if (id == R.id.imageView) {
            Intent profile=new Intent(Home.this,Profile.class);
            startActivity(profile);

    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStop() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }


}
